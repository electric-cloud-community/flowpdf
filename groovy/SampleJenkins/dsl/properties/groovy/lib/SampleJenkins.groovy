import com.cloudbees.flowpdf.Context
import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.StepParameters
import com.cloudbees.flowpdf.StepResult
import com.cloudbees.flowpdf.client.HTTPRequest
import com.cloudbees.flowpdf.client.REST

/**
 * SampleJenkins
 */
class SampleJenkins extends FlowPlugin {

    String jenkinsBreadcrumbField
    String jenkinsBreadcrumbValue

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [ authScheme: 'basic' ]
        ]
    }

/**
 * getJenkinsJob - GetJenkinsJob/GetJenkinsJob
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param jobName (required: true)

 */
    def getJenkinsJob(StepParameters p, StepResult sr) {

        /* Log is automatically available from the parent class */
        log.info(
                "getJenkinsJob was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                p.toString()
        )

        String jobName = p.getRequiredParameter('jobName').getValue()

        Context context = getContext()
        REST rest = context.newRESTClient()

        HTTPRequest request = rest.newRequest([method: 'GET', path: sprintf("/job/%s/api/json", jobName)])
        addBreadcrumbsToRequest(request)

        def jobResponse = rest.doRequest(request)
        log.debug("JSON", jobResponse.dump())

        if (jobResponse['lastBuild'] != null) {
            String lastBuildNumber = jobResponse['lastBuild']['number']
            String lastBuildUrl = jobResponse['lastBuild']['url']

            sr.setOutcomeProperty('/myCall/lastBuildNumber', lastBuildNumber)
            sr.setJobStepSummary("Saved properties for build number ${lastBuildNumber}")

            // Showing link to a build
            sr.setReportUrl("Last Build", lastBuildUrl, "Jenkins Build ${jobName}#${lastBuildNumber}\"")
        }

        sr.apply()
        log.info("step GetJenkinsJob has been finished")
    }

// === step ends ===

    HTTPRequest addBreadcrumbsToRequest(HTTPRequest httpRequest) {
        // Checking cached values
        if (!jenkinsBreadcrumbField || !jenkinsBreadcrumbValue) {
            REST client = getContext().newRESTClient()
            HTTPRequest breadCrumbsRequest = client.newRequest([method: 'GET', path: '/crumbIssuer/api/json'])

            def breadCrumbResponse = client.doRequest(breadCrumbsRequest)

            jenkinsBreadcrumbField = breadCrumbResponse['crumbRequestField'] as String
            jenkinsBreadcrumbValue = breadCrumbResponse['crumb'] as String
        }

        httpRequest.headers.put(jenkinsBreadcrumbField, jenkinsBreadcrumbValue)
        return httpRequest
    }
}
