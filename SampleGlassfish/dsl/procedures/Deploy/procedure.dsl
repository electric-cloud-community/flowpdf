
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'Deploy', description: 'Deploys an application to Glassfish', {

    step 'Deploy', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/Deploy/steps/Deploy.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'deployed',
        description: 'JSON representation of the deployed application'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: ce0b18a833e29de6976d9a4b7f3e0c26 ===
}