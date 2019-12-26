import sun.reflect.generics.reflectiveObjects.NotImplementedException

import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.components.ComponentManager

/**
* Asd
*/
class Asd extends FlowPlugin {

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


        ReportingAsd reporting = (ReportingAsd) ComponentManager.loadComponent(ReportingAsd.class, [
                reportObjectTypes  : ['build'],
                metadataUniqueKey  : new Random().nextInt().abs(),
                payloadKeys        : ['buildNumber'],
        ], this)
        reporting.collectReportingData()

    }
// === step ends ===

}
