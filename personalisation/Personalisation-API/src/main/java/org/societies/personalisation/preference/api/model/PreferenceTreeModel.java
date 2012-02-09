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

import javax.swing.tree.DefaultTreeModel;

import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;


/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 14:02:57
 */
public class PreferenceTreeModel extends DefaultTreeModel implements IPreferenceTreeModel, Serializable {

	private IServiceResourceIdentifier serviceID;
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

	public IServiceResourceIdentifier getServiceID() {
		return this.serviceID;
	}

	public String getServiceType() {
		return this.serviceType;
	}

	public void setPreferenceName(String prefname) {
		this.preferenceName = prefname;
	}

	public void setServiceID(IServiceResourceIdentifier id) {
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