package org.societies.integration.tests.bit.webapp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.integration.test.IntegrationTestCase;

public class TestInit extends IntegrationTestCase {

    private static final Logger log = LoggerFactory.getLogger(TestInit.class);

    public TestInit() {
        super(2078, TestIndexPage.class, TestExamplePage.class, TestProfileSettings.class);
        log.debug("Starting TestInit");

    }

}
