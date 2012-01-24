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

import java.util.List;

import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.feedback.IUserFeedbackCallback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;

import junit.framework.Assert;
import junit.framework.TestCase;

public class TestUserFeedback extends TestCase implements IUserFeedbackCallback{

	IUserFeedback userFeedback;
	List<String> expFeedback;
	Boolean impFeedback;

	public void setUp() throws Exception{
		userFeedback = new UserFeedback();
		expFeedback = null;
		impFeedback = null;
	}

	public void tearDown() throws Exception{
		//null
	}

	/*public void testAckNackGUI() {
		String proposalText = "Press: YES";
		String[] options = {"YES", "NO"};
		userFeedback.getExplicitFB(ExpProposalType.ACKNACK, new ExpProposalContent(proposalText, options), this);
		while(expFeedback == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//analyse results
		System.out.println("Got here");
		Assert.assertNotNull(expFeedback);
		Assert.assertTrue(expFeedback.size() == 1);
		String results = expFeedback.get(0);
		Assert.assertEquals("YES", results);
	}



	public void testCheckBoxGUI(){
		String proposalText = "Select: RED, GREEN and BLUE";
		String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW"};
		userFeedback.getExplicitFB(ExpProposalType.CHECKBOXLIST, new ExpProposalContent(proposalText, options), this);
		while(expFeedback == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//analyse results
		Assert.assertNotNull(expFeedback);
		Assert.assertTrue(expFeedback.size() == 3);
		Assert.assertTrue(expFeedback.contains("RED"));
		Assert.assertTrue(expFeedback.contains("GREEN"));
		Assert.assertTrue(expFeedback.contains("BLUE"));
		Assert.assertTrue(!expFeedback.contains("WHITE"));
		Assert.assertTrue(!expFeedback.contains("BLACK"));
		Assert.assertTrue(!expFeedback.contains("YELLOW"));
	}



	public void testRadioGUI(){
		String proposalText = "Select: WHITE";
		String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW"};
		userFeedback.getExplicitFB(ExpProposalType.RADIOLIST, new ExpProposalContent(proposalText, options), this);
		while(expFeedback == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//analyse results
		Assert.assertNotNull(expFeedback);
		Assert.assertTrue(expFeedback.size() == 1);
		String results = expFeedback.get(0);
		Assert.assertEquals("WHITE", results);
	}

	
	
	public void testTimedGUI_abort(){
		String proposalText = "Press: ABORT";
		userFeedback.getImplicitFB(ImpProposalType.TIMED_ABORT, new ImpProposalContent(proposalText, 10000), this);
		while(impFeedback == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//analyse results
		Assert.assertNotNull(impFeedback);
		Assert.assertEquals(false, impFeedback.booleanValue());
	}

	
	
	public void testTimedGUI_timeout(){
		String proposalText = "DO NOT press any button";
		userFeedback.getImplicitFB(ImpProposalType.TIMED_ABORT, new ImpProposalContent(proposalText, 5000), this);
		while(impFeedback == null){
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		//analyse results
		Assert.assertNotNull(impFeedback);
		Assert.assertEquals(true, impFeedback.booleanValue());
	}*/

	
	
	@Override
	public void handleExpFeedback(List<String> results) {
		expFeedback = results;
	}

	@Override
	public void handleImpFeedback(Boolean result) {
		impFeedback = result;
	}

}
