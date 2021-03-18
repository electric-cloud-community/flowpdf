import groovy.json.JsonSlurper

def retval = []

//https://docs.gitlab.com/ee/user/project/integrations/webhooks.html

def trigger = args.trigger
def body = args.body
Map<String, String> headers = args.headers
def event = headers.get('x-gitlab-event')
def token = headers.get('x-gitlab-token')

if (trigger.webhookSecret != token) {
    throw new RuntimeException("Failed to validate token")
}

def decoded = new JsonSlurper().parseText(body)
def branch = decoded.ref?.replaceAll('refs/heads/', '')

def pluginParameters = [:]
trigger.pluginParameters.properties.each { k, v ->
    pluginParameters[k] = v['value']
}

retval << "Parameters: $pluginParameters"
def repositoryParameter = pluginParameters['repository']
retval << "Parameter $repositoryParameter"

def eventTypeParameter = pluginParameters['eventType']
retval << "Parameter $eventTypeParameter"

if (eventTypeParameter != event) {
    return [
        responseMessage: "Ignoring unsupported '${event}' event".toString(),
        eventType      : event,
        launchWebhook  : false
    ]
}
def repoName = decoded.repository?.name
if (repositoryParameter != repoName) {
    return [
        responseMessage: "Ignoring unsupported repository ${repoName}".toString(),
        repository     : repoName,
        launchWebhook  : false
    ]
}

def commitId = decoded.checkout_sha

def response = [
    eventType      : event,
    launchWebhook  : true,
    branch         : branch,
    responseMessage: "Launched for commit $commitId".toString(),
    webhookData    : [commitId: commitId, branch: branch.toString(), user_name: decoded.user_name?.toString()] as Map<String, String>,
]

return response