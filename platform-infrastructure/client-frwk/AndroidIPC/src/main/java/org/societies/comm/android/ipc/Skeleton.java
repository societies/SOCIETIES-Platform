package org.societies.comm.android.ipc;

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
                	
                	MessageMethodResult result;
                	try {
                		Method method = getMethod(object.getClass(), msgMethod.name(), msgMethod.parameterTypes());
                		Object rv;
	                	if(msgMethod.hasCallback()) {	                		
	                		rv = method.invoke(object, msgMethod.params(msgMethod.id(), msg.replyTo));
	                	}
	                	else {	                		
	                		rv = method.invoke(object, msgMethod.params());
	                	}             	    
	                	result = new MessageMethodResult(msgMethod, rv);
                	} catch(Exception e) {
                		result = new MessageMethodResult(msgMethod, e);
                	}
                	try {
                		msg.replyTo.send(result.message());                		
                	} catch(Exception e) {
                		Log.e(TAG, e.getMessage(), e);
                	}  
                    break;                    
                default:
                    super.handleMessage(msg);
            }
        }
        
        private Method getMethod(Class<?> target, String methodName, Class<?>[] methodParameterTypes) throws NoSuchMethodException {
        	for (Method method : target.getMethods()) {
        		Class<?>[] parameterTypes = method.getParameterTypes();
        		if (!method.getName().equals(methodName) || parameterTypes.length != methodParameterTypes.length) {
        			continue;
        		}
        		boolean matches = true;
        		for (int i = 0; i < parameterTypes.length; i++) {
        			if (!parameterTypes[i].isAssignableFrom(methodParameterTypes[i])) {
        				matches = false;
        				break;
        			}
        		}
        		if (matches) {
        			return method;
        		}
        	}
        	throw new NoSuchMethodException();
        }
	}
	
	private Object object;
    private Messenger messenger;
    
    public Skeleton(Object object) throws InterruptedException {
    	this.object = object;
    	messenger =  new MessengerThreadFactory() {
			@Override
			protected Handler createHandler() {
				return new IncomingHandler();
			}			
		}.createMessengerThread();
//    	messenger = new Messenger(new MessengerThreadFactory().createThread(new IHandlerFactory() {
//			public Handler createHandler() {
//				return new IncomingHandler();
//			}	    		
//    	}));
    }
    
    public Messenger messenger() {
    	return messenger;
    }
}
