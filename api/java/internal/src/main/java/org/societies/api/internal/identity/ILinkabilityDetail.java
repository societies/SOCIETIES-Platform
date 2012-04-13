package org.societies.api.internal.identity;

import org.societies.api.identity.IIdentity;

public interface ILinkabilityDetail {
	IIdentity getReferenceIdentity();
	IIdentity getOtherIdentity();
	int getLinkabilityRisk(); // TODO properly model this
}
