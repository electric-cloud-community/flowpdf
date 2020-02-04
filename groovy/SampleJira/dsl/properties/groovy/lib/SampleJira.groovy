import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.StepParameters
import com.cloudbees.flowpdf.StepResult
import groovy.json.JsonOutput

/**
 * SampleJira
 */
class SampleJira extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [authScheme: 'basic']
        ]
    }

    /**
     * getIssue - GetIssue/GetIssue
     * Add your code into this method and it will be called when the step runs
     * @param config (required: true)
     * @param jiraKey (required: true)

     */
    def getIssue(StepParameters p, StepResult sr) {
        /* Log is automatically available from the parent class */
        log.info(
                "getIssue was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        String issueIdentifier = p.getRequiredParameter('jiraKey').getValue()

        def requestParams = [
                method: 'GET',
                path  : sprintf('/rest/api/2/issue/%s', issueIdentifier),
                query : [
                        fields: 'summary,comment,status'
                ]
        ]

        // Initializing the client and the request
        def restClient = getContext().newRESTClient()
        def request = restClient.newRequest(requestParams)

        // Executing the request
        def issue = restClient.doRequest(request)
        log.debug("Response: " + issue.dump())

        // Saving issue as a JSON
        def jsonIssuesStr = JsonOutput.toJson(issue)
        log.debug("Issue JSON: " + jsonIssuesStr)

        sr.setOutcomeProperty('/myCall/issue', jsonIssuesStr)
        String issueStatus = issue?.fields?.status?.name ?: 'no status'
        log.info("Issue status is: ${issueStatus}")

        // Setting outputs
        sr.setOutputParameter('issueStatus', issueStatus)
        sr.setPipelineSummary("Issue number", issueIdentifier)
        sr.setJobSummary("Issue status is ${issueStatus}")
        sr.setJobStepSummary("Saved json representation of ${issueIdentifier}")

        sr.apply()
        log.infoDiag("step GetIssue has been finished")
    }
// === step ends ===

}