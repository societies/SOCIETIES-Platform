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
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PPNPOutcomeBean;
/**
 * @author Eliza
 *
 */
@ViewScoped
@ManagedBean(name="PPNcreateBean")
public class PPNCreateBean implements Serializable{
	
	private final Logger logging = LoggerFactory.getLogger(getClass());
	
	private TreeNode root;

	private TreeNode selectedNode;

	private List<String> ctxIds = new ArrayList<String>();
	private String ctxId = "enter a value";
	
	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;
	
	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commMgr;
	
	private String ctxValue;
	
	private IIdentity userId;
	
	public PPNCreateBean() {
		root = new DefaultTreeNode("Root", null);
		TreeNode node0 = new DefaultTreeNode("UnderRoot", root);
	}

	public TreeNode getRoot() {
		return root;
	}

	public TreeNode getSelectedNode() {
		return selectedNode;
	}

	public void setSelectedNode(TreeNode selectedNode) {
		this.selectedNode = selectedNode;
	}
	
	public void displaySelectedSingle() {
        if(selectedNode != null) {
            FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", selectedNode.getData().toString());

            FacesContext.getCurrentInstance().addMessage(null, message);
        }
	}

	public void createCondition(){
		
	}
	
	
	public void addCondition(){
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, "Selected", "id: "+this.ctxId+", value: "+this.ctxValue);

        FacesContext.getCurrentInstance().addMessage(null, message);
	}
	public void addOutcome(){
		
		
		
	}
	
	public void editNode(){
		if (selectedNode !=null){
			Object data = selectedNode.getData();
			if (data instanceof PPNPOutcomeBean){
				
			}
		}
	}
	
	
    public void deleteNode() {
    	if (selectedNode.getParent()==null){
    		FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, "Deleting root node", "You can't delete the root node. If you want to cancel, this use the back button");
    		FacesContext.getCurrentInstance().addMessage(null, message);
    		return;
    	}
        selectedNode.getChildren().clear();
        selectedNode.getParent().getChildren().remove(selectedNode);
        selectedNode.setParent(null);
        
        selectedNode = null;
    }

	public List<String> getCtxIds() {
		try {
			this.logging.debug("Retrieving context attributes to be used as conditions");
			IndividualCtxEntity individualCtxEntity = this.ctxBroker.retrieveIndividualEntity(userId).get();
		    Set<CtxAttribute> attributes = individualCtxEntity.getAttributes();
		    
		    Iterator<CtxAttribute> iterator = attributes.iterator();
		    this.ctxIds.clear();
		    
		    while(iterator.hasNext()){
		    	this.ctxIds.add(iterator.next().getId().getUri());
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

	public String getCtxId() {
		if (!this.ctxIds.isEmpty()){
			this.ctxId = ctxIds.get(0);
		}
		return ctxId;
	}

	public void setCtxId(String ctxId) {
		this.ctxId = ctxId;
	}
	
}
