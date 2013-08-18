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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifierFactory;
import org.societies.api.context.model.CtxModelObjectFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.userPrediction.impl.UserContextPrediction;

public class UserContextPredictionNonJunitTest {

	CtxAttribute attribute = null;
	private static final String HoD = "hourOfDay";
	
	UserContextPredictionNonJunitTest(){
	
		CtxAttributeIdentifier id;
		try {
			id = (CtxAttributeIdentifier) CtxIdentifierFactory.getInstance().fromString("context://university.ict-societies.eu/ENTITY/person/1/ATTRIBUTE/locationSymbolic/7");
			attribute = CtxModelObjectFactory.getInstance().createAttribute(id, new Date(), new Date(), "home");
			
		} catch (MalformedCtxIdentifierException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	
	private static Vector<String> desiredOutput = new Vector<String>();
	String temperatureList = "mild, mild, hot, hot, warm, warm, cold, none, cool, n/a, mild, mild, hot, warm, warm, tttt, TV, n/a, hot, warm, warm, cold, none, TV, n/a, mild, hot, warm, warm, tttt, TV, n/a";
	String statusList = "sitting, sitting, sitting, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, sitting, standing, lying, lying, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, standing, lying, lying";
	String activityList = "Browsing, Emailing, driving, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, Browsing, driving, working, working, working, watching_movie, sleeping, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, driving, working, working, working, watching_movie, sleeping";
	String locationList = "home, home, kifisiasStr, vouliagmenisStr, office, office, office, cantine, home, home, home, home, kifisiasStr, office, office, cantine, home, home, vouliagmenisStr, office, office, office, cantine, home, home, home, kifisiasStr, office, office, cantine, home, home" ;
	String tod = "morning, morning, morning, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, morning, afternoon, afternoon, night, night, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, afternoon, afternoon, night, night";

	
	
	void printDataSet(){
		
	}
	
	public static void main(String[] args) throws IOException {

	
		UserContextPredictionNonJunitTest predictorTest = new UserContextPredictionNonJunitTest();
		System.out.println("1.start testing");
	//	HashMap<String, Vector<String>> dataSet = predictorTest.createDataSet();
	//	System.out.println("2.print data set:");
	//	predictorTest.printDataSet(dataSet);
	
		UserContextPrediction predictor = new UserContextPrediction();
	
		
		
	//	predictor.runNNFromMapOfContext(dataSet, 5);
	
		Map<String,String> situation = new HashMap<String,String>();
		situation.put(CtxAttributeTypes.TEMPERATURE,"mild");
		situation.put(CtxAttributeTypes.ACTION,"Browsing");
		situation.put(CtxAttributeTypes.LOCATION_SYMBOLIC,"home");
		situation.put(HoD,"morning");
		
		
		String predictionStatus = predictor.predictContextTraining(CtxAttributeTypes.STATUS,situation);
		System.out.println("4. predict context for situation "+ situation);
		System.out.println("5. predicted Status: "+ predictionStatus);
		
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


	private void printDataSet(HashMap<String, Vector<String>> dataSet ){
		
		Vector<String> vectorDataTemp = new Vector<String>();
		Vector<String> vectorDataACTIVITY = new Vector<String>();
		Vector<String> vectorDataLOCATION_SYMBOLIC = new Vector<String>();
		Vector<String> vectorDataToD = new Vector<String>();
		Vector<String> vectorDataPHYSICAL_STATUS = new Vector<String>();
		
		vectorDataTemp = dataSet.get(CtxAttributeTypes.TEMPERATURE);
		vectorDataACTIVITY = dataSet.get(CtxAttributeTypes.ACTION);
		vectorDataLOCATION_SYMBOLIC = dataSet.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
		vectorDataToD = dataSet.get(HoD);
		vectorDataPHYSICAL_STATUS = dataSet.get(CtxAttributeTypes.STATUS);
		
		for( int i=0 ; i < vectorDataToD.size(); i++){
			System.out.println(vectorDataToD.get(i)+","+vectorDataTemp.get(i)+","+vectorDataACTIVITY.get(i)+","+vectorDataLOCATION_SYMBOLIC.get(i)+","+vectorDataPHYSICAL_STATUS.get(i));
		}
	}
	

	private Vector<String> readData(String type){

		Vector<String> genericVector = new Vector<String>();
		String []  dataArray = null;

		if(type.equals("DEVICE")){
			dataArray = temperatureList.split("\\, ");	
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
