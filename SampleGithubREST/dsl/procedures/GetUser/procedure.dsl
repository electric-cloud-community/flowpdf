// This procedure.dsl was generated automatically
// === procedure_autogen starts ===
procedure 'Get User', description: 'This procedure downloads the specified user data', {

    step 'Get User', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetUser/steps/GetUser.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// === procedure_autogen ends, checksum: 036bee2b8e61f047ac1c85da00fd2e96 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}