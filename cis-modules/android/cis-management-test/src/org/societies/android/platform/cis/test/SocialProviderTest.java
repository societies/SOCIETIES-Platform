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
package org.societies.android.platform.cis.test;

import org.societies.android.platform.cis.CisRecord;
import org.societies.android.platform.cis.SocialContract;
import org.societies.android.platform.cis.SocialProvider;

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
	
	public void testDelete(){
	//TODO: probably add a CisRecord, then delete it.	
		assertFalse(true);
		
	}
	public void testInsertGroup(){
	//TODO insert and get and compare.
	//FIXME This method tests both insert and query.
		ContentValues values = new ContentValues();
		//TODO ID should not be part of CisRecord constructor.
		CisRecord record = new CisRecord("1", "XYZ", 
				"babak@societies.org", "today");
		values.put(SocialContract.Groups.NAME , record.getName());
		values.put(SocialContract.Groups.OWNER_ID , record.getOwnerId());
		values.put(SocialContract.Groups.CREATION_DATE , record.getCreationDate());
		Uri newGroupUri= resolver.insert(SocialContract.Groups.CONTENT_URI , values);
		String[] projection ={
				SocialContract.Groups.NAME,
				SocialContract.Groups.OWNER_ID,
				SocialContract.Groups.CREATION_DATE
			};
		//WHERE _id = new
		String selection = SocialContract.Groups._ID + " = " +
			newGroupUri.getLastPathSegment();
		Cursor cursor = resolver.query(SocialContract.Groups.CONTENT_URI,
				projection, selection, null, null);
		assertFalse(cursor != null);
		//Create new ContentValues object based on return.
		ContentValues newValues = new ContentValues();
		newValues.put(SocialContract.Groups.NAME , cursor.getString(0));
		newValues.put(SocialContract.Groups.OWNER_ID , cursor.getString(1));
		newValues.put(SocialContract.Groups.CREATION_DATE , cursor.getString(1));
		assertEquals(newValues,values);

	}
	public void testUpdate(){
	//TODO 
		assertFalse(true);
	}
}
