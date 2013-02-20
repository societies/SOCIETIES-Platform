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

package org.societies.android.platform.useragent.feedback.container.test;

import java.util.List;

import org.societies.android.api.internal.useragent.IAndroidUserFeedback;
import org.societies.android.platform.useragent.feedback.container.TestContainerFeedbackService;
import org.societies.android.platform.useragent.feedback.container.TestContainerFeedbackService.FeedbackContainerBinder;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;

import android.content.Intent;
import android.test.ServiceTestCase;
import android.test.suitebuilder.annotation.MediumTest;
import android.util.Log;

public class JUnitTestsFeedback extends ServiceTestCase <TestContainerFeedbackService>{

	private static final String LOG_TAG = JUnitTestsFeedback.class.getName();
	
	public JUnitTestsFeedback() {
		super(TestContainerFeedbackService.class);
	}

	@Override
	protected void setUp() throws Exception {
		super.setUp();
	}

	@MediumTest
	public void testPreconditions() {
	}
	
	//@MediumTest
	public void testRadioPopup() throws Exception {
		Log.d(LOG_TAG, "Testing radio popups");
		Intent feedbackIntent = new Intent(getContext(), TestContainerFeedbackService.class);
		FeedbackContainerBinder binder = (FeedbackContainerBinder) bindService(feedbackIntent);
		assertNotNull(binder);

		IAndroidUserFeedback feedbackService = (IAndroidUserFeedback) binder.getService();
		
		String proposalText = "What is your favourite cuisine?";
		String[] options = {"Scottish", "Indian", "Chinese", "Mexican", "Italian", "Spanish", "French", "American"};
		ExpProposalContent content = new ExpProposalContent(proposalText, options);
		feedbackService.getExplicitFB("CLIENT", ExpProposalType.RADIOLIST, content);
		
		//get result through Intent
		Log.d(LOG_TAG, "Radio popup test complete!");
	}
	
	@MediumTest
	public void testCheckboxPopup() throws Exception {
		Log.d(LOG_TAG, "Testing checkbox popups");
		Intent feedbackIntent = new Intent(getContext(), TestContainerFeedbackService.class);
		FeedbackContainerBinder binder = (FeedbackContainerBinder) bindService(feedbackIntent);
		assertNotNull(binder);

		IAndroidUserFeedback feedbackService = (IAndroidUserFeedback) binder.getService();
		
		String proposalText = "Please choose your favourite colours?";
		String[] options = {"RED", "WHITE", "GREEN", "BLUE", "BLACK", "YELLOW", "Purple", "Gold", "Pink", "Silver"};
		ExpProposalContent content = new ExpProposalContent(proposalText, options);
		feedbackService.getExplicitFB("CLIENT", ExpProposalType.CHECKBOXLIST, content);
		
		//get result through Intent
		Log.d(LOG_TAG, "Checkbox popup test complete!");
	}
	
	
	
	//@MediumTest
	public void testAcknackPopup() throws Exception {
		Log.d(LOG_TAG, "Testing ack/nack popups");
		Intent feedbackIntent = new Intent(getContext(), TestContainerFeedbackService.class);
		FeedbackContainerBinder binder = (FeedbackContainerBinder) bindService(feedbackIntent);
		assertNotNull(binder);

		IAndroidUserFeedback feedbackService = (IAndroidUserFeedback) binder.getService();
		
		String proposalText = "Is it raining?";
		String[] options = {"Yes", "No"};
		ExpProposalContent content = new ExpProposalContent(proposalText, options);
		feedbackService.getExplicitFB("CLIENT", ExpProposalType.ACKNACK, content);
		
		//get result through Intent
		Log.d(LOG_TAG, "Ack/nack popup test complete!");
	}
}
