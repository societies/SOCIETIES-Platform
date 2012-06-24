package org.societies.api.identity;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

/**
 * Utility class that allows to map context parameters to different identities of the user.
 */
@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface IIdentityContextMapper {
	/**
	 * Retrieve a mapped context parameter.
	 */
	CtxIdentifier getMappedCtxIdentifier(IIdentity publicId, String attributeType);
	
	/**
	 * Add a mapped context parameter.
	 */
	CtxIdentifier addMappedCtxIdentifier(IIdentity publicId, CtxIdentifier attributeID);
	
	/**
	 * Remove a mapped context parameter.
	 */
	boolean removeMappedCtxIdentifier(IIdentity publicId, CtxIdentifier ctxIdentifier);
}
