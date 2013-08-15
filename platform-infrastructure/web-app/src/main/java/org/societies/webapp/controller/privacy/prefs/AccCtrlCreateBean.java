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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
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
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPreferenceDetailsBean;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Decision;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ppn.PPNPOutcome;
/**
 * @author Eliza
 *
 */
@SessionScoped
@ManagedBean(name="AccCtrlcreateBean")
public class AccCtrlCreateBean implements Serializable{

	private final Logger logging = LoggerFactory.getLogger(getClass());

	private TreeNode root;

	private TreeNode selectedNode;

	private List<String> ctxIds = new ArrayList<String>();
	private String selectedCtxID = "";

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commMgr;

	private String ctxValue;

	private IIdentity userId;


	Hashtable<String, CtxIdentifier> ctxIDTable = new Hashtable<String, CtxIdentifier>();

	private List<OperatorConstants> operators = new ArrayList<OperatorConstants>();

	private OperatorConstants selectedOperator;

	private PPNPreferenceDetailsBean preferenceDetails = new PPNPreferenceDetailsBean();

	private int requestorType;

	private String requestorService;
	private String requestorCis;

	private boolean preferenceDetailsCorrect = false;

	private Decision selectedDecision;
	private List<Decision> decisions;
	
	public AccCtrlCreateBean() {

	}


	@PostConstruct
	public void setup(){

		preferenceDetails.setRequestor(new RequestorBean());
		preferenceDetails.getRequestor().setRequestorId("");
		preferenceDetails.setResource(new Resource());
		preferenceDetails.getResource().setDataType("");
		setOperators(Arrays.asList(OperatorConstants.values()));
		setDecisions(Arrays.asList(Decision.values()));
	}
	public void savePreferenceDetails(){
		ServiceResourceIdentifier serviceID; 
		String rType = "simple";
		String specific = "";

		if (requestorType==0){

			try {
				this.logging.debug("validating cis ID:"+requestorCis);
				IIdentity cisid = this.commMgr.getIdManager().fromJid(requestorCis);
				((RequestorCisBean) this.preferenceDetails.getRequestor()).setCisRequestorId(cisid.getBareJid());
				rType = "cis";
				specific = "\nid: "+((RequestorCisBean) this.preferenceDetails.getRequestor()).getCisRequestorId();
				this.logging.debug("successfully validated CIS id");
			} catch (InvalidFormatException e) {
				this.logging.debug("caught exception while validating cis id");
				e.printStackTrace();
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("CIS Jid is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}

		}else if (requestorType==1){
			try{
				this.logging.debug("validating service id: "+requestorService);
				serviceID = ServiceModelUtils.generateServiceResourceIdentifierFromString(requestorService);
				rType = "service";
				((RequestorServiceBean) this.preferenceDetails.getRequestor()).setRequestorServiceId(serviceID);
				specific = "\nid: "+((RequestorServiceBean) this.preferenceDetails.getRequestor()).getRequestorServiceId();
				this.logging.debug("successfully validated service id");
			}
			catch (Exception e){
				this.logging.debug("caught exception while generating service resource id");
				e.printStackTrace();
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("ServiceID is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}

			if (serviceID == null){
				this.logging.debug("service id is null");
				preferenceDetailsCorrect = false;
				FacesMessage message = new FacesMessage("ServiceID is not valid");
				FacesContext.getCurrentInstance().addMessage(null, message);
				return;
			}
		}

		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("prefDetailsDlg.hide()");
		this.logging.debug("Successfully validated preferenceDetails");
		this.preferenceDetailsCorrect = true;
		
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "PPN preference details set", "Set requestor: "+preferenceDetails.getRequestor().getRequestorId()+
				"\n, type: "+rType+specific+"\nSet resource: "+preferenceDetails.getResource().getDataType());
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("Set selected node"));
		if (selectedNode==null){
			this.logging.debug("setting selected node to null!");
		}else{
			this.logging.debug("Setting selected node: "+selectedNode.toString());
		}
		this.selectedNode = selectedNode;
		this.printTree();

	}

	private void printTree(){
		if (this.root==null){
			this.logging.debug("root is null. tree is corrupted");
			return;
		}
		this.logging.debug("********** <TREE **********");

		String tree = "\nRoot: "+root.getData()+" "+root.getChildCount();
		List<TreeNode> children = this.root.getChildren();
		for (TreeNode child : children){
			tree = tree.concat(getChildrenToPrint(child));
		}
		this.logging.debug(tree);

		this.logging.debug("******** </TREE> ************");

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
	
	public void addCondition(){
		
		if (selectedNode==null){
			this.logging.debug("selected node is null - addCondition");
			return;
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding condition", "id: "+this.selectedCtxID+", value: "+this.ctxValue);

		FacesContext.getCurrentInstance().addMessage(null, message);

		ContextPreferenceCondition conditionBean = new ContextPreferenceCondition(this.ctxIDTable.get(selectedCtxID), selectedOperator, ctxValue);
		if (selectedNode.getData() instanceof PPNPOutcome){
			this.logging.debug("Adding condition to outcome");
			//get the parent of the outcome node
			TreeNode parent = selectedNode.getParent();
			this.logging.debug("parent of selected node is: "+parent);
			//remove the outcome from its parent
			parent.getChildren().remove(selectedNode);
			this.logging.debug("removed selected node from parent. parent now has "+parent.getChildCount()+" children nodes");
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(conditionBean, parent);
			this.logging.debug("added: "+conditionNode+" to parent: "+parent);
			//add the condition node to the parent node
			//parent.getChildren().add(conditionNode);
			//set the condition as parent of the outcome
			selectedNode.setParent(conditionNode);
			this.logging.debug("set parent: "+conditionNode+" for selectedNode: "+selectedNode);
			//add the outcome node to the condition node;
			//conditionNode.getChildren().add(selectedNode);
		}else{
			this.logging.debug("Adding condition to condition");
			//create the condition node
			TreeNode conditionNode = new DefaultTreeNode(conditionBean, selectedNode);
			//add the conditionNode under the selected node
			//selectedNode.getChildren().add(conditionNode);
			//set the selected Node to be parent of the new condition node
			conditionNode.setParent(selectedNode);
			
			
		}

		this.logging.debug("Added new condition "+conditionBean+" to selected node:"+selectedNode);
		printTree();
	}

	public void addOutcome(){
		if (selectedNode==null){
			this.logging.debug("selected node is null - addOutcome");
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
		
		
		this.logging.debug("Added new outcome : "+newNode+" to selected node: "+selectedNode);

	}
	
	public String formatNodeForDisplay(Object node){
		if (node==null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("can't format node. node is null"));
			return "null node";
		}
		
		if (node instanceof PPNPOutcome){
			PPNPOutcome outcome = (PPNPOutcome) node;
			return "Decision: "+ outcome.getDecision().value();
		}
		
		if (node instanceof ContextPreferenceCondition){
			ContextPreferenceCondition condition = (ContextPreferenceCondition) node;
			return "Condition: "+condition.getCtxIdentifier().getType()+" = "+condition.getValue();
		}
		
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
		this.logging.debug("savePreferences called");
	}
	
	
	public List<String> getCtxIds() {
		try {
			this.logging.debug("Retrieving context attributes to be used as conditions");
			IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
			Set<CtxAttribute> attributes = individualCtxEntity.getAttributes();

			Iterator<CtxAttribute> iterator = attributes.iterator();
			this.ctxIds.clear();

			while(iterator.hasNext()){

				CtxAttributeIdentifier id = iterator.next().getId();
				this.ctxIds.add(id.getUri());
				this.ctxIDTable.put(id.getUri(), id);
			}
			this.logging.debug("Found "+this.ctxIds.size()+" context attributes");

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

		return this.ctxIds;
	}

	public void setCtxIds(List<String> ctxIds) {
		this.ctxIds = ctxIds;
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
		this.userId = this.commMgr.getIdManager().getThisNetworkNode();
	}

	public String getCtxValue() {
		return ctxValue;
	}

	public void setCtxValue(String ctxValue) {
		this.ctxValue = ctxValue;
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

	public String getSelectedCtxID() {
		if (!this.ctxIds.isEmpty()){
			this.selectedCtxID = ctxIds.get(0);
		}
		return selectedCtxID;
	}

	public void setSelectedCtxID(String selectedCtxID) {
		this.selectedCtxID = selectedCtxID;
	}



	public TreeNode getRoot() {
		if (this.root==null){
			this.root = new DefaultTreeNode("Root", null);
			this.logging.debug("loading tree with default tree node");
			PPNPOutcome outcome = new PPNPOutcome(Decision.PERMIT);
			TreeNode node0 = new DefaultTreeNode(outcome, root);
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
		switch (requestorType){
		case 0:
			RequestorCisBean cisBean = new RequestorCisBean();
			cisBean.setRequestorId(requestorId);
			this.preferenceDetails.setRequestor(cisBean);
			this.logging.debug("setting requestor Type :"+requestorType);
			break;
		case 1:
			RequestorServiceBean serviceBean = new RequestorServiceBean();
			serviceBean.setRequestorId(requestorId);
			this.preferenceDetails.setRequestor(serviceBean);
			this.logging.debug("setting requestor Type :"+requestorType);
			break;
		default:
			RequestorBean bean = new RequestorBean();
			bean.setRequestorId(requestorId);
			this.preferenceDetails.setRequestor(bean);
			this.logging.debug("setting requestor Type :"+requestorType);
		}
		this.requestorType = requestorType;
	}






	public boolean isPreferenceDetailsCorrect() {
		this.logging.debug("preferenceDetailsCorrect: "+preferenceDetailsCorrect);
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



}
