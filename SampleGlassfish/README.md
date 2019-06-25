## EC-Glassfish

The plugin demonstrates CLI-based integration using [Oracle Glassfish Application Server](
https://javaee.github.io/glassfish/) as an example.

The plugin contains two procedures to deploy and undeploy a sample application.

* [Plugin Specification](config/pluginspec.yaml)
* [Plugin Code](dsl/properties/perl/lib/FlowPlugin/SampleGlassfishPlugin.pm)

To create an equivalent plugin, copy the plugin specification into the config/ folder of your workspace and run `flowpdk generate plugin`.

Then add the following code into the `dsl/properties/perl/lib/FlowPlugin/SampleGlassfishPlugin.pm` file:

```perl

sub processRes {
    my ($self, $res) = @_;

    print "STDOUT: " . $res->getStdout();
    print "STDERR: " . $res->getStderr();
    my $code = $res->getCode();
    my $stepResult = $self->newContext()->newStepResult();
    if ($code != 0) {
        $stepResult->setJobStepOutcome('error');
        $stepResult->setJobStepSummary($res->getStderr());
    }
    else {
        $stepResult->setJobStepSummary($res->getStdout());
    }
    $stepResult->apply();
}


sub authCommand {
    my ($self) = @_;

    my $context = $self->newContext();
    my $configValues = $context->getConfigValues();
    # Step 1 and 2. Loading component and creating CLI executor with working directory of current workspace.
    my $cli = FlowPDF::ComponentManager->loadComponent('FlowPDF::Component::CLI', {
      workingDirectory => $ENV{COMMANDER_WORKSPACE}
    });
    my $cliPath = $configValues->getParameter('cliPath');
    my $command = $cli->newCommand($cliPath);
    my $cred = $configValues->getParameter('credential');
    if ($cred) {
        my $username = $cred->getUserName();
        my $password = $cred->getSecretValue();
        my ($fh, $filename) = tempfile();
        print $fh "AS_ADMIN_PASSWORD=$password";
        close $fh;
        $command->addArguments('--user');
        $command->addArguments($username);
        $command->addArguments('--passwordfile');
        $command->addArguments($filename);
    }

    return ($cli, $command);
}

sub deploy {
    my ($self) = @_;

    my $context = $self->newContext();
    my $params = $context->getStepParameters();

    my $appPath = $params->getParameter('applicationPath');
    my ($cli, $command) = $self->authCommand();
    $command->addArguments("deploy", $appPath);
    print "Command to run: ". $command->renderCommand() . "\n";

    # Step 4. Executing a command
    my $res = $cli->runCommand($command);
    $self->processRes($res);
}

sub undeploy {
    my ($self) = @_;

    my $context = $self->newContext();
    my $params = $context->getStepParameters();

    my $appName = $params->getParameter('applicationName');
    my ($cli, $command) = $self->authCommand();
    $command->addArguments("undeploy", $appName);
    print "Command to run: ". $command->renderCommand() . "\n";

    my $res = $cli->runCommand($command);
    $self->processRes($res);
}

```

And load two additional libraries:

```perl
use FlowPDF::Log;
use File::Temp qw(tempfile);
```

Then build the plugin using `flowpdk build` command.
