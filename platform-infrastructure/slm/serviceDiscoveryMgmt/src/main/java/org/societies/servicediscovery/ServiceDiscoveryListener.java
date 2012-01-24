package org.societies.servicediscovery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class ServiceDiscoveryListener implements ApplicationListener<ServiceDiscoveryEvent> {

	Logger logger = LoggerFactory.getLogger(ServiceDiscoveryListener.class);

	public void onApplicationEvent(ServiceDiscoveryEvent event) {

		String eventId = event.getEventId();
		SomeCustom user = (SomeCustom) event.getContextParams("user");
		String tempPassword = (String) event.getContextParams("tPassword");
		logger.info(user.getUserId());
		System.out.println("from UserEventListener eventId " + eventId);
		System.out.println("from UserEventListener tempPassword " + tempPassword);
		System.out.println("from UserEventListener user id " + user.getUserId());
	}
	
	
}