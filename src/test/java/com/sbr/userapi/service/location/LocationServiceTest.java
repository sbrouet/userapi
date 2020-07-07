package com.sbr.userapi.service.location;

import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.web.client.RestTemplate;

public class LocationServiceTest {

	// TODO write test
	// TODO ? junit 5 parameterized test
	@Test
	public void buildCountryCodeRestServiceURLForIP() {
		// TODO inject builder with Spring
		RestTemplateBuilder fakeBuilder = new RestTemplateBuilder() {
			@Override
			public RestTemplate build() {
				return null;
			}
		};

		LocationService locationService = new LocationService(fakeBuilder);
		Object[] testArgs;

		final String anFrIP = "90.8.134.100";
		testArgs = new Object[] { anFrIP };
		System.out.println(locationService.threadLocalMessageFormat.get().format(testArgs));

		final String ricardoChIP = "104.18.30.124";
		testArgs = new Object[] { ricardoChIP };
		System.out.println(locationService.threadLocalMessageFormat.get().format(testArgs));

		final String anInvalidIP = "90.8.134.100";
		testArgs = new Object[] { anInvalidIP };
		System.out.println(locationService.threadLocalMessageFormat.get().format(testArgs));

	}
}
