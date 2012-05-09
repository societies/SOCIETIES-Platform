/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.platform.test;

import org.societies.android.platform.CisRecord;
import org.societies.android.platform.SocialContract;
import org.societies.android.platform.SocialProvider;

import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.test.ProviderTestCase2;
import android.test.mock.MockContentResolver;


/**
 * @author Babak.Farshchian@sintef.no
 *
 */
public class SocialProviderTest extends ProviderTestCase2<SocialProvider> {
	
	//This is the mock resolver that will do all the tests:
	private MockContentResolver resolver;
	//This is the mock context where the provider runs:
	//private IsolatedContext context;
	
	public SocialProviderTest(){
		super(SocialProvider.class, SocialContract.AUTHORITY.getAuthority());
	}

	/* (non-Javadoc)
	 * @see android.test.ProviderTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		//Store the value of the mock resolver:
		resolver = getMockContentResolver();
		//context = getMockContext();
		resolver.addProvider(SocialContract.AUTHORITY.getAuthority(), getProvider());
	}

	/* (non-Javadoc)
	 * @see android.test.AndroidTestCase#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	
//	public void testDelete(){
//	//TODO: probably add a CisRecord, then delete it.	
//		assertFalse(true);
//		
//	}

	/**
	 * Tests that inserting a CIS from Android will actually create that CIS
	 * in CIS Manager Cloud.
	 * TODO: Currently only local DB so no cloud change is being done.
	 * 
	 * The logic for the test:
	 * 1- Create a local {@link ContentValues} to hold all the data for the CIS
	 * 2- Call insert in SocialProvider to perform insertion
	 * 3- Get the newly inserted CIS from SocialProvider.
	 * 4- Fail if the CIS was not returned.
	 * 5- Fail if the CIS data are not the same as those inserted in step 2.
	 * 
	 * FIXME: currently does not care about multiple CISs. Does not use
	 * index number that is returned by resolver.insert.
	 *  
	 */
	public void testInsertCommunity(){
		//1- Create local ContentValues to hold the community data.
		ContentValues initialValues = new ContentValues();
		initialValues.put(SocialContract.Community.NAME , "XYZ");
		initialValues.put(SocialContract.Community.OWNER_ID , "babak@societies.org");
		initialValues.put(SocialContract.Community.CREATION_DATE , "today");
		
		//2- Call insert in SocialProvider to initiate insertion
		Uri newGroupUri= resolver.insert(SocialContract.Community.CONTENT_URI , 
				initialValues);
		
		//3- Get the newly inserted CIS from SocialProvider.
		//What to get:
		String[] projection ={
				SocialContract.Community.NAME,
				SocialContract.Community.OWNER_ID,
				SocialContract.Community.CREATION_DATE
			};
		//WHERE _id = ID of the newly created CIS:
		String selection = SocialContract.Community._ID + " = " +
			newGroupUri.getLastPathSegment();
		Cursor cursor = resolver.query(SocialContract.Community.CONTENT_URI,
				projection, selection, null, null);
		
		//4- Fail if the CIS was not returned.
		assertFalse(cursor == null);
		if (cursor == null)	return;
		if (!cursor.moveToFirst()) return;
		//5- Fail if the CIS data are not correct:
		//Create new ContentValues object based on returned CIS:
		ContentValues returnedValues = new ContentValues();
		returnedValues.put(SocialContract.Community.NAME , cursor.getString(0));
		returnedValues.put(SocialContract.Community.OWNER_ID , cursor.getString(1));
		returnedValues.put(SocialContract.Community.CREATION_DATE , cursor.getString(2));
		assertEquals(returnedValues,initialValues);

	}
//	public void testUpdate(){
//	//TODO 
//		assertFalse(true);
//	}
}
