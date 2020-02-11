# Eclipse Text Annotation Plugin

[![SonarCloud](https://sonarcloud.io/images/project_badges/sonarcloud-white.svg)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Build Status](https://travis-ci.com/lukasbach/textannotation.svg?branch=master)](https://travis-ci.com/lukasbach/textannotation)

[![Coverage](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=coverage)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Lines of Code](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=ncloc)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Bugs](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=bugs)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Code Smells](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=code_smells)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Vulnerabilities](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=vulnerabilities)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)
[![Duplicated Lines (%)](https://sonarcloud.io/api/project_badges/measure?project=lukasbach_textannotation&metric=duplicated_lines_density)](https://sonarcloud.io/dashboard?id=lukasbach_textannotation)

![Plugin Screenshot](screenshot.png)

## Quick Setup

For now the plugin has to be installed manually. Download the latest ``edu.kit.textannotation.annotationplugin-1.0.0-SNAPSHOT.jar`` file from the most recent [release](https://github.com/lukasbach/textannotation/releases) and place it in ``%eclipse-install-dir%/dropins/plugins``. The ``plugins`` subdirectory might not exist yet and has to be created in this case.

Then start eclipse with the ``-clean`` option for it to load the plugin.

## Examples

The repository comes with an example Eclipse workspace to explore the functions of the plugin. Clone the repository with ``git clone https://github.com/lukasbach/textannotation && cd textannotation/example`` and open an Eclipse workspace at that location. You can then open the file ``annotation-example-project/travelManagement.taf`` in Eclipse with the plugin installed.

## Using the Annotation Perspective

The plugin adds a custom perspective that provides all necessary views for using the plugin. Click the perspective shortcut on the top right or navigate through the menu bar via ``Window > Perspective > Open Perspective`` and open the *Text Annotation Perspective*. This automatically opens the relevant views and adds shortcuts for creating new files.

## About
(TODO)
