package org.societies.android.platform.events.mocks;

import java.util.List;

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

	public MockPubsubClientAndroid(Context androidContext, ISubscriber subscriberCallback) {
		super(androidContext, subscriberCallback);
	}
	
	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
    }
	
    public boolean subscriberUnsubscribe(final IIdentity pubsubService, final String node,
            ISubscriber subscriber) throws XMPPError, CommunicationException {
    	return true;
    }
    
    public boolean subscriberSubscribe(IIdentity pubsubService,
            final String node, final ISubscriber subscriber) throws XMPPError,
            CommunicationException {
    	return true;
    }
}
