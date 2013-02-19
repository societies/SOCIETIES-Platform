package org.societies.comm.android.ipc;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.Parcel;
import android.os.Parcelable;

class MessageMethodInvocation {
	
	private static final String KEY_ID = "id",
								KEY_CALLER = "caller",
								KEY_NAME = "name",
								KEY_PARAMS = "params",
								KEY_CALLBACK = "callback";
	public static final int WHAT = 1;
	
	private String id;
	private String callerId;
	private String name;
	private Object[] params;
	private Class<?> callback = null;
	private Message msg;	


	
	public MessageMethodInvocation(String callerId, String methodName, Object[] params, Messenger replyTo) {		
		id = UUID.randomUUID().toString();
		this.callerId = callerId;
		name = methodName;
		this.params = params;
		Bundle data = new Bundle();
		data.putString(KEY_ID, id);
		data.putString(KEY_CALLER, callerId);
		data.putString(KEY_NAME, methodName);
		data.putParcelable(KEY_PARAMS, new Params(params));
		msg = Message.obtain(null, WHAT);
		msg.replyTo = replyTo;
		msg.setData(data);
	}
	
	public MessageMethodInvocation(String callerId, String methodName, Object[] params, Class<?> callbackInterface, Messenger replyTo) {
		this(callerId, methodName, params, replyTo);
		this.callback = callbackInterface;
		msg.getData().putSerializable(KEY_CALLBACK, callbackInterface);
	}
	
	public MessageMethodInvocation(Message message) {
		msg = message;
		Bundle data = message.getData();
		data.setClassLoader(MessageMethodInvocation.class.getClassLoader());
		id = data.getString(KEY_ID);
		callerId = data.getString(KEY_CALLER);
		name = data.getString(KEY_NAME);
		params = ((Params)data.getParcelable(KEY_PARAMS)).params();
		if(data.containsKey(KEY_CALLBACK))
			callback = (Class<?>)data.getSerializable(KEY_CALLBACK);
	}
	
	public String id() {
		return id;
	}
	
	public String callerId() {
		return callerId;
	}
	
	public String name() {
		return name;		
	}
	

	public Class<?>[] parameterTypes() {
		List<Class<?>> rv = new ArrayList<Class<?>>();
		for(int i=0; i<params.length; i++) {			
			if(params[i] == null)
				rv.add(null);
			else
				rv.add(params[i].getClass());
		}
		
		if(hasCallback())
			rv.add(callback);
		return rv.toArray(new Class<?>[0]);
	}
	
	public Object[] params() {
		return params;
	}
	
	public Object[] params(String callerId, Messenger replyTo) {
		List<Object> rv = new ArrayList<Object>(params.length+1);
		rv.addAll(Arrays.asList(params));
		rv.add(Stub.newInstance(callback.getInterfaces(), callerId, replyTo));
		return rv.toArray();
	}
	
	public boolean hasCallback() {
		return callback != null;
	}
	
	public Class<?> callbackInterface() {
		return callback;
	}
	
	public Message message() {
		return msg;
	}
	
    public Method getMethod(Class<?> target) throws NoSuchMethodException {
    	Class<?>[] methodParameterTypes = parameterTypes();
    	for (Method method : target.getMethods()) {
    		Class<?>[] parameterTypes = method.getParameterTypes();
    		if (!method.getName().equals(name) || parameterTypes.length != methodParameterTypes.length) {
    			continue;
    		}
    		boolean matches = true;
    		for (int i = 0; i < parameterTypes.length; i++) {
    			if (methodParameterTypes[i] != null && !parameterTypes[i].isAssignableFrom(methodParameterTypes[i])) {
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
	
	private static class Params implements Parcelable {
		
		private Object[] params;
		
		public Params(Object[] params) {
			this.params = params;
		}
		
		public Object[] params() {
			return params;
		}

	    public int describeContents() {
	        return 0;
	    }

	    public void writeToParcel(Parcel out, int flags) {
	        out.writeArray(params);
	    }

	    public static final Parcelable.Creator<Params> CREATOR
	            = new Parcelable.Creator<Params>() {
	        public Params createFromParcel(Parcel in) {
	            return new Params(in);
	        }

	        public Params[] newArray(int size) {
	            return new Params[size];
	        }
	    };
	     
	    private Params(Parcel in) {
	        params = in.readArray(Params.class.getClassLoader());
	    }
	}


}
