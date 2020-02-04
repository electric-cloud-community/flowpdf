// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'GetIssue', description: '''Saves details for Jira issue to the properties''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'GetIssue', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetIssue/steps/GetIssue.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'issueStatus',
        description: 'Name of the status of the retrieved issue'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: 45ca18e86e3e9dae574c2132024092ce ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}