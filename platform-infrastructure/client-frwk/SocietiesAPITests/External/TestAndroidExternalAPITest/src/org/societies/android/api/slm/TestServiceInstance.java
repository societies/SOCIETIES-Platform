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

import java.net.URI;

import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestServiceInstance  extends AndroidTestCase {

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
		ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
		sri.setIdentifier(new URI("http://alec.societies.org"));
		sri.setServiceInstanceIdentifier("alecBundle123");
		
		ServiceImplementation serviceImp = new ServiceImplementation();
		serviceImp.setServiceClient("TestClient");
		serviceImp.setServiceNameSpace("http://soceities.org/test/namespace");
		serviceImp.setServiceProvider("TestProvider");
		serviceImp.setServiceVersion("V0.1.1b");
		
		ServiceInstance serInstance = new ServiceInstance();
		serInstance.setCssJid("john.societies.local");
		serInstance.setFullJid("john@societies.local/android");
		serInstance.setParentJid("parent.societies.local");
		serInstance.setXMPPNode("john.societies.local");
		serInstance.setParentIdentifier(sri);
		serInstance.setServiceImpl(serviceImp);
		
		assertEquals(0, serviceImp.describeContents());
		
        Parcel parcel = Parcel.obtain();
        serInstance.writeToParcel(parcel, 0);
        
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        ServiceInstance createFromParcel = ServiceInstance.CREATOR.createFromParcel(parcel);
       
        assertEquals(serInstance.getCssJid(), createFromParcel.getCssJid());
        assertEquals(serInstance.getFullJid(), createFromParcel.getFullJid());
        assertEquals(serInstance.getParentJid(), createFromParcel.getParentJid());
        assertEquals(serInstance.getXMPPNode(), createFromParcel.getXMPPNode());
        //CAN'T COMPARE SRI'S DIRECTLY, MUST COMPARE EACH PROPERTY
        assertEquals(serInstance.getParentIdentifier().getServiceInstanceIdentifier(), createFromParcel.getParentIdentifier().getServiceInstanceIdentifier());
        assertEquals(serInstance.getParentIdentifier().getIdentifier(), createFromParcel.getParentIdentifier().getIdentifier());
        //CAN'T COMPARE ServiceImpl'S DIRECTLY, MUST COMPARE EACH PROPERTY
        assertEquals(serInstance.getServiceImpl().getServiceClient(), createFromParcel.getServiceImpl().getServiceClient());
        assertEquals(serInstance.getServiceImpl().getServiceNameSpace(), createFromParcel.getServiceImpl().getServiceNameSpace());
        assertEquals(serInstance.getServiceImpl().getServiceProvider(), createFromParcel.getServiceImpl().getServiceProvider());
        assertEquals(serInstance.getServiceImpl().getServiceVersion(), createFromParcel.getServiceImpl().getServiceVersion());
	}

}
