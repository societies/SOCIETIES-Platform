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
package org.societies.context.community.estimation.impl;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Hashtable;

import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 */
@Service("communityCtxEstimation")
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{
	/**
	 * The CommunityContextEstimation class contains the methods to be called in order to estimate the community context.
	 * It has four types of methods. These that contain the letters "Num" in their name and deal with numeric attributes,
	 * these that contain the letters "Geom" in their name and deal with geometric attributes (e.g. location),
	 * these containing the letters "Special" and deal with other attributes and these containing the letters "String" in 
	 * their name that deal with string attributes
	 */
	

	public CommunityContextEstimation() {
		// TODO Auto-generated constructor stub

	}
	
	@Autowired(required = true)
	private ICtxBroker ctxBroker = null;

	private CtxEntityIdentifier comId;
	private String entityType;
	private String attributeType;

	@Override
	public double cceNumMean(ArrayList<Integer> inputValuesList) {
		/**
		 * Returns the mean value of an integers' ArrayList 
		 * @param an array list of integers
		 * @return a double as the mean value of the input integers
		 * 
		 */
		Assert.notEmpty(inputValuesList,"Cannot use estimation without attributes");
		int total = 0; 

		for (Integer i : inputValuesList){
			total = total + inputValuesList.get(i);
		}		
		double res = total/inputValuesList.size();
		return res;
	}

	@Override
	public double cceNumMedian(ArrayList<Integer> inputValuesList) {
		/**
		 * Returns the median of an integers' ArrayList
		 * @param an array list of integers
		 * @return a double as the median value of the input integers
		 */
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

	@Override
	public ArrayList<Integer> cceNumMode(ArrayList<Integer> inputValuesList) {
		/**
		 * Returns the mode of an integer's ArrayList
		 * @param an array list of integers
		 * @return an array representing the mode value of the input integers
		 */
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

	@Override
	public Integer[] cceNumRange(ArrayList<Integer> inputValuesList) {
		/**
		 * Returns the range of an integers' ArrayList
		 * @param an array list of integers
		 * @return the range of the input integers as Integer[]
		 */		
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

	@Override
	public ArrayList<Point> cceGeomConvexHull(ArrayList<Point> points) {
		/**
		 * Returns the convex hull of a points' ArrayList. It recursively uses the singleSideHulSet method
		 * @param an array list of points.
		 * @return the convex hull of the input points
		 */		
		ArrayList<Point> convexHullSet = new ArrayList<Point>();
		int minX= Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minPointIndex = -1;
		int maxPointIndex = -1;
		ArrayList<Point> leftPointsSet = new ArrayList<Point>();
		ArrayList<Point> rightPointsSet = new ArrayList<Point>();

		if (points.size()<3){
			return points;
		}
		
		for (int i=0; i<points.size(); ++i){
			if (points.get(i).x < minX){
				minX=points.get(i).x;
				minPointIndex = i;
			}
			if (points.get(i).x > maxX){
				maxX=points.get(i).x;
				maxPointIndex =i;
			}
		}
		
		Point minP = points.get(minPointIndex);
		Point maxP = points.get(maxPointIndex);	
		Point p = new Point();
		convexHullSet.add(minP);
		convexHullSet.add(maxP);
		points.remove(minP);
		points.remove(maxP);
		
		for (int i=0; i<points.size(); ++i){
			p = points.get(i);
			int crossProduct = (maxP.x-minP.x)*(p.y-minP.y) - (maxP.y-minP.y)*(p.x-minP.x);
			if (crossProduct>0){
				leftPointsSet.add(p);
			}
			else rightPointsSet.add(p);
		}
		
		singleSideHullSet(leftPointsSet,minP,maxP,convexHullSet);
		singleSideHullSet(rightPointsSet,maxP,minP,convexHullSet);
		return convexHullSet;
	}


	private void singleSideHullSet(ArrayList<Point> pointsSet, Point minPoint,
			Point maxPoint, ArrayList<Point> convexHullSet) {
	/**
	 * This method finds the points of the given pointsSet, that belong to convex hull and adds them to the given convexHull set. 
	 * It constructs a segment with the points minPoint and maxPoint and calculates if the points belonging to the pointsSet and are at the left of the segment
	 * belong to the convexHull set
	 * @param minPoint, maxPoint the two points that construct the segment
	 * @param pointsSet a set of points that are lying at the left of the segment (minPoint,maxPoint)
	 * @param convexHullSet the set that contains the points belonging to the convex hull
	 */
		
		Point fP = new Point();
		Point rP = new Point();

		int distance_max = Integer.MIN_VALUE;
		int relativeDistance = 0;
		int farthestPointIndex = -1;
		int insertPosition = convexHullSet.indexOf(maxPoint);

		ArrayList<Point> set1 = new ArrayList<Point>();
		ArrayList<Point> set2 = new ArrayList<Point>();		
		
		if (pointsSet.size()==0){
			return ;
		}
		if (pointsSet.size()==1){
			Point p = pointsSet.get(0);
			pointsSet.remove(p);
			convexHullSet.add(insertPosition, p);
			return;
		}

		for (int i=0; i<pointsSet.size(); i++){	
			Point m =pointsSet.get(i);				
			relativeDistance=(maxPoint.x-minPoint.x)*(minPoint.y-m.y)-(maxPoint.y-minPoint.y)*(minPoint.x-m.x);
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
			int crossProduct = (fP.x-minPoint.x)*(rP.y-minPoint.y) - (fP.y-minPoint.y)*(rP.x-minPoint.x);
			if (crossProduct >= 0){
				set1.add(rP);
			}
		}
			
		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			int crossProduct = (maxPoint.x-fP.x)*(rP.y-fP.y) - (maxPoint.y-fP.y)*(rP.x-fP.x);
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

	@Override
	public Point[] cceGeomMinBB(ArrayList<Point> points) {
		/**
		 * Returns the minimum boundary box that contains all the given points
		 * @param an array list of integers
		 * @return the minimum boundary box of the input points
		 */
		Point[] minBB = new Point[2];
		int minX= Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minY= Integer.MAX_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int i=0; i<points.size(); ++i){
			if (points.get(i).x < minX){
				minX=points.get(i).x;
			}
			if (points.get(i).x > maxX){
				maxX=points.get(i).x;
			}
			if (points.get(i).y < minY){
				minY=points.get(i).y;
			}
			if (points.get(i).y > maxY){
				maxY=points.get(i).y;
			}
		}

		Point topLeft = new Point(minX,maxY);
		Point bottomRight = new Point(maxX,minY);

		minBB[0]=topLeft;
		minBB[1]=bottomRight;
		return minBB;      
	}
	
	@Override
	public ArrayList<String> cceStringMode(ArrayList<String> inputValuesList) {
		/**
		 * Returns the range of a strings' ArrayList
		 * @param an array list of strings
		 * @return the range of the input integers as ArrayList<String>
		 */	
		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		ArrayList<String> finalList = new ArrayList<String>();

		ArrayList<String> mode = new ArrayList<String>();
		int max=0;

		for (int i=0; i<inputValuesList.size(); i++){
			if (finalList.contains(inputValuesList.get(i))){
				int elementCount = frequencyMap.get(inputValuesList.get(i));
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
			if (frequencyMap.get(e)==max){
				mode.add(e.toString());
			}
		}
		return mode;
	}


	@Override
	public void cceSpecial1() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cceSpecial2() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void cceSpecial3() {
		// TODO Auto-generated method stub
		
	}
	
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}


	public CtxEntityIdentifier getComId() {
		return comId;
	}


	public void setComId(CtxEntityIdentifier comId) {
		this.comId = comId;
	}


	public String getEntityType() {
		return entityType;
	}


	public void setEntityType(String entityType) {
		this.entityType = entityType;
	}


	public String getAttributeType() {
		return attributeType;
	}


	public void setAttributeType(String attributeType) {
		this.attributeType = attributeType;
	}


}