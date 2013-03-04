/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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
package org.societies.orchestration.CSSDataCollector.main.java;

import java.net.URI;
import java.net.URISyntaxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.broker.ICtxBroker;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.context.api.event.ICtxEventMgr;


/**
 * CSS Data Collector
 * 
 * @author John
 */

public class CSSDataCollector {
	
	private ICtxBroker ctxBroker;
	private ICommManager commsMgr;
	private IIdentity myCssID;
	private IIdentityManager idMgr;
	private Logger LOG = LoggerFactory.getLogger(CSSDataCollector.class);
	private String myDeviceID;
	
	private ICtxEventMgr ctxEventMgr;
	private ServiceResourceIdentifier myServiceID;
	private RequestorService requestorService;
	private IIdentity userIdentity;
	private IIdentity serviceIdentity;
	private CssDCEventPublish cssDcEventPub;

	public CSSDataCollector(ICtxEventMgr ctxEventMgr, ICommManager commMgr)  
	{
		LOG.debug("Starting data collector");
		this.commsMgr = commMgr;
		myDeviceID = commsMgr.getIdManager().getThisNetworkNode().getJid();
		myCssID = commsMgr.getIdManager().getThisNetworkNode();
		myServiceID = new ServiceResourceIdentifier();
		idMgr = commsMgr.getIdManager();
		cssDcEventPub = new CssDCEventPublish();
		//identities
		try {
			this.userIdentity = this.idMgr.getThisNetworkNode();
			this.serviceIdentity = this.idMgr.fromJid(userIdentity.getJid());
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		myServiceID.setServiceInstanceIdentifier("css://" + myDeviceID + "/ContextAware3pService");
		
		try {
			myServiceID.setIdentifier(new URI("css://" + myDeviceID + "/ContextAware3pService"));
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		requestorService = new RequestorService(serviceIdentity, myServiceID);
		//initCtxEvent();

	}
	
	public void registerForContextChanges() {

		LOG.info("*** registerForContextChanges");
		//********************************************************************
		//
		//  TODO - this needs to register for narrower scope of attributes eventually
		//
		//********************************************************************
		try {
			getCtxBroker().registerForChanges(requestorService, new MyCtxChangeEventListener(), null);
			System.out.print("");

		} catch (Exception e) {
			LOG.error(" Context event error");
		} 
		LOG.info("*** registerForContextChanges success");
	}

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onCreation(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent arg0) {
			cssDcEventPub.manageEvent(arg0,myCssID);
			
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onModification(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent arg0) {
			cssDcEventPub.manageEvent(arg0,myCssID);
			
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onRemoval(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent arg0) {
			cssDcEventPub.manageEvent(arg0,myCssID);
			
		}

		/* (non-Javadoc)
		 * @see org.societies.api.context.event.CtxChangeEventListener#onUpdate(org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent arg0) {
			cssDcEventPub.manageEvent(arg0,myCssID);
		}

	}
	
    

    
    public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}
    public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}



    
}
