package FlowPlugin::TutorialJIRAReporting;
use strict;
use warnings;
use base qw/FlowPDF/;
use JSON;
use Data::Dumper;
use FlowPDF::Log;

### Importing exceptions that will be used for reporting.
use FlowPDF::Exception::WrongFunctionArgumentValue;
use FlowPDF::Exception::MissingFunctionArgument;
use FlowPDF::Exception::RuntimeException;
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
        }
    };
}

# Procedure parameters:
# config
# jiraProjectName
# previewMode
# transformScript
# debug
# releaseName
# releaseProjectName

sub collectReportingData {
    my $self = shift;
    my $params = shift;
    my $stepResult = shift;

    my $featureReporting = FlowPDF::ComponentManager->loadComponent(
        'FlowPlugin::TutorialJIRAReporting::Reporting', {
            reportObjectTypes     => ['feature'],
            metadataUniqueKey     => $params->{jiraProjectName},
            payloadKeys           => ['modifiedOn', 'key']
        }, $self);
    $featureReporting->CollectReportingData();
}
## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.

### This functions is a regular function that performs get request against provided url.
sub get {
    my ($self, $url) = @_;

    if (!$url) {
        FlowPDF::Exception::MissingFunctionArgument->new('Missing url parameter for function get')->throw();
    }
    if ($url !~ m|\/|s) {
        FlowPDF::Exception::WrongFunctionArgumentValue->new('$url parameter should be started from /, got: ' . $url)->throw();
    }
    my $context = $self->getContext();
    my $rest = $context->newRESTClient();
    my $runtimeParameters = $context->getRuntimeParameters();
    my $endpoint = $runtimeParameters->{endpoint};

    $endpoint =~ s|\/$||gs;

    my $finalUrl = $endpoint . $url;
    my $request = $rest->newRequest(GET => $finalUrl);
    my $response = $rest->doRequest($request);

    ### If code is more than 399, it is an error.
    if ($response->code() && $response->code() > 399) {
        FlowPDF::Exception::RuntimeException->new("Received not ok response with code: " . $response->code())->throw();
    }
    return decode_json($response->decoded_content());
}


### This functions receives an issues from JIRA using JQL.
### Since our reporting mechanics is primitive enough, we will be using only issue types stories.

sub getIssues {
    my ($self, $projectName, $opts) = @_;

    my $runtimeParameters = $self->getContext()->getRuntimeParameters();
    my $tempJQL = qq|project=$projectName AND issuetype=Story|;

    my $lastIssueJQL = $tempJQL . ' ORDER BY updatedDate DESC&maxResults=1';

    if ($opts->{after}) {
        if ($opts->{after} =~ m/^\d+$/s) {
            my $dt = DataTime->from_epoch($opts->{after});
            $opts->{after} = sprintf('%s %s', $dt->ymd(), $dt->hms());
        }
        $tempJQL .= qq| AND updatedDate > "$opts->{after}" ORDER BY updatedDate DESC|;
    }

    if ($opts->{limit}) {
        $tempJQL .= '&maxResults=' . $opts->{limit};
    }
    logInfo("Running JQL: $tempJQL");
    if ($opts->{getLastIssue}) {
        $tempJQL = $lastIssueJQL;
    }
    my $issues = $self->get('/rest/api/2/search?jql=' . $tempJQL);
    unless ($opts->{asRecords}) {
        return $issues;
    }
    my $records = [];
    for my $issue (@{$issues->{issues}}) {
        my $tempRecord = {
            type                => $issue->{fields}->{issuetype}->{name},
            source              => 'JIRA',
            createdOn           => $issue->{fields}->{created},
            defectName          => $issue->{fields}->{summary},
            key                 => $issue->{key},
            modifiedOn          => $issue->{fields}->{updated},
            resolution          => $issue->{fields}->{resolution}->{name},
            resolvedOn          => $issue->{fields}->{resolutionDate},
            sourceUrl           => $runtimeParameters->{endpoint},
            status              => $issue->{fields}->{status}->{statusCategory}->{name},
        };
        unshift @$records, $tempRecord;
    }

    return $records;
}



1;
