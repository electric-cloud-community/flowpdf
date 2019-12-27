// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'GetIssues', description: '''Sample procedure description''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'GetIssues', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetIssues/steps/GetIssues.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'issueIds',
        description: 'Ids of the retrieved issues.'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: adffc76917b84590ab88101bae50ea5d ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}