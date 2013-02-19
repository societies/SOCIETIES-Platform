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

package org.societies.android.api.personalisation.model;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.schema.personalisation.model.ActionBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestParcelableAction extends AndroidTestCase{
	
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	@MediumTest
	public void testParcelable() {
		ActionBean action = new ActionBean();
		assertNotNull(action);
		
		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		String serviceIDString = "TEST_SERVICE_ID";
		try {
			serviceID.setIdentifier(new URI(serviceIDString));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		serviceID.setServiceInstanceIdentifier(serviceIDString);
		
		action.setServiceID(serviceID);
		action.setServiceType("TEST_SERVICE_TYPE");
		action.setParameterName("TEST_PARAMETER_NAME");
		action.setValue("TEST_VALUE");
		
		Parcel parcel = Parcel.obtain();
        action.writeToParcel(parcel, 0);
				
		assertEquals(0, action.describeContents());
		       
        //done writing, now reset parcel for reading
		parcel.setDataPosition(0);
       
        //finish round trip
        ActionBean createFromParcel = ActionBean.CREATOR.createFromParcel(parcel);
       
        assertEquals(action.getServiceID().getIdentifier(), createFromParcel.getServiceID().getIdentifier());
        assertEquals(action.getServiceID().getServiceInstanceIdentifier(), createFromParcel.getServiceID().getServiceInstanceIdentifier());
        assertEquals(action.getServiceType(), createFromParcel.getServiceType());
        assertEquals(action.getParameterName(), createFromParcel.getParameterName());
        assertEquals(action.getValue(), createFromParcel.getValue());
	}

	/*@MediumTest
	public void testConvertAction(){
		Action action = new Action();
		assertNotNull(action);
		
		ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
		String serviceIDString = "TEST_SERVICE_ID";
		try {
			serviceID.setIdentifier(new URI(serviceIDString));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		serviceID.setServiceInstanceIdentifier(serviceIDString);
		
		action.setServiceID(serviceID);
		action.setServiceType("TEST_SERVICE_TYPE");
		action.setparameterName("TEST_PARAMETER_NAME");
		action.setvalue("TEST_VALUE");
		
		//convert to aaction and check
		AAction aaction = AAction.convertAction(action);
		assertEquals(aaction.getServiceID().getIdentifier(), action.getServiceID().getIdentifier());
		assertEquals(aaction.getServiceID().getServiceInstanceIdentifier(), action.getServiceID().getServiceInstanceIdentifier());
		assertEquals(aaction.getServiceType(), action.getServiceType());
		assertEquals(aaction.getparameterName(), action.getparameterName());
		assertEquals(aaction.getvalue(), action.getvalue());
	}
	
	@MediumTest
	public void testConvertAAction(){
		AAction aaction = new AAction();
		assertNotNull(aaction);
		
		AServiceResourceIdentifier aServiceID = new AServiceResourceIdentifier();
		String serviceIDString = "TEST_SERVICE_ID";
		try {
			aServiceID.setIdentifier(new URI(serviceIDString));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		aServiceID.setServiceInstanceIdentifier(serviceIDString);
		
		aaction.setServiceID(aServiceID);
		aaction.setServiceType("TEST_SERVICE_TYPE");
		aaction.setparameterName("TEST_PARAMETER_NAME");
		aaction.setvalue("TEST_VALUE");
				
		assertEquals(0, aaction.describeContents());
		
		//convert to action and check
		Action action = AAction.convertAAction(aaction);
		assertEquals(action.getServiceID().getIdentifier(), aaction.getServiceID().getIdentifier());
		assertEquals(action.getServiceID().getServiceInstanceIdentifier(), aaction.getServiceID().getServiceInstanceIdentifier());
		assertEquals(action.getServiceType(), aaction.getServiceType());
		assertEquals(action.getparameterName(), aaction.getparameterName());
		assertEquals(action.getvalue(), aaction.getvalue());
	}*/
}
