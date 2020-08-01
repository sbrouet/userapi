# Project description 
**NOT FOR PRODUCTION** The UserApiApplication is a demo of a usable REST API for managing users.

It allows :
- the CRUD operations on users :
  - Create via Http POST method
  - Read via Http GET method
  - (full) Update via the Http PUST method
  - Delete via the Http DELETE method
- It also allows :
  - updating partially a user via the Http PATCH method
  - finding users with filters : by first name and/or email

Users are stored in an in-memory relational database (H2).
Upon each event that impacts users, such as creation / full or partial update / deletion, a message is sent to a message bus. The message contains a timestamp, the ```id``` of impacted user and the nature of the operation (USER_CREATED, USER_DELETED, USER_UPDATED)

# Important notice
All sample URLs and commands in this documentation and the sample commands file assume that the application is running on default port 8080.
In case you use a different port, you will have to adapt the port number accordingly in URLs and commands


# Prerequisites

## Prerequisites for running the application
In order for the UserApiApplication to be working you need to run the following components in that order (technical details follow)
- running a RabbitMQ instance
- running the UserApiApplication

## Technical prerequisites
In order to build and run this project, you need :
  - a Java SE Development Kit (JDK) with at least version 14
  - a working Maven installation : the ```mvn``` command is available on the command line, it uses the JDK version 8+ and your settings allow Maven to download artifacts from the Maven Central Repository
  - a working Docker installation being able to run the RabbitMQ docker image (details below)
  - the ```curl``` command is available on the command line

### Running RabbitMQ docker image
The **UserApiApplication assumes that RabbitMQ run on localhost and is accessible on default port (5672).**
    
If you do not have an already running RabbitMQ instance on the localhost, you can follow these steps to run one using a Docker image.
The RabbitMQ docker image documentation is available at:
    (https://hub.docker.com/_/rabbitmq)

Run the following command from your terminal
 
**```docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management```**

**Output should look like :**
```
[...]
2020-07-08 08:26:59.791 [info] <0.268.0>
 Starting RabbitMQ 3.8.5 on Erlang 23.0.2
[...]
2020-07-08 07:55:29.285 [info] <0.684.0> Server startup complete; 3 plugins started.
```

The **management ui** should be accessible at
  ```http://localhost:15672/#/channels```

**Later when the UserApiApplication is started**, the container logs should display messages such as:
```
2020-07-07 14:38:30.983 [info] <0.5487.0> accepting AMQP connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672)
2020-07-07 14:38:31.068 [info] <0.5487.0> Connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672) has a client-provided name: rabbitConnectionFactory#2c3f43d1:0
2020-07-07 14:38:31.082 [info] <0.5487.0> connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672 - rabbitConnectionFactory#2c3f43d1:0): user 'guest' authenticated and granted access to vhost '/'
```

# Building the project
On the command line, run the following command:

```mvn clean install```

The project should be built successfully and unit tests run automatically and be successful too


# Running the application
## Recommended way : via the Maven plugin
This the **prefered way for demo purpose only**

The spring-boot maven plugin allows running Spring Boot applications by providing all the required dependencies.

In order to run the application, please execute the following command from the root of the project, where the ```pom.xml``` file is located :

**```mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8080```**

You can customize the server port the application will listen requests to (here 8080 in the example), please choose a free port number

You can customize the log level of the Spring / Spring Boot framework classes by adding the logging.level argument as shown below

**```mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8080 --logging.level.org.springframework=DEBUG"```**

## Running as a standalone java application
You can run the application by producing the deliverable of the application as a jar file and then execute the main class from the jar file

Run the following commands :

**```mvn clean package spring-boot:repackage```**
  * or to avoid running the unit tests each time :
    * ```mvn clean package spring-boot:repackage -DskipTests=true``` 

**```ls target/userapi-0.0.1-SNAPSHOT.jar```**
  * file should exist


**```java -Dserver.port=8080 -jar target/userapi-0.0.1-SNAPSHOT.jar```**

You can customize the server port the application by changing the server.port value in the command

## Running from your favotrite IDE
Run class ```com.sbr.userapi.UserApiApplication``` as a java main class
Add run option if you want to user a different port than default 8080

## How to check the application has started correctly

### First check the logs
At startup the application outputs logs on the console

The log should display the server port used :
```
2020-07-08 21:13:04.161  INFO [    main]    o.s.b.w.e.t.TomcatWebServer - Tomcat initialized with port(s): 8080 (http)
```

and end with lines similar to these:
```
2020-07-08 21:13:07.104  INFO [    main] c.s.userapi.UserApiApplication - Started UserApiApplication in 7.381 seconds (JVM running for 8.295)
2020-07-08 21:13:07.106  INFO [    main] c.s.userapi.UserApiApplication - The User Application has started...
```

### Then run a first command on the application API
Assuming the application is running on port **8080**, run command :

```curl http://localhost:8080/users```

Output should look like :
```
[...some progress information...][{"id":1,"firstName":"Nikola","email":"ntesla@userapi.sbr","password":"@x5RK!~;2<JmwSC"},{"id":2,"firstName":"Albert","email":"aeinstein@userapi.sbr","password":"pEJHewG2+EvNZ$"},{"id":3,"firstName":"Albert","email":"aotherguy@userapi.sbr","password":"oo@bcDsxmCI7~06vX$j"}]
```

**Great ! You just was the list of test users which are automatically created by the application at startup !
The application is responding correctly and ready to be used !**


# API documentation
The API documentation is generated using Swagger and is served by the application at this url (adapt port number):
http://localhost:8080/swagger-ui.html


# How to use the application

The base URL for accessing the application API is (assuming you run the application on port 8080)

```curl http://localhost:8080/users```

This commands requests for the list of all users currently present in the database

**Please check the Swagger documentation and find some sample commands that show how to call each operation in the ```sample-commands.txt``` file at the root of project files**. 
Each command has a description and an sample of the expected output.


# How to view the H2 in-memory actual database contents
Open url ***```http://localhost:8080/h2/```***

(use same port number as the one used when running the application)

A login form should be displayed. Change the ```JDBC URL``` input value to ```jdbc:h2:mem:testdb```
Keep other values unchanged (database url and credentials must be the same as in the ```application.properties``` file from the application)


##  Design choices and limitations
Here is an explanation of some design choices.

* Libraries
  * Spring Boot : well adapted to standalone applications and good support for REST / RESTFul applications
  * Spring Cloud framework which is compatible with many messaging systems : Apache Kafka, Amazon Kinesis, Google PusbSub and others
* In memory H2 database
  * Data is persisted into an in memory database, which is launched when the application starts and is shutdown when application stops. All data is lost at shutdown
  * This allows running the application without having to rely on an external database and still having all the JPA / Hibernate persistence layers working as on any database, which is **suitable for a demo usage only**
  * Note : **an actual external database can be used** simply by changing the configuration in the ```src/main/resources/application.properties``` file (see the ```spring.datasource.``` properties)
* Logging to console only
  * This choice was done purposely with containerization in mind. Containers orchestrators such as Kubernetes are better suited for streaming logs from the application standard output
* Error messages in response and error management
  * Send in response body an ErrorDetails object containing a timestamp, an error message and the path of the request
  * Use the Spring @ControllerAdvice annotation and implement a ResponseEntityExceptionHandler that catches exceptions and produces adequate ResponseEntity with ErrorDetails in body
* Validation
  * Validate Hibernate entities using Jakarta Bean Validation 2.0 (JSR 380)

# Enhancements
Many enhancements of this demo application are possible

## Functional enhancements
- Ability to GET only a subset of users attributes
- Ability to GET multiple users at once by id (Example: /users/{id1},{id2})

## Technical enhancements
- Unit tests code coverage : currently coverage is greather than 80% (measured with EclEmma in Eclipse). It has to be increased. 
- Logging : create multiple profiles (e.g. dev/staging/production)
- RESTFul API : make the API RESTFUl using String HATEOAS project (https://spring.io/projects/spring-hateoas)
- Error management : provide more details about the errors in the API responses (currently messages in thrown exceptions are not used)
- Message bus :
  - make access to the message bus configurable using properties
  - application could connect to many other systems than RabbitMQ such as Kafka since it uses the Spring Cloud framework which is compatible with many systems. Some configuration changes would allow such change without changing the application code.
See documentation at https://spring.io/projects/spring-cloud-stream
- Securing sensitive data :
  - currently the API allows requesting all the users information, including their password. It should allow to request only an authorized subset of fields and hide sensitive data
  - the PATCH method allows changing any attribute (except the id). It could manage a set of authorized fields
- Configure credentials : the application uses default login/passwords for connecting to the message bus (RabbitMQ), this shoud be configurable
- Run into a container (Docker / Kubernetes)

# Troubleshooting

## Lombok classes are not generated by Eclipse at compile time
You need to install the Lombok agent on your Eclipse. See official tutorial :
https://howtodoinjava.com/automation/lombok-eclipse-installation-examples/#lombok-eclipse
If the installation screen of the Lombok agent does not appear correctly (e.g. the installation buton is missing or the window seems truncated), try running the installer with an Oracle JDK
instead of an Open JDK


## MapStruct classes are not generated by Eclipse at compile time
You need the m2e-apt plugin to be installed
See official page : https://marketplace.eclipse.org/content/m2e-apt
Also annotation processing must be enabled. Go to Window > Preferences > Maven > Annotation Processing or right-click on your project Properties > Maven > Annotation Processing to select the Annotation Processing strategy of your choice. m2e-apt supports both Annotation Processing set on the maven-compiler-plugin or the maven-processor-plugin (the latter takes precedence over the former).
