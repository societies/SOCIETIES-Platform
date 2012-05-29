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
import java.util.Hashtable;
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
//import org.societies.context.community.estimation.impl.ConvexHull;
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
	
	//ConvexHull cH = new ConvexHull();
    CommunityContextEstimation cce = new CommunityContextEstimation();
	Random rand1 = new Random();
	Random rand2 = new Random();
	ArrayList<Point> setOfPoints=new ArrayList<Point>();
	ArrayList<Point> ExpectedsetOfConvexHullPoints = new ArrayList<Point>();
	
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
	
		
		
		p1.x=-1;
		p1.y=-1;
		setOfPoints.add(0,p1);
		//ExpectedsetOfConvexHullPoints.add(0,p1);
		
		p2.x=1;
		p2.y=-1;
		setOfPoints.add(1,p2);
		//ExpectedsetOfConvexHullPoints.add(1,p2);
				
		p3.x=1;
		p3.y=1;
		setOfPoints.add(2,p3);
		//ExpectedsetOfConvexHullPoints.add(2,p3);
		
		p4.x=-1;
		p4.y=1;
		setOfPoints.add(3,p4);
		//ExpectedsetOfConvexHullPoints.add(3,p4);
	
		p5.x=0;
		p5.y=0;
		setOfPoints.add(4,p5);
				
		p9.x=-2;
		p9.y=-2;
		setOfPoints.add(5,p9);
				
		p8.x=-2;
		p8.y=2;
		setOfPoints.add(6,p8);
				
		p6.x=2;
		p6.y=2;
		setOfPoints.add(7,p6);
		
		
		p7.x=2;
		p7.y=-2;
		setOfPoints.add(8,p7);
		
		ExpectedsetOfConvexHullPoints.add(0,p7);
		ExpectedsetOfConvexHullPoints.add(1,p9);
		ExpectedsetOfConvexHullPoints.add(2,p8);
		ExpectedsetOfConvexHullPoints.add(3,p6);
	

	System.out.println("size is "+setOfPoints.size());
//	for (int i=0; i<setOfPoints.size();++i){
//		System.out.println("Stoixeio "+i+" is "+setOfPoints.get(i).x+", "+setOfPoints.get(i).y);
//	}
	
	//ArrayList<Point> a = cH.quickHull(setOfPoints);
	//ArrayList<Point> a = cH.qHull(setOfPoints);
	ArrayList<Point> a = cce.cceGeomConvexHull(setOfPoints);
	
	//ArrayList<Point> a = cH.qHull(setOfPoints);
	
	System.out.println("THE CONVEX HULL SIZE IS  : "+a.size());
			for (int z=0; z<a.size();++z){
	System.out.println("ConvexHull a["+z+"] = "+a.get(z));
}
	Assert.assertEquals(ExpectedsetOfConvexHullPoints,a);

//	for (int i=0; i<setOfPoints.size();++i){
//		System.out.println("Stoixeio tou Hull "+i+" is "+a.get(i).x+", "+a.get(i).y);
//	}

	//return setOfPoints;
}

@Test
public void testCalculateStringStatistics() throws InterruptedException, ExecutionException, CtxException{
	
	List<CtxAttribute> attrList = new ArrayList<CtxAttribute>();
	
	//Create a person entity 1
	CtxEntity person1 = iB.createEntity("person1").get();				
	CtxEntityIdentifier pId1 = person1.getId();
	CtxAttribute person1Age = this.iB.createAttribute(pId1, "profession").get(); 
	person1Age.setValueType(CtxAttributeValueType.STRING);
	person1Age = (CtxAttribute) iB.update(person1Age).get();
	person1Age.setStringValue("Engineer");
	attrList.add(person1Age);

	//Create a person entity 2
	CtxEntity person2 = iB.createEntity("person2").get();
	CtxEntityIdentifier pId2 = person2.getId();
	System.out.println("PID2 = "+pId2);
	CtxAttribute person2Age = iB.createAttribute(pId2, "profession").get(); 
	person2Age.setValueType(CtxAttributeValueType.STRING);
	person2Age = (CtxAttribute) iB.update(person2Age).get();
	person2Age.setStringValue("Chef");
	attrList.add(person2Age);

	//Create a person entity 3
	CtxEntity person3 = iB.createEntity("person3").get();
	CtxEntityIdentifier pId3 = person3.getId();
	CtxAttribute person3Age = this.iB.createAttribute(pId3, "age").get(); 
	person3Age.setValueType(CtxAttributeValueType.STRING);
	person3Age = (CtxAttribute) iB.update(person3Age).get();
	person3Age.setStringValue("Cook");
	attrList.add(person3Age);
	
	//Create a person entity 4
	CtxEntity person4 = iB.createEntity("person4").get();				
	CtxEntityIdentifier pId4 = person4.getId();
	CtxAttribute person4Age = this.iB.createAttribute(pId4, "profession").get(); 
	person4Age.setValueType(CtxAttributeValueType.STRING);
	person4Age = (CtxAttribute) iB.update(person4Age).get();
	person4Age.setStringValue("Engineer");
	attrList.add(person4Age);
	
	//Create a person entity 5
	CtxEntity person5 = iB.createEntity("person5").get();				
	CtxEntityIdentifier pId5 = person5.getId();
	CtxAttribute person5Age = this.iB.createAttribute(pId5, "profession").get(); 
	person5Age.setValueType(CtxAttributeValueType.STRING);
	person5Age = (CtxAttribute) iB.update(person5Age).get();
	person5Age.setStringValue("Plumber");
	attrList.add(person5Age);
	
	//Create a person entity 6
	CtxEntity person6 = iB.createEntity("person6").get();				
	CtxEntityIdentifier pId6 = person1.getId();
	CtxAttribute person6Age = this.iB.createAttribute(pId6, "profession").get(); 
	person6Age.setValueType(CtxAttributeValueType.STRING);
	person6Age = (CtxAttribute) iB.update(person6Age).get();
	person6Age.setStringValue("Engineer");
	attrList.add(person6Age);
	
	//Create a person entity 7
	CtxEntity person7 = iB.createEntity("person7").get();				
	CtxEntityIdentifier pId7 = person7.getId();
	CtxAttribute person7Age = this.iB.createAttribute(pId7, "profession").get(); 
	person7Age.setValueType(CtxAttributeValueType.STRING);
	person7Age = (CtxAttribute) iB.update(person7Age).get();
	person7Age.setStringValue("Engineer");
	attrList.add(person7Age);
	
	//Create a person entity 8
	CtxEntity person8 = iB.createEntity("person8").get();				
	CtxEntityIdentifier pId8 = person8.getId();
	CtxAttribute person8Age = this.iB.createAttribute(pId8, "profession").get(); 
	person8Age.setValueType(CtxAttributeValueType.STRING);
	person8Age = (CtxAttribute) iB.update(person8Age).get();
	person8Age.setStringValue("Engineer");
	attrList.add(person8Age);
	
	//Create a person entity 9
	CtxEntity person9 = iB.createEntity("person9").get();				
	CtxEntityIdentifier pId9 = person1.getId();
	CtxAttribute person9Age = this.iB.createAttribute(pId9, "profession").get(); 
	person9Age.setValueType(CtxAttributeValueType.STRING);
	person9Age = (CtxAttribute) iB.update(person9Age).get();
	person9Age.setStringValue("Plumber");
	attrList.add(person9Age);
	
	//Create a person entity 10
	CtxEntity person10 = iB.createEntity("person10").get();				
	CtxEntityIdentifier pId10 = person1.getId();
	CtxAttribute person10Age = this.iB.createAttribute(pId10, "profession").get(); 
	person10Age.setValueType(CtxAttributeValueType.STRING);
	person10Age = (CtxAttribute) iB.update(person10Age).get();
	person10Age.setStringValue("Chef");
	attrList.add(person10Age);

	//Run the tests!!
	CommunityContextEstimation cce = new CommunityContextEstimation();
	Hashtable<String, Integer> res = cce.calculateStringAttributeStatistics(attrList);
	Hashtable<String, Integer> expectedHashMapTable = new Hashtable<String, Integer>();
	expectedHashMapTable.put("Engineer", 5);
	expectedHashMapTable.put("Plumber", 2);
	expectedHashMapTable.put("Cook", 1);
	expectedHashMapTable.put("Chef", 2);
	assertEquals(expectedHashMapTable, res);
}

//@Test
public void cceGeomMinBBTest(){
	CommunityContextEstimation cce = new CommunityContextEstimation();

	ArrayList<Point> setOfPoints=new ArrayList<Point>();
	Point[] ExpectedsetOfBBPoints = new Point[2];
	
		Point p1 = new Point();
		Point p2 =new Point();
		Point p3 = new Point();
		Point p4 = new Point();
		Point p5 = new Point();
		Point p6 = new Point();
		Point p7 = new Point();
		Point p8 = new Point();
		Point p9 = new Point();
	
		
		
		p1.x=-1;
		p1.y=-1;
		setOfPoints.add(0,p1);
		//ExpectedsetOfConvexHullPoints.add(0,p1);
		
		p2.x=1;
		p2.y=-1;
		setOfPoints.add(1,p2);
		//ExpectedsetOfConvexHullPoints.add(1,p2);
				
		p3.x=1;
		p3.y=1;
		setOfPoints.add(2,p3);
		//ExpectedsetOfConvexHullPoints.add(2,p3);
		
		p4.x=-1;
		p4.y=1;
		setOfPoints.add(3,p4);
		//ExpectedsetOfConvexHullPoints.add(3,p4);
	
		p5.x=0;
		p5.y=0;
		setOfPoints.add(4,p5);
				
		p9.x=-2;
		p9.y=-2;
		setOfPoints.add(5,p9);
				
		p8.x=-2;
		p8.y=2;
		setOfPoints.add(6,p8);
				
		p6.x=7;
		p6.y=7;
		setOfPoints.add(7,p6);
		
		
		p7.x=2;
		p7.y=-2;
		setOfPoints.add(8,p7);
		
		Point po = new Point();
		po.x=-2;
		po.y=7;
		ExpectedsetOfBBPoints[0]=po;

		Point po2 =  new Point();
		po2.x=7;
		po2.y=-2;
		ExpectedsetOfBBPoints[1]=po2;
	


//	for (int i=0; i<setOfPoints.size();++i){
//		System.out.println("Stoixeio "+i+" is "+setOfPoints.get(i).x+", "+setOfPoints.get(i).y);
//	}
	
	//ArrayList<Point> a = cH.quickHull(setOfPoints);
	//ArrayList<Point> a = cH.qHull(setOfPoints);
		
		System.out.println("Ta POINT EINAI "+setOfPoints.get(0)+ " "+ setOfPoints.get(1));
	Point[] a = cce.cceGeomMinBB(setOfPoints);
	for (int i=0; i<a.length; i++){
		System.out.println("Afto pou bgazei h methodos einai to "+a[i]);
		System.out.println("To sosto einai"+ExpectedsetOfBBPoints[i]);
	}
	
	//ArrayList<Point> a = cH.qHull(setOfPoints);
	
//	System.out.println("THE CONVEX HULL SIZE IS  : "+a.size());
//			for (int z=0; z<a.size();++z){
//	System.out.println("ConvexHull a["+z+"] = "+a.get(z));
//}
	Assert.assertEquals(ExpectedsetOfBBPoints,a);

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



