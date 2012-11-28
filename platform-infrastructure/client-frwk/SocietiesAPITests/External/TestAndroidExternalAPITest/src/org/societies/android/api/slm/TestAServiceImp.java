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
package org.societies.android.api.slm;

import org.societies.android.api.servicelifecycle.AServiceImplementation;
import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestAServiceImp extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		//after
	}
	
	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}
	
	@MediumTest
	public void testParcelable() throws Exception {
		AServiceImplementation aServiceImp = new AServiceImplementation();
		aServiceImp.setServiceClient("TestClient");
		aServiceImp.setServiceNameSpace("http://soceities.org/test/namespace");
		aServiceImp.setServiceProvider("TestProvider");
		aServiceImp.setServiceVersion("V0.1.1b");
		
		assertEquals(0, aServiceImp.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aServiceImp.writeToParcel(parcel, 0);
        
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        AServiceImplementation createFromParcel = AServiceImplementation.CREATOR.createFromParcel(parcel);
       
        assertEquals(aServiceImp.getServiceClient(), createFromParcel.getServiceClient());
        assertEquals(aServiceImp.getServiceNameSpace(), createFromParcel.getServiceNameSpace());
        assertEquals(aServiceImp.getServiceProvider(), createFromParcel.getServiceProvider());
        assertEquals(aServiceImp.getServiceVersion(), createFromParcel.getServiceVersion());
	}
	
	@MediumTest
	public void testEquals() throws Exception {
		AServiceImplementation aServiceImp = new AServiceImplementation();
		aServiceImp.setServiceClient("TestClient");
		aServiceImp.setServiceNameSpace("http://soceities.org/test/namespace");
		aServiceImp.setServiceProvider("TestProvider");
		aServiceImp.setServiceVersion("V0.1.1b");
		
		assertEquals(0, aServiceImp.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aServiceImp.writeToParcel(parcel, 0);
        
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        AServiceImplementation createFromParcel = AServiceImplementation.CREATOR.createFromParcel(parcel);
       
        assert(aServiceImp.equals(createFromParcel));
	}
}
