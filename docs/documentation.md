# TextAnnotation Eclipse-Plugin

The software development process contains many activities where various
documents are being worked with and where a structured approach to their
analysis becomes a relevant task, especially during early phases such as
requirements gathering and model specification.

In an effort to cope with these complications, this project deploys a
free-to-use tool for annotating textual documents within eclipse IDE
environments. The tool is developed as eclipse plugin and can be installed
to an existing eclipse installation.

This document is segmented into two parts. The
[first section](#User-Documentation)
documents how the application is installed and used and serves as a user
documentation. The
[second section](#Project-Documentation) documents the overall approach,
the pitfalls and takeaways experienced during the project's developemnt
as well as the internal project structure.

- [TextAnnotation Eclipse-Plugin](#textannotation-eclipse-plugin)
  - [User Documentation](#user-documentation)
    - [Installing the Plugin](#installing-the-plugin)
    - [Basic Concepts](#basic-concepts)
    - [Getting started with a First Annotation File](#getting-started-with-a-first-annotation-file)
    - [Views](#views)
    - [Advanced Usage](#advanced-usage)
    - [Overview of the Contributions of the Plugin](#overview-of-the-contributions-of-the-plugin)
  - [Project Documentation](#project-documentation)
    - [An Overview of the Project](#an-overview-of-the-project)
    - [Plugin Architecture](#plugin-architecture)
    - [XML Schemas](#xml-schemas)
    - [Quality Assurance and Continuous Integration](#quality-assurance-and-continuous-integration)
    - [Pitfalls and Takeaways Experienced during Development](#pitfalls-and-takeaways-experienced-during-development)
      - [Setting up the Project](#setting-up-the-project)
      - [Setting up Unit Tests](#setting-up-unit-tests)
      - [Generating Coverage Results](#generating-coverage-results)
      - [Generating changelogs](#generating-changelogs)
      - [CI Setup](#ci-setup)
      - [Specifying a custom Syntax Highlighter](#specifying-a-custom-syntax-highlighter)
      - [Developing with JetBrains IntelliJ](#developing-with-jetbrains-intellij)
    - [Contribution Guidelines](#contribution-guidelines)
    - [Conclusion](#conclusion)

## User Documentation

### Installing the Plugin

For now the plugin has to be installed manually. Download the latest
``edu.kit.textannotation.annotationplugin-1.0.0-SNAPSHOT.jar`` file from the
[most recent](https://github.com/lukasbach/textannotation/releases)
release and place it in ``%eclipse-install-dir%/dropins/plugins``. The plugins
subdirectory might not exist yet and has to be created in this case.

Then start eclipse with the ``-clean`` option for it to load the plugin.

### Basic Concepts

The plugin introduces two new files that are associated with text annotation:

- *Text Annotation Profile*: (``.tap``): A profile file contains information
  about the kinds of annotations that can be applied to textual documents.
  These kinds of annotations are called *Annotation Classes*, and are
  specified on a per-profile basis. An example for a profile in the context of
  requirements engineering would be an requirements profile which specifies
  annotation classes ``Stakeholder``, ``Goal``, and ``Functional Requirement``
  for annotating relevant concepts specified in a requirement specification.
- *Annotatable Text Files* (``.taf``): An annotatable text file contains
  textual content that is annotated using the annotation plugin. Such a file
  references an external annotation profile that is used within the file, thus
  a text file can only reference a specific profile while a profile can be
  referenced by arbitrarily many annotation text files. The annotations are
  stored within the annotatable text file.

As mentioned before, a profile mostly consists of Annotation Classes. Text
in annotatable text files is annotated using *Single Annotations*. Each
Single Annotation references a specific annotation class. Both an annotation
class and a single annotation can also be extended by custom meta data, which
is composed of a key-value dataset that is attached to either of them.
Additionally, a description can be specified for an annotation class, which
is shown when hovering over the annotation in the editor view.

### Getting started with a First Annotation File

After you have installed the plugin in eclipse, create a new
*Text Annotation Profile* by rightclicking on your current project,
selecting ``New > Other... > Text Annotation > Text Annotation Profile``.
An annotation profile will be required to create a new annotatable text
file. On the next step, you have to specify a file name, the directory
where the profile will be stored as well as a name for the profile.

After you have created your first profile, continue by creating your
first annotatable text file. Again, rightclick on your current project
and select ``New > Other... > Text Annotation > Annotatable Text File``.
Choose the profile that you have just created from the *Annotation
Profile* dropdown and click on *Finish*.

The file will now be created and opened automatically. Additionally,
the *Text Annotation Perspective* has opened, which provides a control
panel on the right side and a detail view on the bottom, which should be
empty by default.

You should start by specifying the annotation classes within your profile
that you anticipate to use. In the control view, you can see that your
newly created annotation profile is currently selected. Note that, changing
the profile here also changes the profile used in the text annotation file,
potentially invalidating annotations that are incompatible with the changed
profile.

Click on *Edit Profile*. You can add new Annotation classes with the
*Add Class* button on the right. Newly added classes can be edited by
selecting them from the list on the left and changing the values in the form
on the right. If you're finished with specifying annotation classes, click
*Save*.

You can now start writing text and annotating the document! Add some content
to your text file, select chunks of text and click the annotation class buttons
on the right to annotate the selection. When clicking on an annotation, it will
be selected in the detail view where the annotation can be deleted again.

### Views
[ ] Overview of all views contributed by the plugin

### Advanced Usage
[ ] Description of the detail view, and metadata on profile classes etc

### Overview of the Contributions of the Plugin

## Project Documentation

### An Overview of the Project

The project is set up as a Eclipse plugin project which is organized as an
Maven Tycho repository. The setup is based on a tutorial by Vogella, which can
be found [here][vogella-maven-tycho].

The directory structure is organized as follows:

- ``bundles``: Contains plugin projects
  - ``edu.kit.textannotation.annotationplugin``: Contains the actual plugin
    code
- ``features``: Contains feature projects
- ``releng``: Contains release configuration data
- ``example``: Contains an example text annotation project that can be used
  after installing the plugin to explore its features and see how the internal
  data structures look like.
- ``docs``: Contains the documentation that you are currently reading.
- ``tests``: Contains unit test cases for the plugin.

### Plugin Architecture

The plugin project defines the following packages, all of which are located
within the ``edu.kit.textannotation.annotationplugin`` parent package:

- ``editor``: This contains classes which are relevant for the Eclipse
  editor integration. Its containing class ``AnnotationTextEditor`` is the
  most relevant here, as this class specifies the actual editor logic. The
  other classes are additional data structures which are based on specific
  eclipse integrations.
- ``profile``: This package contains data structures regarding the profile,
  e.g. a class modelling an annotation profile itself as well as classes
  related to profiles. Notable is also the ``AnnotationProfileRegistry``-class,
  which is responsible for locating a profile file within the project given its
  name.
- ``textmodel``: Classes in this package are related to a specific annotatable
  text file rather than its used profile. This package also contains classes
  regarding the validation of source files as well as the subpackage
  ``xmlinterface``, which implements classes for parsing and serializing
  data from XML source files.
- ``views``: Classes which implement a specific UI view are placed in this
  package.
- ``wizards``: The profile creation wizard and the annotatable text file wizard
  are located here.
- ``utils``: Various utility methods.

The following figure illustrates the process that the plugin goes through
when opening a new annotatable text file:

![Annotation Process][annotationprocessimage]

### XML Schemas

### Quality Assurance and Continuous Integration

There exist many different approaches and frameworks for testing modern Java
applications. The typical stand-of-the-art method of leveraging unit test-cases
for testing internal data structures and algorithms was incorporated here as
well. However, as with many more specific projects, more sophisticated ways of
testing are required to thoroughly test all aspects of the developed
application.

Testing and verifying the integrity of applications that heavily rely on
graphical user interfaces is only possible through the realization of
end-to-end tests, which specify the exact user interactions that a customer or
user of the application would perform, such as clicking buttons or entering
input into forms, and then asserting on the generated state of the application.

In the context of Eclipse plugin development, this is almost always done by
employing the [SWT Bot Framework][swtbot]. Due to the scope of the project
and the time constraints, the actual implementation of end-to-end test
automation was not realized, but is definitely a relevant follow-up point for
future work on the project.

To improve the usability of testing and allow agile development methods on
the project, it was setup to implement a Continuous Integration configuration.
As described later in the
[section on pitfalls and takeaways](#pitfalls-and-takeaways-experienced-during-development),
initially a GitLab CI configuration was targeted, but the project ended up
employing [Travis CI][travisci] with its move to GitHub.

Travis CI offers many integration methods especially for GitHub features,
some of which are employed within this project. Additionally, a
[SonarCloud instance][sonarcloud-project] was setup to perform a static code
analysis and display the analysis results alongside the test results on a
consistent report page, which can be found [here][sonarcloud-project].

The relevant steps of the CI configuration follow:

- Generate a changelog file based on the version tag using
  [standard-version][standard-version].
- Build the project, run test cases and record coverage.
- Generate a XML coverage report using the [Jacoco CLI][jacoco-cli].
- Run the Sonar Scanner, uploading the analysis results and coverage reports to
  [SonarCloud][sonarcloud-project].
- If the CI task was triggered by a version tag, deploy the generated binaries
  to a new GitHub release and use the generated changelog file as release
  description.

Due to the integrative natur of Travis CI regarding GitHub, this setup
can increase productivity and software quality as test- and analysis-results
are displayed directly within GitHub and deployment happens quickly without
manual effort.

### Pitfalls and Takeaways Experienced during Development

The following sections give insights into the problems and pitfalls that
were encountered during development, and how they were solved. This can
additionally serve as reference for future plugin projects where similar
problems might occur, while at the same time documents the projects process.

#### Setting up the Project

As mentioned before, I started with following the
[tutorial by Vogella][vogella-maven-tycho] on creating plugin projects
using Maven Tycho. The first issue was a current bug in Maven which made the
usage of Tycho impossible with Maven 3.6.2. The issue is reported as
[MNG-6765][maven-bug] under the Maven Bug Tracker and was fixed in 3.6.3, which
was only released much later during the plugin's development phase. However,
the issue could be worked around by falling back to a later version of Maven.

Another issue was the fact that one of my development systems had its maven
repositories rerouted to a company internal artifactory instance where some
of the Tycho packages where not available, so I had to fall back to a different
development machine for running the Maven builds. Running the actual plugin
for development purposes still worked as it was not relying on the Tycho
packages.

#### Setting up Unit Tests

When setting up the unit tests for the project, again the aforementioned
[tutorial by Vogella][vogella-maven-tycho] was leveraged. For a quick overview
of how unit tests can be integrated into Tycho projects, the changeset of the
commit [06ff0f][git-commit-tests] can be looked into. The significant changes
include the creation of a new module within the Tycho monorepository. I
primarily struggled with the test code having access to both the JUnit test
framework as well as the internal data structures of the plugin. This was
solved with the following content for the ``MANIFEST.MF`` file:

    Manifest-Version: 1.0
    Bundle-ManifestVersion: 2
    Bundle-Name: Annotationplugin Tests
    Bundle-SymbolicName: edu.kit.textannotation.annotationplugin.tests
    Bundle-Version: 1.0.0.qualifier
    Fragment-Host: edu.kit.textannotation.annotationplugin;bundle-version="1.0.0"
    Automatic-Module-Name: edu.kit.textannotation.annotationplugin.tests
    Bundle-RequiredExecutionEnvironment: JavaSE-1.8
    Require-Bundle: org.junit;bundle-version="4.12.0"

The relevant lines here is the one starting with ``Fragment-Host``, which gives
the test code access to the actual referenced plugin code, and the line
starting with ``Require-Bundle``, which gives it access to the JUnit framework.

#### Generating Coverage Results

On the subject of unit test cases, another issue was the generating of
coverage reports that are available in a readable format. I wanted the test
pipeline to be able to generate coverage reports so that they could be included
in the SonarCloud analysis report.

By default, the Tycho setup as suggested by Vogella generates coverage reports
automatically without the need of additional effort. However, due to technical
problems it only generated the reports in the form of a ``jacoco.exec`` file,
a file format defined by the Jacoco coverage tool which does not include
source code information, so the report file itself does not yield actual
readable results.

The workaround I used was to use an additional Jacoco CLI tool to generate the
reusable XML coverage report files that SonarCloud understands from the
``exec``-file and the explicitly referenced source code directory.

The steps are illustrated by the section in the CI configuration which does
that on the fly:

    # Build, run tests and generate binary jacoco output
    - mvn clean install test verify

    # Download Jacoco CLI
    - curl [...]/org.jacoco.cli-0.8.5-nodeps.jar --output jacococli.jar
    
    # Generate jacoco XML reports from binary output
    - java -jar jacococli.jar report [...]/jacoco.exec --classfiles [...]/target/classes --html $HOME/coverage --xml $HOME/jacoco.xml

Again, by default the existing Jacoco integration should do that automatically.
I suspect that the problem was caused by the Jacoco setting of merging
coverage reports together after each package analysis (which is required so
that the analysis of the test code includes the coverage of the actual plugin
code) as disabling this setting yields the anticipated XML reports out of the
box, however then the coverage reports only show covered code in the test
package which invalidates the idea of test coverage reports.

#### Generating changelogs

#### CI Setup

The repository started out as a GitLab repository on the KIT SCC GitLab
instance. The typical approach for implementing CI in GitLab environments
is to use the included CI features of GitLab, however GitLab does not include
actual CI runners.

The initial idea was to leverage [bwCloud][bwcloud], a cloud computing service
maintained by several universities in Baden-Württemberg with the scope of
offering free-to-use cloud computing resources for academic purposes. As the
setup of a bwCloud server and its integration into the GitLab CI environment
turned out to be more complicated than thought, and with implementing CI deemed
not as relevant for the scope of the project, this idea was discarded.

Later, when the repository was moved to a GitHub repository, a reattempt on
leveraging CI was done by setting up a [Travis CI][travisci]-configuration.
Travis CI is a CI service specifically targeted at GitHub repositories, which
is free to use on open source projects and, contrary to GitLab CI, supplies
its own computing resources. It only requires a CI configuration file within
the repository and read access to the users repositories who is hosting
the analyzed repository.

Most issues that were encountered during implementing the CI configuration
were caused by other contexts and discussed in earlier sections, such as the
unit test setup, the generation of coverage reports or the generation of
changelogs.

The only problem encountered that can specifically be addressed with Travis
CI was the fact that the plugin test code requires physical displays to be
present when running (for example, even the creation of a ``Color`` object
needs a reference to a display), however the virtual machines that are running
Travis CI tasks do not have physical displays.

This issue was solved by adding the following lines in the Travis config:

    dist: xenial
    env: DISPLAY=:99.0
    services:
    - xvfb

``xenial`` specifies a Ubuntu image that is more compatible with the Tycho
environment, the ``DISPLAY`` environment variable is relevant for the unit
tests that rely on this variable and ``xvfb`` is a service which implements
a virtual framebuffer.

#### Specifying a custom Syntax Highlighter

A relevant part of the annotation plugin is the Presentation Reconciler,
which is responsible for coloring the text data in a IDE text editor,
thus essentially specifying a custom syntax highlighter.

I initially attempted to do this by extending the abstract class
``org.eclipse.jface.text.presentation.PresentationReconciler``, which
implements some base functionality by default, which helps most syntax
highlighting use cases.

My problem here was that the way I wanted to implemented syntax highlighting
was very different to typical use cases: The Text Annotation plugin should
not highlight text based on some token analysis or even any basis that takes
the actual text content into consideration, but instead highlight sections
in the text content which are specifically defined by the plugin. This lead
to weird bugs where manually triggering rehighlighting did not always properly
work and new sections of the text are not always highlighted.

The fairly easy solution was to directly implement the relevant interfaces
which Eclipse specifies for Presentation Reconcilers rather than extending a
utility class. These interfaces,
``IPresentationDamager`` and ``IPresentationRepairer`` in the
``org.eclipse.jface.text.presentation`` package, only specifies the following
methods which have to be implemented:

    IPresentationDamager & IPresentationRepairer {
      void setDocument(document);
      IRegion getDamageRegion(partition, documentEvent, documentPartitioningChanged);
      void createPresentation(presentation, damageRegion);
    }

``getDamageRegion`` is used to determine the section of the content that needs
to be rehighlighted in case changes to the text are made. As in my case
the text content does often not correlate with the actual highlighting data,
this method always just outputs the entire documents boundaries.

``createPresentation`` on the other hand then highlights the sections in
the document. This is usually a very complicated process as a token based
analysis comes into place here, but again as in the use case of the Text
Annotation plugin the text partitions to be highlighted were explicitly
given, this was a straight forward process.

#### Developing with JetBrains IntelliJ

Eclipse plugin projects are typically developed in the Eclipse IDE for obvious
reasons. In an effort to have the development process being available in
different IDE environments, I explored how Eclipse plugin development can be
employed within other IDEs, specifically JetBrains IntelliJ that I was used to
work with.

While the usage of Maven Tycho is highly advantageous for this case as Maven
is a IDE-independent platform that is well supported by IntelliJ, actually
running the plugin for testing and debugging purposes is much harder.

The solution that I found was mostly based
[on a StackOverflow question][intellij-so]
which is based on this use case. The interesting steps of the solution include
the specification of the local ``m2`` repository as dependencies in IntelliJ,
as well as manually running the Eclipse plugin task to see the command line
input that Eclipse uses, and copying that input to a custom IntelliJ run task.

The detail steps are described below:

- Open the Eclipse plugin directory in IntelliJ (not the root directory)
- Open Project Structure (``Ctrl``+``Alt``+``Shift``+``S``), add
  ``{eclipseinstallationroot}/plugins`` and S2 root
  (``{userdirectory}/.p2/pool/plugins``)
- Install the Bash Plugin in IntelliJ
- Run the Plugin in Eclipse, open the Debug View, rightclick on the entry,
  click on ``Properties``, copy the field value for "Command Line" and paste
  into a ``bat`` file. The file path may not contain spaces.
  Add ``cd {eclipseinstallationroot} &&`` to the script content.
- Create a new bash configuration with the script as target. Add
  "Build Project" as Before-Launch-Option.

The [StackOverflow thread][intellij-so] also describes how the debug task
can be embedded in IntelliJ.

### Contribution Guidelines
### Conclusion

[vogella-maven-tycho]: https://www.vogella.com/tutorials/EclipseTycho/article.html
[swtbot]: https://www.eclipse.org/swtbot/
[travisci]: https://travis-ci.com
[sonarcloud-project]: https://sonarcloud.io/dashboard?id=lukasbach_textannotation
[jacoco-cli]: https://www.jacoco.org/jacoco/trunk/doc/cli.html
[standard-version]: https://github.com/conventional-changelog/standard-version
[git-commit-tests]: https://github.com/lukasbach/textannotation/commit/06ff0fd52529eadfe9345d79d8a325a70632786d
[bwcloud]: https://www.bw-cloud.org/
[intellij-so]: https://stackoverflow.com/a/43195085/2692307
[annotationprocessimage]: ./images/annotationprocess.png
[maven-bug]: https://issues.apache.org/jira/browse/MNG-6765