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

package org.societies.useragent.feedback;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.springframework.scheduling.annotation.AsyncResult;

import junit.framework.Assert;
import junit.framework.TestCase;
import static org.mockito.Mockito.*;

public class TestUserFeedback extends TestCase{

	UserFeedback userFeedback;
	ICtxBroker mockCtxBroker;
	
	String mockDeviceID;
	String mockIdentity;
	
	//context
	CtxEntityIdentifier mockPersonId;
	CtxEntityIdentifier mockEntityId;
	CtxAttributeIdentifier mockUIDId;
	CtxAttribute mockUID;
	Future<CtxModelObject> mockUIDFuture;
	List<CtxIdentifier> mockUIDIdList;
	Future<List<CtxIdentifier>> mockUIDIdListFuture;

	public void setUp() throws Exception{
		mockCtxBroker = mock(ICtxBroker.class);
		
		mockDeviceID = "sarah.societies.local/android";
		mockIdentity = "sarah.societies.local";
		
		userFeedback = new UserFeedback();
		userFeedback.setCtxBroker(mockCtxBroker);
		//userFeedback.myCloudID = mockDeviceID;
		
		mockPersonId = new CtxEntityIdentifier(mockIdentity, "PERSON", new Long(12345));
		mockEntityId = new CtxEntityIdentifier(mockIdentity, "testEntity", new Long(12345));
		mockUIDId = new CtxAttributeIdentifier(mockEntityId, CtxAttributeTypes.UID, new Long(12345));
		mockUID = new CtxAttribute((CtxAttributeIdentifier)mockUIDId);
		mockUID.setStringValue(mockDeviceID);
		mockUIDFuture = new AsyncResult<CtxModelObject>(mockUID);
		mockUIDIdList = new ArrayList<CtxIdentifier>();
		mockUIDIdList.add(mockUIDId);
		mockUIDIdListFuture = new AsyncResult<List<CtxIdentifier>>(mockUIDIdList);
	}

	public void tearDown() throws Exception{
		userFeedback = null;
		mockCtxBroker = null;
		mockDeviceID = null;
		mockIdentity = null;
		mockPersonId = null;
		mockEntityId = null;
		mockUIDId = null;
		mockUID = null;
		mockUIDIdList = null;
		mockUIDFuture = null;
	}
	
	public void testTmp(){
		Assert.assertTrue(true);
	}


	/*public void testAckNackGUI() {
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID)).thenReturn(mockUIDIdListFuture);
			when(mockCtxBroker.retrieve(mockUIDId)).thenReturn(mockUIDFuture);
			
			String proposalText = "Press: YES";
			String[] options = {"YES", "NO"};
			List<String> feedback = userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, options)).get();
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID);
			verify(mockCtxBroker).retrieve(mockUIDId);
						
			//analyse results
			Assert.assertNotNull(feedback);
			Assert.assertTrue(feedback.size() == 1);
			String results = feedback.get(0);
			Assert.assertEquals("YES", results);
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}



	public void testCheckBoxGUI(){
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID)).thenReturn(mockUIDIdListFuture);
			when(mockCtxBroker.retrieve(mockUIDId)).thenReturn(mockUIDFuture);
			
			String proposalText = "Select: RED, GREEN and BLUE";
			String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW"};
			List<String> feedback = userFeedback.getExplicitFB(ExpProposalType.CHECKBOXLIST, new ExpProposalContent(proposalText, options)).get();
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID);
			verify(mockCtxBroker).retrieve(mockUIDId);
			
			//analyse results
			Assert.assertNotNull(feedback);
			Assert.assertTrue(feedback.size() == 3);
			Assert.assertTrue(feedback.contains("RED"));
			Assert.assertTrue(feedback.contains("GREEN"));
			Assert.assertTrue(feedback.contains("BLUE"));
			Assert.assertTrue(!feedback.contains("WHITE"));
			Assert.assertTrue(!feedback.contains("BLACK"));
			Assert.assertTrue(!feedback.contains("YELLOW"));
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}



	public void testRadioGUI(){
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID)).thenReturn(mockUIDIdListFuture);
			when(mockCtxBroker.retrieve(mockUIDId)).thenReturn(mockUIDFuture);
			
			String proposalText = "Select: WHITE";
			String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW"};
			List<String> feedback = userFeedback.getExplicitFB(ExpProposalType.RADIOLIST, new ExpProposalContent(proposalText, options)).get();
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID);
			verify(mockCtxBroker).retrieve(mockUIDId);
			
			//analyse results
			Assert.assertNotNull(feedback);
			Assert.assertTrue(feedback.size() == 1);
			String results = feedback.get(0);
			Assert.assertEquals("WHITE", results);
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	
	public void testTimedGUI_abort(){
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID)).thenReturn(mockUIDIdListFuture);
			when(mockCtxBroker.retrieve(mockUIDId)).thenReturn(mockUIDFuture);
			
			String proposalText = "Press: ABORT";
			Boolean feedback = userFeedback.getImplicitFB(ImpProposalType.TIMED_ABORT, new ImpProposalContent(proposalText, 10000)).get();
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID);
			verify(mockCtxBroker).retrieve(mockUIDId);
			
			//analyse results
			Assert.assertNotNull(feedback);
			Assert.assertEquals(false, feedback.booleanValue());
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	
	
	public void testTimedGUI_timeout(){
		try {
			when(mockCtxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID)).thenReturn(mockUIDIdListFuture);
			when(mockCtxBroker.retrieve(mockUIDId)).thenReturn(mockUIDFuture);
			
			String proposalText = "DO NOT press any button";
			Boolean feedback = userFeedback.getImplicitFB(ImpProposalType.TIMED_ABORT, new ImpProposalContent(proposalText, 5000)).get();
			
			verify(mockCtxBroker).lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.UID);
			verify(mockCtxBroker).retrieve(mockUIDId);
			
			//analyse results
			Assert.assertNotNull(feedback);
			Assert.assertEquals(true, feedback.booleanValue());
			
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}*/

}
