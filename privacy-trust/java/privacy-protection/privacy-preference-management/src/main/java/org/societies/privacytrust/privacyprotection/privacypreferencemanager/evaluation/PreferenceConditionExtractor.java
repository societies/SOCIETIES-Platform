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
import java.util.Iterator;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.ContextPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreference;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceCondition;
import org.societies.privacytrust.privacyprotection.api.model.privacypreference.IPrivacyPreferenceTreeModel;


public class PreferenceConditionExtractor {

	public PreferenceConditionExtractor(){
		
	}
	public List<CtxIdentifier> extractConditions(IPrivacyPreferenceTreeModel pModel){
		IPrivacyPreference p = pModel.getRootPreference();
		return this.extractConditions(p);
	}
	public List<CtxIdentifier> extractConditions(IPrivacyPreference p){
		
		ArrayList<CtxIdentifier> list = new ArrayList<CtxIdentifier>();
		
		Enumeration<IPrivacyPreference> newNodeEnum = p.depthFirstEnumeration();
		if (p.getUserObject()!=null){
			if (p.getUserObject() instanceof IPrivacyPreferenceCondition){
				IPrivacyPreferenceCondition condition = p.getCondition();
				if (condition instanceof ContextPreferenceCondition){
					
					list.add(((ContextPreferenceCondition) condition).getCtxIdentifier());	
				}
				
			}
		}
		
		while (newNodeEnum.hasMoreElements()){
			IPrivacyPreference temp = newNodeEnum.nextElement();
			if (temp.getUserObject()!=null){
				if (temp.getUserObject() instanceof IPrivacyPreferenceCondition){
					IPrivacyPreferenceCondition condition = temp.getCondition();

					if (condition instanceof ContextPreferenceCondition){
						CtxIdentifier id = ((ContextPreferenceCondition) temp.getCondition()).getCtxIdentifier();
						if (!hasCondition(list,id)){
							CtxAttributeIdentifier ctxId = ((ContextPreferenceCondition) temp.getCondition()).getCtxIdentifier();
							list.add(ctxId);
						}
					}
					
				}
			}
		}
		
		return list;
	}
	
	
	private boolean hasCondition(ArrayList<CtxIdentifier> list, CtxIdentifier id){
		Iterator<CtxIdentifier> tuplesIt = list.iterator();
		while (tuplesIt.hasNext()){
			
			CtxIdentifier ctxId = tuplesIt.next();
			
			if (ctxId.equals(id)){
				return true;
			}
		}
		return false;
	}
	
/*	public static void main(String[] args) throws IOException{
		PreferenceConditionExtractor pce = new PreferenceConditionExtractor();
		
		ArrayList<IPreferenceConditionIOutcomeName> list = (ArrayList) pce.extractConditions(new PreferenceTreeModel(getTree()));
		
	}
	
		static PreferenceTreeNode getTree(){
		PreferenceOutcome po1 = new PreferenceOutcome("volume","10");
		PreferenceTreeNode leaf1 = new PreferenceTreeNode(po1);
		ContextPreferenceCondition pc1 = new ContextPreferenceCondition(, "=", "busy", "context", "activity");
		PreferenceTreeNode ptn1 = new PreferenceTreeNode(pc1);
		ptn1.add(leaf1);
		
		PreferenceOutcome po = new PreferenceOutcome("volume","10");
		PreferenceTreeNode leaf = new PreferenceTreeNode(po);
		ContextPreferenceCondition pc = new ContextPreferenceCondition("location", "=", "home", "context", "location");
		PreferenceTreeNode ptn = new PreferenceTreeNode(pc);
		ptn.add(leaf);
		

		
		PreferenceTreeNode root = new PreferenceTreeNode();
		root.add(ptn);
		root.add(ptn1);
		return root;
	}*/
}

