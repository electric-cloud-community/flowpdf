import com.cloudbees.flowpdf.*

/**
* Confluence
*/
class Confluence extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName     : '@PLUGIN_KEY@',
                pluginVersion  : '@PLUGIN_VERSION@',
                configFields   : ['config'],
                configLocations: ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
    }

/**
     * Auto-generated method for the procedure Sample REST Procedure/Sample REST Procedure
     * Add your code into this method and it will be called when step runs* Parameter: config* Parameter: username
     */
    def sampleRESTProcedure(StepParameters p, StepResult sr) {
        ECConfluenceRESTClient rest = genECConfluenceRESTClient()
        Map restParams = [:]
        Map requestParams = p.asMap
        restParams.put('username', requestParams.get('username'))

        Object response = rest.getUser(restParams)
        log.info "Got response from server: $response"
        //TODO step result output parameters
        sr.apply()
    }
/**
     * This method returns REST Client object
     */
    ECConfluenceRESTClient genECConfluenceRESTClient() {
        Context context = getContext()
        ECConfluenceRESTClient rest = ECConfluenceRESTClient.fromConfig(context.getConfigValues(), this)
        return rest
    }

/**
     * Auto-generated method for the procedure Create Page/Create Page
     * Add your code into this method and it will be called when step runs* Parameter: config* Parameter: SpaceKey* Parameter: Title* Parameter: Ancestors* Parameter: Content
     */
    def createPage(StepParameters p, StepResult sr) {
        ECConfluenceRESTClient rest = genECConfluenceRESTClient()
        Map restParams = [:]
        Map requestParams = p.asMap
        restParams.put('SpaceKey', requestParams.get('SpaceKey'))
        restParams.put('Title', requestParams.get('Title'))
        restParams.put('Ancestors', requestParams.get('Ancestors'))
        restParams.put('Content', requestParams.get('Content'))

        Object response = rest.createPage(restParams)
        log.info "Got response from server: $response"
        //TODO step result output parameters

        sr.apply()
    }
// === step ends ===

}
