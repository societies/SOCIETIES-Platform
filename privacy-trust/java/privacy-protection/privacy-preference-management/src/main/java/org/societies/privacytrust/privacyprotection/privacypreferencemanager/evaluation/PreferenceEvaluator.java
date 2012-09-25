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
package org.societies.privacytrust.privacyprotection.privacypreferencemanager.evaluation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.trust.ITrustBroker;
import org.societies.api.privacytrust.trust.TrustException;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyOutcome;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.TrustPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.constants.OperatorConstants;



public class PreferenceEvaluator {
	
	private PrivateContextCache contextCache;
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	private ITrustBroker trustBroker;
	public PreferenceEvaluator(PrivateContextCache cache, ITrustBroker broker){
		
		this.contextCache = cache;
		trustBroker = broker;
	}
	
	public Hashtable<IPrivacyOutcome,List<CtxIdentifier>> evaluatePreference(IPrivacyPreference ptn){
		Hashtable<IPrivacyOutcome,List<CtxIdentifier>> temp = new Hashtable<IPrivacyOutcome,List<CtxIdentifier>>();
		IPrivacyPreference p = this.evaluatePreferenceInternal(ptn);
		if (p!=null){
			ArrayList<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>();
			
			Object[] objs = p.getUserObjectPath();
			for (Object obj : objs){
				if (obj instanceof ContextPreferenceCondition){
					ctxIds.add( ((ContextPreferenceCondition) obj).getCtxIdentifier());
				}
			}
			
			/*IPreference[] prefs = (IPreference[]) p.getUserObjectPath();
			for (int i = 0; i<prefs.length; i++){
				if (null!=prefs[i].getUserObject()){
					if (prefs[i].isBranch()){
						IPreferenceCondition condition = prefs[i].getCondition();
						ctxIds.add(condition.getCtxIdentifier());
					}
				}
			}*/
			
			
			temp.put(p.getOutcome(), ctxIds);
			//JOptionPane.showMessageDialog(null, "Evaluation: returning outcome: "+p.getOutcome().toString());
			return temp;
		}else{
			return new Hashtable<IPrivacyOutcome,List<CtxIdentifier>>();
		}
	}
	
	
	private IPrivacyPreference evaluatePreferenceInternal(IPrivacyPreference ptn){
		log("evaluating preference");
		//a non-context aware preference
		if (ptn.isLeaf()){
			log("preference is not context-dependent. returning IAction object"+ptn.getOutcome().toString());
			return ptn;
		}
		//if the root object is null then the tree is split so we have to evaluate more than one tree
		if (ptn.getUserObject()==null){
			log("preference tree is split. we might have a conflict");
			Enumeration<IPrivacyPreference> e = ptn.children();
			ArrayList<IPrivacyPreference> prefList = new ArrayList<IPrivacyPreference>(); 
			while (e.hasMoreElements()){
				IPrivacyPreference p = e.nextElement();
				IPrivacyPreference outcomePreference = this.evaluatePreferenceInternal(p);
				if (outcomePreference!=null){
					prefList.add(outcomePreference);
				}
				
			}
			//if only one IOutcome is applicable with the current context return that
			if (prefList.size()==1){
				log("PrefEvaluator> Returning: "+ prefList.get(0).toString());
				return prefList.get(0);
			}
			//if no IOutcome is applicable, return a null object
			else if (prefList.size()==0){
				log("PrefEvaluator> No preference applicable");
				return null;
			}
			//if more than one IOutcome objs is applicable, use conflict resolution and return the most applicable
			else{
				ConflictResolver cr = new ConflictResolver();
				IPrivacyPreference io = cr.resolveConflicts(prefList);
				log("PrefEvaluator> Returning: "+io.toString());
				return io;
			}
		}
		//if the root node is not empty
		else{
			log("preference tree is not split. no conflicts here");
			//and it's a condition
			if (ptn.isBranch()){
				//evaluate the condition
				IPrivacyPreferenceCondition con = ptn.getCondition();
				try {
					if (evaluatesToTrue(con)){
						//JOptionPane.showMessageDialog(null, con.toString()+" evaluated to true");
						log(con.toString()+" is true - descending tree levels");
						//traverse the tree in preorder traversal to evaluate all the conditions under this branch and find an Action 
						Enumeration<IPrivacyPreference> e = ptn.children();
						while (e.hasMoreElements()){
							IPrivacyPreference p = e.nextElement();
							IPrivacyPreference outcomePreference = this.evaluatePreferenceInternal(p);
							if(null != outcomePreference){
								return outcomePreference;
							}
						}		
					}else{
						//JOptionPane.showMessageDialog(null, con.toString()+" evaluated to false");
						log(con.toString()+" is false - returning");
					}
				} catch (PrivacyException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}//and it's not a condition but an Outcome (i.e. not a branch but a leaf)
			else{
				log("PrefEvaluator> Returning: "+ptn.getOutcome());
				return ptn;
			}
		}
		
		
		return null;
	}

	
	
	
	public boolean evaluatesToTrue(IPrivacyPreferenceCondition cond)throws PrivacyException{
		if (cond instanceof ContextPreferenceCondition){
			ContextPreferenceCondition contextCond = (ContextPreferenceCondition) cond;
			String currentContextValue = this.getValueFromContext(contextCond.getCtxIdentifier());
			OperatorConstants operator = contextCond.getOperator();
			
			log("evaluating cond: "+contextCond.toString()+" against current value: "+currentContextValue);
			if (operator.equals(OperatorConstants.EQUALS)){
				
				return currentContextValue.equalsIgnoreCase(contextCond.getValue());
			}
			else
				return this.evaluateInt(parseString(contextCond.getValue()), parseString(currentContextValue), operator);
		}else if (cond instanceof TrustPreferenceCondition){
			TrustPreferenceCondition trustCond = (TrustPreferenceCondition) cond;
			try {
				Double trustValue = this.trustBroker.retrieveTrust(trustCond.getTrustId()).get();
				if (trustValue.compareTo(trustCond.getTrustThreshold())>=0){
					return true;
				}else{
					return false;
				}
			} catch (TrustException e) {
				e.printStackTrace();
				return false;
			} catch (InterruptedException e) {
				e.printStackTrace();
				return false;
			} catch (ExecutionException e) {
				e.printStackTrace();
				return false;
			}
		}else{
			throw new PrivacyException("PM: Condition is not a Context condition");
		}
		
	}
	
	public boolean evaluateInt(int valueInPreference, int valueInContext, OperatorConstants operator){
		boolean result = false;
		switch (operator){
		case GREATER_OR_EQUAL_THAN:
			result = valueInContext >= valueInPreference;
			break;
		case GREATER_THAN:
			result = valueInContext > valueInPreference;
			break;
		case LESS_OR_EQUAL_THAN:
			result = valueInContext <= valueInPreference;
			break;
		case LESS_THAN:
			result = valueInContext < valueInPreference;
			break;
		default: log("Invalid Operator");
		}
		
		return result;
	}
	
	public String getValueFromContext(CtxIdentifier id){
		if (id==null){
			this.logging.debug("can't get context value from null id");
		}
		if (this.contextCache==null){
			this.logging.debug("ContextCache is null. PrefEvaluator not initialised properly");
		}
		return this.contextCache.getContextValue(id);
		
	}
	
	public int parseString(String str){
		try{
			return Integer.parseInt(str);
		}catch (NumberFormatException nbe){
			log("Could not parse String to int");
			return 0;
		}
		
	}
	
	private void log(String message){
		this.logging.info(this.getClass().getName()+" : "+message);
	}
	

/*	public static void main(String[] args){
		ICtxBroker sbroker = new StubCtxBroker();
		
		try {
			
			ICtxEntity entity = sbroker.createEntity("Person");
			if (entity==null){
				System.out.println("entity is null");
			}
			ICtxEntityIdentifier entityID = entity.getCtxIdentifier();
			ICtxAttribute symlocAttr = sbroker.createAttribute(entityID, CtxTypes.SYMBOLIC_LOCATION);
			//ICtxEntityIdentifier entityID = new StubCtxEntityIdentifier("AliceSecret1234%5Bbe94bf83-1264-4f36-b339-53894b0a376f%5D,","ac3fdc679c58235326","person",1L);
			//ICtxAttributeIdentifier symlocAttrID = new StubCtxAttributeIdentifier(entityID, CtxTypes.SYMBOLIC_LOCATION, 62L);
			//ICtxAttribute symlocAttr = new StubCtxAttribute(symlocAttrID, entityID);
			symlocAttr.setStringValue("home");
			sbroker.update(symlocAttr);
			
			IPreference condition1 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "home", CtxTypes.SYMBOLIC_LOCATION));
			IPreference condition2 = new PreferenceTreeNode(
					new ContextPreferenceCondition(symlocAttr.getCtxIdentifier(), OperatorConstants.EQUALS, "work", CtxTypes.SYMBOLIC_LOCATION));

			//define outcomes
			IPreference outcome1 = new PreferenceTreeNode(new PreferenceOutcome("volume", "100"));
			IPreference outcome2 = new PreferenceTreeNode(new PreferenceOutcome("volume", "0"));
			
		
			//build tree
			IPreference preference = new PreferenceTreeNode();
			preference.add(condition1); //branch 1
			condition1.add(outcome1);
			preference.add(condition2); //branch 2
			condition2.add(outcome2);
			
			PrivateContextCache cc = new PrivateContextCache(sbroker);
		
			PreferenceEvaluator ev = new PreferenceEvaluator(cc);
			Hashtable<IOutcome,List<CtxIdentifier>> result = ev.evaluatePreference(preference);
			if (result!=null && (!result.isEmpty())){
				System.out.println("got result. size: "+result.size());
				Enumeration<IOutcome> outcomes = result.keys();
				while (outcomes.hasMoreElements()){
					IOutcome o = outcomes.nextElement();
					System.out.println(o.toString());
				}
			}
		} catch (ContextException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
}

