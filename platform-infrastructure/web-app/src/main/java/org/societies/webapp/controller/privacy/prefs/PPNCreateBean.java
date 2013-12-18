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
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.privacytrust.privacy.model.privacypolicy.constants.PrivacyConditionsConstantValues;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.ConditionConstants;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPreferenceManager;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPrivacyPreferenceTreeModel;
import org.societies.webapp.controller.BasePageController;
/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PPNcreateBean")
public class PPNCreateBean extends BasePageController implements Serializable{

	private final Logger logging = LoggerFactory.getLogger(getClass());

	private TreeNode root;

	private TreeNode selectedNode;



	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commMgr;

	@ManagedProperty(value = "#{privPrefMgr}")
	private IPrivacyPreferenceManager privPrefmgr;

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	private IIdentity userId;



	private List<OperatorConstants> operators = new ArrayList<OperatorConstants>();

	private OperatorConstants selectedOperator;

	private PPNPreferenceDetailsBean preferenceDetails = new PPNPreferenceDetailsBean();

	private int requestorType;

	private String requestorService;
	private String requestorCis;

	private boolean preferenceDetailsCorrect = false;

	private Decision selectedDecision;
	private List<Decision> decisions;

	private ConditionConstants selectedPrivacyCondition;
	private Map<ConditionConstants,ConditionConstants> privacyConditions;

	private String selectedPrivacyValue;
	private Map<String, String> privacyValues;

	private Map<ConditionConstants,Map<String,String>> privacyConditionData = new HashMap<ConditionConstants, Map<String,String>>(); 


	private String trustValue;

	private String displaySpecificRequestor;

	private List<String> contextTypes;

	private List<String> cisTypes;

	private List<String> deviceTypes;

	private List<String> activityTypes;

	private List<String> resourceTypes = new ArrayList<String>();

	private List<DataIdentifierScheme> schemeList;

	private PPNPrivacyPreferenceTreeModel existingPpnPreference;





	public PPNCreateBean() {

	}


	@PostConstruct
	public void setup(){
		this.logging.info("#CODE2#: Initialising PPNCreate controller");
		preferenceDetails.setRequestor(new RequestorBean());
		preferenceDetails.getRequestor().setRequestorId("");
		preferenceDetails.setResource(new Resource());
		preferenceDetails.getResource().setDataType("");
		preferenceDetails.setAction(new Action());

		this.createSchemeList();
		this.setupDataTypes();
		this.createCtxAttributeTypesList();
		this.selectScheme(this.schemeList.get(0));
		this.handleSchemeTypeChange();
		
		setOperators(Arrays.asList(OperatorConstants.values()));
		setDecisions(Arrays.asList(Decision.values()));
		this.setupConditions();

	}


	private void setupConditions(){
		List<ConditionConstants> conditionsList = Arrays.asList(ConditionConstants.values());
		this.privacyConditions = new HashMap<ConditionConstants, ConditionConstants>();
		for (ConditionConstants c: conditionsList){
			this.privacyConditions.put(c, c);
			List<String> list = Arrays.asList(PrivacyConditionsConstantValues.getValues(c));

			Map<String, String> temp = new HashMap<String, String>();

			for (String l : list){
				String userFriendlyString = l;
				if (l.equalsIgnoreCase("0")){
					userFriendlyString = "No";
				}else if (l.equalsIgnoreCase("1")){
					userFriendlyString = "Yes";
				}
				temp.put(userFriendlyString, l);
			}

			this.privacyConditionData.put(c, temp);

		}

		ConditionConstants cc = this.privacyConditionData.keySet().iterator().next();
		this.setPrivacyValues(this.privacyConditionData.get(cc));
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
			FacesMessage message = new FacesMessage("Please select a valid resourceType");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}

		if (preferenceDetails.getAction()==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("Action is null");
			}
			preferenceDetailsCorrect = false;
			FacesMessage message = new FacesMessage("Please select an action");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		existingPpnPreference = this.privPrefmgr.getPPNPreference(preferenceDetails);
		if (existingPpnPreference!=null){
			RequestContext.getCurrentInstance().execute("pdcd.show();");
		}
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("prefDetailsDlg.hide()");
		if (logging.isDebugEnabled()){
			this.logging.debug("Successfully validated preferenceDetails");
		}
		this.preferenceDetailsCorrect = true;

		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "PPN preference details set", "Set requestor: "+preferenceDetails.getRequestor().getRequestorId()+
				"\n, type: "+rType+specific+"\nSet resource: "+preferenceDetails.getResource().getDataType());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public void loadExistingPreference(){
		TreeNode node = new DefaultTreeNode("Root!", null);
		this.root = ModelTranslator.getPrivacyPreference(this.existingPpnPreference.getRootPreference(), node);
		printTree();
	}
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Set selected node"));
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("setting selected node to null!");
			}
		}else{
			if (logging.isDebugEnabled()){
				this.logging.debug("Setting selected node: "+selectedNode.toString());
			}
		}
		this.selectedNode = selectedNode;
		this.printTree();

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
	public void displaySelectedSingle() {
		if(selectedNode != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());

			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not Selected", "You need to select a node to display");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}

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

	public void addTrustCondition(){
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("selected node is null - addPrivacyCondition");
			}
			return;
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding condition", "wait");

		FacesContext.getCurrentInstance().addMessage(null, message);

		TrustedEntityId trustId = createTrustedEntityId();

		if (trustId==null){
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding trust condition failed", "Failed to create TrustedEntityId");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}


		Double tValue = 1.0;
		try{
			tValue = Double.parseDouble(this.trustValue);
		}catch(NumberFormatException nfe){
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding trust condition failed", "Failed to parse double value for trust");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		TrustPreferenceCondition trustCondition = new TrustPreferenceCondition(trustId, tValue);
		if (selectedNode.getData() instanceof PPNPOutcome){
			if (logging.isDebugEnabled()){
				this.logging.debug("Adding condition to outcome");
			}
			//get the parent of the outcome node
			TreeNode parent = selectedNode.getParent();
			if (logging.isDebugEnabled()){
				this.logging.debug("parent of selected node is: "+parent);
			}
			//remove the outcome from its parent
			parent.getChildren().remove(selectedNode);
			if (logging.isDebugEnabled()){
				this.logging.debug("removed selected node from parent. parent now has "+parent.getChildCount()+" children nodes");
			}
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(trustCondition, parent);
			if (logging.isDebugEnabled()){
				this.logging.debug("added: "+conditionNode+" to parent: "+parent);
			}
			//add the condition node to the parent node
			//parent.getChildren().add(conditionNode);
			//set the condition as parent of the outcome
			selectedNode.setParent(conditionNode);
			if (logging.isDebugEnabled()){
				this.logging.debug("set parent: "+conditionNode+" for selectedNode: "+selectedNode);
			}
			//add the outcome node to the condition node;
			//conditionNode.getChildren().add(selectedNode);
		}else{
			if (logging.isDebugEnabled()){
				this.logging.debug("Adding condition to condition");
			}
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(trustCondition, selectedNode);
			//add the conditionNode under the selected node
			//selectedNode.getChildren().add(conditionNode);
			//set the selected Node to be parent of the new condition node
			conditionNode.setParent(selectedNode);


		}
	}
	private TrustedEntityId createTrustedEntityId() {
		try {
			if (this.preferenceDetails.getRequestor() instanceof RequestorCisBean){

				return new TrustedEntityId(TrustedEntityType.CIS, this.preferenceDetails.getRequestor().getRequestorId());
			}
			if (this.preferenceDetails.getRequestor() instanceof RequestorServiceBean){

				return new TrustedEntityId(TrustedEntityType.SVC, ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) this.preferenceDetails.getRequestor()).getRequestorServiceId()));
			}

			if (this.preferenceDetails.getRequestor() instanceof RequestorBean){
				return new TrustedEntityId(TrustedEntityType.CSS, this.preferenceDetails.getRequestor().getRequestorId());
			}
		} catch (MalformedTrustedEntityIdException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;

	}


	public void addPrivacyCondition(){
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("selected node is null - addPrivacyCondition");
			}
			return;
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding condition", "wait");

		FacesContext.getCurrentInstance().addMessage(null, message);

		Condition condition = new Condition();
		condition.setConditionConstant(selectedPrivacyCondition);
		condition.setValue(selectedPrivacyValue);
		PrivacyCondition privacyCondition = new PrivacyCondition(condition); 

		if (selectedNode.getData() instanceof PPNPOutcome){
			if (logging.isDebugEnabled()){
				this.logging.debug("Adding condition to outcome");
			}
			//get the parent of the outcome node
			TreeNode parent = selectedNode.getParent();
			if (logging.isDebugEnabled()){
				this.logging.debug("parent of selected node is: "+parent);
			}
			//remove the outcome from its parent
			parent.getChildren().remove(selectedNode);
			if (logging.isDebugEnabled()){
				this.logging.debug("removed selected node from parent. parent now has "+parent.getChildCount()+" children nodes");
			}
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(privacyCondition, parent);
			if (logging.isDebugEnabled()){
				this.logging.debug("added: "+conditionNode+" to parent: "+parent);
			}
			//add the condition node to the parent node
			//parent.getChildren().add(conditionNode);
			//set the condition as parent of the outcome
			selectedNode.setParent(conditionNode);
			if (logging.isDebugEnabled()){
				this.logging.debug("set parent: "+conditionNode+" for selectedNode: "+selectedNode);
			}
			//add the outcome node to the condition node;
			//conditionNode.getChildren().add(selectedNode);
		}else{
			if (logging.isDebugEnabled()){
				this.logging.debug("Adding condition to condition");
			}
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(privacyCondition, selectedNode);
			//add the conditionNode under the selected node
			//selectedNode.getChildren().add(conditionNode);
			//set the selected Node to be parent of the new condition node
			conditionNode.setParent(selectedNode);


		}
	}


	public void addOutcome(){
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("selected node is null - addOutcome");
			}
			return;
		}

		if (selectedNode.getData() instanceof PPNPOutcome){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding outcome", "You can't add an outcome as a subnode of another outcome. Please select a condition node to add the new outcome to.");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}

		if (selectedNode.getChildCount()>0){
			List<TreeNode> children = selectedNode.getChildren();
			for (TreeNode child :children){
				if (child.getData() instanceof PPNPOutcome){
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding outcome", "You can't have two outcomes under the same condition. Please delete the existing one and create a new one or edit the existing one.");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return;
				}
			}
		}
		PPNPOutcome outcome = new PPNPOutcome(selectedDecision);

		TreeNode newNode = new DefaultTreeNode(outcome, selectedNode);


		if (logging.isDebugEnabled()){
			this.logging.debug("Added new outcome : "+newNode+" to selected node: "+selectedNode);
		}

	}

	public String formatNodeForDisplay(Object node){
		if (node==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("can't format node. node is null"));
			return "null node";
		}

		if (node instanceof PPNPOutcome){
			PPNPOutcome bean = (PPNPOutcome) node;
			return "Decision: "+ bean.getDecision().value();
		}

		if (node instanceof PrivacyCondition){
			PrivacyCondition privacyCondition = (PrivacyCondition) node;
			return "Condition: "+privacyCondition.getCondition().getConditionConstant()+" = "+privacyCondition.getCondition().getValue();
		}

		if (node instanceof TrustPreferenceCondition){
			TrustPreferenceCondition trustCondition = (TrustPreferenceCondition) node;

			return "Condition: trustOfRequestor > "+trustCondition.getTrustThreshold();
		}
		//TODO: add support for trust condition
		else return "Unparseable: "+node;

	}

	public void editNode(){
		if (selectedNode !=null){
			Object data = selectedNode.getData();
			if (data instanceof PPNPOutcome){

			}
		}
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
			PPNPOutcome outcome = new PPNPOutcome(Decision.PERMIT);


			TreeNode node0 = new DefaultTreeNode(outcome, root);
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion", "Node deleted");
		FacesContext.getCurrentInstance().addMessage(null, message);
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
		PPNPrivacyPreferenceTreeModel model = new PPNPrivacyPreferenceTreeModel(preferenceDetails, privacyPreference);
		if (this.privPrefmgr.storePPNPreference(preferenceDetails, model)){
			message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Success", "Your new Privacy Policy Negotiation preference has been successfully saved.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Failure", "An error occurred while saving your new Privacy Policy Negotiation preference.");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}

	}



	public ICommManager getCommMgr() {
		return commMgr;
	}

	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
		this.userId = this.commMgr.getIdManager().getThisNetworkNode();
	}



	public List<OperatorConstants> getOperators() {
		return operators;
	}

	public void setOperators(List<OperatorConstants> operators) {
		this.operators = operators;
	}

	public OperatorConstants getSelectedOperator() {
		if (selectedOperator==null){
			return OperatorConstants.EQUALS;
		}
		return selectedOperator;
	}

	public void setSelectedOperator(OperatorConstants selectedOperator) {
		this.selectedOperator = selectedOperator;
	}


	public TreeNode getRoot() {
		if (this.root==null){
			this.root = new DefaultTreeNode("Root", null);
			if (logging.isDebugEnabled()){
				this.logging.debug("loading tree with default tree node");
			}
			PPNPOutcome outcome = new PPNPOutcome(Decision.PERMIT);
			TreeNode node0 = new DefaultTreeNode(outcome, root);

			if (logging.isDebugEnabled()){
				this.logging.debug("((((((((((((((");
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("getRoot");
			}
			if (logging.isDebugEnabled()){
				this.logging.debug(node0+" ");
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("))))))))))))))))))");
			}
		}
		return root;
	}



	public void setRoot(TreeNode root) {
		this.root = root;
	}



	public PPNPreferenceDetailsBean getPreferenceDetails() {
		return preferenceDetails;
	}



	public void setPreferenceDetails(PPNPreferenceDetailsBean preferenceDetails) {
		this.preferenceDetails = preferenceDetails;
	}




	public int getRequestorType() {
		return requestorType;
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






	public boolean isPreferenceDetailsCorrect() {
		if (logging.isDebugEnabled()){
			this.logging.debug("preferenceDetailsCorrect: "+preferenceDetailsCorrect);
		}
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



	public String getRequestorCis() {
		return requestorCis;
	}



	public void setRequestorCis(String requestorCis) {
		this.requestorCis = requestorCis;
	}


	public Decision getSelectedDecision() {
		return selectedDecision;
	}


	public void setSelectedDecision(Decision selectedDecision) {
		this.selectedDecision = selectedDecision;
	}


	public List<Decision> getDecisions() {
		return decisions;
	}


	public void setDecisions(List<Decision> decisions) {
		this.decisions = decisions;
	}


	public ConditionConstants getSelectedPrivacyCondition() {
		return selectedPrivacyCondition;
	}


	public void setSelectedPrivacyCondition(ConditionConstants selectedPrivacyCondition) {
		this.selectedPrivacyCondition = selectedPrivacyCondition;
	}


	public Map<ConditionConstants, ConditionConstants> getPrivacyConditions() {
		return privacyConditions;
	}


	public void setPrivacyConditions(Map<ConditionConstants, ConditionConstants> privacyConditions) {
		this.privacyConditions = privacyConditions;
	}


	public String getSelectedPrivacyValue() {
		return selectedPrivacyValue;
	}


	public void setSelectedPrivacyValue(String selectedPrivacyValue) {
		this.selectedPrivacyValue = selectedPrivacyValue;
	}


	public Map<String, String> getPrivacyValues() {
		return privacyValues;
	}

	public void handlePrivacyTypeChange(){
		if (logging.isDebugEnabled()){
			this.logging.debug("handlePrivacyTypeChange for: "+this.selectedPrivacyCondition);
		}
		this.setPrivacyValues(this.privacyConditionData.get(selectedPrivacyCondition));
	}

	public void setPrivacyValues(Map<String, String> privacyValues) {
		this.privacyValues = privacyValues;
	}


	public String getTrustValue() {
		return trustValue;
	}


	public void setTrustValue(String trustValue) {
		this.trustValue = trustValue;
	}


	public IPrivacyPreferenceManager getPrivPrefmgr() {
		return privPrefmgr;
	}


	public void setPrivPrefmgr(IPrivacyPreferenceManager privPrefmgr) {
		this.privPrefmgr = privPrefmgr;
	}


	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}


	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}


	public String getDisplaySpecificRequestor() {
		if (this.preferenceDetails.getRequestor() instanceof RequestorCisBean){
			displaySpecificRequestor = "Cis: "+((RequestorCisBean) this.preferenceDetails.getRequestor()).getCisRequestorId();
		}else if (this.preferenceDetails.getRequestor() instanceof RequestorServiceBean){
			displaySpecificRequestor = "Service: "+ ServiceModelUtils.serviceResourceIdentifierToString(((RequestorServiceBean) this.preferenceDetails.getRequestor()).getRequestorServiceId());
		}else {
			displaySpecificRequestor = "None";
		}
		return displaySpecificRequestor;
	}


	public void setDisplaySpecificRequestor(String displaySpecificRequestor) {
		this.displaySpecificRequestor = displaySpecificRequestor;
	}



	/**
	 * utility methods
	 */

	private void createSchemeList() {
		this.schemeList = new ArrayList<DataIdentifierScheme>();

		this.schemeList.add(DataIdentifierScheme.CONTEXT);
		this.schemeList.add(DataIdentifierScheme.CIS);
		this.schemeList.add(DataIdentifierScheme.DEVICE);
		this.schemeList.add(DataIdentifierScheme.ACTIVITY);


	}

	private void createCtxAttributeTypesList() {
		try{
			this.contextTypes = new ArrayList<String>();
			IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
			Set<CtxAttribute> attributes = individualCtxEntity.getAttributes();

			Iterator<CtxAttribute> iterator = attributes.iterator();
			this.contextTypes.clear();
			while(iterator.hasNext()){

				String ctxType = iterator.next().getType();

				if (!this.contextTypes.contains(ctxType)){
					if (logging.isDebugEnabled()){
						this.logging.debug("Adding new context type: "+ctxType);
					}
					this.contextTypes.add(ctxType);
				}
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("Found "+this.contextTypes.size()+" context attributes");
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void setupDataTypes() {
		this.cisTypes = new ArrayList<String>();
		this.cisTypes.add("cis-member-list");
		this.cisTypes.add("cis-list");

		this.deviceTypes = new ArrayList<String>();
		this.deviceTypes.add("meta-data");

		this.activityTypes = new ArrayList<String>();
		this.activityTypes.add("activityfeed");


	}


	public void handleSchemeTypeChange(){
		DataIdentifierScheme scheme = this.preferenceDetails.getResource().getScheme();
		if (scheme==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("handleSchemeTypeChange: selected scheme is null");
			}
			return;
		}
		selectScheme(scheme);
	}

	private void selectScheme(DataIdentifierScheme scheme){
		switch (scheme)
		{
		case ACTIVITY: 
			this.resourceTypes = this.activityTypes;

			break;
		case CIS: 
			this.resourceTypes = this.cisTypes;

			break;
		case CONTEXT:
			this.resourceTypes = this.contextTypes;

			break;

		case DEVICE:
			this.resourceTypes = this.deviceTypes;

			break;
		default: 
			this.resourceTypes = new ArrayList<String>();
			break;
		}
	}

	public List<String> getContextTypes() {
		return contextTypes;
	}


	public void setContextTypes(List<String> contextTypes) {
		this.contextTypes = contextTypes;
	}


	public List<DataIdentifierScheme> getSchemeList() {
		return schemeList;
	}


	public void setSchemeList(List<DataIdentifierScheme> schemeList) {
		this.schemeList = schemeList;
	}


	public List<String> getCisTypes() {
		return cisTypes;
	}


	public void setCisTypes(List<String> cisTypes) {
		this.cisTypes = cisTypes;
	}


	public List<String> getDeviceTypes() {
		return deviceTypes;
	}


	public void setDeviceTypes(List<String> deviceTypes) {
		this.deviceTypes = deviceTypes;
	}


	public List<String> getActivityTypes() {
		return activityTypes;
	}


	public void setActivityTypes(List<String> activityTypes) {
		this.activityTypes = activityTypes;
	}

	public List<String> getResourceTypes() {
		return resourceTypes;
	}


	public void setResourceTypes(List<String> resourceTypes) {
		this.resourceTypes = resourceTypes;
	}



}
