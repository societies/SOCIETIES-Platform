/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druÅ¾be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÃ‡ÃƒO, SA (PTIN), IBM Corp., 
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
package org.societies.context.userPrediction.impl;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.api.user.prediction.IUserCtxPredictionMgr;
import org.societies.context.api.user.prediction.PredictionMethod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserContextPrediction implements IUserCtxPredictionMgr {


	private static final Logger LOG = LoggerFactory.getLogger(UserContextPrediction.class);
	private ICtxBroker ctxBroker;
	private ICommManager commMgr;

	private static final String HoD = "hourOfDay";
	
	PredictionMethod currentPredictionMehtod;


	private static Vector<String> desiredOutput = new Vector<String>();
	NeuralNetwork loadedMlPerceptron = null;

	HashMap<String, HashMap<String, Double>> dictionary = new HashMap<String, HashMap<String, Double>>();


	@Autowired(required=true)
	public UserContextPrediction(ICtxBroker ctxBroker, ICommManager commMgr){
		if (LOG.isDebugEnabled()){
			LOG.info(this.getClass() + "instantiated UserContextPrediction ");
		}
		this.ctxBroker = ctxBroker;
		this.commMgr = commMgr;	

		this.currentPredictionMehtod = new PredictionMethod();
		currentPredictionMehtod.setCurrentMethod(PredictionMethod.NEURAL_NETWORKS);
		LOG.debug("current UserContextPrediction: " + currentPredictionMehtod.getCurrentMethod() );
	}

	public UserContextPrediction(){

	}


	public void runNNFromMapOfContext(HashMap<?, ?> mapOfContext, int neurons) {

		Vector<String> HoDVector = new Vector<String>();
		Vector<String> LocationVector = new Vector<String>();
		Vector<String> temperatureVector = new Vector<String>();
		Vector<String> ActivityVector = new Vector<String>();
		Vector<String> PhysicalStatusVector = new Vector<String>();

		Iterator<?> it = mapOfContext.keySet().iterator();
		//elements
		while (it.hasNext()) {
			String attrType = (String) it.next();
			Vector<?> values = (Vector<?>) mapOfContext.get(attrType);    //Vector of values

			//check the type and call transform
			if(attrType.equals(HoD))
				HoDVector = transformType(attrType, values);
			else if(attrType.equals(CtxAttributeTypes.LOCATION_SYMBOLIC))
				LocationVector = transformType(attrType, values);
			else if(attrType.equals(CtxAttributeTypes.TEMPERATURE))
				temperatureVector = transformType(attrType, values);
			else if(attrType.equals(CtxAttributeTypes.ACTION))
				ActivityVector = transformType(attrType, values);
			else if(attrType.equals(CtxAttributeTypes.STATUS))
				PhysicalStatusVector = transformType(attrType, values);
		} 
				
		//now that we have the vectors set up the training set
		TrainingSet<SupervisedTrainingElement>  trainingSet = new TrainingSet<SupervisedTrainingElement>(4, 1);
		Iterator<String> iter = HoDVector.iterator();	//suppose that we have the same size on all input vectors
		int index=0;
		while (iter.hasNext()) {
			String element = (String) iter.next(); 

			//check if we have -1 in the vectors in order to ignore the vector record
			if (element.equals("-1") || LocationVector.get(index).equals("-1") || temperatureVector.get(index).equals("-1") ||
					ActivityVector.get(index).equals("-1") || PhysicalStatusVector.get(index).equals("-1"))
				;
			else { //no -1 found
				trainingSet.addElement(new SupervisedTrainingElement(new double[] {Double.parseDouble(element), 
						Double.parseDouble(LocationVector.get(index)),
						Double.parseDouble(temperatureVector.get(index)),
						Double.parseDouble(ActivityVector.get(index))}, new double[] {Double.parseDouble(PhysicalStatusVector.get(index))})); 
				desiredOutput.add(PhysicalStatusVector.get(index));
			}
			index++;
		}
		
		//create the NN
		NeuralNetwork mlp = new MultiLayerPerceptron(TransferFunctionType.TANH, 4, neurons, 1); //neurons number
		SupervisedLearning learningRule = (SupervisedLearning) mlp.getLearningRule(); 
		learningRule.setMaxError(0.000001d);
		//learningRule.setLearningRate(0.5);
		//learn
		mlp.learn(trainingSet);
		//test
		//testNeuralNetwork(mlp, trainingSet);
		
		//save neural network
		mlp.save("MLPForContextData@"+ neurons +".nnet");

		//load neural network
		this.loadedMlPerceptron = NeuralNetwork.load("MLPForContextData@"+ neurons +".nnet");

		//test loaded neural network
		//testNeuralNetwork(this.loadedMlPerceptron, trainingSet);

	}

	public void testNeuralNetwork(NeuralNetwork nnet, TrainingSet<SupervisedTrainingElement> tset) {

		for(int i=0; i<tset.size(); i++) {
			TrainingElement trainingElement = tset.elementAt(i);
			nnet.setInput(trainingElement.getInput());
			nnet.calculate();
			double[] networkOutput = nnet.getOutput();
		//	System.out.print("Input num :" + arrayToString(trainingElement.getInput()) + " Trained Output num:" + desiredOutput.get(i));
		//	System.out.print("\n Input types :" + arrayToTypes(trainingElement.getInput()) + " Trained Output type:" + numberToType("PHYSICAL_STATUS",desiredOutput.get(i)));

			String netOut = arrayToString(networkOutput);
		// System.out.println("\n prediction value:" + netOut); 
		//	System.out.println(" prediction type :" + numberToType(CtxAttributeTypes.STATUS,netOut) );
		}

	}

	/*
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
*/

	private HashMap<String, Vector<String>> createDataSet(String type){

		HashMap<String, Vector<String>> dataSet = new HashMap<String, Vector<String>> ();

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history =  retrieveHistoryTupleData(type);

		Vector <String> statusData = new Vector<String>();
		Vector <String> temperatureData = new Vector<String>();
		Vector <String> activityData = new Vector<String>();
		Vector <String> locData = new Vector<String>();
		Vector <String> hodData = new Vector<String>();

		for(CtxHistoryAttribute hocAttr : history.keySet()){

			//get primary
			statusData.add(this.getHocValue(hocAttr));

			//get escorting
			List<CtxHistoryAttribute> hocList = history.get(hocAttr);

			for(CtxHistoryAttribute hocAttrEsc : hocList){

				if(hocAttrEsc.getType().equals(CtxAttributeTypes.TEMPERATURE)){
					temperatureData.add(this.getHocValue(hocAttrEsc));	
				} else if(hocAttrEsc.getType().equals(CtxAttributeTypes.ACTION)){
					activityData.add(this.getHocValue(hocAttrEsc));
				} else if(hocAttrEsc.getType().equals(CtxAttributeTypes.LOCATION_SYMBOLIC)){
					locData.add(this.getHocValue(hocAttrEsc));
				} else if(hocAttrEsc.getType().equals(HoD)){
					hodData.add(this.getHocValue(hocAttrEsc));
				}
			}
		}

		//	dataSet.put("PHYSICAL_STATUS", statusData);
		//	dataSet.put("DEVICE", readData("DEVICE"));
		//	dataSet.put("PHYSICAL_STATUS", readData("PHYSICAL_STATUS"));
		//	dataSet.put("ACTIVITY", readData("ACTIVITY"));
		//	dataSet.put("LOCATION_SYMBOLIC", readData("LOCATION_SYMBOLIC"));
		//	dataSet.put("ToD", readData("ToD"));

		return dataSet;
	}



	private String getHocValue(CtxHistoryAttribute hocAttr){

		String result = "";
		if(hocAttr.getStringValue() != null){
			result = hocAttr.getStringValue();	
		} else {
			result = "n.a";
		}		
		return result;
	}



	/*
	 * retrieve history tuples with key status and escorting data (xx,yy,zzz)
	 */

	private Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveHistoryTupleData(String attributeType){

		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> results = new LinkedHashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();

		try {
			results = ctxBroker.retrieveHistoryTuples(attributeType, listOfEscortingAttributeIds, null, null).get();
			if (LOG.isDebugEnabled())	LOG.debug(" history: "+ attributeType  +" retrieveHistoryTupleData: " +results);

		} catch (Exception e) {
			LOG.error("Exception when trying to retrieve context history for attribute type:"+attributeType+". "+e.getLocalizedMessage() );
			e.printStackTrace();
		} 
		
		return results;
	}





	/*
	 * retrieve's the predicted string value of ctxAttribute
	 */
	public String predictContextTraining(String type, Map<String,String> situation) {

		String outcome = "";



		HashMap<String, Vector<String>> dataSet = this.createDataSet(type);
		if (LOG.isDebugEnabled())	LOG.debug("2. create dataset:: "+dataSet );

		if (LOG.isDebugEnabled())	LOG.debug("3. train neural network");
		runNNFromMapOfContext(dataSet, 5);

		String todValue = situation.get(HoD);
		String locValue = situation.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
		String devValue = situation.get(CtxAttributeTypes.TEMPERATURE);
		String actValue = situation.get(CtxAttributeTypes.ACTION);

		//System.out.println("\n situation" + situation);

		double input[]= {typeToNumber(HoD,todValue), typeToNumber(CtxAttributeTypes.LOCATION_SYMBOLIC,locValue), typeToNumber(CtxAttributeTypes.TEMPERATURE,devValue),
				typeToNumber(CtxAttributeTypes.ACTION,actValue)};

		this.loadedMlPerceptron.setInput(input);
		this.loadedMlPerceptron.calculate();

		double[] networkOutput = this.loadedMlPerceptron.getOutput();

		String netOut = arrayToString(networkOutput);
		outcome = numberToType(type , netOut);

		//System.out.println("\n prediction string value:" + outcome);
		if (LOG.isDebugEnabled())	LOG.debug("\n prediction string value:" + outcome);

		return outcome;
	}


	public String predictContextTraining(String type, Map<String,String> situation, HashMap<String, Vector<String>> dataSet) {

		String outcome = "";

		//HashMap<String, Vector<String>> dataSet = this.createDataSet(type);
		if (LOG.isDebugEnabled())	LOG.debug("2. create dataset:: "+dataSet );

		if (LOG.isDebugEnabled())	LOG.debug("3. train neural network");
		runNNFromMapOfContext(dataSet, 5);

		String todValue = situation.get(HoD);
		String locValue = situation.get(CtxAttributeTypes.LOCATION_SYMBOLIC);
		String devValue = situation.get(CtxAttributeTypes.TEMPERATURE);
		String actValue = situation.get(CtxAttributeTypes.ACTION);

     	//System.out.println("\n situation" + situation);

		double input[]= {typeToNumber(HoD,todValue), typeToNumber(CtxAttributeTypes.LOCATION_SYMBOLIC,locValue), typeToNumber(CtxAttributeTypes.TEMPERATURE,devValue),
				typeToNumber(CtxAttributeTypes.ACTION,actValue)};

		this.loadedMlPerceptron.setInput(input);
		this.loadedMlPerceptron.calculate();

		double[] networkOutput = this.loadedMlPerceptron.getOutput();

		String netOut = arrayToString(networkOutput);
		outcome = numberToType(type , netOut);

		//System.out.println("\n prediction string value:" + outcome);
		if (LOG.isDebugEnabled())	LOG.debug("\n prediction string value:" + outcome);

		return outcome;
	}

	
	
	public static String arrayToString(double[] array) { 
		String str= "";
		for (int i = 0; i < array.length; i++)
			str += array[i]+" ";
		return str;
	}

	public String arrayToTypes(double[] array) { 
		String str= "";
		//		ToD
		//		LOCATION_SYMBOLIC
		//		DEVICE
		//		ACTIVITY
		//		PHYSICAL_STATUS


		for (int i = 0; i < array.length; i++){
			Double doubleValue = array[i];
			String stringValue = doubleValue.toString(); 
			String type = "";
			if(i==0){
				type = numberToType(HoD ,stringValue);	
			} else if(i==1){
				type = numberToType(CtxAttributeTypes.LOCATION_SYMBOLIC,stringValue);
			} else if (i==2){
				type = numberToType(CtxAttributeTypes.TEMPERATURE ,stringValue);
			} else if (i==3){
				type = numberToType(CtxAttributeTypes.ACTION ,stringValue);
			} else if (i==4){
				type = numberToType(CtxAttributeTypes.STATUS ,stringValue);
			}
			str += type+" ";
		}

		return str;
	}

	/*
	 * converts a numerical value to the respective attribute string value 
	 * 
	 */
	public String numberToType(String attributeType , String attrNumericalValue){

		String type = ""; 
		Double numericalValue = Double.valueOf(attrNumericalValue);

		//System.out.println("Identify value of "+attributeType+" for double:"+ attrNumericalValue);
		//System.out.println("Identify value of "+attributeType+" for double:"+ numericalValue);

		for(String attrType : this.dictionary.keySet()){
			if(attributeType.equals(attrType)){
				HashMap<String,Double> attTypeValues = this.dictionary.get(attributeType);
				//	System.out.println("all values for "+attributeType+" are "+attTypeValues);
				//System.out.println("current value:  "+numericalValue);
				for(String valueString : attTypeValues.keySet()){
					//System.out.println("compare with "+valueString);
					//if(numericalValue.equals(attTypeValues.get(valueString))) type = valueString;
					if(areEqual(numericalValue,attTypeValues.get(valueString))) {
						type = valueString;
						//System.out.println("identified value: "+type);
					}
				}		
			}
		}
		return type;
	}

	/*
	 * converts a string type value to the respective attribute numerical value 
	 * 
	 */
	public Double typeToNumber(String attributeType , String stringValue){

		Double numOut = null;

		for(String attrType : this.dictionary.keySet()){

			if(attributeType.equalsIgnoreCase(attrType)){
				HashMap<String,Double> attTypeValues = this.dictionary.get(attributeType);
				numOut = attTypeValues.get(stringValue);
			}
		}

		return numOut;
	}


	private Boolean areEqual(Double a, Double b){
		Boolean result = false;

		if( a <= 0.001) a= 0d;
		if( b <= 0.001) b= 0d;

		if(a.equals(b)) return true;

		Double out = Math.abs(a-b)/Math.max(a, b);

		if( out <= 0.1){
			result = true;
		}

		return result;
	}


	private Vector<String> transformType(String type, Vector<?> values) {

		Vector<String> valueNum = new Vector<String>();
		Vector<String> typesSeen = new Vector<String>();
		HashMap<String, Double> typeValues = new HashMap<String, Double>();
		double numOfTypes = 0d;
		double value = 0d;

		// define the different context types
		for (int i = 0; i < values.size(); i++) {
			if (!typesSeen.contains(values.get(i))) {
				typesSeen.add((String) values.get(i));
			}
		}
		numOfTypes = typesSeen.size();
		// put values for each type seen
		for (int i = 0; i < typesSeen.size(); i++) {
			value = i * (1 / (numOfTypes - 1));
			typeValues.put((String) typesSeen.get(i), value);
		}

		this.dictionary.put(type, typeValues);

		// put values for each type seen
		for (int i = 0; i < values.size(); i++) {
			valueNum.add(i, String.valueOf(typeValues.get(values.get(i))));
		}

		//System.out.println("typeValues "+typeValues);
		if (LOG.isDebugEnabled())LOG.debug("typeValues "+typeValues);

		return valueNum;
	}



	@Override
	public org.societies.context.api.user.prediction.PredictionMethod getDefaultPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {

		if (LOG.isDebugEnabled()) LOG.debug("default UserContextPrediction method: " + currentPredictionMehtod.getCurrentMethod() );

		return this.currentPredictionMehtod;
	}

	@Override
	public org.societies.context.api.user.prediction.PredictionMethod getPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {

		if (LOG.isDebugEnabled())	LOG.debug("current UserContextPrediction method: " + currentPredictionMehtod.getCurrentMethod() );

		return this.currentPredictionMehtod;
	}

	@Override
	public CtxAttribute predictContext(	PredictionMethod predictionModel,
			CtxAttributeIdentifier ctxAttrID, Date date) {

		this.setPredictionMethod(predictionModel);

		return predictContext(ctxAttrID, date);
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID,
			Date date) {

		if (ctxAttrID == null) {
			throw new NullPointerException("attribute Id can't be null");
		}

		CtxAttribute predictedCtxAttr = null;

		// build current situation based on current context values
		String locationValue = "";
		String tempValue = "";
		String activityValue = "";
		String type = ctxAttrID.getType();
		CtxEntityIdentifier entId;

		try {

			predictedCtxAttr = (CtxAttribute) this.ctxBroker.retrieve(ctxAttrID).get();

			if (predictedCtxAttr == null) {
				throw new NullPointerException("attribute requested for prediction doesn't exist,  attrID:"+ctxAttrID);
			}
			
			entId = this.ctxBroker.retrieveIndividualEntityId(null, getOwnerId()).get();
			List<CtxIdentifier> locationList = this.ctxBroker.lookup(entId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if(!locationList.isEmpty()){
				CtxAttribute locationAttr = (CtxAttribute) this.ctxBroker.retrieve(locationList.get(0)).get();
				locationValue = locationAttr.getStringValue();
			}

			List<CtxIdentifier> temperatureList = this.ctxBroker.lookup(entId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.TEMPERATURE).get();
			if(!temperatureList.isEmpty()){
				CtxAttribute tempAttr = (CtxAttribute) this.ctxBroker.retrieve(temperatureList.get(0)).get();
				tempValue = tempAttr.getStringValue();
			}
			
			List<CtxIdentifier> activityList = this.ctxBroker.lookup(entId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.ACTION).get();
			if(!activityList.isEmpty()){
				CtxAttribute actionAttr = (CtxAttribute) this.ctxBroker.retrieve(activityList.get(0)).get();
				activityValue = actionAttr.getStringValue();
			}

			//date.get
			Calendar cal = Calendar.getInstance();
			cal.setTimeInMillis(date.getTime());
			int HOD = cal.get(Calendar.HOUR_OF_DAY);
			String hourOfDay = Integer.toString(HOD);
			
			
			HashMap<String,String> situation = new HashMap<String,String>();
			situation.put(HoD, hourOfDay);
			situation.put(CtxAttributeTypes.LOCATION_SYMBOLIC, locationValue);
			situation.put(CtxAttributeTypes.TEMPERATURE, tempValue);
			situation.put(CtxAttributeTypes.ACTION, activityValue);

			String prdValue = this.predictContextTraining(type, situation);
			
			
			LOG.debug("predicted string Value: "+ prdValue);
			if(prdValue != null ){
				predictedCtxAttr.setStringValue(prdValue);	
			}
			


		} catch (Exception e) {
			LOG.error("Exception while predicting context for attrID:"+ ctxAttrID+". "+e.getLocalizedMessage());
			e.printStackTrace();
		} 		

		if (LOG.isDebugEnabled()) LOG.debug("predictedCtxAttr :"+ predictedCtxAttr);
		if (LOG.isDebugEnabled()) LOG.debug("prdValue :"+ predictedCtxAttr.getStringValue());

		return predictedCtxAttr;
	}


	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID,
			int index) {

		if (ctxAttrID == null) {
			throw new NullPointerException("attribute Id can't be null");
		}

		CtxAttribute result = null;
		if( index == 0){

			Date date = new Date();
			result = this.predictContext(ctxAttrID, date);	
		} else {
			LOG.info("context prediction is supported only based on time");
		}

		return result;
	}

	@Override
	public CtxAttribute predictContext(
			org.societies.context.api.user.prediction.PredictionMethod predictionModel,
			CtxAttributeIdentifier ctxAttrID, int index) {

		if (ctxAttrID == null) {
			throw new NullPointerException("attribute Id can't be null");
		}

		this.setPredictionMethod(predictionModel);

		return predictContext(ctxAttrID, index);
	}

	@Override
	public void removePredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {

		this.currentPredictionMehtod = null;

	}

	@Override
	public void setDefaultPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {

		this.currentPredictionMehtod = predMethod;
	}

	@Override
	public void setPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {

		this.currentPredictionMehtod = predMethod;

	}



	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (Exception e) {
			LOG.error("Exception when retrieving IIdentity of owner: "+e.getLocalizedMessage());
			e.printStackTrace();
		}

		return cssOwnerId;
	}




// this dataset is used for testing
//	String deviceList = "homePc, homePc, carPC, carPC, OfficePC, OfficePC, mobilePhone, none, TV, n/a, homePc, homePc, carPC, OfficePC, OfficePC, tttt, TV, n/a, carPC, OfficePC, OfficePC, mobilePhone, none, TV, n/a, homePc, carPC, OfficePC, OfficePC, tttt, TV, n/a";
//	String statusList = "sitting, sitting, sitting, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, sitting, standing, lying, lying, sitting, sitting, sitting, walking, standing, lying, lying, sitting, sitting, sitting, sitting, standing, lying, lying";
//	String activityList = "Browsing, Emailing, driving, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, Browsing, driving, working, working, working, watching_movie, sleeping, driving, working, working, chatting, chatting, watching_movie, sleeping, Browsing, driving, working, working, working, watching_movie, sleeping";
//	String locationList = "home, home, kifisiasStr, vouliagmenisStr, office, office, office, cantine, home, home, home, home, kifisiasStr, office, office, cantine, home, home, vouliagmenisStr, office, office, office, cantine, home, home, home, kifisiasStr, office, office, cantine, home, home" ;
//	String tod = "morning, morning, morning, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, morning, afternoon, afternoon, night, night, morning, morning, afternoon, afternoon, afternoon, night, night, morning, morning, morning, afternoon, afternoon, night, night";

}