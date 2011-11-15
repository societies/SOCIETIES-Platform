package org.societies.privacytrust.trust.api.repo;

import org.societies.privacytrust.trust.api.model.TrustedEntity;

public class TrustEntityEvent {

	private TrustedEntity entity;

	public TrustEntityEvent(){

	}

	public TrustedEntity getEntity(){
		return this.entity;
	}
}
