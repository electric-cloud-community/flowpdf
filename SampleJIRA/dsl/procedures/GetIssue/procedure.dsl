
// === procedure_autogen starts ===
procedure 'Get Issue', description: 'Gets issue by its id', {

    step 'Get Issue', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetIssue/steps/GetIssue.pl").text
        shell = 'ec-perl'

        }

    formalOutputParameter 'issue',
        description: 'An issue details'
// === procedure_autogen ends, checksum: 4736fe41488a1fe942b9000646f8d6eb ===
}