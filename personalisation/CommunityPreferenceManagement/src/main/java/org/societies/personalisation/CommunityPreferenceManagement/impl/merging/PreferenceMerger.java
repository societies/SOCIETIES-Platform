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
package org.societies.personalisation.CommunityPreferenceManagement.impl.merging;

import java.util.ArrayList;
import java.util.Enumeration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.preference.api.UserPreferenceMerging.IUserPreferenceMerging;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceOutcome;
import org.societies.personalisation.preference.api.model.PreferenceTreeNode;



public class PreferenceMerger implements IUserPreferenceMerging{
	
	private String situation;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	public PreferenceMerger(){
		//RequestUserConfirmation ruc = new RequestUserConfirmation();
		situation = "";
	}

	@Override
	public IPreference mergeTrees(IPreference oldTree, IPreference newNode, String title){
		this.situation = title;

		if (newNode.isLeaf()){
			if (logging.isDebugEnabled()){
				this.logging.debug("new node does not contain context condition. merging as leaf");
			}
			return mergeNewNodeAsLeaf(newNode,oldTree);
			
		}
		if (oldTree.isLeaf()){
			if (logging.isDebugEnabled()){
				this.logging.debug("old node does not contain context condition. merging as leaf");
			}
			return mergeNewNodeAsLeaf(oldTree,newNode);
			
		}
		
		
		ArrayList<SingleRule> singleRules = this.convertToSingleRules(newNode);
		
		
		IPreference mergedTree = oldTree;
		for (int i = 0; i< singleRules.size(); i++){
			
			SingleRule sr = singleRules.get(i);
			if (logging.isDebugEnabled()){
				logging.debug("Merging new Single Rule: "+sr.toString());
			}
			if (logging.isDebugEnabled()){
				logging.debug("\twith: "+mergedTree.toTreeString());
			}
			IPreference temp = merge(mergedTree, sr);
			if (temp==null){
				return null;
			}
			mergedTree = temp; //in the MergingManager if this method returns null, it means we have to request a full learning cycle
			
		}
		//DisplayPreferenceTree dpt = new DisplayPreferenceTree(new PreferenceTreeModel(oldTree),"Merged Tree :"+title);

		return mergedTree;
	}
	
	public ArrayList<SingleRule> convertToSingleRules(IPreference ptn){
		ArrayList<SingleRule> singleRules = new ArrayList<SingleRule>();
		
		
		Enumeration<IPreference> newNodeEnum = ptn.depthFirstEnumeration();
		
		ArrayList<IPreference> leaves = new ArrayList<IPreference>();
		
		
		while (newNodeEnum.hasMoreElements()){
			IPreference temp = newNodeEnum.nextElement();
			
			if (temp.isLeaf()){
				leaves.add(temp);
			}
		}
		//we're going to construct SingleRule objects from the new tree to use as input to merge with the old tree
		
		for (IPreference leaf : leaves){
			SingleRule sr = new SingleRule();
			Object[] userObjs = leaf.getUserObjectPath();
			for (int i=0; i<userObjs.length; i++){
				if (userObjs[i]!=null){
					if (userObjs[i] instanceof IPreferenceCondition){
						sr.addConditions((IPreferenceCondition) userObjs[i]); 
					}else {
						sr.setOutcome((IPreferenceOutcome) userObjs[i]);
					}
				}
			}
			singleRules.add(sr);
		}

		
		for (int i=0; i<singleRules.size(); i++){
			if (logging.isDebugEnabled()){
				logging.debug("::"+singleRules.get(i).toString());
			}
		}
		return singleRules;
	}
	private IPreference merge(IPreference oldTree, SingleRule sr){
		//IPreference newTree = null;
		ArrayList<SingleRule> oldRules = this.convertToSingleRules(oldTree);
		
		//check if we're in Situation 1 (same conditions different outcomes)
		ArrayList<SingleRule> temp = this.checkConflicts(oldRules, sr);
		if (temp.size()>0){
			return null;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Not in situation 1");
		}
		
		//check if we're in Situation 2 (100% match)
		temp = this.checkMatches(oldRules, sr);
		if (temp.size()>0){
			//return createTree(temp);
			return oldTree;
		}
		if (logging.isDebugEnabled()){
			this.logging.debug("Not in Situation 2");
		}
		
		//we're going to find a branch that has the most common conditions with this rule.
		IPreference commonNode = this.findCommonNode(oldTree, sr);
		
		
		
		if (null==commonNode){
			IPreference root = (IPreference) oldTree.getRoot();
			if (null==root.getUserObject()){
				return this.addToNode((IPreference) oldTree.getRoot(),sr);
			}
			IPreference newRoot = new PreferenceTreeNode();
			newRoot.add(root);
			return this.addToNode(newRoot, sr);
		}
		
		return this.addToNode(commonNode, sr);
		
		//ArrayList<SingleRule> sortedRules = sortTree(oldRules);
		//newTree = createTree(sortedRules);
		//return newTree;
		
		
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
	
	
	private IPreference findCommonNode(IPreference ptn, SingleRule sr){
		
		CommonNodeCounter cnc = new CommonNodeCounter();
		
		//if it's an empty root, we have to repeat with all its children
		if (ptn.getUserObject() == null){
			if (logging.isDebugEnabled()){
				this.logging.debug("current node is empty root");
			}
			Enumeration<IPreference> e = ptn.children();
			while (e.hasMoreElements()){
				IPreference p = e.nextElement();
				if (logging.isDebugEnabled()){
					this.logging.debug("processing child :"+p.toString()+" which is child of: "+ptn.toString());
				}
				cnc = findCommonNode(p,sr, cnc);
			}
		}else{
			
			cnc = findCommonNode(ptn,sr,cnc);
		}
		
		return cnc.getMostCommonNode();
	}
	
	private CommonNodeCounter findCommonNode(IPreference ptn, SingleRule sr, CommonNodeCounter cnc){
		
		//unlikely
		if (ptn.isLeaf()){
			if (logging.isDebugEnabled()){
				this.logging.debug("current node is leaf. returning common node counter");
			}
			return cnc;
		}
		
		IPreferenceCondition pc = (IPreferenceCondition) ptn.getUserObject();
		//if they have a common condition, go to the children, otherwise, return and continue with siblings
		if (sr.hasCondition(pc)){
			if (logging.isDebugEnabled()){
				this.logging.debug("Single rule: "+sr.toString()+" has common node: "+pc.toString());
			}
			cnc.add(ptn, ptn.getLevel());
			Enumeration<IPreference> e = ptn.children();
			while (e.hasMoreElements()){
				cnc = findCommonNode(e.nextElement(),sr,cnc);
			}
		}
		return cnc;
	}
	
	private IPreference addToNode(IPreference ptn, SingleRule sr){
		
		if (logging.isDebugEnabled()){
			logging.debug(situation+"BEFORE REMOVAL: "+sr.toString());
		}
		if (null!=ptn.getUserObject()){
			if (logging.isDebugEnabled()){
				logging.debug(this.situation+" found common node: "+ptn.getUserObject().toString());
			}
			//IPreferenceCondition[] cons = new IPreferenceCondition[ptn.getLevel()];
			Object[] objs = ptn.getUserObjectPath();
			
			for (int i = 0; i< objs.length; i++){
				if (objs[i] instanceof IPreferenceCondition){
					IPreferenceCondition con = (IPreferenceCondition) objs[i];
					if (logging.isDebugEnabled()){
						logging.debug(this.situation+" removing conditions");
					}
					if (sr.hasCondition(con)){
						sr.removeCondition(con);
						if (logging.isDebugEnabled()){
							logging.debug(this.situation+" REMOVED "+con.toString());
						}
					}
				}
			}
			if (ptn.getUserObject() instanceof IPreferenceCondition){
				if (sr.hasCondition((IPreferenceCondition) ptn.getUserObject())){
					sr.removeCondition((IPreferenceCondition) ptn.getUserObject());
				}
			}
		}else{
			if (logging.isDebugEnabled()){
				logging.debug(this.situation+" not found common node");
			}
		}
		
		
		if (logging.isDebugEnabled()){
			logging.debug(situation+"AFTER REMOVAL: "+sr.toString());
		}
		IPreference leaf = new PreferenceTreeNode(sr.getOutcome());
		for (int i = 0; i< sr.getConditions().size(); i++){
			IPreferenceCondition pc = (IPreferenceCondition) ptn.getUserObject();
			if (null==pc){
				if (logging.isDebugEnabled()){
					logging.debug("weird");
				}
			}
			if (sr.getConditions().get(i) == null){
				if (logging.isDebugEnabled()){
					logging.debug("even weirder");
				}
			}
			
				//log("pc: "+pc.toString());
			if (logging.isDebugEnabled()){
				logging.debug("sr con: "+sr.getConditions().get(i).toString());
			}
				IPreference temp = new PreferenceTreeNode(sr.getConditions().get(i));
				ptn.add(temp);
				ptn = temp;
			
			
		}
		
		ptn.add(leaf);
		return (IPreference) ptn.getRoot();
	}
	
	public IPreference mergeNewNodeAsLeaf(IPreference newNode, IPreference oldTree){
		//DisplayPreferenceTree dpt;
		if (newNode.isLeaf() && oldTree.isLeaf()){
			return newNode;
		}
		
		IPreference root = (IPreference) oldTree.getRoot();
		
		if (root.getUserObject()==null){
			root.add(newNode);
			if (logging.isDebugEnabled()){
				logging.debug("root user object is null");
			}
			//dpt = new DisplayPreferenceTree(new PreferenceTreeModel(root), "Merged Tree");
			return root;
		}else{
			IPreference newRoot = new PreferenceTreeNode();
			newRoot.add(newNode);
			newRoot.add(oldTree);
			//dpt = new DisplayPreferenceTree(new PreferenceTreeModel(newRoot), "Merged Tree");
			if (logging.isDebugEnabled()){
				logging.debug("root user object is not null");
			}
			return newRoot;
		}
	}
	
/*	
	public IPreference createTree(ArrayList<SingleRule> rules){
		log("rules size: "+rules.size());
		SingleRule sr = rules.get(0);
		log("conditions size: "+sr.getConditions().size());
		IPreference ptn = new IPreference(rules.get(0).getConditions().get(0));
		
		
		for (int i = 0; i < rules.size(); i++){
			
			sr = rules.get(i);
			
			ptn = createTree(ptn,sr);
		}
		

		return ptn;
	}
	
	public IPreference createTree(IPreference ptn, SingleRule sr){
		
		ptn = (IPreference) ptn.getRoot();
		
		
		int index = 0;
		boolean eq = true;
		while ((eq ==true) && (index < sr.getConditions().size())){
			IPreferenceCondition pc = sr.getConditions().get(index);
			if (ptn.getUserObject().equals(pc)){
				ptn = lookupNode(ptn,pc);
			}else{
				eq = false;
			}
			index =+1;
		}
		for (int i = index; i<sr.getConditions().size();i++){
			IPreference temp = new IPreference(sr.getConditions().get(i));
			ptn.add(temp);
			ptn = temp;
		}
		ptn.add(new IPreference(sr.getOutcome()));
		return ptn;
	}
	
	public IPreference lookupNode(IPreference ptn, IPreferenceCondition pc){
		
		IPreference child;
		for (int i = 0; i < ptn.getChildCount(); i++){
			child = (IPreference) ptn.getChildAt(i);
			if (child.getUserObject().equals(pc)){
				return child;
			}
		}
		
		return ptn;
	}
	public ArrayList<SingleRule> sortTree(ArrayList<SingleRule> rules){
		//ArrayList<IPreferenceCondition> newConditions = new ArrayList<IPreferenceCondition>();
		log("START Sorting rules");
		SortingCounter sc = new SortingCounter();
		for (int i =0; i< rules.size();i++){
			SingleRule temp = rules.get(i);
			log("Processing Rule: "+temp.toString());
			ArrayList<IPreferenceCondition> cons = temp.getConditions();
			for (int k=0;k<cons.size();k++){
				log("adding condition: "+cons.get(k));
				sc.incrementCounter(cons.get(k));
			}
		}
		log("END Sorting rules");
		sc.printSortingCounterData();
		ArrayList<SingleRule> sortedRules = this.sortSingleRules(rules, sc);
		
		return sortedRules;
	}
	
	public ArrayList<SingleRule> sortSingleRules(ArrayList<SingleRule> rules, SortingCounter sc){
		//initially we have to find a condition that is common in all single rules. 
		//if there's not one, then we have to split the tree and put a generic root
		ArrayList<SingleRule> srs = new ArrayList<SingleRule>();
		ArrayList<IPreferenceCondition> sortedConditions = sc.getSortedList();
		ArrayList<IPreferenceCondition> popularConditions = sc.getMax();
		if (rules.size()>sc.getMaxCounter()){
			//split tree
		}else{
			//tree does not have to be split. the most common condition will be the root of the tree
			for (int i=0; i<rules.size();i++){
				srs.add(this.sortRule(rules.get(i),sortedConditions));
			}
		}
	
		
		
		
		return srs;
	}
	
	/**
	 * this method sorts a rule according to the order the preference conditions appear in the IPreferenceCondition list
	 * @param sr the rule to sort
	 * @param pcs the list in sorted order 
	 * @return the sorted Rule
	 */
	
	/*
	public SingleRule sortRule(SingleRule sr, ArrayList<IPreferenceCondition> pcs){
		SingleRule sorted = new SingleRule();
		sorted.setOutcome(sr.getOutcome());
		
		for (int i=0; i<pcs.size(); i++){
			IPreferenceCondition pc = pcs.get(i);
			if (sr.hasCondition(pc)){
				sorted.addConditions(pc);
				sr.removeCondition(pc);
			}
			
		}
		
		for (int i=0; i<sr.getConditions().size();i++){
			sorted.addConditions(sr.getConditions().get(i));
		}
		return sorted;
	}
	
	
*/	
	

	

}

