package org.societies.privacytrust.trust.api.model;

import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;

import org.societies.privacytrust.trust.api.mock.EntityIdentifier;

public class TrustedEntityId implements Serializable {

	private static final long serialVersionUID = 7390835311816850816L;
	
	private final EntityIdentifier entityId;
	private URI urn;

	public TrustedEntityId(EntityIdentifier entityId) throws URISyntaxException{
		this.entityId = entityId;
		this.urn = new URI("teid" + entityId.toString());
	}

	public EntityIdentifier getEntityId(){
		return this.entityId;
	}

	public URI getUrn(){
		return this.urn;
	}
	
	public String toString() {
		return this.urn.toString();
	}
}