package org.societies.comm.xmpp.pubsub.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class PubsubServiceDAO {
	
	private long hbnId; // hibernate id
	private String pubsubServiceEndpoint;
	private Collection<PubsubNodeDAO> nodes;
	
	public PubsubServiceDAO() {
		nodes = new ArrayList<PubsubNodeDAO>();
	}
	
	@Id
	@GeneratedValue
	public long getHbnId() {
		return hbnId;
	}
	public void setHbnId(long hbnId) {
		this.hbnId = hbnId;
	}

	public String getPubsubServiceEndpoint() {
		return pubsubServiceEndpoint;
	}
	public void setPubsubServiceEndpoint(String pubsubServiceEndpoint) {
		this.pubsubServiceEndpoint = pubsubServiceEndpoint;
	}

	@OneToMany(mappedBy="pubsubService", fetch=FetchType.EAGER)
	public Collection<PubsubNodeDAO> getNodes() {
		return nodes;
	}
	public void setNodes(Collection<PubsubNodeDAO> nodes) {
		this.nodes = nodes;
	}
}
