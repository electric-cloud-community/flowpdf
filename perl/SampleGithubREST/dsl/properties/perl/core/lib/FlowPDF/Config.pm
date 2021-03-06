# WARNING
# Do not edit this file manually. Your changes will be overwritten with next FlowPDF update.
# WARNING

package FlowPDF::Config;
use ElectricCommander;

use base qw/FlowPDF::StepParameters/;
use strict;
use warnings;


1;


=head1 NAME

FlowPDF::Config

=head1 AUTHOR

CloudBees

=head1 DESCRIPTION

This class represents values of current configuration (global values) that is available in current run context by the name that is provided in procedure config form.

=head1 SYNOPSIS

To get an FlowPDF::Config object you need to use getConfigValues() method from L<FlowPDF::Context>.

=head1 METHODS

=head2 isParameterExists()

=head3 Description

Returns true if parameter exists in the current configuration.

=head3 Parameters

=over 4

=item None

=back

=head3 Returns

=over 4

=item (Boolean) True if parameter exists.

=back

=head3 Usage

%%%LANG=perl%%%

    if ($configValues->isParameterExists('endpoint')) {
        ...;
    }

%%%LANG%%%

=head2 getParameter($parameterName)

=head3 Description

Returns an L<FlowPDF::Parameter> object or L<FlowPDF::Credential> object.

=head3 Parameter

=over 4

=item (String) Name of parameter to get.

=back

=head3 Returns

=over 4

=item (L<FlowPDF::Parameter>|L<FlowPDF::Credential>) Parameter or credential by it's name

=back

=head3 Usage

To get parameter object:

%%%LANG=perl%%%

    my $query = $configValues->getParameter('query');

%%%LANG%%%

If your parameter is an L<FlowPDF::Parameter> object, you can get its value either by getValue() method, or using string context:

%%%LANG=perl%%%

    print "Query:", $query->getValue();

%%%LANG%%%

Or:

%%%LANG=perl%%%

    print "Query: $query"

%%%LANG%%%

If your parameter is L<FlowPDF::Credential>, follow its own documentation.

=head2 getRequiredParameter($parameterName)

=head3 Description

Returns an L<FlowPDF::Parameter> object or L<FlowPDF::Credential> object if this parameter exists.

If parameter does not exist, this method aborts execution with exit code 1.

This exception can't be catched.

=head3 Parameter

=over 4

=item (String) Name of parameter to get.

=back

=head3 Returns

=over 4

=item (L<FlowPDF::Parameter>|L<FlowPDF::Credential>) Parameter or credential by it's name

=back

=head3 Usage

To get parameter object:

%%%LANG=perl%%%

    my $query = $configValues->getRequiredParameter('endpoint');

%%%LANG%%%

If your parameter is an L<FlowPDF::Parameter> object, you can get its value either by getValue() method, or using string context:

%%%LANG=perl%%%

    print "Query:", $query->getValue();

%%%LANG%%%

Or:

%%%LANG=perl%%%

    print "Query: $query"

%%%LANG%%%

If your parameter is L<FlowPDF::Credential>, follow its own documentation.


=cut
