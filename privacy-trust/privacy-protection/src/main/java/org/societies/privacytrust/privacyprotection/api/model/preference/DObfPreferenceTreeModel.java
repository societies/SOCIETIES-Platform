package org.societies.privacytrust.privacyprotection.api.model.preference;

import java.io.Serializable;

import javax.swing.event.TreeModelListener;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ICtxAttributeIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.preference.constants.PrivacyPreferenceTypeConstants;



/**
 * This class represents a tree model for Data Obfuscation Preferences and
 * encapsulates a tree of DObfPreference objects.
 * @author Eliza
 * @version 1.0
 * @created 11-Nov-2011 17:06:54
 */
public class DObfPreferenceTreeModel extends DefaultTreeModel implements IPrivacyPreferenceTreeModel, Serializable{
	private ICtxAttributeIdentifier affectedCtxId;
	private String myContextType;
	private EntityIdentifier providerDPI;
	private ServiceResourceIdentifier serviceID;
	private PrivacyPreferenceTypeConstants myPrivacyType;
	private IPrivacyPreference pref;
	
	public DObfPreferenceTreeModel(String myCtxType, IPrivacyPreference preference){
		super(preference);
		this.myContextType = myCtxType;
		this.myPrivacyType = PrivacyPreferenceTypeConstants.DOBF;
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