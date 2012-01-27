package org.societies.comm.xmpp.client.impl;

import org.societies.comm.xmpp.interfaces.ICommCallback;
import org.societies.interfaces.Callback;

import android.content.Context;
import android.content.ServiceConnection;

public class CallbackAdapter implements Callback {
	
	private ICommCallback callback;
	private Context context;
	private ServiceConnection service;
	
	public CallbackAdapter(ICommCallback callback, Context context, ServiceConnection service) {
		this.callback = callback;
		this.context = context;
		this.service = service;
	}
	
	@Override
	public void receiveResult(String xml) {
		unbindService();
		callback.receiveResult(null, null); // TODO
	}
	@Override
	public void receiveError(String xml) {
		unbindService();
		callback.receiveError(null, null); //TODO
	}
	
	private void unbindService() {
		context.unbindService(service);
	}
}
