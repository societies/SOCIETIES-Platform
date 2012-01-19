package org.societies.android.platform.devicestatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import org.societies.android.platform.interfaces.IDeviceStatus;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Binder;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.MessageQueue.IdleHandler;
import android.util.Log;

public class DeviceStatusServiceDifferentProcess extends Service implements IDeviceStatus {
	private Messenger inMessenger;
	
	public static final String CONNECTIVITY = "org.societies.android.platform.devicestatus.CONNECTIVITY";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.devicestatus.ReturnValue";
	
	public DeviceStatusServiceDifferentProcess() {
		super();
		this.inMessenger = new Messenger(new IncomingHandler());
	}
	
	public class LocalBinder extends Binder {
		DeviceStatusServiceDifferentProcess getService() {
			return DeviceStatusServiceDifferentProcess.this;
		}
	}
	
	@Override
	public IBinder onBind(Intent arg0) {
		return inMessenger.getBinder();
	}
	
	class IncomingHandler extends Handler {
		@Override
		public void handleMessage(Message message) {
			String targetMethod = ServiceMethodTranslator.getMethodSignature(IDeviceStatus.methodsArray, message.what);
			
			if (targetMethod != null) {
				try {
					Log.d(this.getClass().getName(), "Target method: " + targetMethod);
					
					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.d(this.getClass().getName(), "Target method param types: " + element.getName());
			
					}
								
					Method method = DeviceStatusServiceDifferentProcess.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(IDeviceStatus.methodsArray, message.what), parameters);
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
						method.invoke(DeviceStatusServiceDifferentProcess.this, params);
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
	
	public boolean isInternetConnectivityOn() {
		// Create Data		
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isInternetConnectivityOn = (connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED);
		
		// Create intent to broadcast results to interested receivers
		Intent intent = new Intent(CONNECTIVITY);
		intent.putExtra(INTENT_RETURN_KEY, isInternetConnectivityOn);
//		/**
//		 * Intentionally restricting potential intent receiver to client 
//		 */
//		intent.setPackage(client);
//		Log.d(this.getClass().getName(), "Intent package name: " + client);
		this.sendBroadcast(intent);
		
		return isInternetConnectivityOn;
	}

	public List<?> getLocationProvidersStatus() {
		// TODO Auto-generated method stub
		return null;
	}
}
