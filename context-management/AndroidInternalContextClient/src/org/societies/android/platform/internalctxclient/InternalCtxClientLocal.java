package org.societies.android.platform.internalctxclient;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
 */

public class InternalCtxClientLocal extends Service {

	 private static final String LOG_TAG = InternalCtxClientLocal.class.getName();
	    private IBinder binder = null;
	    
	    @Override
		public void onCreate () {
			this.binder = new LocalBinder();
			Log.d(LOG_TAG, "InternalCtxClientLocal service starting");
		}

		@Override
		public void onDestroy() {
			Log.d(LOG_TAG, "InternalCtxClientLocal service terminating");
		}

		/**Create Binder object for local service invocation */
		public class LocalBinder extends Binder {
			public InternalCtxClientBase getService() {
				return new InternalCtxClientBase(InternalCtxClientLocal.this.getApplicationContext());
			}
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return this.binder;
		}
}
