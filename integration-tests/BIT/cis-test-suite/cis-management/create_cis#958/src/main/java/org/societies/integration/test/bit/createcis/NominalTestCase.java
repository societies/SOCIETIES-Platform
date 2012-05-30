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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;



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
		
		String cssId = TestCase958.node.getBareJid();
		
		LOG.info("0. before createCis cssNodeId = " + cssId);
		
		Future<ICisOwned> futureCis = TestCase958.cisManager.createCis(cssId, "admin", "CisTest","trialog" , 2);
		
		LOG.info("1. after createCis");
		
		assertNotNull("futureCis is null", futureCis);
		
		ICisOwned cis = null;
		

		try 
		{
			LOG.info("2. before futureCis.get(10, TimeUnit.SECONDS)");
			cis = futureCis.get(10, TimeUnit.SECONDS);
			LOG.info("3. after futureCis.get(10, TimeUnit.SECONDS)");
		} 
			catch (InterruptedException e) 
			{
				LOG.info("4. InterruptedException "+ e.getMessage());
				e.printStackTrace();
			} 
			catch (ExecutionException e)
			{
				LOG.info("5. ExecutionException "+ e.getMessage());
				e.printStackTrace();
			}
			catch (TimeoutException e) 
			{	
				LOG.info("6. TimeoutException "+ e.getMessage());
				e.printStackTrace();
			}
		

		assertNotNull("cis is null", cis);
		
		LOG.info("7. before cis.getCisId()");
		String cisId =  cis.getCisId();
		LOG.info("8. after cis.getCisId()");
		
		
		
		assertNotNull("cisId is null", cisId);
		
		LOG.info("9. before cisManager.getCis");
		
		ICis cis2 =  TestCase958.cisManager.getCis(TestCase958.node.getJid(), cisId);
		
		LOG.info("10. afeter cisManager.getCis");
		
		assertNotNull("cis is not stored", cis2);
		
		LOG.info("10. CIS name:" + cis2.getName() + " CIS ID: " + cis2.getCisId());
		
		IIdentity cisJidIdentity = null;
		try {
			LOG.info("11. before idMgr.fromJid(cisId)");
			cisJidIdentity = TestCase958.idMgr.fromJid(cisId);
			LOG.info("12. after idMgr.fromJid(cisId)");
		} catch (InvalidFormatException e) {
			
			LOG.info("13. InvalidFormatException "+ e.getMessage());
			e.printStackTrace();
		}
		
		assertNotNull("cisJidIdentity = TestCase958.idMgr.fromJid(cisId) is null", cisJidIdentity);
		
		RequestorCis requestorCis = null;

		
		requestorCis = new RequestorCis(TestCase958.node, cisJidIdentity);

		
		RequestPolicy requestPolicy = null;
		
		try {
			
			LOG.info("14. before privacyPolicyManager.getPrivacyPolicy(requestorCis)");
			requestPolicy =  TestCase958.privacyPolicyManager.getPrivacyPolicy(requestorCis);
			LOG.info("15. after privacyPolicyManager.getPrivacyPolicy(requestorCis)");
		} catch (PrivacyException e) {
			
			LOG.info("16. PrivacyException "+ e.getMessage());
			e.printStackTrace();
		}
		
		assertNotNull("requestPolicy is null, privacy policy not created when ", requestPolicy);
	}
	

	@After
	public void tearDown() {
		LOG.info("###958... tearDown");
	}

}