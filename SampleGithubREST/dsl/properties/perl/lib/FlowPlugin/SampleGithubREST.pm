package FlowPlugin::SampleGithubREST;
use JSON;
use strict;
use warnings;
use base qw/FlowPDF/;
use FlowPDF::Log;

# Feel free to use new libraries here, e.g. use File::Temp;

# Service function that is being used to set some metadata for a plugin.
sub pluginInfo {
    return {
        pluginName          => '@PLUGIN_KEY@',
        pluginVersion       => '@PLUGIN_VERSION@',
        configFields        => ['config'],
        configLocations     => ['ec_plugin_cfgs'],
        defaultConfigValues => {}
    };
}

# Auto-generated method for the procedure Get User/Get User
# Add your code into this method and it will be called when step runs
# Parameter: config
# Parameter: username

sub getUser {
    my ($pluginObject) = @_;

    my $context = $pluginObject->getContext();
    my $params = $context->getStepParameters();

    my $SampleGithubRESTRESTClient = $pluginObject->getSampleGithubRESTRESTClient;
    # If you have changed your parameters in the procedure declaration, add/remove them here
    my %restParams = (
        username => $params->getParameter('username')->getValue,
    );
    my $response = $SampleGithubRESTRESTClient->getUser(%restParams);
    logInfo("Got response from the server: ", $response);

    my $stepResult = $context->newStepResult;

    $stepResult->apply();
}

# This method is used to access auto-generated REST client.
sub getSampleGithubRESTRESTClient {
    my ($self) = @_;

    my $context = $self->getContext();
    my $config = $context->getRuntimeParameters();
    require FlowPlugin::SampleGithubRESTRESTClient;
    my $client = FlowPlugin::SampleGithubRESTRESTClient->createFromConfig($config);
    return $client;
}

# Auto-generated method for the procedure Create Release/Create Release
# Add your code into this method and it will be called when step runs
# Parameter: config
# Parameter: repositoryOwner
# Parameter: repositoryName
# Parameter: tagName
# Parameter: name
# Parameter: assetName
# Parameter: assetPath

sub createRelease {
    my ($pluginObject) = @_;

    my $context = $pluginObject->getContext();
    my $params = $context->getStepParameters();

    my $client = $pluginObject->getSampleGithubRESTRESTClient;
    # If you have changed your parameters in the procedure declaration, add/remove them here
    my %restParams = (
        repositoryOwner => $params->getParameter('repositoryOwner')->getValue,
        repositoryName => $params->getParameter('repositoryName')->getValue,
        tag_name => $params->getParameter('tagName')->getValue,
        name => $params->getParameter('name')->getValue,
    );
    my $release = eval {
        $client->getReleaseByTagName(%restParams, tag => $params->getParameter('tagName')->getValue);
    };

    if (!$release) {
        $release = $client->createRelease(%restParams);
    }
    logInfo "Release: ", $release;

    my $releaseId = $release->{id};
    my $asset = eval {
        $client->uploadReleaseAsset(
            %restParams,
            releaseId => $releaseId,
            assetPath => $params->getParameter('assetPath')->getValue,
        );
    };
    logInfo "Asset", $asset;
    logInfo("Got response from the server: ", $release);

    my $stepResult = $context->newStepResult;

    $stepResult->apply();
}

## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.


1;
