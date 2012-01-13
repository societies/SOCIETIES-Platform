package org.societies.android.platform.servicemonitor;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.societies.android.platform.interfaces.ServiceMethodTranslator;
import org.societies.android.platform.interfaces.ICoreServiceMonitor;

import android.app.ActivityManager;
import android.app.Service;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

public class CoreMonitor extends Service implements ICoreServiceMonitor {
	private Messenger inMessenger;
	private static final int MAX_SERVICES = 30;
	private static final int MAX_TASKS = 30;
	
	public static final String ACTIVE_SERVICES = "org.societies.android.platform.servicemonitor.ACTIVE_SERVICES";
	public static final String ACTIVE_TASKS = "org.societies.android.platform.servicemonitor.ACTIVE_TASKS";
	public static final String INTENT_RETURN_KEY = "org.societies.android.platform.servicemonitor.ReturnValue";
	
	@Override
	public void onCreate () {
		this.inMessenger = new Messenger(new IncomingHandler());
		Log.i(this.getClass().getName(), "Service starting");

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
								
					Method method = CoreMonitor.this.getClass().getMethod(ServiceMethodTranslator.getMethodName(ICoreServiceMonitor.methodsArray, message.what), parameters);
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
						method.invoke(CoreMonitor.this, params);
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
	public void onDestroy() {
		Log.i(this.getClass().getName(), "Service terminating");
	}
	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningServiceInfo> activeServices(String client) {
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		/**
		 * This can also be achieved using the internal IActivityManager interface and ActivityManagerNative class
		 * IActivityManager manager = ActivityManagerNative.getDefault();
		 * manager.getTasks
		 * 
		 * but it requires access to hidden and internal components
		 */
		List <ActivityManager.RunningServiceInfo> runningServices = manager.getRunningServices(MAX_SERVICES);

		/**
		 * Create intent to broadcast results to interested receivers
		 */
		Intent intent = new Intent(ACTIVE_SERVICES);
		int i = 0;
		ActivityManager.RunningServiceInfo arrayServices [] = new ActivityManager.RunningServiceInfo[runningServices.size()];
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			arrayServices[i] = service;
			i++;
		}
		intent.putExtra(INTENT_RETURN_KEY, arrayServices);
		/**
		 * Intentionally restricting potential intent receiver to client 
		 */
		intent.setPackage(client);
		Log.d(this.getClass().getName(), "Intent package name: " + client);
		this.sendBroadcast(intent);
		
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			
			Log.d(this.getClass().getName(), "Service name: " + service.service.flattenToString());
		}
		
		return runningServices;
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningServiceInfo> activeServices(String client, String serviceFilter) {
		List <ActivityManager.RunningServiceInfo> filteredServices = new ArrayList<ActivityManager.RunningServiceInfo>();
		List <ActivityManager.RunningServiceInfo> runningServices = this.activeServices(client);
		for (ActivityManager.RunningServiceInfo service : runningServices) {
			if (service.service.flattenToString().contains(serviceFilter)) {
				filteredServices.add(service);
			}
		}
		return filteredServices; 
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no tasks found
	 */
	public List<ActivityManager.RunningTaskInfo> activeTasks(String client) {
		ActivityManager manager = (ActivityManager) this.getSystemService(ACTIVITY_SERVICE);
		List <ActivityManager.RunningTaskInfo> runningTasks = manager.getRunningTasks(MAX_TASKS);		
		
		/**
		 * Create intent to broadcast results to interested receivers
		 */
		Intent intent = new Intent(ACTIVE_TASKS);
		int i = 0;
		ActivityManager.RunningTaskInfo arrayTasks [] = new ActivityManager.RunningTaskInfo[runningTasks.size()];
		
		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			arrayTasks[i] = task;
			i++;
		}

		
		intent.putExtra(INTENT_RETURN_KEY, arrayTasks);
		this.sendBroadcast(intent);

		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			Log.d(this.getClass().getName(), "Task name: " + task.baseActivity.flattenToString());
		}
		return runningTasks;
	}

	/**
	 * {@link ICoreServiceMonitor}
	 * 
	 * @return List - maybe empty if no services found
	 */
	public List<ActivityManager.RunningTaskInfo> activeTasks(String client, String taskFilter) {
		List <ActivityManager.RunningTaskInfo> filteredTasks = new ArrayList<ActivityManager.RunningTaskInfo>();
		List <ActivityManager.RunningTaskInfo> runningTasks = this.activeTasks(client);
		for (ActivityManager.RunningTaskInfo task : runningTasks) {
			if (task.baseActivity.flattenToString().contains(taskFilter)) {
				filteredTasks.add(task);
			}
		}
		return filteredTasks; 
	}

	public boolean startActivity(String client, String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean startService(String client, String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopActivity(String client, String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean stopService(String client, String arg0) {
		// TODO Auto-generated method stub
		return false;
	}

		


}
