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

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import jxl.read.biff.BiffException;

import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.learning.SupervisedLearning;
import org.neuroph.core.learning.SupervisedTrainingElement;
import org.neuroph.core.learning.TrainingElement;
import org.neuroph.core.learning.TrainingSet;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.util.TransferFunctionType;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.context.api.user.prediction.IUserCtxPredictionMgr;
import org.societies.context.api.user.prediction.PredictionMethod;

public class UserContextPrediction implements IUserCtxPredictionMgr {

	
	public UserContextPrediction(){
		
	}
	
	 /**
     * @param args the command line arguments
     */
	private static Vector<String> desiredOutput = new Vector<String>();
	
    public static void main(String[] args) throws IOException, BiffException {
        
        ReadExcel test = new ReadExcel();
        test.setInputFile("./dataSet2.xls");
        UserContextPrediction predictor = new UserContextPrediction();
        predictor.runNNFromMapOfContext(test.read(), 5);
        
    }
    
    private void runNNFromMapOfContext(HashMap<?, ?> mapOfContext, int neurons) {
        
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
        testNeuralNetwork(mlp, trainingSet);
        
        //save neural network
        mlp.save("MLPForContextData@"+ neurons +".nnet");

        //load neural network
        NeuralNetwork loadedMlPerceptron = NeuralNetwork.load("MLPForContextData@"+ neurons +".nnet");

        //test loaded neural network
        System.out.println("Testing loaded neural network");
        testNeuralNetwork(loadedMlPerceptron, trainingSet);
        
    }
    
    
    public static void testNeuralNetwork(NeuralNetwork nnet, TrainingSet<SupervisedTrainingElement> tset) {
    	
    	for(int i=0; i<tset.size(); i++) {
    		TrainingElement trainingElement = tset.elementAt(i);
    		nnet.setInput(trainingElement.getInput());
    		nnet.calculate();
    		double[] networkOutput = nnet.getOutput();
    		System.out.print("Input:" + arrayToString(trainingElement.getInput()) + " Trained Output:" + desiredOutput.get(i));
    		System.out.println(" Output:" + arrayToString(networkOutput)); 
    	}
    }
    
    public static String arrayToString(double[] array) { 
    	String str= "";
    	for (int i = 0; i < array.length; i++)
    		str += array[i]+" ";
    	return str;
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

		// put values for each type seen
		for (int i = 0; i < values.size(); i++) {
			valueNum.add(i, String.valueOf(typeValues.get(values.get(i))));
		}
        
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
		// TODO Auto-generated method stub
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

}