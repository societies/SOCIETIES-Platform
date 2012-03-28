/**
 * 
 */
package org.societies.android.platform.cis.test;

import org.societies.android.platform.cis.SocialContract;
import org.societies.android.platform.cis.SocialProvider;

import android.test.ProviderTestCase2;

/**
 * @author bfars
 *
 */
public class SocialProviderTest extends ProviderTestCase2<SocialProvider> {
	
	SocialProviderTest(){
		super(SocialProvider.class, SocialContract.AUTHORITY.getAuthority());
	}

	/* (non-Javadoc)
	 * @see android.test.ProviderTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	void testDelete(){
		
	}
}
