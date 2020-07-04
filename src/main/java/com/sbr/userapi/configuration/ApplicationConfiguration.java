package com.sbr.userapi.configuration;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * The application configuration allows to create Spring Beans that are used by
 * other components so that they can be autowired
 * 
 * @author sbrouet
 *
 */
@Component
@Configuration
public class ApplicationConfiguration {

	@Autowired
	private ConfigurationBean configBean;

	/**
	 * Create a {@link RestTemplate} configured according to the application
	 * properties file
	 */
	@Bean
	public RestTemplate restTemplate(RestTemplateBuilder builder) {
		return builder.setConnectTimeout(Duration.ofMillis(configBean.getIpAPITimeoutConnect())) //
				.setReadTimeout(Duration.ofMillis(configBean.getIpAPITimeoutRead())).build();
	}

}