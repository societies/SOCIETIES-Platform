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
package org.societies.webapp.controller.rfid;

import java.util.Hashtable;

import javax.faces.application.ViewHandler;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * @author Eliza
 *
 */
public class RfidEventListener extends EventListener{

	protected final Logger log = LoggerFactory.getLogger(getClass()); 
	private RFidClientController controller;
	private static final String RFID_EVENT_TYPE = "org/societies/rfid";
	public RfidEventListener(RFidClientController controller, IEventMgr eventMgr){
		this.controller = controller;
		eventMgr.subscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		if(log.isDebugEnabled()) log.debug("123456789 - subscribed");
	}

	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		log.debug("HERE - INTERNAL EVENT!" + event.geteventName());
		if (event.geteventType().equals(RFID_EVENT_TYPE) && (event.geteventName().equals("registrationResult"))){
			Hashtable<String, CtxAttribute> information = (Hashtable<String, CtxAttribute>) event.geteventInfo();
			log.debug("HERE - INTERNAL EVENT!" + information.toString());
			//boolean registered = false;
			if (information.containsKey("RFID_REGISTERED")){
				controller.retrieveRfidInfo();
			}

		}
	}

	public void unsubscribe(){
		this.controller.getEventManager().unSubscribeInternalEvent(this, new String[]{RFID_EVENT_TYPE}, null);
		if(log.isDebugEnabled()) log.debug("123456789 - unsubscribed");
	}

}
