import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.client.*

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

        if (jobResponse['lastBuild'] == null) {
            context.bailOut("No build was found for job '${jobName}'")
        }

        String lastBuildNumber = jobResponse['lastBuild']['number']

        sr.setOutcomeProperty('/myCall/lastBuildNumber', lastBuildNumber)
        sr.setJobStepSummary("Saved properties for build number ${lastBuildNumber}")
        sr.setPipelineSummary("Last build number is", lastBuildNumber)
        sr.setJobSummary("Build properties saved.")

        // Showing link to a build
        String lastBuildUrl = jobResponse['lastBuild']['url']
        sr.setReportUrl("Last Build", lastBuildUrl, "Jenkins Build ${jobName}#${lastBuildNumber}")

        sr.setOutputParameter('lastBuildNum', lastBuildNumber)

        sr.apply()
        log.info("step GetJenkinsJob has been finished")
    }

// === step ends ===

    HTTPRequest addBreadcrumbsToRequest(HTTPRequest httpRequest) {
        // Checking cached values
        if (!this.jenkinsBreadcrumbField || !this.jenkinsBreadcrumbValue) {

            // Requesting new crumb
            REST client = getContext().newRESTClient()
            HTTPRequest breadCrumbsRequest = client.newRequest([method: 'GET', path: '/crumbIssuer/api/json'])
            def breadCrumbResponse = client.doRequest(breadCrumbsRequest)

            // Saving
            this.jenkinsBreadcrumbField = breadCrumbResponse['crumbRequestField'] as String
            this.jenkinsBreadcrumbValue = breadCrumbResponse['crumb'] as String
        }

        httpRequest.headers.put(this.jenkinsBreadcrumbField, this.jenkinsBreadcrumbValue)
        return httpRequest
    }
}
