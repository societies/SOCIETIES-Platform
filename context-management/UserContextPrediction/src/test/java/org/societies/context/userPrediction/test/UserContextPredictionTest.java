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


	static String deviceList;
	static String statusList;
	static String activityList;
	static String locationList;
	static String tod;
	static UserContextPrediction predictor;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {

		predictor = new UserContextPrediction();
		deviceList = "homePc, homePc, carPC, carPC, OfficePC, OfficePC, mobilePhone, none, TV, n/a, homePc, homePc, carPC, OfficePC, OfficePC, tttt, TV, n/a, carPC, OfficePC, OfficePC, mobilePhone, none, TV, n/a, homePc, carPC, OfficePC, OfficePC, tttt, TV, n/a";
		statusList = "sitting, sitting, sitting, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, sitting, standing, lying, lying, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, standing, lying, lying";
		activityList = "Browsing, Emailing, driving, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, Browsing, driving, working, working, working, watching_movie, sleeping, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, driving, working, working, working, watching_movie, sleeping";
		locationList = "home, home, kifisiasStr, vouliagmenisStr, office, office, office, cantine, home, home, home, home, kifisiasStr, office, office, cantine, home, home, vouliagmenisStr, office, office, office, cantine, home, home, home, kifisiasStr, office, office, cantine, home, home" ;
		tod = "morning, morning, morning, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, morning, afternoon, afternoon, night, night, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, afternoon, afternoon, night, night";


	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Test
	public void testPredictContext() throws Exception {
		
		HashMap<String, Vector<String>> dataSet =  createDataSet();
		System.out.println("dataSet "+ dataSet);
		System.out.println("dataSet size "+ dataSet.size());
		predictor.runNNFromMapOfContext(dataSet, 5);
	
		HashMap<String,String> situation = new HashMap<String,String>();
		situation.put("ToD", "morning");
		situation.put("LOCATION_SYMBOLIC", "home");
		situation.put("DEVICE", "walking");
		situation.put("ACTIVITY", "Browsing");
		
		predictor.predictContext(CtxAttributeTypes.STATUS, situation);
	
	}

	private HashMap<String, Vector<String>> createDataSet(){

		HashMap<String, Vector<String>> dataSet = new HashMap<String, Vector<String>> ();

		dataSet.put("DEVICE", readData("DEVICE"));
		dataSet.put("PHYSICAL_STATUS", readData("PHYSICAL_STATUS"));
		dataSet.put("ACTIVITY", readData("ACTIVITY"));
		dataSet.put("LOCATION_SYMBOLIC", readData("LOCATION_SYMBOLIC"));
		dataSet.put("ToD", readData("ToD"));
		
		return dataSet;
	}
	
	
	private Vector<String> readData(String type){

		Vector<String> genericVector = new Vector<String>();
		String []  dataArray = null;

		if(type.equals("DEVICE")){
			dataArray = deviceList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}

		} else if(type.equals("PHYSICAL_STATUS")){
			dataArray = statusList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals("ACTIVITY")){
			dataArray = activityList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals("LOCATION_SYMBOLIC")){
			dataArray = locationList.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} else if(type.equals("ToD")){
			dataArray = tod.split("\\, ");	
			for(String value : dataArray){
				genericVector.add(value);
			}
		} 

				
		return genericVector;
	}
	
}