package com.sbr.userapi.service.location;

import static com.sbr.userapi.service.location.LocationTestConstants.AN_FR_IP;
import static com.sbr.userapi.service.location.LocationTestConstants.FRANCE_COUNTRY_CODE_ISO_3166_1;
import static com.sbr.userapi.service.location.LocationTestConstants.SWISSCOM_CH_IP;
import static com.sbr.userapi.service.location.LocationTestConstants.SWITZERLAND_COUNTRY_CODE_ISO_3166_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.times;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.sbr.userapi.configuration.ConfigurationBean;
import com.sbr.userapi.exception.location.CannotComputeLocationException;

/**
 * Unit test for {@link LocationService}
 * 
 * @author sbrouet
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest
@Import(com.sbr.userapi.configuration.ConfigurationBean.class)
public class LocationServiceTest {

	/**
	 * Expected URL of the external Ip Api service when getting the country code for
	 * {@link LocationTestConstants#AN_FR_IP}
	 */
	private static final String IP_API_SERVICE_URL_FOR_AN_FR_IP = "https://ipapi.co/" + AN_FR_IP + "/country";

	/**
	 * Expected URL of the external Ip Api service when getting the country code for
	 * {@link LocationTestConstants#SWISSCOM_CH_IP}
	 */
	private static final String IP_API_SERVICE_URL_FOR_AN_CH_IP = "https://ipapi.co/" + SWISSCOM_CH_IP + "/country";

	@TestConfiguration
	@SpringBootConfiguration
	static class LocationServiceTestContextConfiguration {

		/**
		 * Create a LocationService with mocked dependencies
		 * 
		 * @param restTemplate mock is automatically injected by Spring
		 * @return an initialized {@link LocationService}
		 */
		@Bean
		public LocationService locationService(RestTemplate restTemplate, ConfigurationBean configurationBean) {
			return new LocationService(restTemplate, configurationBean);
		}
	}

	@Autowired
	private LocationService locationService;

	@MockBean
	private RestTemplate restTemplate;

	/**
	 * Test method
	 * {@link LocationService#buildCountryCodeRestServiceURLForIP(String)}
	 */
	@Test
	public void buildCountryCodeRestServiceURLForIP_checkURL() {
		assertThat(locationService.buildCountryCodeRestServiceURLForIP(AN_FR_IP))
				.isEqualTo(IP_API_SERVICE_URL_FOR_AN_FR_IP);
		assertThat(locationService.buildCountryCodeRestServiceURLForIP(SWISSCOM_CH_IP))
				.isEqualTo(IP_API_SERVICE_URL_FOR_AN_CH_IP);
	}

	/**
	 * Test method {@link LocationService#getCountryCodeForIP(String)} for an IP
	 * address from France with a mocked response from the Ip Api external service
	 */
	@Test
	public void getCountryCodeForIP_checkExternalServiceIsCalledAndReturnedValueIsCorrect_forFrIp()
			throws CannotComputeLocationException {
		ResponseEntity<String> mockResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockResponse.getBody()).thenReturn(FRANCE_COUNTRY_CODE_ISO_3166_1);
		Mockito.when(restTemplate.getForEntity(IP_API_SERVICE_URL_FOR_AN_FR_IP, String.class)).thenReturn(mockResponse);

		// Test
		assertThat(locationService.getCountryCodeForIP(AN_FR_IP)).isEqualTo(FRANCE_COUNTRY_CODE_ISO_3166_1);
		Mockito.verify(mockResponse, atLeast(1)).getStatusCode();
		Mockito.verify(mockResponse, atLeast(1)).getBody();
	}

	/**
	 * Test method {@link LocationService#getCountryCodeForIP(String)} for an IP
	 * address from Switzerland with a mocked response from the Ip Api external
	 * service
	 */
	@Test
	public void getCountryCodeForIP_checkExternalServiceIsCalledAndReturnedValueIsCorrect_forChIp()
			throws CannotComputeLocationException {
		ResponseEntity<String> mockResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(mockResponse.getStatusCode()).thenReturn(HttpStatus.OK);
		Mockito.when(mockResponse.getBody()).thenReturn(SWITZERLAND_COUNTRY_CODE_ISO_3166_1);
		Mockito.when(restTemplate.getForEntity(IP_API_SERVICE_URL_FOR_AN_CH_IP, String.class)).thenReturn(mockResponse);

		// Test
		assertThat(locationService.getCountryCodeForIP(SWISSCOM_CH_IP)).isEqualTo(SWITZERLAND_COUNTRY_CODE_ISO_3166_1);
		Mockito.verify(mockResponse, atLeast(1)).getStatusCode();
		Mockito.verify(mockResponse, atLeast(1)).getBody();
	}

	@Test
	public void getCountryCodeForIP_externalServiceFailure_notOKStatusCode_ShouldRaiseException() {
		ResponseEntity<String> mockResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
		Mockito.when(restTemplate.getForEntity(IP_API_SERVICE_URL_FOR_AN_CH_IP, String.class)).thenReturn(mockResponse);

		// Test
		assertThrows(CannotComputeLocationException.class, () -> {
			locationService.getCountryCodeForIP(SWISSCOM_CH_IP);
		});
		Mockito.verify(mockResponse, atLeast(1)).getStatusCode();
		Mockito.verify(mockResponse, times(1)).getBody();
		Mockito.verify(restTemplate, times(1)).getForEntity(IP_API_SERVICE_URL_FOR_AN_CH_IP, String.class);
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : when
	 * requesting an invalid IP, external services returns an "Undefined" response
	 * and an exception should be raised
	 */
	@Test
	public void getCountryCodeForIP_externalServiceFailure_bodyUndefinedValue_ShouldRaiseException() {
		ResponseEntity<String> mockResponse = Mockito.mock(ResponseEntity.class);

		Mockito.when(mockResponse.getStatusCode()).thenReturn(HttpStatus.INTERNAL_SERVER_ERROR);
		Mockito.when(mockResponse.getBody()).thenReturn(LocationService.COUNTRY_UNDEFINED_RESPONSE_BODY);
		Mockito.when(restTemplate.getForEntity(IP_API_SERVICE_URL_FOR_AN_CH_IP, String.class)).thenReturn(mockResponse);

		// Test
		assertThrows(CannotComputeLocationException.class, () -> {
			locationService.getCountryCodeForIP(SWISSCOM_CH_IP);
		});
		Mockito.verify(mockResponse, atLeast(1)).getStatusCode();
		Mockito.verify(mockResponse, times(1)).getBody();
		Mockito.verify(restTemplate, times(1)).getForEntity(IP_API_SERVICE_URL_FOR_AN_CH_IP, String.class);
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : IP
	 * address is expected to be detected as from Switzerland
	 */
	@Test
	public void isCallerFromSwitzerland_true() throws CannotComputeLocationException {
		doTest_isCallerFromSwitzerland(SWISSCOM_CH_IP, SWITZERLAND_COUNTRY_CODE_ISO_3166_1, true);
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : IP
	 * address is expected to be detected as NOT from Switzerland
	 */
	@Test
	public void isCallerFromSwitzerland_false() throws CannotComputeLocationException {
		doTest_isCallerFromSwitzerland(AN_FR_IP, FRANCE_COUNTRY_CODE_ISO_3166_1, false);
	}

	/**
	 * Actual test for {@link LocationService#isCallerFromSwitzerland(String)}
	 * 
	 * @param ipAddress       IP address to be tested
	 * @param mockCountryCode the mock value for country code returned by the mocked
	 *                        external service that the tested method calls
	 * @param expectedResult  expected result of method
	 *                        {@link LocationService#isCallerFromSwitzerland(String)}
	 * @throws CannotComputeLocationException
	 */
	private void doTest_isCallerFromSwitzerland(final String ipAddress, final String mockCountryCode,
			final boolean expectedResult) throws CannotComputeLocationException {
		final LocationService mockLocationService = Mockito.mock(LocationService.class);

		// Ensure tested method is called for real and mock dependend method result
		Mockito.when(mockLocationService.isCallerFromSwitzerland(ipAddress)).thenCallRealMethod();
		Mockito.when(mockLocationService.getCountryCodeForIP(ipAddress)).thenReturn(mockCountryCode);
		// Test
		assertThat(mockLocationService.isCallerFromSwitzerland(ipAddress)).isEqualTo(expectedResult);
		Mockito.verify(mockLocationService, atLeast(1)).getCountryCodeForIP(ipAddress);
	}
}
