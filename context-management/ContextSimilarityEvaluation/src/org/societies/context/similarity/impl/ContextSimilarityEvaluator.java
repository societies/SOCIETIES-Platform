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
package org.societies.context.similarity.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;
import org.societies.api.context.similarity.IContextSimilarity;

/**
 *
 * @author John
 *
 */
public class ContextSimilarityEvaluator implements IContextSimilarity {
	
	private evaluationResult er;

	public ContextSimilarityEvaluator(){
		//
		er = new evaluationResult();
		er.init();
	}
	
	public evaluationResult evaluateSimilarity (String[] ids, ArrayList<String> attrib){
		
		// 
		for (String att : attrib){
			evaluateAttribute( att,ids);
		}
		
		HashMap<String, String> summery = er.getSummary();		
		for (String summeryB : summery.values()){
			if ( (summeryB.equals(er.STRONG))  || (summeryB.equals(er.MEDIUM)) ) {
				er.setResult(true);
			}
			else {
				er.setResult(false);
			}
		}
		return er;
	}
	

	private void evaluateAttribute(String att, String[] ids){
		String path = "org.societies.context.similarity.attributes." + att;
		
		try {
			Class<?> c = Class.forName(path);
			String[] argu = ids;
			Method methodToExecute = c.getDeclaredMethod("evaluate", new Class[]{String[].class});
			@SuppressWarnings("unchecked")
			HashMap<String, Double> attributeResults = (HashMap<String, Double>) methodToExecute.invoke(c.newInstance(), new Object[]{argu});
			er.setAattBreakDown(attributeResults);
			evaluateAttributeSummary(att, attributeResults);
			
		} catch (Exception e) {
			e.printStackTrace();
		} 

	}
	
	private void evaluateAttributeSummary(String att, HashMap<String, Double> attributeResults){
		Double max = 0.0;
		for (Double resultV : attributeResults.values()  ){
			if (resultV > max){
				max = resultV;
			}
		}
		if (max > 80.0){
			er.setAttSummary(att, er.STRONG);
		} 
		else if (max > 50.0){
			er.setAttSummary(att, er.MEDIUM);
		}
		else if (max > 25.0){
			er.setAttSummary(att, er.WEAK);
		}
		else {
			er.setAttSummary(att, er.NONE);
		}
	}

	/***
	 *   result
	 */
	public Boolean getResult(){
		return er.getResult();
	}

	public HashMap<String, String> getSummary(){
		return er.getSummary();
	}	

	@SuppressWarnings("rawtypes")
	public Map getAttBreakDown(){
		return er.getAttBreakDown();
	}
	
}