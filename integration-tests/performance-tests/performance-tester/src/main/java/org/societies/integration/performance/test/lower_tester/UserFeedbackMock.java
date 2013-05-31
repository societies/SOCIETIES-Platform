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
package org.societies.integration.performance.test.lower_tester;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.schema.useragent.feedback.UserFeedbackBean;

/**
 * @author Rafik
 *
 */

@Deprecated
public class UserFeedbackMock implements Subscriber{
	
	private IUserFeedback userFeedback;
	private ICommManager commsMgr;
	private PubsubClient pubsub;
	private IIdentity myCloudID;
	private static Logger LOG = LoggerFactory.getLogger(UserFeedbackMock.class);
	
	
	public UserFeedbackMock() {
	}
	
	public void setCommsMgr(ICommManager commsMgr){
		LOG.info("### [UserFeedbackMock] commsMgr injected");
		this.commsMgr = commsMgr;
	}
	
	public void setPubsub(PubsubClient pubsub){
		LOG.info("### [UserFeedbackMock] pubsub injected");
		this.pubsub = pubsub;
	}
	
	public void setUserFeedback(IUserFeedback userFeedback) {
		LOG.info("### [UserFeedbackMock] userFeedback injected");
		this.userFeedback = userFeedback;
	}

	public void init() {
		
		LOG.info("### [UserFeedbackMock] init method");
		//get cloud ID
				myCloudID = commsMgr.getIdManager().getThisNetworkNode();
				LOG.debug("### [UserFeedbackMock] Got my cloud ID: "+myCloudID);
		
		//register for events from created pubsub node
		try {
				LOG.debug("### [UserFeedbackMock] Registering for user feedback pubsub node");
				
				pubsub.subscriberSubscribe(myCloudID, "org/societies/useragent/feedback/event/REQUEST", this);
				//pubsub.subscriberSubscribe(myCloudID, "org/societies/useragent/feedback/event/EXPLICIT_RESPONSE", this);
				//pubsub.subscriberSubscribe(myCloudID, "org/societies/useragent/feedback/event/IMPLICIT_RESPONSE", this);
				
				LOG.debug("### [UserFeedbackMock] Pubsub registration complete!");
			} catch (XMPPError e) {
				e.printStackTrace();
			} catch (CommunicationException e) {
				e.printStackTrace();
			}
	}
	
	@Override
	public void pubsubEvent(IIdentity identity, String eventTopic, String itemID, Object item) {
		LOG.debug("+++ [UserFeedbackMock] Received pubsub event with topic: "+eventTopic);
		if(eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/REQUEST"))
		{
			//read from request bean
			UserFeedbackBean ufBean = (UserFeedbackBean)item;
			switch(ufBean.getMethod()){
			case GET_EXPLICIT_FB:
					
				LOG.info("+++ [UserFeedbackMock] EXPLICIT_FB");
				List<String> result;
				
				if(ufBean.getType() == ExpProposalType.RADIOLIST)
				{
					for (String string : ufBean.getOptions()) 
					{
						LOG.info("+++ [UserFeedbackMock] option: " + string);
					}	
//					result = new ArrayList<String>();						
//					result.add("Proceed");
//					userFeedback.submitExplicitResponse(ufBean.getRequestId(), result);
					
				}
				else if(ufBean.getType() == ExpProposalType.CHECKBOXLIST)
				{
					
					for (String string : ufBean.getOptions()) 
					{
						LOG.info("+++ [UserFeedbackMock] option: " + string);
					}
//					result = new ArrayList<String>();	
//					result.add("");
//					userFeedback.submitExplicitResponse(ufBean.getRequestId(), result);
				}
				else if(ufBean.getType() == ExpProposalType.ACKNACK)
				{	
					for (String string : ufBean.getOptions()) 
					{
						LOG.info("+++ [UserFeedbackMock] option: " + string);
					}
					result = new ArrayList<String>();
					result.add("Proceed");
					userFeedback.submitExplicitResponse(ufBean.getRequestId(), result);

				}
				else
				{
					LOG.error("+++ [UserFeedbackMock] Could not understand this type of explicit request: "+ ufBean.getType());
				}
				
				break;
			case GET_IMPLICIT_FB:
				LOG.info("+++ [UserFeedbackMock] IMPLICIT_FB");
				
				for (String string : ufBean.getOptions()) 
				{
					LOG.info("+++ [UserFeedbackMock] option: " + string);
				}
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
				
				break;
			case SHOW_NOTIFICATION:	
				LOG.info("+++ [UserFeedbackMock] SHOW_NOTIFICATION");
				
				userFeedback.submitImplicitResponse(ufBean.getRequestId(), true);
			}
		}
//		else if(eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/EXPLICIT_RESPONSE"))
//		{
//			
//		}
//		else if(eventTopic.equalsIgnoreCase("org/societies/useragent/feedback/event/IMPLICIT_RESPONSE"))
//		{
//			
//		}
	}

}
