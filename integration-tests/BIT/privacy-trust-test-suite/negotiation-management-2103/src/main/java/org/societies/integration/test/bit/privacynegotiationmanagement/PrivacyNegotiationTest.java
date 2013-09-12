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
package org.societies.integration.test.bit.privacynegotiationmanagement;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.PrivacyException;

import org.societies.api.privacytrust.privacy.util.privacypolicy.RequestPolicyUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;

public class PrivacyNegotiationTest extends EventListener{

	//NOTE: This negotiation test uses only 1 CSS so the CSS negotiates with itself. 
	public static Integer testCaseNumber;
	private RequestorCisBean requestorCis;
	private static Logger LOG = LoggerFactory.getLogger(PrivacyNegotiationTest.class);
	private Hashtable<RequestorBean, InternalEvent>  results = new Hashtable<RequestorBean, InternalEvent>();
	private RequestPolicy cisPolicy;
	private IIdentity userId;
	
	@Before
	public void setup(){
		userId = TestCase.commManager.getIdManager().getThisNetworkNode();
		TestCase.eventManager.subscribeInternalEvent(this, new String[]{EventTypes.FAILED_NEGOTIATION_EVENT,EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, null);
		XMLPolicyReader reader = new XMLPolicyReader(TestCase.ctxBroker, TestCase.commManager.getIdManager());
		cisPolicy = RequestPolicyUtils.toRequestPolicyBean(reader.readPolicyFromFile(TestCase.getFile()));
		
		try {
			
			requestorCis = new RequestorCisBean();
			requestorCis.setRequestorId(TestCase.commManager.getIdManager().getThisNetworkNode().getBareJid());
			requestorCis.setCisRequestorId("mockCis"+requestorCis.getRequestorId());
			cisPolicy.setRequestor(requestorCis);

			RequestPolicy updatePrivacyPolicy = TestCase.privacyPolicyManager.updatePrivacyPolicy(cisPolicy);
			Assert.assertNotNull("RequestPolicy is null", updatePrivacyPolicy);
			
			LOG.debug("Updated privacy policy");
			
			checkContextExists();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	@After
	public void tearDown(){
		try {
			boolean deletePrivacyPolicy = TestCase.privacyPolicyManager.deletePrivacyPolicy(requestorCis);
			Assert.assertTrue(deletePrivacyPolicy);
			
			TestCase.eventManager.unSubscribeInternalEvent(this, new String[]{EventTypes.FAILED_NEGOTIATION_EVENT,EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT}, null);
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Test
	public void testNegotiationwithCis(){
		NegotiationDetails details;
		try {
			details = new NegotiationDetails(RequestorUtils.toRequestor(requestorCis, TestCase.commManager.getIdManager()), 123);
			TestCase.privacyPolicyNegotiationManager.negotiateCISPolicy(details);
			this.LOG.debug("checking whether result has been received");
			while (!results.containsKey(requestorCis)){
				synchronized (results) {
					try {
						this.LOG.debug("Waiting for results.");
						results.wait();
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
			this.LOG.debug("result received "+results.containsKey(requestorCis));
			InternalEvent event = this.results.get(requestorCis);
			if (event.geteventInfo() instanceof FailedNegotiationEvent){
				Assert.fail("Negotiation has failed");
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PrivacyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		RequestorBean req;
		if (event.geteventInfo() instanceof FailedNegotiationEvent){
			FailedNegotiationEvent failedNegEvent = (FailedNegotiationEvent) event.geteventInfo();
			req = RequestorUtils.toRequestorBean(failedNegEvent.getDetails().getRequestor());
			this.LOG.debug("Received FailedNegotiationEvent");
			synchronized(results){
				
				this.results.put(req, event);
				this.results.notifyAll();
			}
		}else{
			PPNegotiationEvent negEvent = (PPNegotiationEvent) event.geteventInfo();
			req = RequestorUtils.toRequestorBean(negEvent.getDetails().getRequestor());
			this.LOG.debug("Received Successful Negotiation Event");
			synchronized(results){
				
				this.results.put(req, event);
				this.results.notifyAll();
			}
		}
		
		
	}
	
	
	private void checkContextExists(){
		ICtxBroker ctxBroker = TestCase.ctxBroker;
		List<RequestItem> requestItems = this.cisPolicy.getRequestItems();
		for (RequestItem item: requestItems){
			if (!item.isOptional()){
				try {
					IndividualCtxEntity individualCtxEntity = ctxBroker.retrieveIndividualEntity(userId).get();
					if (individualCtxEntity.getAttributes(item.getResource().getDataType()).size()==0){
						ctxBroker.createAttribute(individualCtxEntity.getId(), item.getResource().getDataType()).get();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ExecutionException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (CtxException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
