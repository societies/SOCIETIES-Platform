package org.societies.android.platform.ctxclient;

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

public class CtxClientLocal extends Service {

	 private static final String LOG_TAG = CtxClientLocal.class.getName();
	    private IBinder binder = null;
	    
	    @Override
		public void onCreate () {
			this.binder = new LocalBinder();
			Log.d(LOG_TAG, "CtxClientLocal service starting");
		}

		@Override
		public void onDestroy() {
			Log.d(LOG_TAG, "CtxClientLocal service terminating");
		}

		/**Create Binder object for local service invocation */
		public class LocalBinder extends Binder {
			public CtxClientBase getService() {
				return new CtxClientBase(CtxClientLocal.this.getApplicationContext());
			}
		}

		@Override
		public IBinder onBind(Intent arg0) {
			return this.binder;
		}
}
