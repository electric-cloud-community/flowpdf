import com.cloudbees.flowpdf.components.reporting.Reporting
import com.cloudbees.flowpdf.components.ComponentManager

import com.cloudbees.flowpdf.*

/**
* SampleReporting
*/
class SampleReporting extends FlowPlugin {

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

    /**
    * Procedure parameters:
    * @param config
    * @param param1
    * @param param2
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

        Reporting reporting = (Reporting) ComponentManager.loadComponent(ReportingSampleReporting.class, [
                reportObjectTypes  : ['build'],
                metadataUniqueKey  : params['param1'] + (params['param2'] ? ('-' + params['param2']) : ''),
                payloadKeys        : ['buildNumber'],
        ], this)

        reporting.collectReportingData()
    }
// === step ends ===

}