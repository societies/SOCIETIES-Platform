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


package org.societies.integration.test.bit.comm_ctx_estimation;
import static org.junit.Assert.*;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

import junit.framework.Assert;

import org.junit.Before;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.internal.context.broker.ICtxBroker;


/**
 * Utility class that creates mock actions
 *
 * @author Yiorgos
 *
 */
public class Tester {
	
	private ICtxBroker bro;
	
	public Tester(){

	}
	
	@Before
	public void setUp(){
			
	}
	
	
	@org.junit.Test
	public void Test(){
	bro = Test1108.getCtxBroker();
	
	}
	
	/*
	@org.junit.Test	
	public void testConvexHull(){
		
		ConvexHull cH = new ConvexHull();
		Random rand1 = new Random();
		Random rand2 = new Random();
		ArrayList<Point> setOfPoints=new ArrayList<Point>();
		ArrayList<Point> setOfConvexHullPoints = new ArrayList<Point>();
		
//		for (int i=0; i<1000; ++i){
//			Point p = new Point();
//			p.x=rand1.nextInt(1000);
//			p.y = rand2.nextInt(1000);
//			setOfPoints.add(p);
//		}
		
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
//		for (int i=0; i<setOfPoints.size();++i){
//			System.out.println("Stoixeio "+i+" is "+setOfPoints.get(i).x+", "+setOfPoints.get(i).y);
//		}
		
		//ArrayList<Point> a = cH.quickHull(setOfPoints);
		ArrayList<Point> a = cH.qHull(setOfPoints);
		
		for (int z=0; z<a.size();++z){
		System.out.println("ConvexHulla["+z+"] = "+a.get(z));
	}
		Assert.assertEquals(a,setOfConvexHullPoints) ;
//		for (int i=0; i<setOfPoints.size();++i){
//			System.out.println("Stoixeio tou Hull "+i+" is "+a.get(i).x+", "+a.get(i).y);
//		}

		//return setOfPoints;
	}
	@org.junit.Test
	public void testCalculateStringStatistics() throws InterruptedException, ExecutionException, CtxException{
		
		List<CtxAttribute> attrList = new ArrayList<CtxAttribute>();
		
		//Create a person entity 1
		CtxEntity person1 = this.bro.createEntity("person1").get();				
		CtxEntityIdentifier pId1 = person1.getId();
		CtxAttribute person1Age = this.bro.createAttribute(pId1, "profession").get(); 
		person1Age.setValueType(CtxAttributeValueType.STRING);
		person1Age = (CtxAttribute) this.bro.update(person1Age).get();
		person1Age.setStringValue("Engineer");
		attrList.add(person1Age);

		//Create a person entity 2
		CtxEntity person2 = this.bro.createEntity("person2").get();
		CtxEntityIdentifier pId2 = person2.getId();
		System.out.println("PID2 = "+pId2);
		CtxAttribute person2Age = this.bro.createAttribute(pId2, "profession").get(); 
		person2Age.setValueType(CtxAttributeValueType.STRING);
		person2Age = (CtxAttribute) this.bro.update(person2Age).get();
		person2Age.setStringValue("Chef");
		attrList.add(person2Age);

		//Create a person entity 3
		CtxEntity person3 = this.bro.createEntity("person3").get();
		CtxEntityIdentifier pId3 = person3.getId();
		CtxAttribute person3Age = this.bro.createAttribute(pId3, "age").get(); 
		person3Age.setValueType(CtxAttributeValueType.STRING);
		person3Age = (CtxAttribute) this.bro.update(person3Age).get();
		person3Age.setStringValue("Cook");
		attrList.add(person3Age);
		
		//Create a person entity 4
		CtxEntity person4 = this.bro.createEntity("person4").get();				
		CtxEntityIdentifier pId4 = person4.getId();
		CtxAttribute person4Age = this.bro.createAttribute(pId4, "profession").get(); 
		person4Age.setValueType(CtxAttributeValueType.STRING);
		person4Age = (CtxAttribute) this.bro.update(person4Age).get();
		person4Age.setStringValue("Engineer");
		attrList.add(person4Age);
		
		//Create a person entity 5
		CtxEntity person5 = this.bro.createEntity("person5").get();				
		CtxEntityIdentifier pId5 = person5.getId();
		CtxAttribute person5Age = this.bro.createAttribute(pId5, "profession").get(); 
		person5Age.setValueType(CtxAttributeValueType.STRING);
		person5Age = (CtxAttribute) this.bro.update(person5Age).get();
		person5Age.setStringValue("Plumber");
		attrList.add(person5Age);
		
		//Create a person entity 6
		CtxEntity person6 = this.bro.createEntity("person6").get();				
		CtxEntityIdentifier pId6 = person1.getId();
		CtxAttribute person6Age = this.bro.createAttribute(pId6, "profession").get(); 
		person6Age.setValueType(CtxAttributeValueType.STRING);
		person6Age = (CtxAttribute) this.bro.update(person6Age).get();
		person6Age.setStringValue("Engineer");
		attrList.add(person6Age);
		
		//Create a person entity 7
		CtxEntity person7 = this.bro.createEntity("person7").get();				
		CtxEntityIdentifier pId7 = person7.getId();
		CtxAttribute person7Age = this.bro.createAttribute(pId7, "profession").get(); 
		person7Age.setValueType(CtxAttributeValueType.STRING);
		person7Age = (CtxAttribute) this.bro.update(person7Age).get();
		person7Age.setStringValue("Engineer");
		attrList.add(person7Age);
		
		//Create a person entity 8
		CtxEntity person8 = this.bro.createEntity("person8").get();				
		CtxEntityIdentifier pId8 = person8.getId();
		CtxAttribute person8Age = this.bro.createAttribute(pId8, "profession").get(); 
		person8Age.setValueType(CtxAttributeValueType.STRING);
		person8Age = (CtxAttribute) this.bro.update(person8Age).get();
		person8Age.setStringValue("Engineer");
		attrList.add(person8Age);
		
		//Create a person entity 9
		CtxEntity person9 = this.bro.createEntity("person9").get();				
		CtxEntityIdentifier pId9 = person1.getId();
		CtxAttribute person9Age = this.bro.createAttribute(pId9, "profession").get(); 
		person9Age.setValueType(CtxAttributeValueType.STRING);
		person9Age = (CtxAttribute) this.bro.update(person9Age).get();
		person9Age.setStringValue("Plumber");
		attrList.add(person9Age);
		
		//Create a person entity 10
		CtxEntity person10 = this.bro.createEntity("person10").get();				
		CtxEntityIdentifier pId10 = person1.getId();
		CtxAttribute person10Age = this.bro.createAttribute(pId10, "profession").get(); 
		person10Age.setValueType(CtxAttributeValueType.STRING);
		person10Age = (CtxAttribute) this.bro.update(person10Age).get();
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
*/
}