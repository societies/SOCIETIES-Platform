package org.societies.security.policynegotiator.provider;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPNode;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.domainauthority.IClientJarServerRemote;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;
import org.societies.api.internal.security.policynegotiator.NegotiationException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.comms.policynegotiator.CommsClient;

public class ProviderServiceMgrTest {

	private static final long TIME_TO_WAIT_IN_MS = 200;

	private ProviderServiceMgr classUnderTest;
	private IClientJarServerRemote clientJarServer;
	private ISignatureMgr signatureMgr;
	private CommsClient groupMgr;
	
	private ServiceResourceIdentifier serviceId;
	
	private static final String slaXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><a>abc</a>";
	private URI fileServer;
	NegotiationProviderSLMCallback callback;

	private class NegotiationProviderSLMCallback implements INegotiationProviderSLMCallback {

		public boolean success = false;
		public boolean error = false;
		
		public String msg = null;

		@Override
		public void notifySuccess() {
			this.success = true;
		}

		@Override
		public void notifyError(String msg, Throwable e) {
			this.error = true;
			this.msg = msg;
		}
	}
	
	private class CommMgrMock implements ICommManager {

		private IIdentityManager idMgr = mock(IIdentityManager.class);
		
		@Override
		public boolean isConnected() {
			return false;
		}

		@Override
		public void register(IFeatureServer featureServer)
				throws CommunicationException {
		}

		@Override
		public void register(ICommCallback messageCallback)
				throws CommunicationException {
		}

		@Override
		public void sendIQGet(Stanza stanza, Object payload,
				ICommCallback callback) throws CommunicationException {
		}

		@Override
		public void sendIQSet(Stanza stanza, Object payload,
				ICommCallback callback) throws CommunicationException {
		}

		@Override
		public void sendMessage(Stanza stanza, String type, Object payload)
				throws CommunicationException {
		}

		@Override
		public void sendMessage(Stanza stanza, Object payload)
				throws CommunicationException {
		}

		@Override
		public void addRootNode(XMPPNode newNode) {
		}

		@Override
		public void removeRootNode(XMPPNode node) {
		}

		@Override
		public String getInfo(IIdentity entity, String node,
				ICommCallback callback) throws CommunicationException {
			return null;
		}

		@Override
		public String getItems(IIdentity entity, String node,
				ICommCallback callback) throws CommunicationException {
			return null;
		}

		@Override
		public IIdentityManager getIdManager() {
			return idMgr;
		}

		@Override
		public boolean UnRegisterCommManager() {
			return false;
		}
		
	}
	
	@Before
	public void setUp() throws Exception {
		
		classUnderTest = new ProviderServiceMgr();
		clientJarServer = mock(IClientJarServerRemote.class);
		signatureMgr = mock(ISignatureMgr.class);
		
		groupMgr = new CommsClient();
		ICommManager commMgr = new CommMgrMock();
		assertNotNull(commMgr.getIdManager());
		groupMgr.setCommMgr(commMgr);
		groupMgr.init();
		assertNotNull(groupMgr.getCommMgr());
		assertNotNull(groupMgr.getCommMgr().getIdManager());
		
		classUnderTest.setClientJarServer(clientJarServer);
		classUnderTest.setGroupMgr(groupMgr);
		classUnderTest.setSignatureMgr(signatureMgr);
		
		serviceId = new ServiceResourceIdentifier();
		serviceId.setIdentifier(new URI("societies://aaa.bbb.ccc"));
		serviceId.setServiceInstanceIdentifier("service-1");
		fileServer = new URI("http://localhost/foo");
		callback = new NegotiationProviderSLMCallback();
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public void testGettersAndSetters() {
		assertSame(clientJarServer, classUnderTest.getClientJarServer());
		assertSame(groupMgr, classUnderTest.getGroupMgr());
		assertSame(signatureMgr, classUnderTest.getSignatureMgr());
	}

	@Test
	public void testAddService_ListStrings_0() throws Exception {
		
		List<String> files = new ArrayList<String>();
		
		assertFalse(callback.success);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
		classUnderTest.addService(serviceId, slaXml, fileServer, files, callback);
		assertEquals(1, classUnderTest.getServices().size(), 0.0);

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		assertTrue(callback.success);
		assertFalse(callback.error);
	}

	@Test
	public void testAddService_ListStrings_1() throws Exception {
		
		List<String> files = new ArrayList<String>();
		files.add("foo.jar");
		
		assertFalse(callback.success);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
		classUnderTest.addService(serviceId, slaXml, fileServer, files, callback);
		assertEquals(1, classUnderTest.getServices().size(), 0.0);
	}

	@Test
	public void testAddService_UrlArray_0() throws Exception {

		URL[] fileUrls = new URL[] {};

		assertFalse(callback.success);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
		classUnderTest.addService(serviceId, slaXml, fileServer, fileUrls, callback);
		assertEquals(1, classUnderTest.getServices().size(), 0.0);

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		assertTrue(callback.success);
		assertFalse(callback.error);
	}

	@Test
	public void testAddService_UrlArray_1() throws Exception {

		URL[] fileUrls = new URL[] {new URL("http://localhost/foo")};

		assertFalse(callback.success);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
		classUnderTest.addService(serviceId, slaXml, fileServer, fileUrls, callback);
		assertEquals(1, classUnderTest.getServices().size(), 0.0);
	}

	@Test
	public void testAddService_SingleFile() throws Exception {
		
		String filePath = "foo.jar";
		
		assertFalse(callback.success);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
		classUnderTest.addService(serviceId, slaXml, fileServer, filePath, callback);
		assertEquals(1, classUnderTest.getServices().size(), 0.0);
	}

	@Test
	public void testRemoveService() throws Exception {
		testAddService_SingleFile();
		assertEquals(1, classUnderTest.getServices().size(), 0.0);
		classUnderTest.removeService(serviceId);
		assertEquals(0, classUnderTest.getServices().size(), 0.0);
	}

	@Test
	public void testGetService() throws Exception {
		
		Service service;
		
		service = classUnderTest.getService(serviceId.getIdentifier().toASCIIString());
		assertNull(service);
		
		testAddService_SingleFile();
		service = classUnderTest.getService(serviceId.getIdentifier().toASCIIString());
		assertNotNull(service);
	}

	@Test
	public void testGetSignedUris() throws Exception {
		
		List<URI> signedUris;
		
		testAddService_UrlArray_1();
		signedUris = classUnderTest.getSignedUris(serviceId.getIdentifier().toASCIIString());
		assertEquals(1, signedUris.size(), 0.0);
	}
}
