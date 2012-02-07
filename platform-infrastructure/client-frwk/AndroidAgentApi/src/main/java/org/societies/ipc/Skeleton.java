package org.societies.ipc;

import java.lang.reflect.Method;

import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class Skeleton {
	
	private static final String TAG =  Skeleton.class.getSimpleName();
	
	private class IncomingHandler extends Handler {		
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MessageMethodInvocation.WHAT:
                	MessageMethodInvocation msgMethod = new MessageMethodInvocation(msg);
                	try {
                		Method method = object.getClass().getMethod(msgMethod.name(), msgMethod.parameterTypes());
	                	
	                	if(msgMethod.hasCallback()) {	                		
	                		method.invoke(object, msgMethod.params(msgMethod.id(), msg.replyTo));
	                	}
	                	else {	                		
	                		method.invoke(object, msgMethod.params());
	                	}             	               		
                	} catch(Exception e) {
                		Log.e(TAG, e.getMessage(), e);
                	}           	
                    break;
                default:
                    super.handleMessage(msg);
            }
        }
    }
	
	private Object object;

    public final Messenger messenger = new Messenger(new IncomingHandler());
    
    public Skeleton(Object object) {
    	this.object = object;
    }    
}
