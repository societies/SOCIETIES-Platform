package org.societies.android.platform.devicestatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.societies.android.platform.interfaces.IDeviceStatus;
import org.societies.android.platform.interfaces.ServiceMethodTranslator;
import org.societies.android.platform.interfaces.model.LocationProviderStatus;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
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
	
	public DeviceStatusServiceDifferentProcess() {
		super();
		this.inMessenger = new Messenger(new IncomingHandler());
	}
	
	public class ExternalBinder extends Binder {
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
					Log.i(this.getClass().getSimpleName(), "Target method: " + targetMethod);
					
					Class parameters [] = ServiceMethodTranslator.getParameterClasses(targetMethod);
					for (Class element : parameters) {
						Log.i(this.getClass().getSimpleName(), "Target method param types: " + element.getName());
			
					}
								
					Method method = DeviceStatusServiceDifferentProcess.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(IDeviceStatus.methodsArray, message.what), parameters);
					Log.i(this.getClass().getSimpleName(), "Found method: " + method.getName());
					try {
						Object params [] = new Object [ServiceMethodTranslator.getParameterNumber(targetMethod)];
						Log.i(this.getClass().getSimpleName(),"Number of parameters: " + params.length); 

						String paramTypeList [] = ServiceMethodTranslator.getMethodParameterTypesCapitalised(targetMethod);

						String paramNameList [] = ServiceMethodTranslator.getMethodParameterNames(targetMethod);
						for (int i = 0; i < paramTypeList.length; i++) {
							Class bundleParam [] = {String.class};
							Object bundleValue [] = {paramNameList[i]};
							Method bundleMethod = Bundle.class.getMethod("get" + paramTypeList[i], bundleParam);
							
							params[i] = bundleMethod.invoke(message.getData(), bundleValue);
							Log.i(this.getClass().getSimpleName(), "parameter i = " + i + " value: " + params[i]);
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
	
	public boolean isInternetConnectivityOn(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "isInternetConnectivityOn called");
		// -- Create Data		
		ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		boolean isInternetConnectivityOn = (connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTED ||
				connectivity.getNetworkInfo(0).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTING ||
				connectivity.getNetworkInfo(1).getState() == NetworkInfo.State.CONNECTED);
		
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(CONNECTIVITY);
		intent.putExtra(INTENT_RETURN_KEY, isInternetConnectivityOn);
		// Intentionally restricting potential intent receiver to client 
		intent.setPackage(callerPackageName);
		this.sendBroadcast(intent);
		
		return isInternetConnectivityOn;
	}

	public List<?> getLocationProvidersStatus(String callerPackageName) {
		Log.i(this.getClass().getSimpleName(), "getLocationProvidersStatus called");
		// -- Create Data
		ArrayList<LocationProviderStatus> locationProviders = new ArrayList<LocationProviderStatus>();
		LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		List<String> providers = locationManager.getAllProviders();
		for (String provider : providers) {
			locationProviders.add(new LocationProviderStatus(provider, locationManager.isProviderEnabled(provider)));
			
		}
		
		// -- Create intent to broadcast results to interested receivers
		Intent intent = new Intent(LOCATION_STATUS);
		intent.putExtra(INTENT_RETURN_TYPE, "org.societies.android.platform.interfaces.model.LocationProviderStatus");
		intent.putParcelableArrayListExtra(INTENT_RETURN_KEY, locationProviders);
		// Intentionally restricting potential intent receiver to client 
		intent.setPackage(callerPackageName);
		// Send
		this.sendBroadcast(intent);
		
		return locationProviders;
	}
}
