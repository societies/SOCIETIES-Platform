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
package org.societies.orchestration.CSSDataCollector.main.java;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.identity.IIdentity;
import org.societies.api.osgi.event.*;

/**
 * Describe your class here...
 *
 * @author John
 *
 */

public class CssDCEventPublish implements Subscriber{

	private IEventMgr eventMgr;
	private IIdentity myCssID;
	private Logger LOG = LoggerFactory.getLogger(CssDCEventPublish.class);
	
    public void manageEvent(CtxChangeEvent arg0, IIdentity myCssID, String evtType){
    	LOG.info("publishing event to :   " + myCssID);
    	//send local event
    	CssDCEvent payload = new CssDCEvent(myCssID, arg0, evtType);
    	InternalEvent event = new InternalEvent(EventTypes.CSSDC_EVENT, "newaction", "org/societies/orchestration/CSSDC", payload);
    
    	try {
    		getEventMgr().publishInternalEvent(event);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    	
    /* (non-Javadoc)
     * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
     */
    public void handleExternalEvent(CssDCEvent arg0) {
    	LOG.info("CssDCEventPublish handleExternalEvent error ");   		
    }

    /* (non-Javadoc)
     * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
     */
    public void handleInternalEvent(InternalEvent arg0) {
    	LOG.info("CssDCEventPublish handleInternalEvent error ");
    }
    
    public IEventMgr getEventMgr() {
		return eventMgr;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.pubsub.Subscriber#pubsubEvent(org.societies.api.identity.IIdentity, java.lang.String, java.lang.String, java.lang.Object)
	 */
	@Override
	public void pubsubEvent(IIdentity arg0, String arg1, String arg2,
			Object arg3) {
		// TODO Auto-generated method stub
		
	}
}