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
package org.societies.context.community.prediction.impl;

import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;

import org.societies.context.api.community.prediction.ICommunityCtxPredictionMgr;
import org.springframework.util.Assert;

public class CommunityContextPrediction implements ICommunityCtxPredictionMgr{
	
	/*
	 * Returns the mean value of an integers' ArrayList 
	 * @param an array list of integers
	 * @return a double as the mean value of the input integers
	 * 
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputValuesList must come from User Context Prediction!
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
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputValuesList must come from User Context Prediction!
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
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputValuesList must come from User Context Prediction!
	 * 
	 */
	public ArrayList<Integer> ccpNumMode(ArrayList<Integer> inputValuesList) {

		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
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
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputValuesList must come from User Context Prediction!
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
	 * 
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) points must come from User Context Prediction!
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
	
	/*
	 * Returns the minimum bounding box that contains all the given points
	 * @param an array list of integers
	 * @return an array of points representing the minimum bounding box of the input points
	 * 
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) points must come from User Context Prediction!
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
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputValuesList must come from User Context Prediction!
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
	
	/* Edo den eixe sxolia ... giati???
	 * Einai test code? An nai, prepei na ginoun diorthoseis kai edo kai sto cce ...
	 * 
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputListOfStrings must come from User Context Prediction!
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
	 * Differences from Community Context Estimation:
	 * 1) "ccp..." instead of "cce..." (stands for community context prediction)
	 * 2) inputListOfStrings must come from User Context Prediction!
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
			Enumeration<String> e = frequencyMap.keys();
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
	
	
	
	
	@Override
	public void getCommunity(EntityIdentifier cisID) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see org.societies.context.api.community.prediction.ICommunityCtxPredictionMgr#predictContext(org.societies.api.context.model.CtxAttributeIdentifier, java.util.Date)
	 */
	@Override
	public CtxIdentifier predictContext(CtxAttributeIdentifier arg0, Date arg1) {
		// TODO Auto-generated method stub
		return null;
	}
}