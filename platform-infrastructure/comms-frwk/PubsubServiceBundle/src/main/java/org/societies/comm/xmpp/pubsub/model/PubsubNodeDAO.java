package org.societies.comm.xmpp.pubsub.model;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity
public class PubsubNodeDAO {

	private long hbnId; // hibernate id
	private PubsubServiceDAO pubsubService;
	private String nodeId; // ps service specific key
	private String owner;
	private Map<String, String> subscriptionsById;
//	private Stack<String> itemIdByOrder;
//	private Map<String, Object> itemsById;
//	private Map<String, String> publisherByItemId;
	
	public PubsubNodeDAO() {
		subscriptionsById = new HashMap<String, String>();
	}
	
	@Id
	@GeneratedValue
	public long getHbnId() {
		return hbnId;
	}
	public void setHbnId(long hbnId) {
		this.hbnId = hbnId;
	}
	
	@ManyToOne(fetch=FetchType.EAGER)
	public PubsubServiceDAO getPubsubService() {
		return pubsubService;
	}
	public void setPubsubService(PubsubServiceDAO pubsubService) {
		this.pubsubService = pubsubService;
	}
	
	public String getNodeId() {
		return nodeId;
	}
	public void setNodeId(String nodeId) {
		this.nodeId = nodeId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	@CollectionOfElements(fetch=FetchType.EAGER)
	public Map<String, String> getSubscriptionsById() {
		return subscriptionsById;
	}
	public void setSubscriptionsById(Map<String, String> subscriptionsById) {
		this.subscriptionsById = subscriptionsById;
	}
	
	
}
