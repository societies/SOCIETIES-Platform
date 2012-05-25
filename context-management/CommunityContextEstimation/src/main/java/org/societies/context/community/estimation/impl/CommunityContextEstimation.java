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
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.EstimationModels;
import org.societies.context.api.community.estimation.ICommunityCtxEstimationMgr;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

/**
 * @author yboul 07-Dec-2011 4:15:14 PM
 */
@Service("communityCtxEstimation")
public class CommunityContextEstimation implements ICommunityCtxEstimationMgr{

	@Override
	public double cceNumMean(ArrayList<Integer> inputValuesList) {
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		
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
		// TODO Auto-generated method stub
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
		// TODO Auto-generated method stub
		ArrayList<Point> convexHullSet = new ArrayList<Point>();
		int minX= Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int minPoint = -1;
		int maxPoint = -1;
		ArrayList<Point> leftPointsSet = new ArrayList<Point>();
		ArrayList<Point> rightPointsSet = new ArrayList<Point>();

		if (points.size()<3){
			return points;
		}
		
		for (int i=0; i<points.size(); ++i){
			if (points.get(i).x < minX){
				minX=points.get(i).x;
				minPoint = i;
			}
			if (points.get(i).x > maxX){
				maxX=points.get(i).x;
				maxPoint =i;
			}
		}
		
		Point minP = points.get(minPoint);
		Point maxP = points.get(maxPoint);	
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
		
		singleSideHullSet(rightPointsSet,minP,maxP,convexHullSet);
		singleSideHullSet(rightPointsSet,maxP,minP,convexHullSet);
		return convexHullSet;
	}


	private void singleSideHullSet(ArrayList<Point> pointsSet, Point minPoint,
			Point maxPoint, ArrayList<Point> convexHullSet) {
		// TODO Auto-generated method stub
		Point fP = new Point();
		Point rP = new Point();

		int distance_min = Integer.MIN_VALUE;
		int relativeDistance = 0;
		int farthestPoint = -1;
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

			if (relativeDistance > distance_min){			
				distance_min =relativeDistance;
				farthestPoint=i;		
			}
		}
		
		fP=pointsSet.get(farthestPoint);
		convexHullSet.add(insertPosition,fP);
		pointsSet.remove(farthestPoint);
		
		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			int crossProduct = (fP.x-minPoint.x)*(rP.y-minPoint.y) - (fP.y-minPoint.y)*(rP.x-minPoint.x);
			if (crossProduct <= 0){
				set1.add(rP);
			}
	
		}
			
		for (int i=0; i<pointsSet.size(); ++i){
			rP = pointsSet.get(i);
			int crossProduct = (maxPoint.x-fP.x)*(rP.y-fP.y) - (maxPoint.y-fP.y)*(rP.x-fP.x);
			if (crossProduct <= 0){
				set2.add(rP);
				
			}
		}
		singleSideHullSet(set1, minPoint, fP, convexHullSet); 
		singleSideHullSet(set2,fP,maxPoint,convexHullSet);	
	}

	@Override
	public Point[] cceGeomMinBB(ArrayList<Point> points) {
		// TODO Auto-generated method stub

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
	
	//Constructor

	public CommunityContextEstimation() {
		// TODO Auto-generated constructor stub

	}
	
	@Autowired(required = true)
	private ICtxBroker ctxBroker = null;

	private CtxEntityIdentifier comId;
	private String entityType;
	private String attributeType;


	//@Override
	public Integer estimateContext(EstimationModels estimationModel, List<CtxAttribute> list) {
		switch (estimationModel) {
		case MEAN:
			return estimateMeanValue(list);			

		case MEDIAN:
			return estimateMedianValue(list);

		default:
			return 0;
		}


	}



	//@Override
	public void estimateContext(EstimationModels estimationmodel, CtxAttribute type, CtxIdentifier cisId) {
		// TODO Auto-generated method stub
		Assert.notNull(cisId,"Cannot use estimation without any cmmunity");
		//b = new InternalCtxBroker();
		try {
			CtxEntity retrievedCtxEntity = (CtxEntity) this.ctxBroker.retrieve(cisId).get();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private Integer estimateMeanValue(List<CtxAttribute> list) {
		Assert.notEmpty(list,"Cannot use estimation without attributes");

		//BigDecimal result = BigDecimal.ZERO;
		int total = 0; 

		for (CtxAttribute ca : list){
			total = total + ca.getIntegerValue();
			//list.iterator().next();
			System.out.println("Halloooooooo" +" "+total);
		}
		int res = total/list.size();
		return res;
	}

	private Integer estimateMedianValue(List<CtxAttribute> list) {
		// TODO Auto-generated method stub
		Integer med,med1,med2=0;

		if (list.size()%2 == 1 ){
			med = list.get((list.size()+1)/2-1).getIntegerValue();	
			System.out.println("Odd number of elements");
		}
		else {
			System.out.println("Even number of elements");
			med1 = list.get((list.size())/2-1).getIntegerValue();
			med2 = list.get((list.size()+2)/2-1).getIntegerValue();
			med = (med1+med2)/2;
		}
		// an update is needed here
		return med;	

	}

	//******************************************NEW***************************************************************************
	//
	//
	//&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&


	/**
	 * @param modelType
	 * @param ctxEntityType
	 * @param ctxAttr
	 * @return
	 * @throws CtxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public Double estimateMeanValueOfIntegers(CtxModelType modelType, String ctxEntityType, String ctxAttr) throws CtxException, InterruptedException, ExecutionException{

		int total = 0;
		Double mo = 0.0;

		CommunityContextEstimation cce = new CommunityContextEstimation();
		List<CtxAttribute> listOfCtxAttributes = cce.retrieveCertainAttributes(modelType, ctxEntityType, ctxAttr);

		//Mean value estimation
		for (CtxAttribute cA : listOfCtxAttributes) {
			total = total+cA.getIntegerValue();
		}

		int noOfCtxEntities = cce.retrieveListOfCtxEntities(modelType, ctxEntityType).size();

		mo = (double) (total/noOfCtxEntities);

		return mo;

	}

	/**
	 * @param modelType
	 * @param ctxEntityType
	 * @param ctxAttr
	 * @return
	 * @throws CtxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<CtxAttribute> retrieveCertainAttributes(CtxModelType modelType, String ctxEntityType, String ctxAttr) throws CtxException, InterruptedException, ExecutionException {

		//Retrieve a list of ctxEntitiesIdentifiers, of certain modelType (e.g. entity) and certain ctxEntityType (e.g. "Person")
		CommunityContextEstimation cce = new CommunityContextEstimation();
		List<CtxEntity> listOfCtxEntities = cce.retrieveListOfCtxEntities(modelType, ctxEntityType);

		//We iterate on the previous list of ctxEntities in order to retrieve the attributes of each ctxEntity, of the given value (ctxAttr)
		List<CtxAttribute> listOfCtxAttributes = new ArrayList<CtxAttribute>();
		for (CtxEntity cE : listOfCtxEntities){

			Set<CtxAttribute> setOfEntityCtxAttributes = cE.getAttributes();

			for (CtxAttribute cA : setOfEntityCtxAttributes) {
				if (cA.getStringValue() == ctxAttr) {
					listOfCtxAttributes.add(cA);
				}
			}
		}
		return listOfCtxAttributes;
	}

	/**
	 * @param modelType
	 * @param ctxEntityType
	 * @return
	 * @throws CtxException
	 * @throws InterruptedException
	 * @throws ExecutionException
	 */
	public List<CtxEntity> retrieveListOfCtxEntities(CtxModelType modelType, String ctxEntityType) throws CtxException, InterruptedException, ExecutionException {

		Future<List<CtxIdentifier>> ctxEntitiesIdentifiersFutureList = this.ctxBroker.lookup(modelType, ctxEntityType);
		List<CtxIdentifier> ctxEntitiesIdentifiersList = ctxEntitiesIdentifiersFutureList.get();
		List<CtxEntity> listOfCtxEntities= new ArrayList<CtxEntity>();

		for (CtxIdentifier id : ctxEntitiesIdentifiersList){
			CtxEntity ctxEntity = (CtxEntity) this.ctxBroker.retrieve(id);
			listOfCtxEntities.add(ctxEntity);
		}
		return listOfCtxEntities;
	}

	// Setters and Getters for the private fields ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^//^

//	public ICtxBroker getB() {
//		return b;
//	}

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



	//@Override
	public Hashtable<String, Integer> calculateStringAttributeStatistics(List<CtxAttribute> list) {
		
		//List<CtxAttribute> list = new List<CtxAttribute>;
		
		List<String> proffesions = new ArrayList<String>();

		for(int i=0; i<list.size(); ++i){
			proffesions.add(list.get(i).getStringValue());
		}

		Hashtable <String, Integer> frequencyMap = new Hashtable();
		ArrayList<String> finalList = new ArrayList<String>();

		for (int i=0; i<proffesions.size(); i++){
			if (finalList.contains(proffesions.get(i)))
			{
				int elementCount =
						Integer.parseInt(frequencyMap.get(proffesions.get(i)).toString());
				elementCount++;
				frequencyMap.put(proffesions.get(i), elementCount);
			}
			else
			{
				finalList.add(proffesions.get(i));
				frequencyMap.put(proffesions.get(i), 1);
			}
		}
		System.out.println(frequencyMap);
		return frequencyMap;
	}

	@Override
	public ArrayList<String> cceStringMode(ArrayList<String> inputValuesList) {
		// TODO Auto-generated method stub
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





	// private void returnListOfDesiredAttributes(List<CtxAttributeIdentifier> listOfMembersOfGivenType) {
	// // TODO Auto-generated method stub
	// // I want to receive the attributes value through the contextIdentifier
	//
	// ArrayList<CtxAttribute> listOfAttributes = new ArrayList<CtxAttribute>();
	// Iterator<CtxAttributeIdentifier> membIterator = listOfMembersOfGivenType.iterator();
	// while (membIterator.hasNext()){
	// CtxAttributeIdentifier cEI = membIterator.next();
	// CtxAttribute ctxAtt;
	// CtxModelObject ctxModObj;
	// //if
	// //(cEI.getModelType().ATTRIBUTE != null)
	// //ctxModObj.
	// //listOfAttributes.add(e);
	//
	// }
	//}

	// private List<CtxAttributeIdentifier> returnEntitiesWithGivenEntiryType(List<CtxEntityIdentifier> allMembersList) {
	// // TODO Auto-generated method stub
	// //If the modelType is Entity then put in the listCtxEntityIdentifier this ctxEntityIdentifier.
	// //So at the end I will have a list with Entity ctxEntityIdentifiers of the community under discussion
	//
	// List<CtxAttributeIdentifier> listCtxEntityIdentifier = new ArrayList<CtxAttributeIdentifier>();
	//
	// Iterator<CtxEntityIdentifier> membIterator = allMembersList.iterator();
	// while (membIterator.hasNext()){
	// CtxEntityIdentifier cEI = membIterator.next();
	// CtxAttributeIdentifier a = new CtxAttributeIdentifier(cEI, cEI.getType(),cEI.getObjectNumber());
	// {
	// if
	// (cEI.getModelType().ENTITY != null && cEI.getType().equals(entityType))
	// listCtxEntityIdentifier.add(a);
	// else
	// System.out.println(cEI.getType());
	// }
	//
	// }
	// return listCtxEntityIdentifier;
	//}

	// public void estimateContext_John(EntityIdentifier communityID, List<CtxAttribute> list, Boolean currentDB) throws CtxException{
	// // TODO Auto-generated method stub
	//
	//// CtxAttribute a = new CtxAttribute(null);
	//// a.getId().getType();
	//// a.getIntegerValue();
	//
	// ArrayList<CtxAttribute> allAttributes = new ArrayList<CtxAttribute>();
	//
	// ArrayList<CtxEntity> m = retrieveCisMembersWitPredefinedAttr_John(communityID, list);
	// // elegxos gia null h oxi (ta members)
	// for (CtxEntity e:m){
	// allAttributes.addAll((retrieveMembersAttribute_John(e, list)));
	// }
	//
	// CalculateAlgorithm(allAttributes);
	// CtxEntityIdentifier community = null;
	// Identity requester = null;
	// b.retrieveCommunityMembers(requester, community);
	//
	//}
	//
	//private ArrayList<CtxEntity> retrieveCisMembersWitPredefinedAttr_John(EntityIdentifier communityID, List<CtxAttribute> hasTheseAttributes) throws CtxException {
	// // TODO Auto-generated method stub
	// //b
	// //return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
	// return null;
	//}
	//
	//
	//private ArrayList<CtxAttribute> retrieveMembersAttribute_John(CtxEntity member, List<CtxAttribute> hasTheseAttributes) {
	// // TODO Auto-generated method stub
	// //b
	// //return (ArrayList<CtxEntity>) b.retrieveAdministratingCSS(null, null); na vro tin kanoniki methodo tou broker...
	// return null;
	//}

}