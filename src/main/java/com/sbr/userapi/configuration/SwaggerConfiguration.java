package com.sbr.userapi.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

/**
 * Make Swagger API docs available for all the application controllers.<BR/>
 * URL look like
 * <UL>
 * <LI>http://localhost:8080/v2/api-docs</LI>
 * <LI>http://localhost:8080/swagger-ui.html</LI>
 * <UL>
 * 
 * @author sbrouet
 *
 */
@Configuration
@EnableSwagger2
public class SwaggerConfiguration {

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.SWAGGER_2).select()
				.apis(RequestHandlerSelectors.basePackage("com.sbr.userapi")).paths(PathSelectors.any()).build();
	}
}