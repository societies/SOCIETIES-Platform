package org.societies.privacytrust.trust.api.model;

import java.util.Set;

/**
 * This class represents trusted CSSs. A TrustedCSS object is referenced by its
 * TrustedEntityId, while the associated Trust value objects express the
 * trustworthiness of this CSS, i.e. direct, indirect and user-perceived. Each
 * trusted CSS is assigned a set of TrustedService objects.
 */
public class TrustedUser extends TrustedEntity {
	
	private static final long serialVersionUID = -5663024798098392757L;
	
	private Set<TrustedCommunity> communities;
	private Set<TrustedService> services;

	public TrustedUser() {

	}

	public Set<TrustedCommunity> getCommunities(){
		return this.communities;
	}

	public Set<TrustedService> getServices(){
		return this.services;
	}

	/**
	 * 
	 * @param s
	 */
	public Set<TrustedService> getServices(String serviceType){
		return null;
	}
}
