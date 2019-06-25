
// === procedure_autogen starts ===
procedure 'Undeploy', description: 'Undeploys a previously deployed application', {

    step 'Undeploy', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/Undeploy/steps/Undeploy.pl").text
        shell = 'ec-perl'

        }
// === procedure_autogen ends, checksum: 044b3a45b979888c860568f0314254ae ===
}