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
package org.societies.comm.event.consumer;

import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;

/**
 * 
 * @author pkuppuud
 *
 */
public class EventManagementTest extends EventListener {
	
	private IEventMgr eventMgr;

	public IEventMgr getEventMgr() {
		return eventMgr;
	}

	public void setEventMgr(IEventMgr eventMgr) {
		this.eventMgr = eventMgr;
	}

	public void initMethod(){
		
		 String eventFilter = "(&" + "(" + CSSEventConstants.EVENT_NAME + "="
	                + "test_event_name" + ")" + "(" + CSSEventConstants.EVENT_SOURCE
	                + "=" + "test_event_source" + ")" + ")";
		
		System.out.println(" ***** subscribing to internal event ***** ");
		getEventMgr().subscribeInternalEvent(this, new String[] {EventTypes.CONTEXT_EVENT}, eventFilter);
		System.out.println(" ***** creating internal event ***** ");
		InternalEvent event = new InternalEvent(EventTypes.CONTEXT_EVENT, "test_event_name", "test_event_source", new String("content"));	
		try {
			System.out.println(" ***** publishing internal event ***** ");
			getEventMgr().publishInternalEvent(event);
		} catch (EMSException e) {			
			System.out.println(" ***** EMS exception while publishing event ***** ");
			e.printStackTrace();
		}
		System.out.println(" ***** unsubscribing to internal event ***** ");
		getEventMgr().unSubscribeInternalEvent(this, new String[] {EventTypes.CONTEXT_EVENT}, "someFilter");
		System.out.println(" ***** publishing event after unsubscribe to internal event ***** ");
		
		try {
			System.out.println(" ***** publishing internal event ***** ");
			getEventMgr().publishInternalEvent(event);
		} catch (EMSException e) {			
			System.out.println(" ***** EMS exception while publishing event ***** ");
			e.printStackTrace();
		}
	}
	
	@Override
	public void handleInternalEvent(InternalEvent event) {
		System.out.println(" ***** internal event received *****");	
		System.out.println(" ***** event name : "+event.geteventName());
		System.out.println(" ***** event source : "+event.geteventSource());
		System.out.println(" ***** event type : "+event.geteventType());
		System.out.println(" ***** event name : "+(String) event.geteventInfo());
		
	}

	@Override
	public void handleExternalEvent(CSSEvent event) {
		System.out.println("CSS event received");		
	}	
}