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
package org.societies.context.community.estimation.test;

import static org.junit.Assert.assertEquals;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.context.api.community.estimation.EstimationModels;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.community.estimation.impl.CommunityContextEstimation;
import org.societies.context.community.estimation.impl.ConvexHull;
import org.societies.context.user.db.impl.UserCtxDBMgr;
import org.societies.context.userHistory.impl.UserContextHistoryManagement;
import org.springframework.beans.factory.annotation.Autowired;

public class CommunityContextEstimationTest{
	
	@Autowired
	private ICtxBroker internalCtxBroker;
	
	InternalCtxBroker iB;
	CtxEntityIdentifier pId1 = null;
	CtxEntityIdentifier pId2 = null;
	CtxEntityIdentifier pId3 = null;
	
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	
	}

	
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		//ICtxBroker internalCtxBroker;
		iB = new InternalCtxBroker();	
		iB.setUserCtxDBMgr(new UserCtxDBMgr());
		iB.setUserCtxHistoryMgr(new UserContextHistoryManagement());
	}

	
	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		//internalCtxBroker = null;
	}
		

	//@Test
	public void estimationTestMedian() throws Exception, ExecutionException, CtxException{

		//List<CtxEntity> personList = new ArrayList<CtxEntity>();
		List<CtxAttribute> attrList = new ArrayList<CtxAttribute>();

		//Create a person entity 1
		CtxEntity person1 = iB.createEntity("person1").get();	
		CtxEntityIdentifier pId1 = person1.getId();
		CtxAttribute person1Age = this.iB.createAttribute(pId1, "age").get(); 
		person1Age.setValueType(CtxAttributeValueType.INTEGER);
		person1Age = (CtxAttribute) iB.update(person1Age).get();
		person1Age.setIntegerValue(30);
		
		attrList.add(person1Age);
		
		//Create a person entity 2
		CtxEntity person2 = iB.createEntity("person2").get();
		CtxEntityIdentifier pId2 = person2.getId();
		CtxAttribute person2Age = iB.createAttribute(pId2, "age").get(); 
		person2Age.setValueType(CtxAttributeValueType.INTEGER);
		person2Age = (CtxAttribute) iB.update(person2Age).get();
		person2Age.setIntegerValue(40);
		attrList.add(person2Age);

		
		//Create a person entity 3
		CtxEntity person3 = iB.createEntity("person3").get();
		CtxEntityIdentifier pId3 = person3.getId();
		CtxAttribute person3Age = this.iB.createAttribute(pId3, "age").get(); 
		person3Age.setValueType(CtxAttributeValueType.INTEGER);
		person3Age = (CtxAttribute) iB.update(person3Age).get();
		person3Age.setIntegerValue(50);
		attrList.add(person3Age);

		CommunityContextEstimation cce = new CommunityContextEstimation();
		Integer res = cce.estimateContext(EstimationModels.MEDIAN, attrList);
		assertEquals(40, res);
	}
	
	//@Test
	public void estimationTestMean() throws Exception, ExecutionException, CtxException{

		//List<CtxEntity> personList = new ArrayList<CtxEntity>();
		List<CtxAttribute> attrList = new ArrayList<CtxAttribute>();

		//Create a person entity 1
		CtxEntity person1 = iB.createEntity("person1").get();				
		CtxEntityIdentifier pId1 = person1.getId();
		CtxAttribute person1Age = this.iB.createAttribute(pId1, "age").get(); 
		person1Age.setValueType(CtxAttributeValueType.INTEGER);
		person1Age = (CtxAttribute) iB.update(person1Age).get();
		person1Age.setIntegerValue(30);

		attrList.add(person1Age);
		
		//Create a person entity 2
		CtxEntity person2 = iB.createEntity("person2").get();
		CtxEntityIdentifier pId2 = person2.getId();
		CtxAttribute person2Age = iB.createAttribute(pId2, "age").get(); 
		person2Age = (CtxAttribute) iB.update(person2Age).get();
		person2Age.setIntegerValue(40);
		
		attrList.add(person2Age);
		
		//Create a person entity 3
		CtxEntity person3 = iB.createEntity("person3").get();
		CtxEntityIdentifier pId3 = person3.getId();
		CtxAttribute person3Age = this.iB.createAttribute(pId3, "age").get(); 
		person3Age.setValueType(CtxAttributeValueType.INTEGER);
		person3Age = (CtxAttribute) iB.update(person3Age).get();
		person3Age.setIntegerValue(50);
		attrList.add(person3Age);

		//Run the tests!!
		CommunityContextEstimation cce = new CommunityContextEstimation();
		Integer res = cce.estimateContext(EstimationModels.MEAN, attrList);
		assertEquals(40, res);
	}
	
	//@Test
	public void estimateContextWithoutEnteringAttributes() {
		CommunityContextEstimation cce = new CommunityContextEstimation();
		try{
			cce.estimateContext(EstimationModels.MEAN, null);
		}catch (IllegalArgumentException e) {
			//throw new RuntimeException();
		}
	}
	
	
	public void est1imateContexEnteringAttributes() {
		try{
			estimateContextWithoutEnteringAttributes();
		}catch (RuntimeException e) {
			// TODO: handle exception
		}
	}
	
//	@Test
//	public void convexHullTest() {
//		ConvexHull cH = new ConvexHull();
//		float[][] setOfPoints = new float[4][2];
//		Random rG = new Random();
//		for (int i=0; i<4; ++i){
//			for (int j=0; j<2; ++j){
//				Float randNum = rG.nextFloat();
//				setOfPoints[i][j] = 100*randNum;
//				System.out.println("Stoixeio ["+ i +"], ["+ j + "] is :"+setOfPoints[i][j]);
//			}			
//		}
//		float[][] a = ConvexHull.findConvexHull(setOfPoints);
//		System.out.println("To megethos einai:"+a.length);
//		for (int i = 0; i<a.length; ++i){
//			for(int j=0; j<a[i].length; ++j){
//				System.out.println("To apotelesma einai:" +a[i][j]);	
//			}
//		
//		}
		
//	}
	
	
@Test	
public void testConvexHull(){
	
	ConvexHull cH = new ConvexHull();

	Random rand1 = new Random();
	Random rand2 = new Random();
	ArrayList<Point> setOfPoints=new ArrayList<Point>();
	ArrayList<Point> setOfConvexHullPoints = new ArrayList<Point>();
	
//	for (int i=0; i<1000; ++i){
//		Point p = new Point();
//		p.x=rand1.nextInt(1000);
//		p.y = rand2.nextInt(1000);
//		setOfPoints.add(p);
//	}
	
		Point p1 = new Point();
		Point p2 =new Point();
		Point p3 = new Point();
		Point p4 = new Point();
		Point p5 = new Point();
		Point p6 = new Point();
		Point p7 = new Point();
		Point p8 = new Point();
		Point p9 = new Point();
	
		
		p2.x=3;
		p2.y=8;
		setOfPoints.add(0,p2);
		setOfConvexHullPoints.add(0,p2);
		
		p1.x=-1;
		p1.y=-1;
		setOfPoints.add(1,p1);
		setOfConvexHullPoints.add(1,p1);
		
		p3.x=4;
		p3.y=7;
		setOfPoints.add(2,p3);
		
		p4.x=5;
		p4.y=8;
		setOfPoints.add(3,p4);
		
		p5.x=6;
		p5.y=2;
		setOfPoints.add(4,p5);
		
		p6.x=8;
		p6.y=6;
		setOfPoints.add(4,p6);
		
		p7.x=9;
		p7.y=3;
		setOfPoints.add(4,p7);
		setOfConvexHullPoints.add(2,p7);
		
		p8.x=10;
		p8.y=10;
		setOfPoints.add(4,p8);
		
		p9.x=11;
		p9.y=11;
		setOfPoints.add(4,p9);
		setOfConvexHullPoints.add(3,p9);
	System.out.println("size is "+setOfPoints.size());
//	for (int i=0; i<setOfPoints.size();++i){
//		System.out.println("Stoixeio "+i+" is "+setOfPoints.get(i).x+", "+setOfPoints.get(i).y);
//	}
	
	//ArrayList<Point> a = cH.quickHull(setOfPoints);
	ArrayList<Point> a = cH.qHull(setOfPoints);
	
	for (int z=0; z<a.size();++z){
	System.out.println("ConvexHulla["+z+"] = "+a.get(z));
}
	Assert.assertEquals(a,setOfConvexHullPoints) ;
//	for (int i=0; i<setOfPoints.size();++i){
//		System.out.println("Stoixeio tou Hull "+i+" is "+a.get(i).x+", "+a.get(i).y);
//	}

	//return setOfPoints;
}




}



//@Test
//public void estimateContexEnteringAttributes() {
//	List<CtxAttribute> atrsList = new ArrayList<CtxAttribute>();
//	CtxAttribute e = new CtxAttribute(null);
//	atrsList.add(e);
//	CommunityContextEstimation cce = new CommunityContextEstimation();		
//	Integer actual = cce.estimateContext(EstimationModels.MEAN, null);
//	Assert.assertEquals(5, actual.intValue());
//}


