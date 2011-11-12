package org.societies.privacytrust.privacyprotection.api.model.privacyPolicy;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

/**
 * The NegotiationAgreement class represents the agreement reached between the user and the service provider. 
 * It lists all the personal data that hte user has agreed to give the provider access to and what operations 
 * the provider is allowed to perform on these personal data. It also contains all the obligations the provider 
 * and the user have agreed for each item in the personal data such as the data retention period, sharing with 
 * third parties, the right to opt-out etc. The NegotiationAgreement document is drafted by the user after a 
 * successful negotiation and sent to the provider embedded in an AgreementEnvelope object.
 * @author Elizabeth
 *
 */
public class NegotiationAgreement implements IAgreement, Serializable {

	private List<RequestItem> items;
	private ServiceResourceIdentifier serviceID;
	private EntityIdentifier serviceDPI;
	private EntityIdentifier userDPI;
	private EntityIdentifier userPublicDPI;

	private NegotiationAgreement(){
		this.items = new ArrayList<RequestItem>();
	}

	public NegotiationAgreement(ResponsePolicy policy){
		this.serviceID = policy.getSubject().getServiceID();
		this.serviceDPI = policy.getSubject().getDPI();
		List<RequestItem> l = new ArrayList<RequestItem>();
		for (ResponseItem r : policy.getResponseItems()){
			l.add(r.getRequestItem());
		}
		this.items = java.util.Collections.unmodifiableList(l);
	}
	public NegotiationAgreement(List<RequestItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}
	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getServiceIdentifier()
	 */
	@Override
	public ServiceResourceIdentifier getServiceIdentifier() {
		
		return this.serviceID;
	}
	
	public void setRequestItems(List<RequestItem> items){
		this.items = java.util.Collections.unmodifiableList(items);
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getServiceDPI()
	 */
	@Override
	public EntityIdentifier getServiceDPI() {
		return this.serviceDPI;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getUserDPI()
	 */
	@Override
	public EntityIdentifier getUserDPI() {
		return this.userDPI;
	}

	public EntityIdentifier getUserPublicDPI(){
		return this.userPublicDPI;
	}

	public void setUserPublicDPI(EntityIdentifier userPublicDPI){
		this.userPublicDPI = userPublicDPI;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setServiceDPI(org.personalsmartspace.sre.api.pss3p.EntityIdentifier)
	 */
	@Override
	public void setServiceDPI(EntityIdentifier serviceDPI) {
		this.serviceDPI = serviceDPI;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setServiceIdentifier(org.personalsmartspace.sre.api.pss3p.ServiceResourceIdentifier)
	 */
	@Override
	public void setServiceIdentifier(ServiceResourceIdentifier serviceId) {
		this.serviceID = serviceId;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#setUserDPI(org.personalsmartspace.sre.api.pss3p.EntityIdentifier)
	 */
	@Override
	public void setUserDPI(EntityIdentifier userDPI) {
		this.userDPI = userDPI;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.policy.api.platform.IAgreement#getRequestedItems()
	 */
	@Override
	public List<RequestItem> getRequestedItems() {
		return this.items;
	}

}

