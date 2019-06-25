
// === procedure_autogen starts ===
procedure 'Deploy', description: 'Deploys an application to Glassfish', {

    step 'Deploy', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/Deploy/steps/Deploy.pl").text
        shell = 'ec-perl'

        }

    formalOutputParameter 'deployed',
        description: 'JSON representation of the deployed application'
// === procedure_autogen ends, checksum: 027e9824e29516f135ae93c6a2d7bea7 ===
}