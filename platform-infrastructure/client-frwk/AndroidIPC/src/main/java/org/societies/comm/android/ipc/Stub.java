package org.societies.comm.android.ipc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import android.os.Messenger;

public class Stub implements InvocationHandler {
	private Client client;
	

	public static Object newInstance(Class<?>[] interfaces, String id, Messenger messenger) throws IllegalArgumentException {
		return Proxy.newProxyInstance(Stub.class.getClassLoader(), 
				interfaces, new Stub(id, messenger));
	}
	
	public static Object newInstance(Class<?>[] interfaces, Messenger messenger) throws IllegalArgumentException {
		return Stub.newInstance(interfaces, "0", messenger);
	}

	private Stub(String id, Messenger messenger) {
		client = new Client(id, messenger);
	}

	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		Object rv;
		CallbackArgFilterer caf = new CallbackArgFilterer(args);
		
		if(caf.hasCallback())
			rv = client.invoke(m.getName(), caf.filteredArgs, caf.callback);
		else
			rv = client.invoke(m.getName(), args);
		return rv;
	}
	
	private static class CallbackArgFilterer {

		public final Object[] filteredArgs;
		public final Object callback;
		
		public CallbackArgFilterer(Object[] args) {
			int i = args.length - 1;
			if(i != -1 && args[i] instanceof ICallback) {
				filteredArgs = removeIndexFromArray(args, i);
				callback = args[i];
			}
			else {
				filteredArgs = args;
				callback = null;
			}			
		}
		
		public boolean hasCallback() {
			return callback != null;
		}
		
		private static Object[] removeIndexFromArray(Object[] args, int index) {
			Object[] rv = new Object[args.length-1];
			for(int i=0; i<index; i++)
				rv[i] = args[i];
			for(int i=index+1; i<args.length; i++)
				rv[i-1] = args[i];
			return rv;
		}
		
	}

}
