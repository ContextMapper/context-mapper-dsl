![Context Mapper](https://raw.githubusercontent.com/wiki/ContextMapper/context-mapper-dsl/logo/cm-logo-github-small.png) 
# Context Mapper DSL (CML) 
[![Build (master)](https://github.com/ContextMapper/context-mapper-dsl/workflows/Build%20(master)/badge.svg)](https://github.com/ContextMapper/context-mapper-dsl/actions) [![codecov](https://codecov.io/gh/ContextMapper/context-mapper-dsl/branch/master/graph/badge.svg)](https://codecov.io/gh/ContextMapper/context-mapper-dsl) [![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0) [![Maven Central](https://img.shields.io/maven-central/v/org.contextmapper/context-mapper-dsl.svg?label=Maven%20Central)](https://search.maven.org/search?q=g:%22org.contextmapper%22%20AND%20a:%22context-mapper-dsl%22)

ContextMapper is an open source tool providing a Domain-specific Language based on Domain-Driven Design (DDD) patterns for context mapping and service decomposition. 

With the ContextMapper DSL language you can express DDD context maps. Once you have modeled your system with the language you can use the provided generators to create UML diagrams, service contracts, and calculate proposals for service decomposition with [Service Cutter](https://github.com/ServiceCutter/ServiceCutter).

Checkout our website [https://contextmapper.org/](https://contextmapper.org/) to get started.
The Context Mapper project has originally been developed as part of research projects at [OST (former HSR)](https://www.ost.ch). You can find the project reports and further background information [here](https://contextmapper.org/background-and-publications/).

 * **Installation links:**
 Context Mapper is available for Visual Studio Code and Eclipse:
   * VS Code:
     * [VS Code Marketplace](https://marketplace.visualstudio.com/items?itemName=contextmapper.context-mapper-vscode-extension)
   * Eclipse:
     * [Eclipse Marketplace](https://marketplace.eclipse.org/content/context-mapper)
     * Update Site: [https://contextmapper.org/eclipse-update-site/](https://contextmapper.org/eclipse-update-site/)
 * [Release notes for latest releases](https://contextmapper.org/allnews)

## Features
* ContextMapper DSL language support (CML files)
    * Write context maps with bounded contexts and their relationships (Strategic DDD)
    * Specify bounded contexts (Tactic DDD): Tactic DSL based on [Sculptor](https://github.com/sculptor/sculptor)
    * Find examples in our [examples repository](https://github.com/ContextMapper/context-mapper-examples)
    * Consult our [online documentation](https://contextmapper.org/docs/) to get detailed language documentation, manuals and how to get started.
* Use our [Architectural Refactorings (ARs)](https://contextmapper.org/docs/architectural-refactorings/) to evolve and improve your DDD context maps iteratively.
* Generate [graphical Context Maps](https://contextmapper.org/docs/context-map-generator/)
* Generate [PlantUML](http://plantuml.com/) component diagram from context map
* Generate [PlantUML](http://plantuml.com/) class diagram from bounded context and/or subdomain (tactic DDD)
* Generate [PlantUML](http://plantuml.com/) state diagrams for aggregate states
* Generate [PlantUML](http://plantuml.com/) use case diagrams
* Generate [MDSL](https://socadk.github.io/MDSL/) (micro-) service contracts out of DDD context maps
* Generate [generic text files](https://contextmapper.org/docs/generic-freemarker-generator/) using [Freemarker](https://freemarker.apache.org/) templates
* Generate [ServiceCutter](https://github.com/ServiceCutter/ServiceCutter) input files for service decomposition proposals
* Convert [ServiceCutter](https://github.com/ServiceCutter/ServiceCutter) output back into context map (CML)

## System Requirements
To use the ContextMapper DSL (CML) you need the following tools:

* [Java JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (JDK 11 or newer)
* [Eclipse](https://www.eclipse.org/downloads/packages/) or [Visual Studio Code](https://code.visualstudio.com/)
* If you want to use our [Context Map generator](https://contextmapper.org/docs/context-map-generator/) you need to have [Graphviz](https://www.graphviz.org/) installed on your system.
    * Ensure that the binaries are part of the _PATH_ environment variable and can be called from the terminal.
    * Especially on Windows this is not the case after the installation of [Graphviz](https://www.graphviz.org/). The default installation path is
      `C:\Program Files (x86)\GraphvizX.XX`, which means you have to add `C:\Program Files (x86)\GraphvizX.XX\bin` to your _PATH_ variable.
* You may want to install one of the following two plugins to display the plantUML diagrams directly in Eclipse:
    * [Asciidoctor Editor](https://marketplace.eclipse.org/content/asciidoctor-editor) (Update site: [https://dl.bintray.com/de-jcup/asciidoctoreditor](https://dl.bintray.com/de-jcup/asciidoctoreditor))
    * [PlantUML Eclipse Plugin](https://github.com/hallvard/plantuml) (Update site: [http://hallvard.github.io/plantuml/](http://hallvard.github.io/plantuml/))
    * **Note:** Both plugins require [Graphviz](http://www.graphviz.org/) to be installed on your machine!
    * Alternatively you can use the [plantUML online server](http://www.plantuml.com/plantuml/uml).

## Getting Started
If you want to use our Context Mapper tool, install the plugin in Eclipse or the extension in VS Code:
 1. Installation of Context Mapper:
     * [Eclipse Marketplace](https://marketplace.eclipse.org/content/context-mapper) 
     * [VS Code Marketplace](https://marketplace.visualstudio.com/items?itemName=contextmapper.context-mapper-vscode-extension)
 2. Find a detailed manual how to create your first CML project [here](https://contextmapper.org/docs/getting-started-create-project/).
 3. Examples which introduce the language (CML) capabilities can be found here: [https://github.com/ContextMapper/context-mapper-examples](https://github.com/ContextMapper/context-mapper-examples)
 4. Further instructions, user guides and documentation can be found on our website [https://contextmapper.org/](https://contextmapper.org/).

## Getting Started (Standalone Library Usage)
If you want to use the Context Mapper DSL (CML) language and the generator tools as standalone library within your Java application you can add the following dependency to your project.

**Gradle:**
```gradle
implementation 'org.contextmapper:context-mapper-dsl:6.9.0'
```

**Maven:**
```xml
<dependency>
  <groupId>org.contextmapper</groupId>
  <artifactId>context-mapper-dsl</artifactId>
  <version>6.9.0</version>
</dependency>
```
In our [context-mapper-standalone-example](https://github.com/ContextMapper/context-mapper-standalone-example) repository you find an example project showing how to include the library within your project. It further contains code examples illustrating how to use the CML models and the generator tools (PlantUML, MDSL, etc.).

## Development
If you want to contribute and setup the IDE for this project on your local system follow the following instructions. [Here](./wiki/Development) you get the instructions in more details.

### Building the Project
The project currently has two builds since we have to use Maven for building the Eclipse plugin but prefer Gradle in the standalone case.
With Gradle you can only build the DSL and the IDE (LSP) project. It is further used to deploy these standalone JARs to the Maven central.

#### Complete Build with Eclipse Plugin (Maven)
The project uses the [Maven Wrapper](https://github.com/takari/maven-wrapper). 

After cloning the project it can be built with the following command within the root directory of the project:

`./mvnw clean install`

**Note:** This is an Xtext project and the tests which are evaluating the correctness of the language grammar need an additional maven goal besides _test_ to be executed. Use this command if you want to execute the tests:

`./mvnw clean integration-test`

Of course they are also executed with a _clean install_.

#### Standalone Build Only (Gradle)
To build the standalone projects only, you can use the Gradle Wrapper:

`./gradlew clean build`

**Note:** If you want to deploy the libraries into your local Maven repository, you need a GPG key to sign the artifacts:

`./gradlew clean publishToMavenLocal -Psigning.keyId=<your-gpg-key-id> -Psigning.password=<gpg-passphrase> -Psigning.secretKeyRingFile=<path-to-gpg-keyring-file>` 

### Setup Eclipse IDE
Since this is an Xtext project you need an Eclipse IDE to work on it. Download the **Eclipse IDE for Java and DSL Developers** from [here](https://www.eclipse.org/downloads/packages/). The latest version of Eclipse we worked with is [2019-12](https://www.eclipse.org/downloads/packages/release/2019-12/r/eclipse-ide-java-and-dsl-developers).

#### Importing the project
You can import the project with as an existing maven project (_Import -> Existing Maven Project_).

#### Build in Eclipse
After importing the project you will have lots of build errors. This is because you have to generate some Xtext sources first. There exists an eclipse launch configuration called 'GenerateContextMappingDSLInfrastructure' delivered with the project. Run this launch configuration (yes, even if there exist build errors). As soon this has completed, execute a clean build with _Project -> Clean... -> Clean all projects_. 

That's it. You should have a clean workspace now without build errors.

## Contributing
Contribution is always welcome! Here are some ways how you can contribute:
 * Create Github issues if you find bugs or just want to give suggestions for improvements.
 * This is an open source project: if you want to code, [create pull requests](https://help.github.com/articles/creating-a-pull-request/) from [forks of this repository](https://help.github.com/articles/fork-a-repo/). Please refer to a Github issue if you contribute this way. In our [wiki](https://github.com/ContextMapper/context-mapper-dsl/wiki/IDE-Setup) you can find out how to build the project and setup the development environment locally.
 * If you want to contribute to our documentation and user guides on our website [https://contextmapper.org/](https://contextmapper.org/), create pull requests from forks of the corresponding page repo [https://github.com/ContextMapper/contextmapper.github.io](https://github.com/ContextMapper/contextmapper.github.io) or create issues [there](https://github.com/ContextMapper/contextmapper.github.io/issues).

## Licence
ContextMapper is released under the [Apache License, Version 2.0](http://www.apache.org/licenses/LICENSE-2.0).
