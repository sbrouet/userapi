package com.sbr.userapi.service.location;

import java.text.MessageFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.sbr.userapi.configuration.ConfigurationBean;
import com.sbr.userapi.exception.location.CannotComputeLocationException;

/**
 * Service which allows checking for the location of an IP address
 * 
 * @author sbrouet
 *
 */
@Service
public class LocationService {
	private static final Logger LOGGER = LoggerFactory.getLogger(LocationService.class);

	/**
	 * The ISO 3166-1 Alpha 2 code for country Switzerland<BR/>
	 * See country code list at https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
	 */
	private static final String SWITZERLAND_COUNTRY_CODE_ISO_3166_1 = "CH";

	/**
	 * A {@link MessageFormat} used to create the location service REST URLs by IP.
	 * It is put inside a {@ThreadLocal} object so it is thread safe, so it can be
	 * reused safely from all threads
	 */
	private ThreadLocal<MessageFormat> threadLocalMessageFormat = new ThreadLocal<MessageFormat>() {
		@Override
		protected MessageFormat initialValue() {
			return new MessageFormat(configBean.getIpAPIUrlTemplate());
		}
	};

	/**
	 * The response body from the API location REST Service when it does not know
	 * what is an IP's country
	 */
	private static String COUNTRY_UNDEFINED_RESPONSE_BODY = "Undefined";

	/**
	 * Client to the external REST Webservice used to get IP location information
	 */
	private RestTemplate restTemplate;

	// TODO provide all dependent objects in constructor
	@Autowired
	private ConfigurationBean configBean;

	@Autowired
	public LocationService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	/**
	 * Indicates whether or not an IP address is from Switzerland
	 * 
	 * @param ip IP address to be checked
	 * @return <code>true</code> when IP is from Switzerland, otherwise return
	 *         <code>false</code>
	 * @throws CannotComputeLocationException
	 */
	public boolean isCallerFromSwitzerland(final String ip) throws CannotComputeLocationException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isCallerFromSwitzerland(" + ip + ")");
		}
		final boolean isFromSwitzerland = SWITZERLAND_COUNTRY_CODE_ISO_3166_1.equals(getCountryCodeForIP(ip));
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("isCallerFromSwitzerland(" + ip + ") : " + isFromSwitzerland);
		}
		return isFromSwitzerland;
	}

	/**
	 * Get the country code for given IP address<BR/>
	 * WARNING : this method does call an external REST service (see
	 * {@link #IP_API_REST_URL_PATTERN})
	 * 
	 * @param ip IP address to get code for
	 * @return the ISO 3166-1 Alpha 2 country code
	 * @throws CannotComputeLocationException when the client location could not be
	 *                                        computed by its IP
	 */
	String getCountryCodeForIP(final String ip) throws CannotComputeLocationException {
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("getCountryCodeForIP(" + ip + ")");
		}
		final ResponseEntity<String> response = restTemplate.getForEntity(buildCountryCodeRestServiceURLForIP(ip),
				String.class);
		if (!HttpStatus.OK.equals(response.getStatusCode())
				|| COUNTRY_UNDEFINED_RESPONSE_BODY.equals(response.getBody())) {
			if (LOGGER.isInfoEnabled()) {
				LOGGER.info("Could get location from external service for ip [" + ip + "]. HttpStatus ["
						+ response.getStatusCode() + "] Response Body [" + response.getBody() + "]");
			}
			throw new CannotComputeLocationException("Could not compute location for ip [" + ip + "]");
		}

		final String country = response.getBody();
		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("IP " + ip + " is from country [" + country + "]");
		}
		return country;
	}

	/**
	 * Build the REST Service URL for requesting location data for given IP address
	 * 
	 * @param ip to request
	 * @return a String that represents the REST Service URL, never
	 *         <code>null</code>
	 */
	String buildCountryCodeRestServiceURLForIP(final String ip) {
		return threadLocalMessageFormat.get().format(new Object[] { ip });
	}
}
