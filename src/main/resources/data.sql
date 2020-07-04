/*
  DO NOT RENAME unless you checked naming conventions
  See documentation at
    https://docs.spring.io/spring-boot/docs/current/reference/html/howto.html#howto-database-initialization
  This file is automatically executed by Spring Boot on database in order to initialize database data
  
  This is a basic dataset of users for demo purpose
*/
INSERT INTO 
	USER (id, first_name, email, password) 
VALUES
  	(1, 'Nikola', 'ntesla@userapi.sbr', '@x5RK!~;2<JmwSC'),
  	(2, 'Albert', 'aeinstein@userapi.sbr', 'pEJHewG2+EvNZ$'),
  	(3, 'Albert', 'aotherguy@userapi.sbr', 'oo@bcDsxmCI7~06vX$j')
  	;