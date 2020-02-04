import com.cloudbees.flowpdf.*

/**
* SampleGithubREST
*/
class SampleGithubREST extends FlowPlugin {

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
     * Auto-generated method for the procedure Get User/Get User
     * Add your code into this method and it will be called when step runs* Parameter: config* Parameter: username
     */
    def getUser(StepParameters p, StepResult sr) {
        SampleGithubRESTRESTClient rest = getSampleGithubRESTRESTClient()
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
    SampleGithubRESTRESTClient getSampleGithubRESTRESTClient() {
        Context context = getContext()
        SampleGithubRESTRESTClient rest = SampleGithubRESTRESTClient.fromConfig(context.getConfigValues(), this)
        return rest
    }
/**
     * Auto-generated method for the procedure Create Release/Create Release
     * Add your code into this method and it will be called when step runs* Parameter: config* Parameter: repositoryOwner* Parameter: repositoryName* Parameter: tagName* Parameter: name* Parameter: assetName* Parameter: assetPath
     */
    def createRelease(StepParameters p, StepResult sr) {
        SampleGithubRESTRESTClient rest = getSampleGithubRESTRESTClient()
        Map restParams = [:]
        Map requestParams = p.asMap

        restParams.put('repositoryOwner', requestParams.get('repositoryOwner'))
        restParams.put('repositoryName', requestParams.get('repositoryName'))
        restParams.put('tag_name', requestParams.get('tagName'))
        restParams.put('name', requestParams.get('name'))

        def release
        try {
            restParams.tag = requestParams.tagName
            release = rest.getReleaseByTagName(restParams)
        }
        catch (Throwable e) {
            log.info e.message
            log.info("Failed to get release for tag $requestParams.tagName")
        }

        if (!release) {
            Object response = rest.createRelease(restParams)
            release = response
            log.info "Created release: $release"
        }
        //TODO asset

        String htmlUrl = release.html_url
        sr.setReportUrl("Release URL", htmlUrl)
        sr.apply()

        def existingAsset = release.assets?.grep {
            it.name == requestParams.assetName
        }
        if (existingAsset) {
            context.bailOut("The asset named ${requestParams.assetName} already exists in the release")
        }

        String assetPath = requestParams.assetPath
        File asset = new File(assetPath)
        if (!asset.exists()) {
            context.bailOut("The file $asset.absolutePath does not exist")
        }
        restParams.releaseId = release.id
        restParams.assetPath = asset.absolutePath
        restParams.assetName = requestParams.assetName
        def uploadedAsset = rest.uploadReleaseAsset(restParams)
        log.info "Asset: $uploadedAsset"
    }
// === step ends ===

}