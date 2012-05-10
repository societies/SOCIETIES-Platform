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
package org.societies.api.osgi.event; 

import java.io.Serializable;
import org.osgi.service.event.Event;
import org.osgi.service.event.EventConstants;
import org.osgi.service.event.EventHandler;

/**
 * The listener interface for receiving event events.
 * The class that is interested in processing a event
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addEventListener<code> method. When
 * the event event occurs, that object's appropriate
 * method is invoked.
 *
 * @author pkuppuud
 */

public abstract class EventListener implements EventHandler {

	/**
	 * Cannot be overwritten. Implementation provided for abstraction.
	 *
	 * @param event the event {@link org.osgi.service.event.Event}
	 */
	public final void handleEvent(Event event) {
		
	    if (((String)event.getProperty(CSSEventConstants.EVENT_TARGET)).equals(CSSEventConstants.INTERNAL_EVENT)) {
	        handleInternalEvent(new InternalEvent(
                        (String)event.getProperty(EventConstants.EVENT_TOPIC),
                        (String)event.getProperty(CSSEventConstants.EVENT_NAME),
                        (String)event.getProperty(CSSEventConstants.EVENT_SOURCE),
                        (Serializable)event.getProperty(CSSEventConstants.EVENT_INFO)));
	    } else if (((String)event.getProperty(CSSEventConstants.EVENT_TARGET)).equals(CSSEventConstants.EXTERNAL_EVENT)) {
	        handleExternalEvent(new CSSEvent(
                        (String)event.getProperty(EventConstants.EVENT_TOPIC),
                        (String)event.getProperty(CSSEventConstants.EVENT_NAME),
                        (String)event.getProperty(CSSEventConstants.EVENT_SOURCE),
                        (String)event.getProperty(CSSEventConstants.EVENT_INFO)));
	    }
	}

	/**
	 * Handler to handle internal event
	 * Must be implemented by sub class.
	 *
	 * @param event InternalEvent
	 */
	public abstract void handleInternalEvent(InternalEvent event);
	
	/**
	 * Handler to handle external event
	 * Must be implemented by sub class.
	 *
	 * @param event PSSEvent
	 */
	public abstract void handleExternalEvent(CSSEvent event);

}
