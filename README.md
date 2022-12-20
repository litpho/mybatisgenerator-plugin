MyBatisgenerator Liquibase Gradle Plugin
-----------------------
A plugin for [Gradle](http://gradle.org) that allows you to use [MyBatisgenerator](https://mybatis.org/generator/) with 
[Liquibase](http://liquibase.org) to generate java code for the current state of your database migrations.

Usage
-----
TBD

There are 3 basic parts to using the Liquibase Gradle Plugin.  Including the plugin, setting up the
Liquibase runtime dependencies, and configuring the plugin.  Each step is described below.

#### 1. Including the plugin
To include the plugin into Gradle builds, simply add the following to your build.gradle file:

```groovy
plugins {
  id 'nl.litpho.mybatisgenerator' version '0.0.1'
}
```

#### 2. Setting up the classpath.
The plugin will need to be able to find Liquibase on the classpath when it runs a task, and
Liquibase will need to be able to find database drivers, changelog parsers, etc. in the classpath.
This is done by adding `liquibaseRuntime` dependencies to the `dependencies` block in the
`build.gradle` file.  At a minimum, you'll need to include Liquibase itself along with a database
driver.  Liquibase 4.4.0+ also requires the picocli library.
MyBatis generator only needs to be able to find the database driver and uses a separate `mybatisgeneratorRuntime`
configuration for that.

```groovy
dependencies {
   liquibaseRuntime 'com.h2database:h2:2.1.214'
   liquibaseRuntime 'info.picocli:picocli:4.7.0'
   liquibaseRuntime 'org.liquibase:liquibase-core:4.17.2'

   mybatisgeneratorRuntime 'com.h2database:h2:2.1.214'
}
```

The `dependencies` block will contain many other dependencies to build and run your project, but
those dependencies are not part of the classpath when liquibase or MyBatis generator runs, because they typically
only needs to be able to parse the change logs and connect to the database, and I didn't want to
clutter up the classpath with dependencies that weren't needed.

Using this plugin with Java 9+ and XML based change sets will need to add JAXB th classpath since
JAXB was removed from the core JVM.  This can be done by adding the following to your
`liquibaseRuntime` dependencies:

```groovy
  liquibaseRuntime 'javax.xml.bind:jaxb-api:2.3.1'
``` 

#### 3. Configuring the plugin

Parameters for Liquibase and MyBatis generator `mybatisgenerator` block inside the build.gradle
file. This block contains the following properties:
* configFile - the MyBatis generator config file
* database - database properties for use by both Liquibase and MyBatis generator
* directories - target directories for source generation
* liquibase - properties for liquibase such as the changelog location and the loglevel

```groovy
mybatisgenerator {
   configFile = file("$projectDir/src/main/mybatis/generatorConfig.xml")
   database {
      connectionUrl = "jdbc:h2:${projectDir}/build/db/h2"
      driverClass = "org.h2.Driver"
      username = "sa"
      password = ""
   }
   directories {
      java = file("$buildDir/generatedSources/src/main/java")
      resources = file("$buildDir/generatedSources/src/main/resources")
   }
   liquibase {
      changelogLocation = file("$projectDir/src/main/resources/db/changelog/changelog-master.xml")
      logLevel = nl.litpho.mybatisgenerator.LiquibaseLogLevel.FINE
   }
}
```
