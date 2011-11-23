package org.societies.privacytrust.trust.api.model;

import java.net.URI;
import java.util.Set;

/**
 * This class represents trusted services. A TrustedService object is referenced
 * by its TrustedEntityId, while the associated Trust value objects express the
 * trustworthiness of this service, i.e. direct, indirect and user-perceived. Each
 * trusted service is also associated with a TrustedCSS which represents its
 * provider.
 */
public class TrustedService extends TrustedEntity {

	private static final long serialVersionUID = 8253551733059925542L;
	
	private Set<TrustedCis> communities;
	private URI id;
	private TrustedCss provider;
	private String type;
	public TrustedDeveloper developer;

	public TrustedService() {
	}

	public Set<TrustedCis> getCommunities(){
		return this.communities;
	}

	public URI getId(){
		return this.id;
	}

	public TrustedCss getProvider(){
		return this.provider;
	}

	public String getType(){
		return this.type;
	}
}