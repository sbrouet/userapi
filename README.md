# Project description 
**NOT FOR PRODUCTION** The UserApiApplication is a demo of a usable REST API for managing users
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
Upon each event that impacts users, such as creation /full or partial update / deletion, a message is sent to a message bus. The message contains the ```id``` of impacter user and the nature of the operation (USER_CREATED, USER_DELETED, USER_UPDATED)


# Prerequisites

## Prerequisites for runnning the application
In order for the UserApiApplication to be working you need to run the following components in that order (technical details follow)
- a running RabbitMQ
- running the UserApiApplication

## Technical prerequisites
In order to build and run this project, you need :
  - a Java SE Development Kit (JDK) with at least version 11
  - a working Maven installation : the ```mvn``` command is available on the command line, it uses the JDK version 11+ and your settings allow Maven to download artifacts from the Maven Central Repository
  - a working Docker installation being able to run the RabbitMQ docker image (details below)
  - the ```curl``` command is available on the command line

### Running RabbitMQ docker image
The **UserApiApplication assumes that RabbitMQ run on localhost and is accessible on default port (5672).**
If you do not have one running, you can follow these steps to run a RabbitMQ instance using a Docker image.
The RabbitMQ docker image documentation is available at:
    https://hub.docker.com/_/rabbitmq

Run the following command from your terminal
 
```docker run -it --rm --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management```

Output should look like :
```
[...]
2020-07-08 08:26:59.791 [info] <0.268.0>
 Starting RabbitMQ 3.8.5 on Erlang 23.0.2
[...]
2020-07-08 07:55:29.285 [info] <0.684.0> Server startup complete; 3 plugins started.
```

When the UserApiApplication is started, the container logs should display messages such as:
2020-07-07 14:38:30.983 [info] <0.5487.0> accepting AMQP connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672)
2020-07-07 14:38:31.068 [info] <0.5487.0> Connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672) has a client-provided name: rabbitConnectionFactory#2c3f43d1:0
2020-07-07 14:38:31.082 [info] <0.5487.0> connection <0.5487.0> (172.17.0.1:39220 -> 172.17.0.2:5672 - rabbitConnectionFactory#2c3f43d1:0): user 'guest' authenticated and granted access to vhost '/'

# Building the projet
On the command line, run the following command:
```mvn clean install```
The project should be built successfully and unit tests run automatically and be successfull too

# Running the application
## Recommended way : via the Maven plugin
This the **prefered way for demo purpose only**
The spring-boot maven plugin allows to run Spring Boot applications by providing all the required dependencies.
In order to run the application, please execute the following command from the root of the project, where the ```pom.xml``` file is located :
**```mvn spring-boot:run -Dspring-boot.run.arguments=--server.port=8085```**

You can customize the server port the application will listen requests to (here 8085 in the example), please choose a free port number
You can customize the log level of the Spring / Spring Boot framework classes by adding the logging.level argument as shown below
**```mvn spring-boot:run -Dspring-boot.run.arguments="--server.port=8085 --logging.level.org.springframework=DEBUG"```**

## Running as a standalone java application
You can run the application by producing the deliverable of the application as a jar file and then execute the main class from the jar file

Run the following commands :

**```mvn clean package spring-boot:repackage```**
  * or to avoid running the unit tests each time :
    * ```mvn clean package spring-boot:repackage -DskipTests=true``` 

**```ls target/userapi-0.0.1-SNAPSHOT.jar```**
  * file should exist

**```java -Dserver.port=8085 -jar target/userapi-0.0.1-SNAPSHOT.jar```**
You can customize the server port the application by changing the server.port value in the command

## How to check the application has started correctly

### First check the logs
At startup the application outputs logs on the console
The log should display the server port used :
```
2020-07-08 21:13:04.161  INFO [    main]    o.s.b.w.e.t.TomcatWebServer - Tomcat initialized with port(s): 8085 (http)
```

and end with lines similar to these:
```
2020-07-08 21:13:07.104  INFO [    main] c.s.userapi.UserApiApplication - Started UserApiApplication in 7.381 seconds (JVM running for 8.295)
2020-07-08 21:13:07.106  INFO [    main] c.s.userapi.UserApiApplication - The User Application has started...
```

### Then run a first command on the application API
Assuming the application is running on port **8085**, run command :

```curl http://localhost:8085/users```

Output should look like :
```
[...some progress information...][{"id":1,"firstName":"Nikola","email":"ntesla@userapi.sbr","password":"@x5RK!~;2<JmwSC"},{"id":2,"firstName":"Albert","email":"aeinstein@userapi.sbr","password":"pEJHewG2+EvNZ$"},{"id":3,"firstName":"Albert","email":"aotherguy@userapi.sbr","password":"oo@bcDsxmCI7~06vX$j"}]
```

**Great ! You just was the list of test users which are automatically created by the application on sartup !
The application is responding correctly and ready to be used !**

# How to use the application

# TODO curl + h2 console

The base URL for accessing the application API is (assuming you run the application on port 8085)
```curl http://localhost:8085/users```
This commands requests for the list of all users currently present in the database

**Please find sample commands that show how to call each operation in the ```sample-commands.txt``` file at the root of project files**
Each command has a description and an sample of the expected output.

##  Design choices and limitations
Here is an explanation of some design choices.

# TODO spring boot (easy, well adapted to standalone applications), spring cloud (many Kafka etc)

* In memory H2 database
  * Data is persisted into an in memory database, which is launched when the application starts and is shutdown when application stops. All data is lost at shutdown.
  * This allows running the application without having to rely on an external database and still having all the JPA / Hibernate persistence layers working as on any database, which is **suitable for a demo usage only**
  * Note : **an actual external database can be used*** simply by changing the configuration in the ```src/main/resources/application.properties``` file (see the ```spring.datasource.*``` properties)
* Logging to console only
  * This choice was done purposely with containerization in mind. Containers orchestrators such as Kubernetes are better suited for streaming logs from the application standard output


# Enhancements
Many enhancements this demo application are possible

## Functional enhancements
- Ability to GET only a subset of users attributes
- Ability to GET multiple users at once by id (Example: /users/{id1},{id2})

## Technical enhancements
- **TODO** Unit tests code coverage : currently coverage is **TODO**
- Error management : **TODO a voir** have more details about the errors in the API responses
- Run into a container (Docker / Kubernetes)
- Message bus : application could connect to many other systems than RabbitMQ. The UserApiApplication usesn the Spring Cloud framework which is compatible with many systems : Apache Kafka, Amazon Kinesis, Google PusbSub and others. Some configuration changes would allow such change without changing the application code.
See documentation at https://spring.io/projects/spring-cloud-stream
- Securing sensitive data : currently the API allows requesting all the users information, including their password. It should allow to request only an authorized subset of fields and hide sensitive data
- Configure credentials : the application uses default login/passwords for connecting to the message bus (RabbitMQ), this shoud be configurable
- 

