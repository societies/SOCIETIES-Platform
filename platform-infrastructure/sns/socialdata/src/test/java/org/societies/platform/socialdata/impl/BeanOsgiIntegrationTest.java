package org.societies.platform.socialdata.impl;



/**
 * OSGi integration test (inside OSGi).
 * @see AbstractConfigurableBundleCreatorTests
 */
public class BeanOsgiIntegrationTest {

	protected String[] getConfigLocations() {
	  return new String[] {"META-INF/spring/*.xml"};
	}
	
	public void testOsgiBundleContext() {
	 
	}

}