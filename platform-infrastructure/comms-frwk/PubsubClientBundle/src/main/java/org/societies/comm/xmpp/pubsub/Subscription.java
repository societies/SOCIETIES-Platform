package org.societies.comm.xmpp.pubsub;

import org.societies.api.comm.xmpp.datatypes.Identity;

public class Subscription {

	// TODO subId is ignored for equals and hash
	private Identity pubsubService;
	private Identity subscriber;
	private String node;
	private String subId;
	
	public Subscription(Identity pubsubService, Identity subscriber,
			String node, String subId) {
		super();
		this.pubsubService = pubsubService;
		this.subscriber = subscriber;
		this.node = node;
		this.subId = subId;
	}
	public Identity getPubsubService() {
		return pubsubService;
	}
	public void setPubsubService(Identity pubsubService) {
		this.pubsubService = pubsubService;
	}
	public Identity getSubscriber() {
		return subscriber;
	}
	public void setSubscriber(Identity subscriber) {
		this.subscriber = subscriber;
	}
	public String getNode() {
		return node;
	}
	public void setNode(String node) {
		this.node = node;
	}
	public String getSubId() {
		return subId;
	}
	public void setSubId(String subId) {
		this.subId = subId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((node == null) ? 0 : node.hashCode());
		result = prime * result
				+ ((pubsubService == null) ? 0 : pubsubService.hashCode());
		result = prime * result
				+ ((subscriber == null) ? 0 : subscriber.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Subscription other = (Subscription) obj;
		if (node == null) {
			if (other.node != null)
				return false;
		} else if (!node.equals(other.node))
			return false;
		if (pubsubService == null) {
			if (other.pubsubService != null)
				return false;
		} else if (!pubsubService.equals(other.pubsubService))
			return false;
		if (subscriber == null) {
			if (other.subscriber != null)
				return false;
		} else if (!subscriber.equals(other.subscriber))
			return false;
		return true;
	}

}
