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
package org.societies.personalisation.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.personalisation.mgmt.IPersonalisationManager;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class TestComms {

	private IPersonalisationManager persoMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ICommManager commsMgr;
	private IIdentity userId;
	private Requestor requestor;
	private IIdentityManager iDM;
	
	public void initialiseTestComms(){
		userId = this.getCommsMgr().getIdManager().getThisNetworkNode();
		requestor = this.getRequestorService();
		String serviceType = "HelloWorld";
		this.logging.debug("Requesting preference from Personalisation Manager");
		Future<IAction> futurePrefAction = this.persoMgr.getPreference(requestor, userId, serviceType, ((RequestorService) requestor).getRequestorServiceId(), "bgColour");
		Future<IAction> futurePrefAction2 = this.persoMgr.getPreference(requestor, userId, serviceType, ((RequestorService) requestor).getRequestorServiceId(), "volume");
		Future<IAction> futureIntentAction = this.persoMgr.getIntentAction(requestor, userId, ((RequestorService) requestor).getRequestorServiceId(), "bgColour");
		Future<IAction> futureIntentAction2 = this.persoMgr.getIntentAction(requestor, userId, ((RequestorService) requestor).getRequestorServiceId(), "volume");
		try {
			IAction prefAction2 = futurePrefAction2.get();
			
			if (prefAction2==null){
				this.logging.debug("persoMgr returned null action for preference request 2");
			}else{
				this.logging.debug("Received pref action 2 from persoMgr: \n"+prefAction2.toString());
			}
			
			
			IAction prefAction = futurePrefAction.get();
			if (prefAction==null){
				this.logging.debug("persoMgr returned null action for preference request 1 ");
			}else{
				this.logging.debug("Received pref action 1 from persoMgr: \n"+prefAction.toString());
			}
			
			IAction intentAction2 = futureIntentAction2.get();
			
			if (intentAction2==null){
				this.logging.debug("persoMgr returned null action for intent request 2 ");
			}else{
				this.logging.debug("Received intent action 2 from persoMgr: \n"+intentAction2.toString());
			}
			
			IAction intentAction = futureIntentAction.get();
			
			if (intentAction==null){
				this.logging.debug("persoMgr returned null action for intent request 1 ");
			}else{
				this.logging.debug("Received intent action 1 from persoMgr: \n"+intentAction.toString());
			}
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		
	}


	
	
	/**
	 * @return the persoMgr
	 */
	public IPersonalisationManager getPersoMgr() {
		return persoMgr;
	}
	/**
	 * @param persoMgr the persoMgr to set
	 */
	public void setPersoMgr(IPersonalisationManager persoMgr) {
		this.persoMgr = persoMgr;
	}
	private RequestorService getRequestorService(){
		try {
		IIdentity requestorId = this.iDM.fromJid("eliza.societies.org");
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setServiceInstanceIdentifier("css://eliza.societies.org/HelloEarth");
		
			serviceId.setIdentifier(new URI("css://eliza.societies.org/HelloEarth"));
			return new RequestorService(requestorId, serviceId);
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
	private RequestorCis getRequestorCis(){
		try {
			IIdentity requestorId = this.iDM.fromJid("eliza.societies.org");
			IIdentity cisId = this.iDM.fromJid("Holidays@societies.org");
			return new RequestorCis(requestorId, cisId);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}




	/**
	 * @return the commsMgr
	 */
	public ICommManager getCommsMgr() {
		return commsMgr;
	}


	/**
	 * @param commsMgr the commsMgr to set
	 */
	public void setCommsMgr(ICommManager commsMgr) {
		this.commsMgr = commsMgr;
		this.iDM = commsMgr.getIdManager();
	}
	
}
