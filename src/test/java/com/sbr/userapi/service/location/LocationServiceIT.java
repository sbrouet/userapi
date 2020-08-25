package com.sbr.userapi.service.location;

import static com.sbr.userapi.service.location.LocationTestConstants.AN_FR_IP;
import static com.sbr.userapi.service.location.LocationTestConstants.SWISSCOM_CH_IP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.sbr.userapi.UserApiApplication;
import com.sbr.userapi.exception.location.CannotComputeLocationException;

/**
 * Integration test for {@link LocationService} that actually calls the external
 * IP API Service
 * 
 * @author sbrouet
 *
 */
@ExtendWith(SpringExtension.class)
@SpringBootTest(classes = UserApiApplication.class)
@Import(com.sbr.userapi.configuration.ConfigurationBean.class)
public class LocationServiceIT {

	@Autowired
	private LocationService locationService;

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : IP
	 * address is expected to be detected as from Switzerland
	 */
	@Test
	public void isCallerFromSwitzerland_true() throws CannotComputeLocationException {
		doTest_isCallerFromSwitzerland(SWISSCOM_CH_IP, true);
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : IP
	 * address is expected to be detected as NOT from Switzerland
	 */
	@Test
	public void isCallerFromSwitzerland_false() throws CannotComputeLocationException {
		doTest_isCallerFromSwitzerland(AN_FR_IP, false);
	}

	private void doTest_isCallerFromSwitzerland(final String ipAddress, final boolean expectedResult)
			throws CannotComputeLocationException {
		assertThat(locationService.isCallerFromSwitzerland(ipAddress)).isEqualTo(expectedResult);
	}

	/**
	 * Test method {@link LocationService#isCallerFromSwitzerland(String)} : when
	 * requesting an invalid IP, an exception should be raised
	 */
	@Test
	public void getCountryCodeForIP_invalidIP_ShouldRaiseException() throws CannotComputeLocationException {
		assertThrows(CannotComputeLocationException.class, () -> {
			locationService.isCallerFromSwitzerland("A.B.C.D");
		});
	}
}
