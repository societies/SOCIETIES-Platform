package org.societies.api.internal.servicelifecycle.serviceRegistry.model;

import java.io.Serializable;
import java.net.URI;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author apanazzolo
 * @version 1.0
 * @created 06-dic-2011 12.12.59
 */

@Entity
@Table (name="ServiceResourceIdentifier")
public class ServiceResourceIdentifier implements Serializable {
	
	private long id;
	
	private String hash;
	private int lifetime;
	private URI identifier;

	public ServiceResourceIdentifier(){

	}

	public void finalize() throws Throwable {

	}

	public String getHash() {
		return hash;
	}

	public void setHash(String hash) {
		this.hash = hash;
	}

	public int getLifetime() {
		return lifetime;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	public URI getIdentifier() {
		return identifier;
	}

	public void setIdentifier(URI identifier) {
		this.identifier = identifier;
	}

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@Column(name = "ServiceResourceIdentifierId")
	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}