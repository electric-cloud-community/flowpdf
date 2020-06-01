# Flow Plugin Development Framework (flowpdf)

## Overview
The Flow Plugin Development Framework (**flowpdf** in short) provides a combination of Tools, APIs and libraries which can be used to create a flow plugin in 4 steps. Refer to the Development Process section below.

The entire set of capabilities of the framework are made available through a single command line tool (CLI) called **flowpdk**.

A detailed **Plugin Developer Guide** is provided below for further reference.

## Development Process

![flowpdk-user-flow](https://user-images.githubusercontent.com/6411605/59960113-f55c5980-9477-11e9-89b4-f044b3c56843.png)

## Download flowpdk
**Note**:
- Once you download **flowpdk** all other tools/libraries can be downloaded using the CLI itself.
- **flowpdk** has an upgrade command which when invoked will download the latest version of other components required for your development envirnment. In order to know if you need to invoke the upgrade command, you could either refer to the Changelog section (see below) or the Release Notes section of the Plugin Developer Guide, both of which would maintain a cumulative list of all changes.
- The Download link below points to the latest version of **flowpdk**. In situations where **flowpdk** itself has changed and you are using an older version of **flowpdk** any upgrade to other flowpdf components using **flowpdk** will not work unless you upgrade to the latest version of **flowpdk**.

[Download flowpdk](https://storage.googleapis.com/flowpdf-binaries/flowpdf-cli.zip)

Latest Version: 2.2.0.3

Release Date: 4/28/2020

## Plugin Developer Guide

  [HTML Documentation](https://storage.googleapis.com/flowpdf-binaries/doc/latest/index.html)

  [PDF Documentation](https://storage.googleapis.com/flowpdf-binaries/doc/latest/PluginDeveloperGuide.pdf)

## Changelog

Refer to [Release Notes Section](https://storage.googleapis.com/flowpdf-binaries/doc/latest/releasenotes.html) of the Plugin Developer Guide.

## Sample Plugins built with flowpdf

### Perl

1. Refer to [SampleJIRA Plugin](perl/SampleJIRA/README.md) for a plugin that uses Rest API to integrate with JIRA.

2. Refer to [SampleGlassfish Plugin](perl/SampleGlassfish/README.md) for a plugin that uses CLI to integrate with Glassfish Application Server.

3. Refer to [SampleGithubREST Plugin](perl/SampleGithubREST/README.md) for a plugin that uses REST generator to integrate with Github.

4. Refer to [SampleReporting Plugin](perl/SampleReporting/README.md) for a plugin that uses Reporting Framework to integrate with DOIS.

### Groovy

1. Refer to [Sample JIRA Plugin](groovy/SampleJira/README.md) for a plugin that uses REST API to integrate with JIRA.
2. Refer to [Sample Gradle Plugin](grooovy/SampleGradle/README.md) for a plugin that uses CLI to integrate with Gradle.
3. Refer to [Sample Github REST Plugin](groovy/SampleGithubREST) for a plugin that uses REST Generator to integrate with Github.
4. Refer to [Sample JIRA Reporting Plugin](groovy/SampleJIRAReporting) for a plugin that uses Reporting Framework to integrate with DOIS.


## Community Plugins Built with FlowPDF

* [GCP Secret Manager Plugin](https://github.com/electric-cloud-community/EC-GCP-SecretManager)
* [GCP Storage Plugin](https://github.com/electric-cloud-community/EC-GCP-Storage)
* [AWS CloudFront Plugin](https://github.com/electric-cloud-community/EC-AWS-CloudFront)
