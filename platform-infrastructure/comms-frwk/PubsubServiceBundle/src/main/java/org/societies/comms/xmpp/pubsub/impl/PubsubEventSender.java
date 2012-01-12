package org.societies.comms.xmpp.pubsub.impl;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.comm.xmpp.datatypes.Identity;
import org.societies.comm.xmpp.datatypes.Stanza;
import org.societies.comm.xmpp.exceptions.CommunicationException;
import org.societies.comm.xmpp.interfaces.CommManager;

public class PubsubEventSender extends Thread {
	
	private static Logger LOG = LoggerFactory
			.getLogger(PubsubEventSender.class);
	
	public static final int TIMEOUT = 10000;
	
	private CommManager endpoint;
	private List<Notification> notificationQueue;
	private boolean live;
	
	public PubsubEventSender(CommManager endpoint) {
		this.endpoint = endpoint;
		live = true;
		notificationQueue = new ArrayList<PubsubEventSender.Notification>();
		start();
	}
	
	// Asynch, non-blocking method!
	public void sendEvent(Collection<Identity> recipients, Object eventPayload) {
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
						wait(TIMEOUT);
					} catch (InterruptedException e) {
						LOG.info("InterruptedException!!!!!!!!");
					}
				}
				n = notificationQueue.remove(0);
			}

			for (Identity i : n.recipients) {
				Stanza stanza = new Stanza(i);
				try {
					endpoint.sendMessage(stanza, n.eventPayload);
				} catch (CommunicationException e) {
					LOG.warn(e.getMessage());
				}
			}
		}
	}
	
	private class Notification {
		private Collection<Identity> recipients;
		private Object eventPayload;
		
		private Notification(Collection<Identity> recipients, Object eventPayload) {
			this.recipients = recipients;
			this.eventPayload = eventPayload;
		}
	}
}
