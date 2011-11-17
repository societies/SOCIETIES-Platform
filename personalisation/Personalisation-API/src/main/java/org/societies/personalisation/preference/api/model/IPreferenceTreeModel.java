/**
 * Copyright (c) 2011, SOCIETIES Consortium
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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