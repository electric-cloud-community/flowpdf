
// === configuration starts ===
// This part is auto-generated and will be regenerated upon subsequent updates
procedure 'CreateConfiguration', description: 'Creates a plugin configuration', {

    step 'checkConnectionGeneric',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/checkConnectionGeneric.groovy").text,
        errorHandling: 'abortProcedure',
        shell: 'ec-groovy',
        condition: '$[/javascript myJob.checkConnection == "true"]'

    step 'createConfiguration',
        command: new File(pluginDir, "dsl/procedures/CreateConfiguration/steps/createConfiguration.pl").text,
        errorHandling: 'abortProcedure',
        exclusiveMode: 'none',
        postProcessor: 'postp',
        releaseMode: 'none',
        shell: 'ec-perl',
        timeLimitUnits: 'minutes'

    property 'ec_checkConnection', value: ''
// === configuration ends, checksum: b4a6c484fc0423abd31f5544c4f797f4 ===
// Place your code below
}