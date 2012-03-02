package org.societies.comm.xmpp.pubsub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;

public class PubsubEventSender extends Thread {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubEventSender.class);
	
	public static final int TIMEOUT = 10000;
	
	private ICommManager endpoint;
	private List<Notification> notificationQueue;
	private boolean live;
	
	public PubsubEventSender(ICommManager endpoint) {
		this.endpoint = endpoint;
		live = true;
		notificationQueue = new ArrayList<PubsubEventSender.Notification>();
		start();
	}
	
	// Asynch, non-blocking method!
	public void sendEvent(Collection<IIdentity> recipients, Object eventPayload) {
		synchronized (notificationQueue) {
			notificationQueue.add(new Notification(recipients, eventPayload));
			notificationQueue.notifyAll();
		}
	}
	
	public synchronized void dispose() {
		live = false;
	}
	
	@Override
	public void run() {
		while (live) {
			Notification n = null;
			
			synchronized (notificationQueue) {
				while (notificationQueue.size()==0) {
					try {
						notificationQueue.wait(TIMEOUT);
					} catch (InterruptedException e) {
						LOG.info(e.getMessage());
					}
				}
				n = notificationQueue.remove(0);
			}

			for (IIdentity i : n.recipients) {
				Stanza stanza = new Stanza(i);
				try {
					endpoint.sendMessage(stanza, n.eventPayload);
				} catch (CommunicationException e) {
					LOG.warn("Error sending "+stanza.toString(),e);
				}
			}
		}
	}
	
	private class Notification {
		private Collection<IIdentity> recipients;
		private Object eventPayload;
		
		private Notification(Collection<IIdentity> recipients, Object eventPayload) {
			this.recipients = recipients;
			this.eventPayload = eventPayload;
		}
	}
}
