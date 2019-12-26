package FlowPlugin::Reporting;
use strict;
use warnings;
use base qw/FlowPDF/;
use Data::Dumper;
# Feel free to use new libraries here, e.g. use File::Temp;

# Service function that is being used to set some metadata for a plugin.
sub pluginInfo {
    return {
        pluginName    => '@PLUGIN_KEY@',
        pluginVersion => '@PLUGIN_VERSION@',
        configFields  => ['config'],
        configLocations => ['ec_plugin_cfgs']
    };
}


sub validateCRDParams {
    my $self = shift;
    my $params = shift;
    my $stepResult = shift;

    # Add parameters check here, e.g.
    # if (!$params->{myField}) {
    #     use FlowPDF::Helpers qw/bailOut/;
    #     bailOut("Field myField is required");
    # }

    $stepResult->setJobSummary('success');
    $stepResult->setJobStepOutcome('Parameters check passed');

    exit 0;
}


sub collectReportingData {
    my $self = shift;
    my $params = shift;
    my $stepResult = shift;


    my $buildReporting = FlowPDF::ComponentManager->loadComponent('FlowPlugin::Reporting::Reporting', {
        # An array reference of report object types that are supported by your component.
        reportObjectTypes     => [ 'build' ],

        # An unique key for metadata. It will be used to store metadata for
        # different datasource entities in the different paths.
        # It should be set to some value. For example, if you have a parameter
        # for the jenkins job, that should be reported, you may set this to it's value,
        # like HelloWorld. Basically, you can use any string here. But you need to be
        # sure, that your unique key is really unique and you can use it for further
        # metadata retrieval. Do not use just random values like in this sample, it is not
        # applicable for the real case.
        metadataUniqueKey     => int rand 89898,

        # The fields of payload that will be used for metadata creation.
        # An array reference of scalars. These fields should be present in payload.
        # If not, procedure will be failed. For example, if you have in payload buildNumber
        # field, and you want to have this number as identifier, provide just ['buildNumber'].
        payloadKeys           => [ 'buildNumber' ]
    }, $self);
    $buildReporting->CollectReportingData();
}

## === step ends ===
# Please do not remove the marker above, it is used to place new procedures into this file.


1;
