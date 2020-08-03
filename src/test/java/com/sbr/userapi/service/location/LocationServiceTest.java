package com.sbr.userapi.service.location;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.atLeast;

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
import com.sbr.userapi.test.TestUtils;

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

	/** An IP address from France, used for mocks */
	private static final String AN_FR_IP = "90.8.134.100";
	/**
	 * Expected URL of the external Ip Api service when getting the country code for
	 * {@link #AN_FR_IP}
	 */
	private static final String IP_API_SERVICE_URL_FOR_AN_FR_IP = "https://ipapi.co/" + AN_FR_IP + "/country";

	/**
	 * The ISO 3166-1 Alpha 2 code for country France<BR/>
	 * See country code list at https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
	 */
	private static final String FRANCE_COUNTRY_CODE_ISO_3166_1 = "FR";

	/**
	 * Expected URL of the external Ip Api service when getting the country code for
	 * {@link TestUtils#SWISSCOM_CH_IP}
	 */
	private static final String IP_API_SERVICE_URL_FOR_AN_CH_IP = "https://ipapi.co/" + TestUtils.SWISSCOM_CH_IP
			+ "/country";

	/**
	 * The ISO 3166-1 Alpha 2 code for country Switzerland<BR/>
	 * See country code list at
	 * https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2<BR/>
	 * Note : purposely duplicated constant here instead of reusing the one from the
	 * {@link LocationService} in order the test to be independant
	 */
	private static final String SWITZERLAND_COUNTRY_CODE_ISO_3166_1 = "CH";

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

	/*
	 * @MockBean private ConfigurationBean configurationBean;
	 */

	/**
	 * Test method
	 * {@link LocationService#buildCountryCodeRestServiceURLForIP(String)}
	 */
	@Test
	public void buildCountryCodeRestServiceURLForIP_checkURL() {
		assertThat(locationService.buildCountryCodeRestServiceURLForIP(AN_FR_IP))
				.isEqualTo(IP_API_SERVICE_URL_FOR_AN_FR_IP);
		assertThat(locationService.buildCountryCodeRestServiceURLForIP(TestUtils.SWISSCOM_CH_IP))
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
		assertThat(locationService.getCountryCodeForIP(TestUtils.SWISSCOM_CH_IP))
				.isEqualTo(SWITZERLAND_COUNTRY_CODE_ISO_3166_1);
		Mockito.verify(mockResponse, atLeast(1)).getStatusCode();
		Mockito.verify(mockResponse, atLeast(1)).getBody();
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : IP
	 * address is expected to be detected as from Switzerland
	 */
	@Test
	public void isCallerFromSwitzerland_true() throws CannotComputeLocationException {
		doTest_isCallerFromSwitzerland(TestUtils.SWISSCOM_CH_IP, SWITZERLAND_COUNTRY_CODE_ISO_3166_1, true);
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
