package com.sbr.userapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class UserApiApplication {

	private static final Logger LOGGER = LoggerFactory.getLogger(UserApiApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(UserApiApplication.class, args);
		LOGGER.info("The User Application has started...");
	}

}
