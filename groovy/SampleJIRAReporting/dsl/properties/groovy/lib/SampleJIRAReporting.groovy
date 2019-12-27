import com.cloudbees.flowpdf.Context
import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.StepParameters
import com.cloudbees.flowpdf.StepResult
import com.cloudbees.flowpdf.client.REST
import com.cloudbees.flowpdf.components.ComponentManager
import com.cloudbees.flowpdf.components.reporting.Reporting
import com.cloudbees.flowpdf.exceptions.*

import java.text.SimpleDateFormat

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

        String storyJql = "project=$projectName AND issuetype=Story ORDER BY updatedDate ASC"

        def requestParams = [jql: storyJql]

        if (opts['limit']) {
            requestParams['maxResults'] = opts['limit']
        }

        def issues = get('/rest/api/2/search', requestParams)

        return issues['issues']
    }

    def getLastUpdatedIssue(String projectName) {
        String lastJql = "project=$projectName AND issuetype=Story ORDER BY updatedDate DESC"

        def result = get('/rest/api/2/search', [jql: lastJql, maxResults: '1'])
        if (result['total'] > 0 && result.issues.size()) {
            return result['issues'][0]
        }

        throw new UnexpectedMissingValue("JIRA did not returned last updated issue.")
    }

    def getIssuesAfterDate(String projectName, String lastUpdateDateISO) {
        // Metadata contains data in ISO format. Should convert it to JIRA format
        SimpleDateFormat devopsDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        Date parsedDate = devopsDateFormat.parse(lastUpdateDateISO)

        // Valid formats include: 'yyyy/MM/dd HH:mm', 'yyyy-MM-dd HH:mm', 'yyyy/MM/dd', 'yyyy-MM-dd'
        SimpleDateFormat jqlDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm")

        // The timezone should be adjusted to jira value
        jqlDateFormat.setTimeZone(TimeZone.getTimeZone("UTC"))
        String jiraFormattedDate = jqlDateFormat.format(parsedDate)

        String storyJql = "project='$projectName' AND issuetype=Story AND updatedDate >= \"${jiraFormattedDate}\" ORDER BY updatedDate ASC"

        def result = get('/rest/api/2/search', [jql: storyJql])
        if (result['total'] > 0 && result.issues.size()) {
            return result['issues']
        }

        throw new UnexpectedMissingValue("JIRA did not return issue updated after ${jiraFormattedDate}. Check the timezone.")
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