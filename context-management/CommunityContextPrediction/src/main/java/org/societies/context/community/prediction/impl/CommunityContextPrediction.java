/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druΞ•Ξ�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAΞ“β€΅Ξ“Ζ’O, SA (PTIN), IBM Corp., 
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
package org.societies.context.community.prediction.impl;
//package org.societies.context.community.prediction.impl;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeComplexValue;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.context.api.community.prediction.ICommunityCtxPredictionMgr;
import org.societies.context.api.user.prediction.IUserCtxPredictionMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Assert;

public class CommunityContextPrediction implements ICommunityCtxPredictionMgr {
	//It needs refinement!!

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityContextPrediction.class);

	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;

	@Autowired(required=false)
	private ICommManager commMngr;
	
	private IUserCtxPredictionMgr userCtxPrediction;

	public CommunityContextPrediction() {
		LOG.info(this.getClass() + "CommunityContextPrediction instantiated ");
		
	}

	/* It uses the appropriate ctxBroker method to retrieve the input from User Context Prediction. This method 
	 * is the retrieveFuture(CtxAttributeIdentifier attrId, Date date) with Date=null?
	 * 
	 */
	//@Override
	public CtxAttribute predictCommunityCtx(CtxEntityIdentifier communityCtxId, CtxAttributeIdentifier ctxAttributeIdentifier) throws InvalidFormatException {

		LOG.info("predictCommunityCtx 1");
		CtxAttribute communityAttr = null;

		// this values will be set in complexAttrType
		double meanPredictedIntegerValue = 0.0;

		ArrayList<Integer> integerAttrValues = new ArrayList<Integer>();
		ArrayList<String> stringAttrValues = new ArrayList<String>();
		ArrayList<Double> doubleAttrValues = new ArrayList<Double>();

		ArrayList<String> finalArrayStringList = new ArrayList<String>();
		ArrayList<String> modeStringValue = new ArrayList<String>();

		List<CtxAttributeValueType> valueTypesStringIntegerDouble = new ArrayList<CtxAttributeValueType>();
		valueTypesStringIntegerDouble.add(CtxAttributeValueType.STRING);
		valueTypesStringIntegerDouble.add(CtxAttributeValueType.INTEGER);
		valueTypesStringIntegerDouble.add(CtxAttributeValueType.DOUBLE);		

		List<CtxAttributeValueType> valueTypesString = new ArrayList<CtxAttributeValueType>();
		valueTypesString.add(CtxAttributeValueType.STRING);

		List<CtxAttributeValueType> valueTypesIntegerString = new ArrayList<CtxAttributeValueType>();
		valueTypesIntegerString.add(CtxAttributeValueType.INTEGER);
		valueTypesIntegerString.add(CtxAttributeValueType.STRING);

		Map<String,List<CtxAttributeValueType>> possibleValueTypes = new HashMap<String,List<CtxAttributeValueType>>();
		possibleValueTypes.put(CtxAttributeTypes.TEMPERATURE, valueTypesStringIntegerDouble);
		possibleValueTypes.put(CtxAttributeTypes.AGE, valueTypesStringIntegerDouble);	
		possibleValueTypes.put(CtxAttributeTypes.INTERESTS, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.LANGUAGES, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.LOCATION_COORDINATES, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.OCCUPATION, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.LOCATION_SYMBOLIC, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.BOOKS, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.FAVOURITE_QUOTES, valueTypesString);
		possibleValueTypes.put(CtxAttributeTypes.MOVIES, valueTypesString);

		// TODO add all data types and values
		// resolve issue with different value types for the same attribute type e.g. hot vs 32C

		Set<String> attributeTypesSetToBeChecked = new HashSet<String>();
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.TEMPERATURE);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.INTERESTS);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.AGE);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.LANGUAGES);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.LOCATION_COORDINATES);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.OCCUPATION);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.LOCATION_SYMBOLIC);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.BOOKS);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.FAVOURITE_QUOTES);
		attributeTypesSetToBeChecked.add(CtxAttributeTypes.MOVIES);

		CtxAttributeComplexValue complexValue = new CtxAttributeComplexValue();

		try {

			String ownerId = commMngr.getIdManager().getThisNetworkNode().getBareJid();
			IIdentity identityOfOwner = commMngr.getIdManager().fromJid(ownerId);
			Requestor requestorCSS = new Requestor(identityOfOwner);

			communityAttr = (CtxAttribute) internalCtxBroker.retrieveAttribute(ctxAttributeIdentifier, false).get();

			if (communityAttr!=null){

				String attributeType = ctxAttributeIdentifier.getType().toString();

				// checks if attribute type is included in the list of types that can be estimated

				if(attributeTypesSetToBeChecked.contains(attributeType)){

					//List<CtxHistoryAttribute> retrievedCommunityHistoryAtributes = internalCtxBroker.retrieveHistory(requestorCSS, ctxAttributeIdentifier, null, null).get();
					//Set<CtxEntityIdentifier> communityMembers = ((CommunityCtxEntity) retrievedCommunityHistoryAtributes).getMembers();
					//List<CtxAttribute> retrievedCommunityAtributes = internalCtxBroker.retrieveFuture(requestorCSS, ctxAttributeIdentifier, null).get();
					//Set<CtxEntityIdentifier> communityMembers = ((CommunityCtxEntity) retrievedCommunityAtributes).getMembers();

					CommunityCtxEntity retrievedCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityCtxId).get();
					Set<CtxEntityIdentifier> communityMembers = retrievedCommunity.getMembers();
					List<CtxAttribute> listOfFutureCtxAttributes = new ArrayList<CtxAttribute>();

					for(CtxEntityIdentifier comMemb:communityMembers){
						IndividualCtxEntity individualMember = (IndividualCtxEntity) internalCtxBroker.retrieve(comMemb).get();
						Set<CtxAttribute> individualCSSAttributeList = individualMember.getAttributes(attributeType);	

						if (individualCSSAttributeList.size()==1){

							for (CtxAttribute ca:individualCSSAttributeList){
								CtxAttributeIdentifier ctxAttrId = ca.getId();

								//List<CtxAttribute> listOfFutureCtxAttribute = internalCtxBroker.retrieveFuture(requestorCSS, ctxAttrId, null).get();

								CtxAttribute predictedCtxAttribute = userCtxPrediction.predictContext(ctxAttrId, null);

								if (predictedCtxAttribute!=null){
									listOfFutureCtxAttributes.add(predictedCtxAttribute);
								}
							}

						}

						if (listOfFutureCtxAttributes.size()!=0 && listOfFutureCtxAttributes.get(0).getIntegerValue()!=null){
							for (CtxAttribute futAtt:listOfFutureCtxAttributes){
								integerAttrValues.add(futAtt.getIntegerValue());
							}

						if(listOfFutureCtxAttributes.size()!=0 && listOfFutureCtxAttributes.get(0).getStringValue()!= null){
							for (CtxAttribute futAtt:listOfFutureCtxAttributes){
									stringAttrValues.add(futAtt.getStringValue());
								}							}
						if(listOfFutureCtxAttributes.size()!=0 && listOfFutureCtxAttributes.get(0).getDoubleValue()!= null){
							for (CtxAttribute futAtt:listOfFutureCtxAttributes){
									doubleAttrValues.add(futAtt.getDoubleValue());
								}							
							}						
						}
						else{
							LOG.info("Cannot calculate the predicted community value");

						}
					}

					// Integer values
					// average, median, 
					if (!integerAttrValues.isEmpty()){
						LOG.info("predictCommunityCtx 4for integer" );
						//average
						meanPredictedIntegerValue = ccpNumMean(integerAttrValues);	
						complexValue.setAverage(meanPredictedIntegerValue);
						LOG.info("Mean Predicted Integer Value is :"+meanPredictedIntegerValue);

						// pairs
						LOG.info("Calculating Pairs");
						HashMap<String,Integer> pairs = new HashMap<String,Integer>();
						pairs = ccpStringPairs(stringAttrValues);
						LOG.info("PAIRS are :"+pairs.get(0));
						complexValue.setPairs(pairs);

						//range 
						LOG.info("Calculating Range ");
						Integer [] range = ccpNumRange(integerAttrValues);
						complexValue.setRangeMax(range[1]);
						complexValue.setRangeMin(range[0]);
						LOG.info("predictCommunityCtx 4 integer finished ");							

						//median
						LOG.info("Calculating Median");
						Double medianNumber = ccpNumMedian(integerAttrValues);
						LOG.info("The median is "+medianNumber);
						complexValue.setMedian(medianNumber);
						LOG.info("pewdictCommunityCtx 4 integer finished ");

						//mode
						LOG.info("Calculating Mode");
						//ArrayList<Integer> modeNumber = cceNumMode(integerAttrValues);
						//complexValue.setMode(integerAttrValues);
						ArrayList<Integer> modeNumber = ccpNumMode(integerAttrValues);
						complexValue.setMode(modeNumber);

						LOG.info("Calculating ConvexHull");
						//Converting the integers to Points2D
						ArrayList<String> finalStringArrayList = new ArrayList<String>();
						ArrayList<Point2D> cH = new ArrayList<Point2D>();

						for (String strPoint:stringAttrValues){
							String[] helperString = strPoint.split(",");
							for (String s1:helperString){
								finalStringArrayList.add(s1);
							}
						}
						cH = ccpGeomConvexHull(CommunityContextPrediction.splitString(finalArrayStringList.toString()));
						ArrayList<String> stringPoints = new ArrayList<String>();
						for (Point2D point:cH){
							stringPoints.add(point.toString());
						}
						complexValue.setLocationGPS(stringPoints.toString());
						//TODO add any other applicable

					}

					// calculate strings 
					if( !stringAttrValues.isEmpty()){

						for (String s: stringAttrValues){
							String[] helper = s.split(",");
							for (String s1:helper){
								finalArrayStringList.add(s1);
							}
						}	
						HashMap<String,Integer> occurences = new HashMap<String,Integer>();
						occurences = ccpStringPairs(finalArrayStringList);
						complexValue.setPairs(occurences);
					}

					// calculate double
					if(!doubleAttrValues.isEmpty()){
						//average
						// TODO add a method cceNumMean that will take array of doubles
						//range

						//median

						//mode
					}


					communityAttr.setComplexValue(complexValue);
				}

			}			

		} catch (InterruptedException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (ExecutionException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (CtxException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} 

		return communityAttr;
	}

	/*
	 * Returns the mean value of an integers' ArrayList 
	 * @param an array list of integers
	 * @return a double as the mean value of the input integers
	 *
	 * inputValuesList must come from User Context Prediction!
	 * 
	 */
	public double ccpNumMean(ArrayList<Integer> inputValuesList) {

		Assert.notEmpty(inputValuesList,"Cannot use prediction without attributes");
		int total = 0; 

		for (int i=0; i<inputValuesList.size(); i++) {
			total = total + inputValuesList.get(i);
		}		

		double res = (double)total/(double)inputValuesList.size();		

		return res;
	}

	/*
	 * Returns the median of an integers' ArrayList
	 * @param an array list of integers
	 * @return a double as the median value of the input integers
	 * 
	  inputValuesList must come from User Context Prediction!
	 * 
	 */
	//@Override
	public double ccpNumMedian(ArrayList<Integer> inputValuesList) {

		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		Integer med,med1,med2=0;
		Collections.sort(inputValuesList);

		if (inputValuesList.size()%2 == 1 ){
			med = inputValuesList.get((inputValuesList.size()-1)/2);	
		}
		else {
			med1 = inputValuesList.get((inputValuesList.size())/2-1);
			med2 = inputValuesList.get((inputValuesList.size())/2);
			med = (med1+med2)/2;
		}
		return med;	
	}

	/*
	 * Returns the mode of an integer's ArrayList
	 * @param an array list of integers
	 * @return an ArrayList of integers representing the mode value of the input integers
	 * 
	 * inputValuesList must come from User Context Prediction!
	 * 
	 */
	public ArrayList<Integer> ccpNumMode(ArrayList<Integer> inputValuesList) {

		//Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		Hashtable <Integer, Integer> frequencyMap = new Hashtable<Integer, Integer>();
		ArrayList<Integer> finalList = new ArrayList<Integer>();

		ArrayList<Integer> mode = new ArrayList<Integer>();
		int max=0;

		for (int i=0; i<inputValuesList.size(); i++){
			if (finalList.contains(inputValuesList.get(i))){
				int elementCount =frequencyMap.get(inputValuesList.get(i));
				elementCount++;
				frequencyMap.put(inputValuesList.get(i), elementCount);

				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				finalList.add(inputValuesList.get(i));
				frequencyMap.put(inputValuesList.get(i), 1);
			}	
		}
		Enumeration<Integer> e = frequencyMap.keys();
		while(e.hasMoreElements()){
			if (frequencyMap.get(e)==max){
				mode.add(Integer.parseInt(e.toString()));
			}
		}

		return mode;
	}

	/*
	 * Returns the range of an integers' ArrayList
	 * @param an array list of integers
	 * @return the range of the input integers as Integer[]
	 *
	 * inputValuesList must come from User Context Prediction!
	 * 
	 */
	public Integer[] ccpNumRange(ArrayList<Integer> inputValuesList) {

		Integer[] r = new Integer[2];

		Integer min= Integer.MAX_VALUE;
		Integer max = Integer.MIN_VALUE;

		for (int i=0; i<inputValuesList.size(); ++i){
			if (inputValuesList.get(i) < min){
				min=inputValuesList.get(i);
			}
			if (inputValuesList.get(i) > max){
				max=inputValuesList.get(i);
			}
		}
		r[0]=min;
		r[1]=max;
		return r;
	}	

	/*
	 * Returns the convex hull of a points' ArrayList. It recursively uses the singleSideHulSet method
	 * @param an array list of points.
	 * @return an ArrayList of points, representing the convex hull set of the input points
	 * points must come from User Context Prediction!
	 * 
	 */
	public ArrayList<Point2D> ccpGeomConvexHull(ArrayList<Point2D> points) {

		ArrayList<Point2D> convexHullSet = new ArrayList<Point2D>();
		double minX= Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		int minPointIndex = -1;
		int maxPointIndex = -1;
		ArrayList<Point2D> leftPointsSet = new ArrayList<Point2D>();
		ArrayList<Point2D> rightPointsSet = new ArrayList<Point2D>();

		if (points.size()<3){
			return points;
		}

		for (int i=0; i<points.size(); ++i){
			if (points.get(i).getX() < minX){
				minX=points.get(i).getX();
				minPointIndex = i;
			}
			if (points.get(i).getX() > maxX){
				maxX=points.get(i).getX();
				maxPointIndex =i;
			}
		}

		Point2D minP = points.get(minPointIndex);
		Point2D maxP = points.get(maxPointIndex);	
		//Point2D p = new Point2D();
		convexHullSet.add(minP);
		convexHullSet.add(maxP);
		points.remove(minP);
		points.remove(maxP);

		for (int i=0; i<points.size(); ++i){
			Point2D p = points.get(i);
			double crossProduct = (maxP.getX()-minP.getY())*(p.getY()-minP.getY()) - (maxP.getY()-minP.getY())*(p.getX()-minP.getX());
			if (crossProduct>0){
				leftPointsSet.add(p);
			}
			else rightPointsSet.add(p);
		}

		singleSideHullSet(leftPointsSet,minP,maxP,convexHullSet);
		singleSideHullSet(rightPointsSet,maxP,minP,convexHullSet);
		return convexHullSet;
	}


	private void singleSideHullSet(ArrayList<Point2D> pointsSet, Point2D minPoint,
			Point2D maxPoint, ArrayList<Point2D> convexHullSet) {


		Point2D fP = new Point();
		Point2D rP = new Point();

		double distance_max = Integer.MIN_VALUE;
		double relativeDistance = 0;
		int farthestPointIndex = -1;
		int insertPosition = convexHullSet.indexOf(maxPoint);

		ArrayList<Point2D> set1 = new ArrayList<Point2D>();
		ArrayList<Point2D> set2 = new ArrayList<Point2D>();	

		if (pointsSet.size()==0){
			return ;
		}
		if (pointsSet.size()==1){
			Point2D p = pointsSet.get(0);
			pointsSet.remove(p);
			convexHullSet.add(insertPosition, p);
			return;
		}

		for (int i=0; i<pointsSet.size(); i++){	
			Point2D m =pointsSet.get(i);	
			relativeDistance=(maxPoint.getX()-minPoint.getX())*(minPoint.getY()-m.getY())-(maxPoint.getY()-minPoint.getY())*(minPoint.getX()-m.getX());
			if (relativeDistance < 0){
				relativeDistance= -relativeDistance;
			}

			if (relativeDistance > distance_max){	
				distance_max =relativeDistance;
				farthestPointIndex=i;	
			}	
		}

		fP=pointsSet.get(farthestPointIndex);
		convexHullSet.add(insertPosition,fP);
		pointsSet.remove(farthestPointIndex);

		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			double crossProduct = (fP.getX()-minPoint.getX())*(rP.getY()-minPoint.getY()) - (fP.getY()-minPoint.getY())*(rP.getX()-minPoint.getX());
			if (crossProduct >= 0){
				set1.add(rP);
			}
		}

		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			double crossProduct = (maxPoint.getX()-fP.getX())*(rP.getY()-fP.getY()) - (maxPoint.getY()-fP.getY())*(rP.getX()-fP.getX());
			if (crossProduct >= 0){
				set2.add(rP);	
			}
		}
		if (set1.size()!=0){
			singleSideHullSet(set1, minPoint, fP, convexHullSet);
		}
		if (set2.size()!=0){
			singleSideHullSet(set2,fP,maxPoint,convexHullSet);
		}

	}

	/*
	 * Returns the minimum bounding box that contains all the given points
	 * @param an array list of integers
	 * @return an array of points representing the minimum bounding box of the input points
	 * 
	 *points must come from User Context Prediction!
	 * 
	 */
	public Point2D[] ccpGeomMinBB(ArrayList<Point2D> points) {

		Point2D[] minBB = new Point2D[2];
		double minX= Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		double minY= Integer.MAX_VALUE;
		double maxY = Integer.MIN_VALUE;

		for (int i=0; i<points.size(); ++i){
			if (points.get(i).getX() < minX){
				minX=points.get(i).getX();
			}
			if (points.get(i).getX() > maxX){
				maxX=points.get(i).getX();
			}
			if (points.get(i).getY() < minY){
				minY=points.get(i).getY();
			}
			if (points.get(i).getY() > maxY){
				maxY=points.get(i).getY();
			}
		}

		//Point2D topLeft = new Point2D(minX,maxY);
		//Point2D bottomRight = new Point2D(maxX,minY);

		Point2D topLeft = null;
		minBB[0]=topLeft;
		Point2D bottomRight = null;
		minBB[1]=bottomRight;
		return minBB;      
	}
	/*
	 * Returns the range of a strings' ArrayList
	 * @param an array list of strings
	 * @return an ArrayList of strings showing the mode of the input strings
	 * 
	 * inputValuesList must come from User Context Prediction!
	 * 
	 */
	public ArrayList<String> ccpStringMode(ArrayList<String> inputValuesList) {

		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		ArrayList<String> finalList = new ArrayList<String>();

		ArrayList<String> mode = new ArrayList<String>();
		int max=0;
		for (int i=0; i<inputValuesList.size(); i++){
			if (finalList.contains(inputValuesList.get(i))){
				int elementCount = Integer.parseInt(frequencyMap.get(inputValuesList.get(i)).toString());
				elementCount++;
				frequencyMap.put(inputValuesList.get(i), elementCount);				
				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				finalList.add(inputValuesList.get(i));
				frequencyMap.put(inputValuesList.get(i), 1);
			}	
		}

		Enumeration<String> e = frequencyMap.keys();

		while(e.hasMoreElements()){
			String curString = e.nextElement();
			if (frequencyMap.get(curString)==max){
				mode.add(curString.toString());
			}
		}

		return mode;
	}

	//method for splitting the LOCATION string. Location should be in String representation of a pair of double values
	public static ArrayList<Point2D> splitString (String s){

		Point2D.Double p = new Point2D.Double();
		double x=0.0;
		double y=0.0;
		int l = 0;
		ArrayList<Point2D> points = new ArrayList<Point2D>();
		String[] splited_string = s.split(",");

		System.out.println("The size of splitted string is "+splited_string.length);

		for (int k = 0; k< splited_string.length -1; k++){
			x = Double.parseDouble(splited_string[l]);
			y = Double.parseDouble(splited_string[l+1]);
			l=l+2;
			p.setLocation(x, y);
			points.add(p);
			//returns the point with the coordinates
		}
		return points;	

	}

	/* 
	 * @param ArrayList<String> 
	 * @return a frequency map 
	 * 
	 */
	public HashMap<String,Integer> ccpStringPairs(ArrayList<String> inputListOfStrings) {

		HashMap <String, Integer> frequencyMap = new HashMap<String, Integer>();
		ArrayList<String> outputList = new ArrayList<String>();
		ArrayList<String> arrayListWithStringPercent = new ArrayList<String>();

		int max=0;
		for (int i=0; i<inputListOfStrings.size(); i++){
			if (outputList.contains(inputListOfStrings.get(i))){
				int elementCount = Integer.parseInt(frequencyMap.get(inputListOfStrings.get(i)).toString());
				elementCount++;
				frequencyMap.put(inputListOfStrings.get(i), elementCount);				
				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				outputList.add(inputListOfStrings.get(i));
				frequencyMap.put(inputListOfStrings.get(i), 1);
			}	

		}

		return frequencyMap;
	}

	/*
	 * @param an array list of strings
	 * @return an array list of strings of type [abd, 57%, abc, 14%, cde, 28%]
	 * 
	 * 
	 */
	public ArrayList<String> ccpStringPercentage(ArrayList<String> inputListOfStrings) {
		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		ArrayList<String> outputList = new ArrayList<String>();
		ArrayList<String> arrayListWithStringPercent = new ArrayList<String>();

		int max=0;
		for (int i=0; i<inputListOfStrings.size(); i++){
			if (outputList.contains(inputListOfStrings.get(i))){
				int elementCount = Integer.parseInt(frequencyMap.get(inputListOfStrings.get(i)).toString());
				elementCount++;
				frequencyMap.put(inputListOfStrings.get(i), elementCount);				
				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				outputList.add(inputListOfStrings.get(i));
				frequencyMap.put(inputListOfStrings.get(i), 1);
			}	
			int total=0;
			//Enumeration<String> e = frequencyMap.keys();
			Iterator<Integer> it = frequencyMap.values().iterator();

			while (it.hasNext()){
				Integer key = it.next();
				total = total+key;
			}

			Hashtable<String, Integer> hashTabletWithPercentage =  new Hashtable<String, Integer>();

			Enumeration<String> keys = frequencyMap.keys();

			while (keys.hasMoreElements()){
				Object k = keys.nextElement();
				System.out.println("Key = "+k+" Value = "+frequencyMap.get(k));
				hashTabletWithPercentage.put(k.toString(), 100*frequencyMap.get(k)/total);
				arrayListWithStringPercent.add(k.toString());
				int help = 100*frequencyMap.get(k)/total;
				arrayListWithStringPercent.add(String.valueOf(help)+"%");
			}

		}

		return arrayListWithStringPercent;

	}
	/*
	 * predicts the next community coordinates location, based on the previous and current location
	 * @param communityAttrId
	 * @return CtxAttribute the predicted context attribute
	 */
	public CtxAttribute predictCommunityNextLocationCoordinates(CtxAttributeIdentifier communityAttrId) throws InvalidFormatException, InterruptedException, ExecutionException, CtxException {
		//retrieve previous location from historyofContext db

		CtxHistoryAttribute freshAttribute =null;
		CtxAttribute returnAttribute = null;

		Double xp = null;
		Double yp = null;
		Double xc = null;
		Double yc = null;

		String ownerJid = commMngr.getIdManager().getThisNetworkNode().getBareJid();
		IIdentity ownerIdentiy = commMngr.getIdManager().fromJid(ownerJid);
		Requestor requestor = new Requestor(ownerIdentiy);

		List<CtxHistoryAttribute> communityHistoryAttribute = internalCtxBroker.retrieveHistory(requestor, communityAttrId, null, null).get();

		if (communityHistoryAttribute.size()!=0){
			Date fresher = communityHistoryAttribute.get(0).getLastUpdated();
			for (CtxHistoryAttribute ctxHA:communityHistoryAttribute){

				if (fresher.before(ctxHA.getLastUpdated()))
				{
					fresher = ctxHA.getLastUpdated();
					freshAttribute = ctxHA;
				}	
			}			
			//retrieve latest position
			String coordinatesString = freshAttribute.getStringValue();
			List<String> listCoordinates = Arrays.asList(coordinatesString);

			xp = (Double.parseDouble(listCoordinates.get(0)));
			yp = (Double.parseDouble(listCoordinates.get(1)));
		}

		//retrieve current coordinates of the Community
		CommunityCtxEntity currentCommunity = (CommunityCtxEntity) internalCtxBroker.retrieve(communityAttrId).get();
		Set<CtxAttribute> currentGPSLocation = currentCommunity.getAttributes(CtxAttributeTypes.LOCATION_COORDINATES);

		if (currentGPSLocation.size()==1){
			for (CtxAttribute ctxAtt: currentGPSLocation){
				List<String> listCurrentPointsCoordinates = Arrays.asList(ctxAtt.getStringValue());
				xc = (Double.parseDouble(listCurrentPointsCoordinates.get(0)));
				yc = (Double.parseDouble(listCurrentPointsCoordinates.get(1)));	
			} 
		}
		//calculate the next position using vectors
		Double	xf = (xc + (xc-xp));
		Double	yf = (yc + (yc-yp));

		String stringToUpdate = xf.toString()+yf.toString();

		//update the future attribute
		returnAttribute.setStringValue(stringToUpdate);					
		return returnAttribute;
	}



	@Override
	public CtxAttribute predictContext(CtxAttributeIdentifier attrID, Date date) {
				if (attrID == null) {
			throw new NullPointerException("attribute Id can't be null");
		}

		CtxAttribute result = null;
		
		try {
			result = this.predictCommunityCtx(attrID.getScope(),attrID);
		
		} catch (Exception e) {
			LOG.error("Exception while performing community ctx prediction for ctx attribute id:"+ attrID+"."+ e.getLocalizedMessage());
			e.printStackTrace();
		}		
		return result;
	}



	@Override
	public void getCommunity(IIdentity arg0) {
		// TODO Auto-generated method stub

	}
}