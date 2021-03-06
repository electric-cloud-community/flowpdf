import com.cloudbees.flowpdf.*
import com.cloudbees.flowpdf.components.ComponentManager
import com.cloudbees.flowpdf.components.cli.*

/**
 * SampleGradle
 */
class SampleGradle extends FlowPlugin {

    @Override
    Map<String, Object> pluginInfo() {
        return [
                pluginName         : '@PLUGIN_KEY@',
                pluginVersion      : '@PLUGIN_VERSION@',
                configFields       : ['config'],
                configLocations    : ['ec_plugin_cfgs'],
                defaultConfigValues: [:]
        ]
    }

/**
 * runGradle - Run Gradle/Run Gradle
 * Add your code into this method and it will be called when the step runs
 * @param tasks (required: true)
 * @param options (required: false)
 * @param workspaceDir (required: false)
 * @param useWrapper (required: true)
 * @param saveLogs (required: true)

 */
    def runGradle(StepParameters p, StepResult sr) {
        /** Reading parameters */
        String tasksRaw = p.getRequiredParameter('tasks').getValue() as String
        String optionsRaw = p.getParameter('options').getValue() as String
        String workspaceDir = p.getParameter('workspaceDir').getValue() as String
        boolean useWrapper = p.getParameter('useWrapper').getValue() as boolean
        boolean saveLogs = p.getParameter('saveLogs').getValue() as boolean

        /** Processing the parameters*/
        ArrayList<String> tasks = tasksRaw.split(' ')
        ArrayList<String> options = optionsRaw.split(';')

        String executableName = 'gradle'
        if (useWrapper) {
            executableName = (CLI.isWindows()) ? 'gradlew.bat' : './gradlew'
        }

        if (!workspaceDir) {
            workspaceDir = System.getProperty('user.dir')
        }

        /** Instantiating CLI component with a ComponentManager */
        CLI cli = (CLI) ComponentManager.loadComponent(CLI.class, [workingDirectory: workspaceDir], this)

        /** Creating a Command instance */
        Command cmd = cli.newCommand(executableName, tasks)

        if (options.size() > 0) {
            cmd.addArguments(options)
        }

        log.infoDiag("Command to run is " + cmd.renderCommand().command().join(' '))

        try {
            ExecutionResult result = cli.runCommand(cmd)

            if (saveLogs) {
                String stdOut = result.getStdOut()
                String stdErr = result.getStdErr()

                if (stdOut)
                    sr.setOutcomeProperty('/myJob/gradleStdOut', result.getStdOut())

                if (stdErr)
                    sr.setOutcomeProperty('/myJob/gradleStdErr', result.getStdErr())
            }

            if (!result.isSuccess()) {
                log.errorDiag("Standard output: " + result.getStdOut())
                log.errorDiag("Standard err: " + result.getStdErr())
                sr.setJobStepOutcome('error')
            }

            sr.setJobStepSummary('All tasks have been finished.')
        }
        catch (Exception ex) {
            ex.printStackTrace()
            sr.setJobStepOutcome('error')
            sr.setJobStepSummary(ex.getMessage())
        }

        log.info("Finished")
        sr.apply()
    }

// === step ends ===

}