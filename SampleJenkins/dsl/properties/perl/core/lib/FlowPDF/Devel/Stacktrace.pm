package FlowPDF::Devel::Stacktrace;
use strict;
use warnings;
use Data::Dumper;

my $TEMPLATES = {
    'package'    => 0,
    'filename'   => 1,
    'line'       => 2,
    'subroutine' => 3,
    'hasargs'    => 4
};
#                                 ($frame->[0]) > $frame->[1]:L$frame->[2]:$frame->[3]
our $STACKTRACE_FRAME_TEMPLATE = '({{package}}) :: {{filename}}:L{{line}}:{{subroutine}}';
sub new {
    my ($class) = @_;

    my $st = [];
    my $i = 0;

    while (1) {
        my @caller = caller($i);
        last unless @caller;
        unshift @$st, \@caller;
        if ($i >= 100) {
            last;
        }
        $i++
    }

    # removing last stack element if it is this function.
    my $function = __PACKAGE__ . '::new';
    if ($st->[-1]->[3] eq $function) {
        pop @$st;
    }
    bless $st, $class;
    return $st;
}


sub clone {
    my ($self) = @_;

    my @t = @$self;

    my $rv = \@t;
    bless $rv, ref $self;
    return $rv;
}

sub toString {
    my ($self) = @_;

    my $rv = '';
    my $shift = 0;
    for my $frame (@$self) {
        my $t = $self->interpolateTemplate($STACKTRACE_FRAME_TEMPLATE, $frame);
        $rv .= ' ' x $shift . "at $t\n";
        $shift++;
    }

    $rv = $rv if ($rv);
    $rv =~ s/^\s+//gs;
    return $rv;
}


sub interpolateTemplate {
    my ($self, $line, $frame) = @_;

    for my $k (keys %$TEMPLATES) {
        $line =~ s/\{\{$k\}\}/$frame->[$TEMPLATES->{$k}]/gs;
    }

    return $line;
}
1;
