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
package org.societies.personalisation.UserPreferenceManagement.impl.evaluation;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.personalisation.preference.api.model.IPreference;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;
import org.societies.personalisation.preference.api.model.IPreferenceConditionIOutcomeName;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;


public class PreferenceConditionExtractor {

	public PreferenceConditionExtractor(){
		
	}
	
	public List<IPreferenceConditionIOutcomeName> extractConditions(IPreferenceTreeModel pModel){
		IPreference p = pModel.getRootPreference();
		ArrayList<IPreferenceConditionIOutcomeName> list = new ArrayList<IPreferenceConditionIOutcomeName>();
		String preferenceName = pModel.getPreferenceDetails().getPreferenceName();
		
		Enumeration<IPreference> newNodeEnum = p.depthFirstEnumeration();
		if (p.getUserObject()!=null){
			if (p.getUserObject() instanceof IPreferenceCondition){
				IPreferenceConditionIOutcomeName tuple = new IPreferenceConditionIOutcomeName(p.getCondition().getCtxIdentifier(),preferenceName);
				list.add(tuple);
			}
		}
		
		while (newNodeEnum.hasMoreElements()){
			IPreference temp = newNodeEnum.nextElement();
			if (temp.getUserObject()!=null){
				if (temp.getUserObject() instanceof IPreferenceCondition){
					CtxIdentifier id = temp.getCondition().getCtxIdentifier();
					if (!hasCondition(list,id)){
						CtxAttributeIdentifier ctxId = temp.getCondition().getCtxIdentifier();
						IPreferenceConditionIOutcomeName tuple = new IPreferenceConditionIOutcomeName(ctxId,preferenceName);
						list.add(tuple);
					}
				}
			}
		}
		
		return list;
	}
	
	private boolean hasCondition(ArrayList<IPreferenceConditionIOutcomeName> list, CtxIdentifier id){
		Iterator<IPreferenceConditionIOutcomeName> tuplesIt = list.iterator();
		while (tuplesIt.hasNext()){
			IPreferenceConditionIOutcomeName pcOut = tuplesIt.next();
			CtxIdentifier ctxId = pcOut.getICtxIdentifier();
			
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

