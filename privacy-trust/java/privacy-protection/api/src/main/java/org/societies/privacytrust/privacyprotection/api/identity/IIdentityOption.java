package org.societies.privacytrust.privacyprotection.api.identity;

import java.util.List;
import java.util.Map;

import org.societies.api.identity.IIdentity;

public interface IIdentityOption {
	IIdentity getReferenceIdentity();
	Map<IIdentity,ILinkabilityDetail> getLinkabilityDetailMap();
	List<ILinkabilityDetail> getOrderedLinkabilityDetailList();
	int getIdentityContextMatch(); // TODO properly model this
}
