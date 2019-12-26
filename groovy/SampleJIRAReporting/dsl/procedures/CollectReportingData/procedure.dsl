// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'CollectReportingData', description: '''''', {
    property 'standardStepPicker', value: false

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'CollectReportingData', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/CollectReportingData/steps/CollectReportingData.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: 59c1f536b6ca72fb8eac8fd144b1677e ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}