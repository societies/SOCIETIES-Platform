package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import javax.swing.tree.DefaultTreeModel;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;

/**
 * This class is used to represent a privacy preference for identity selection. This class represents a node in a tree. 
 * If the node is a branch, then the embedded object of the node is a condition (IPrivacyPreferenceCondition), otherwise, 
 * if it's a leaf, the embedded object is an IdentitySelectionPreferenceOutcome.
 * @author Elizabeth
 *
 */
public class IDSPrivacyPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable {

	
	private EntityIdentifier affectedDPI;
	private EntityIdentifier serviceDPI;
	private ServiceResourceIdentifier serviceID;
	private PrivacyPreferenceTypeConstants myPrivacyType;
	private IPrivacyPreference pref;
	
	public IDSPrivacyPreferenceTreeModel(EntityIdentifier affectedDPI,  IPrivacyPreference preference){
		super(preference);
		this.setAffectedDPI(affectedDPI);
		this.myPrivacyType = PrivacyPreferenceTypeConstants.IDS;
		this.pref = preference;
	}


	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceTreeModel#getPrivacyType()
	 */
	@Override
	public PrivacyPreferenceTypeConstants getPrivacyType() {
		return this.getPrivacyType();
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.spm.preference.api.platform.IPrivacyPreferenceTreeModel#getRootPreference()
	 */
	@Override
	public IPrivacyPreference getRootPreference() {
		return this.pref;
	}


	public void setAffectedDPI(EntityIdentifier affectedDPI) {
		this.affectedDPI = affectedDPI;
	}


	public EntityIdentifier getAffectedDPI() {
		return affectedDPI;
	}


	public void setServiceID(ServiceResourceIdentifier serviceID) {
		this.serviceID = serviceID;
	}


	public ServiceResourceIdentifier getServiceID() {
		return serviceID;
	}


	public void setServiceDPI(EntityIdentifier serviceDPI) {
		this.serviceDPI = serviceDPI;
	}


	public EntityIdentifier getServiceDPI() {
		return serviceDPI;
	}

}

