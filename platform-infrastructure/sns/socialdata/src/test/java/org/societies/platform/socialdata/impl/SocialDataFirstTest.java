package org.societies.platform.socialdata.impl;

import org.societies.api.internal.sns.ISocialData;


/**
 * Local integration test (outside of OSGi).
 * @see SocialDataOsgiIntegrationTest for integration test inside OSGi.
 */
public class SocialDataFirstTest {

	private ISocialData socialData;
	
	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/bundle-context.xml"};
	}
	
	public void setBean(ISocialData bean) {
	  this.socialData = bean;
	}
	
	public void testBeanIsABean() {
		System.out.println("Last Update:"+socialData.getLastUpdate());
	}

}
