pipeline 'Sample webhook', {
    enabled = '1'
    skipStageMode = 'ENABLED'

    formalParameter 'ec_stagesToRun', defaultValue: null, {
        expansionDeferred = '1'
        label = null
        orderIndex = null
        required = '0'
        type = null
    }

    stage 'Stage 1', {
        colorCode = '#289ce1'
        completionType = 'auto'


        gate 'PRE', {
            condition = null
            precondition = null
        }

        gate 'POST', {
            condition = null
            precondition = null
        }
    }

    trigger 'Trigger', {
        actualParameter = [
            'ec_stagesToRun': '["Stage 1"]',
        ]
        enabled = '1'

        pluginKey = 'SampleWebhook'
        pluginParameter = [
            'eventType': 'Push Hook',
            'repository': 'ololo',
        ]
        serviceAccountName = 'test'
        triggerType = 'webhook'
        webhookName = 'default'

        // Custom properties

        property 'ec_trigger_state', {
            propertyType = 'sheet'
        }
        triggerSecret = 'secret'
    }

}