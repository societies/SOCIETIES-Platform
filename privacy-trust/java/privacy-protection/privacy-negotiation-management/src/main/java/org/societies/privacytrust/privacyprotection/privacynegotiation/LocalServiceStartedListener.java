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

package org.societies.privacytrust.privacyprotection.privacynegotiation;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
//import org.societies.privacytrust.privacyprotection.privacynegotiation.negotiation.provider.PrivacyPolicyRegistryManager;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;


public class LocalServiceStartedListener extends EventListener{

	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private final IIdentityManager iDM;
	//private final IIdentity userID;
	//private PrivacyPolicyRegistryManager privacyPolicyRegMgr;
	
	public LocalServiceStartedListener(IEventMgr eventMgr, IIdentityManager IDM /*PrivacyPolicyRegistryManager privacyPolicyRegMgr*/){

		iDM = IDM;
		//this.privacyPolicyRegMgr = privacyPolicyRegMgr;
		/**
		 * TODO: need local user id!
		 * this.userID = iDM.getLocalDigitalPersonalIdentifier();
		 */
		
		eventMgr.subscribeInternalEvent(this, new String[] {EventTypes.SERVICE_SESSION_EVENT}, null);
		logging.debug("Registered for events: "+EventTypes.SERVICE_SESSION_EVENT);
	}
	

	@Override
	public void handleInternalEvent(InternalEvent event) {
		if (event.geteventType().equals(EventTypes.SERVICE_SESSION_EVENT)){
			/**
			 * TODO: Ask Alan?
			 */
			/*SessionEvent sessionEvt = (SessionEvent) event.geteventInfo();


			if (sessionEvt.getSessionType().equals(SessionEventType.SessionCreated)){
				SessionPlan sp = sessionEvt.getSessionPlan();
				SessionPlanNode[] nodes = sp.getServices();
				for (SessionPlanNode node : nodes){
					this.logging.debug("Processing: "+sessionEvt.getSessionType());
					if (node.getUserDPI().equals(userID)){
						PssService service = node.getService();
						IServiceIdentifier serviceID = service.getServiceId();
						RequestPolicy policy = this.privacyPolicyRegMgr.getPolicy(serviceID);
						if (policy!=null){
							this.mapAttributesToLocalIdentity(policy);
						}else{
							this.logging.debug("Service: "+serviceID.toUriString()+" does not have a Privacy Policy");
						}
					}


				}
			}*/
		}

	}
	
	private void mapAttributesToLocalIdentity(RequestPolicy policy){
		List<RequestItem> requestItems = policy.getRequests();
		List<String> contextTypes = new ArrayList<String>();
		
		for (RequestItem item : requestItems){
			contextTypes.add(item.getResource().getDataType());
		}
		/**
		 * TODO: adapt!
		this.iDM.addMappedCtxIdentifiersToLocalDigitalPersonalIdentifier(contextTypes);
		*/
	}


	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}

}
