package org.societies.android.platform.events.mocks;

import java.io.ByteArrayOutputStream;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.pubsub.ISubscriber;
import org.societies.android.platform.pubsub.helper.PubsubHelper;
import org.societies.api.identity.IIdentity;

import android.content.Context;

/**
 * Mock version of {@link MockPubsubClientAndroid} class for use in testing
 */
public class MockPubsubClientAndroid extends PubsubHelper {

	
	public boolean bindPubsubService(IMethodCallback bindCallback) {
		bindCallback.returnAction(true);
		return false;
	}

	public boolean unbindCommsService(IMethodCallback bindCallback) {
		bindCallback.returnAction(true);
		return false;
	}

	public MockPubsubClientAndroid(Context androidContext) {
		super(androidContext);
	}
	
	public void setSubscriberCallback(ISubscriber subscriberCallback) {
	}

	public String publisherPublish(IIdentity pubsubServiceID, String node, String itemID, Object payload, IMethodCallback callback) throws XMPPError, CommunicationException {
		callback.returnAction("test");
		return null;
	}

	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
	}
	public boolean subscriberSubscribe(IIdentity pubsubServiceID, String node, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		methodCallback.returnAction("test");
		return false;
	}
	public boolean subscriberUnsubscribe(IIdentity pubsubServiceID, String node, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		methodCallback.returnAction("test");
		return false;
	}

}
