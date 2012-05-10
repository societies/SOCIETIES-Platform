package org.societies.api.identity;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * Returns the identity of this node
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface INetworkNode extends IIdentity {
	/**
	 * Returns the Node Identifier
	 */
	String getNodeIdentifier();
}
