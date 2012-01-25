package org.societies.servicediscovery;


import java.util.Map;

import org.springframework.context.ApplicationEvent;

public abstract class ServiceDiscoveryAbstractEvent extends ApplicationEvent {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String eventId;
	protected Object eventContext;

	public ServiceDiscoveryAbstractEvent(String eventId, Object eventContext) {
		super(eventId);
		this.eventId = eventId;
		this.eventContext = eventContext;
	}

	public String getEventId() {
		return eventId;
	}

	public Object getEventContext() {
		return eventContext;
	}

	@SuppressWarnings("rawtypes")
	public Object getContextParams(String paramName) {
		if (!(eventContext != null && eventContext instanceof Map)) {
			return null;
		}
		return ((Map) eventContext).get(paramName);
	}
}