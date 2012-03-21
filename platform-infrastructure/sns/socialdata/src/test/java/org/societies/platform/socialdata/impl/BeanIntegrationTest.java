package org.societies.platform.socialdata.impl;

import org.societies.platform.socialdata.SocialData;


/**
 * Local integration test (outside of OSGi).
 * @see BeanOsgiIntegrationTest for integration test inside OSGi.
 */
public class BeanIntegrationTest {

	private SocialData myBean;
	
	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/bundle-context.xml"};
	}
	
	public void setBean(SocialData bean) {
	  this.myBean = bean;
	}
	
	public void testBeanIsABean() {
	  
	}

}
