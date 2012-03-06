package org.societies.api.identity;

import org.societies.api.context.model.CtxIdentifier;

public interface IIdentityContextMapper {
	CtxIdentifier getMappedCtxIdentifier(IIdentity publicId, String attributeType);
	CtxIdentifier addMappedCtxIdentifier(IIdentity publicId, CtxIdentifier attributeID);
	boolean removeMappedCtxIdentifier(IIdentity publicId, CtxIdentifier ctxIdentifier);
}
