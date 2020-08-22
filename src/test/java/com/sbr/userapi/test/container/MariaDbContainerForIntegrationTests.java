package com.sbr.userapi.test.container;

import org.testcontainers.containers.MariaDBContainer;

/**
 * A container that runs a MariaDb database. The container is accessible as a
 * singleton, so if multiple tests require it, it does not need to be created
 * multiple times as initialization is costly
 * 
 * @author sbrouet
 *
 */
public class MariaDbContainerForIntegrationTests extends MariaDBContainer<MariaDbContainerForIntegrationTests> {
	private static final String IMAGE_VERSION = "mariadb:10.5.5";
	private static MariaDbContainerForIntegrationTests container;

	private MariaDbContainerForIntegrationTests() {
		super(IMAGE_VERSION);
	}

	public static synchronized MariaDbContainerForIntegrationTests getInstance() {
		if (container == null) {
			container = new MariaDbContainerForIntegrationTests();
		}
		return container;
	}

	@Override
	public void start() {
		super.start();
	}

	@Override
	public void stop() {
		// do nothing, JVM handles shut down
	}

}