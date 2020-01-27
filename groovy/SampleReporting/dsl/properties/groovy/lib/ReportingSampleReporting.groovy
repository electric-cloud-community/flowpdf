import com.cloudbees.flowpdf.FlowPlugin
import com.cloudbees.flowpdf.Log
import com.cloudbees.flowpdf.components.reporting.Dataset
import com.cloudbees.flowpdf.components.reporting.Metadata
import com.cloudbees.flowpdf.components.reporting.Reporting

import java.time.Instant

/**
 * User implementation of the reporting classes
 */
class ReportingSampleReporting extends Reporting {


    /*
    // Default compareMetadata implementation can compare numeric values automatically.
    // Uncomment and implement additional logic if necessary.
    @Override
    int compareMetadata(Metadata param1, Metadata param2) {
        def value1 = param1.getValue()
        def value2 = param2.getValue()

        return value2['buildNumber'].compareTo(value1['buildNumber'])
    }*/

    @Override
    List<Map<String, Object>> initialGetRecords(FlowPlugin flowPlugin, int i = 10) {
        Map<String, Object> params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        flowPlugin.log.logDebug("Initial parameters.\n" + params.toString())

        // Generating initial records
        def records = generateRecords(params, i, 1)

        return records
    }

    @Override
    List<Map<String, Object>> getRecordsAfter(FlowPlugin flowPlugin, Metadata metadata) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        def metadataValues = metadata.getValue()
        def log = flowPlugin.getLog()

        log.info("\n\nGot metadata value in getRecordsAfter:  ${metadataValues.toString()}")

        // Should generate one build right after the initial set
        List<Map<String, Object>> records = generateRecords(params, 1, metadataValues['buildNumber'] + 1)

        log.info("Records after GetRecordsAfter ${records.toString()}")

        return records
    }

    @Override
    Map<String, Object> getLastRecord(FlowPlugin flowPlugin) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        def log = flowPlugin.getLog()

        log.info("Last record runtime params: ${params.toString()}")

        // Last record will always be 11th.
        int initialPos = 11

        return generateRecords(params, 1, initialPos)[0]
    }

    @Override
    Dataset buildDataset(FlowPlugin plugin, List<Map> records) {
        def dataset = this.newDataset(['build'], [])
        def context = plugin.getContext()
        def params = context.getRuntimeParameters().getAsMap()

        def log = plugin.getLog()
        log.info("Start procedure buildDataset")

        log.info("buildDataset received params: ${params}")
        println records

        for (def row in records.reverse()) {
            def payload = [
                    source             : 'Test Source',
                    pluginName         : '@PLUGIN_NAME@',
                    projectName        : context.retrieveCurrentProjectName(),
                    releaseName        : params['releaseName'] ?: '',
                    releaseProjectName : params['releaseProjectName'] ?: '',
                    pluginConfiguration: params['config'],
                    baseDrilldownUrl   : (params['baseDrilldownUrl'] ?: params['endpoint']) + '/browse/',

                    buildNumber        : row['buildNumber'],
                    timestamp          : row['startTime'],
                    endTime            : row['endTime'],
                    startTime          : row['startTime'],
                    buildStatus        : row['buildStatus'],
                    launchedBy         : 'N/A',
                    duration           : row['duration'],
            ]

            for (key in payload.keySet()) {
                if (!payload[key]) {
                    log.info("Payload parameter '${key}' don't have a value and will not be sent.")
                    payload.remove(key)
                }
            }

            dataset.newData(
                    reportObjectType: 'build',
                    values: payload
            )
        }

        log.info("Dataset: ${dataset.data}")

        return dataset
    }

    static List<Map<String, Object>> generateRecords(Map<String, Object> params, int count, int startPos = 1) {

        List<Map<String, Object>> generatedRecords = new ArrayList<>()

        for (int i = 0; i < count; i++) {
            String status = new Random().nextDouble() > 0.5 ? "SUCCESS" : "FAILURE"

            Instant generatedDate = new Date().toInstant()

            int buildNumber = startPos + i

            Log.logInfo("StartPos: $startPos, Generating with build number" + buildNumber)

            // Minus one day
            generatedDate.minusSeconds(86400)

            // Adding a second, so builds have a time sequence
            generatedDate.plusSeconds(buildNumber)

            String dateString = generatedDate.toString()

            def record = [
                    source             : "SampleReporting",
                    pluginName         : "@PLUGIN_NAME@",
                    buildNumber        : buildNumber,
                    projectName        : params['releaseProjectName'],
                    releaseName        : params['releaseName'],
                    timestamp          : dateString,
                    buildStatus        : status,
                    pluginConfiguration: params['config'],
                    endTime            : dateString,
                    startTime          : dateString,
                    duration           : new Random().nextInt().abs()
            ]

            generatedRecords += (record)
        }

        return generatedRecords.reverse()
    }
}