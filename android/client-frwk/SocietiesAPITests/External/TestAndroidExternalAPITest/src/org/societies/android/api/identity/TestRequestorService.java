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
package org.societies.android.api.identity;

import java.net.URI;
import java.net.URISyntaxException;

import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

/**
 * @author Eliza
 *
 */
public class TestRequestorService extends AndroidTestCase{

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {

		super.tearDown();
	}


	@MediumTest
	public void testParcelable() throws URISyntaxException{
		RequestorServiceBean requestor = new RequestorServiceBean();
		requestor.setRequestorId("emma.societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI("http://alec.societies.org"));
		serviceId.setServiceInstanceIdentifier("alecBundle123");
		requestor.setRequestorServiceId(serviceId);
		assertNotNull(requestor);

		assertEquals(0, requestor.describeContents());

		Parcel parcel = Parcel.obtain();
		requestor.writeToParcel(parcel, 0);
		//done writing, now reset parcel for reading
		parcel.setDataPosition(0);
		//finish round trip

		RequestorServiceBean createFromParcel = RequestorServiceBean.CREATOR.createFromParcel(parcel);

		assertNotNull(createFromParcel);
		assertEquals(requestor.getRequestorId(), createFromParcel.getRequestorId());		
		//TEST SRI's
		assertEquals(requestor.getRequestorServiceId().getServiceInstanceIdentifier(), createFromParcel.getRequestorServiceId().getServiceInstanceIdentifier());
		assertEquals(requestor.getRequestorServiceId().getIdentifier(), createFromParcel.getRequestorServiceId().getIdentifier());
	}
}
