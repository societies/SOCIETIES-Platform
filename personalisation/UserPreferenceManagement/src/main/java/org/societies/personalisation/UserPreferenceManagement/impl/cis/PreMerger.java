/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
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
package org.societies.personalisation.UserPreferenceManagement.impl.cis;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.OperatorConstants;
import org.societies.personalisation.preference.api.model.PreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;

/**
 * @author Eliza
 *
 */
public class PreMerger {

	private final ICtxBroker broker;
	private SimpleTree sTree;
	private final IIdentity userId;
	private IndividualCtxEntity individualCtxEntity;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public PreMerger(ICtxBroker broker, IIdentity userId){
		this.broker = broker;
		this.userId = userId;
		try {
			individualCtxEntity = this.broker.retrieveIndividualEntity(userId).get();
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
	
	public SimpleTree showTree(PreferenceTreeNode node){
		return new SimpleTree(node);
	}

	public IPreference replaceCtxIdentifiers(IPreference node){
		Enumeration<IPreference> depthFirstEnumeration = node.depthFirstEnumeration();

		List<IPreference> toBeRemoved = new ArrayList<IPreference>();
		
		int i = 0;
		while (depthFirstEnumeration.hasMoreElements()){
			
			
			IPreference currentNode = depthFirstEnumeration.nextElement();
			//if(this.logging.isDebugEnabled()){this.logging.debug("Iterating "+i+" processing: "+currentNode.getUserObject().toString());}
			i++;
			if (currentNode.getUserObject()!=null){
				if (currentNode.getUserObject() instanceof ContextPreferenceCondition){
					
					ContextPreferenceCondition condition = (ContextPreferenceCondition) currentNode.getCondition();
					if(this.logging.isDebugEnabled()){
						this.logging.debug("nextElement => ContextPreferenceCondition: "+condition.getname());
					}
					CtxAttributeIdentifier ctxIdentifier = condition.getCtxIdentifier();
					String type = "";
					if (ctxIdentifier==null){
						String ctxType = condition.getname();
						if (ctxType == null){
							if (currentNode.getParent()==null){
								return null;
							}
							toBeRemoved.add(currentNode);
							if(this.logging.isDebugEnabled()){
								this.logging.debug("removed");
							}
						}else{
							try {
								List<CtxIdentifier> list = this.broker.lookup(individualCtxEntity.getId(), CtxModelType.ATTRIBUTE, ctxType).get();
								if (list.size()==0){
									
									toBeRemoved.add(currentNode);
								}else{
									CtxAttributeIdentifier ctxIdtoReplace = (CtxAttributeIdentifier) list.get(0);
									condition.setCtxIdentifier(ctxIdtoReplace);
								}
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								toBeRemoved.add(currentNode);
							} catch (ExecutionException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								toBeRemoved.add(currentNode);
							} catch (CtxException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								toBeRemoved.add(currentNode);
							}
							
						}
					}else{
						String ctxType = ctxIdentifier.getType();
						try {
							
							List<CtxIdentifier> list = this.broker.lookup(individualCtxEntity.getId(), CtxModelType.ATTRIBUTE, ctxType).get();
							if(this.logging.isDebugEnabled()){
								this.logging.debug("retrieved: "+list.size()+" "+ctxType+" attributes");
							}
							if (list.size()==0){
								
								toBeRemoved.add(currentNode);
							}else{
								CtxAttributeIdentifier ctxIdtoReplace = (CtxAttributeIdentifier) list.get(0);
								condition.setCtxIdentifier(ctxIdtoReplace);
							}
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							toBeRemoved.add(currentNode);
						} catch (ExecutionException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							toBeRemoved.add(currentNode);
						} catch (CtxException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
							toBeRemoved.add(currentNode);
						}
						if(this.logging.isDebugEnabled()){
							this.logging.debug("ctxId not null");
						}
					}
				}
			}

		}

		for (IPreference p : toBeRemoved){
			p.removeFromParent();
		}
		return removeInvalidBranches(node);
		
	}

	private IPreference removeInvalidBranches(IPreference root){
		Enumeration<IPreference> depthFirstEnumeration = root.depthFirstEnumeration();
		ArrayList<PreferenceTreeNode> leaves = new ArrayList<PreferenceTreeNode>();
		while(depthFirstEnumeration.hasMoreElements()){
			PreferenceTreeNode currentNode = (PreferenceTreeNode) depthFirstEnumeration.nextElement();
			if (currentNode.getChildCount()==0){
				leaves.add(currentNode);
				if(this.logging.isDebugEnabled()){
					this.logging.debug("adding leaf "+currentNode.toTreeString());
				}
			}
		}
		

		for (PreferenceTreeNode node : leaves){
			if (node.getUserObject()==null || node.getUserObject() instanceof ContextPreferenceCondition){
				PreferenceTreeNode parent = (PreferenceTreeNode) node.getParent();
				if (parent==null){
					return null;
				}
				PreferenceTreeNode temp = parent;
				while(parent.getChildCount()==1){
					temp = parent;
					parent = (PreferenceTreeNode) parent.getParent();
				}
				temp.removeFromParent();
			}
		}
		return root;
	}
	

	class SimpleTree extends JFrame {

	  public SimpleTree(PreferenceTreeNode node) {
		    super("Creating a Simple JTree");
		    
		    
		    Container content = getContentPane();
		    
		    JTree tree = new JTree(node);
		    content.add(new JScrollPane(tree), BorderLayout.CENTER);
		    setSize(275, 300);
		    setVisible(true);
		  }
	}
	
/*	public static void main(String[] args){
		
		PreferenceTreeNode root = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "work", CtxAttributeTypes.LOCATION_COORDINATES));
		PreferenceTreeNode nodeNullCondition = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "home", null));
		nodeNullCondition.add(new PreferenceTreeNode(new PreferenceOutcome(null, "", "", "")));
		root.add(nodeNullCondition);
		
		PreferenceTreeNode nodeNonNullCondition = new PreferenceTreeNode(new ContextPreferenceCondition(null, OperatorConstants.EQUALS, "something", CtxAttributeTypes.STATUS));
		nodeNonNullCondition.add(new PreferenceTreeNode(new PreferenceOutcome(null, "", "", "")));
		root.add(nodeNonNullCondition);
		
		PreMerger premerger = new PreMerger(null, null);
		SimpleTree sTree = premerger.showTree(root);
				PreferenceTreeNode root = new PreferenceTreeNode();
		root.add(node);
		PreferenceTreeNode replacedNode = premerger.replaceCtxIdentifiers(root);
		if (replacedNode==null){
			System.out.println("returned node is null");
		}else{
			System.out.println(replacedNode.toTreeString());
			SimpleTree sTree2 = premerger.showTree(root);
			
		}

		
	}*/
}
