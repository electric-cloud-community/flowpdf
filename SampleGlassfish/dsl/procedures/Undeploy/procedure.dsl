
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'Undeploy', description: 'Undeploys a previously deployed application', {

    step 'Undeploy', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/Undeploy/steps/Undeploy.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: 68fd8d36086ffddc145a14ea1b54770c ===
}