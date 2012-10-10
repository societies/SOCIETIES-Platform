package org.societies.integration.test.bit.policynegotiate;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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
	
	private static final String SERVICE_CLIENT_BASENAME = "Calculator.jar";
	// External requirement: service client jar filename may start with "/"
	private static final String SERVICE_CLIENT_FILENAME = "/3p-service/" + SERVICE_CLIENT_BASENAME;
	private static final String SERVICE_ADDITIONAL_RESOURCE_FILENAME = "3p-service/" + "foo.bar";
	
	private static final String SERVER_HOSTNAME = "http://localhost:8080";
	
	private static final String SERVICE_ID_1 = "http://localhost/societies/services/service-1";
	private static final String SERVICE_ID_2 = "http://localhost/societies/services/service-2";
	private static final String SERVICE_ID_3 = "http://localhost/societies/services/service-3";
	
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

	private List<URI> serviceFiles;
	

	public NominalTestCaseLowerTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}

	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 * Select the relevant service example: the Calculator
	 * @throws NegotiationException 
	 * @throws URISyntaxException 
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void initialization() throws Exception {
		
		LOG.info("[#1001] Initialization");
		LOG.info("[#1001] Prerequisite: The CSS is created");
		LOG.info("[#1001] Prerequisite: The user is logged to the CSS");

		negotiator = TestCase1001.getNegotiator();
		assertNotNull(negotiator);

		negotiationProviderServiceMgmt = TestCase1001.getNegotiationProviderServiceMgmt();
		assertNotNull(negotiationProviderServiceMgmt);
		
		List<String> files0 = new ArrayList<String>();
		invokeAddService(SERVICE_ID_1, files0);

		List<String> files1 = new ArrayList<String>();
		files1.add(SERVICE_CLIENT_FILENAME);
		invokeAddService(SERVICE_ID_2, files1);

		List<String> files2 = new ArrayList<String>();
		files2.add(SERVICE_CLIENT_FILENAME);
		files2.add(SERVICE_ADDITIONAL_RESOURCE_FILENAME);
		invokeAddService(SERVICE_ID_3, files2);
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

	private static void invokeAddService(String serviceId, List<String> files) throws Exception {
		
		LOG.info("Adding service {}", serviceId);
		ServiceResourceIdentifier id = new ServiceResourceIdentifier();
		id.setIdentifier(new URI(serviceId));

		NegotiationProviderSLMCallback callback = new NegotiationProviderSLMCallback();
		negotiationProviderServiceMgmt.addService(id, null, new URI(SERVER_HOSTNAME), files, callback);
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		assertTrue(callback.isInvoked());
		assertTrue(callback.isSuccessful());
	}

	/**
	 * Try to consume the service
	 * Part 1: select the service and start it if necessary
	 * @throws InterruptedException 
	 * @throws URISyntaxException 
	 */
	@Test
	public void testNegotiationServiceWith0Files() throws InterruptedException, URISyntaxException {
		
		LOG.info("[#1001] testNegotiationServiceWith0Files()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity myId = idMgr.getThisNetworkNode();
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI(SERVICE_ID_1));
		Requestor provider = new RequestorService(myId, serviceId);

		negotiator.startNegotiation(provider, false, new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
				LOG.info("onNegotiationComplete({}, {})", agreementKey, fileUris);
				assertNotNull(agreementKey);
				assertNotNull(fileUris);
				assertEquals(0, fileUris.size(), 0.0);
				callbackInvokedService = true;
				serviceFiles = fileUris;
			}
			@Override
			public void onNegotiationError(String msg) {
				fail();
			}
		});
		
		LOG.debug("[#1001] testNegotiationServiceWith0Files(): negotiation started");

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		LOG.info("[#1001] testNegotiationServiceWith0Files(): checking if successful");
		assertTrue(callbackInvokedService);
		LOG.info("[#1001] testNegotiationServiceWith0Files(): SUCCESS");
	}

	@Test
	public void testNegotiationServiceWith2Files() throws InterruptedException, URISyntaxException {
		
		LOG.info("[#1001] testNegotiationServiceWith2Files()");

		IIdentityManager idMgr = TestCase1001.getGroupMgr().getIdMgr();
		IIdentity myId = idMgr.getThisNetworkNode();
		ServiceResourceIdentifier serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI(SERVICE_ID_3));
		Requestor provider = new RequestorService(myId, serviceId);

		negotiator.startNegotiation(provider, false, new INegotiationCallback() {
			@Override
			public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
				LOG.info("onNegotiationComplete({}, {})", agreementKey, fileUris);
				assertNotNull(agreementKey);
				assertNotNull(fileUris);
				assertEquals(2, fileUris.size(), 0.0);
				for (URI f : fileUris) {
					assertTrue( f.toString().contains(UrlPath.BASE + UrlPath.PATH));
					assertTrue( f.toString().contains(UrlPath.URL_PARAM_SERVICE_ID + "="));
					assertFalse(f.toString().endsWith(UrlPath.URL_PARAM_SERVICE_ID + "="));
					assertTrue( f.toString().contains(UrlPath.URL_PARAM_SIGNATURE + "="));
					assertFalse(f.toString().endsWith(UrlPath.URL_PARAM_SIGNATURE + "="));
				}
				callbackInvokedService = true;
				serviceFiles = fileUris;
			}
			@Override
			public void onNegotiationError(String msg) {
				fail();
			}
		});
		
		LOG.debug("[#1001] testNegotiationServiceWith2Files(): negotiation started");

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		LOG.info("[#1001] testNegotiationServiceWith2Files(): checking if successful");
		assertTrue(callbackInvokedService);
		LOG.info("[#1001] testNegotiationServiceWith2Files(): SUCCESS");
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
			public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
				LOG.info("onNegotiationComplete({}, {})", agreementKey, fileUris);
				assertNotNull(agreementKey);
				if (fileUris != null) {
					assertEquals(0, fileUris.size(), 0.0);
				}
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
			public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
				LOG.error("onNegotiationComplete({})", agreementKey);
				fail();
			}
			@Override
			public void onNegotiationError(String msg) {
				LOG.info("onNegotiationError({}): This error is supposed to happen", msg);
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
		String fileName = SERVICE_CLIENT_FILENAME;
		
		LOG.info("[#1001] testServiceClientDownload()");
		LOG.info("[#1001] *** Domain Authority Rest server is required for this test! ***");

		testNegotiationServiceWith2Files();

		if (fileName.startsWith("/")) {
			fileName = fileName.replaceFirst("/", "");
		}
		InputStream is = getClass().getClassLoader().getResourceAsStream(SERVICE_CLIENT_BASENAME);
		assertNotNull(is);
		Files.writeFile(is, fileName);
		is = getClass().getClassLoader().getResourceAsStream(SERVICE_CLIENT_BASENAME);
		assertNotNull(is);
		Files.writeFile(is, SERVICE_ADDITIONAL_RESOURCE_FILENAME);
		
		// URL with valid signature
		urlStr = serviceFiles.get(0).toString();
		LOG.info("[#1001] testServiceClientDownload(): URL with valid signature: {}", urlStr);
		httpCode = getHttpCode(new URL(urlStr));
		assertEquals(HttpURLConnection.HTTP_OK, httpCode, 0.0);
		
		// URL 2 with valid signature
		urlStr = serviceFiles.get(1).toString();
		LOG.info("[#1001] testServiceClientDownload(): URL with valid signature: {}", urlStr);
		assertFalse(serviceFiles.get(0).toString().equals(serviceFiles.get(1).toString()));
		httpCode = getHttpCode(new URL(urlStr));
		assertEquals(HttpURLConnection.HTTP_OK, httpCode, 0.0);
		
		// URL with invalid signature
		String sigKeyword = UrlPath.URL_PARAM_SIGNATURE + "=";
		int sigKeywordEnd = serviceFiles.get(0).toString().indexOf(sigKeyword) + sigKeyword.length();
		urlStr = serviceFiles.get(0).toString().substring(0, sigKeywordEnd) + "123456789012345678901234567890";
		urlStr += serviceFiles.get(0).toString().substring(sigKeywordEnd + 30);
		LOG.info("[#1001] testServiceClientDownload(): URL with invalid signature: {}", urlStr);
		assertEquals(serviceFiles.get(0).toString().length(), urlStr.length(), 0.0);
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
