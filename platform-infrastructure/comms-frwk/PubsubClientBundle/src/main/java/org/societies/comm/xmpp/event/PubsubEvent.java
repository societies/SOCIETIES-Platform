package org.societies.comm.xmpp.event;

import org.societies.api.comm.xmpp.datatypes.Identity;
import org.springframework.context.ApplicationEvent;
import org.w3c.dom.Element;

public class PubsubEvent extends ApplicationEvent {

	private Identity pubsubService;
	private String node;
	private String itemId;
	private Element payload;
	private boolean isPublished;
	
	public PubsubEvent(Object source, Element payload) {
		super(source);
		this.payload = payload;
		this.isPublished = false;
	}

	public Identity getPubsubService() {
		return pubsubService;
	}

	public String getNode() {
		return node;
	}

	public String getItemId() {
		return itemId;
	}

	public Element getPayload() {
		return payload;
	}

	public boolean isPublished() {
		return isPublished;
	}

	protected void setPublished(Identity pubsubService, String node, String itemId) {
		this.pubsubService = pubsubService;
		this.node = node;
		this.isPublished = true;
		this.itemId = itemId;
	}
}
