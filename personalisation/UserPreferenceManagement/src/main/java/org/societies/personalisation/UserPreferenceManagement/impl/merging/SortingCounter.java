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
package org.societies.personalisation.UserPreferenceManagement.impl.merging;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.preference.api.model.IPreferenceCondition;

/**
 * @author Elizabeth
 *
 */
public class SortingCounter{
	private Logger logging = LoggerFactory.getLogger(this.getClass());
	
	private class ConditionCounter{
		IPreferenceCondition pc;
		int counter;
		
		ConditionCounter(IPreferenceCondition con){
			this.pc = con;
			this.counter = 0;
		}
		
		void incrementCounter(){
			this.counter+=1;
		}
		
		IPreferenceCondition getCondition(){
			return this.pc;
		}
		
		int getCounter(){
			return this.counter;
		}
		
		public String toString(){
			return this.pc.toString() + " : "+this.counter;
		}
	}
	
	private ArrayList<ConditionCounter> ccList;
	
	SortingCounter(){
		this.ccList = new ArrayList<ConditionCounter>();
	}
	
	public void addCondition(IPreferenceCondition pc){
		ConditionCounter cc = new ConditionCounter(pc);
		cc.incrementCounter();
		this.ccList.add(cc);
	}
	
	public void incrementCounter(IPreferenceCondition pc){
		boolean exists = false;
		for (int i=0; i<this.ccList.size(); i++){
			ConditionCounter cc = this.ccList.get(i);
			if (cc.getCondition().equals(pc)){
				cc.incrementCounter();
				exists = true;
			}
		}
		
		if (!exists){
			this.addCondition(pc);
		}
	}
	
	public int getCounter(IPreferenceCondition pc){
		for (int i=0; i<this.ccList.size(); i++){
			ConditionCounter cc = this.ccList.get(i);
			if (cc.getCondition().equals(pc)){
				return cc.getCounter();
			}
		}			
		return 0;
	}
	
	public ArrayList<IPreferenceCondition>  getMax(){
		int counter = 0;
		for (int i=0; i<this.ccList.size(); i++){
			ConditionCounter cc = this.ccList.get(i);
			if (counter < cc.getCounter()){
				counter = cc.getCounter();
			}
		}
		ArrayList<IPreferenceCondition> ret = new ArrayList<IPreferenceCondition>();
		for (int i=0; i< this.ccList.size(); i++){
			ConditionCounter cc = this.ccList.get(i);
			if (counter==cc.getCounter()){
				ret.add(cc.getCondition());
			}
		}
		return ret;
	}
	
	public int getMaxCounter(){
		int counter = 0;
		for (int i=0; i<this.ccList.size(); i++){
			ConditionCounter cc = this.ccList.get(i);
			if (counter < cc.getCounter()){
				counter = cc.getCounter();
			}
		}
		return counter;
	}
	
	public ArrayList<IPreferenceCondition> getSortedList(){
		ArrayList<IPreferenceCondition> ret = new ArrayList<IPreferenceCondition>();
		int counter = this.getMaxCounter();
		
		for (int i=counter;i>0;i--){
			for (int k=0; k<ccList.size(); k++){
				if (ccList.get(k).counter==i){
					ret.add(ccList.get(k).getCondition());
				}
			}
		}
		return ret;
	}
	
	public ArrayList<IPreferenceCondition> getConditions(){
		ArrayList<IPreferenceCondition> pcList = new ArrayList<IPreferenceCondition>();
		
		for (int i=0;i<this.ccList.size();i++){
			pcList.add(ccList.get(i).getCondition());
		}
		return pcList;
	}
	
	public void printSortingCounterData(){
		for (int i =0; i<this.ccList.size(); i++){
			if(this.logging.isDebugEnabled()){
				logging.debug(this.ccList.get(i).toString());
			}
		}
	}
	
	
}

