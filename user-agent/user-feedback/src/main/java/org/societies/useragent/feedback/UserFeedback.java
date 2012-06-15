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
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.useragent.feedback.guis.AckNackGUI;
import org.societies.useragent.feedback.guis.CheckBoxGUI;
import org.societies.useragent.feedback.guis.RadioGUI;
import org.societies.useragent.feedback.guis.TimedGUI;
import org.springframework.scheduling.annotation.AsyncResult;

public class UserFeedback implements IUserFeedback{
	
	Logger LOG = LoggerFactory.getLogger(UserFeedback.class);
	
	public void initialiseUserFeedback(){
		LOG.debug("User Feedback initialised!!");
	}
	
	@Override
	public Future<List<String>> getExplicitFB(int type, ExpProposalContent content) {
		LOG.debug("Returning explicit feedback");
		List<String> result = null;
		String proposalText = content.getProposalText();
		String[] options = content.getOptions();
		if(type == ExpProposalType.RADIOLIST){
			LOG.debug("Radio list GUI");
			RadioGUI gui = new RadioGUI();
			result = gui.displayGUI(proposalText, options);
		}else if(type == ExpProposalType.CHECKBOXLIST){
			LOG.debug("Check box list GUI");
			CheckBoxGUI gui = new CheckBoxGUI();
			result = gui.displayGUI(proposalText, options);
		}else{ //ACK-NACK
			LOG.debug("ACK/NACK GUI");
			result = AckNackGUI.displayGUI(proposalText, options);
		}
		
		return new AsyncResult<List<String>>(result);
	}

	@Override
	public Future<Boolean> getImplicitFB(int type, ImpProposalContent content) {
		LOG.debug("Returning implicit feedback");
		Boolean result = null;
		String proposalText = content.getProposalText();
		int timeout = content.getTimeout();
		if(type == ImpProposalType.TIMED_ABORT){
			LOG.debug("Timed Abort GUI");
			TimedGUI gui = new TimedGUI();
			result = gui.displayGUI(proposalText, timeout);
		}
		
		return new AsyncResult<Boolean>(result);
	}	
}