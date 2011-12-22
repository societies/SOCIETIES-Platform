package org.societies.android.platform.servicemonitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.societies.android.platform.interfaces.ICoreServiceMonitor;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;

import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;
import android.widget.Toast;

public class CoreServiceMonitorDifferentProcess extends Service implements ICoreServiceMonitor {

	private Messenger inMessenger;
	
	public CoreServiceMonitorDifferentProcess() {
		super();
		this.inMessenger = new Messenger(new IncomingHandler());
	}
	
	class IncomingHandler extends Handler {
		
		@Override
		public void handleMessage(Message message) {
			String targetMethod = ServiceMethodTranslator.getMethodSignature(ICoreServiceMonitor.methodsArray, message.what);
			
			if (targetMethod != null) {
				try {
					Log.d(this.getClass().getName(), "Target method: " + targetMethod);
					
					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.d(this.getClass().getName(), "Target method param types: " + element.getName());
			
					}
								
					Method method = CoreServiceMonitorDifferentProcess.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, message.what), parameters);
					Log.d(this.getClass().getName(), "Found method: " + method.getName());
					try {
						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
						Log.d(this.getClass().getName(),"Number of parameters: " + params.length); 

						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
						for (int i = 0; i < paramTypeList.length; i++) {
							Class bundleParam [] = {String.class};
							Object bundleValue [] = {paramNameList[i]};
							Method bundleMethod = Bundle.class.getMethod("get" + paramTypeList[i], bundleParam);
							
							params[i] = bundleMethod.invoke(message.getData(), bundleValue);
							Log.d(this.getClass().getName(), "parameter i = " + i + " value: " + params[i]);
						}
						method.invoke(CoreServiceMonitorDifferentProcess.this, params);
					} catch (IllegalArgumentException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IllegalAccessException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (InvocationTargetException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (SecurityException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NoSuchMethodException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}

	@Override
	public String getGreeting() {
		Toast.makeText(getApplicationContext(), "Bugger off", Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public String getGreeting(String appendToMessage) {
		Toast.makeText(getApplicationContext(), ("Bugger off " + appendToMessage), Toast.LENGTH_LONG).show();
		return null;
	}

	@Override
	public String getNumberGreeting(String appendToMessage, int index) {
		Toast.makeText(getApplicationContext(), ("Bugger off " + appendToMessage + " index: " + index), Toast.LENGTH_LONG).show();
		return null;
	}

}
