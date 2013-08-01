package org.societies.security.policynegotiator.requester;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Random;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;

public class ProviderCallbackTest {

	private ProviderCallback classUnderTest;
	private NegotiationRequester requester;
	private Requestor provider;
	private MethodType method;
	private static final boolean includePrivacyPolicyNegotiation = false;
	private INegotiationCallback callback;
	
	private SlaBean result;
	private static final int sessionId = new Random().nextInt();
	private static final String sla = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><a>abc</a>";

	private class NegotiationCallback implements INegotiationCallback {

		public boolean negotiationComplete = false;
		public boolean negotiationError = false;
		
		public String agreementKey;
		public List<URI> fileUris;
		public String msg;
		
		@Override
		public void onNegotiationComplete(String agreementKey, List<URI> fileUris) {
			this.negotiationComplete = true;
			this.agreementKey = agreementKey;
			this.fileUris = fileUris;
		}

		@Override
		public void onNegotiationError(String msg) {
			this.negotiationError = true;
			this.msg = msg;
		}
	}
	
	@Before
	public void setUp() throws Exception {
		requester = mock(NegotiationRequester.class);
		provider = mock(Requestor.class);
		callback = new NegotiationCallback();
		result = new SlaBean();
	}

	@After
	public void tearDown() throws Exception {
		classUnderTest = null;
	}

	@Test
	public void testReceiveResult_GET_POLICY_OPTIONS() {
		
		method = MethodType.GET_POLICY_OPTIONS;
		classUnderTest = new ProviderCallback(requester, provider, method, includePrivacyPolicyNegotiation, callback);
		result.setSessionId(sessionId);
		result.setSuccess(true);
		result.setSla(sla);
		
		//classUnderTest.receiveResult(result);
	}

}
