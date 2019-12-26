import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.components.reporting.Dataset
import com.cloudbees.flowpdf.components.reporting.Metadata
import com.cloudbees.flowpdf.components.reporting.Reporting
import net.sf.json.JSONObject

import java.text.SimpleDateFormat
import java.util.Date
import java.util.TimeZone

/**
 * User implementation of the reporting classes
 */
class ReportingSampleJIRAReporting extends Reporting {

    @Override
    int compareMetadata(Metadata param1, Metadata param2) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
        format.setTimeZone(TimeZone.getTimeZone("UTC"))

        Date date1 = format.parse(param1.getValue()['modifiedOn'])
        Date date2 = format.parse(param2.getValue()['modifiedOn'])

        // Return 1 if there are newer records than record to which metadata is pointing.
        return date2.compareTo(date1)
    }


    @Override
    List<Map<String, Object>> initialGetRecords(FlowPlugin flowPlugin, int i = 10) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()

        return (flowPlugin as SampleJIRAReporting).getIssues(
                params['jiraProjectName'],
                [
                        limit: i
                ]
        )
    }

    @Override
    List<Map<String, Object>> getRecordsAfter(FlowPlugin flowPlugin, Metadata metadata) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()

        def metadataValue = metadata.getValue()

        return (flowPlugin as SampleJIRAReporting).getIssues(
                params['jiraProjectName'],
                [
                        after: metadataValue['modifiedOn']
                ]
        )
    }

    @Override
    Map<String, Object> getLastRecord(FlowPlugin flowPlugin) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()

        def oneRecordList = (flowPlugin as SampleJIRAReporting).getIssues(
                params['jiraProjectName'],
                [
                        getLastIssue: true
                ]
        )

        return oneRecordList[0]
    }

    @Override
    Dataset buildDataset(FlowPlugin plugin, List<Map> records) {
        SampleJIRAReporting jiraPlugin = plugin as SampleJIRAReporting
        Dataset dataset = this.newDataset(['feature'], [])

        Context context = plugin.getContext()
        Map params = context.getRuntimeParameters().getAsMap()

        for (Map<String, Object> issue : records) {
            plugin.log.debug("Issue:", JSONObject.fromObject(issue).toString())

            Map<String, String> statusMappings = [
                    'Done'       : 'Closed',
                    'Closed'     : 'Closed',
                    'In Progress': 'In Progress',
                    'Open'       : 'Open',
                    'To Do'      : 'Open',
                    'Reopened'   : 'Reopened',
                    'Resolved'   : 'Resolved',
            ]

            String rawStatus = issue.fields.status?.statusCategory?.name ?: ''

            String jiraModifiedOn = issue.fields.updated ?: ''
            String jiraCreatedOn = issue.fields.created ?: ''
            String jiraResolvedOn = issue.fields.resolutionDate ?: ''

            String modifiedOn = jiraPlugin.jiraDateStringToISODatetime(jiraModifiedOn)
            String createdOn = jiraPlugin.jiraDateStringToISODatetime(jiraCreatedOn)
            String resolvedOn = jiraPlugin.jiraDateStringToISODatetime(jiraResolvedOn)

            dataset.newData([
                    reportObjectType: 'feature',
                    values          : [
                            source    : 'JIRA',
                            sourceUrl : params['endpoint'],
                            type      : issue.fields.issuetype?.name,
                            defectName: issue.fields.summary,
                            key       : issue.key,
                            resolution: issue.fields.resolution?.name ?: '',
                            status    : statusMappings[rawStatus] ?: 'Open',
                            modifiedOn: modifiedOn,
                            createdOn : createdOn,
                            resolvedOn: resolvedOn,
                    ]])
        }

        plugin.log.debug("DATASET: ", dataset['data'].toString())

        return dataset
    }
}