/*
  DO NOT RENAME unless you checked naming conventions
  See documentation at
    https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-database-initialization
  This file is automatically executed by Spring Boot on database in order to initialize database schema
  
  This is the database schema
*/
DROP TABLE IF EXISTS USER;

CREATE TABLE USER (
	id BIGINT NOT NULL,
	first_name VARCHAR(100) NOT NULL,
	email VARCHAR(50) NOT NULL,
	password VARCHAR(50) DEFAULT NULL,
	
	CONSTRAINT PK_USER PRIMARY KEY (id)
);

DROP SEQUENCE IF EXISTS SEQ_USER_ID;

CREATE SEQUENCE SEQ_USER_ID START WITH 10;
