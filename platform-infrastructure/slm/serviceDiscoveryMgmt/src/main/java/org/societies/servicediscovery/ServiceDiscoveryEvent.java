package org.societies.servicediscovery;


import java.util.HashMap;
import java.util.Map;

public class ServiceDiscoveryEvent extends ServiceDiscoveryAbstractEvent {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public ServiceDiscoveryEvent(String eventId, Object eventContext) {
		super(eventId, eventContext);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public ServiceDiscoveryEvent(String eventId, SomeCustom user, String tPassword) {
		super(eventId, new HashMap());
		Map<String, Object> params = (Map<String, Object>) this.getEventContext();
		params.put("user", user);
		params.put("tPassword", tPassword);
	}

}