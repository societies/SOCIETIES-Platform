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
package org.societies.integration.test.bit.remote_api_calls;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.identity.IIdentity;
import org.societies.api.schema.examples.calculatorbean.CalcBean;
import org.societies.api.schema.examples.calculatorbean.MethodType;

public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	private Stanza stanza;
	private CommsClientCallback callback;



	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###771... setUp");

		//GET CURRENT NODE IDENTITY
		IIdentity toIdentity = TestCase771.idMgr.getThisNetworkNode();
		
		LOG.info("###771... getThisNetworkNode: " + toIdentity.getJid());
		
		stanza = new Stanza(toIdentity);

		//SETUP CALC CLIENT RETURN STUFF
		callback = new CommsClientCallback();
	}

	@Test
	public void body1() {
		LOG.info("###771... body1");

		//CREATE MESSAGE BEAN
		CalcBean calc = new CalcBean();
		calc.setA(1); 
		calc.setB(2);
		calc.setMethod(MethodType.ADD);
		calc.setMessage("Testing body1 - Adding");
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			LOG.info("***771... sendIQGet: ADD(1,2)");
			TestCase771.commManager.sendIQGet(stanza, calc, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};


		synchronized (callback) {
			LOG.info("Body 1 waiting...");

			try {
				callback.wait(15*1000);
			} catch (InterruptedException e) {
				LOG.info("Body 1 InterruptedException: " + e.getMessage());
				fail("InterruptedException");
				e.printStackTrace();
			}
		}

		// if the method times out, fail
		assertNotNull("Timed out", callback.getCalcResult());
				
		// otherwise, check the result
		// Get the result
		Integer result = callback.getCalcResult().getResult();
		LOG.info("Body1 result: " + result);	
		assertEquals(new Integer(3), result);
	}

	@Test
	public void body2() {
		LOG.info("###771... body2");

		//CREATE MESSAGE BEAN
		CalcBean calc = new CalcBean();
		calc.setA(2); 
		calc.setB(1);
		calc.setMethod(MethodType.SUBTRACT);
		calc.setMessage("Testing body2 - subtract");
		try {
			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback.RecieveMessage()"
			LOG.info("***771... sendIQGet: SUBTRACT(2,1)");
			TestCase771.commManager.sendIQGet(stanza, calc, callback);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};

		synchronized (callback) {

			LOG.info("Body 2 waiting...");
			
			try {
				callback.wait(15*1000);
			} catch (InterruptedException e) {
				LOG.info("Body 2 InterruptedException: " + e.getMessage());
				fail("InterruptedException");
				e.printStackTrace();
			}
		}

		
		// if the method times out, fail
		assertNotNull("Timed out", callback.getCalcResult());
		
		// otherwise, check the result
		// Get the result
		Integer result = callback.getCalcResult().getResult();
		
		LOG.info("Body2 result: " + result);
		
		assertEquals(new Integer(1), result);
	}

	@After
	public void tearDown() {
		LOG.info("###771... tearDown");
 	}
}