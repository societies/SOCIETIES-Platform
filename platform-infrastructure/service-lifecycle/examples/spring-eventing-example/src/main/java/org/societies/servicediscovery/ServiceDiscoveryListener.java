package org.societies.servicediscovery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationListener;

public class ServiceDiscoveryListener implements ApplicationListener<ServiceDiscoveryEvent> {

	Logger logger = LoggerFactory.getLogger(ServiceDiscoveryListener.class);

	public void onApplicationEvent(ServiceDiscoveryEvent event) {

		String eventId = event.getEventId();
		SomeCustom someObj = (SomeCustom) event.getContextParams("objectname");
		String tempPassword = (String) event.getContextParams("objectName1");
		logger.info(someObj.getUserId());
		logger.info("event received");
	}	
}