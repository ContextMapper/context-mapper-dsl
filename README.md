[![Build Status](https://travis-ci.com/stefan-ka/context-mapping-dsl.svg?token=jy7qsL9QVYqmygVYPcq3&branch=master)](https://travis-ci.com/stefan-ka/context-mapping-dsl)

# ContextMapper DSL
ContextMapper is an open source tool providing a Domain-specific Language based on Domain-Driven Design (DDD) patterns for context mapping and service decomposition. 

With the ContextMapper DSL language you can express DDD context maps. Once you have modeled your system with the language you can use the provided generators to create UML diagrams or calculate proposals for service decomposition.

## System Requirements
To use the ContextMapper DSL you need the following tools:

* [Java JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) (JDK 8 or newer)
* [Eclipse](https://www.eclipse.org/downloads/packages/)
* ContextMapper Eclipse Plugin

## Development
If you want to contribute and setup the IDE on your local system follow the following instructions. [Here](./wiki/Development) you get the instructions in more details.

### Building the Project
The project uses the [Maven Wrapper](https://github.com/takari/maven-wrapper). 

After cloning the project it can be built with the following command within the root directory of the project:

`./mvnw clean install`

**Note:** This is an Xtext project and the tests which are evaluating the correctness of the language grammar need an additional maven goal besides _test_ to be executed. Use this command if you want to execute the tests:

`./mvnw clean integration-test`

Of course they are also executed with a _clean install_.

### Setup Eclipse IDE
Since this is an Xtext project you need an Eclipse IDE to work on it. Download the **Eclipse IDE for Java and DSL Developers** from [here](https://www.eclipse.org/downloads/packages/). The latest version of eclipse we worked with is [2018-09](https://www.eclipse.org/downloads/packages/release/2018-09/r/eclipse-ide-java-and-dsl-developers).

#### Additional requirements
Before importing the project you need to install the **Tycho Configurator** m2e connector. This can be done by following these steps:

1. Start your Eclipse
2. Open the maven preferences page: _Window -> Preferences -> Maven_
3. Under _Discovery_ press the button _Open Catalog_ and search for _Tycho_. You should find the _Tycho Configurator_.
4. Select the Configurator and press _Finish_.
5. An eclipse installation wizard will appear. Go through the wizard to finish the installation and restart eclipse.


#### Importing the project
After having installed the Tycho Configurator you can import the project with as an existing maven project (_Import -> Existing Maven Project_).

#### Build in Eclipse
After importing the project you will have lots of build errors. This is because you have to generate some Xtext sources first. There exists an eclipse launch configuration called 'GenerateContextMappingDSLInfrastructure' delivered with the project. Run this launch configuration (yes, even if there exist build errors). As soon this has completed, execute a clean build with _Project -> Clean... -> Clean all projects_. 

That's it. You should have a clean workspace now without build errors.

  
