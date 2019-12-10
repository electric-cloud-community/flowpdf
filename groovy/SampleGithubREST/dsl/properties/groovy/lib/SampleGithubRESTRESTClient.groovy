// DO NOT EDIT THIS BLOCK === rest client imports starts ===
import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.client.HTTPRequest
import com.cloudbees.flowpdf.client.REST
import com.cloudbees.flowpdf.client.RESTConfig
import groovy.json.JsonOutput
import groovy.json.JsonSlurper
import groovy.transform.InheritConstructors
import groovyx.net.http.ContentType
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import org.apache.http.HttpException
import org.apache.http.HttpResponse
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import org.apache.http.auth.AuthScope
import org.apache.http.auth.UsernamePasswordCredentials
// DO NOT EDIT THIS BLOCK === rest client imports ends, checksum: bd43b259ee4d532c8bd07cf2f708baba ===
// Place for the custom user imports, e.g. import groovy.xml.*
// DO NOT EDIT THIS BLOCK === rest client starts ===
@InheritConstructors
class InvalidRestClientException extends Exception {

}

class ProxyConfig {
    String url
    String userName
    String password
}

class SampleGithubRESTRESTClient {

    private static String BEARER_PREFIX = 'Bearer'
    private static String USER_AGENT = 'My Github Client'
    private static String CONTENT_TYPE = 'application/json'
    private static OAUTH1_SIGNATURE_METHOD = 'RSA-SHA1'

    String endpoint
    String method
    Map<String, String> methodParameters

    private Log log
    private REST rest
    private ProxyConfig proxyConfig

    SampleGithubRESTRESTClient(String endpoint, RESTConfig restConfig, FlowPlugin plugin) {
        this.endpoint = endpoint
        this.log = plugin.log
        this.rest = new REST(restConfig)
    }

    /**
     * Will create a SampleGithubRESTRESTClient object from the plugin Config object.
     * Convenient as it can use pre-defined configuration fields.
     */
    static SampleGithubRESTRESTClient fromConfig(Config config, FlowPlugin plugin) {
        Map params = [:]
        String endpoint = config.getRequiredParameter('endpoint').value.toString()
        Log log = plugin.log
        Credential credential
        RESTConfig restConfig = new RESTConfig()
        restConfig.endpoint = endpoint
        if ((credential = config.getCredential('bearer_credential')) && credential.secretValue) {
            restConfig.authScheme = 'bearer'
            restConfig.bearerToken = credential.secretValue
            restConfig.bearerPrefix = BEARER_PREFIX
            log.debug "Using bearer credential in REST Client"
        }
        else if ((credential = config.getCredential('basic_credential'))) {
            restConfig.authScheme = 'basic'
            restConfig.basicCredentialUsername = credential.userName
            restConfig.basicCredentialPassword = credential.secretValue
        }
        else if (config.isParameterHasValue('authScheme') && config.getParameter('authScheme').value == 'anonymous') {
            log.debug "Using anonymous auth scheme"
        }
        if (config.isParameterHasValue('httpProxyUrl')) {
            String proxyUrl = config.getParameter('httpProxyUrl').value
            restConfig.httpProxyUrl = proxyUrl
            log.debug "Using proxy $proxyUrl"
            if ((credential = config.getCredential('proxy_credential'))) {
                restConfig.proxyCredentialUsername = credential.userName
                restConfig.proxyCredentialPassword = credential.secretValue
                log.debug "Using proxy authorization"
            }
        }
        return new SampleGithubRESTRESTClient(endpoint, restConfig, plugin)
    }

    // Handles templates like , taking values from the params
    private static String renderOneLineTemplate(String uri, Map params) {
        for(String key in params.keySet()) {
            Object value = params.get(key)
            if (value) {
                uri = uri.replaceAll(/\{\{$key\}\}/, value as String)
            }
            else {
                throw new InvalidRestClientException("A field $key is empty in params but required in the template")
            }
        }
        return uri
    }

    /**
     * This is the main request method
     * methodString - GET|POST|PUT - request method
     * url - uri path (without the endpoint)
     * query - uri.query
     * payload - payload for POST/PUT requests
     * h - headers for the request
     */
    Object makeRequest(String methodString, String url, Map query, def payload, Map h) {
        Method method = Method.valueOf(methodString)

        HTTPRequest request = new HTTPRequest(
            method: method,
            path: url,
            query: query,
            headers: h
        )

        if (payload) {
            if (payload instanceof byte[]) {
                request.setContentBytes(payload)
            }
            else {
                request.setContentString(encodePayload(payload))
            }
        }

        if (method == Method.POST || method == Method.PUT) {
            if (!request.requestContentType) {
                request.requestContentType = ContentType.JSON
            }
        }
        request = rest.prepareRequest(request)
        request = augmentRequest(request)

        HTTPBuilder builder = rest.httpBuilder
        boolean success = true
        Tuple2 responseTuple = builder.request(method) {
            if (request.path) {
                uri.path = request.path
                log.trace("Set request path: $uri.path")
            }
            if (request.query) {
                uri.query = request.query
            }
            request.headers.each { headerName, headerValue ->
                headers.put(headerName, headerValue)
                log.trace "Added request hheader $headerName -> $headerValue"
            }
            if (request.contentString) {
                send request.requestContentType, request.contentString
                log.trace "Request body is $body"
            }

            if (request.contentBytes) {
                send request.requestContentType, request.contentBytes
            }

            response.success = { HttpResponse resp, decoded ->
                log.trace "Got response: $resp"
                log.trace "Got content: $decoded"

                return new Tuple2(resp, decoded)
            }
            response.failure = { HttpResponse resp, body ->
                log.trace "Request failed: $resp"
                log.trace "$body"
                success = false

                return new Tuple2(resp, body)
            }
        }
        HttpResponse response = responseTuple.first
        Object body = responseTuple.second

        Object processedResponse = processResponse(response, body)
        if (processedResponse) {
            return processedResponse
        }
        if (!success) {
            throw new HttpException("Request for uri $url failed: ${response.statusLine.statusCode}, ${body}")
        }
        Object parsed = parseResponse(response, body)
        return parsed
    }

    private static payloadFromTemplate(String template, Map params) {
        Object object = new JsonSlurper().parseText(template)
        object = fillFields(object, params)
        return object
    }

    private static def fillFields(def o, Map params) {
        def retval
        if (o instanceof Map) {
            retval = [:]
            for(String key in o.keySet()) {
                key = renderOneLineTemplate(key, params)
                def value = o.get(key)
                if (value instanceof String) {
                    value = renderOneLineTemplate(value, params)
                }
                else {
                    value = fillFields(value, params)
                }
                retval.put(key, value)
            }
        }
        else if (o instanceof List) {
            retval = []
            for (def i in o) {
                i = fillFields(i, params)
                retval.add(i)
            }
        }
        else if (o instanceof String) {
            o = renderOneLineTemplate(o, params)
            retval = o
        }
        else {
            throw new NotImplementedException()
        }
        return retval
    }

    /** Generated code for the endpoint /repos/{{repositoryOwner}}/{{repositoryName}}/releases/{{releaseId}}/assets
    * Do not change this code
    * name: in query
    * repositoryOwner: in path
    * releaseId: in path
    * repositoryName: in path
    */
    def uploadReleaseAsset(Map<String, String> params) {
        this.method = 'uploadReleaseAsset'
        this.methodParameters = params

        String uri = '/repos/{{repositoryOwner}}/{{repositoryName}}/releases/{{releaseId}}/assets'
        log.debug("URI template $uri")
        uri = renderOneLineTemplate(uri, params)

        Map query = [:]

        query.put('name', params.get('name'))

        log.debug "Query: ${query}"

        Object payload

        String jsonTemplate = ''
        if (jsonTemplate) {
            payload = payloadFromTemplate(jsonTemplate, params)
            log.debug("Payload from template: $payload")
        }
        //TODO clean empty fields
        Map headers = [:]
        return makeRequest('POST', uri, query, payload, headers)
    }

    /** Generated code for the endpoint /repos/{{repositoryOwner}}/{{repositoryName}}/releases/tags/{{tag}}
    * Do not change this code
    * name: in query
    * repositoryOwner: in path
    * tag: in path
    * repositoryName: in path
    */
    def getReleaseByTagName(Map<String, String> params) {
        this.method = 'getReleaseByTagName'
        this.methodParameters = params

        String uri = '/repos/{{repositoryOwner}}/{{repositoryName}}/releases/tags/{{tag}}'
        log.debug("URI template $uri")
        uri = renderOneLineTemplate(uri, params)

        Map query = [:]

        query.put('name', params.get('name'))

        log.debug "Query: ${query}"

        Object payload

        String jsonTemplate = ''
        if (jsonTemplate) {
            payload = payloadFromTemplate(jsonTemplate, params)
            log.debug("Payload from template: $payload")
        }
        //TODO clean empty fields
        Map headers = [:]
        return makeRequest('GET', uri, query, payload, headers)
    }

    /** Generated code for the endpoint /users/{{username}}
    * Do not change this code
    * username: in path
    */
    def getUser(Map<String, String> params) {
        this.method = 'getUser'
        this.methodParameters = params

        String uri = '/users/{{username}}'
        log.debug("URI template $uri")
        uri = renderOneLineTemplate(uri, params)

        Map query = [:]

        log.debug "Query: ${query}"

        Object payload

        String jsonTemplate = ''
        if (jsonTemplate) {
            payload = payloadFromTemplate(jsonTemplate, params)
            log.debug("Payload from template: $payload")
        }
        //TODO clean empty fields
        Map headers = [:]
        return makeRequest('GET', uri, query, payload, headers)
    }

    /** Generated code for the endpoint /repos/{{repositoryOwner}}/{{repositoryName}}/releases
    * Do not change this code
    * repositoryOwner: in path
    * repositoryName: in path
    * tag_name: in body
    * name: in body
    */
    def createRelease(Map<String, String> params) {
        this.method = 'createRelease'
        this.methodParameters = params

        String uri = '/repos/{{repositoryOwner}}/{{repositoryName}}/releases'
        log.debug("URI template $uri")
        uri = renderOneLineTemplate(uri, params)

        Map query = [:]

        log.debug "Query: ${query}"

        Object payload
        payload = [:]

        payload.put('tag_name', params.get('tag_name'))

        payload.put('name', params.get('name'))

        String jsonTemplate = ''
        if (jsonTemplate) {
            payload = payloadFromTemplate(jsonTemplate, params)
            log.debug("Payload from template: $payload")
        }
        //TODO clean empty fields
        Map headers = [:]
        return makeRequest('POST', uri, query, payload, headers)
    }
// DO NOT EDIT THIS BLOCK === rest client ends, checksum: eed3a2495450ca8faa63720e929f2e51 ===
    /**
     * Use this method for any request pre-processing: adding custom headers, binary files, etc.
     */
    HTTPRequest augmentRequest(HTTPRequest request) {
        if (method == 'uploadReleaseAsset') {
            request = _uploadReleaseAsset(request)
        }
        return request
    }

    private HTTPRequest _uploadReleaseAsset(HTTPRequest request) {
        String assetPath = methodParameters.assetPath
        assert assetPath
        File asset = new File(assetPath)
        request.binaryContent = asset.bytes
        rest.httpBuilder.setUri("https://uploads.github.com")
        if (methodParameters.assetName) {
            request.setQueryParameter('name', methodParameters.assetName)
        }
        return request
    }

    /**
     * Use this method to provide a custom encoding for you payload (XML, yaml etc)
     */
    Object encodePayload(def payload) {
        return JsonOutput.toJson(payload)
    }

    /**
     * Use this method to parse/alter response from the server
     */
    def parseResponse(HttpResponse response, Object body) {
        //Relying on http builder content type processing
        return body
    }

    /**
     * Use this method to alter default server response processing.
     * The response from this method will be returned as is, if any.
     * To disable response, just return null.
     */
    def processResponse(HttpResponse response, Object body) {
        return null
    }

}