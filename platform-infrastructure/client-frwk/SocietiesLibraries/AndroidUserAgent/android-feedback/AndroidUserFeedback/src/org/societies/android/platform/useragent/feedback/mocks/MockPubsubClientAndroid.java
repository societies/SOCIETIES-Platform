package org.societies.android.platform.useragent.feedback.mocks;

import java.util.List;

import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.pubsub.Subscriber;
import org.societies.api.comm.xmpp.pubsub.Subscription;
import org.societies.api.identity.IIdentity;
import org.societies.comm.xmpp.client.impl.PubsubClientAndroid;

import android.content.Context;

/**
 * Mock version of {@link MockPubsubClientAndroid} class for use in testing
 */
public class MockPubsubClientAndroid extends PubsubClientAndroid {

	public MockPubsubClientAndroid(Context androidContext) {
		super(androidContext);
	}
	
	public void addSimpleClasses(List<String> classList) throws ClassNotFoundException {
    }
	
    public void subscriberUnsubscribe(final IIdentity pubsubService, final String node,
            Subscriber subscriber) throws XMPPError, CommunicationException {
    }
    
    public Subscription subscriberSubscribe(IIdentity pubsubService,
            final String node, final Subscriber subscriber) throws XMPPError,
            CommunicationException {
    	return null;
    }
}
