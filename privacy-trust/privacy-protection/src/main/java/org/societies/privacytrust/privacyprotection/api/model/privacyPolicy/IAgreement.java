package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;

import java.util.List;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public interface IAgreement {
	
	
	public List<RequestItem> getRequestedItems();
	
	public ServiceResourceIdentifier getServiceIdentifier();
	
	public void setServiceIdentifier(ServiceResourceIdentifier serviceId);
	
	public EntityIdentifier getServiceDPI();
	
	public void setServiceDPI(EntityIdentifier serviceDPI);
	
	public EntityIdentifier getUserDPI();
	
	public void setUserDPI(EntityIdentifier userDPI);
	
	public EntityIdentifier getUserPublicDPI();
	
	public void setUserPublicDPI(EntityIdentifier userPublicDPI);
}
