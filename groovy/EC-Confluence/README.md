## EC-Confluence

This sample demonstrates how one can alter the structure of POST request of
auto-generated REST client.


After a plugin has been generated you must see the following in the main plugin module:

```
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
```

This part should be added to the auto-generated REST client in order to turn the default
request payload into a nested structure:

```
    /**
     * Use this method to provide a custom encoding for you payload (XML, yaml etc)
     */
    Object encodePayload(def payload) {
        println payload
        println methodParameters
        println method
        if (method == 'createPage') {
            payload.type = 'page'
            payload.title = payload.Title
            payload.space = [key: payload.SpaceKey]
            payload.body = [
                storage: [
                    value: payload.Content,
                    representation: 'storage'
                ]
            ]
            payload.ancestors = [[id: payload.Ancestors]]
            println payload
        /*
            {
              "type": "page",
              "title": "",
              "space": {
                "key": ""
              },
              "body": {
                "storage": {
                  "value": "",
                  "representation": "storage"
                }
              },
              "ancestors": [{
                "id": ""
              }]
            }
            */
        }
        return JsonOutput.toJson(payload)
    }
```
