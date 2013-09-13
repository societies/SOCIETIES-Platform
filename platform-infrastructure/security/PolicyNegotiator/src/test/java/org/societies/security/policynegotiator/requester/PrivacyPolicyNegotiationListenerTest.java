package org.societies.security.policynegotiator.requester;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.PPNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.FailedNegotiationEvent;
import org.societies.api.internal.privacytrust.privacyprotection.negotiation.NegotiationDetails;
import org.societies.api.internal.security.policynegotiator.INegotiationCallback;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.privacytrust.privacy.model.privacypolicy.NegotiationStatus;

public class PrivacyPolicyNegotiationListenerTest {
	
	private PrivacyPolicyNegotiationListener classUnderTest;
	private NegotiationCallback finalCallback;
	private String slaKey;
	List<URI> fileUris;
	IEventMgr eventMgr;
	String[] eventTypes;
	int id;
	private PrivacyPolicyNegotiationInfo info;

	private static final long TIME_TO_WAIT_IN_MS = 200;

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
		finalCallback = new NegotiationCallback();
		slaKey = "123";
		fileUris = new ArrayList<URI>();
		eventMgr = mock(IEventMgr.class);
		eventTypes = new String[] {};
		id = new Random().nextInt();
		info = new PrivacyPolicyNegotiationInfo(finalCallback, slaKey, fileUris, id);

		classUnderTest = new PrivacyPolicyNegotiationListener(eventMgr);
		classUnderTest.addNegotiationInfo(info);
	}

	@Test
	public void testHandleInternalSuccessEvent() throws InterruptedException {
		
		NegotiationDetails details = new NegotiationDetails(null, id);
		PPNegotiationEvent payload = new PPNegotiationEvent(null, NegotiationStatus.SUCCESSFUL, details);
		InternalEvent event = new InternalEvent(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT,
				"eventName", "eventSource", payload);
		
		classUnderTest.handleInternalEvent(event);

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		
		assertTrue(finalCallback.negotiationComplete);
		assertFalse(finalCallback.negotiationError);
		
		assertSame(slaKey, finalCallback.agreementKey);
		assertSame(fileUris, finalCallback.fileUris);
	}
	
	@Test
	public void testHandleInternalOtherEvent() throws InterruptedException {
		NegotiationDetails details = new NegotiationDetails(null, id % 17 + 1);
		PPNegotiationEvent payload = new PPNegotiationEvent(null, NegotiationStatus.SUCCESSFUL, details);
		InternalEvent event = new InternalEvent(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT,
				"eventName", "eventSource", payload);
		
		classUnderTest.handleInternalEvent(event);
	
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		
		assertFalse(finalCallback.negotiationComplete);
		assertFalse(finalCallback.negotiationError);
		
		assertNull(finalCallback.agreementKey);
		assertNull(finalCallback.fileUris);
	}

	@Test
	public void testHandleInternalFailureEvent_1() throws InterruptedException {
		
		NegotiationDetails details = new NegotiationDetails(null, id);
		PPNegotiationEvent payload = new PPNegotiationEvent(null, NegotiationStatus.FAILED, details);
		InternalEvent event = new InternalEvent(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT,
				"eventName", "eventSource", payload);
		
		classUnderTest.handleInternalEvent(event);

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		
		assertFalse(finalCallback.negotiationComplete);
		assertTrue(finalCallback.negotiationError);
		
		assertNotNull(finalCallback.msg);
	}

	@Test
	public void testHandleInternalFailureEvent_2() throws InterruptedException {
		
		NegotiationDetails details = new NegotiationDetails(null, id);
		FailedNegotiationEvent payload = new FailedNegotiationEvent(details);
		InternalEvent event = new InternalEvent(EventTypes.FAILED_NEGOTIATION_EVENT,
				"eventName", "eventSource", payload);
		
		classUnderTest.handleInternalEvent(event);

		Thread.sleep(TIME_TO_WAIT_IN_MS);
		
		assertFalse(finalCallback.negotiationComplete);
		assertTrue(finalCallback.negotiationError);
		
		assertNotNull(finalCallback.msg);
	}
	
	@Test
	public void testHandleExternalEvent() throws InterruptedException {
		
		String eventInfoAsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\" ?><a>abc</a>";
		CSSEvent event = new CSSEvent(EventTypes.PRIVACY_POLICY_NEGOTIATION_EVENT,
				"eventName", "eventSource", eventInfoAsXml);
		
		classUnderTest.handleExternalEvent(event);
		
		Thread.sleep(TIME_TO_WAIT_IN_MS);
		
		assertFalse(finalCallback.negotiationComplete);
		assertFalse(finalCallback.negotiationError);
	}

}
