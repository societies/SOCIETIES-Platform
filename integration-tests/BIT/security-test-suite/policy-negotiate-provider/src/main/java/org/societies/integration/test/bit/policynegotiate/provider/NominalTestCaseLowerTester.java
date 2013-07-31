package org.societies.integration.test.bit.policynegotiate.provider;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.domainauthority.LocalPath;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
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
	private static final String SERVICE_ADDITIONAL_RESOURCE_FILENAME2 = "META-INF/spring/bundle-context.xml";
	// External requirement: service client jar filename may start with "/"
	private static final String SERVICE_CLIENT_FILENAME = "/" + LocalPath.PATH_3P_SERVICES + "/" + SERVICE_CLIENT_BASENAME;
	private static final String SERVICE_ADDITIONAL_RESOURCE_FILENAME = LocalPath.PATH_3P_SERVICES + "/" + "foo.bar";
	
	private static final String SERVICE_ID_1 = "http://localhost/societies/services/service-1";
	private static final String SERVICE_ID_2 = "http://localhost/societies/services/service-2";
	private static final String SERVICE_ID_3 = "http://localhost/societies/services/service-3";
	private static final String SERVICE_ID_4 = "http://localhost/societies/services/service-4";
	
	private static INegotiationProviderServiceMgmt negotiationProviderServiceMgmt;
	private static ICommManager commMgr;
	private static IIdentityManager idMgr;
	private static URI serverUrl;
	
	/**
	 * Tools for integration test
	 */
	private IntegrationTestUtils integrationTestUtils;
	
	/**
	 * Test case number
	 */
	public static int testCaseNumber;
	

	public NominalTestCaseLowerTester() {
		integrationTestUtils = new IntegrationTestUtils();
	}

	/**
	 * This method is called only one time, at the very beginning of the process
	 * (after the constructor) in order to initialize the process.
	 * Select the relevant service example: the Calculator
	 * @throws org.societies.api.internal.security.policynegotiator.NegotiationException 
	 * @throws java.net.URISyntaxException 
	 * @throws InterruptedException 
	 */
	@BeforeClass
	public static void initialization() throws Exception {
		
		LOG.info("[#1879] Initialization");
		LOG.info("[#1879] Prerequisite: The CSS is created");
		LOG.info("[#1879] Prerequisite: The user is logged to the CSS");

		negotiationProviderServiceMgmt = TestCase1001.getNegotiationProviderServiceMgmt();
		assertNotNull(negotiationProviderServiceMgmt);
		
		commMgr = TestCase1001.getCommMgr();
		assertNotNull(commMgr);
		
		idMgr = commMgr.getIdManager();
		assertNotNull(idMgr);
		
		String serverUrlStr = TestCase1001.getServerUrl();
		serverUrl = new URI(serverUrlStr);
	}

	/**
	 * This method is called before every @Test methods.
	 * Verify that the service is installed
	 */
	@Before
	public void setUp() {
		LOG.info("[#1879] NominalTestCaseLowerTester::setUp");
	}

	/**
	 * This method is called after every @Test methods
	 */
	@After
	public void tearDown() {
		LOG.info("[#1879] tearDown");
	}

	@Test
	public void testAddService1() throws Exception {
		List<String> files0 = new ArrayList<String>();
		invokeAddService(SERVICE_ID_1, files0);
	}

	@Test
	public void testAddService2() throws Exception {
		List<String> files1 = new ArrayList<String>();
		files1.add(SERVICE_CLIENT_FILENAME);
		invokeAddService(SERVICE_ID_2, files1);
	}

	@Test
	public void testAddService3() throws Exception {
		List<String> files2 = new ArrayList<String>();
		files2.add(SERVICE_CLIENT_FILENAME);
		files2.add(SERVICE_ADDITIONAL_RESOURCE_FILENAME);
		invokeAddService(SERVICE_ID_3, files2);
	}

	@Test
	public void testAddService4() throws Exception {
		URL[] filesUrl = new URL[2];
		filesUrl[0] = NominalTestCaseLowerTester.class.getClassLoader().getResource(SERVICE_CLIENT_BASENAME);
		filesUrl[1] = NominalTestCaseLowerTester.class.getClassLoader().getResource(SERVICE_ADDITIONAL_RESOURCE_FILENAME2);
		LOG.debug("URL = {}", filesUrl[0]);
		invokeAddService(SERVICE_ID_4, filesUrl);
	}
	
	private void invokeAddService(String serviceId, List<String> files) throws Exception {
		
		LOG.info("invokeAddService({}, List<String>: {})", serviceId, files);
		ServiceResourceIdentifier id = new ServiceResourceIdentifier();
		id.setIdentifier(new URI(serviceId));

		NegotiationProviderSLMCallback callback = new NegotiationProviderSLMCallback();
		negotiationProviderServiceMgmt.addService(id, null, serverUrl, files, callback);
		LOG.debug("invokeAddService(): invoked");
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		assertTrue(callback.isInvoked());
		assertTrue(callback.isSuccessful());
	}

	private void invokeAddService(String serviceId, URL[] files) throws Exception {
		
		LOG.info("invokeAddService({}, URL[]: {})", serviceId, files);
		
		String directory = LocalPath.PATH_3P_SERVICES + File.separator +
				FileName.removeUnsupportedChars(serviceId) + File.separator;
		File file;
		String basename;
		String[] fileNames = new String[files.length];
		
		for (int k = 0; k < files.length; k++) {
		
			basename = FileName.getBasename(files[k].getPath());
			LOG.debug("File basename: {}", basename);
			fileNames[k] = directory + basename;

			file = new File(fileNames[k]);
			if (file.exists()) {
				assertTrue(file.delete());
			}
		}
		
		ServiceResourceIdentifier id = new ServiceResourceIdentifier();
		id.setIdentifier(new URI(serviceId));

		NegotiationProviderSLMCallback callback = new NegotiationProviderSLMCallback();
		negotiationProviderServiceMgmt.addService(id, null, serverUrl, files, callback);
		LOG.debug("invokeAddService(): invoked");
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		assertTrue(callback.isInvoked());
		assertTrue(callback.isSuccessful());
		
		if (idMgr.isMine(idMgr.getDomainAuthorityNode())) {
			LOG.info("Domain Authority node is the local node. Will check if files are created.");
			for (int k = 0; k < files.length; k++) {
				file = new File(fileNames[k]);
				assertTrue("File " + fileNames[k] + " not found", file.exists());
			}
		}
	}
	
	@Test
	public void testManualFilePlacement() throws IOException {

		String fileName = SERVICE_CLIENT_FILENAME;

		LOG.info("[#1879] testManualFilePlacement()");
		LOG.info("[#1879] *** Domain Authority Rest server is required in THIS container! ***");

		if (fileName.startsWith("/")) {
			fileName = fileName.replaceFirst("/", "");
		}
		InputStream is = getClass().getClassLoader().getResourceAsStream(SERVICE_CLIENT_BASENAME);
		assertNotNull(is);
		Files.writeFile(is, fileName);
		is = getClass().getClassLoader().getResourceAsStream(SERVICE_CLIENT_BASENAME);
		assertNotNull(is);
		Files.writeFile(is, SERVICE_ADDITIONAL_RESOURCE_FILENAME);
	}


}
