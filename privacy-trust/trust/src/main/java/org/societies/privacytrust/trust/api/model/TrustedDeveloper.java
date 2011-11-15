package org.societies.privacytrust.trust.api.model;

import java.util.Set;

/**
 * This class represents service developers. A TrustedDeveloper object is
 * referenced by its TrustedEntityId, while the associated Trust value objects
 * express the trustworthiness of this developer, i.e. direct, indirect and user-
 * perceived. Each trusted developer is assigned a set of TrustedService objects.
 */
public class TrustedDeveloper extends TrustedEntity {

	private static final long serialVersionUID = -7272100351846916160L;
	
	private Set<TrustedService> services;

	public TrustedDeveloper() {
	}

	public Set<TrustedService> getServices(){
		return this.services;
	}
}
