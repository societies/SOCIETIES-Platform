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
package org.societies.android.api.internal.privacy.policy;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ActionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.NegotiationStatus;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponseItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ResponsePolicy;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

import android.os.Parcel;
import android.test.AndroidTestCase;
/**
 * @author Eliza
 *
 */
public class TestPrivacyPolicy extends AndroidTestCase{

	protected void setUp() throws Exception {
		super.setUp();
	}
	
	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testParcelable() throws URISyntaxException{
		Action action = new Action();
		action.setActionConstant(ActionConstants.READ);
		action.setOptional(false);
		assertNotNull(action);
		Action action2 = new Action();
		action2.setActionConstant(ActionConstants.WRITE);
		action.setOptional(true);
		assertNotNull(action2);
		
		ArrayList<Action> actions = new ArrayList<Action>();
		actions.add(action);
		actions.add(action2);
		
		
		Condition condition = new Condition();
		condition.setConditionConstant(ConditionConstants.DATA_RETENTION_IN_HOURS);
		condition.setOptional(false);
		condition.setValue("24");
		
		Condition condition2 = new Condition();
		condition2.setConditionConstant(ConditionConstants.SHARE_WITH_3RD_PARTIES);
		condition2.setValue("YES");
		condition2.setOptional(false);
		
		ArrayList<Condition> conditions =  new ArrayList<Condition>();
		conditions.add(condition);
		conditions.add(condition2);
		
		Resource resource = new Resource();
		resource.setDataType("SYMBOLIC_LOCATION");
	
		
		RequestItem requestItem = new RequestItem();
		requestItem.setActions(actions);
		requestItem.setConditions(conditions);
		requestItem.setResource(resource);
		requestItem.setOptional(false);
		
		RequestItem requestItem2 = new RequestItem();
		requestItem2.setActions(actions);
		requestItem2.setConditions(conditions);
		requestItem2.setResource(resource);
		requestItem2.setOptional(false);
		
		
		ArrayList<RequestItem> requestItems = new ArrayList<RequestItem>();
		requestItems.add(requestItem);
		requestItems.add(requestItem2);
		
		RequestPolicy requestPolicy = new RequestPolicy();
		requestPolicy.setRequestor(this.getRequestorServiceBean());
		requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.SERVICE);
		requestPolicy.setRequestItems(requestItems);
		
		assertEquals(0, requestPolicy.describeContents());
		
		Parcel parcel = Parcel.obtain();
		requestPolicy.writeToParcel(parcel, 0);
		parcel.setDataPosition(0);
		RequestPolicy fromParcel = RequestPolicy.CREATOR.createFromParcel(parcel);
		assertNotNull(fromParcel);
		
		
		
		ArrayList<ResponseItem> responseItems = new ArrayList<ResponseItem>();
		ResponseItem responseItem = new ResponseItem();
		responseItem.setRequestItem(requestItem);
		responseItem.setDecision(Decision.PERMIT);
		responseItems.add(responseItem);
		
		ResponseItem responseItem2 = new ResponseItem();
		responseItem2.setRequestItem(requestItem2);
		responseItem2.setDecision(Decision.DENY);
		responseItems.add(responseItem2);
		
		ResponsePolicy responsePolicy = new ResponsePolicy();
		responsePolicy.setRequestor(this.getRequestorServiceBean());
		responsePolicy.setResponseItems(responseItems);
		responsePolicy.setNegotiationStatus(NegotiationStatus.ONGOING);
		
		assertEquals(0, responsePolicy.describeContents());
		
		Parcel parcel2 = Parcel.obtain();
		responsePolicy.writeToParcel(parcel2, 0);
		parcel2.setDataPosition(0);
		ResponsePolicy fromParcel2 = ResponsePolicy.CREATOR.createFromParcel(parcel2);
		assertNotNull(fromParcel2);
		
	}
	
	private RequestorBean getRequestorServiceBean() throws URISyntaxException{
		RequestorServiceBean requestor = new RequestorServiceBean();
		requestor.setRequestorId("emma.societies.local");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI("http://eliza.societies.org"));
		serviceId.setServiceInstanceIdentifier("elizaBundle123");
		requestor.setRequestorServiceId(serviceId);
		assertNotNull(requestor);
		return requestor;
	}
	
	
}
