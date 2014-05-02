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
import java.util.UUID;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.util.DataTypeUtils;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.service.PrivacyUtilService;
import org.societies.webapp.service.UserService;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PrivacyPrefsController")
public class PrivacyPreferencesController extends BasePageController{

	private final Logger logging = LoggerFactory.getLogger(getClass());
	
	@ManagedProperty(value = "#{privPrefMgr}")
	private IPrivacyPreferenceManager privPrefmgr;
	
	
    @ManagedProperty(value = "#{userService}")
    private UserService userService; // NB: MUST include public getter/setter


	@ManagedProperty(value="#{privacyUtilService}")
	private PrivacyUtilService privacyUtilService;
	
	@ManagedProperty(value="#{RequestorsController}")
	private RequestorsController requestorsController;

    private PPNPreferenceDetailsBean selectedPPNDetail;
    private AccessControlPreferenceDetailsBean selectedAccCtrlDetail;
    private DObfPreferenceDetailsBean selecteddobfDetail;
	private List<PPNPreferenceDetailsBean> ppnPreferenceDetails;


	private List<AccessControlPreferenceDetailsBean> accCtrlPreferenceDetails;


	private List<IDSPreferenceDetailsBean> idsPreferenceDetails;


	private List<DObfPreferenceDetailsBean> dObfPreferenceDetails;


	private String ppnUUID;
	private String dobfUUID;
	private String accCtrlUUID;
	
    
	@PostConstruct
	public void initController(){
		this.logging.info("#CODE2#: Initialising PrivacyPreference controller");
		this.retrievePPNPreferences();
		this.retrieveAccCtrlPreferences();
		this.retrieveDObfPreferences();
		this.retrieveDObfPreferences();
		ppnUUID = UUID.randomUUID().toString();
		accCtrlUUID = UUID.randomUUID().toString();
		dobfUUID = UUID.randomUUID().toString();
	}
	public void retrievePPNPreferences() {
		
		setPpnPreferenceDetails(privPrefmgr.getPPNPreferenceDetails());
		

	}
	
	public String storePPNDetailToUtils(){
		if (logging.isDebugEnabled()){
			this.logging.debug("Adding ppn preference details bean to util service");
		}
		this.privacyUtilService.setPpnPreferenceDetailsBean(ppnUUID, selectedPPNDetail);
		return "privacy_ppn_edit.xhtml";
		
	}
	

	
	public String storeAccCtrlDetailToUtils(){
		if (logging.isDebugEnabled()){
			this.logging.debug("Adding accCtrl preference details bean to util service");
		}
		this.privacyUtilService.setAccessControlPreferenceDetailsBean(accCtrlUUID, selectedAccCtrlDetail);
		return "privacy_accCtrl_edit.xhtml";
	}
	
	public String storeDObfDetailToUtils(){
		if (logging.isDebugEnabled()){
			this.logging.debug("Adding dobf preference details bean to util service");
		}
		this.privacyUtilService.setDObfPreferenceDetailsBean(dobfUUID, this.selecteddobfDetail);
		return "privacy_dobf_edit.xhtml";
		
	}
	
	public void retrieveAccCtrlPreferences(){
		setAccCtrlPreferenceDetails(privPrefmgr.getAccCtrlPreferenceDetails());
		
	}
	
	public void retrieveIDSPreferences(){
		setIdsPreferenceDetails(privPrefmgr.getIDSPreferenceDetails());
	}
	
	public void retrieveDObfPreferences(){
		setdObfPreferenceDetails(privPrefmgr.getDObfPreferenceDetails());
	}
	
	
	public String toStringDataType(String dataType) {
		DataTypeUtils dataTypeUtils = new DataTypeUtils();
		return dataTypeUtils.getFriendlyDescription(dataType).getFriendlyName();
	}
	 public static String capitalize(String s) {
	        if (s.length() == 0) return s;
	        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
	    }
	
	public String toStringRequestor(RequestorBean requestor){
		if (requestor==null){
			return "Unknown requestor";
		}
		String user = requestor.getRequestorId();
		user = user.replaceAll(".societies.local.macs.hw.ac.uk", "");
		user = capitalize(user);
		if (requestor instanceof RequestorCisBean){
			List<CisAdvertisementRecord> cisListByOwner = this.requestorsController.getCisListByOwner(requestor.getRequestorId());
			String cisName = "";
			for (CisAdvertisementRecord record : cisListByOwner){
				if (record.getId().equalsIgnoreCase(((RequestorCisBean) requestor).getCisRequestorId())){
					cisName = record.getName();
				}
			}
			
			return "CIS: "+cisName+" <br/> User: "+user;
			
		}
		if (requestor instanceof RequestorServiceBean){
			//String completeStr = ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) requestor).getRequestorServiceId());
			return "Service: "+((RequestorServiceBean) requestor).getRequestorServiceId().getServiceInstanceIdentifier() + "<br/> User: "+user;

			
		}
		
		return "User: " + user;
		
		
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
		if (logging.isDebugEnabled()){
			this.logging.debug("Setting selectedPPNDetail: "+selectedPPNDetail.toString());
		}
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
	public PrivacyUtilService getPrivacyUtilService() {
		return privacyUtilService;
	}
	public void setPrivacyUtilService(PrivacyUtilService privacyUtilService) {
		this.privacyUtilService = privacyUtilService;
	}

	public String getPpnUUID() {
		return ppnUUID;
	}
	public void setPpnUUID(String ppnUUID) {
		this.ppnUUID = ppnUUID;
	}
	public String getAccCtrlUUID() {
		return accCtrlUUID;
	}
	public void setAccCtrlUUID(String accCtrlUUID) {
		this.accCtrlUUID = accCtrlUUID;
	}
	public String getDobfUUID() {
		return dobfUUID;
	}
	public void setDobfUUID(String dobfUUID) {
		this.dobfUUID = dobfUUID;
	}
	public DObfPreferenceDetailsBean getSelecteddobfDetail() {
		return selecteddobfDetail;
	}
	public void setSelecteddobfDetail(DObfPreferenceDetailsBean selecteddobfDetail) {
		this.selecteddobfDetail = selecteddobfDetail;
	}
	public RequestorsController getRequestorsController() {
		return requestorsController;
	}
	public void setRequestorsController(RequestorsController requestorsController) {
		this.requestorsController = requestorsController;
	}

	
	
}
