package org.societies.servicediscovery;


import org.societies.servicediscovery.api.IServiceDiscoverPublisher;
import org.societies.servicediscovery.api.IServiceDiscoveryEvents;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ApplicationEventMulticaster;

public class ServiceDiscoveryMulticaster implements ApplicationEventMulticaster, 
				IServiceDiscoveryEvents, IServiceDiscoverPublisher{

private ApplicationEventMulticaster multicaster;
	
	
	public ApplicationEventMulticaster getMulticaster() {
		return multicaster;
	}

	public void setMulticaster(ApplicationEventMulticaster multicaster) {
		this.multicaster = multicaster;
	}

	@SuppressWarnings("rawtypes")
	public void addApplicationListener(ApplicationListener listener) {
		getMulticaster().addApplicationListener(listener);
	}

	public void multicastEvent(ApplicationEvent event) {
		getMulticaster().multicastEvent(event);		
	}

	public void removeAllListeners() {
		getMulticaster().removeAllListeners();
	}

	@SuppressWarnings("rawtypes")
	public void removeApplicationListener(ApplicationListener listener) {
		getMulticaster().removeApplicationListener(listener);
		
	}

	public void addApplicationListenerBean(String listenerBean) {
		getMulticaster().addApplicationListenerBean(listenerBean);
		
	}

	public void removeApplicationListenerBean(String listenerBean) {
		getMulticaster().removeApplicationListenerBean(listenerBean);
		
	}
	
}