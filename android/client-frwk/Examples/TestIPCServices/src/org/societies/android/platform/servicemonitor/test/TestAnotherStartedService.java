package org.societies.android.platform.servicemonitor.test;

import org.societies.android.platform.servicemonitor.AnotherStartedService;
import org.societies.android.platform.servicemonitor.StartedService;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class TestAnotherStartedService extends ServiceTestCase<AnotherStartedService>{


	public TestAnotherStartedService() {
		super(AnotherStartedService.class);
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
        startIntent.setClass(getContext(), AnotherStartedService.class);
        startService(startIntent);
        assertNotNull(getService());
	}

}
