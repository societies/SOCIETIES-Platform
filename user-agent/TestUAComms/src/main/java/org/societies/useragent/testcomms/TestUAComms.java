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

package org.societies.useragent.testcomms;

import java.util.List;
import java.util.concurrent.ExecutionException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.useragent.model.ExpProposalContent;
import org.societies.api.internal.useragent.model.ExpProposalType;
import org.societies.api.internal.useragent.model.ImpProposalContent;
import org.societies.api.internal.useragent.model.ImpProposalType;
import org.societies.useragent.api.remote.IUserAgentRemoteMgr;

public class TestUAComms {

	private IUserAgentRemoteMgr uaRemote;
	private ICommManager commsMgr;
	private ICtxBroker ctxBroker;
	private Logger LOG = LoggerFactory.getLogger(TestUAComms.class);
	private IIdentity myIdentity;
	private String remoteDeviceId;

	public void initService(){

		myIdentity = commsMgr.getIdManager().getThisNetworkNode();
		remoteDeviceId = "XCManager.societies.local";

		//testMonitor();  REMOVED - monitor will never be sent Virgo -> Virgo
		setUID();  //set UID to id of other device [XCManager.societies.local]
		testExplicitFeedback();  //test get explicit feedback from other Virgo device
		testImplicitFeedback();  //test get implicit feedback from other Virgo device
	}

	/*private void testMonitor(){
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("http://testService");
		try {
			serviceId.setIdentifier(new URI("http://testService"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		LOG.info("Calling remote UAM interface");
		IAction action = new Action(serviceId, "testService", "volume", "high");
		uaRemote.monitor(myIdentity, action);

		 This should have caused: 
	 * - an action to be sent to XCManager for storage with ctx snapshot
	 * - the UID to be set to XCManager node
	 * - 

	}*/

	private void setUID(){
		try {
			IndividualCtxEntity personEntity = ctxBroker.retrieveIndividualEntity(myIdentity).get();
			CtxAttribute uidAttr = ctxBroker.createAttribute(personEntity.getId(), CtxAttributeTypes.UID).get();
			ctxBroker.updateAttribute(uidAttr.getId(), remoteDeviceId);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
	}

	private void testExplicitFeedback(){
		//Test ACK/NACK
		String proposalText = "Click YES";
		String[] options1 = {"YES", "NO", "Maybe"};
		ExpProposalContent content = new ExpProposalContent(proposalText, options1);
		try {
			if(uaRemote != null){
				List<String> result = uaRemote.getExplicitFB(ExpProposalType.ACKNACK, content).get();
				LOG.debug("Result = "+result.get(0));
			}else{
				LOG.error("Test1 - uaRemote is null!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		//Test Checkbox
		proposalText = "Select WHITE, BLUE and RED";
		String[] options2 = {"WHITE", "GREEN", "BLACK", "BLUE", "YELLOW", "RED"};
		content = new ExpProposalContent(proposalText, options2);
		try {
			if(uaRemote != null){
				List<String> result = uaRemote.getExplicitFB(ExpProposalType.CHECKBOXLIST, content).get();
				String selected = "";
				for(String next: result){
					selected = selected +", "+ next;
				}
				LOG.debug("Result = "+selected);
			}else{
				LOG.error("Test2 - uaRemote is null!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		//Test RadioButtons
		proposalText = "Select DOG";
		String[] options3 = {"CAT", "PIG", "DOG", "CHICKEN"};
		content = new ExpProposalContent(proposalText, options3);
		try {
			if(uaRemote != null){
				List<String> result = uaRemote.getExplicitFB(ExpProposalType.RADIOLIST, content).get();
				LOG.debug("Result = "+result.get(0));
			}else{
				LOG.error("Test3 - uaRemote is null!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	private void testImplicitFeedback(){
		//Test timeout
		String proposalText = "Do NOT push anything";
		int timeout = 5000;
		ImpProposalContent content = new ImpProposalContent(proposalText, timeout);
		try {
			if(uaRemote != null){
				Boolean result = uaRemote.getImplicitFB(ImpProposalType.TIMED_ABORT, content).get();
				LOG.debug("Result = "+result.booleanValue());
			}else{
				LOG.error("Test4 - uaRemote is null!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

		//Test abort
		proposalText = "Click ABORT";
		timeout = 10000;
		content = new ImpProposalContent(proposalText, timeout);
		try {
			if(uaRemote != null){
				Boolean result = uaRemote.getImplicitFB(ImpProposalType.TIMED_ABORT, content).get();
				LOG.debug("Result = "+result.booleanValue());
			}else{
				LOG.error("Test5 - uaRemote is null!!");
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
	}

	public void setUaRemote(IUserAgentRemoteMgr uaRemote){
		this.uaRemote = uaRemote;
	}

	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}

	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}
}
