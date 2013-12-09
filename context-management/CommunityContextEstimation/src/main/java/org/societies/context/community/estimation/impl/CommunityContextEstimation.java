/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske druzbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVACAO, SA (PTIN), IBM Corp., 
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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CommunityCtxEntity;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxAttributeComplexValue;
import org.societies.api.context.model.CtxOriginType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.societies.context.api.community.estimation.estimationModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * The CommunityContextEstimation class contains the methods to be called in order to estimate the community context.
 * It has four types of methods. These that contain the letters "Num" in their name and deal with numeric attributes,
 * these that contain the letters "Geom" in their name and deal with geometric attributes (e.g. location),
 * these containing the letters "Special" and deal with other attributes and these containing the letters "String" in 
 * their name that deal with string attributes
 * 
 * @author yboul 07-Dec-2011 4:15:14 PM
 */
@Service
@Lazy(true)
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(CommunityContextEstimation.class);
	
	// TODO add all data types and values
	/** The list of types that can be estimated. */
	private static final List<String> INFERRABLE_TYPES = Collections.unmodifiableList(
			Arrays.asList(
					CtxAttributeTypes.TEMPERATURE,
					CtxAttributeTypes.INTERESTS,
					CtxAttributeTypes.AGE,
					CtxAttributeTypes.LANGUAGES,
					CtxAttributeTypes.LOCATION_COORDINATES,
					CtxAttributeTypes.OCCUPATION,
					CtxAttributeTypes.LOCATION_SYMBOLIC,
					CtxAttributeTypes.BOOKS,
					CtxAttributeTypes.FAVOURITE_QUOTES,
					CtxAttributeTypes.MOVIES
			));
	
	/** The time in ms to wait for responses from member CSSs. */
	private static final long RESPONSE_TIMEOUT = 1500l;

	@Autowired(required=false)
	private ICtxBroker internalCtxBroker;
	
	@Autowired(required=true)
	private ICommManager commMgr;

	public CommunityContextEstimation() {

		LOG.info("{} CommunityContextEstimation instantiated", this.getClass());
	}

	/*
	 * @see org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr#estimateCommunityCtx(org.societies.api.context.model.CtxEntityIdentifier, org.societies.api.context.model.CtxAttributeIdentifier, org.societies.context.api.community.estimation.estimationModel)
	 */
	@Override
	public CtxAttribute estimateCommunityCtx(CtxEntityIdentifier ctxId,
			CtxAttributeIdentifier ctxAttributeIdentifier, estimationModel model) {
		// TODO Auto-generated method stub

		switch (model) {
		case mean:

			break;

		case median:
			break;

		case mode:
			break;

		case range:
			break;

		case minBB:
			break;

		case stringMode:
			break;

		case convexHull:
			break;

		default:
			break;
		}

		return null;
	}


	/*
	 * temp example method utilizing CtxAttributeComplexValue class
	 */
	@Override
	public CtxAttribute estimateCommunityCtx(CtxEntityIdentifier communityCtxId, CtxAttributeIdentifier ctxAttributeIdentifier) {
		
		LOG.debug("estimateCommunityCtx: ctxAttributeIdentifier={}", ctxAttributeIdentifier);
		
		if (ctxAttributeIdentifier == null) {
			throw new NullPointerException("ctxAttributeIdentifier can't be null");
		}
		
		// checks if attribute type is included in the list of types that can be estimated
		if (!INFERRABLE_TYPES.contains(ctxAttributeIdentifier.getType())) {
			LOG.warn("Type '{}' can't be inferred", ctxAttributeIdentifier.getType());
			return null;
		}

		CtxAttribute communityAttr = null;

		// this values will be set in complexAttrType
		double meanIntegerValue = 0.0;

		final List<Integer> integerAttrValues = new ArrayList<Integer>();
		final List<String> stringAttrValues = new ArrayList<String>();
		final List<Double> doubleAttrValues = new ArrayList<Double>();

		final List<String> finalArrayStringList = new ArrayList<String>();
		/* TODO
		List<String> modeStringValue = new ArrayList<String>();

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
		possibleValueTypes.put(CtxAttributeTypes.MOVIES, valueTypesString);*/

		// resolve issue with different value types for the same attribute type e.g. hot vs 32C

		CtxAttributeComplexValue complexValue = new CtxAttributeComplexValue();

		try {
			final Requestor localRequestor = this.createLocalRequestor();
			final RequestorCis requestorCis = this.createRequestorCis(localRequestor, ctxAttributeIdentifier);
			LOG.debug("estimateCommunityCtx: localRequestor={}, requestorCis={}", localRequestor, requestorCis);
			// TODO check if CtxAttribute is null
			communityAttr = (CtxAttribute) internalCtxBroker.retrieveAttribute(ctxAttributeIdentifier, false).get();
			String attributeType = ctxAttributeIdentifier.getType();

			final CommunityCtxEntity retrievedCommunity = (CommunityCtxEntity) 
					this.internalCtxBroker.retrieve(communityCtxId).get();
			// TODO check if CommunityCtxEntity is null
			// The map of future results from CIS members
			final Map<CtxEntityIdentifier, Future<List<CtxIdentifier>>> futureMemberAttrIdMap = 
					new HashMap<CtxEntityIdentifier, Future<List<CtxIdentifier>>>(retrievedCommunity.getMembers().size());
			// Lookup member attributes (async)
			for (final CtxEntityIdentifier communityMemberEntId : retrievedCommunity.getMembers()) {
				try {
					final Requestor requestor;
					if (communityMemberEntId.getOwnerId().equals(localRequestor.getRequestorId().getBareJid())) {
						requestor = localRequestor;
					} else {
						requestor = requestorCis;
					}
					final Future<List<CtxIdentifier>> futureMemberAttrIdList = this.internalCtxBroker.lookup(
							requestor, communityMemberEntId, CtxModelType.ATTRIBUTE, attributeType);
					futureMemberAttrIdMap.put(communityMemberEntId, futureMemberAttrIdList);
				} catch (Exception e) {
					LOG.warn("Could not lookup '{}' attribute from member CSS '{}': {}",
							new Object[] { attributeType, communityMemberEntId, e.getLocalizedMessage() });
				}
			}
			
			final List<CtxIdentifier> memberAttrIdList = new ArrayList<CtxIdentifier>(futureMemberAttrIdMap.size());
			for (final Map.Entry<CtxEntityIdentifier, Future<List<CtxIdentifier>>> futureMemberAttrIdList : futureMemberAttrIdMap.entrySet()) {
				try {
					final List<CtxIdentifier> memberAttrIds =
							futureMemberAttrIdList.getValue().get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS); 
					if (!memberAttrIds.isEmpty()) {
						memberAttrIdList.add(memberAttrIds.get(0));
					} else {
						LOG.debug("'{}' attribute from member CSS '{}' not found",
								attributeType, futureMemberAttrIdList.getKey());
					}
				} catch (TimeoutException te) {
					LOG.warn("Could not lookup '{}' attribute from member CSS '{}': Operation timed out",
							attributeType, futureMemberAttrIdList.getKey());
				} catch (ExecutionException ee) {
					LOG.warn("Could not lookup '{}' attribute from member CSS '{}': {}",
							new Object[] { attributeType, futureMemberAttrIdList.getKey(), 
							               ee.getLocalizedMessage()});
				}
			}
			
			// The map of future results from CIS members
			final Map<CtxIdentifier, Future<CtxModelObject>> futureMemberAttrList =
					new HashMap<CtxIdentifier, Future<CtxModelObject>>(memberAttrIdList.size());
			// Retrieve member attributes (async)
			for (final CtxIdentifier memberAttrId : memberAttrIdList) {
				try {
					final Requestor requestor;
					if (memberAttrId.getOwnerId().equals(localRequestor.getRequestorId().getBareJid())) {
						requestor = localRequestor;
					} else {
						requestor = requestorCis;
					}
					final Future<CtxModelObject> futureMemberAttr = 
							this.internalCtxBroker.retrieve(requestor, memberAttrId);
					futureMemberAttrList.put(memberAttrId, futureMemberAttr);
				} catch (Exception e) {
					LOG.warn("Could not retrieve member attribute '{}': {}",
							memberAttrId, e.getLocalizedMessage());
				}
			}
			
			for (final Map.Entry<CtxIdentifier, Future<CtxModelObject>> futureMemberAttr : futureMemberAttrList.entrySet()) {
				try {
					LOG.debug("estimateCommunityCtx: Retrieving user attribute with ID '{}'",
							futureMemberAttr.getKey());
					final CtxAttribute attribute = (CtxAttribute)
							futureMemberAttr.getValue().get(RESPONSE_TIMEOUT, TimeUnit.MILLISECONDS); 
					if (null != attribute) {
						LOG.debug("estimateCommunityCtx: Retrieved user attribute '{}'", attribute);
						if (attribute.getIntegerValue() != null){
							integerAttrValues.add(attribute.getIntegerValue());
						}
						if (attribute.getStringValue() != null){
							stringAttrValues.add(attribute.getStringValue());
						}
						if (attribute.getDoubleValue() != null){
							doubleAttrValues.add(attribute.getDoubleValue());
						}
					} else {
						LOG.warn("Retrieved null result for attribute with ID '{}'",
								futureMemberAttr.getKey());
					}
				} catch (TimeoutException te) {
					LOG.warn("Could not retrieve attribute '{}': Operation timed out",
							futureMemberAttr.getKey());
				} catch (ExecutionException ee) {
					LOG.warn("Could not retrieve attribute '{}': {}",
							futureMemberAttr.getKey(), ee.getLocalizedMessage());
				}
			}

			// Integer values
			// average, median, 
			if( !integerAttrValues.isEmpty()){
				LOG.debug("estimateCommunityCtx for integer" );
				//average
				meanIntegerValue = cceNumMean(integerAttrValues);	
				complexValue.setAverage(meanIntegerValue);
				LOG.debug("Mean Integer Value is '{}'" + meanIntegerValue);

				// pairs
				LOG.debug("Calculating Pairs");
				Map<String,Integer> pairs = new HashMap<String,Integer>();
				pairs = cceStringPairs(stringAttrValues);
				complexValue.setPairs(pairs);

				//range 
				Integer [] range = cceNumRange(integerAttrValues);
				complexValue.setRangeMax(range[1]);
				complexValue.setRangeMin(range[0]);

				//median
				Double medianNumber = cceNumMedian(integerAttrValues);
				complexValue.setMedian(medianNumber);

				//mode
				List<Integer> modeNumber = cceNumMode(integerAttrValues);
				complexValue.setMode(modeNumber);

				//Converting the integers to Points2D
				List<String> finalStringArrayList = new ArrayList<String>();
				List<Point2D> cH = new ArrayList<Point2D>();

				for (String strPoint:stringAttrValues){
					String[] helperString = strPoint.split(",");
					for (String s1:helperString){
						finalStringArrayList.add(s1);
					}
				}
				cH = cceGeomConvexHull(CommunityContextEstimation.splitString(finalArrayStringList.toString()));
				ArrayList<String> stringPoints = new ArrayList<String>();
				for (Point2D point:cH){
					stringPoints.add(point.toString());
				}
				complexValue.setLocationGPS(stringPoints.toString());
				//TODO add any other applicable

			}

			// calculate strings 
			if( !stringAttrValues.isEmpty()){
				LOG.debug("estimateCommunityCtx for string values");
				for (String s: stringAttrValues){
					String[] helper = s.split(",");
					for (String s1:helper){
						finalArrayStringList.add(s1);
					}
				}	
				Map<String,Integer> occurences = new HashMap<String,Integer>();
				occurences = cceStringPairs(finalArrayStringList);

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
			communityAttr.getQuality().setOriginType(CtxOriginType.INFERRED);
			LOG.debug("estimateCommunityCtx: attribute={}, complexValue={}",
					ctxAttributeIdentifier, complexValue);

		} catch (Exception e) {
			LOG.error("Could not estimate community context attribute with ID '"
					+ ctxAttributeIdentifier + "': " + e.getLocalizedMessage(), e);
			return null;
		} 

		return communityAttr;
	}

	//@Override
	/*
	 * Returns the mean value of an integers' ArrayList 
	 * @param an array list of integers
	 * @return a double as the mean value of the input integers
	 * 
	 */
	public double cceNumMean(List<Integer> integerAttrValues) {

		Assert.notEmpty(integerAttrValues,"Cannot use estimation without attributes");
		int total = 0; 


		for (int i=0; i<integerAttrValues.size(); i++) {
			total = total + integerAttrValues.get(i);
		}		

		double res = (double)total/(double)integerAttrValues.size();		

		return res;
	}


	/*
	 * Returns the median of an integers' ArrayList
	 * @param an array list of integers
	 * @return a double as the median value of the input integers
	 */
	//@Override
	public double cceNumMedian(List<Integer> integerAttrValues) {
		Assert.notEmpty(integerAttrValues,"Cannot use estimation without attributes");
		Integer med,med1,med2=0;
		Collections.sort(integerAttrValues);

		if (integerAttrValues.size()%2 == 1 ){
			med = integerAttrValues.get((integerAttrValues.size()-1)/2);	
		}
		else {
			med1 = integerAttrValues.get((integerAttrValues.size())/2-1);
			med2 = integerAttrValues.get((integerAttrValues.size())/2);
			med = (med1+med2)/2;
		}
		return med;	
	}

	//@Override
	/*
	 * Returns the mode of an integer's ArrayList
	 * @param an array list of integers
	 * @return an ArrayList of integers representing the mode value of the input integers
	 */
	public List<Integer> cceNumMode(List<Integer> integerAttrValues) {

		Assert.notEmpty(integerAttrValues,"Cannot use estimation without attributes");
		Hashtable <Integer, Integer> frequencyMap = new Hashtable<Integer, Integer>();
		List<Integer> finalList = new ArrayList<Integer>();

		List<Integer> mode = new ArrayList<Integer>();
		int max=0;

		for (int i=0; i<integerAttrValues.size(); i++){
			if (finalList.contains(integerAttrValues.get(i))){
				int elementCount =frequencyMap.get(integerAttrValues.get(i));
				elementCount++;
				frequencyMap.put(integerAttrValues.get(i), elementCount);

				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				finalList.add(integerAttrValues.get(i));
				frequencyMap.put(integerAttrValues.get(i), 1);
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

	//@Override
	/*
	 * Returns the range of an integers' ArrayList
	 * @param an array list of integers
	 * @return the range of the input integers as Integer[]
	 */
	public Integer[] cceNumRange(List<Integer> integerAttrValues) {

		Integer[] r = new Integer[2];

		Integer min= Integer.MAX_VALUE;
		Integer max = Integer.MIN_VALUE;

		for (int i=0; i<integerAttrValues.size(); ++i){
			if (integerAttrValues.get(i) < min){
				min=integerAttrValues.get(i);
			}
			if (integerAttrValues.get(i) > max){
				max=integerAttrValues.get(i);
			}
		}
		r[0]=min;
		r[1]=max;
		return r;
	}	

	//@Override
	/*
	 * Returns the convex hull of a points' ArrayList. It recursively uses the singleSideHulSet method
	 * @param an array list of points.
	 * @return an ArrayList of points, representing the convex hull set of the input points
	 */
	public List<Point2D> cceGeomConvexHull(List<Point2D> points) {

		List<Point2D> convexHullSet = new ArrayList<Point2D>();
		double minX= Integer.MAX_VALUE;
		double maxX = Integer.MIN_VALUE;
		int minPointIndex = -1;
		int maxPointIndex = -1;
		List<Point2D> leftPointsSet = new ArrayList<Point2D>();
		List<Point2D> rightPointsSet = new ArrayList<Point2D>();

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
	 * This method finds the points of the given pointsSet, that belong to convex hull and adds them to the given convexHull set. 
	 * It constructs a segment with the points minPoint and maxPoint and calculates if the points belonging to the pointsSet and are at the left of the segment
	 * belong to the convexHull set
	 * @param minPoint, maxPoint the two points that construct the segment
	 * @param pointsSet a set of points that are lying at the left of the segment (minPoint,maxPoint)
	 * @param convexHullSet the set that contains the points belonging to the convex hull
	 */
	private void singleSideHullSet(List<Point2D> leftPointsSet, Point2D minPoint,
			Point2D maxPoint, List<Point2D> convexHullSet) {


		Point2D fP = new Point();
		Point2D rP = new Point();

		double distance_max = Integer.MIN_VALUE;
		double relativeDistance = 0;
		int farthestPointIndex = -1;
		int insertPosition = convexHullSet.indexOf(maxPoint);

		List<Point2D> set1 = new ArrayList<Point2D>();
		List<Point2D> set2 = new ArrayList<Point2D>();		

		if (leftPointsSet.size()==0){
			return ;
		}
		if (leftPointsSet.size()==1){
			Point2D p = leftPointsSet.get(0);
			leftPointsSet.remove(p);
			convexHullSet.add(insertPosition, p);
			return;
		}

		for (int i=0; i<leftPointsSet.size(); i++){	
			Point2D m =leftPointsSet.get(i);				
			relativeDistance=(maxPoint.getX()-minPoint.getX())*(minPoint.getY()-m.getY())-(maxPoint.getY()-minPoint.getY())*(minPoint.getX()-m.getX());
			if (relativeDistance < 0){
				relativeDistance= -relativeDistance;
			}

			if (relativeDistance > distance_max){			
				distance_max =relativeDistance;
				farthestPointIndex=i;			
			}		
		}

		fP=leftPointsSet.get(farthestPointIndex);
		convexHullSet.add(insertPosition,fP);
		leftPointsSet.remove(farthestPointIndex);

		for (int i=0; i<leftPointsSet.size(); ++i){
			rP = leftPointsSet.get(i);
			double crossProduct = (fP.getX()-minPoint.getX())*(rP.getY()-minPoint.getY()) - (fP.getY()-minPoint.getY())*(rP.getX()-minPoint.getX());
			if (crossProduct >= 0){
				set1.add(rP);
			}
		}

		for (int i=0; i<leftPointsSet.size(); ++i){
			rP = leftPointsSet.get(i);
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

	//@Override
	/*
	 * Returns the minimum bounding box that contains all the given points
	 * @param an array list of integers
	 * @return an array of points representing the minimum bounding box of the input points
	 */
	public Point2D[] cceGeomMinBB(ArrayList<Point2D> points) {

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

	//@Override
	/*
	 * Returns the range of a strings' ArrayList
	 * @param an array list of strings
	 * @return an ArrayList of strings showing the mode of the input strings
	 */
	public List<String> cceStringMode(ArrayList<String> inputValuesList) {

		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		List<String> finalList = new ArrayList<String>();

		List<String> mode = new ArrayList<String>();
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

	//method for splitting the LOCATION string. Location should be in String representation of a pair of double  values
	public static List<Point2D> splitString (String s){

		Point2D.Double p = new Point2D.Double();
		double x=0.0;
		double y=0.0;
		int l = 0;
		List<Point2D> points = new ArrayList<Point2D>();
		String[] splited_string = s.split(",");

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


	public Map<String,Integer> cceStringPairs(List<String> stringAttrValues) {

		Map <String, Integer> frequencyMap = new HashMap<String, Integer>();
		List<String> outputList = new ArrayList<String>();
		List<String> arrayListWithStringPercent = new ArrayList<String>();

		int max=0;
		for (int i=0; i<stringAttrValues.size(); i++){
			if (outputList.contains(stringAttrValues.get(i))){
				int elementCount = Integer.parseInt(frequencyMap.get(stringAttrValues.get(i)).toString());
				elementCount++;
				frequencyMap.put(stringAttrValues.get(i), elementCount);				
				if (elementCount>max){
					max=elementCount;
				}
			}
			else
			{
				outputList.add(stringAttrValues.get(i));
				frequencyMap.put(stringAttrValues.get(i), 1);
			}	

		}

		return frequencyMap;
	}

	//@Override
	/*
	 * @param an array list of strings
	 * @return an array list of strings of type [abd, 57%, abc, 14%, cde, 28%]
	 */
	public List<String> cceStringPercentage(ArrayList<String> inputListOfStrings) {
		Hashtable <String, Integer> frequencyMap = new Hashtable<String, Integer>();
		List<String> outputList = new ArrayList<String>();
		List<String> arrayListWithStringPercent = new ArrayList<String>();

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
				//	System.out.println("Key = "+k+" Value = "+frequencyMap.get(k));
				hashTabletWithPercentage.put(k.toString(), 100*frequencyMap.get(k)/total);
				arrayListWithStringPercent.add(k.toString());
				int help = 100*frequencyMap.get(k)/total;
				arrayListWithStringPercent.add(String.valueOf(help)+"%");
			}

		}

		return arrayListWithStringPercent;

	}

	//@Override
	public void cceSpecial2() {
		// TODO Auto-generated method stub

	}

	//@Override
	public void cceSpecial3() {
		// TODO Auto-generated method stub

	}
	
	private IIdentity getLocalIdentity() throws InvalidFormatException {

		return this.commMgr.getIdManager().fromJid(
				this.commMgr.getIdManager().getThisNetworkNode().getBareJid());
	}
	
	private Requestor createLocalRequestor() throws InvalidFormatException {
		
		return new Requestor(this.getLocalIdentity());
	}
	
	private RequestorCis createRequestorCis(Requestor localRequestor, CtxAttributeIdentifier cisAttrId) 
			throws InvalidFormatException {
		
		final IIdentity cisId = this.commMgr.getIdManager().fromJid(cisAttrId.getOwnerId());
		
		return new RequestorCis(localRequestor.getRequestorId(), cisId);
	}
}