package org.societies.api.identity;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IIdentity {
	String getIdentifier();

	String getDomain();

	IdentityType getType();

	String getJid();

	String getBareJid();
}
