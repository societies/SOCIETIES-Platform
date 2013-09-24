package org.societies.integration.test.bit.communitysign;

import static org.junit.Assert.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.cert.X509Certificate;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.integration.test.IntegrationTestUtils;

/**
 * @author Mitja Vardjan
 *
 */
public class NominalTestCaseLowerTester {
	
	private static Logger LOG = LoggerFactory.getLogger(NominalTestCaseLowerTester.class);

	/**
	 * Tools for integration test
	 */
	private IntegrationTestUtils integrationTestUtils;
	
	private static String daUrl;
	private static ISignatureMgr signatureMgr;
	private static IIdentityManager identityManager;
	
	private static final String id1 = "id1";
	private static final String xml = "<xml><node1 Id='" + id1 + "'>abc</node1></xml>";
	private static final String path = "foo.xml";
	
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
		
		daUrl = TestCase1001.getDaUrl();
		
		signatureMgr = TestCase1001.getSignatureMgr();
		assertNotNull(signatureMgr);
		
		identityManager = TestCase1001.getIdentityManager();
		assertNotNull(identityManager);
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
	public void testDocumentUploadDownload() throws Exception {

		String urlStr;
		int httpCode;
		
		LOG.info("[#1879] testDocumentUploadDownload()");
		LOG.info("[#1879] *** Domain Authority Rest server is required for this test! ***");

		// URL for initial upload
		X509Certificate cert = signatureMgr.getCertificate(identityManager.getThisNetworkNode());
		String certStr = signatureMgr.cert2str(cert);
		urlStr = uriForFileUpload(daUrl, path, certStr, identityManager.getThisNetworkNode().getJid());
		LOG.info("[#1879] testDocumentUploadDownload(): uploading initial document to {}", urlStr);
		URL url = new URL(urlStr);
		Net net = new Net(url);
		net.put(path, xml.getBytes(), url.toURI());
		
		// URL for download
		urlStr = uriForFileDownload(daUrl, path, signatureMgr.sign(path, identityManager.getThisNetworkNode()));
		httpCode = getHttpCode(new URL(urlStr));
		assertEquals(HttpURLConnection.HTTP_OK, httpCode, 0.0);
		
		url = new URL(urlStr);
		net = new Net(url);
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		net.download(os);
		byte[] downloaded = os.toByteArray();
		LOG.info("Size of downloaded xml: {}", downloaded.length);
		assertEquals(xml, new String(downloaded));
		os.close();
		
		// URL with invalid signature
		String sigKeyword = UrlPath.URL_PARAM_SIGNATURE + "=";
		int sigKeywordEnd = urlStr.indexOf(sigKeyword) + sigKeyword.length();
		String urlStrInvalid = urlStr.substring(0, sigKeywordEnd) + "123456789012345678901234567890" +
				urlStr.substring(sigKeywordEnd + 30);
		LOG.info("[#1879] testDocumentUploadDownload(): URL with invalid signature: {}", urlStrInvalid);
		assertEquals(urlStr.length(), urlStrInvalid.length(), 0.0);
		httpCode = getHttpCode(new URL(urlStrInvalid));
		assertEquals(HttpURLConnection.HTTP_UNAUTHORIZED, httpCode, 0.0);
	}
	
	private String uriForFileDownload(String host, String path, String signature) {
		
		String uriStr;
		
		LOG.debug("uriForFileDownload({}, {}, ...)", host, path);
		
		uriStr = host + UrlPath.BASE + UrlPath.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + UrlPath.URL_PARAM_SIGNATURE + "=" + signature;

		LOG.debug("uriForFileDownload(): uri = {}", uriStr);
		return uriStr;
	}
	
	private String uriForFileUpload(String host, String path, String pubkey, String notificationEndpoint) {
		
		String uriStr;

		LOG.debug("uriForFileUpload({}, {}, ...)", host, path);

		pubkey = UrlParamName.base64ToUrl(pubkey);
		
		uriStr = host + UrlPath.BASE + UrlPath.PATH_XML_DOCUMENTS + "/" + path.replaceAll(".*/", "") +
				"?" + UrlPath.URL_PARAM_PUB_KEY + "=" + pubkey +
				"&" + UrlPath.URL_PARAM_NOTIFICATION_ENDPOINT + "=" + notificationEndpoint; 

		LOG.debug("uriForFileUpload(): uri = {}", uriStr);
		return uriStr;
	}
	
	private int getHttpCode(URL resource) throws IOException {
		HttpURLConnection.setFollowRedirects(false);
		//HttpURLConnection.setInstanceFollowRedirects(false);
		HttpURLConnection con = (HttpURLConnection) resource.openConnection();
		con.setRequestMethod("HEAD");
		return con.getResponseCode();
	}
}
