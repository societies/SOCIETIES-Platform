package org.societies.android.platform.servicemonitor.test;

import org.societies.android.platform.servicemonitor.AnotherStartedService;
import org.societies.android.platform.servicemonitor.SameProcessService;

import android.content.Intent;
import android.os.IBinder;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.SmallTest;

public class TestSameProcessService extends ServiceTestCase<SameProcessService>{



		public TestSameProcessService() {
			super(SameProcessService.class);
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
	        startIntent.setClass(getContext(), SameProcessService.class);
	        IBinder service = bindService(startIntent);
	    }

}
