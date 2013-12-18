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

import java.lang.reflect.Field;
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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.personalisation.model.PreferenceDetails;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.ServiceDiscoveryException;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.IUserPreferenceManagement;
import org.societies.personalisation.preference.api.UserPreferenceConditionMonitor.IUserPreferenceConditionMonitor;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeModel;
import org.societies.webapp.controller.BasePageController;
import org.societies.webapp.controller.privacy.prefs.ModelTranslator;

/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PreferenceCreateBean")
public class PreferenceCreateBean extends BasePageController {

	private final Logger logging = LoggerFactory.getLogger(getClass());
	
	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;
	
	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commMgr;
	
	@ManagedProperty(value="#{serviceDiscovery}")
	private IServiceDiscovery serviceDiscovery;

    @ManagedProperty(value = "#{userPreferenceConditionMonitor}")
    private IUserPreferenceConditionMonitor userPreferenceConditionMonitor;



	private IUserPreferenceManagement preferenceManager;
	private PreferenceDetails preferenceDetails = new PreferenceDetails();
	private String serviceIDStr;
	
	private TreeNode selectedNode;
	private TreeNode root;
	
	private String selectedCtxID = "";
	private List<String> ctxIds = new ArrayList<String>();
	private String ctxValue;
	
	Hashtable<String, CtxAttributeIdentifier> ctxIDTable = new Hashtable<String, CtxAttributeIdentifier>();
	
	private OperatorConstants selectedCtxOperator;
	private List<OperatorConstants> operators = new ArrayList<OperatorConstants>();
	
	private List<String> existingActionValues  = new ArrayList<String>();
	private String selectedAction = "";

	private List<String> contextTypes;

	private IIdentity userId;

	private boolean proactive;
	
	private String defaultNodeValue;


	private List<ServiceResourceIdentifier> availableServices = new ArrayList<ServiceResourceIdentifier>();

	private IPreferenceCondition erroneousNode;
	@PostConstruct
	public void setup(){
		this.logging.debug("#CODE2#: Initiating PreferenceCreate controller");
		root = new DefaultTreeNode();
		createCtxAttributeTypesList();
		setOperators(Arrays.asList(OperatorConstants.values()));
		setupCtxIds();
		
	}
	
	/**
	 * only called once in the beginning
	 */
	public void setDefaultNode(){
		if (this.defaultNodeValue == null){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You need to enter a value"));
			return;
		}
		if (this.defaultNodeValue.trim().equalsIgnoreCase("")){
			FacesContext.getCurrentInstance().addMessage(null, new FacesMessage("You need to enter a value"));
			return;
		}
		PreferenceOutcome outcome = new PreferenceOutcome(this.preferenceDetails.getServiceID(), 
				this.preferenceDetails.getServiceType(), 
				this.preferenceDetails.getPreferenceName(), 
				this.defaultNodeValue, 
				true, true, proactive);
		TreeNode node = new DefaultTreeNode(outcome, root);
		
		RequestContext.getCurrentInstance().execute("outcomeDlg.hide()");
		printTree();
	}
	private void createCtxAttributeTypesList() {
		this.contextTypes = new ArrayList<String>();
		Field[] fields = CtxAttributeTypes.class.getDeclaredFields();
		
		String[] names = new String[fields.length];
		
		for (int i=0; i<names.length; i++){
			names[i] = fields[i].getName();
			
			
		}
		this.contextTypes = Arrays.asList(names);
		
	}
	
	
	public void setupCtxIds() {
		try {
			if (logging.isDebugEnabled()){
				this.logging.debug("Retrieving context attributes to be used as conditions");
			}
			IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
			Set<CtxAttribute> attributes = individualCtxEntity.getAttributes();

			Iterator<CtxAttribute> iterator = attributes.iterator();
			this.ctxIds.clear();

			while(iterator.hasNext()){

				CtxAttributeIdentifier id = iterator.next().getId();
				this.ctxIds.add(id.getUri());
				this.ctxIDTable.put(id.getUri(), id);
			}
			if (logging.isDebugEnabled()){
				this.logging.debug("Found "+this.ctxIds.size()+" context attributes");
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
	/*
	 * METHODS CALLED FROM BUTTONS
	 */
	public void savePreferenceDetails(){
		
		
		if (this.preferenceDetails==null){
			FacesMessage message = new FacesMessage("Please enter at least a preferenceName");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		if (this.preferenceDetails.getPreferenceName()==null){
			FacesMessage message = new FacesMessage("Please enter at least a preferenceName");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		if (this.preferenceDetails.getPreferenceName().trim().equalsIgnoreCase("")){
			FacesMessage message = new FacesMessage("Please enter at least a preferenceName");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		if (serviceIDStr!=null){
			if (!serviceIDStr.trim().equalsIgnoreCase("")){
				try{
				ServiceResourceIdentifier temp = ServiceModelUtils.generateServiceResourceIdentifierFromString(serviceIDStr);
				this.preferenceDetails.setServiceID(temp);
				}catch(Exception e){
					FacesMessage message = new FacesMessage("The service identifier you entered is invalid. Either remove it or enter a valid service identifier");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return;
				}
				
				
			}
		}
		RequestContext context = RequestContext.getCurrentInstance();
		context.execute("prefDetailsDlg.hide()");
		context.execute("outcomeDlg.show()");
		if (logging.isDebugEnabled()){
			this.logging.debug("Successfully validated preferenceDetails");
		}
		
		
	}
	
	
	
	public void displaySelectedSingle(){
		if(selectedNode != null) {
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());

			FacesContext.getCurrentInstance().addMessage(null, message);
		}else{
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Not Selected", "You need to select a node to display");
			FacesContext.getCurrentInstance().addMessage(null, message);
		}
	}
	
	public void deleteNode(){
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
			PreferenceOutcome outcome = new PreferenceOutcome(this.preferenceDetails.getServiceID(), 
					this.preferenceDetails.getServiceType(), 
					this.preferenceDetails.getPreferenceName(), 
					this.defaultNodeValue, 
					true, true, proactive);
			TreeNode node = new DefaultTreeNode(outcome, root);
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Deletion", "Node deleted");
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
	public void startAddConditionProcess(){
		RequestContext.getCurrentInstance().execute("addConddlg.show();");
	}
	public void addCondition(){
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("selected node is null - addCondition");
			}
			return;
		}
		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Adding condition", "id: "+this.selectedCtxID+", value: "+this.ctxValue);

		FacesContext.getCurrentInstance().addMessage(null, message);

		ContextPreferenceCondition conditionBean = new ContextPreferenceCondition(this.ctxIDTable.get(selectedCtxID), selectedCtxOperator, ctxValue, "");
		if (selectedNode.getData() instanceof IOutcome){
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
			TreeNode conditionNode = new DefaultTreeNode(conditionBean, parent);
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
			TreeNode conditionNode = new DefaultTreeNode(conditionBean, selectedNode);
			//add the conditionNode under the selected node
			//selectedNode.getChildren().add(conditionNode);
			//set the selected Node to be parent of the new condition node
			conditionNode.setParent(selectedNode);
			
			
		}

		
		if (logging.isDebugEnabled()){
			this.logging.debug("Added new condition "+conditionBean+" to selected node:"+selectedNode);
		}
		printTree();
	}
	
	public void startAddOutcomeProcess(){
		if (this.existingActionValues.size()==0){
			RequestContext.getCurrentInstance().execute("addOutdlg2.show();");
		}else{
			RequestContext.getCurrentInstance().execute("addOutdlg1.show();");
		}
	}
	public void addOutcome(){
		if (selectedNode==null){
			if (logging.isDebugEnabled()){
				this.logging.debug("selected node is null - addOutcome");
			}
			return;
		}
		
		if (selectedNode.getData() instanceof IOutcome){
			FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding outcome", "You can't add an outcome as a subnode of another outcome. Please select a condition node to add the new outcome to.");
			FacesContext.getCurrentInstance().addMessage(null, message);
			return;
		}
		
		if (selectedNode.getChildCount()>0){
			List<TreeNode> children = selectedNode.getChildren();
			for (TreeNode child :children){
				if (child.getData() instanceof IOutcome){
					FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, "Adding outcome", "You can't have two outcomes under the same condition. Please delete the existing one and create a new one or edit the existing one.");
					FacesContext.getCurrentInstance().addMessage(null, message);
					return;
				}
			}
		}
		IOutcome outcome = new PreferenceOutcome(this.preferenceDetails.getServiceID(), 
				this.preferenceDetails.getServiceType(), 
				this.preferenceDetails.getPreferenceName(), 
				this.selectedAction, 
				true, 
				true, 
				proactive);
		
			
			TreeNode newNode = new DefaultTreeNode(outcome, selectedNode);
		
		
			if (logging.isDebugEnabled()){
				this.logging.debug("Added new outcome : "+newNode+" to selected node: "+selectedNode);
			}
	}
	public void editNode(){
		
	}
	
	public String formatNodeForDisplay(Object node){
		
		if (node==null){
			return "";
		}
		
		if (node instanceof IOutcome){
			return ((IOutcome) node).getparameterName()+" = " + ((IOutcome) node).getvalue();
		}
		
		if (node instanceof ContextPreferenceCondition){
			ContextPreferenceCondition condition = (ContextPreferenceCondition) node;
			return "Condition: "+condition.getCtxIdentifier().getType()+" = "+condition.getvalue();
		}
		return "";
		
	}
	
	public void savePreference(){
		FacesMessage fMessage = new FacesMessage(FacesMessage.SEVERITY_INFO, "Saving preference", "Now saving prefernece");
		FacesContext.getCurrentInstance().addMessage(null, fMessage);
		if (logging.isDebugEnabled()){
			this.logging.debug("savePreferences called");
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Before translating the preference :");
		}
		printTree();
		IPreference preference = ModelTranslator.getPreference(root);
		erroneousNode = ModelTranslator.checkPreference(preference);
		if (erroneousNode!=null){
			RequestContext.getCurrentInstance().execute("msgFailureDlg1.show();");
			return;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Printing preference before save: \n"+preference.toString());
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Saving preferences with details: "+preferenceDetails.toString());
		}
		
		if (logging.isDebugEnabled()){
			this.logging.debug("Saving preference to preference manager: "+preference.toTreeString());
		}
		if (this.preferenceManager.storePreference(userId, preferenceDetails, preference)){
			
			RequestContext.getCurrentInstance().execute("msgSuccessDlg.show();");
			
		}else{
			RequestContext.getCurrentInstance().execute("msgFailureDlg.show();");
		}
	}
	
	/*
	 * 
	 * UTIL METHODS
	 */
	

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
	
	
	
	
	
	
	
	
	/*
	 * GET/SET METHODS 
	 *
	 */
	public PreferenceDetails getPreferenceDetails() {
		return preferenceDetails;
	}

	public void setPreferenceDetails(PreferenceDetails preferenceDetails) {
		this.preferenceDetails = preferenceDetails;
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

	public String getSelectedCtxID() {
		return selectedCtxID;
	}

	public void setSelectedCtxID(String selectedCtxID) {
		this.selectedCtxID = selectedCtxID;
	}

	public List<String> getCtxIds() {
		return ctxIds;
	}

	public void setCtxIds(List<String> ctxIds) {
		this.ctxIds = ctxIds;
	}

	public OperatorConstants getSelectedCtxOperator() {
		return selectedCtxOperator;
	}

	public void setSelectedCtxOperator(OperatorConstants selectedCtxOperator) {
		this.selectedCtxOperator = selectedCtxOperator;
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

	public List<String> getExistingActionValues() {
		return existingActionValues;
	}

	public void setExistingActionValues(List<String> existingActionValues) {
		this.existingActionValues = existingActionValues;
	}

	public String getSelectedAction() {
		return selectedAction;
	}

	public void setSelectedAction(String selectedAction) {
		this.selectedAction = selectedAction;
	}

	public List<String> getContextTypes() {
		return contextTypes;
	}

	public void setContextTypes(List<String> contextTypes) {
		this.contextTypes = contextTypes;
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

	public boolean isProactive() {
		return proactive;
	}

	public void setProactive(boolean proactive) {
		this.proactive = proactive;
	}

	public String getServiceIDStr() {
		return serviceIDStr;
	}

	public void setServiceIDStr(String serviceIDStr) {
		this.serviceIDStr = serviceIDStr;
	}

	public IServiceDiscovery getServiceDiscovery() {
		return serviceDiscovery;
	}

	public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
		this.serviceDiscovery = serviceDiscovery;
	}

	public List<ServiceResourceIdentifier> getAvailableServices() {
		try {
			List<Service> services = this.serviceDiscovery.getServices(userId).get();
			for (Service service : services){
				this.availableServices.add(service.getServiceIdentifier());
			}
		} catch (ServiceDiscoveryException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return availableServices;
	}

	public void setAvailableServices(List<ServiceResourceIdentifier> availableServices) {
		this.availableServices = availableServices;
	}

	public String getDefaultNodeValue() {
		return defaultNodeValue;
	}

	public void setDefaultNodeValue(String defaultNodeValue) {
		this.defaultNodeValue = defaultNodeValue;
		if (logging.isDebugEnabled()){
			this.logging.debug("Setting default node value: "+this.defaultNodeValue);
		}
	}

	public IUserPreferenceConditionMonitor getUserPreferenceConditionMonitor() {
		return userPreferenceConditionMonitor;
	}

	public void setUserPreferenceConditionMonitor(
			IUserPreferenceConditionMonitor userPreferenceConditionMonitor) {
		this.userPreferenceConditionMonitor = userPreferenceConditionMonitor;
		this.preferenceManager = this.userPreferenceConditionMonitor.getPreferenceManager();
	}

	public IPreferenceCondition getErroneousNode() {
		return erroneousNode;
	}

	public void setErroneousNode(IPreferenceCondition erroneousNode) {
		this.erroneousNode = erroneousNode;
	}




	
	
}
