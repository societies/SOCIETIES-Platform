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
package org.societies.context.userPrediction.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.context.model.CtxEntityTypes;
import org.societies.context.userPrediction.impl.UserContextPrediction;

public class UserContextPredictionTest {


	static String temperatureList;
	static String statusList;
	static String activityList;
	static String locationList;
	static String hodList;
	static UserContextPrediction predictor;
	private static final String HoD = "hourOfDay";
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		predictor = new UserContextPrediction();
		
		temperatureList = "mild, mild, hot, hot, warm, warm, cold, none, cool, n/a, mild, mild, hot, warm, warm, tttt, TV, n/a, hot, warm, warm, cold, none, TV, n/a, mild, hot, warm, warm, tttt, TV, n/a";
		statusList = "sitting, sitting, sitting, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, sitting, standing, lying, lying, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, standing, lying, lying";
		activityList = "Browsing, Emailing, driving, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, Browsing, driving, working, working, working, watching_movie, sleeping, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, driving, working, working, working, watching_movie, sleeping";
		locationList = "home, home, kifisiasStr, vouliagmenisStr, office, office, office, cantine, home, home, home, home, kifisiasStr, office, office, cantine, home, home, vouliagmenisStr, office, office, office, cantine, home, home, home, kifisiasStr, office, office, cantine, home, home" ;
		hodList = "morning, morning, morning, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, morning, afternoon, afternoon, night, night, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, afternoon, afternoon, night, night";


	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testPredictContext() throws Exception {
		
		HashMap<String, Vector<String>> dataSet =  createDataSet();
		System.out.println("dataSet "+ dataSet);
		System.out.println("dataSet size "+ dataSet.size());

		//predictor.runNNFromMapOfContext(dataSet, 5);
		
//		System.out.println("learning performed ");
		
		HashMap<String,String> situation = new HashMap<String,String>();
		situation.put(HoD, "morning");
		situation.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		situation.put(CtxAttributeTypes.TEMPERATURE, "mild");
		situation.put(CtxAttributeTypes.ACTION, "Browsing");
		
		String outcome = predictor.predictContextTraining(CtxAttributeTypes.STATUS, situation, dataSet);
		System.out.println("outcome1: "+ outcome);
		assertEquals("sitting", outcome);
		
		HashMap<String,String> situation2 = new HashMap<String,String>();
		situation2.put(HoD, "afternoon");
		situation2.put(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
		situation2.put(CtxAttributeTypes.TEMPERATURE, "cold");
		situation2.put(CtxAttributeTypes.ACTION, "chatting");
		String outcome2 = predictor.predictContextTraining(CtxAttributeTypes.STATUS, situation2, dataSet);
		System.out.println("outcome2: "+ outcome2);
		assertEquals("walking", outcome2);
		
		
	}

	private HashMap<String, Vector<String>> createDataSet(){

		HashMap<String, Vector<String>> dataSet = new HashMap<String, Vector<String>> ();

		dataSet.put(CtxAttributeTypes.TEMPERATURE, readData(CtxAttributeTypes.TEMPERATURE));
		dataSet.put(CtxAttributeTypes.STATUS, readData(CtxAttributeTypes.STATUS));
		dataSet.put(CtxAttributeTypes.ACTION, readData(CtxAttributeTypes.ACTION));
		dataSet.put(CtxAttributeTypes.LOCATION_SYMBOLIC, readData(CtxAttributeTypes.LOCATION_SYMBOLIC));
		dataSet.put(HoD, readData(HoD));

		return dataSet;
	}
	
	
	private Vector<String> readData(String type){

		
		temperatureList = "mild, mild, hot, hot, warm, warm, cold, none, cool, n/a, mild, mild, hot, warm, warm, tttt, TV, n/a, hot, warm, warm, cold, none, TV, n/a, mild, hot, warm, warm, tttt, TV, n/a";
		statusList = "sitting, sitting, sitting, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, sitting, standing, lying, lying, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, standing, lying, lying";
		activityList = "Browsing, Emailing, driving, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, Browsing, driving, working, working, working, watching_movie, sleeping, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, driving, working, working, working, watching_movie, sleeping";
		locationList = "home, home, kifisiasStr, vouliagmenisStr, office, office, office, cantine, home, home, home, home, kifisiasStr, office, office, cantine, home, home, vouliagmenisStr, office, office, office, cantine, home, home, home, kifisiasStr, office, office, cantine, home, home" ;
		hodList = "morning, morning, morning, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, morning, afternoon, afternoon, night, night, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, afternoon, afternoon, night, night";

		
		Vector<String> genericVector = new Vector<String>();
		String []  dataArray = null;

		if(type.equals(CtxAttributeTypes.TEMPERATURE)){
			dataArray = temperatureList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}

		} else if(type.equals(CtxAttributeTypes.STATUS)){
			dataArray = statusList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals(CtxAttributeTypes.ACTION)){
			dataArray = activityList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
			dataArray = locationList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals(HoD)){
			dataArray = hodList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} 

				
		return genericVector;
	}
	
}