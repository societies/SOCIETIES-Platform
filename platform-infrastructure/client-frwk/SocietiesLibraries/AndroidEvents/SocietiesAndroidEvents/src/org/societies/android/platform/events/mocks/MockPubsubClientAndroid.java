package org.societies.android.platform.events.mocks;

import java.util.List;

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

	public MockPubsubClientAndroid(Context androidContext) {
		super(androidContext);
	}
	
	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
	}
	public boolean subscriberSubscribe(IIdentity pubsubServiceID, String node, ISubscriber callback, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		return false;
	}
	public boolean subscriberUnsubscribe(IIdentity pubsubServiceID, String node, ISubscriber callback, IMethodCallback methodCallback) throws XMPPError, CommunicationException {
		return false;
	}

}
