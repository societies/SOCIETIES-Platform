package org.societies.servicediscovery;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationListener;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class MainRunner {

	private static final Logger logger = LoggerFactory.getLogger(MainRunner.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		// TODO Auto-generated method stub		
		logger.info("Testing...........");
		
		ApplicationContext ctx = new ClassPathXmlApplicationContext("/META-INF/spring/bundle-context.xml");
		
		ServiceDiscoveryMulticaster eventMulticast= (ServiceDiscoveryMulticaster) ctx.getBean("serviceDiscMulticast");
		
		SomeCustom customObject=new SomeCustom();
		
		customObject.setUserId("some user id: User_A");
		
		ApplicationListener<ServiceDiscoveryEvent> appListener = null;
		
		appListener = new ServiceDiscoveryListener();	 	
		//registering event listener
		eventMulticast.addApplicationListener(appListener);	
		//publishing event
		eventMulticast.multicastEvent(new ServiceDiscoveryEvent("some string", customObject, "some more string"));
		//un-registering event
		eventMulticast.removeApplicationListener(appListener);
		//publishing event
		eventMulticast.multicastEvent(new ServiceDiscoveryEvent("Event_RegisterUser",customObject, "mySecretpass"));		
	}

}
