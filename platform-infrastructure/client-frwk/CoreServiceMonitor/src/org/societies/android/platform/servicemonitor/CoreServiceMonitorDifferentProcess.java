package org.societies.android.platform.servicemonitor;

import org.societies.android.platform.interfaces.CoreMessages;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.widget.Toast;

public class CoreServiceMonitorDifferentProcess extends Service {

	private Messenger inMessenger;
	
	public CoreServiceMonitorDifferentProcess() {
		super();
		this.inMessenger = new Messenger(new IncomingHandler());
	}
	
	class IncomingHandler extends Handler {
		
		@Override
		public void handleMessage(Message message) {
			
			
			switch (message.what) {
			case CoreMessages.MESSAGE_HELLO:
				Toast.makeText(getApplicationContext(), "Bugger off", Toast.LENGTH_LONG).show();
				break;

			default:
				super.handleMessage(message);
				break;
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}

}
