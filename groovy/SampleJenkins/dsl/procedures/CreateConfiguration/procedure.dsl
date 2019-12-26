
// DO NOT EDIT THIS BLOCK === configuration starts ===
procedure 'CreateConfiguration', description: 'Creates a plugin configuration', {

    //First, let's download third-party dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
        resourceName = 'local'
    }

    step 'checkConnection',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/checkConnection.groovy").text,
        errorHandling: 'abortProcedure',
        shell: 'ec-groovy -cp $[/myJob/flowpdk_classpath]',
        condition: '$[/javascript myJob.checkConnection == "true" || myJob.checkConnection == "1"]',
        resourceName: 'local',
        postProcessor: '$[/myProject/perl/postpLoader]'

    step 'createConfiguration',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/createConfiguration.pl").text,
        errorHandling: 'abortProcedure',
        exclusiveMode: 'none',
        postProcessor: '$[/myProject/perl/postpLoader]',
        releaseMode: 'none',
        shell: 'ec-perl',
        timeLimitUnits: 'minutes'

    property 'ec_checkConnection', value: ''
// DO NOT EDIT THIS BLOCK === configuration ends, checksum: 36b51d355f49c6abe7bbad96eee5b8cc ===
}