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

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.context.RequestContext;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.DataTypeUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.privacytrust.privacy.model.dataobfuscation.ObfuscatorInfo;
import org.societies.api.internal.privacytrust.privacy.util.dataobfuscation.ObfuscatorInfoFactory;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.DObfPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.webapp.controller.privacy.ResourceUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.dobf.DObfPreferenceTreeModel;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="dobfcreateBean")
public class DObfCreateBean implements Serializable{

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commMgr;

	@ManagedProperty(value = "#{privPrefMgr}")
	private IPrivacyPreferenceManager privPrefmgr;


	private final Logger logging = LoggerFactory.getLogger(getClass());
	private DObfPreferenceDetailsBean preferenceDetails = new DObfPreferenceDetailsBean();
	private int requestorType;
	private String requestorCis;
	private boolean preferenceDetailsCorrect;
	private String requestorService;

	private TreeNode selectedNode;
	private TreeNode root;

	private DataTypeUtils dataTypeUtils = new DataTypeUtils();
	private ObfuscatorInfo obfuscatorInfo;
	private double obfuscationLevel = 1.0;
	private int disreteObfuscationLevel = -1;
	private int continuousObfuscationLevel = 100;

	private String displaySpecificRequestor;

	private DObfPreferenceTreeModel existingDObfPreference;

	@PostConstruct
	public void setup(){

		preferenceDetails.setRequestor(new RequestorBean());
		preferenceDetails.getRequestor().setRequestorId("");
		preferenceDetails.setResource(new Resource());
		preferenceDetails.getResource().setDataType("");

		/*		this.createCtxAttributeTypesList();
		this.createSchemeList();
		setOperators(Arrays.asList(OperatorConstants.values()));
		this.setupConditions();*/

	}

	/*
	 * METHODS CALLED FROM XHTML:
	 */

	public String getDisplaySpecificRequestor() {
		try{
			if (this.preferenceDetails.getRequestor() instanceof RequestorCisBean){
				displaySpecificRequestor = "Cis: "+((RequestorCisBean) this.preferenceDetails.getRequestor()).getCisRequestorId();
			}else if (this.preferenceDetails.getRequestor() instanceof RequestorServiceBean){
				displaySpecificRequestor = "Service: "+ ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) this.preferenceDetails.getRequestor()).getRequestorServiceId());
			}else {
				displaySpecificRequestor = "None";
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return displaySpecificRequestor;
	}

	public void savePreference(){
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saving preference", "Now saving prefernece");
		FacesContext.getCurrentInstance().addMessage(null, message);
		if (logging.isDebugEnabled()){
			this.logging.debug("savePreferences called");
		}
		PrivacyPreference privacyPreference = ModelTranslator.getPrivacyPreference(root);
		IPrivacyPreferenceCondition erroneousNode = ModelTranslator.checkPreference(privacyPreference);
		if (erroneousNode!=null){
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure", "Please add an outcome under this node: \n"+erroneousNode.toString()+"\nError: Condition cannot be leaf of the tree. ");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Printing preference before save: \n"+privacyPreference.toString());
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Saving preferences with details: "+preferenceDetails.toString());
		}

		DObfPreferenceTreeModel model = new DObfPreferenceTreeModel(preferenceDetails, privacyPreference);


		if (this.privPrefmgr.storeDObfPreference(preferenceDetails, model)){
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Your new Data Obfuscation preference has been successfully saved.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure", "An error occurred while saving your new Data Obfuscation preference.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void loadExistingPreference(){
		TreeNode node = new DefaultTreeNode("Root!", null);
		this.root = ModelTranslator.getPrivacyPreference(this.existingDObfPreference.getRootPreference(), node);
		printTree();
	}

	private void printTree(){
		if (this.root==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("root is null. tree is corrupted");
			}
			return;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("********** <TREE **********");
		}

		String tree = "\nRoot: "+root.getData()+" "+root.getChildCount();
		List<TreeNode> children = this.root.getChildren();
		for (TreeNode child : children){
			tree = tree.concat(getChildrenToPrint(child));
		}
		if (logging.isDebugEnabled()){
			this.logging.debug(tree);
		}

		if (logging.isDebugEnabled()){
			this.logging.debug("******** </TREE> ************");
		}

	}

	private String getChildrenToPrint(TreeNode node){
		String str = "\n"+node;

		str = str.concat(" "+node.getChildCount());
		List<TreeNode> children = node.getChildren();
		for (TreeNode child : children){
			str = str+child+"\n";
			return str.concat(getChildrenToPrint(child));


		}
		return str;
	}
	public void savePreferenceDetails(){
		ServiceResourceIdentifier serviceID; 
		String rType = "simple";
		String specific = "";

		if (requestorType==0){

			try {
				if (logging.isDebugEnabled()){
					this.logging.debug("validating cis ID:"+requestorCis);
				}
				IIdentity cisid = this.commMgr.getIdManager().fromJid(requestorCis);
				((RequestorCisBean) this.preferenceDetails.getRequestor()).setCisRequestorId(cisid.getBareJid());
				rType = "cis";
				specific = "\nid: "+((RequestorCisBean) this.preferenceDetails.getRequestor()).getCisRequestorId();
				if (logging.isDebugEnabled()){
					this.logging.debug("successfully validated CIS id");
				}
			} catch (InvalidFormatException e) {
				if (logging.isDebugEnabled()){
					this.logging.debug("caught exception while validating cis id");
				}
				e.printStackTrace();
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("CIS Jid is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}

		}else if (requestorType==1){
			try{
				if (logging.isDebugEnabled()){
					this.logging.debug("validating service id: "+requestorService);
				}
				serviceID = ServiceModelUtils.generateServiceResourceIdentifierFromString(requestorService);
				rType = "service";
				((RequestorServiceBean) this.preferenceDetails.getRequestor()).setRequestorServiceId(serviceID);
				specific = "\nid: "+((RequestorServiceBean) this.preferenceDetails.getRequestor()).getRequestorServiceId();
				if (logging.isDebugEnabled()){
					this.logging.debug("successfully validated service id");
				}
			}
			catch (Exception e){
				if (logging.isDebugEnabled()){
					this.logging.debug("caught exception while generating service resource id");
				}
				e.printStackTrace();
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("ServiceID is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}

			if (serviceID == null){
				if (logging.isDebugEnabled()){
					this.logging.debug("service id is null");
				}
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("ServiceID is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}
		}

		if (preferenceDetails.getResource().getDataType()==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Resource dataType is null");
			}
			preferenceDetailsCorrect = false;
			FacesMessage message = new FacesMessage("Resource dataType is null");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}

		existingDObfPreference = this.privPrefmgr.getDObfPreference(preferenceDetails);
		if (existingDObfPreference!=null){
			RequestContext.getCurrentInstance().execute("pdcd.show();");
		}
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("prefDetailsDlg.hide()");
		if (logging.isDebugEnabled()){
			this.logging.debug("Successfully validated preferenceDetails");
		}
		this.preferenceDetailsCorrect = true;

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "DObf preference details set", "Set requestor: "+preferenceDetails.getRequestor().getRequestorId()+
				"\n, type: "+rType+specific+"\nSet resource: "+preferenceDetails.getResource().getDataType());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void startAddConditionProcess(){
		RequestContext.getCurrentInstance().execute("addConddlg.show();");
	}

	public void startAddOutcomeProcess(){
		RequestContext.getCurrentInstance().execute("addOutdlg.show();");
	}
	public void startAddPrivacyConditionProcess(){
		RequestContext.getCurrentInstance().execute("addPrivConddlg.show();");
	}

	public void startAddTrustConditionProcess(){
		RequestContext.getCurrentInstance().execute("addTrustConddlg.show();");
	}

	public void displaySelectedSingle() {
		if(selectedNode != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());

			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not Selected", "You need to select a node to display");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}

	public void addOutcome() {
		if (null == selectedNode) {
			logging.debug("selected node is null - addOutcome");
			return;
		}

		// -- Retrieve selected obfuscation level
		logging.debug("Selected obfuscation level: {}", obfuscationLevel);

		// -- Store obfuscation level
		// TODO ?
	}


	public void deleteNode() {
		if (selectedNode==null){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Node not selected", "You must select a node to delete");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		if (selectedNode.getParent()==null){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Deleting root node", "You can't delete the root node. If you want to cancel, this use the back button");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}


		selectedNode.getChildren().clear();
		selectedNode.getParent().getChildren().remove(selectedNode);
		selectedNode.setParent(null);

		selectedNode = null;

		if (this.root.getChildCount()==0){
			DObfOutcome outcome = new DObfOutcome(0.0);


			TreeNode node0 = new DefaultTreeNode(outcome, root);
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion", "Node deleted");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void setRequestorType(int requestorType) {
		String requestorId = this.preferenceDetails.getRequestor().getRequestorId();
		if (!preferenceDetailsCorrect){
			switch (requestorType){
			case 0:

				RequestorCisBean cisBean = new RequestorCisBean();
				cisBean.setRequestorId(requestorId);
				this.preferenceDetails.setRequestor(cisBean);
				if (logging.isDebugEnabled()){
					this.logging.debug("setting requestor Type :"+requestorType);
				}

				break;
			case 1:

				RequestorServiceBean serviceBean = new RequestorServiceBean();
				serviceBean.setRequestorId(requestorId);
				this.preferenceDetails.setRequestor(serviceBean);
				if (logging.isDebugEnabled()){
					this.logging.debug("setting requestor Type :"+requestorType);
				}


				break;
			default:

				RequestorBean bean = new RequestorBean();
				bean.setRequestorId(requestorId);
				this.preferenceDetails.setRequestor(bean);
				if (logging.isDebugEnabled()){
					this.logging.debug("setting requestor Type :"+requestorType);
				}

				break;
			}
		}else{
			if (logging.isDebugEnabled()){
				this.logging.debug("setting requestorType: "+requestorType+" but not changing the preferenceDetails");
			}
		}
		this.requestorType = requestorType;
	}



	/*
	 * GET/SET METHODS
	 */

	public int getRequestorType() {
		return requestorType;
	}


	public String getRequestorCis() {
		return requestorCis;
	}

	public void setRequestorCis(String requestorCis) {
		this.requestorCis = requestorCis;
	}

	public DObfPreferenceDetailsBean getPreferenceDetails() {
		return preferenceDetails;
	}

	public void setPreferenceDetails(DObfPreferenceDetailsBean preferenceDetails) {
		this.preferenceDetails = preferenceDetails;
	}
	public boolean isPreferenceDetailsCorrect() {
		return preferenceDetailsCorrect;
	}
	public void setPreferenceDetailsCorrect(boolean preferenceDetailsCorrect) {
		this.preferenceDetailsCorrect = preferenceDetailsCorrect;
	}
	public String getRequestorService() {
		return requestorService;
	}
	public void setRequestorService(String requestorService) {
		this.requestorService = requestorService;
	}
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
	}
	public IPrivacyPreferenceManager getPrivPrefmgr() {
		return privPrefmgr;
	}
	public void setPrivPrefmgr(IPrivacyPreferenceManager privPrefmgr) {
		this.privPrefmgr = privPrefmgr;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}

	public TreeNode getRoot() {
		return root;
	}

	public void setRoot(TreeNode root) {
		this.root = root;
	}
	
	public double getObfuscationLevel() {
		return obfuscationLevel;
	}
	public void setObfuscationLevel(double obfuscationLevel) {
		this.obfuscationLevel = obfuscationLevel;
	}
	
	public int getDisreteObfuscationLevel() {
		if (-1 == disreteObfuscationLevel) {
			disreteObfuscationLevel = getObfuscatorInfo().getNbOfObfuscationLevelStep();
		}
		return disreteObfuscationLevel;
	}
	public void setDisreteObfuscationLevel(int disContObfuscationLevel) {
		this.obfuscationLevel = (double)disContObfuscationLevel/(double)obfuscatorInfo.getNbOfObfuscationLevelStep();
		this.disreteObfuscationLevel = disContObfuscationLevel;
		logging.debug("Discrete obfuscation level: {} -> {}", disContObfuscationLevel, obfuscationLevel);
	}

	public int getContinuousObfuscationLevel() {
		return continuousObfuscationLevel;
	}
	public void setContinuousObfuscationLevel(int contObfuscationLevel) {
		this.obfuscationLevel = (double)contObfuscationLevel/(double)100;
		this.continuousObfuscationLevel = contObfuscationLevel;
		logging.debug("Continuous obfuscation level: {} -> {}", contObfuscationLevel, obfuscationLevel);
	}

	public ObfuscatorInfo getObfuscatorInfo() {
		ObfuscatorInfoFactory factory = new ObfuscatorInfoFactory();
		obfuscatorInfo = factory.getObfuscatorInfo(preferenceDetails.getResource().getDataType());
		if (-1 == disreteObfuscationLevel) {
			disreteObfuscationLevel = obfuscatorInfo.getNbOfObfuscationLevelStep();
		}
		return obfuscatorInfo;
	}
	public void setObfuscatorInfo(ObfuscatorInfo obfuscatorInfo) {
		this.obfuscatorInfo = obfuscatorInfo;
	}
	
	public DataTypeUtils getDataTypeUtils() {
		return dataTypeUtils;
	}
}
