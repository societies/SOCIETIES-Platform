/**

 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
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

package org.societies.webapp.service;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * @author Eliza
 *
 */
@Service
@Scope("Session")
@SessionScoped // JSF
@ManagedBean // JSF
public class PrivacyUtilService implements Serializable{

	private final Logger logging = LoggerFactory.getLogger(getClass());

	Map<String, PPNPreferenceDetailsBean> ppnDetails;
	Map<String, AccessControlPreferenceDetailsBean> accCtrlDetails;
	Map<String, DObfPreferenceDetailsBean> dobfDetails;
	Map<String, PreferenceDetails> prefDetails;

	public PrivacyUtilService(){
		this.ppnDetails = new HashMap<String, PPNPreferenceDetailsBean>();
		this.accCtrlDetails = new HashMap<String, AccessControlPreferenceDetailsBean>();
		this.dobfDetails = new HashMap<String, DObfPreferenceDetailsBean>();
		this.prefDetails = new HashMap<String, PreferenceDetails>();
	}

	public PPNPreferenceDetailsBean getPpnPreferenceDetailsBean(String key){
		if (this.ppnDetails.containsKey(key)){
			return this.ppnDetails.get(key);
		}
		return null;
	}

	public AccessControlPreferenceDetailsBean getAccessControlPreferenceDetailsBean(String key){
		if (this.accCtrlDetails.containsKey(key)){
			return this.accCtrlDetails.get(key);
		}

		return null;
	}

	public DObfPreferenceDetailsBean getDObfPreferenceDetailsBean(String key){
		if (this.dobfDetails.containsKey(key)){
			return this.dobfDetails.get(key);
		}

		return null;
	}

	public PreferenceDetails getPreferenceDetail(String key){
		if (this.prefDetails.containsKey(key)){
			return this.prefDetails.get(key);
		}
		return null;
	}

	public void setPpnPreferenceDetailsBean(String key, PPNPreferenceDetailsBean bean){
		this.logging.debug("Adding ppn detail to util service: "+key+" and bean: \n"+bean.toString());
		this.ppnDetails.put(key, bean);
	}
	public void setAccessControlPreferenceDetailsBean(String key, AccessControlPreferenceDetailsBean bean){
		this.logging.debug("Adding accCtrl detail to util service: "+key+" and bean: \n"+bean.toString());
		this.accCtrlDetails.put(key, bean);
	}

	public void setDObfPreferenceDetailsBean(String key, DObfPreferenceDetailsBean bean){
		this.logging.debug("Adding dobf detail to util service: "+key+" and bean: \n"+bean.toString());
		this.dobfDetails.put(key, bean);
	}

	public void setPreferenceDetails(String key, PreferenceDetails detail){
		this.logging.debug("Adding preference detail to util service: "+key+" and detail: "+detail.toString());
		this.prefDetails.put(key, detail);
	}
	public void removePpnPreferenceDetailsBean(String key){
		if (this.ppnDetails.containsKey(key)){
			ppnDetails.remove(key);
		}
	}

	public void removeAccCtrlPreferenceDetailsBean(String key){
		if (this.accCtrlDetails.containsKey(key)){
			accCtrlDetails.remove(key);
		}
	}

	public void removeDObfPreferenceDetailsBean(String key){
		if (this.dobfDetails.containsKey(key)){
			dobfDetails.remove(key);
		}
	}



	public void removePrefDetail(String key){
		if (this.prefDetails.containsKey(key)){
			prefDetails.remove(key);
		}
	}
}
