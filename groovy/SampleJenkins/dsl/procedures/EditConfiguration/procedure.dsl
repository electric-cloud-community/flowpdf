
// DO NOT EDIT THIS BLOCK === configuration starts ===
// This part is auto-generated and will be regenerated upon subsequent updates
procedure 'EditConfiguration', description: 'Checks connection for the changed configuration', {

    step 'checkConnection',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/checkConnection.pl").text,
        errorHandling: 'abortProcedure',
        shell: 'ec-perl',
        postProcessor: '$[/myProject/perl/postpLoader]',
        resourceName: 'local',
        condition: '$[/javascript myJob.checkConnection == "true" || myJob.checkConnection == "1"]'

}
// DO NOT EDIT THIS BLOCK === configuration ends, checksum: 039b1e1a410c30f43151e5e2cfb6cd58 ===
