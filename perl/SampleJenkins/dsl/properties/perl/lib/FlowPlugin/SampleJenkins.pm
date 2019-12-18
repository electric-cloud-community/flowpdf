package FlowPlugin::SampleJenkins;
use strict;
use warnings;
use base qw/FlowPDF/;

use FlowPDF::Log;
use FlowPDF::Helpers qw/bailOut/;
use Data::Dumper;
use JSON;
use Try::Tiny;

# Feel free to use new libraries here, e.g. use File::Temp;

# Service function that is being used to set some metadata for a plugin.
sub pluginInfo {
    return {
        pluginName          => '@PLUGIN_KEY@',
        pluginVersion       => '@PLUGIN_VERSION@',
        configFields        => ['config'],
        configLocations     => ['ec_plugin_cfgs'],
        defaultConfigValues => {
            authScheme => 'basic'
        },
    };
}


# Auto-generated method for the procedure Get Last Build Number/Get Last Build Number
# Add your code into this method and it will be called when step runs
sub getLastBuildNumber {
    my ($pluginObject, $runtimeParameters, $stepResult) = @_;

    my $buildNumber = $pluginObject->retrieveLastBuildNumberFromJob($runtimeParameters->{jenkinsJobName});
    unless ($buildNumber) {
        bailOut("No buildNumber for jenkins job.");
    }

    logInfo("Last Build Number for $runtimeParameters->{jenkinsJobName} is $buildNumber");
    $stepResult->setOutputParameter(lastBuildNumber => $buildNumber);
    $stepResult->setPipelineSummary("Build Number for $runtimeParameters->{jenkinsJobName}", $buildNumber);
    $stepResult->setJobSummary("Build Number for $runtimeParameters->{jenkinsJobName}: $buildNumber");
    $stepResult->setJobStepSummary("Build Number for $runtimeParameters->{jenkinsJobName}: $buildNumber");

}
## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.

sub retrieveLastBuildNumberFromJob {
    my ($self, $jobName) = @_;

    if (!$jobName) {
        bailOut "Missing jobName for retrieveLastBuildNumberFromJob";
    }
    # Retrieving base URL for jenkins job.
    my $baseUrl = $self->getBaseUrl(
        sprintf('job/%s/api/json', $jobName)
    );

    # getting rest client
    my $rest = $self->getContext()->newRESTClient();
    # creating request object
    my $request = $rest->newRequest(GET => $baseUrl);
    # autmenting this request with crumbs
    $request = $self->addBreadCrumbsToRequest($request);
    # performing request:
    my $response = $rest->doRequest($request);
    # decoding request content:
    my $json = decode_json($response->decoded_content());
    # finding and returning build number.
    if ($json->{lastBuild} && $json->{lastBuild}->{number}) {
        return $json->{lastBuild}->{number};
    }
    return undef;
}


sub getBaseUrl {
    my ($self, $url) = @_;

    if (!$url) {
        bailOut("URL is mandatory parameter");
    }
    # retrieving runtime parameters
    my $runtimeParameters = $self->getContext()->getRuntimeParameters();
    # endpoint is a field from configuration
    my $endpoint = $runtimeParameters->{endpoint};

    # removing trailing slash.
    $endpoint =~ s|\/+$||gs;

    # appending url
    my $retval = $endpoint . '/' . $url;
    return $retval;
}


sub addBreadCrumbsToRequest {
    my ($self, $request) = @_;

    # Creating base URL using previously implemented function
    my $crumbUrl = $self->getBaseUrl('crumbIssuer/api/json');
    # Creating REST client object
    my $rest = $self->getContext()->newRESTClient();
    # Creating crumb request object
    my $crumbRequest = $rest->newRequest(GET => $crumbUrl);
    # actual request.
    my $response = $rest->doRequest($crumbRequest);

    if ($response->code() > 399) {
        return $request;
    }

    my $decodedResponse;
    try {
        $decodedResponse = decode_json($response->decoded_content());
    };

    if ($decodedResponse->{crumb} && $decodedResponse->{crumbRequestField}) {
        $request->header($decodedResponse->{crumbRequestField} => $decodedResponse->{crumb});
    }
    return $request;
}

1;

