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

package org.societies.webapp.controller.privacy.prefs;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;

import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.UserService;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PrivacyPrefsController")
public class PrivacyPreferencesController extends BasePageController{

	@ManagedProperty(value = "#{privPrefMgr}")
	private IPrivacyPreferenceManager privPrefmgr;
	
	
    @ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter

    private PPNPreferenceDetailsBean selectedPPNDetail;
    private AccessControlPreferenceDetailsBean selectedAccCtrlDetail;
	private List<PPNPreferenceDetailsBean> ppnPreferenceDetails;


	private List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails;


	private List<IDSPreferenceDetailsBean> idsPreferenceDetails;


	private List<DObfPreferenceDetailsBean> dObfPreferenceDetails;
    
	@PostConstruct
	public void initController(){
		this.retrievePPNPreferences();
		this.retrieveAccCtrlPreferences();
		this.retrieveDObfPreferences();
		this.retrieveDObfPreferences();
	}
	public void retrievePPNPreferences() {
		
		setPpnPreferenceDetails(privPrefmgr.getPPNPreferenceDetails());

	}
	
	public void retrieveAccCtrlPreferences(){
		setAccCtrlPreferenceDetails(privPrefmgr.getAccCtrlPreferenceDetails());
		
	}
	
	public void retrieveIDSPreferences(){
		setIdsPreferenceDetails(privPrefmgr.getIDSPreferenceDetails());

		
	}
	
	public void retrieveDObfPreferences(){
		setdObfPreferenceDetails(privPrefmgr.getDObfPreferenceDetails());
		DObfPreferenceDetailsBean bean;
		
	}
	
	
	public String toStringRequestor(RequestorBean requestor){
		
		if (requestor instanceof RequestorCisBean){
			return "CIS: "+((RequestorCisBean) requestor).getCisRequestorId();
			
		}
		if (requestor instanceof RequestorServiceBean){
			return "Service: "+ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) requestor).getRequestorServiceId());
			
		}
		
		return "none";
		
		
	}
	public IPrivacyPreferenceManager getPrivPrefmgr() {
		return privPrefmgr;
	}

	public void setPrivPrefmgr(IPrivacyPreferenceManager privPrefmgr) {
		this.privPrefmgr = privPrefmgr;
	}

	public UserService getUserService() {
		return userService;
	}

	public void setUserService(UserService userService) {
		this.userService = userService;
	}


	public AccessControlPreferenceDetailsBean getSelectedAccCtrlDetail() {
		return selectedAccCtrlDetail;
	}
	public void setSelectedAccCtrlDetail(AccessControlPreferenceDetailsBean selectedAccCtrlDetail) {
		this.selectedAccCtrlDetail = selectedAccCtrlDetail;
	}
	public PPNPreferenceDetailsBean getSelectedPPNDetail() {
		return selectedPPNDetail;
	}
	public void setSelectedPPNDetail(PPNPreferenceDetailsBean selectedPPNDetail) {
		this.selectedPPNDetail = selectedPPNDetail;
	}
	public List<PPNPreferenceDetailsBean> getPpnPreferenceDetails() {
		return ppnPreferenceDetails;
	}
	public void setPpnPreferenceDetails(List<PPNPreferenceDetailsBean> ppnPreferenceDetails) {
		this.ppnPreferenceDetails = ppnPreferenceDetails;
	}
	public List<AccessControlPreferenceDetailsBean> getAccCtrlPreferenceDetails() {
		return accCtrlPreferenceDetails;
	}
	public void setAccCtrlPreferenceDetails(List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails) {
		this.accCtrlPreferenceDetails = accCtrlPreferenceDetails;
	}
	public List<IDSPreferenceDetailsBean> getIdsPreferenceDetails() {
		return idsPreferenceDetails;
	}
	public void setIdsPreferenceDetails(List<IDSPreferenceDetailsBean> idsPreferenceDetails) {
		this.idsPreferenceDetails = idsPreferenceDetails;
	}
	public List<DObfPreferenceDetailsBean> getdObfPreferenceDetails() {
		return dObfPreferenceDetails;
	}
	public void setdObfPreferenceDetails(List<DObfPreferenceDetailsBean> dObfPreferenceDetails) {
		this.dObfPreferenceDetails = dObfPreferenceDetails;
	}
	
	
}
