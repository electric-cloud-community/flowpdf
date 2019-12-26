import com.cloudbees.flowpdf.Context
import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.StepParameters
import com.cloudbees.flowpdf.StepResult
import com.cloudbees.flowpdf.client.REST
import com.cloudbees.flowpdf.components.ComponentManager
import com.cloudbees.flowpdf.components.reporting.Reporting

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * SampleJIRAReporting
 */
class SampleJIRAReporting extends FlowPlugin {

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
 * Procedure parameters:
 * @param config
 * @param jiraProjectName
 * @param previewMode
 * @param transformScript
 * @param debug
 * @param releaseName
 * @param releaseProjectName

 */
    def collectReportingData(StepParameters paramsStep, StepResult sr) {
        def params = paramsStep.getAsMap()

        if (params['debug']) {
            log.setLogLevel(log.LOG_DEBUG)
        }

        Reporting reporting = (Reporting) ComponentManager.loadComponent(ReportingSampleJIRAReporting.class, [
                reportObjectTypes: ['feature'],
                metadataUniqueKey: params['jiraProjectName'],
                payloadKeys      : ['key', 'modifiedOn'],
        ] as Map<String, Object>, this)

        reporting.collectReportingData()
    }

    def get(String path, Map<String, String> queryParams) {
        Context context = getContext()
        REST rest = context.newRESTClient()

        return rest.request('GET', path, queryParams)
    }

    def getIssues(String projectName, Map<String, String> opts) {
        Map<String, String> runtimeParameters = getContext().getRuntimeParameters().getAsMap()

        String storyJql = "project=$projectName AND issuetype=Story"
        int maxResults = opts['limit'] ?: 0
        String lastJql = storyJql + " ORDER BY updatedDate DESC"

        if (opts['after']) {
            // Valid formats include: 'yyyy/MM/dd HH:mm', 'yyyy-MM-dd HH:mm', 'yyyy/MM/dd', 'yyyy-MM-dd'
            SimpleDateFormat jqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")
            jqlDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

            if (opts['after'] =~ /^\d+$/) {
                Date afterDate = new Date(opts['after'])
                opts['after'] = jqlDateFormat.format(afterDate)
            } else if (opts['after'] =~ /[0-9]Z$/) {
                SimpleDateFormat devopsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                Date parsedDate = devopsDateFormat.parse(opts['after'])
                opts['after'] = jqlDateFormat.format(parsedDate)
            }

            storyJql += " AND updatedDate >= \"${opts['after']}\" ORDER BY updatedDate DESC"
        }

        log.info("Running JQL: $storyJql", "with limit: $maxResults")

        if (opts['getLastIssue']) {
            storyJql = lastJql
            maxResults = 1
        }

        def requestParams = [jql: storyJql]
        if (maxResults > 0) requestParams['maxResults'] = maxResults

        def issues = get('/rest/api/2/search', requestParams)

        log.debug("Response", issues)

        return issues['issues']
    }

    String jiraDateStringToISODatetime(String rawDate) {
        if (rawDate == null || !rawDate) return ''

        SimpleDateFormat jiraDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ")
        Date parsedDate = jiraDateFormat.parse(rawDate)

        SimpleDateFormat devopsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        devopsDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))

        String formatted = devopsDateFormat.format(parsedDate)
    }

// === step ends ===

}