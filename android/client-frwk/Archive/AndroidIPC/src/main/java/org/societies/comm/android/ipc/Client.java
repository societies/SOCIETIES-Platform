package org.societies.comm.android.ipc;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;


import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

class Client {
	
	private Messenger outMessenger, inMessenger;
	private String id;
	private Map<String, Object> callbacks = new HashMap<String, Object>();
	private Object mutex = new Object();
	private Object methodReturn;
	private boolean hasThrowable = false;
	private Throwable throwable;
	private static final String LOG_TAG = Client.class.getName();

	
	private class IncomingHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
        	MessageMethodResult result;
            switch (msg.what) {
            	case MessageMethodInvocation.WHAT:
            		MessageMethodInvocation msgMethod = new MessageMethodInvocation(msg);
            		Object callback = callbacks.get(msgMethod.callerId());            		
					try {						
						Method method = msgMethod.getMethod(callback.getClass());
	            		Object rv = method.invoke(callback, msgMethod.params());
	            		result = new MessageMethodResult(msgMethod, rv);
                	} catch(Exception e) {
                		result = new MessageMethodResult(msgMethod, e);
                	}
					try {
                		msg.replyTo.send(result.message());   
					} catch (Exception e) {
						Log.e(LOG_TAG, e.getMessage(), e);
					}
            		break;  
            	case MessageMethodResult.WHAT:
                	result = new MessageMethodResult(msg);
                	synchronized(mutex) {
                		if(result.hasThrowable()) {
                			hasThrowable = true;
                			throwable = result.throwable();
                		}
                		else {
                			hasThrowable = false;
                			methodReturn = result.returnValue();
                		}               			

                		mutex.notify();
                	}
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	public Client(String id, Messenger messenger) {
		outMessenger = messenger;
		inMessenger =  new MessengerThreadFactory() {
			@Override
			protected Handler createHandler() {
				return new IncomingHandler();
			}			
		}.createMessengerThread();
		this.id = id;
	}
	
	public Object invoke(String name, Object[] params) throws Throwable{
		
		Message msg = (new MessageMethodInvocation(id, name, params, inMessenger)).message();			
	
		waitMethodReturn(msg);
		
		return methodReturn;
	}
	
	public Object invoke(String name, Object[] params, Object callback) throws Throwable{	
		MessageMethodInvocation msg = new MessageMethodInvocation(id, name, params, callback.getClass(), inMessenger);					

		callbacks.put(msg.id(), callback);		
		
		waitMethodReturn(msg.message());		
		
		return methodReturn;
	}
	
	private void waitMethodReturn(Message msg) throws Throwable {
		synchronized(mutex){
			outMessenger.send(msg);
			mutex.wait(); 
		}
		if(hasThrowable)
			throw throwable;
	}
}
