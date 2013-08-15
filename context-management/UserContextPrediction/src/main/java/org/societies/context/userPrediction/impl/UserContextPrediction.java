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


import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
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
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.api.user.prediction.IUserCtxPredictionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class UserContextPrediction implements IUserCtxPredictionMgr {


	private static final Logger LOG = LoggerFactory.getLogger(UserContextPrediction.class);
	private ICtxBroker ctxBroker;
	private ICommManager commMgr;
	
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
	}

	public UserContextPrediction(){
		
	}
	
	
	public void runNNFromMapOfContext(HashMap<?, ?> mapOfContext, int neurons) {

		Vector<String> ToDVector = new Vector<String>();
		Vector<String> LocationVector = new Vector<String>();
		Vector<String> DeviceVector = new Vector<String>();
		Vector<String> ActivityVector = new Vector<String>();
		Vector<String> PhysicalStatusVector = new Vector<String>();





		Iterator<?> it = mapOfContext.keySet().iterator();
		//elements
		while (it.hasNext()) {
			String attrType = (String) it.next();
			Vector<?> values = (Vector<?>) mapOfContext.get(attrType);    //Vector of values

			//check the type and call transform
			if(attrType.equals("ToD"))
				ToDVector = transformType(attrType, values);
			else if(attrType.equals("LOCATION_SYMBOLIC"))
				LocationVector = transformType(attrType, values);
			else if(attrType.equals("DEVICE"))
				DeviceVector = transformType(attrType, values);
			else if(attrType.equals("ACTIVITY"))
				ActivityVector = transformType(attrType, values);
			else if(attrType.equals("PHYSICAL_STATUS"))
				PhysicalStatusVector = transformType(attrType, values);
		} 

		//now that we have the vectors set up the training set
		TrainingSet<SupervisedTrainingElement>  trainingSet = new TrainingSet<SupervisedTrainingElement>(4, 1);
		Iterator<String> iter = ToDVector.iterator();	//suppose that we have the same size on all input vectors
		int index=0;
		while (iter.hasNext()) {
			String element = (String) iter.next(); 

			//check if we have -1 in the vectors in order to ignore the vector record
			if (element.equals("-1") || LocationVector.get(index).equals("-1") || DeviceVector.get(index).equals("-1") ||
					ActivityVector.get(index).equals("-1") || PhysicalStatusVector.get(index).equals("-1"))
				;
			else { //no -1 found
				trainingSet.addElement(new SupervisedTrainingElement(new double[] {Double.parseDouble(element), 
						Double.parseDouble(LocationVector.get(index)),
						Double.parseDouble(DeviceVector.get(index)),
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
		//		System.out.println("Testing loaded neural network");
		//testNeuralNetwork(this.loadedMlPerceptron, trainingSet);

		//System.out.println("dictionary : " + this.dictionary);

	}


	public void testNeuralNetwork(NeuralNetwork nnet, TrainingSet<SupervisedTrainingElement> tset) {

		for(int i=0; i<tset.size(); i++) {
			TrainingElement trainingElement = tset.elementAt(i);
			nnet.setInput(trainingElement.getInput());
			nnet.calculate();
			double[] networkOutput = nnet.getOutput();
			System.out.print("Input num :" + arrayToString(trainingElement.getInput()) + " Trained Output num:" + desiredOutput.get(i));
			System.out.print("\n Input types :" + arrayToTypes(trainingElement.getInput()) + " Trained Output type:" + numberToType("PHYSICAL_STATUS",desiredOutput.get(i)));

			String netOut = arrayToString(networkOutput);
			System.out.println("\n prediction value:" + netOut); 
			System.out.println(" prediction type :" + numberToType("PHYSICAL_STATUS",netOut) );
		}

	}
	
	/*
	 * new interface
	 */
	public String predictContext(String type, Map<String,String> situation) {

		String outcome = "";
		//convertStringToNum()


		double input[]= {typeToNumber("ToD","morning"), typeToNumber("LOCATION_SYMBOLIC","home"), typeToNumber("DEVICE","homePc"),
				typeToNumber("ACTIVITY","Browsing")};
		//String typeString = arrayToTypes(input);

		this.loadedMlPerceptron.setInput(input);
		this.loadedMlPerceptron.calculate();

		double[] networkOutput = this.loadedMlPerceptron.getOutput();

		String netOut = arrayToString(networkOutput);
		outcome = numberToType("PHYSICAL_STATUS" , netOut);

		//	System.out.println("\n prediction input:" + typesString);
		//	System.out.println("\n prediction num value:" + netOut);
		  System.out.println("\n prediction string value:" + outcome);

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
				type = numberToType("ToD" ,stringValue);	
			} else if(i==1){
				type = numberToType("LOCATION_SYMBOLIC" ,stringValue);
			} else if (i==2){
				type = numberToType("DEVICE" ,stringValue);
			} else if (i==3){
				type = numberToType("ACTIVITY" ,stringValue);
			} else if (i==4){
				type = numberToType("PHYSICAL_STATUS" ,stringValue);
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

			if(attributeType.equals(attrType)){
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
		System.out.println("typeValues "+typeValues);
		return valueNum;
	}



	@Override
	public org.societies.context.api.user.prediction.PredictionMethod getDefaultPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public org.societies.context.api.user.prediction.PredictionMethod getPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttribute predictContext(
			org.societies.context.api.user.prediction.PredictionMethod predictionModel,
			CtxAttributeIdentifier ctxAttrID, Date date) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID,
			Date date) {

		CtxAttribute predictedCtxAttr = null;

		String locationValue = "";
		String type = ctxAttrID.getType();
		CtxEntityIdentifier entId;
	
		
		
		try {
		
			predictedCtxAttr = (CtxAttribute) this.ctxBroker.retrieve(ctxAttrID);
			
			entId = this.ctxBroker.retrieveIndividualEntityId(null, getOwnerId()).get();
			List<CtxIdentifier> locationList = this.ctxBroker.lookup(entId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
				
			if(!locationList.isEmpty()){
				CtxAttribute locationAttr = (CtxAttribute) this.ctxBroker.retrieve(locationList.get(0));
				locationValue = locationAttr.getStringValue();
			}
			
			HashMap<String,String> situation = new HashMap<String,String>();
			situation.put("ToD", "");
			situation.put("LOCATION_SYMBOLIC", locationValue);
			situation.put("DEVICE", "");
			situation.put("ACTIVITY", "");
			
			String prdValue = this.predictContext(type, situation);
			
			predictedCtxAttr.setStringValue(prdValue);
			
			
		} catch (Exception e) {
			LOG.error("Exception while predicting context for attrID:"+ ctxAttrID+". "+e.getLocalizedMessage());
			e.printStackTrace();
		} 		
		
		LOG.debug("predictedCtxAttr :"+ predictedCtxAttr);
		LOG.debug("prdValue :"+ predictedCtxAttr.getStringValue());
		
		return predictedCtxAttr;
	}


	/*
	 * new interface
	 */
	public String predictContext(String type, Date date) {

		return null;
	}


	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier ctxAttrID,
			int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttribute predictContext(
			org.societies.context.api.user.prediction.PredictionMethod predictionModel,
			CtxAttributeIdentifier ctxAttrID, int index) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void removePredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefaultPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setPredictionMethod(
			org.societies.context.api.user.prediction.PredictionMethod predMethod) {
		// TODO Auto-generated method stub

	}

	/*
	private CtxIdentifier getEntityID(){
		
	}
	*/
	
	private IIdentity getOwnerId(){

		IIdentity cssOwnerId = null;
		try {
			final INetworkNode cssNodeId = this.commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + cssNodeId);
			final String cssOwnerStr = cssNodeId.getBareJid();
			cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerStr);
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return cssOwnerId;
	}

	
	
}