package org.societies.api.identity;

import org.societies.api.context.model.CtxIdentifier;

/**
 * MISSING_ANNOTATION
 * MISSING_JAVADOCS
 */
public interface IIdentityContextMapper {
	CtxIdentifier getMappedCtxIdentifier(IIdentity publicId, String attributeType);
	CtxIdentifier addMappedCtxIdentifier(IIdentity publicId, CtxIdentifier attributeID);
	boolean removeMappedCtxIdentifier(IIdentity publicId, CtxIdentifier ctxIdentifier);
}
