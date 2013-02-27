package org.societies.android.api.pubsub;

/**
 * Class represents a Pubsub node event payload
 *
 */
public class PubsubNodePayload {
	private Object payload;
	private String eventId;
	private String pubsubNode;
	
	public PubsubNodePayload(Object payload, String eventId, String pubsubNode) {
		this.payload = payload;
		this.eventId = eventId;
		this.pubsubNode = pubsubNode;
	}
	
	public Object getPayload() {
		return payload;
	}
	public void setPayload(Object payload) {
		this.payload = payload;
	}
	public String getEventId() {
		return eventId;
	}
	public void setEventId(String eventId) {
		this.eventId = eventId;
	}
	public String getPubsubNode() {
		return pubsubNode;
	}
	public void setPubsubNode(String pubsubNode) {
		this.pubsubNode = pubsubNode;
	}
}
