package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import javax.swing.tree.DefaultTreeModel;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;

/**
 * This class represents a tree model for Privacy Policy Negotiation Preferences and encapsulates a tree of IPrivacyPreference objects.
 * @author Elizabeth
 *
 */
public class PPNPrivacyPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable {

	
	private ICtxAttributeIdentifier affectedCtxId;
	private String myContextType;
	private EntityIdentifier providerDPI;
	private ServiceResourceIdentifier serviceID;
	private PrivacyPreferenceTypeConstants myPrivacyType;
	private IPrivacyPreference pref;
	
	public PPNPrivacyPreferenceTreeModel(String myCtxType, IPrivacyPreference preference){
		super(preference);
		this.myContextType = myCtxType;
		this.myPrivacyType = PrivacyPreferenceTypeConstants.PPNP;
		this.pref = preference;
	}
	
	public ICtxAttributeIdentifier getAffectedContextIdentifier() {
		return this.getAffectedCtxId();
	}

	
	public String getContextType() {
		return this.myContextType;
	}


	@Override
	public PrivacyPreferenceTypeConstants getPrivacyType() {
		return this.myPrivacyType;
	}


	@Override
	public IPrivacyPreference getRootPreference() {
		return this.pref;
	}

	public void setAffectedCtxId(ICtxAttributeIdentifier affectedCtxId) {
		this.affectedCtxId = affectedCtxId;
	}

	public ICtxAttributeIdentifier getAffectedCtxId() {
		return affectedCtxId;
	}

	public void setProviderDPI(EntityIdentifier providerDPI) {
		this.providerDPI = providerDPI;
	}

	public EntityIdentifier getProviderDPI() {
		return providerDPI;
	}

	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}

	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}

}
