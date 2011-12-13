package org.societies.comm.xmpp;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.societies.comm.identity.Identity;

@Entity
@Table (name="Endpoint")
public class Endpoint implements Serializable {
	
	private long id;
	
	private Identity identity;
	private String nodeIdentifier; //TODO if CIS, this is null?
	
	public Endpoint() {
		// TODO Auto-generated constructor stub
	}
	
	public Endpoint(Identity identity, String nodeIdentifier) {
		this.identity = identity;
		this.nodeIdentifier = nodeIdentifier;
	}

	@Column (name="Identity")
	public Identity getIdentity() {
		return identity;
	}
	
	public void setIdentity(Identity identity) {
		this.identity = identity;
	}

	@Column (name="NodeIdentifier")
	public String getNodeIdentifier() {
		return nodeIdentifier;
	}

	public void setNodeIdentifier(String nodeIdentifier) {
		this.nodeIdentifier = nodeIdentifier;
	}
	
	@Id
	@GeneratedValue (strategy=GenerationType.AUTO)
	@Column(name = "EndpointId")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
}
