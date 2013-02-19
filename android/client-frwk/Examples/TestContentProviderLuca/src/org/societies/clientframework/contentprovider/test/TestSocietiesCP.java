package org.societies.clientframework.contentprovider.test;


import org.societies.clientframework.contentprovider.services.SocietiesCP;

import android.test.ProviderTestCase2;
import android.test.suitebuilder.annotation.SmallTest;

public class TestSocietiesCP extends ProviderTestCase2<SocietiesCP> {
 
	
	
	private static final String AUTHORITY = "org.societies.android.platform.contentprovider";
	

	public TestSocietiesCP() {
		super(SocietiesCP.class, AUTHORITY);
		
	}

	
	protected void setUp() throws Exception {
		super.setUp();	
		
	}
	
	
	

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	
	@SmallTest
	public void testOnCreate() {
		
	}


}
