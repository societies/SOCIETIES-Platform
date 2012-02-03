package org.societies.android.platform.contentprovidermonitor.test;

import org.societies.clientframework.contentprovider.services.DifferentProcessService;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;

public class TestSameProcessService extends ServiceTestCase<DifferentProcessService>{



		public TestSameProcessService() {
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
		 * Test starting the bound service
		 */
	    public void testBindable() {
	        Intent startIntent = new Intent();
	        startIntent.setClass(getContext(), DifferentProcessService.class);
	        IBinder service = bindService(startIntent);
	    }

}
