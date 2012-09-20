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
//import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.context.broker.impl.InternalCtxBroker;
import org.societies.context.community.estimation.impl.CommunityContextEstimation;
//import org.societies.context.user.db.impl.UserCtxDBMgr;

import org.springframework.beans.factory.annotation.Autowired;

public class CommunityContextEstimationTest{


	//private ICtxBroker internalCtxBroker;

	//InternalCtxBroker iB;
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
		//iB = new InternalCtxBroker();	
	//	iB.setUserCtxDBMgr(new UserCtxDBMgr());
	
	}


	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
		//internalCtxBroker = null;
	}
/*

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

		//		CommunityContextEstimation cce = new CommunityContextEstimation();
		//		Integer res = cce.estimateContext(EstimationModels.MEDIAN, attrList);
		//		assertEquals(40, res);
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
		//		CommunityContextEstimation cce = new CommunityContextEstimation();
		//		Integer res = cce.estimateContext(EstimationModels.MEAN, attrList);
		//		assertEquals(40, res);
	}



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

		ArrayList<Point> a = cce.cceGeomConvexHull(setOfPoints);


		Assert.assertEquals(ExpectedsetOfConvexHullPoints,a);
	}

	@Test
	public void testCalculateStringStatistics() throws InterruptedException, ExecutionException, CtxException{

		List<CtxAttribute> attrList = new ArrayList<CtxAttribute>();
		ArrayList<String> stringList = new ArrayList<String>();

		//Create a person entity 1
		CtxEntity person1 = iB.createEntity("person1").get();				
		CtxEntityIdentifier pId1 = person1.getId();
		CtxAttribute person1Profession = this.iB.createAttribute(pId1, "profession").get(); 
		person1Profession.setValueType(CtxAttributeValueType.STRING);
		person1Profession = (CtxAttribute) iB.update(person1Profession).get();
		person1Profession.setStringValue("Engineer");
		attrList.add(person1Profession);
		stringList.add(person1Profession.getStringValue());

		//Create a person entity 2
		CtxEntity person2 = iB.createEntity("person2").get();
		CtxEntityIdentifier pId2 = person2.getId();
		System.out.println("PID2 = "+pId2);
		CtxAttribute person2Profession= iB.createAttribute(pId2, "profession").get(); 
		person2Profession.setValueType(CtxAttributeValueType.STRING);
		person2Profession = (CtxAttribute) iB.update(person2Profession).get();
		person2Profession.setStringValue("Chef");
		attrList.add(person2Profession);
		stringList.add(person2Profession.getStringValue());

		//Create a person entity 3
		CtxEntity person3 = iB.createEntity("person3").get();
		CtxEntityIdentifier pId3 = person3.getId();
		CtxAttribute person3Profession = this.iB.createAttribute(pId3, "profession").get(); 
		person3Profession.setValueType(CtxAttributeValueType.STRING);
		person3Profession = (CtxAttribute) iB.update(person3Profession).get();
		person3Profession.setStringValue("Cook");
		attrList.add(person3Profession);
		stringList.add(person3Profession.getStringValue());

		//Create a person entity 4
		CtxEntity person4 = iB.createEntity("person4").get();				
		CtxEntityIdentifier pId4 = person4.getId();
		CtxAttribute person4Profession = this.iB.createAttribute(pId4, "profession").get(); 
		person4Profession.setValueType(CtxAttributeValueType.STRING);
		person4Profession = (CtxAttribute) iB.update(person4Profession).get();
		person4Profession.setStringValue("Engineer");
		attrList.add(person4Profession);
		stringList.add(person4Profession.getStringValue());

		//Create a person entity 5
		CtxEntity person5 = iB.createEntity("person5").get();				
		CtxEntityIdentifier pId5 = person5.getId();
		CtxAttribute person5Profession = this.iB.createAttribute(pId5, "profession").get(); 
		person5Profession.setValueType(CtxAttributeValueType.STRING);
		person5Profession = (CtxAttribute) iB.update(person5Profession).get();
		person5Profession.setStringValue("Plumber");
		attrList.add(person5Profession);
		stringList.add(person5Profession.getStringValue());

		//Create a person entity 6
		CtxEntity person6 = iB.createEntity("person6").get();				
		CtxEntityIdentifier pId6 = person1.getId();
		CtxAttribute person6Profession = this.iB.createAttribute(pId6, "profession").get(); 
		person6Profession.setValueType(CtxAttributeValueType.STRING);
		person6Profession = (CtxAttribute) iB.update(person6Profession).get();
		person6Profession.setStringValue("Engineer");
		attrList.add(person6Profession);
		stringList.add(person6Profession.getStringValue());

		//Create a person entity 7
		CtxEntity person7 = iB.createEntity("person7").get();				
		CtxEntityIdentifier pId7 = person7.getId();
		CtxAttribute person7Profession = this.iB.createAttribute(pId7, "profession").get(); 
		person7Profession.setValueType(CtxAttributeValueType.STRING);
		person7Profession = (CtxAttribute) iB.update(person7Profession).get();
		person7Profession.setStringValue("Engineer");
		attrList.add(person7Profession);
		stringList.add(person7Profession.getStringValue());

		//Create a person entity 8
		CtxEntity person8 = iB.createEntity("person8").get();				
		CtxEntityIdentifier pId8 = person8.getId();
		CtxAttribute person8Profession = this.iB.createAttribute(pId8, "profession").get(); 
		person8Profession.setValueType(CtxAttributeValueType.STRING);
		person8Profession = (CtxAttribute) iB.update(person8Profession).get();
		person8Profession.setStringValue("Engineer");
		attrList.add(person8Profession);
		stringList.add(person8Profession.getStringValue());

		//Create a person entity 9
		CtxEntity person9 = iB.createEntity("person9").get();				
		CtxEntityIdentifier pId9 = person1.getId();
		CtxAttribute person9Profession = this.iB.createAttribute(pId9, "profession").get(); 
		person9Profession.setValueType(CtxAttributeValueType.STRING);
		person9Profession = (CtxAttribute) iB.update(person9Profession).get();
		person9Profession.setStringValue("Plumber");
		attrList.add(person9Profession);
		stringList.add(person9Profession.getStringValue());

		//Create a person entity 10
		CtxEntity person10 = iB.createEntity("person10").get();				
		CtxEntityIdentifier pId10 = person1.getId();
		CtxAttribute person10Profession = this.iB.createAttribute(pId10, "profession").get(); 
		person10Profession.setValueType(CtxAttributeValueType.STRING);
		person10Profession = (CtxAttribute) iB.update(person10Profession).get();
		person10Profession.setStringValue("Chef");
		attrList.add(person10Profession);
		stringList.add(person10Profession.getStringValue());

		//Run the tests!!
		CommunityContextEstimation cce = new CommunityContextEstimation();
		//Hashtable<String, Integer> res = cce.cceStringMode(stringList);
		//ArrayList<String> res = cce.cceStringMode(stringList);
		Hashtable<String, Integer> expectedHashMapTable = new Hashtable<String, Integer>();
		expectedHashMapTable.put("Engineer", 5);
		expectedHashMapTable.put("Plumber", 2);
		expectedHashMapTable.put("Cook", 1);
		expectedHashMapTable.put("Chef", 2);
		//assertEquals(expectedHashMapTable, res);
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


		System.out.println("Ta POINT EINAI "+setOfPoints.get(0)+ " "+ setOfPoints.get(1));
		Point[] a = cce.cceGeomMinBB(setOfPoints);
		for (int i=0; i<a.length; i++){
			System.out.println("Afto pou bgazei h methodos einai to "+a[i]);
			System.out.println("To sosto einai"+ExpectedsetOfBBPoints[i]);
		}


		Assert.assertEquals(ExpectedsetOfBBPoints,a);

	}
*/

}