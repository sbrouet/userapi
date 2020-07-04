package com.sbr.userapi.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Contains values from the application configuration
 * 
 * @author sbrouet
 *
 */
@Component
public class ConfigurationBean {

	/**
	 * The Ip Api external service Url in {@link java.text.MessageFormat} format
	 */
	@Value("${ipapi.url.template}")
	private String ipAPIUrlTemplate;

	/**
	 * The timeout in milliseconds when connecting to the Ip Api external service
	 */
	@Value("${ipapi.timeout.connect}")
	private long ipAPITimeoutConnect;

	/**
	 * The timeout in milliseconds when reading data from the Ip Api external
	 * service
	 */
	@Value("${ipapi.timeout.read}")
	private long ipAPITimeoutRead;

	public String getIpAPIUrlTemplate() {
		return ipAPIUrlTemplate;
	}

	public void setIpAPIUrlTemplate(String ipAPIUrlTemplate) {
		this.ipAPIUrlTemplate = ipAPIUrlTemplate;
	}

	public long getIpAPITimeoutConnect() {
		return ipAPITimeoutConnect;
	}

	public void setIpAPITimeoutConnect(long ipAPITimeoutConnect) {
		this.ipAPITimeoutConnect = ipAPITimeoutConnect;
	}

	public long getIpAPITimeoutRead() {
		return ipAPITimeoutRead;
	}

	public void setIpAPITimeoutRead(long ipAPITimeoutRead) {
		this.ipAPITimeoutRead = ipAPITimeoutRead;
	}

}