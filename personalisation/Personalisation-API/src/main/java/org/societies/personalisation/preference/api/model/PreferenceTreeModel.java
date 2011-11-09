package org.societies.personalisation.preference.api;

import java.io.Serializable;
import java.util.Date;

import javax.swing.tree.DefaultTreeModel;

import org.societies.personalisation.common.api.ServiceResourceIdentifier;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class PreferenceTreeModel extends DefaultTreeModel implements IPreferenceTreeModel, Serializable {

	private ServiceResourceIdentifier serviceID;
	private String serviceType;
	private String preferenceName;
	private IPreference preference;
	private Date lastModifiedDate;

	public PreferenceTreeModel(IPreference root) {
		super(root);
		this.preference = root;
	}

	public IPreference getRootPreference(){
		return this.preference;
	}
	public String getPreferenceName() {
		return this.preferenceName;
	}

	public ServiceResourceIdentifier getServiceID() {
		return this.serviceID;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setPreferenceName(String prefname) {
		this.preferenceName = prefname;
	}

	public void setServiceID(ServiceResourceIdentifier id) {
		this.serviceID = id;
		
	}
	public void setServiceType(String type) {
		this.serviceType = type;
		
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceTreeModel#getLastModifiedDate()
	 */
	@Override
	public Date getLastModifiedDate() {
		return this.lastModifiedDate;
	}

	/* (non-Javadoc)
	 * @see org.personalsmartspace.pm.prefmodel.api.platform.IPreferenceTreeModel#setLastModifiedDate(java.util.Date)
	 */
	@Override
	public void setLastModifiedDate(Date d) {
		this.lastModifiedDate = d;
	}
	


}