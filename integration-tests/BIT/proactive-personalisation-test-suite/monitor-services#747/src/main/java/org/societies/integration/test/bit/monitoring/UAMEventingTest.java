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

package org.societies.integration.test.bit.monitoring;

import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.useragent.monitoring.UIMEvent;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class UAMEventingTest extends EventListener{

	private static Logger LOG = LoggerFactory.getLogger(UAMEventingTest.class);
	private InternalEvent event;

	public void setUp(){
		event = null;
	}

	@Test
	public void test() {
		LOG.info("Monitor services #747 - Running UAMEventingTest");
		//create action
		IIdentity identity = TestCase747.commsMgr.getIdManager().getThisNetworkNode();
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		try {
			serviceId.setIdentifier(new URI("http://testService3"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}
		IAction action1 = new Action(serviceId, "testService", "volume", "high");

		//register for local events
		LOG.info("Monitor services #747 - Subscribing for local events of type UIM_Event");
		/*String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "=newaction)" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/useragent/monitoring)" +
				")";*/
		try{
			TestCase747.eventMgr.subscribeInternalEvent(this, new String[] {EventTypes.UIM_EVENT}, null);
		}catch(Exception e){
			e.printStackTrace();
		}

		//send action
		LOG.info("Monitor services #747 - sending mock action to trigger UAM event");
		TestCase747.uam.monitor(identity, action1);

		//10 second timeout
		int counter = 10;
		while(event == null && counter > 0){
			LOG.info("Monitor services #747 - waiting for event: "+counter);
			try {
				Thread.sleep(1000);
				counter --;
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		Assert.assertNotNull(event);
		Assert.assertEquals("newaction", event.geteventName());
		Assert.assertEquals("org/societies/useragent/monitoring", event.geteventSource());
		Assert.assertEquals(EventTypes.UIM_EVENT, event.geteventType());

		UIMEvent payload = (UIMEvent)event.geteventInfo();
		Assert.assertEquals(identity, payload.getUserId());
		Assert.assertEquals(action1, payload.getAction());
	}


	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		//not needed
	}

	@Override
	public void handleInternalEvent(InternalEvent event) {
		LOG.info("Monitor services #747 - Internal event received!");
		LOG.info("Monitor services #747 - Event type: "+event.geteventType());
		LOG.info("Monitor services #747 - Event name: "+event.geteventName());
		LOG.info("Monitor services #747 - Event source: "+event.geteventSource());
		UIMEvent payload = (UIMEvent)event.geteventInfo();
		LOG.info("Monitor services #747 - Event payload -> Identity: "+payload.getUserId()+", Action: "+payload.getAction());
		this.event = event;
	}

}
