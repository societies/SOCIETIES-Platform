package org.societies.platform.servicelifecycle.serviceRegistry;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.ServiceResourceIdentifier;

public class ServiceRegistryTest {
	//static private Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
	
	public ServiceRegistryTest() {}
	
	@Test
	public void testServiceRegistry() {
		ServiceRegistry sr = new ServiceRegistry();
		
		if (sr != null) {
			RegistryEntry re1 = new RegistryEntry();
			RegistryEntry re2 = new RegistryEntry();
			ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
			
			List<RegistryEntry> temp = new ArrayList<RegistryEntry>();
			
			sri.setHash("hash");
			sri.setLifetime(100);
			/* try {
				sri.setIdentifier(new URI("http://id1"));
			} catch (URISyntaxException e) {
				e.printStackTrace();
			} */
			
			re1.setVersion("0.0.1");
			re1.setOrganizationId("organization1");
			re1.setServiceIdentifier(sri);
			
			re2.setVersion("0.0.2");
			re2.setOrganizationId("organization2");
			re2.setServiceIdentifier(sri);
			
			temp.add(re1);
			temp.add(re2);
			
			sr.registerServiceList(temp);
			
	//		log.info("Entry added to Service Registry");
			
			assertTrue(true);
		}
		else
			fail("Cannot create service registry");
	}

}
