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
package org.societies.comm.examples.clientcommand;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;


/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class TestInternalEventing extends EventListener implements Runnable  {

	private static Logger LOG = LoggerFactory.getLogger(TestInternalEventing.class);
	private IEventMgr eventMgr;

	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()  */
	@Override
	public void run() {
		//CREATE EVENT NODE
		String eventFilter = "(&" + 
 			    "(" + CSSEventConstants.EVENT_NAME + "=" + "test_event_name" + ")" + 
 			    "(" + CSSEventConstants.EVENT_SOURCE + "=" + "test_event_source" + ")" + 
				 			  ")";
		
		LOG.info("eventFilter=" + eventFilter);
		LOG.info("*** subscribing to internal event ***");
		getEventMgr().subscribeInternalEvent(this, new String[] {EventTypes.CONTEXT_EVENT}, eventFilter);

		LOG.info("*** creating internal event ***** ");
		TestObject payload = new TestObject("John1", "Smith1");
		InternalEvent event = new InternalEvent(EventTypes.CONTEXT_EVENT, "test_event_name", "test_event_source", payload);	
		 
		try {
			LOG.info("*** publishing internal event ***** ");
			getEventMgr().publishInternalEvent(event);
		} catch (EMSException e) {			
			LOG.info("*** EMS exception while publishing event ***** ");
			e.printStackTrace();
		}
		 
		LOG.info("*** unsubscribing to internal event ***** ");
		getEventMgr().unSubscribeInternalEvent(this, new String[] {EventTypes.CONTEXT_EVENT}, "someFilter");
		LOG.info("*** publishing event after unsubscribe to internal event ***** ");
		
		try {
			LOG.info("**** publishing internal event ***** ");
			getEventMgr().publishInternalEvent(event);
		} catch (EMSException e) {			
			LOG.info("*** EMS exception while publishing event ***** ");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent) */
	@Override
	public void handleExternalEvent(CSSEvent event) {
		LOG.info("*** CSS event received");
	}

	/* (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent) */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		LOG.info("*** internal event received *****");	
		LOG.info("*** event name : "+ event.geteventName());
		LOG.info("*** event source : "+ event.geteventSource());
		LOG.info("*** event type : "+ event.geteventType());
		TestObject payload = (TestObject)event.geteventInfo();
		LOG.info("*** event name : "+ payload.getName());
	}
}
