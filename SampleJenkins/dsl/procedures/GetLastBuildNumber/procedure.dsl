// This procedure.dsl was generated automatically
// === procedure_autogen starts ===
procedure 'Get Last Build Number', description: 'This procedure gets last build number from provided Jenkins job.', {

    step 'Get Last Build Number', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetLastBuildNumber/steps/GetLastBuildNumber.pl").text
        shell = 'ec-perl'

        }

    formalOutputParameter 'lastBuildNumber',
        description: 'A last build number for job.'
// === procedure_autogen ends, checksum: e1d648be69ef491f809573445ca1038c ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}