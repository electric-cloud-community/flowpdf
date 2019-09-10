// This procedure.dsl was generated automatically
// === procedure_autogen starts ===
procedure 'Create Release', description: 'This procedure creates a Github release from the specified assets', {

    step 'Create Release', {
        description = ''
        command = new File(pluginDir, "dsl/procedures/CreateRelease/steps/CreateRelease.pl").text
        shell = 'ec-perl'

        postProcessor = '''$[/myProject/perl/postpLoader]'''
    }
// === procedure_autogen ends, checksum: 7302afeaaa6f6ee9d6eab7a587ecc154 ===
// Do not update the code above the line
// procedure properties declaration can be placed in here, like
// property 'property name', value: "value"
}