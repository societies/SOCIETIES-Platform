package org.societies.identity;

import java.util.HashMap;
import java.util.Map;

import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityContextMapper;

// TODO synchronization issues??
public class IdentityContextMapperImpl implements IIdentityContextMapper {

	private final Map<IIdentity,Map<String,CtxIdentifier>> contextMap;
	
	public IdentityContextMapperImpl() {
		contextMap = new HashMap<IIdentity, Map<String,CtxIdentifier>>();
	}
	
	public CtxIdentifier getMappedCtxIdentifier(IIdentity publicId,
			String attributeType) {
		Map<String, CtxIdentifier> tempMap = contextMap.get(publicId);
		if (tempMap!=null)
			return tempMap.get(attributeType);
		return null;
	}

	public CtxIdentifier addMappedCtxIdentifier(IIdentity publicId,
			CtxIdentifier attributeID) {
		Map<String, CtxIdentifier> tempMap = contextMap.get(publicId);
		if (tempMap==null) {
			tempMap = new HashMap<String, CtxIdentifier>();
			contextMap.put(publicId, tempMap);
		}
		tempMap.put(attributeID.getType(),attributeID);
		return attributeID; // TODO ??????
	}

	public boolean removeMappedCtxIdentifier(IIdentity publicId,
			CtxIdentifier ctxIdentifier) {
		Map<String, CtxIdentifier> tempMap = contextMap.get(publicId);
		if (tempMap!=null) {
			CtxIdentifier removed = tempMap.remove(ctxIdentifier.getType());
			if (removed!=null)
				return true;
		}
		return false;
	}

}
