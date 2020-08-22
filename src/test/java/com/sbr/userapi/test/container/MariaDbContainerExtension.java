package com.sbr.userapi.test.container;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;

/**
 * Initializes a {@link MariaDbContainerForIntegrationTests} before all tests
 * are run, so the container is available for tests
 * 
 * @author sbrouet
 *
 */
public class MariaDbContainerExtension implements BeforeAllCallback, AfterAllCallback {

	@Override
	public void beforeAll(ExtensionContext context) throws Exception {
		MariaDbContainerForIntegrationTests.getInstance().start();
	}

	@Override
	public void afterAll(ExtensionContext context) throws Exception {
		MariaDbContainerForIntegrationTests.getInstance().stop();
	}

}