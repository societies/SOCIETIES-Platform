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

import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.api.schema.servicelifecycle.model.ServiceType;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestService extends AndroidTestCase {

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
		ServiceResourceIdentifier aSRI = new ServiceResourceIdentifier();
		aSRI.setIdentifier(new URI("http://alec.societies.org"));
		aSRI.setServiceInstanceIdentifier("alecBundle123");
		
		ServiceImplementation aServiceImp = new ServiceImplementation();
		aServiceImp.setServiceClient("TestClient");
		aServiceImp.setServiceNameSpace("http://soceities.org/test/namespace");
		aServiceImp.setServiceProvider("TestProvider");
		aServiceImp.setServiceVersion("V0.1.1b");
		
		ServiceInstance aSerInstance = new ServiceInstance();
		aSerInstance.setCssJid("john.societies.local");
		aSerInstance.setFullJid("john@societies.local/android");
		aSerInstance.setParentJid("parent.societies.local");
		aSerInstance.setXMPPNode("john.societies.local");
		aSerInstance.setParentIdentifier(aSRI);
		aSerInstance.setServiceImpl(aServiceImp);
		
		Service service = new Service();
		service.setAuthorSignature("jsmith");
		service.setContextSource("yes");
		service.setPrivacyPolicy("<Privacy />");
		service.setSecurityPolicy("<Security />");
		service.setServiceCategory("My Service Category");
		service.setServiceDescription("What a service description");
		service.setServiceEndpoint("localhost:8080/endpoint");
		service.setServiceIdentifier(aSRI);
		service.setServiceInstance(aSerInstance);
		service.setServiceLocation("Paris");
		service.setServiceName("The Testing Service");
		service.setServiceStatus(ServiceStatus.STARTED);
		service.setServiceType(ServiceType.THIRD_PARTY_ANDROID);
		
		assertEquals(0, service.describeContents());
		
        Parcel parcel = Parcel.obtain();
        service.writeToParcel(parcel, 0);
        
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        Service createFromParcel = Service.CREATOR.createFromParcel(parcel);
       
        assertEquals(service.getAuthorSignature(), createFromParcel.getAuthorSignature());
        assertEquals(service.getContextSource(), createFromParcel.getContextSource());
        assertEquals(service.getPrivacyPolicy(), createFromParcel.getPrivacyPolicy());
        assertEquals(service.getSecurityPolicy(), createFromParcel.getSecurityPolicy());
        assertEquals(service.getServiceCategory(), createFromParcel.getServiceCategory());
        assertEquals(service.getServiceDescription(), createFromParcel.getServiceDescription());
        assertEquals(service.getServiceEndpoint(), createFromParcel.getServiceEndpoint());
        //SRI
        assertEquals(service.getServiceIdentifier().getServiceInstanceIdentifier(), createFromParcel.getServiceIdentifier().getServiceInstanceIdentifier());
        assertEquals(service.getServiceIdentifier().getIdentifier(), createFromParcel.getServiceIdentifier().getIdentifier());
        //SERVICE INSTANCE
        assertEquals(service.getServiceInstance().getCssJid(), createFromParcel.getServiceInstance().getCssJid());
        assertEquals(service.getServiceInstance().getFullJid(), createFromParcel.getServiceInstance().getFullJid());
        assertEquals(service.getServiceInstance().getParentJid(), createFromParcel.getServiceInstance().getParentJid());
        assertEquals(service.getServiceInstance().getXMPPNode(), createFromParcel.getServiceInstance().getXMPPNode());
        //SERVICE INSTANCE -> PARENT ID (sri)
        assertEquals(service.getServiceInstance().getParentIdentifier().getServiceInstanceIdentifier(), createFromParcel.getServiceInstance().getParentIdentifier().getServiceInstanceIdentifier());
        assertEquals(service.getServiceInstance().getParentIdentifier().getIdentifier(), createFromParcel.getServiceInstance().getParentIdentifier().getIdentifier());
        //SERVICE INSTANCE -> SERVICE IMPL
        assertEquals(service.getServiceInstance().getServiceImpl().getServiceClient(), createFromParcel.getServiceInstance().getServiceImpl().getServiceClient());
        assertEquals(service.getServiceInstance().getServiceImpl().getServiceNameSpace(), createFromParcel.getServiceInstance().getServiceImpl().getServiceNameSpace());
        assertEquals(service.getServiceInstance().getServiceImpl().getServiceProvider(), createFromParcel.getServiceInstance().getServiceImpl().getServiceProvider());
        assertEquals(service.getServiceInstance().getServiceImpl().getServiceVersion(), createFromParcel.getServiceInstance().getServiceImpl().getServiceVersion());
        
        assertEquals(service.getServiceLocation(), createFromParcel.getServiceLocation());
        assertEquals(service.getServiceName(), createFromParcel.getServiceName());
        assertEquals(service.getServiceStatus(), createFromParcel.getServiceStatus());
        assertEquals(service.getServiceType(), createFromParcel.getServiceType());
	}


	@MediumTest
	public void testNullParcelable() throws Exception {
		
		Service service = new Service();
		service.setAuthorSignature("jsmith");
		service.setContextSource("yes");
		service.setPrivacyPolicy("<Privacy />");
		service.setSecurityPolicy("<Security />");
		service.setServiceCategory("My Service Category");
		//service.setServiceDescription("What a service description");
		//service.setServiceEndpoint("localhost:8080/endpoint");
		//service.setServiceIdentifier(aSRI);
		//service.setServiceInstance(aSerInstance);
		service.setServiceLocation("Paris");
		service.setServiceName("The Testing Service");
		//service.setServiceStatus(ServiceStatus.STARTED);
		service.setServiceType(ServiceType.THIRD_PARTY_ANDROID);
		
		assertEquals(0, service.describeContents());
		
        Parcel parcel = Parcel.obtain();
        service.writeToParcel(parcel, 0);
        
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        
        Service createFromParcel = Service.CREATOR.createFromParcel(parcel);
       
        assertEquals(service.getAuthorSignature(), createFromParcel.getAuthorSignature());
        assertEquals(service.getContextSource(), createFromParcel.getContextSource());
        assertEquals(service.getPrivacyPolicy(), createFromParcel.getPrivacyPolicy());
        assertEquals(service.getSecurityPolicy(), createFromParcel.getSecurityPolicy());
        assertEquals(service.getServiceCategory(), createFromParcel.getServiceCategory());
        //assertEquals(service.getServiceDescription(), createFromParcel.getServiceDescription());
        //assertEquals(service.getServiceEndpoint(), createFromParcel.getServiceEndpoint());
        assertEquals(service.getServiceLocation(), createFromParcel.getServiceLocation());
        assertEquals(service.getServiceName(), createFromParcel.getServiceName());
        //assertEquals(service.getServiceStatus(), createFromParcel.getServiceStatus());
        assertEquals(service.getServiceType(), createFromParcel.getServiceType());
	}

}
