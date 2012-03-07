package org.societies.servicediscovery.api;

import org.springframework.context.ApplicationEvent;

public interface IServiceDiscoverPublisher {

	public abstract void multicastEvent(ApplicationEvent event); 

	public abstract void removeAllListeners();

}