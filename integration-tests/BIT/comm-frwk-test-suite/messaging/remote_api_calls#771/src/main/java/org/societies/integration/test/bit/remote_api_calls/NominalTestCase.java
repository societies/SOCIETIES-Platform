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
				callback.wait(9*1000);
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