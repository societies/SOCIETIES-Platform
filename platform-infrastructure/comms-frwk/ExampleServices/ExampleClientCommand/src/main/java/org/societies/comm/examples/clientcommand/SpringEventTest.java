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

import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.event.EventFactory;
import org.societies.comm.xmpp.event.EventStream;
import org.societies.comm.xmpp.event.InternalEvent;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class SpringEventTest implements Runnable, ApplicationListener<InternalEvent> {

	private static Logger LOG = LoggerFactory.getLogger(SpringEventTest.class);
	
	/* (non-Javadoc)
	 * @see org.springframework.context.ApplicationListener#onApplicationEvent(org.springframework.context.ApplicationEvent)  */
	@Override
	public void onApplicationEvent(InternalEvent event) {
		LOG.info(event.getEventNode());
		TestObject obj = (TestObject)event.getEventInfo();
		LOG.info(obj.getName());
	}

	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()  */
	@Override
	public void run() {
		//CREATE EVENT NODE
		EventStream stream1 = EventFactory.getStream("societies.test1");
		EventStream stream2 = EventFactory.getStream("societies.test2");
		
		//SUBSCRIBE
		stream1.addApplicationListener(this);
		stream2.addApplicationListener(this);
		
		//GENERATE PAYLOAD
		TestObject payload1 = new TestObject("John1", "Smith1");
		TestObject payload2 = new TestObject("John2", "Smith2");
		
		//GENERATE EVENT
		InternalEvent event1 = new InternalEvent(this, payload1);
		InternalEvent event2 = new InternalEvent(this, payload2);
		
		stream1.multicastEvent(event1);
		stream2.multicastEvent(event2);
	}

}
