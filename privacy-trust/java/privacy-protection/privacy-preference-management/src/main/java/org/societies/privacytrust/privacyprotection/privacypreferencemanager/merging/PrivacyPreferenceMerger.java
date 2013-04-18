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


package org.societies.privacytrust.privacyprotection.privacypreferencemanager.merging;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.concurrent.ExecutionException;

import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.AccessControlPreferenceDetailsBean;
import org.societies.api.internal.schema.privacytrust.privacyprotection.preferences.IDSPreferenceDetailsBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;

import org.societies.privacytrust.privacyprotection.api.model.privacypreference.PrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.accesscontrol.AccessControlOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IDSPrivacyPreferenceTreeModel;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ids.IdentitySelectionPreferenceOutcome;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.CtxTypes;
import org.societies.privacytrust.privacyprotection.privacypreferencemanager.PrivacyPreferenceManager;


public class PrivacyPreferenceMerger {

	private ICtxBroker broker;
	private PrivacyPreferenceManager ppMgr;
	private IIdentityManager idMgr;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public PrivacyPreferenceMerger(ICtxBroker broker, PrivacyPreferenceManager ppMgr){
		this.broker = broker;
		this.ppMgr = ppMgr;
		this.idMgr = ppMgr.getIdm();

	}

	public void addIDSDecision(IIdentity selectedDPI, RequestorBean requestor){
		ContextSnapshot snapshot = this.takeSnapshot();
		IDSPreferenceDetailsBean details = new IDSPreferenceDetailsBean();
		details.setAffectedIdentity(selectedDPI.getJid());
		details.setRequestor(requestor);
		IPrivacyPreferenceTreeModel existingModel = ppMgr.getIDSPreference(details);
		if (existingModel==null){
			IDSPrivacyPreferenceTreeModel model;
			try {
			
				model = new IDSPrivacyPreferenceTreeModel(details, this.createIDSPreference(snapshot, details));
				this.ppMgr.storeIDSPreference(details, model);
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}else{
			IPrivacyPreference mergedPreference;
			try {
				mergedPreference = this.mergeIDSPreference(details, existingModel.getRootPreference(), snapshot);
				if (mergedPreference!=null){
					IDSPrivacyPreferenceTreeModel model = new IDSPrivacyPreferenceTreeModel(details, mergedPreference);
					this.ppMgr.storeIDSPreference(details, model);
				}
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}



	public IPrivacyPreference mergeIDSPreference(IDSPreferenceDetailsBean d, IPrivacyPreference node, ContextSnapshot snapshot) throws InvalidFormatException{


		if (node.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			IPrivacyPreference p = new PrivacyPreference();
			p.add(this.createIDSPreference(snapshot, d));
			p = p.getRoot();
			p.add(node);
			return p;
		}


		ArrayList<SingleRule> singleRules = this.convertToSingleRules(snapshot);


		IPrivacyPreference mergedTree = node;
		for (int i = 0; i< singleRules.size(); i++){

			SingleRule sr = singleRules.get(i);
			//logging.debug("Merging new Single Rule: "+sr.toString());
			//logging.debug("\twith: "+mergedTree.toTreeString());
			IPrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp; //in the MergingManager if this method returns null, it means we have to request a full learning cycle

		}

		return mergedTree;
	}

	public IPrivacyPreference mergeAccCtrlPreference(AccessControlPreferenceDetailsBean d, IPrivacyPreference existingPreference, IPrivacyPreference newPreference){
		if (existingPreference.isLeaf()){
			this.logging.debug("existing node does not contain context condition. merging as leaf");
			newPreference = newPreference.getRoot();
			IPrivacyPreference p = new PrivacyPreference();
			p.add(newPreference);
			p.add(existingPreference);
			return p;
		}
		
		ArrayList<SingleRule> newSingleRules = this.convertToSingleRules(newPreference);
		
		IPrivacyPreference mergedTree = existingPreference;
		
		for (SingleRule sr : newSingleRules){
			IPrivacyPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp;
		}
		return mergedTree;
	}



	private IPrivacyPreference merge(IPrivacyPreference oldTree, SingleRule sr){
		//IPreference newTree = null;
		ArrayList<SingleRule> oldRules = this.convertToSingleRules(oldTree);

		//check if we're in Situation 1 (same conditions different outcomes)
		ArrayList<SingleRule> temp = this.checkConflicts(oldRules, sr);
		if (temp.size()>0){
			return null;
		}
		this.logging.debug("Not in situation 1");

		//check if we're in Situation 2 (100% match)
		temp = this.checkMatches(oldRules, sr);
		if (temp.size()>0){
			//return createTree(temp);
			return oldTree;
		}
		this.logging.debug("Not in Situation 2");

		//we're going to find a branch that has the most common conditions with this rule.
		IPrivacyPreference commonNode = this.findCommonNode(oldTree, sr);



		if (null==commonNode){
			IPrivacyPreference root = (IPrivacyPreference) oldTree.getRoot();
			if (null==root.getUserObject()){
				return this.addToNode((IPrivacyPreference) oldTree.getRoot(),sr);
			}
			IPrivacyPreference newRoot = new PrivacyPreference();
			newRoot.add(root);
			return this.addToNode(newRoot, sr);
		}

		return this.addToNode(commonNode, sr);

		//ArrayList<SingleRule> sortedRules = sortTree(oldRules);
		//newTree = createTree(sortedRules);
		//return newTree;


	}

	private ArrayList<SingleRule> checkMatches(ArrayList<SingleRule> oldRules, SingleRule newRule){

		for (int i=0; i< oldRules.size(); i++){

			SingleRule sr = oldRules.get(i);
			if (sr.equals(newRule)){
				oldRules.set(i, this.increaseConfidenceLevel(sr));
				return oldRules;
			}
		}
		return new ArrayList<SingleRule>();
	}

	private SingleRule increaseConfidenceLevel(SingleRule sr){
		//need to increase the confidence level by running the algorithm
		return sr;
	}





	private IPrivacyPreference findCommonNode(IPrivacyPreference ptn, SingleRule sr){

		CommonNodeCounter cnc = new CommonNodeCounter();

		//if it's an empty root, we have to repeat with all its children
		if (ptn.getUserObject() == null){
			this.logging.debug("current node is empty root");
			Enumeration<IPrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				IPrivacyPreference p = e.nextElement();
				this.logging.debug("processing child :"+p.toString()+" which is child of: "+ptn.toString());
				cnc = findCommonNode(p,sr, cnc);
			}
		}else{

			cnc = findCommonNode(ptn,sr,cnc);
		}

		return cnc.getMostCommonNode();
	}

	private CommonNodeCounter findCommonNode(IPrivacyPreference ptn, SingleRule sr, CommonNodeCounter cnc){

		//unlikely
		if (ptn.isLeaf()){
			this.logging.debug("current node is leaf. returning common node counter");
			return cnc;
		}

		IPrivacyPreferenceCondition pc = (IPrivacyPreferenceCondition) ptn.getUserObject();
		//if they have a common condition, go to the children, otherwise, return and continue with siblings
		if (sr.hasCondition(pc)){
			this.logging.debug("Single rule: "+sr.toString()+" has common node: "+pc.toString());
			cnc.add(ptn, ptn.getLevel());
			Enumeration<IPrivacyPreference> e = ptn.children();
			while (e.hasMoreElements()){
				cnc = findCommonNode(e.nextElement(),sr,cnc);
			}
		}
		return cnc;
	}


	private ArrayList<SingleRule> checkConflicts(ArrayList<SingleRule> oldRules, SingleRule newRule){

		for (int i=0; i< oldRules.size(); i++){
			SingleRule sr = oldRules.get(i);
			if (sr.conflicts(newRule)){
				oldRules.set(i, this.resolveConflict(sr, newRule));
				return oldRules;
			}

		}

		return new ArrayList<SingleRule>();
	}
	private SingleRule resolveConflict(SingleRule oldRule, SingleRule newRule){
		//resolve
		return oldRule;
	}

	public ArrayList<SingleRule> convertToSingleRules(IPrivacyPreference ptn){
		ArrayList<SingleRule> singleRules = new ArrayList<SingleRule>();
		//Enumeration<IPreference> newNodeEnum = ptn.depthFirstEnumeration();
		Enumeration<IPrivacyPreference> newNodeEnum = ptn.preorderEnumeration();
		//we're going to construct SingleRule objects from the new tree to use as input to merge with the old tree
		while (newNodeEnum.hasMoreElements()){
			IPrivacyPreference temp = (IPrivacyPreference) newNodeEnum.nextElement();
			if (temp.isLeaf()){
				Object[] userObjs = temp.getUserObjectPath();
				SingleRule sr = new SingleRule();
				for (int i=0; i<userObjs.length; i++){
					if (userObjs!=null){
						if (userObjs[i] instanceof IPrivacyPreferenceCondition){
							sr.addConditions((IPrivacyPreferenceCondition) userObjs[i]); 
						}else {
							sr.setOutcome((IPrivacyOutcome) userObjs[i]);
						}
					}
				}
				singleRules.add(sr);
			}

		}	

		for (int i=0; i<singleRules.size(); i++){
			logging.debug("::"+singleRules.get(i).toString());
		}
		return singleRules;
	}

	public ArrayList<SingleRule> convertToSingleRules(ContextSnapshot snapshot){
		ArrayList<SingleRule> srlist = new ArrayList<SingleRule>();
		List<SingleContextAttributeSnapshot> slist = snapshot.getList();
		SingleRule sr = new SingleRule();

		for (SingleContextAttributeSnapshot s : slist){
			IPrivacyPreferenceCondition con = this.getContextConditionPreference(s);
			sr.addConditions(con);
		}
		srlist.add(sr);
		return srlist;
	}
	private IPrivacyPreference createIDSPreference(ContextSnapshot snapshot, IDSPreferenceDetailsBean details) throws InvalidFormatException{
		IdentitySelectionPreferenceOutcome outcome = new IdentitySelectionPreferenceOutcome(this.idMgr.fromJid(details.getAffectedIdentity()));
		IPrivacyPreference p = new PrivacyPreference(outcome);
		List<SingleContextAttributeSnapshot> list = snapshot.getList();
		for (SingleContextAttributeSnapshot s : list){
			IPrivacyPreference temp = new PrivacyPreference(this.getContextConditionPreference(s));
			temp.add(p);
			p = temp;

		}
		return p;
	}


	private IPrivacyPreferenceCondition getContextConditionPreference(SingleContextAttributeSnapshot attrSnapshot){
		ContextPreferenceCondition condition = new ContextPreferenceCondition(attrSnapshot.getId(),OperatorConstants.EQUALS, attrSnapshot.getValue());
		//IPrivacyPreference pref = new PrivacyPreference(condition); 
		return condition;
	}
	private ContextSnapshot takeSnapshot(){
		ContextSnapshot snapshot = new ContextSnapshot();
		SingleContextAttributeSnapshot attrSnapshot = this.takeAttributeSnapshot(CtxTypes.SYMBOLIC_LOCATION);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		attrSnapshot = this.takeAttributeSnapshot(CtxTypes.STATUS);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		attrSnapshot = this.takeAttributeSnapshot(CtxTypes.ACTIVITY);
		if (attrSnapshot!=null){
			snapshot.addSnapshot(attrSnapshot);
		}
		return snapshot;
	}

	private SingleContextAttributeSnapshot takeAttributeSnapshot(String type){
		CtxIdentifier id;
		try {
			List<CtxIdentifier> l = this.broker.lookup(CtxModelType.ATTRIBUTE, type).get();
			if (l.size()==0){
				return null;
			}
			id = l.get(0);
			CtxAttribute attr = (CtxAttribute) this.broker.retrieve(id);
			SingleContextAttributeSnapshot attrSnapshot = new SingleContextAttributeSnapshot(attr);
			return attrSnapshot;
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	private IPrivacyPreference addToNode(IPrivacyPreference ptn, SingleRule sr){

		logging.debug("BEFORE REMOVAL: "+sr.toString());
		if (null!=ptn.getUserObject()){
			logging.debug(" found common node: "+ptn.getUserObject().toString());
			//IPreferenceCondition[] cons = new IPreferenceCondition[ptn.getLevel()];
			Object[] objs = ptn.getUserObjectPath();

			for (int i = 0; i< objs.length; i++){
				if (objs[i] instanceof ContextPreferenceCondition){
					ContextPreferenceCondition con = (ContextPreferenceCondition) objs[i];
					logging.debug(" removing conditions");
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						logging.debug(" REMOVED "+con.toString());
					}
				}
			}
			if (ptn.getUserObject() instanceof ContextPreferenceCondition){
				if (sr.hasCondition((ContextPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((ContextPreferenceCondition) ptn.getUserObject());
				}
			}
		}else{
			logging.debug(" not found common node");
		}


		logging.debug("AFTER REMOVAL: "+sr.toString());
		IPrivacyPreference leaf = new PrivacyPreference(sr.getOutcome());
		for (int i = 0; i< sr.getConditions().size(); i++){
			ContextPreferenceCondition pc = (ContextPreferenceCondition) ptn.getUserObject();
			if (null==pc){
				logging.debug("weird");
			}
			if (sr.getConditions().get(i) == null){
				logging.debug("even weirder");
			}

			//log("pc: "+pc.toString());
			logging.debug("sr con: "+sr.getConditions().get(i).toString());
			IPrivacyPreference temp = new PrivacyPreference(sr.getConditions().get(i));
			ptn.add(temp);
			ptn = temp;


		}

		ptn.add(leaf);
		return (IPrivacyPreference) ptn.getRoot();
	}



}
