package org.societies.integration.test.bit.createcis;

import static org.junit.Assert.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;



import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;



/**
 * @author Rafik
 *
 */
public class NominalTestCase {

	private static Logger LOG = LoggerFactory.getLogger(NominalTestCase.class);
	
	
	public NominalTestCase() {
	}

	@Before
	public void setUp() {
		LOG.info("###958... setUp");

		
	}

	@Test
	public void body1() {
		LOG.info("###958... body1");
		
		Future<ICisOwned> futureCis = TestCase958.cisManager.createCis(TestCase958.node.getJid(), "admin", "CisTest","trialog" , 2);
		
		assertNotNull("futureCis is null", futureCis);
		
		ICisOwned cis = null;
		

		try 
		{
			cis = futureCis.get(10, TimeUnit.SECONDS);
		} 
			catch (InterruptedException e) 
			{
				e.printStackTrace();
			} 
			catch (ExecutionException e)
			{
				e.printStackTrace();
			}
			catch (TimeoutException e) 
			{
				e.printStackTrace();
			}

		assertNotNull("cis is null", cis);
		
		String cisId =  cis.getCisId();
		
		
		assertNotNull("cisId is null", cisId);
		
		ICis cis2 =  TestCase958.cisManager.getCis(TestCase958.node.getJid(), cisId);
		
		assertNotNull("cis is not stored", cis2);
		
		try {
			RequestorCis requestorCis = new RequestorCis(TestCase958.node, TestCase958.idMgr.fromJid(cisId));
		} catch (InvalidFormatException e) {

			e.printStackTrace();
		}
		
		

	}
	

	@After
	public void tearDown() {
		LOG.info("###958... tearDown");
	}

}