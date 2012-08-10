package org.societies.integration.test.bit.policynegotiate;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.internal.security.policynegotiator.INegotiation;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.internal.security.policynegotiator.NegotiationException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	private static final long TIME_TO_WAIT_IN_MS = 3000;
	private static final String SERVICE_CLIENT_FILENAME = "Calculator.jar";
	private static final String SERVER_HOSTNAME = "http://localhost:8080";
	private static final String SERVICE_ID = "http://localhost/societies/services/service-1";
	
	private static INegotiation negotiator;
	private static INegotiationProviderServiceMgmt negotiationProviderServiceMgmt;
	
	/**
	 * Tools for integration test
	 */
	private IntegrationTestUtils integrationTestUtils;
	
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	
	private boolean callbackInvokedService = false;
	private boolean callbackInvokedCis = false;
	private boolean callbackInvokedInvalid = false;

	private URI serviceClient;
	

	public NominalTestCaseLowerTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}

	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 * Select the relevant service example: the Calculator
	 * @throws NegotiationException 
	 * @throws URISyntaxException 
	 */
	@BeforeClass
	public static void initialization() throws URISyntaxException, NegotiationException {
		
		LOG.info("[#1001] Initialization");
		LOG.info("[#1001] Prerequisite: The CSS is created");
		LOG.info("[#1001] Prerequisite: The user is logged to the CSS");

		negotiator = TestCase1001.getNegotiator();
		assertNotNull(negotiator);

		negotiationProviderServiceMgmt = TestCase1001.getNegotiationProviderServiceMgmt();
		assertNotNull(negotiationProviderServiceMgmt);
		
		LOG.info("Adding service {}", SERVICE_ID);
		ServiceResourceIdentifier id = new ServiceResourceIdentifier();
		id.setIdentifier(new URI(SERVICE_ID));
		negotiationProviderServiceMgmt.addService(id, null, new URI(SERVER_HOSTNAME), SERVICE_CLIENT_FILENAME);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#1001] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#1001] tearDown");
	}


	/**
	 * Try to consume the service
	 * Part 1: select the service and start it if necessary
	 * @throws InterruptedException 
	 * @throws URISyntaxException 
	 */
	@Test
	public void testNegotiationService() throws InterruptedException, URISyntaxException {
		
		LOG.info("[#1001] testNegotiationService()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity myId = idMgr.getThisNetworkNode();
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI(SERVICE_ID));
		Requestor provider = new RequestorService(myId, serviceId);
		negotiator.startNegotiation(provider, false, new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, URI jar) {
				LOG.info("onNegotiationComplete({}, {})", agreementKey, jar);
				assertNotNull(agreementKey);
				assertTrue(jar.toString().contains(UrlPath.BASE + UrlPath.PATH));
				assertTrue(jar.toString().contains(UrlPath.URL_PARAM_SERVICE_ID + "="));
				assertTrue(!jar.toString().endsWith(UrlPath.URL_PARAM_SERVICE_ID + "="));
				assertTrue(jar.toString().contains(UrlPath.URL_PARAM_SIGNATURE + "="));
				assertTrue(!jar.toString().endsWith(UrlPath.URL_PARAM_SIGNATURE + "="));
				callbackInvokedService = true;
				serviceClient = jar;
			}
			@Override
			public void onNegotiationError(String msg) {
				fail();
			}
		});
		
		LOG.debug("[#1001] testNegotiationService(): negotiation started");

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		LOG.info("[#1001] testNegotiationService(): checking if successful");
		assertTrue(callbackInvokedService);
		LOG.info("[#1001] testNegotiationService(): SUCCESS");
	}

	/**
	 * Try to join a CIS
	 * @throws InterruptedException 
	 */
	@Test
	public void testNegotiationCis() throws InterruptedException {
		
		LOG.info("[#1001] testNegotiationCis()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity myId = idMgr.getThisNetworkNode();
		IIdentity cisId = idMgr.getThisNetworkNode();
		Requestor provider = new RequestorCis(myId, cisId);
		negotiator.startNegotiation(provider, false, new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, URI jar) {
				LOG.info("onNegotiationComplete({}, {})", agreementKey, jar);
				assertNotNull(agreementKey);
				callbackInvokedCis = true;
			}
			@Override
			public void onNegotiationError(String msg) {
				fail();
			}
		});

		LOG.debug("[#1001] testNegotiationCis(): negotiation started");
		
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		LOG.info("[#1001] testNegotiationCis(): checking if successful");
		assertTrue(callbackInvokedCis);
		LOG.info("[#1001] testNegotiationCis(): SUCCESS");
	}

	/**
	 * Negotiation with invalid parameter
	 * @throws InterruptedException 
	 */
	@Test
	public void testNegotiationInvalid() throws InterruptedException {
		
		LOG.info("[#1001] testNegotiationInvalid()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity myId = idMgr.getThisNetworkNode();
		Requestor provider = new Requestor(myId);
		negotiator.startNegotiation(provider, false, new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, URI jar) {
				LOG.info("onNegotiationComplete({})", agreementKey);
				assertNull(agreementKey);
				fail();
			}
			@Override
			public void onNegotiationError(String msg) {
				callbackInvokedInvalid = true;
			}
		});
		
		LOG.debug("[#1001] testNegotiationInvalid(): negotiation started");

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		LOG.info("[#1001] testNegotiationInvalid(): checking if successful");
		assertTrue(callbackInvokedInvalid);
		LOG.info("[#1001] testNegotiationInvalid(): SUCCESS");
	}
	
	/**
	 * Negotiation with invalid parameter
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 * @throws IOException 
	 * @throws MalformedURLException 
	 */
	@Test
	public void testServiceClientDownload() throws InterruptedException, URISyntaxException,
			MalformedURLException, IOException {

		String urlStr;
		int httpCode;
		
		LOG.info("[#1001] testServiceClientDownload()");
		LOG.info("[#1001] *** Domain Authority Rest server is required for this test! ***");

		testNegotiationService();

		InputStream is = getClass().getClassLoader().getResourceAsStream(SERVICE_CLIENT_FILENAME);
		Files.writeFile(is, SERVICE_CLIENT_FILENAME);
		
		// URL with valid signature
		urlStr = serviceClient.toString();
		LOG.info("[#1001] testServiceClientDownload(): URL with valid signature: {}", urlStr);
		httpCode = getHttpCode(new URL(urlStr));
		assertEquals(HttpURLConnection.HTTP_OK, httpCode, 0.0);
		
		// URL with invalid signature
		String sigKeyword = UrlPath.URL_PARAM_SIGNATURE + "=";
		int sigKeywordEnd = serviceClient.toString().indexOf(sigKeyword) + sigKeyword.length();
		urlStr = serviceClient.toString().substring(0, sigKeywordEnd) + "123456789012345678901234567890";
		urlStr += serviceClient.toString().substring(sigKeywordEnd + 30);
		LOG.info("[#1001] testServiceClientDownload(): URL with invalid signature: {}", urlStr);
		assertEquals(serviceClient.toString().length(), urlStr.length(), 0.0);
		httpCode = getHttpCode(new URL(urlStr));
		assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, httpCode, 0.0);
	}
	
	private int getHttpCode(URL resource) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		//HttpURLConnection.setInstanceFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection) resource.openConnection();
		con.setRequestMethod("HEAD");
		return con.getResponseCode();
	}
}
