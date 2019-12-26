// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'GetJenkinsJob', description: '''Saves details for Jenkins job to properties''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'GetJenkinsJob', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetJenkinsJob/steps/GetJenkinsJob.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'lastBuildNum',
        description: 'Number of the last job build'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: dcb0e142b97806420b7aed9d8f9dec7a ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}