# Levi9 functional-tests 

## Used Technologies

- [Java](https://www.java.com/)
- [JUnit 5](https://junit.org/junit5/)
- [AssertJ](http://joel-costigliola.github.io/assertj/)
- [Cucumber](https://docs.cucumber.io/)
- [Project Lombok](https://projectlombok.org/)
- [REST-Assured](https://github.com/rest-assured/rest-assured/wiki/Usage)
- [Awaitility](https://github.com/awaitility/awaitility/wiki/Usage)
- [Selenium](https://www.seleniumhq.org/)
- [Logback](https://logback.qos.ch/)

## Project usage

Project is executed by running command in project folder:
```console 
mvn clean verify
```
Additional parameters:

- `-Denv` - environment on which to execute tests i.e ```-Denv=test```, if not specified `development` will be used.
- `-Dtags` - scenario tags to execute, can be one or multiple (separated with 'or' ,) i.e. ```-Dtags=@sanity``` or ```-Dtags='@api or @ui'```, if not
  specified `@sanity` will be used.
- `-Dremote` - defines if execution is done locally or remotely i.e. ```-Dremote=false``` execute using local browsers and drivers
  or ```-Dremote=true``` execute remotely using Selenium Grid (URL of Selenium Grid per environment is located in environment properties files).
- `-Dbrowser` - browser on which UI test will be used i.e. ```-Dbrowser=firefox```, if not specified `chrome` will be used
- `-DparallelCount` - maximum number of scenarios executed in parallel i.e. ```-DparallelCount=5```, if not specified `3` will be used.

For example to execute @api tests with 2 features in parallel one development environment with chrome browser and not using Selenium Grid command will
look like:
```console 
mvn clean verify -Dtags='@ui or @api' -DparallelCount=5 -Denv=development -Dbrowser=chrome -Dremote=false
```

## Local Development Setup

### Download and install

- [Java 11+](http://www.oracle.com/technetwork/java/javase/downloads/index.html) and installed as
  described [here](https://docs.oracle.com/en/java/javase/13/install/overview-jdk-installation.html)
- [Maven 3.8+](https://maven.apache.org/download.cgi) and installed as described [here](https://maven.apache.org/install.html)
- IDE of choice, [IntelliJ IDEA](https://www.jetbrains.com/idea/download) or [Eclipse](https://www.eclipse.org/downloads/)
- [Lombok](https://projectlombok.org/download) and configured on chosen IDE, [IntelliJ IDEA](https://projectlombok.org/setup/intellij) or [Eclipse](https://projectlombok.org/setup/eclipse)
- Cucumber plug-ins for chosen IDE, [IntelliJ IDEA Cucumber for Java plug-in](https://plugins.jetbrains.com/plugin/7212-cucumber-for-java) or [Cucumber Eclipse plug-in](https://cucumber.github.io/cucumber-eclipse/)
	- More information about Cucumber Plug-ins usage
		- [IntelliJ IDEA Cucumber for Java plug-in](https://www.jetbrains.com/help/idea/cucumber-support.html)
		- [Cucumber Eclipse plug-in](https://github.com/cucumber/cucumber-eclipse/blob/master/README.md)
- [IntelliJ IDEA Save Actions plug-in](https://plugins.jetbrains.com/plugin/7642-save-actions) to apply code formatting on save action (this is not needed for Eclipse as it comes built-in)
- SonarLint plug-in for chosen IDE [IntelliJ IDEA SonarLint plug-in](https://plugins.jetbrains.com/plugin/7973-sonarlint) or [Eclipse SonarLint plug-in](https://marketplace.eclipse.org/content/sonarlint)

### Maven Settings 

		<?xml version="1.0" encoding="UTF-8"?>
		<settings xmlns="http://maven.apache.org/SETTINGS/1.0.0"
                  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                  xsi:schemaLocation="http://maven.apache.org/SETTINGS/1.0.0 http://maven.apache.org/xsd/settings-1.0.0.xsd">

			    <profiles>
                    <profile>
                        <id>alwaysActiveProfile</id>
                        <repositories>
                            <repository>
                                <id>spring-milestones</id>
                                <name>Spring Milestones</name>
                                <url>http://repo.spring.io/milestone</url>
                                <snapshots>
                                    <enabled>false</enabled>
                                </snapshots>
                            </repository>
                        </repositories>
                    </profile>
                </profiles>
		    <activeProfiles>
                <activeProfile>alwaysActiveProfile</activeProfile>
            </activeProfiles>
        </settings>

## Codding standards and rules

### Coding Standards

- To have all coding standards and formatting just import settings file into chosen IDE
- IntelliJ
	- Go to `File > Settings > Editor > Code Style` and import code formatter `codestyle/intellij/Code Style.xml`
	- Go to `File > Settings > Editor > File and Code Templates` select `Includes` tab and configure it as displayed on `codestyle/intellij/File and Code Templates - Includes - File Header example.PNG` and replace author data with your name and email
	- Go to `File > Settings > Other Settings > Save Actions` and configure it as displayed on `codestyle/intellij/Save Actions plugin.PNG`
- Eclipse
	- Go to `Window > Preferences > Java > Code Style > Formatter` and import `codestyle/eclipse/formatter.xml`
	- Go to `Window > Preferences > Java > Code Style > Code Templates` and import `codestyle/eclipse/codetemplates.xml`
		- After import is done expand `Comments > Types` and replace author data with your name and email
	- Go to `Window > Preferences > Java > Code Style > Cleanup` and import `codestyle/eclipse/cleanup.xml`
	- Go to `Window > Preferences > Java > Code Style > Organize Imports` and import `codestyle/eclipse/importorder.importorder`
	
### Coding Rules

- Always apply code formatting before committing code
- Always fix all Sonar issues stated in Sonar analysis of IDE before committing code
- All Java Classes must have author data
- String place holder in log messages and exceptions is `{}` where in assert messages is `%s`
 Test data is passed between tests using `Storage`
	- `Storage` is comprised of lists with domain entity objects (Pets, Orders, etc.)
	- When creating new entity object make sure that it represent functional domain entity
	- When creating new entity object use only builders as way to create those objects, not constructors 
	- Extending existing entities is encouraged when additional data for them is needed but it must make sure that new fields are updated accordingly in steps
	- When working with Storage, always if otherwise not needed, use latest stored values for entity you need
- When testing some asynchronous operation test must wait for some condition to be fulfilled, **NEVER** for some predefined time.
	- To implement conditional waits Awaitility is used in one of its formats.
		- If asynchronous wait is not part of some assert, plane Java 8 implementation with lambda should be used, more on that is available [here](https://github.com/awaitility/awaitility/wiki/Usage#example-7---java-8)
		- If asynchronous wait is for some assert, AssertJ implementation of Awaitility should be used, more on that is available [here](https://github.com/awaitility/awaitility/wiki/Usage#example-8---using-assertj-or-fest-assert)
- When adding new application properties value *ALWAYS* make sure that it is added for all environment application properties

### Developing new tests

General rules when creating test data for some test in order of priority:

1. Create data over REST API calls
2. Create data over Database Queries
3. Create data over UI interface

Always first tend to create test data with REST API calls, only if that way is not possible than try other two ways in order mentioned above.

When developing new automated test have in mind next order:

1. Create REST API test
2. Create UI tests

Always make sure that test is developed in most optimized way for fast and reliable execution, meaning that only if not possible to develop test as REST API call than develop it as UI test, which are slower and much more prone to errors.

### REST API Interface

To add new Rest API calls to App back end next steps must be followed:

1. Create Proxy class for that domain of App in `rest/proxy/{servicename}` where all REST calls to that endpoint will be located
2. Data Source (Transfer) Objects must be created in `rest/data` package
3. Call created proxy class from step definitions

To add new communication interface with any of micro-services next steps must be followed:

1. Add Micro-Service API dependency in `pom.xml` with parametrized version
2. Create new RESTS Client class in `rest/client` package for that micro-service, by extending BaseRestClient
3. Add micro-service URL to all `application-[environment].properties` files
4. Create Proxy Class in `rest/proxy/{servicename}` package to Communicate with service
	- All direct communication with Micro-Service should be done in Proxy classes which are called from step definitions
5. Adding Data Source (Transfer) Objects is not needed in case communication is done directly with Micro-Service as all existing ones can be used from included API
6. Call created proxy class from step definitions

### UI Interface

To add new UI interface, Page Object pattern is used, meaning that for each application page Page Object class which represents it must exists in Test Automation framework.
To automate actions on some new page:

1. Create Page Object(s) for it in `com.levi9.functionaltests.ui.page` (it can also be multiple page objects if page is complex)
	- Order of priority for Selenium selectors is:
		1. ID locator
		2. CSS locator
		3. Name locator
		4. Link locator
		5. XPath locator
2. Call its methods from step definitions

## Gherkin standards and rules

### Describing Features

Every feature must only contain scenarios related to that it. When grouping scenarios under one feature make sure that `@Background` for that feature is common for all of scenarios.
If some feature is complex and there are different `@Background` for group them in multiple feature file.

If you have problems describing feature you can use next template, known as a Feature Injection template:


	In order to <meet some goal>
	As a <type of stakeholder>
	I want <a feature>
	

By starting with the goal or value that the feature provides, you’re making it explicit to everyone who ever works on this feature why they’re giving up their precious time. You’re also offering people an opportunity to think about other ways that the goal could be met.	
	
### Writing Scenarios

Using Given-When-Then in sequence is a great reminder for several great test design ideas. It suggests that pre-conditions and post-conditions need to be identified and separated. It suggests that the purpose of the test should be clearly communicated, and that each scenario should check one and only one thing. When there is only one action under test, people are forced to look beyond the mechanics of test execution and really identify a clear purpose.

When used correctly, Given-When-Then helps teams design specifications and checks that are easy to understand and maintain. As tests will be focused on one particular action, they will be less brittle and easier to diagnose and troubleshoot. When the parameters and expectations are clearly separated, it’s easier to evaluate if we need to add more examples, and discover missing cases.

#### General Rules

To prevents most of accidental misuse of Given-When-Then use:

- Write *Given* in Past tense as Passive sentences - these statements are describing preconditions and parameters (values rather than actions)
- Write *When* in Present tense as Active sentences - these statements are describing action under test
- Write *Then* in Future tense as Passive sentences - these statements are describing post-conditions and expectations (values rather than actions)

Make sure that there is only **one** *When* statement for each scenario.

Also make sure that there are no **and** conjunctions in sentences. If there is, it must be split into separate step.

### Matching Step Definition with Regular Expressions

- To match Gherkin Scenario Step text regular expression are used
- When writing regular expression matchers always make sure that at least similar words and plurals are covered and will be matched
	- Tool which can help you write and match regular expression [Regexr](https://regexr.com/)

## Maintenance

### Local Selenium Driver Files

New Selenium Drivers and Browser version are being released more often than any other dependencies. Whenever new browser version is released there is
high possibility that previous Driver version will not work with it, causing that UI test will not work anymore.

As Local Selenium Drivers are stored inside project it is **important** that they are always kept up-to-date with current Browser version. They are
located in ```src/main/resources/drivers``` folder. _Recommendation when updating drivers is to update them for all OS variants present in project,
just to be consistent._