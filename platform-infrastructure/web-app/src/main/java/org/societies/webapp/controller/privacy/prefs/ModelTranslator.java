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

import java.util.Enumeration;
import java.util.List;

import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceBean;
import org.societies.personalisation.preference.api.model.ContextPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;


/**
 * @author Eliza
 *
 */
public class ModelTranslator {

	private static final Logger logging = LoggerFactory.getLogger(ModelTranslator.class);

	public TreeNode getJSFTreeNode(){
		return null;
	}

	/**
	 * This method translates a primefaces tree to a swing tree (societies). This method assumes the tree has been checked for errors and assumes that all 
	 * objects (data) embedded in each rootNode are either conditions in the form of IPrivacyPreferenceCondition
	 * or outcomes in the form of IPrivacyOutcome  
	 * @param rootNode
	 * @return
	 */
	public static PrivacyPreference getPrivacyPreference(TreeNode rootNode){
		PrivacyPreference privacyPreference;

		if (rootNode.getData() instanceof IPrivacyOutcome){
			privacyPreference = new PrivacyPreference((IPrivacyOutcome) rootNode.getData());
			return privacyPreference;
		}


		if (rootNode.getData() instanceof IPrivacyPreferenceCondition){
			privacyPreference = new PrivacyPreference((IPrivacyPreferenceCondition) rootNode.getData());
		}else{
			privacyPreference = new PrivacyPreference();
		}

		List<TreeNode> children = rootNode.getChildren();
		for (TreeNode child : children){
			privacyPreference.add(getPrivacyPreference(child));

		}
		return privacyPreference;
	}

	public static IPreference getPreference(TreeNode rootNode){
		IPreference preference;

		if (rootNode.getData() instanceof IOutcome){
			preference = new PreferenceTreeNode((IOutcome) rootNode.getData());
			return preference;
		}

		if (rootNode.getData() instanceof IPreferenceCondition){
			preference = new PreferenceTreeNode((IPreferenceCondition) rootNode.getData());
		}else{
			preference = new PreferenceTreeNode();
		}

		List<TreeNode> children = rootNode.getChildren();

		for (TreeNode child: children){
			preference.add(getPreference(child));
		}

		return preference;
	}

	public static TreeNode getPrivacyPreference(IPrivacyPreference privacyPreference, TreeNode root){
		TreeNode treeNode;

		if (privacyPreference.getUserObject() instanceof IPrivacyOutcome){
			treeNode = new DefaultTreeNode(privacyPreference.getUserObject(), root);
			if (logging.isDebugEnabled()){
				logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
			}
			return root;
		}

		if (privacyPreference.getUserObject() instanceof IPrivacyPreferenceCondition){
			treeNode = new DefaultTreeNode(privacyPreference.getUserObject(), root);
			if (logging.isDebugEnabled()){
				logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
			}
		}else{
			//assuming this is the root
			treeNode = root;
			if (logging.isDebugEnabled()){
				logging.debug("This is the root node. Setting: "+treeNode +" as parent node");
			}
		}

		Enumeration<PrivacyPreference> children = privacyPreference.children();

		while (children.hasMoreElements()){

			PrivacyPreference nextElement = children.nextElement();
			getPrivacyPreference(nextElement, treeNode);
		}
		if (logging.isDebugEnabled()){
			logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
		}
		return treeNode;

	}

	public static TreeNode getPreference(IPreference preference, TreeNode root){
		TreeNode treeNode;

		if (preference.getUserObject() instanceof IOutcome){
			treeNode = new DefaultTreeNode(preference.getUserObject(), root);
			if (logging.isDebugEnabled()){
				logging.debug("Added subnode: "+treeNode+" to parent node: "+root);
			}
			return root;
		}

		if (preference.getUserObject() instanceof IPreferenceCondition){
			treeNode = new DefaultTreeNode(preference.getUserObject(), root);
			if (logging.isDebugEnabled()){
				logging.debug("Added subnode: "+treeNode+" to parent node: "+root);
			}
		}else{
			//assuming this is the root
			treeNode = root;
			if (logging.isDebugEnabled()){
				logging.debug("This is the root node. Setting: "+treeNode +" as parent node");
			}
		}

		Enumeration<IPreference> children = preference.children();

		while (children.hasMoreElements()){
			IPreference nextElement = children.nextElement();
			getPreference(nextElement, treeNode);
		}
		if (logging.isDebugEnabled()){
			logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
		}
		return treeNode;
	}


	public static IPreferenceCondition checkPreference(IPreference preference){

		IPreference root = preference.getRoot();

		Enumeration<IPreference> depthFirstEnumeration = root.depthFirstEnumeration();

		while(depthFirstEnumeration.hasMoreElements()){
			IPreference nextElement = depthFirstEnumeration.nextElement();
			if (nextElement.getUserObject() == null){
				if (logging.isDebugEnabled()){
					logging.debug(" +1+1+ element "+nextElement.toString()+"is leaf: "+nextElement.isLeaf()+" and user object is null: ");
				}
			}else{
				if (logging.isDebugEnabled()){
					logging.debug(" +1+1+ element "+nextElement.toString()+"is leaf: "+nextElement.isLeaf()+" and user object is of instance: "+nextElement.getUserObject().getClass().getName());
				}
			}
			
			if (logging.isDebugEnabled()){
				logging.debug("+2+2+" +(nextElement.isLeaf() && (nextElement.getUserObject() instanceof ContextPreferenceCondition)));
			}
			if (nextElement.isLeaf() && (nextElement.getUserObject() instanceof ContextPreferenceCondition)){
				if (logging.isDebugEnabled()){
					logging.debug("Returning erroneous node");
				}
				return nextElement.getCondition();
			}
		}
		if (logging.isDebugEnabled()){
			logging.debug("Returning null");
		}
		return null;
	}

	public static IPrivacyPreferenceCondition checkPreference(IPrivacyPreference preference){
		IPrivacyPreference root = preference.getRoot();

		Enumeration<IPrivacyPreference> depthFirstEnumeration = root.depthFirstEnumeration();

		while(depthFirstEnumeration.hasMoreElements()){
			IPrivacyPreference nextElement = depthFirstEnumeration.nextElement();
			if (nextElement.isLeaf() 
					&& 
					(nextElement.getUserObject() instanceof org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition 
					|| nextElement.getUserObject() instanceof TrustPreferenceCondition 
					|| nextElement.getUserObject() instanceof PrivacyCondition)){
				return nextElement.getCondition();
			}
		}
		return null;
	}

}
