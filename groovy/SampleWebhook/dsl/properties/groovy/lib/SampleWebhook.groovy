import com.cloudbees.flowpdf.*
import org.gitlab4j.api.GitLabApi
import com.electriccloud.client.groovy.models.Filter
import org.gitlab4j.api.models.ProjectHook

/**
* SampleWebhook
*/
class SampleWebhook extends FlowPlugin {

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
/** This is a special method for checking connection during configuration creation
    */
    def checkConnection(StepParameters p, StepResult sr) {
        // Use this pre-defined method to check connection parameters
        try {
            // Put some checks here
            def config = context.configValues
            log.info(config)
            // Getting parameters:
            // log.info config.asMap.get('config')
            // log.info config.asMap.get('desc')
            // log.info config.asMap.get('endpoint')
            // log.info config.asMap.get('credential')
            
            // assert config.getRequiredCredential("credential").secretValue == "secret"
        }  catch (Throwable e) {
            // Set this property to show the error in the UI
            sr.setOutcomeProperty("/myJob/configError", e.message + System.lineSeparator() + "Please change the code of checkConnection method to incorporate your own connection checking logic")
            sr.apply()
            throw e
        }
    }
// === check connection ends ===
/**
    * setupProcedure - SetupProcedure/SetupProcedure
    * Add your code into this method and it will be called when the step runs
    * @param projectId (required: true)
    * @param ec_trigger (required: true)
    * @param ec_action (required: false)
    * @param webhookSecret_credential (required: false)
    
    */
    def setupProcedure(StepParameters p, StepResult sr) {
        log.info p.asMap.get('projectId')
        log.info p.asMap.get('ec_action')
        log.info p.asMap.get('webhookSecret_credential')
        log.info p.asMap.get('accessToken_credential')

        def triggerId = p.asMap.get('ec_trigger')
        def filters = [new Filter('pluginKey', 'equals', '@PLUGIN_KEY@'),
                       new Filter('triggerType', 'equals', 'webhook') ,
                       new Filter('triggerId', 'equals', triggerId)]

        def triggersResponse = FlowAPI.ec.findObjects(
            objectType: 'trigger',
            filters: filters,
            viewName: 'Details'
        )
        def trigger = triggersResponse.object?.first()?.trigger
        log.info "Trigger: $trigger"
        if(!trigger) {
            throw new RuntimeException("Failed to find trigger $triggerId")
        }
        def url = trigger.webhookUrl
        log.info "Url: $url"
        def secret = p.getRequiredCredential('webhookSecret_credential')?.secretValue
        def publicId = trigger.accessTokenPublicId

        def token = p.getRequiredCredential('accessToken_credential')?.secretValue
        GitLabApi gitLabApi = new GitLabApi("https://gitlab.com", token)
        String projectId = p.asMap.projectId
        def project =  gitLabApi.getProjectApi().getProject(projectId)
        log.info "Found project: $project"
        def hooks = gitLabApi.getProjectApi().getHooks(projectId)
        log.info "Found hooks: $hooks"
        def existing = hooks.find {
            it.url.endsWith(publicId)
        }
        if (existing) {
            gitLabApi.getProjectApi().deleteHook(existing)
            log.info "Deleted existing hook $existing.id"
        }
        def hook = new ProjectHook(pushEvents: true)
        def added = gitLabApi.getProjectApi().addHook(projectId, url as String, hook, false, secret as String)
        log.info "Added hook $added"

        log.info("step SetupProcedure has been finished")
    }

// === step ends ===

}