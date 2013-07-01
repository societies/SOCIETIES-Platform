package org.societies.css.mgmt;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.osgi.framework.BundleContext;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.internal.css.management.ICSSRemoteManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;
import org.societies.api.schema.cssmanagement.CssRequestOrigin;
import org.societies.api.schema.cssmanagement.CssRequestStatusType;
import org.societies.api.internal.css.cssRegistry.ICssRegistry;
import org.societies.api.internal.css.cssRegistry.exception.CssRegistrationException;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.activity.IActivityFeedManager;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.pubsub.PubsubClient;
import org.societies.api.css.directory.ICssDirectoryRemote;
import org.societies.api.activity.IActivityFeedManager;


public class TestCSSManager {
	public static final String TEST_IDENTITY_1 = "node11";
	public static final String TEST_IDENTITY_2 = "node22";

	public static final String TEST_IDENTITY = "CSSProfile1";
	public static final String TEST_INACTIVE_DATE = "20121029";
	public static final String TEST_REGISTERED_DATE = "2012-02-23";
	public static final int TEST_UPTIME = 7799;
	public static final String TEST_EMAIL = "somebody@tssg.org";
	public static final String TEST_FORENAME = "4Name";
	public static final String TEST_HOME_LOCATION = "The Hearth";
	public static final String TEST_IDENTITY_NAME = "Id Name";
	public static final String TEST_IM_ID = "somebody.tssg.org";
	public static final String TEST_NAME = "TestCSS";
	public static final String TEST_PASSWORD = "P455W0RD";
	public static final String TEST_SOCIAL_URI = "sombody@fb.com";
	
	
	
	private ICssRegistry cssRegistry;
//	private ICommManager CommManagerMock;
	private BundleContext context;
	

	private ICommManager commManagerMock;
	private IIdentityManager identityManagerMock;
	private INetworkNode iNetworkNodeMock;
	private PubsubClient pubSubManagerMock; 
	private IActivityFeed activityFeedMock;
	private IActivityFeedManager mockActivityFeedManager;
	
	private ICssDirectoryRemote mockCssDirectoryRemote;
	private ICSSRemoteManager mockcssManagerRemote;
	
	

	@Before
	public void setUp() throws Exception {
		
		//Create mocks
        context = mock(BundleContext.class);
        commManagerMock = mock(ICommManager.class);
        identityManagerMock = mock(IIdentityManager.class);
		pubSubManagerMock = mock(PubsubClient.class);
		activityFeedMock = mock(IActivityFeed.class);
		mockActivityFeedManager = mock(IActivityFeedManager.class);
		cssRegistry = mock(ICssRegistry.class);
		mockCssDirectoryRemote = mock (ICssDirectoryRemote.class);
		mockcssManagerRemote = mock(ICSSRemoteManager.class);
		
		when(commManagerMock.getIdManager()).thenReturn(identityManagerMock);	
		when(identityManagerMock.getThisNetworkNode()).thenReturn(iNetworkNodeMock);
		//when(activityFeedMock.getOrCreateFeed(null, null, null)).thenReturn(activityFeedMock);
		when(mockActivityFeedManager.getOrCreateFeed(eq(TEST_IDENTITY_1), eq(TEST_IDENTITY_1), eq(true))).thenReturn(activityFeedMock);
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() {
		CSSManager manager = new CSSManager();
		assertNotNull(manager);
	}
	
	@Ignore
	public void testInitManager() {
		CSSManager manager = new CSSManager();
		assertNotNull(manager);
		boolean retValue = false;
		
		when(commManagerMock.getIdManager()).thenReturn(identityManagerMock);	
		when(identityManagerMock.getThisNetworkNode()).thenReturn(iNetworkNodeMock);
		when(mockActivityFeedManager.getOrCreateFeed(eq(TEST_IDENTITY_1), eq(TEST_IDENTITY_1), eq(true))).thenReturn(activityFeedMock);
		
		manager.setCommManager(commManagerMock);
		manager.setiActivityFeedManager(mockActivityFeedManager);
		manager.setPubSubManager(pubSubManagerMock);
		manager.cssManagerInit();
	}

	@Test
	public void testXMPPRegistration() {
		CssRecord profile = new CssRecord();
		
		boolean retValue = false;
		
		profile.setCssIdentity(TEST_IDENTITY);
		profile.setEmailID(TEST_EMAIL);
		profile.setForeName(TEST_FORENAME);
		profile.setHomeLocation(TEST_HOME_LOCATION);
		
		assertEquals(TEST_IDENTITY, profile.getCssIdentity());
		assertEquals(TEST_EMAIL, profile.getEmailID());
		assertEquals(TEST_FORENAME, profile.getForeName());
		assertEquals(TEST_HOME_LOCATION, profile.getHomeLocation());


		
		CSSManager manager = new CSSManager();
		
		// cssRegistry registry = cssRegistry.getInstance();
		manager.setCssRegistry(cssRegistry);
		
		//manager.getCssRecord();
		
		Future<CssInterfaceResult> result = manager.registerXMPPServer(profile);		
		assertNotNull(result);
		
		try {
			CssInterfaceResult res = result.get();
		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		/*
		try {
			interfaceResult = result.get();
			//assertTrue(interfaceResult.isResultStatus());
			assertNotNull(interfaceResult);
			
			assertEquals(TEST_IDENTITY, interfaceResult.getProfile().getCssIdentity());
			assertEquals(TEST_EMAIL, interfaceResult.getProfile().getEmailID());
			assertEquals(TEST_FORENAME, interfaceResult.getProfile().getForeName());
			assertEquals(TEST_HOME_LOCATION, interfaceResult.getProfile().getHomeLocation());
			assertEquals(TEST_NAME, interfaceResult.getProfile().getName());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		*/
		
		result = manager.unregisterXMPPServer(profile);		
		assertNotNull(result);
		
	}
	
	@Test
	public void testgetcssrecord() {
		CssRecord profile = new CssRecord();
		
		boolean retValue = false;
		
		profile.setCssIdentity(TEST_IDENTITY);
		profile.setEmailID(TEST_EMAIL);
		profile.setForeName(TEST_FORENAME);
		profile.setHomeLocation(TEST_HOME_LOCATION);
		
		assertEquals(TEST_IDENTITY, profile.getCssIdentity());
		assertEquals(TEST_EMAIL, profile.getEmailID());
		assertEquals(TEST_FORENAME, profile.getForeName());
		assertEquals(TEST_HOME_LOCATION, profile.getHomeLocation());


		
		CSSManager manager = new CSSManager();
		manager.setCssRegistry(cssRegistry);
		
		manager.getCssRecord();
		
		Future<CssInterfaceResult> result = manager.getCssRecord();		
		assertNotNull(result);
		
		manager.setCssRegistry(cssRegistry);
		try {
			cssRegistry.registerCss(profile);
		} catch (CssRegistrationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		result = manager.getCssRecord();		
		assertNotNull(result);
		
	}
	
	@Test
	public void testupdatefriendreq() {
		CssRecord profile = new CssRecord();
		
		boolean retValue = false;
		
		CssRequest request = new CssRequest();
		
		
		request.setCssIdentity(TEST_IDENTITY);
		request.setRequestStatus(CssRequestStatusType.PENDING);
		request.setOrigin(CssRequestOrigin.REMOTE);
		
		CSSManager manager = new CSSManager();
		manager.setCssDirectoryRemote(mockCssDirectoryRemote);
		
		manager.setCssRegistry(cssRegistry);
		manager.updateCssFriendRequest(request);
		
		request.setRequestStatus(CssRequestStatusType.DELETEFRIEND);
		manager.updateCssFriendRequest(request);
		
		request.setRequestStatus(CssRequestStatusType.CANCELLED);
		manager.updateCssFriendRequest(request);
		
		manager.setCssManagerRemote(mockcssManagerRemote);
		manager.sendCssFriendRequest(TEST_IDENTITY_1);
		
		manager.getCssFriends();
		
	}
	
	@Test
	public void testnodestuff() {
		CssRecord profile = new CssRecord();
		
		boolean retValue = false;
		
		CssRequest request = new CssRequest();
		
		
		request.setCssIdentity(TEST_IDENTITY);
		request.setRequestStatus(CssRequestStatusType.PENDING);
		request.setOrigin(CssRequestOrigin.REMOTE);
		
		CSSManager manager = new CSSManager();
		manager.setCssRegistry(cssRegistry);
		manager.setNodeType(profile, TEST_IDENTITY_1, 0, 0, TEST_IDENTITY_NAME, "true");
		
		
		manager.setPubSubManager(pubSubManagerMock);
		manager.getPubSubManager();
	}
}
