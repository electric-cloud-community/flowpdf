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

[Download flowpdk](https://flowpdf-libraries.s3.amazonaws.com/flowpdf-cli.zip)

Latest Version: 1.0.1

Release Date: 7/31/2019

## Plugin Developer Guide

  [HTML Documentation](https://plugin-dev-guide.s3-us-west-1.amazonaws.com/latest/index.html)

  [PDF Documentation](https://plugin-dev-guide.s3-us-west-1.amazonaws.com/latest/PluginDeveloperGuide.pdf)

## Changelog

Refer to [Release Notes Section](https://plugin-dev-guide.s3-us-west-1.amazonaws.com/latest/releasenotes.html) of the Plugin Developer Guide. 

## Sample Plugins built with flowpdf

1. Refer to [SampleJIRA Plugin](SampleJIRA/README.md) for a plugin that uses Rest API to integrate with JIRA.

2. Refer to [SampleGlassfish Plugin](SampleGlassfish/README.md) for a plugin that uses CLI to integrate with Glassfish Application Server.
