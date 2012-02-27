package org.societies.servicediscovery.api;

import org.springframework.context.ApplicationListener;

public interface IServiceDiscoveryEvents {
	
	@SuppressWarnings("rawtypes")
	public abstract void addApplicationListener(ApplicationListener listener);

	@SuppressWarnings("rawtypes")
	public abstract void removeApplicationListener(ApplicationListener listener);

	public abstract void addApplicationListenerBean(String listenerBean);

	public abstract void removeApplicationListenerBean(String listenerBean);

}
