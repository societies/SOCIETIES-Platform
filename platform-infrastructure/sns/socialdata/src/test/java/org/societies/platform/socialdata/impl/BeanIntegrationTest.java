package org.societies.platform.socialdata.impl;

import org.societies.api.internal.sns.ISocialData;


/**
 * Local integration test (outside of OSGi).
 * @see BeanOsgiIntegrationTest for integration test inside OSGi.
 */
public class BeanIntegrationTest {

	private ISocialData myBean;
	
	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/bundle-context.xml"};
	}
	
	public void setBean(ISocialData bean) {
	  this.myBean = bean;
	}
	
	public void testBeanIsABean() {
	  
	}

}
