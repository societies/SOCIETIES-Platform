package org.societies.comm.xmpp.client.impl;

import static android.content.Context.BIND_AUTO_CREATE;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.comm.android.ipc.IMethodInvocation;
import org.societies.comm.android.ipc.MethodInvocationServiceConnection;
import org.societies.comm.android.ipc.utils.MarshallUtils;
import org.societies.comm.xmpp.pubsub.Affiliation;
import org.societies.comm.xmpp.pubsub.PubsubClient;
import org.societies.comm.xmpp.pubsub.Subscriber;
import org.societies.comm.xmpp.pubsub.Subscription;
import org.societies.comm.xmpp.pubsub.SubscriptionState;
import org.societies.pubsub.interfaces.Pubsub;
import org.societies.pubsub.interfaces.SubscriptionParcelable;
import org.w3c.dom.Element;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class PubsubClientAndroid implements PubsubClient { 
	
	private static final ComponentName serviceCN = new ComponentName("org.societies.pubsub", "org.societies.pubsub.PubsubService"); // TODO	

	private MethodInvocationServiceConnection<Pubsub> miServiceConnection;
	private Map<Subscriber, SubscriberAdapter> subscribersMap = new HashMap<Subscriber, SubscriberAdapter>();
	
	public PubsubClientAndroid(Context androidContext) {
		Intent intent = new Intent();
		intent.setComponent(serviceCN);
		miServiceConnection = new MethodInvocationServiceConnection<Pubsub>(intent, androidContext, BIND_AUTO_CREATE, Pubsub.class);
	}
	
	public void ownerCreate(Identity pubsubService, final String node) throws XMPPError, CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.ownerCreate(pubsubServiceJid, node);
				return null;
			}
		});
	}
	
	

	public List<String> discoItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Subscription subscriberSubscribe(Identity pubsubService,
			final String node, final Subscriber subscriber) throws XMPPError,
			CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		final SubscriberAdapter subscriberAdapter = new SubscriberAdapter(subscriber);
		subscribersMap.put(subscriber, subscriberAdapter);
		return ((SubscriptionParcelable)invokeRemoteMethodAndKeepBound(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				return pubsub.subscriberSubscribe(pubsubServiceJid, node, subscriberAdapter);
			}
		})).subscription();
	}

	public void subscriberUnsubscribe(final Identity pubsubService, final String node,
			Subscriber subscriber) throws XMPPError, CommunicationException {
		// TODO 
	}

	public List<Element> subscriberRetrieveLast(Identity pubsubService,
			String node, String subId) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<Element> subscriberRetrieveSpecific(Identity pubsubService,
			String node, String subId, List<String> itemIdList)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public String publisherPublish(Identity pubsubService, final String node,
			final String itemId, Element item) throws XMPPError,
			CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		final String itemXml;
		try {
			itemXml = MarshallUtils.nodeToString(item);
		} catch (TransformerException e) {
			throw new CommunicationException(e.getMessage(), e);
		}
		return (String)invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				return pubsub.publisherPublish(pubsubServiceJid, node, itemId, itemXml);
			}
		});
	}
	
	public void publisherDelete(Identity pubsubService, String node,
			String itemId) throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	public void ownerDelete(Identity pubsubService, final String node)
			throws XMPPError, CommunicationException {
		final String pubsubServiceJid = pubsubService.getJid();
		invokeRemoteMethod(new IMethodInvocation<Pubsub>() {
			public Object invoke(Pubsub pubsub) throws Throwable {
				pubsub.ownerDelete(pubsubServiceJid, node);
				return null;
			}
		});
	}

	public void ownerPurgeItems(Identity pubsubService, String node)
			throws XMPPError, CommunicationException {
		// TODO Auto-generated method stub
		
	}

	public Map<Identity, SubscriptionState> ownerGetSubscriptions(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public Map<Identity, Affiliation> ownerGetAffiliations(
			Identity pubsubService, String node) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		return null;
	}

	public void ownerSetSubscriptions(Identity pubsubService, String node,
			Map<Identity, SubscriptionState> subscriptions) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}

	public void ownerSetAffiliations(Identity pubsubService, String node,
			Map<Identity, Affiliation> affiliations) throws XMPPError,
			CommunicationException {
		// TODO Auto-generated method stub
		
	}
	
	private Object invokeRemoteMethod(IMethodInvocation<Pubsub> methodInvocation) throws XMPPError, CommunicationException {		
		try {
			return miServiceConnection.invoke(methodInvocation);
		} catch (Throwable e) {
			if(e instanceof XMPPError)
				throw (XMPPError)e;
			if(e instanceof CommunicationException)
				throw (CommunicationException)e;
			throw new CommunicationException(e.getMessage(), e);
		}
	}
	
	private Object invokeRemoteMethodAndKeepBound(IMethodInvocation<Pubsub> methodInvocation) throws XMPPError, CommunicationException {		
		try {
			return miServiceConnection.invokeAndKeepBound(methodInvocation);
		} catch (Throwable e) {
			if(e instanceof XMPPError)
				throw (XMPPError)e;
			if(e instanceof CommunicationException)
				throw (CommunicationException)e;
			throw new CommunicationException(e.getMessage(), e);
		}
	}
}
