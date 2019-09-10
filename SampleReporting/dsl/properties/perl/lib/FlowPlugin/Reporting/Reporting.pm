package FlowPlugin::Reporting::Reporting;
use Data::Dumper;
use base qw/FlowPDF::Component::EF::Reporting/;
use FlowPDF::Log;
use strict;
use DateTime;
use warnings;


# A metadata comparator. It should work exactly as cmp, <=> or any other sort function.
# If your metadata that is stored on CloudBees Flow side is pointing on non-latest data,
# simply return 1, it will trigger all logic.
# Note: You don't have to create metadata object by yourself.
# This part is being handled by this component behind the scene.
sub compareMetadata {
    my ($self, $metadata1, $metadata2) = @_;

    print Dumper $metadata1, $metadata2;

    my $value1 = $metadata1->getValue();
    my $value2 = $metadata2->getValue();
    # Implement here logic of metadata values comparison.
    # Return 1 if there are newer records than record to which metadata is pointing.
    return 1;
}

# Function that is responsible for initial data retrieval.
# It will have as parameter $limit. If limit is not passed or it is equals to 0,
# no limit is to be applied.
sub initialGetRecords {
    my ($self, $pluginObject, $limit) = @_;

    # build records and return them
    my $records = [{
        buildNumber => int rand 998889,
        test1 => 'test',
    }];
    # my $records = pluginObject->yourMethodTobuildTheRecords($limit);
    return $records;
}

# A function that retrieves a newer records than a record that is stored
# on the CloudBees Flow side in metadata.
sub getRecordsAfter {
    my ($self, $pluginObject, $metadata) = @_;

    # build records using metadata as start point using your functions
    # my $records = pluginObject->yourMethodTobuildTheRecordsAfter($metadata);
    my $records = [{
        buildNumber => int rand 89128912,
        test1 => 'test'
    }];
    return $records;
}

# A function that always return a last record.
# This function should returh a hash reference instead of array of hash references.
sub getLastRecord {
    my ($self, $pluginObject) = @_;

    my $lastRecord = {buildNumber => int rand 938193};
    return $lastRecord;
}

# A function that gets records set as parameter and builds a dataset from them.
# Note, that transformation script is being applied right before this function
# automatically.
sub buildDataset {
    my ($self, $pluginObject, $records) = @_;

    my $dataset = $self->newDataset(['build']);
    my $context = $pluginObject->getContext;
    my $params = $context->getRuntimeParameters();

    for my $row (@$records) {
        # now, data is a pointer, you need to populate it by yourself using it's methods.
        my $data = $dataset->newData({
            reportObjectType => 'build',
        });

        my $today = DateTime->now;
        # Just some random data
        $row->{source} = "Test Reporting";
        $row->{pluginName} = '@PLUGIN_NAME@';
        $row->{projectName} = $context->retrieveCurrentProjectName;
        $row->{releaseProjectName} = $params->{releaseProjectName};
        $row->{releaseName} = $params->{releaseName};
        $row->{timestamp} = $today->ymd . 'T' . $today->hms . '.000Z';
        my $status = rand() > 0.5 ? 'SUCCESS' : 'FAILURE';
        $row->{buildStatus} = $status;
        $row->{pluginConfiguration} = $params->{config};
        $row->{endTime} = $today->ymd . 'T' . $today->hms . '.000Z';
        $row->{startTime} = $today->ymd . 'T' . $today->hms . '.000Z';
        $row->{duration} = (int rand 9839) * 1000;

        for my $k (keys %$row) {
            $data->{values}->{$k} = $row->{$k};
        }

    }
    return $dataset;
}



1;
