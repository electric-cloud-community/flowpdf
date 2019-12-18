import com.cloudbees.flowpdf.*
import sun.reflect.generics.reflectiveObjects.NotImplementedException
import com.cloudbees.flowpdf.components.reporting.Dataset
import com.cloudbees.flowpdf.components.reporting.Metadata
import com.cloudbees.flowpdf.components.reporting.Reporting

/**
 * User implementation of the reporting classes
 */
class ReportingAsd extends Reporting {

    @Override
    int compareMetadata(Metadata param1, Metadata param2) {
        def value1 = param1.getValue()
        def value2 = param2.getValue()

        def pluginObject = this.getPluginObject()
        // Return 1 if there are newer records than record to which metadata is pointing.
        return 1
    }


    @Override
    List<Map<String, Object>> initialGetRecords(FlowPlugin flowPlugin, int i = 10) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        println params
        // def records = (flowPlugin as Asd).getBuildRuns(params['projectKey'], params['planKey'], [
        //         maxResults: i
        // ])

        // $row->{source} = "Test Reporting";
        // $row->{pluginName} = '@PLUGIN_NAME@';
        // $row->{projectName} = $context->retrieveCurrentProjectName;
        // $row->{releaseProjectName} = $params->{releaseProjectName};
        // $row->{releaseName} = $params->{releaseName};
        // $row->{timestamp} = $today->ymd . 'T' . $today->hms . '.000Z';
        // my $status = rand() > 0.5 ? 'SUCCESS' : 'FAILURE';
        // $row->{buildStatus} = $status;
        // $row->{pluginConfiguration} = $params->{config};
        // $row->{endTime} = $today->ymd . 'T' . $today->hms . '.000Z';
        // $row->{startTime} = $today->ymd . 'T' . $today->hms . '.000Z';
        // $row->{duration} = (int rand 9839) * 1000;

        String status = new Random().nextDouble() > 0.5 ? "SUCCESS" : "FAILURE"

        def record = [
            source: "Test Reporting",
            pluginName: "@PLUGIN_NAME@",
            projectName: params['releaseProjectName'],
            releaseName: params['releaseName'],
            timestamp: new Date().toInstant().toString(),
            buildStatus: status,
            pluginConfiguration: params['config'],
            endTime: new Date().toInstant().toString(),
            startTime : new Date().toInstant().toString(),
            duration: new Random().nextInt().abs()
        ]
        return [record]
    }

    @Override
    List<Map<String, Object>> getRecordsAfter(FlowPlugin flowPlugin, Metadata metadata) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        def metadataValues = metadata.getValue()

        def log = flowPlugin.getLog()
        log.info("Got metadata value in getRecordsAfter:  ${metadataValues.toString()}")

        log.info("Records after GetRecordsAfter ${records.toString()}")
        return []
    }

    @Override
    Map<String, Object> getLastRecord(FlowPlugin flowPlugin) {
        def params = flowPlugin.getContext().getRuntimeParameters().getAsMap()
        def log = flowPlugin.getLog()
        log.info("Last record runtime params: ${params.toString()}")
        throw new NotImplementedException()
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

                    // releaseUri         : (params['projectKey'] + (params['planKey'] ? "-${params['planKey']}" : '')),
                    releaseProjectName : params['releaseProjectName'] ?: '',
                    pluginConfiguration: params['config'],
                    baseDrilldownUrl   : (params['baseDrilldownUrl'] ?: params['endpoint']) + '/browse/',
                    buildNumber        : new Random().nextInt().abs(),
                    timestamp          : row['startTime'],
                    endTime            : row['endTime'],
                    startTime          : row['startTime'],
                    buildStatus        : row['buildStatus'],
                    launchedBy         : 'N/A',
                    // jobName            : row['key'],
                    duration           : row['duration'],
                    // tags                : '',
                    // sourceUrl          : row['link']['href'],
            ]

            for (key in payload.keySet()) {
                if (!payload[key]) {
                    log.info("Payload parameter '${key}' don't have a value and will not be sent.")
                    payload.remove(key)
                }
            }
            log.info("procedure buildDataset created payload: ${payload}")
            dataset.newData(
                    reportObjectType: 'build',
                    values: payload
            )
        }

        log.info("Dataset: ${dataset.data}")
        return dataset
    }
}
