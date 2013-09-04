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
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.PrivacyPreferenceBean;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;


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
	
	
	public static TreeNode getPrivacyPreference(IPrivacyPreference privacyPreference, TreeNode root){
		TreeNode treeNode;
		
		if (privacyPreference.getUserObject() instanceof IPrivacyOutcome){
			treeNode = new DefaultTreeNode(privacyPreference.getUserObject(), root);
			logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
			return root;
		}
		
		if (privacyPreference.getUserObject() instanceof IPrivacyPreferenceCondition){
			treeNode = new DefaultTreeNode(privacyPreference.getUserObject(), root);
			logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
		}else{
			//assuming this is the root
			treeNode = root;
			logging.debug("This is the root node. Setting: "+treeNode +" as parent node");
		}
		
		Enumeration<PrivacyPreference> children = privacyPreference.children();
		
		while (children.hasMoreElements()){
			
			PrivacyPreference nextElement = children.nextElement();
			getPrivacyPreference(nextElement, treeNode);
		}
		logging.debug("Added subnode: "+treeNode +" to parent node: "+root);
		return treeNode;
		
	}
}
