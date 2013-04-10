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
package org.societies.privacytrust.privacyprotection.privacynegotiation.policyGeneration.provider;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.societies.api.internal.privacytrust.privacyprotection.IPrivacyPolicyNegotiationManager;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * class that registers and listens for SLM service deployed events and extracts the privacy policy from the bundle of the service that just started
 * @author Elizabeth
 *
 */
public class PolicyRetriever extends EventListener{

	private IPrivacyPolicyNegotiationManager policyMgr;
	private IEventMgr eventMgr;
	
	
	public PolicyRetriever(IPrivacyPolicyNegotiationManager polMgr, IEventMgr eventMgr){
		this.eventMgr = eventMgr;
		
		this.policyMgr = polMgr;
		
		this.subscribe();
	}
	

	private  void subscribe(){
		String[] trackedEventTypes = new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT};

		String[] eventTypes = new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT};
		eventMgr.subscribeInternalEvent(this, eventTypes, null);

	}

	
	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		//TODO: implementation needs to be updated
		/*
		//JOptionPane.showMessageDialog(null, "Received SLM event through PeerEvent");
		if (event.geteventInfo() instanceof EventServiceInfo){
			EventServiceInfo info = (EventServiceInfo) event.geteventInfo();
			if (info.getServiceState() != ServiceState.Deployed) return;

			long bid = info.getBundleId();

			Bundle bundle = context.getBundle(bid);
			// An enumeration of URL objects for each matching entry
			Enumeration entries = bundle.findEntries("OSGI-INF/PrivacyPolicy/", "*.xml", true);

			if (entries!=null){
				if (entries.hasMoreElements()) {
					try {
						URL url = (URL) entries.nextElement();
						BufferedInputStream in = new BufferedInputStream(url.openStream());
						XMLPolicyReader reader = new XMLPolicyReader(this.context);
						RequestPolicy policy = reader.readPolicyFromFile(in);
						if (policy!=null){
							this.policyMgr.addPrivacyPolicyForService(info.getServiceID(), policy);
						}

					} catch (IOException ioe){

					}

				}
			}
		} 
		*/

	}


	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	

}
