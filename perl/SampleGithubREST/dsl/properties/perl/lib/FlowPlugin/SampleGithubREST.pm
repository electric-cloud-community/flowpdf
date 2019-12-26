package FlowPlugin::SampleGithubREST;
use JSON;
use strict;
use warnings;
use base qw/FlowPDF/;
use FlowPDF::Log;
use FlowPDF::Exception::RuntimeException;
use FlowPDF::Log;
use Try::Tiny;
use FlowPDF::Helpers qw(bailOut);
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
    my ($pluginObject, $r) = @_;

    my $context = $pluginObject->getContext();
    my $params = $context->getStepParameters();

    my $client = $pluginObject->getSampleGithubRESTRESTClient;
    # If you have changed your parameters in the procedure declaration, add/remove them here
    my %restParams = (
        repositoryOwner => $params->getParameter('repositoryOwner')->getValue,
        repositoryName => $params->getParameter('repositoryName')->getValue,
        tag_name => $params->getParameter('tagName')->getValue,
        name => $r->{name},
    );

    my $release;
    try {
        # if there is no such release, this block will fail, but it is fine - we will create the release object
        $release = $client->getReleaseByTagName(%restParams, tag => $params->getParameter('tagName')->getValue);
    };

    my $stepResult = $context->newStepResult;
    if (!$release) {
        $release = $client->createRelease(%restParams);
    }
    logInfo "Release: ", $release;
    my $releaseUrl = $release->{html_url};
    $stepResult->setReportUrl("Release URL", $releaseUrl);

    $stepResult->apply();

    my $releaseId = $release->{id};

    my ($existingAsset) = grep { $_->{name} eq $r->{assetName} } @{$release->{assets}};
    if ($existingAsset) {
        $context->bailOut({
            message => "The asset named $r->{assetName} already exists in the release $releaseId"
        });
    }

    my $assetPath = $r->{assetPath};

    unless(-f $assetPath){
        $context->bailOut({
            message => "The $assetPath should exist and should be a file (not a directory)",
            suggestion => "Please check if $assetPath exists and is file"
        });
    }

    my $asset = $client->uploadReleaseAsset(
        %restParams,
        releaseId => $releaseId,
        assetPath => $assetPath,
        assetName => $r->{assetName},
    );

    logInfo "Asset", $asset;
    logInfo("Got response from the server: ", $release);
}

## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.


1;
