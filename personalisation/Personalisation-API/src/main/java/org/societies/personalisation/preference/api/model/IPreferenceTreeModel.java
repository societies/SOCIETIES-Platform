package org.societies.personalisation.preference.api.model;

import java.io.Serializable;
import java.util.Date;

import javax.swing.tree.TreeModel;

import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:56
 */
public interface IPreferenceTreeModel extends TreeModel, Serializable {

	/**
	 * Method to retrieve the date this preference was last modified either due to
	 * learning or by the user using the GUI
	 * @return	the Date object
	 */
	public Date getLastModifiedDate();

	/**
	 * Method to retrieve the name of the preference object included in this model
	 * @return the name of the preference as String
	 */
	public String getPreferenceName();

	/**
	 * Method to return the preference object included in this model (Returns the root
	 * node of the preference tree)
	 * @return	the preference object
	 */
	public IPreference getRootPreference();

	/**
	 *
	 * Method to retrieve the serviceID of the service affected by this preference
	 * model
	 * @return	the serviceID as String
	 */
	public ServiceResourceIdentifier getServiceID();

	/**
	 * Method to retrieve the service type of the service affected by this preference
	 * model
	 * @return	the serviceType as String
	 */
	public String getServiceType();

	/**
	 * Method to record the last time this preference changed either due to learning
	 * or by the user using the GUI
	 * 
	 * @param d    d
	 */
	public void setLastModifiedDate(Date d);

	/**
	 * Method to set the preference name of this object
	 * 
	 * @param prefname    the name of the preference object included in this model
	 */
	public void setPreferenceName(String prefname);

	/**
	 * 
	 * Method to set the serviceID of the service affected by this preference model
	 * 
	 * @param id    the serviceID
	 */
	public void setServiceID(ServiceResourceIdentifier id);

	/**
	 * Method to set the serviceType of the service affected by this preference model
	 * 
	 * @param type    the type of service as String
	 */
	public void setServiceType(String type);

}