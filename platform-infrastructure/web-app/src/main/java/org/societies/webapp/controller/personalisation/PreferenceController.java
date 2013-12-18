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

package org.societies.webapp.controller.personalisation;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.osgi.util.mobile.UserPromptCondition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.PrivacyUtilService;
import org.societies.webapp.service.UserService;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PreferenceController")
public class PreferenceController extends BasePageController{

	private final Logger logging = LoggerFactory.getLogger(getClass());
	
	

    @ManagedProperty(value = "#{userPreferenceConditionMonitor}")
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor;


	@ManagedProperty(value="#{privacyUtilService}")
	private PrivacyUtilService privacyUtilService;

	private IUserPreferenceManagement preferenceManager;



	private List<PreferenceDetails> preferenceDetails;
	private PreferenceDetails selectedDetail;
	
    private String preferenceDetailUUID;



	@PostConstruct
    private void init(){
    
		this.logging.info("#CODE2#: Initialising PreferenceController page");
		preferenceDetails = this.preferenceManager.getPreferenceDetailsForAllPreferences();
		if (preferenceDetails == null){
			preferenceDetails = new ArrayList<PreferenceDetails>();
		}
		
		preferenceDetailUUID = UUID.randomUUID().toString();
    }
	
	
	public String storePrefDetailToUtils(){
		if (logging.isDebugEnabled()){
			this.logging.debug("Adding user preference details bean to util service");
		}
		this.privacyUtilService.setPreferenceDetails(preferenceDetailUUID, selectedDetail);
		return "preference_edit.xhtml";
	}
	
	
	/**
	 * GET/SET METHODS
	 * 
	 */

    public IUserPreferenceConditionMonitor getUserPreferenceConditionMonitor() {
		return userPreferenceConditionMonitor;
	}


	public void setUserPreferenceConditionMonitor(IUserPreferenceConditionMonitor userPreferenceConditionMonitor) {
		this.userPreferenceConditionMonitor = userPreferenceConditionMonitor;
		preferenceManager = userPreferenceConditionMonitor.getPreferenceManager();
	}

	public List<PreferenceDetails> getPreferenceDetails() {
		return preferenceDetails;
	}


	public void setPreferenceDetails(List<PreferenceDetails> preferenceDetails) {
		this.preferenceDetails = preferenceDetails;
	}





	public String getPreferenceDetailUUID() {
		return preferenceDetailUUID;
	}





	public void setPreferenceDetailUUID(String preferenceDetailUUID) {
		this.preferenceDetailUUID = preferenceDetailUUID;
	}





	public PreferenceDetails getSelectedDetail() {
		return selectedDetail;
	}





	public void setSelectedDetail(PreferenceDetails selectedDetail) {
		this.selectedDetail = selectedDetail;
	}


	public PrivacyUtilService getPrivacyUtilService() {
		return privacyUtilService;
	}


	public void setPrivacyUtilService(PrivacyUtilService privacyUtilService) {
		this.privacyUtilService = privacyUtilService;
	}
	
}
