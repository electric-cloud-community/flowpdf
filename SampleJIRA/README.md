## SampleJIRAPlugin

This plugin demonstrates a REST-based integration using
[Atlassian JIRA](https://www.atlassian.com/software/jira) as a sample.

The plugin contains one procedure that will fetch a specified issue using the issue ID.
The plugin supports two authentication schemes: Basic and OAuth 1.0.

* [Plugin Specification](config/pluginspec.yaml)
* [Plugin Code](dsl/properties/perl/lib/FlowPlugin/SampleJIRAPlugin.pm)

To create an equivalent plugin:
 
 1. Generate the workspace for plugin with the following command:

`    flowpdk generate workspace`

 2. Replace the _config/pluginspec.yaml_ with sample pluginspec.yaml (the "Plugin Specification" link above) and generate a new plugin from it using

`    flowpdk generate plugin`

 3. Open the `dsl/properties/perl/lib/FlowPlugin/SampleJIRAPlugin.pm` and replace the generated content with the following block:

```perl
package FlowPlugin::SampleJIRAPlugin;
use strict;
use warnings;
use base qw/FlowPDF/;

use FlowPDF::Log;

# Service function that is being used to set some metadata for a plugin.
sub pluginInfo {
    return {
        pluginName    => '@PLUGIN_KEY@',
        pluginVersion => '@PLUGIN_VERSION@',
        configFields  => ['config'],
        configLocations => ['ec_plugin_cfgs'],
        pluginValues    => {
            oauth => {
                request_method         => 'POST',
                oauth_signature_method => 'RSA-SHA1',
                oauth_version          => '1.0',
                request_token_path     => 'plugins/servlet/oauth/request-token',
                authorize_token_path   => 'plugins/servlet/oauth/authorize',
                access_token_path      => 'plugins/servlet/oauth/access-token',
            }
        }
    };
}

# Auto-generated method for the procedure Get Issue/Get Issue
# Add your code into this method and it will be called when step runs
sub getIssue {
    my ($pluginObject) = @_;

    my $context = $pluginObject->newContext();
    my $params = $context->getRuntimeParameters();
    my $configValues = $context->getConfigValues();

    my $restClientCreationParams = {};
    if ($params->{authType} eq 'OAuth1.0') {
        logInfo("Using oauth");

        my $oauthCred = $configValues->getParameter('oauth_credential');
        my $token = $oauthCred->getUserName;
        my $privateKey = $oauthCred->getSecretValue;

        my $oauthValues = $pluginObject->getPluginValues()->{oauth};

        $oauthValues->{private_key} = $privateKey;
        print "Setting private key:$privateKey(end)\n";
        $oauthValues->{oauth_token} = $token;
        print "Setting oauth_token :$token(end)\n";
        $oauthValues->{oauth_consumer_key} = $params->{oauthConsumerKey};
        print "Setting consumer key:$params->{oauthConsumerKey}(end)\n";
        $oauthValues->{base_url} = $params->{jiraUrl};
        $oauthValues->{request_method} = 'GET';

        $restClientCreationParams->{oauth} = $oauthValues;
        logDebug("Created REST client creation params: ", Dumper $restClientCreationParams);
    }

    my $rest = $context->newRESTClient($restClientCreationParams);

    my $jiraUrl = $pluginObject->buildJiraAPIUrl($context);
    $jiraUrl .= '/issue/' . $params->{issueId};
    logInfo("Built jira URL: $jiraUrl");

    if ($params->{authType} eq 'OAuth1.0') {
        my $oauth = FlowPDF::ComponentManager->getComponent('FlowPDF::Component::OAuth');
        my $requestParams = $oauth->augment_params_with_oauth('GET', $jiraUrl, {});
        $jiraUrl = $rest->augmentUrlWithParams($jiraUrl, $requestParams);
    }

    my $request = $rest->newRequest(GET => $jiraUrl);
    # $request is HTTP::Request object https://metacpan.org/pod/HTTP::Request
    if ($params->{authType} eq 'Basic') {
        $request->authorization_basic($params->{user}, $params->{password});
    }
    my $response = $rest->doRequest($request);
    my $stepResult = $context->newStepResult();
    if ($response->is_success) {
        $stepResult->setJobSummary("Got issue.");
        $stepResult->setOutputParameter('issue', $response->decoded_content());
    }
    else {
        $stepResult->setJobStepOutcome('error');
        $stepResult->setJobSummary("Failed to get the issue");
        logInfo($response->content);
    }

    $stepResult->apply();
}
## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.

sub buildJiraAPIUrl {
    my ($self, $context) = @_;

    my $params = $context->getRuntimeParameters();
    my $jiraUrl = $params->{jiraUrl};
    $jiraUrl =~ s/\/$//gs;
    $jiraUrl .= '/rest/api/2';
    return $jiraUrl;
}

1;
```

 4. Build the plugin using `flowpdk build` command. The functional plugin will appear in the `build/` folder.
