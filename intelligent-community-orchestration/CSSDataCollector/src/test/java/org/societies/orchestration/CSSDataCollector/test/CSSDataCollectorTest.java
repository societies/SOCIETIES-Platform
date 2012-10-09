/**
* Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
* (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
* informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
* COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
* INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
* ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
* All rights reserved.
*
* Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
* conditions are met:
*
* 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
*
* 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
* disclaimer in the documentation and/or other materials provided with the distribution.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
* BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
* SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
* INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
* NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
*/
package org.societies.orchestration.CSSDataCollector.test;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxEvent;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.orchestration.CSSDataCollector.main.java.CSSDataCollector;
//import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.context.api.event.CtxEventScope;
//import org.societies.context.api.event.ICtxEventMgr;
//import org.societies.context.broker.api.security.ICtxAccessController;
//import org.societies.context.broker.impl.CtxBroker;
//import org.societies.context.broker.impl.InternalCtxBroker;
//import org.societies.context.user.db.impl.UserCtxDBMgr;
//import org.societies.context.userHistory.impl.UserContextHistoryManagement;


import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;
import org.junit.BeforeClass;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Context event subscription/publishing example for CM components
 */

public class CSSDataCollectorTest {

	private static final Logger LOG = LoggerFactory.getLogger(CSSDataCollectorTest.class);
	//private CSSDataCollector cdc;
	//private ICtxEventMgr ctxEventMgr = mock(ICtxEventMgr.class);;
	private static ICommManager mockCommMgr = mock(ICommManager.class);
	private static IIdentityManager mockIdManager = mock(IIdentityManager.class);
	private static INetworkNode mockNetworkNode = mock(INetworkNode.class);
	private static IIdentity mockIIdentity = mock(IIdentity.class);
	//
	private ICtxBroker ctxBroker;
	private static IIdentityManager mockIdentityMgr = mock(IIdentityManager.class);
	private static IIdentity mockIdentityLocal = mock(IIdentity.class);
	//private static ICtxAccessController mockCtxAccessController = mock(ICtxAccessController.class);
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		when(mockCommMgr.getIdManager()).thenReturn(mockIdManager);
		when(mockIdManager.getThisNetworkNode()).thenReturn(mockNetworkNode);
		when(mockIdManager.fromJid(null)).thenReturn(mockIIdentity);
		when(mockCommMgr.getIdManager().getThisNetworkNode().getJid()).thenReturn("myCIS.societies.local");		
	}
	


	@Test
	public void testsetup() throws CtxException{
		LOG.info("Starting test set up");
		ICtxBroker internalCtxBroker;// = new InternalCtxBroker();

		//internalCtxBroker.setUserCtxDBMgr(new UserCtxDBMgr());
		//internalCtxBroker.setUserCtxHistoryMgr(new UserContextHistoryManagement());
		//internalCtxBroker.setIdentityMgr(mockIdentityMgr);
		//internalCtxBroker.createIndividualEntity(mockIdentityLocal, CtxEntityTypes.PERSON); // TODO remove?
		//internalCtxBroker.createCssNode(mockNetworkNode); // TODO remove?

		//ctxBroker = new CtxBroker(internalCtxBroker);
		//ctxBroker.setIdentityMgr(mockIdentityMgr);
		//ctxBroker.setCtxAccessController(mockCtxAccessController);
		
		//cdc = new CSSDataCollector(ctxEventMgr, mockCommMgr);
		//cdc.registerForContextChanges();
		
	}
	
	@Test
	public void testRegister(){

		LOG.info("Starting Register tests");
		//cdc = new CSSDataCollector(mockCommMgr);
		//cdc.setCtxBroker(ctxBroker);
		//cdc.registerForContextChanges();
	}
	
	//@Test
	//public void testEventCreate() throws CtxException{
	//	LOG.info("test Event Create");
		//mockctxIdentifier.setOwnerId("myCIS.societies.local");
		//mockctxIdentifier.setType("ATTRIBUTE");
		//mockctxIdentifier.setUri("myCIS@societies.local");
		//mockCtxChangeEvent = new CtxChangeEvent(mockctxIdentifier);
		//String[] topics = {"Random"};
		//ctxEventMgr.post((CtxEvent)mockCtxChangeEvent, topics, mockCtxEventScope.LOCAL);
		//cdc = new CSSDataCollector(ctxEventMgr, mockCommMgr);
	//}
	
	//@Test
	//public void testEventUpdate(){
	//	LOG.info("test Event Update");
		//cdc = new CSSDataCollector(ctxEventMgr, mockCommMgr);
	//}
}