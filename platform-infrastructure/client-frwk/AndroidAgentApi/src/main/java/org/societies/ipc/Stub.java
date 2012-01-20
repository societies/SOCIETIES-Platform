package org.societies.ipc;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import org.societies.interfaces.Callback;

import android.os.Messenger;

public class Stub implements InvocationHandler {
	private Client client;
	

	public static Object newInstance(Class<?>[] interfaces, String id, Messenger messenger) {
		return Proxy.newProxyInstance(Stub.class.getClassLoader(), 
				interfaces, new Stub(id, messenger));
	}

	private Stub(String id, Messenger messenger) {
		client = new Client(id, messenger);
	}

	public Object invoke(Object proxy, Method m, Object[] args)
			throws Throwable {
		CallbackArgFilterer caf = new CallbackArgFilterer(args);
		
		if(caf.hasCallback())
			client.invoke(m.getName(), caf.filteredArgs, caf.callback);
		else
			client.invoke(m.getName(), args);
		return null;
	}
	
	private static class CallbackArgFilterer {

		public final Object[] filteredArgs;
		public final Object callback;
		
		public CallbackArgFilterer(Object[] args) {
			int i = args.length - 1;
			if(args[i] instanceof Callback) {
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
