# Project description 
**NOT FOR PRODUCTION** The UserApiApplication is a demo of a usable REST API for managing users

# Prerequisites

## How to run the application
In order for the UserApiApplication to be working you need to run the following components in that order (technical details follow)
- a running RabbitMQ
- running the UserApiApplication

## Technical prerequisites
In order to build and run this project, you need :
  - a Java SE Development Kit (JDK) with at least version 11
  - a working Maven installation : the "mvn" command is accessible on the command line, it uses the JDK version 11+ and your settings allow Maven to download artifacts from the Maven Central Repository
  - a working Docker installation being able to run the RabbitMQ docker image (details below)
  
### Running RabbitMQ docker image
The UserApiApplication assumes that RabbitMQ run on localhost and is accessible on default port (5672).
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


# Enhancements
Many enhancements this demo application are possible

## Technical enhancements
- Message bus : application could connect to many other systems than RabbitMQ. The UserApiApplication usesn the Spring Cloud framework which is compatible with many systems : Apache Kafka, Amazon Kinesis, Google PusbSub and others. Some configuration changes would allow such change without changing the application code.
See documentation at https://spring.io/projects/spring-cloud-stream


