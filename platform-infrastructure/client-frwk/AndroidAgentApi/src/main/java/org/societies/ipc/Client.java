package org.societies.ipc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.interfaces.Callback;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;

class Client {
	
	private static final Logger log = LoggerFactory.getLogger(Client.class);
	
	private Messenger outMessenger, inMessenger;
	private String id;
	private Map<String, Object> callbacks = new HashMap<String, Object>();
	
	private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
            	case MessageMethodInvocation.WHAT:
            		MessageMethodInvocation msgMethod = new MessageMethodInvocation(msg);
            		Object callback = callbacks.get(msgMethod.callerId());
					try {
						Method method = callback.getClass().getMethod(msgMethod.name(), msgMethod.parameterTypes());
	            		method.invoke(callback, msgMethod.params());
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
            		break;       
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	public Client(String id, Messenger messenger) {
		outMessenger = messenger;
		inMessenger = new Messenger(new IncomingHandler());
		this.id = id;
	}
	
	public void invoke(String name, Object[] params) throws Throwable{
		
		Message msg = (new MessageMethodInvocation(id, name, params, inMessenger)).message();			

		outMessenger.send(msg);

	}
	
	public void invoke(String name, Object[] params, Object callback) throws Throwable{		
		MessageMethodInvocation msg = new MessageMethodInvocation(id, name, params, Callback.class, inMessenger);					

		callbacks.put(msg.id(), callback);
		
		outMessenger.send(msg.message());
	}
}
