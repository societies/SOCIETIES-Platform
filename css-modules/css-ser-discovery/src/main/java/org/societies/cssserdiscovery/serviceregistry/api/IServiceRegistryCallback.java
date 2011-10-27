package org.societies.cssserdiscovery.serviceregistry.api;

import java.util.Collection;
import java.util.List;

public interface IServiceRegistryCallback {

	public void onRegisterService(Object serviceIdentifier, boolean result, String message);
	
	public void onUnregisterService(Object serviceIdentifier, boolean result, String message);
	
	public void onAddServiceList(boolean result, String message);

	public void onRemoveServiceList(boolean result, String message);
	
	public void onSyncServiceList(boolean result, String message);
	
	public void onGetService(Object service);
	
	public void onGetServiceList(List<Object> service);
	
	public void onShareService(boolean result, String message);
	
	public void onActivateService(boolean result, String message);	

}
