package com.sbr.userapi.service.location;

public class LocationTestConstants {

	/** IP of the Swisscom.ch website */
	public static final String SWISSCOM_CH_IP = "195.186.208.154";

	/** An IP address from France, used for mocks */
	public static final String AN_FR_IP = "90.8.134.100";

	/**
	 * The ISO 3166-1 Alpha 2 code for country Switzerland<BR/>
	 * See country code list at
	 * https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2<BR/>
	 * Note : purposely duplicated constant here instead of reusing the one from the
	 * {@link LocationService} in order the test to be independant
	 */
	public static final String SWITZERLAND_COUNTRY_CODE_ISO_3166_1 = "CH";

	/**
	 * The ISO 3166-1 Alpha 2 code for country France<BR/>
	 * See country code list at https://en.wikipedia.org/wiki/ISO_3166-1_alpha-2
	 */
	public static final String FRANCE_COUNTRY_CODE_ISO_3166_1 = "FR";

}
