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
package org.societies.android.api.internal.useragent.feedback;

import java.io.ByteArrayOutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.convert.Registry;
import org.simpleframework.xml.convert.RegistryStrategy;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.societies.api.internal.schema.useragent.feedback.NegotiationDetailsBean;
import org.societies.api.internal.schema.useragent.feedback.UserFeedbackPrivacyNegotiationEvent;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.useragent.feedback.ExpFeedbackResultBean;
import org.societies.api.schema.useragent.feedback.ImpFeedbackResultBean;
import org.societies.simple.basic.URIConverter;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

/**
 * @author Eliza
 *
 */
public class TestFeedbackBeans extends AndroidTestCase{
	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	
	@MediumTest
	public void testParcelableExpBean() {
		ExpFeedbackResultBean expFBBean = new ExpFeedbackResultBean();
		ArrayList<String> feedback = new ArrayList<String>();
		feedback.add("hello");
		expFBBean.setFeedback(feedback);
		expFBBean.setRequestId("0122");
		Parcel parcel = Parcel.obtain();
        expFBBean.writeToParcel(parcel, 0);
        
		assertEquals(0, expFBBean.describeContents());

        //done writing, now reset parcel for reading
		parcel.setDataPosition(0);
		
		ExpFeedbackResultBean createFromParcel = ExpFeedbackResultBean.CREATOR.createFromParcel(parcel);
		
		assertEquals(expFBBean.getFeedback(), createFromParcel.getFeedback());
		assertEquals(expFBBean.getRequestId(), createFromParcel.getRequestId());		
		
	}

	@MediumTest
	public void testParcelableImpBean(){
		ImpFeedbackResultBean impFBBean = new ImpFeedbackResultBean();
		impFBBean.setAccepted(true);
		impFBBean.setRequestId("0122");
		
		Parcel parcel = Parcel.obtain();
		impFBBean.writeToParcel(parcel, 0);
		
		assertEquals(0, impFBBean.describeContents());
		
		parcel.setDataPosition(0);
		
		ImpFeedbackResultBean createFromParcel = ImpFeedbackResultBean.CREATOR.createFromParcel(parcel);
		
		assertEquals(impFBBean.getRequestId(), createFromParcel.getRequestId());
		assertEquals(impFBBean.isAccepted(), createFromParcel.isAccepted());
	}
	
	@MediumTest
	public void testSimpleBean(){
		UserFeedbackPrivacyNegotiationEvent event = new UserFeedbackPrivacyNegotiationEvent();
			ResponsePolicy respPol = new ResponsePolicy();
				List<ResponseItem> responseItems = new ArrayList<ResponseItem>();
					ResponseItem item = new ResponseItem();
						item.setDecision(Decision.PERMIT);
						RequestItem requestitem = new RequestItem();
							List<Action>actions = new ArrayList<Action>();
								Action action = new Action();
								actions.add(action);
							requestitem.setActions(actions);		
							List<Condition> conditions = new ArrayList<Condition>();
								Condition condition = new Condition();
								conditions.add(condition);
							requestitem.setConditions(conditions);		
							Resource resource = new Resource();
							requestitem.setOptional(true);
							requestitem.setResource(resource);
						item.setRequestItem(requestitem);
				responseItems.add(item);
			respPol.setResponseItems(responseItems);
			respPol.setNegotiationStatus(NegotiationStatus.ONGOING);
				RequestorServiceBean requestor = new RequestorServiceBean();
				requestor.setRequestorId("john.societies.local");
					ServiceResourceIdentifier sri = new ServiceResourceIdentifier();
					try { sri.setIdentifier(new URI("css://eliza@societies.org/HelloEarth")); } 
					catch (URISyntaxException e) { }
					sri.setServiceInstanceIdentifier("css://eliza@societies.org/HelloEarth");
				requestor.setRequestorServiceId(sri);
			respPol.setRequestor(requestor);
		event.setResponsePolicy(respPol);
			NegotiationDetailsBean negDetails = new NegotiationDetailsBean();
				negDetails.setNegotiationID(1223);
				RequestorCisBean negRequestor = new RequestorCisBean();
					negRequestor.setCisRequestorId("CIS-again");
					negRequestor.setRequestorId("aslf");
				negDetails.setRequestor(negRequestor);
		event.setNegotiationDetails(negDetails);

		//SIMPLE XML
		Registry registry = new Registry();
		Strategy strategy = new RegistryStrategy(registry);
		try {
			registry.bind(java.net.URI.class, URIConverter.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		Serializer ser = new Persister(strategy);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			ser.write(event, os);
		} catch (Exception e) {
			Log.d("ERROR", e.getMessage());
		}
		String xml = os.toString();
		Log.d("test", xml);
		
		try {
			ser.read(UserFeedbackPrivacyNegotiationEvent.class, xml);
		} catch (Exception e) {
			Log.d("ERROR", e.getMessage());
		}
	}
}
