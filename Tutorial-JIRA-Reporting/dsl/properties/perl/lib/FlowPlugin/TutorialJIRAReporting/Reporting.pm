package FlowPlugin::TutorialJIRAReporting::Reporting;
use Data::Dumper;
use base qw/FlowPDF::Component::EF::Reporting/;
use FlowPDF::Log;
use strict;
use warnings;
use DateTime;

### Service function, that takes string date in jira format and returns DateTime object.
sub getDateTimeObject {
    my ($self, $date) = @_;

    if ($date =~ m/^(\d+)-(\d+)-(\d+)T(\d+):(\d+):(\d+).*$/s) {
        my $dt = DateTime->new(
            year   => $1,
            month  => $2,
            day    => $3,
            hour   => $4,
            minute => $5,
            second => $6
        );
        return $dt;
    }
    return undef;
}


### This function converts jira's internal date representation in zulu format, that being used across reporting.
sub jiraDateToZulu {
    my ($self, $date) = @_;

    my $dt = $self->getDateTimeObject($date);

    my $zulu = sprintf('%sT%s.000Z', $dt->ymd(), $dt->hms());
    return $zulu
}


### This function compares metadata. It works exactly as the sort function in perl.
### If 1st argument of this function is "bigger" than 2nd, it should return 1. If equal 0, else -1.
sub compareMetadata {
    my ($self, $metadata1, $metadata2) = @_;

    my $value1 = $metadata1->getValue();
    my $value2 = $metadata2->getValue();

    # Implement here logic of metadata values comparison.
    # Return 1 if there are newer records than record to which metadata is pointing.
    my $dt1 = $self->getDateTimeObject($value1->{modifiedOn});
    my $dt2 = $self->getDateTimeObject($value2->{modifiedOn});
    return $dt1->epoch() <=> $dt2->epoch();
}


sub initialGetRecords {
    my ($self, $pluginObject, $limit) = @_;

    my $jiraProjectName = $pluginObject->getContext()->getRuntimeParameters()->{jiraProjectName};
    # build records and return them
    # todo required fields
    my $records = $pluginObject->getIssues($jiraProjectName, {asRecords => 1, after => '1970-01-01 00:00'});
    return $records;
}

sub getRecordsAfter {
    my ($self, $pluginObject, $metadata) = @_;

    my $jiraProjectName = $pluginObject->getContext()->getRuntimeParameters()->{jiraProjectName};
    my $value = $metadata->getValue()->{modifiedOn};
    my $issueKey = $metadata->getValue()->{key};
    my $dt = $self->getDateTimeObject($value);
    my $lastDate = sprintf('%s %s:%s', $dt->ymd(), $dt->hour(), $dt->minute());
    # build records using metadata as start point using your functions
    my $records = $pluginObject->getIssues($jiraProjectName, {
        asRecords => 1,
        after => $lastDate
    });

    # this is required because jira does not allow us looking after date with milliseconds. We have only minutes.
    # so, we have to exclude last reported issue from list.

    @$records = grep {
        my $zulu = $self->jiraDateToZulu($_->{modifiedOn});
        if ($_->{key} eq $issueKey && $zulu eq $_->{modifiedOn}) {
            0;
        }
        else {
            1;
        };
    } @$records;
    return $records;
}

sub getLastRecord {
    my ($self, $pluginObject) = @_;

    my $jiraProjectName = $pluginObject->getContext()->getRuntimeParameters()->{jiraProjectName};
    my $lastRecord = $pluginObject->getIssues($jiraProjectName, {
        asRecords    => 1,
        getLastIssue => 1
    });
    return $lastRecord->[0];
}

sub buildDataset {
    my ($self, $pluginObject, $records) = @_;

    my $dataset = $self->newDataset(['feature']);
    for my $row (@$records) {
        # now, data is a pointer, you need to populate it by yourself using it's methods.
        my $data = $dataset->newData({
            reportObjectType => 'feature',
        });
        for my $k (keys %$row) {
            next unless defined $row->{$k};
            if ($k eq 'modifiedOn' || $k eq 'createdOn' || $k eq 'resolvedOn') {
                $row->{$k} = $self->jiraDateToZulu($row->{$k});
            }
            if ($k eq 'status') {
                if ($row->{$k} eq 'To Do') {
                    $row->{$k} = 'Open';
                }
                elsif ($row->{$k} eq 'Done') {
                    $row->{$k} = 'Resolved';
                }
            }
            if ($k eq 'resolution') {
                if ($row->{$k} eq 'Done') {
                    $row->{$k} = 'Fixed';
                }
            }
            $data->{values}->{$k} = $row->{$k};
        }
    }
    return $dataset;
}


1;
