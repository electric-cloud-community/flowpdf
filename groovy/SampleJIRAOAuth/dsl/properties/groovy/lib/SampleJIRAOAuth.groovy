import com.cloudbees.flowpdf.*
import groovy.json.JsonOutput

/**
* SampleJIRAOAuth
*/
class SampleJIRAOAuth extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                oauth              : [
                        request_method        : 'POST',
                        oauth_signature_method: 'RSA-SHA1',
                        oauth_version         : '1.0',
                        request_token_path    : 'plugins/servlet/oauth/request-token',
                        authorize_token_path  : 'plugins/servlet/oauth/authorize',
                        access_token_path     : 'plugins/servlet/oauth/access-token',
                ],
                defaultConfigValues: [:]
        ]
    }

/**
 * getIssues - GetIssues/GetIssues
 * Add your code into this method and it will be called when the step runs
 * @param config (required: true)
 * @param jiraIdentifier (required: true)
 */
    def getIssues(StepParameters runtimeParameters, StepResult sr) {
        log.infoDiag("Info diag")
        log.warnDiag("Warn diag")
        log.errorDiag("Error diag")

        /* Log is automatically available from the parent class */
        log.info(
                "getIssues was invoked with StepParameters",
                /* runtimeParameters contains both configuration and procedure parameters */
                runtimeParameters.toString()
        )

        String issueIdentifier = runtimeParameters.getRequiredParameter('jiraIdentifier').getValue()
        String jql
        // issue ID(s)
        if (issueIdentifier =~ /^[A-Za-z]+-[0-9]+$/
                || issueIdentifier =~ /^((?:[A-Za-z]+-[0-9]+), ?)+(?:[A-Za-z]+-[0-9]+)$/) {
            def identifiers = issueIdentifier.split(/, ?/)
            jql = "ID IN (${identifiers.collect({ it -> return "'${it}'" }).join(',')})"
        } else { //JQL
            jql = issueIdentifier
        }

        log.info("JQL is: ${jql}")

        def requestParams = [
                method: 'GET',
                path  : '/rest/api/2/search',
                query : ['jql': jql, fields: 'summary,comment']
        ]

        def issues = retrieveChunkedResults(requestParams)

        log.debug("Search result", issues.toString())

        String issueKeys = issues.collect({ it -> return it.key }).join(',')

        // Save JSON
        def jsonIssuesStr = JsonOutput.toJson(issues)
        sr.setOutcomeProperty('/myCall/issues', jsonIssuesStr)
        sr.setOutcomeProperty('/myCall/issuesCount', issues.size() as String)
        sr.setOutcomeProperty('/myCall/issueKeys', issueKeys)

        // Setting job step summary
        sr.setJobStepSummary("Retrieved ${issues.size()} issue(s).")
        sr.setOutputParameter('issueIds', issueKeys)

        sr.apply()
        log.infoDiag("step GetIssues has been finished")
    }

// === step ends ===

    def retrieveChunkedResults(Map<String, Object> requestParameters, Map options = [:]) {

        // Defaults
        options['startAt'] = options['startAt'] ?: 0
        options['maxResults'] = options['maxResults'] ?: 50

        // Initializing the client and the request
        def restClient = getContext().newRESTClient()
        def request = restClient.newRequest(requestParameters)

        // Applying options to the request
        if (options['startAt'] != 0) {
            request.setQueryParameter('startAt', options['startAt'] as String)
        }
        request.setQueryParameter('maxResults', options['maxResults'] as String)

        log.debug("Retrieving starting from ${options['startAt']}.")

        // Retrieving issues
        def retrievedIssues = []
        def currentRequestResult = restClient.doRequest(request)
        if (currentRequestResult['total'] > 0) {
            for (def it : currentRequestResult['issues']) retrievedIssues.push(it)

            // Recursively retrieving next issues
            int lastIssueNum = currentRequestResult['startAt'] + currentRequestResult['maxResults']
            if (currentRequestResult['total'] > lastIssueNum) {
                def nextIssues = retrieveChunkedResults(requestParameters, [startAt: lastIssueNum + 1])
                for (def it : nextIssues) retrievedIssues.push(it)
            }
        }

        // Return result
        return retrievedIssues
    }
}