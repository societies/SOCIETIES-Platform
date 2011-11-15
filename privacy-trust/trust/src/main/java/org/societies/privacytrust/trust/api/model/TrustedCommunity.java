package org.societies.privacytrust.trust.api.model;

import java.util.Set;

/**
 * This class represents trusted CISs. A TrustedCIS object is referenced by its
 * TrustedEntityId, while the associated Trust value objects express the
 * trustworthiness of this community, i.e. direct, indirect and user-perceived.
 * Each trusted CIS is assigned a set of TrustedCSS objects, which represent its
 * members.
 */
public class TrustedCommunity extends TrustedEntity {

	private static final long serialVersionUID = -438368876927927076L;
	
	private Set<TrustedUser> members;
	private Set<TrustedService> services;

	public TrustedCommunity(){

	}

	public Set<TrustedUser> getMembers(){
		return this.members;
	}

	/**
	 * 			
	 */
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