// This procedure.dsl was generated automatically
// === procedure_autogen starts ===
procedure 'CollectReportingData', description: '', {
    property 'standardStepPicker', value: false

    step 'CollectReportingData', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/CollectReportingData/steps/CollectReportingData.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// === procedure_autogen ends, checksum: 73916b2b53394000e20a2d77159f70be ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}