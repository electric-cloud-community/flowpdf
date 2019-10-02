// This procedure.dsl was generated automatically
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'Get Last Build Number', description: 'This procedure gets last build number from provided Jenkins job.', {

    step 'Get Last Build Number', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetLastBuildNumber/steps/GetLastBuildNumber.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'lastBuildNumber',
        description: 'A last build number for job.'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: 11d327ea31f16bcfefaed09c6622474b ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}