package org.societies.comm.xmpp.client.impl;

import org.societies.comm.xmpp.interfaces.CommCallback;
import org.societies.interfaces.Callback;

public class CallbackAdapter implements Callback {
	
	private CommCallback callback;
	
	public CallbackAdapter(CommCallback callback) {
		this.callback = callback;
	}
	
	@Override
	public void receiveResult(String xml) {
		// TODO Auto-generated method stub
		callback.receiveResult(null, null); // TODO
	}
	@Override
	public void receiveError(String xml) {
		callback.receiveError(null); //TODO
	}
}
