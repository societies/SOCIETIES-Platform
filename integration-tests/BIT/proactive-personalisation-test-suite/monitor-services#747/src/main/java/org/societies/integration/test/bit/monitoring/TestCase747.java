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

import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.integration.test.IntegrationTestCase;
import org.societies.api.comm.xmpp.interfaces.ICommManager;

public class TestCase747 extends IntegrationTestCase{
	
	public static ICtxBroker ctxBroker;
	public static IUserActionMonitor uam;
	public static ICommManager commsMgr;
	public static IEventMgr eventMgr;

	public TestCase747(){
		super(747, new Class[]{ContextStorageTest.class, UAMEventingTest.class, UIDUpdateTest.class});
	}

	public void setCtxBroker(ICtxBroker ctxBroker){
		this.ctxBroker = ctxBroker;
	}
	
	public void setUam(IUserActionMonitor uam){
		this.uam = uam;
	}
	
	public void setCommsMgr(ICommManager commsMgr){
		this.commsMgr = commsMgr;
	}
	
	public void setEventMgr(IEventMgr eventMgr){
		this.eventMgr = eventMgr;
	}
	
	/*protected static ICtxBroker getCtxBroker(){
		return TestCase747.ctxBroker;
	}
	
	protected static IUserActionMonitor getUam(){
		return TestCase747.uam;
	}
	
	protected static IEventMgr getEventMgr(){
		return TestCase747.eventMgr;
	}*/
}
