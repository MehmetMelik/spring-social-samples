Twitter Secretary
====================================
This app brings new features to your twitter experience:
* Find out who unfollowed you
* Send twits in a future time.

Step 1: Register your application
---------------------------------
Before you can run the application, you'll need to obtain application credentials from Twitter by registering the application with each Twitter:

 * Twitter: https://apps.twitter.com/

Be sure to read Twitter's usage policies.

Step 2: Edit application.yaml
-----------------------------------
Once you have registered the application, you'll need to edit src/main/resources/application.yaml, adding the credentials to the appropriate properties.

Step 3: Run the application
---------------------------

### Development

Create and run a docker container for mysql:
```
docker run --name status-mysql -p 3306:3306 -e MYSQL_ROOT_PASSWORD=pa55word -d mysql:latest
```

If you have created mysql docker container previously, and want to start it:
```
docker start status-mysql
```

If you have any issue with flyway, install flyway via homebrew:
```
brew install flyway
```
and run 
```
flyway clean
```

### IDE Setup

1. Install IntelliJ "Lombok Plugin": Search for "Lombok Plugin" in Preferences/Plugins 
2. Enable Annotation Processing: Search for "Annotation Processing" in IntelliJ Preferences

### Building & Run

To run, simply import the project into your IDE and deploy to a Servlet 2.5 or > container such as Tomcat 6 or 7.
Access the project at http://localhost:8080/api

Alternatively, you can run the application using Gradle. To make it easier to build the project with Gradle, the Gradle wrapper has been included. The Gradle wrapper makes it possible to run Gradle without having to explicitly install Gradle to your system.

To run the application with Gradle:

```sh
$ gradlew bootRun
```

Or you can build the application with Gradle, then run the resulting WAR file as an executable JAR:

```sh
$ gradlew build
...
$ java -jar build/libs/spring-social-showcase.war
```

When running the application from the command line, you can access it at http://localhost:8080/api from your browser.
