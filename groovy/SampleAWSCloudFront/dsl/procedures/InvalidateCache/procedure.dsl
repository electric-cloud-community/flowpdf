// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'Invalidate Cache', description: '''Invalidates the distribution cache''', {

    // Handling binary dependencies
    step 'flowpdk-setup', {
        description = "This step handles binary dependencies delivery"
        subprocedure = 'flowpdk-setup'
        actualParameter = [
            generateClasspathFromFolders: 'deps/libs'
        ]
    }

    step 'Invalidate Cache', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/InvalidateCache/steps/InvalidateCache.groovy").text
        shell = 'ec-groovy'
        shell = 'ec-groovy -cp $[/myJob/flowpdk_classpath]'

        resourceName = '$[flowpdkResource]'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'invalidationId',
        description: 'Id of the created invalidation'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: fadc4e6c585d7d29d8036ab4b9a53351 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}