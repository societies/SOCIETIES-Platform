package org.societies.comm.xmpp.datatypes;

import java.util.List;

// TODO change this to interface that Identity and Node implement
public class XMPPInfo {
	
	public static final String INFO_NAMESPACE = "http://jabber.org/protocol/disco#info";

	private String identityCategory;
	private String identityType;
	private String identityName;
	private List<String> featureNamespaces;
	
	public XMPPInfo(String identityCategory, String identityType,
			String identityName, List<String> featureNamespaces) {
		this.identityCategory = identityCategory;
		this.identityType = identityType;
		this.identityName = identityName;
		this.featureNamespaces = featureNamespaces;
	}


	public String getIdentityCategory() {
		return identityCategory;
	}

	public String getIdentityType() {
		return identityType;
	}

	public String getIdentityName() {
		return identityName;
	}

	public List<String> getFeatureNamespaces() {
		return featureNamespaces;
	}
}
