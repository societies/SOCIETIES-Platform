package org.societies.comm.xmpp;

public class Stanza {
	private String id;
	private String type;
	private Endpoint from;
	private Endpoint to;
	private StanzaNature nature;
	
	public Stanza(String id, Endpoint from, Endpoint to) {
		this.id = id;
		this.type = type;
		this.from = from;
		this.to = to;
		this.nature = nature;
	}
	
	public String getId() {
		return id;
	}
	public String getType() {
		return type;
	}
	public Endpoint getFrom() {
		return from;
	}
	public Endpoint getTo() {
		return to;
	}
	public StanzaNature getNature() {
		return nature;
	}
	
	public enum StanzaNature {
		IQ,
		Message,
		Presence;
	}
}
