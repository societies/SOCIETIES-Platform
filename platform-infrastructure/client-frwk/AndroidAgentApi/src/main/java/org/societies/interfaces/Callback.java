package org.societies.interfaces;

import org.societies.comm.android.ipc.ICallback;

public interface Callback extends ICallback {
	void receiveResult(String xml);

	void receiveError(String xml); 
	
	void receiveMessage(String xml);
}
