package org.societies.android.platform.contentprovidermonitor.test;

import org.societies.clientframework.contentprovider.services.DifferentProcessService;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class TestStartedService extends ServiceTestCase<DifferentProcessService>{


	public TestStartedService() {
		super(DifferentProcessService.class);
	}

	@Override
    protected void setUp() throws Exception {
		//must be first statement in method
        super.setUp();
    }

	@Override
	protected void tearDown() throws Exception {
		//must be last statement in method
		super.tearDown();
		
	}
	/**
	 * Test starting the service
	 */
	@SmallTest
	public void testStartingService() throws Exception {
        Intent startIntent = new Intent();
        startIntent.setClass(getContext(), DifferentProcessService.class);
        startService(startIntent);
        assertNotNull(getService());
	}

}
