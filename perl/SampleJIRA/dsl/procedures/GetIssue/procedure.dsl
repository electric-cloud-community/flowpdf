
// DO NOT EDIT THIS BLOCK === procedure_autogen starts ===
procedure 'Get Issue', description: 'Gets issue by its id', {

    step 'Get Issue', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/GetIssue/steps/GetIssue.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }

    formalOutputParameter 'issue',
        description: 'An issue details'
// DO NOT EDIT THIS BLOCK === procedure_autogen ends, checksum: da2273ab94402bfee380a81f63972a62 ===
}