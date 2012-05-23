package org.societies.api.identity;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IIdentity {
	/**
	 * Returns the username of a Jabber ID, eg, John from john@societies.local
	 * @return
	 */
	String getIdentifier();

	/**
	 * Returns the domain of a Jabber ID eg, societies.local from john@societies.local
	 * @return
	 */
	String getDomain();

	/**
	 * Returns the CSS Type
	 * @return
	 */
	IdentityType getType();

	/**
	 * Returns the full Jabber ID including resource eg, john@societies.local/laptop
	 * @return
	 */
	String getJid();

	/**
	 * Returns the bare Jabber ID without resource eg, john@societies.local
	 * @return
	 */
	String getBareJid();
}
