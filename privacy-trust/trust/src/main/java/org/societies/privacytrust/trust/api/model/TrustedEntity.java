package org.societies.privacytrust.trust.api.model;

import java.io.Serializable;
import java.net.URI;

/**
 * This abstract class is used to represent an entity trusted by the trustor, i.e.
 * the owner of a CSS. Each trusted entity is referenced by its {@link TrustedEntityId},
 * while the associated Trust objects express the trustworthiness of that entity,
 * i.e. direct, indirect and user-perceived.
 */
public abstract class TrustedEntity implements Serializable {
	
	private static final long serialVersionUID = -495088232194787430L;

	private TrustedEntityId teid;
	
	private DirectTrust directTrust ;
	private IndirectTrust indirectTrust ;
	private UserPerceivedTrust trust;
	
	private URI trustor;

	public TrustedEntity() {

	}

	public URI getTrustor() {
		return this.trustor;
	}
	
	public UserPerceivedTrust getTrust(){
		return this.trust;
	}

	public TrustedEntityId getTrustedEntityId(){
		return this.teid;
	}
}