package org.societies.comm.xmpp.event;

import org.societies.api.identity.IIdentity;
import org.springframework.context.ApplicationEvent;

public class PubsubEvent extends ApplicationEvent {

	private IIdentity pubsubService;
	private String node;
	private String itemId;
	private Object payload;
	private boolean isPublished;
	
	public PubsubEvent(Object source, Object payload) {
		super(source);
		this.payload = payload;
		this.isPublished = false;
	}

	public IIdentity getPubsubService() {
		return pubsubService;
	}

	public String getNode() {
		return node;
	}

	public String getItemId() {
		return itemId;
	}

	public Object getPayload() {
		return payload;
	}

	public boolean isPublished() {
		return isPublished;
	}

	protected void setPublished(IIdentity pubsubService, String node, String itemId) {
		this.pubsubService = pubsubService;
		this.node = node;
		this.isPublished = true;
		this.itemId = itemId;
	}
}
