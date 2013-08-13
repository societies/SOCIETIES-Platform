/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp.,
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM
 * ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */


package org.societies.integration.test.bit.ctx_similarity_eval;
import static org.junit.Assert.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxEvaluationResults;
import org.springframework.stereotype.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
//import org.societies.context.similarity.api.similarity.CtxEvaluationResults;

import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxIdentifier;

/**
 * @author eboylan
 *
 */
@Service
public class Tester {

	private ICtxBroker ctxBroker;
	private static Logger LOG = LoggerFactory.getLogger(Tester.class);
	Requestor req = null;
	//private RequestorService requestorService = null;
	//private ServiceResourceIdentifier myServiceID;

	CtxEntity jack = null;
	CtxEntity jane = null;
	CtxEntity ken = null;
	CtxEntity kelly = null;
	CtxEntityIdentifier jackID = null;
	CtxEntityIdentifier janeID = null;
	CtxEntityIdentifier kenID = null;
	CtxEntityIdentifier kellyID = null;
	CtxEvaluationResults ie = null;
	
	
	public Tester(){

	}

	@Before
	public void setUp(){
	
		this.ctxBroker = Test2018.getCtxBroker();
		
		try {
			INetworkNode cssNodeId = Test2018.getCommManager().getIdManager().getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = Test2018.getCommManager().getIdManager().fromJid(cssOwnerStr);
			this.req = new Requestor(cssOwnerId);
			
			IIdentity targetCss = null;
			
			jack = Test2018.getCtxBroker().createEntity(req, targetCss, CtxEntityTypes.PERSON).get();
			jane = Test2018.getCtxBroker().createEntity(req, targetCss, CtxEntityTypes.PERSON).get();
			ken = Test2018.getCtxBroker().createEntity(req, targetCss, CtxEntityTypes.PERSON).get();
			kelly = Test2018.getCtxBroker().createEntity(req, targetCss, CtxEntityTypes.PERSON).get();
			
			//jack = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
			//jane = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
			//ken = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
			//kelly = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
		} catch (InterruptedException e) {
			e.printStackTrace(); 
		} catch (ExecutionException e) {
			e.printStackTrace(); 
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		jackID = jack.getId();
		janeID = jane.getId();
		kenID = ken.getId();
		kellyID = kelly.getId();
	}

	@After
	public void tearDown(){

		try {
			LOG.info("*** tear down **** " );
			//if(serviceA != null ) this.ctxBroker.remove(serviceA.getId());
			//if(serviceB != null ) this.ctxBroker.remove(serviceB.getId());
			if(jack != null ) this.ctxBroker.remove(jackID);
			if(jane != null ) this.ctxBroker.remove(janeID);
			if(ken != null ) this.ctxBroker.remove(kenID);
			if(kelly != null ) this.ctxBroker.remove(kellyID);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void test0(){

		//assertNotNull(ctxBroker);
		
		try {
			LOG.info("EBOYLANLOGFOOTPRINT starting test0");
			//to create occupation attribute for Jack
			CtxAttribute ctxAttrOccupationJack = this.ctxBroker.createAttribute(jackID,CtxAttributeTypes.OCCUPATION).get();
			//set occupation attribute
			ctxAttrOccupationJack.setStringValue("Construction Managers");
			ctxAttrOccupationJack.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrOccupationJack = (CtxAttribute) this.ctxBroker.update(ctxAttrOccupationJack).get();
			final MyCtxAttrChangeEventListener listener = new MyCtxAttrChangeEventListener();
			this.ctxBroker.registerForChanges(listener, this.jackID);
			
			this.ctxBroker.updateAttribute(ctxAttrOccupationJack.getId(),ctxAttrOccupationJack.getStringValue());
			//LOG.info("EBOYLANLOGFOOTPRINT Jack: "+ ctxAttrOccupationJack.getStringValue());
			LOG.info("EBOYLANLOGFOOTPRINT Jack: "+ this.ctxBroker.retrieve(ctxAttrOccupationJack.getId()).toString());

			//to create occupation attribute for Jane
			CtxAttribute ctxAttrOccupationJane = this.ctxBroker.createAttribute(janeID, CtxAttributeTypes.OCCUPATION).get();
			//set occupation attribute
			ctxAttrOccupationJane.setStringValue("Commercial Pilots");
			ctxAttrOccupationJane.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrOccupationJane = (CtxAttribute) this.ctxBroker.update(ctxAttrOccupationJane).get();
			
			this.ctxBroker.registerForChanges(listener, this.janeID);
			this.ctxBroker.updateAttribute(ctxAttrOccupationJane.getId(),ctxAttrOccupationJane.getStringValue());
			//LOG.info("EBOYLANLOGFOOTPRINT Jane: "+ctxAttrOccupationJane.getStringValue());
			//LOG.info("EBOYLANLOGFOOTPRINT Jack: "+ this.ctxBroker.lookup(ctxAttrOccupationJack.getStringValue()).get());
			
			String[] ids = {jackID.toString(),janeID.toString()};
			ArrayList<String> attrib = new ArrayList<String>();

			attrib.add("occupation");
			LOG.info("EBOYLANLOGFOOTPRINT attrib tested = " + attrib.get(0).toString());
			//LOG.info("EBOYLANLOGFOOTPRINT ID's: " + janeID.toString() + jackID.toString());
			LOG.info("EBOYLANLOGFOOTPRINT ID's: " + janeID + jackID);
			LOG.info("EBOYLANLOGFOOTPRINT broker: " + ctxBroker);
			
			//Future<List<Object>> areSimilar = (Future<List<Object>>) this.ctxBroker.evaluateSimilarity(ids, attrib);
			LOG.info("EBOYLANLOGFOOTPRINT : Start estimating similarity ...");
			
			List<CtxIdentifier> occupationAttrList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.OCCUPATION).get();
			LOG.info("occupationAttrList : " + occupationAttrList);
			if(!occupationAttrList.isEmpty() ){
				for(CtxIdentifier attOccID : occupationAttrList){
					CtxAttribute movieAttr = (CtxAttribute) this.ctxBroker.retrieve(attOccID).get();
					CtxEvaluationResults ie = (this.ctxBroker.evaluateSimilarity(ids, attrib));
					LOG.info("EBOYLANGETRESULT t0: " + ie.getResult());
					LOG.info("EBOYLANGETSUMMARY t0: " + ie.getSummary());
					LOG.info("EBOYLANGETATTBREAKDOWN t0: " + ie.getAttBreakDown());
					assertEquals(true, ie.getResult());
				}
			}
			
			this.ctxBroker.unregisterFromChanges(listener, this.jackID);
			this.ctxBroker.unregisterFromChanges(listener, this.janeID);

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Interrupted Exception");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Execution Exception");
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Context Exception");
			e.printStackTrace();
		} catch (Exception e) {
			LOG.info("EBOYLANLOGFOOTPRINT Tester: " + e.toString());
			e.printStackTrace();
		}
	    
		//assertTrue(true);

	}

	@Test
	public void test1() {
		
		
		//pass if contextSimilarity returns true
		String[] ids = {jackID.toString(),janeID.toString()};
		ArrayList<String> attrib = new ArrayList<String>();
		LOG.info("EBOYLANLOGFOOTPRINT starting test1");

		//to create movies attribute for Jack
		try {
			CtxAttribute ctxAttrMoviesJack = this.ctxBroker.createAttribute(jackID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesJack.setStringValue("Dracula");
			ctxAttrMoviesJack.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrMoviesJack = (CtxAttribute) this.ctxBroker.update(ctxAttrMoviesJack).get();
			final MyCtxAttrChangeEventListener listener = new MyCtxAttrChangeEventListener();
			this.ctxBroker.registerForChanges(listener, this.jackID);
					
			this.ctxBroker.updateAttribute(ctxAttrMoviesJack.getId(),ctxAttrMoviesJack.getStringValue());

			CtxAttribute ctxAttrMoviesJane = this.ctxBroker.createAttribute(janeID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesJane.setStringValue("Dracula");
			ctxAttrMoviesJane.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrMoviesJane = (CtxAttribute) this.ctxBroker.update(ctxAttrMoviesJane).get();
			this.ctxBroker.registerForChanges(listener, this.janeID);
			this.ctxBroker.updateAttribute(ctxAttrMoviesJane.getId(),ctxAttrMoviesJane.getStringValue());
			
			attrib.add("movies");
			
			//CtxEvaluationResults ie = (CtxEvaluationResults) ( this.ctxBroker.evaluateSimilarity(ids, attrib));
			List<CtxIdentifier> moviesAttrList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.MOVIES).get();
			LOG.info("moviesAttrList : " + moviesAttrList);
			if(!moviesAttrList.isEmpty() ){
				for(CtxIdentifier attMovID : moviesAttrList){
					CtxAttribute movieAttr = (CtxAttribute) this.ctxBroker.retrieve(attMovID).get();
					//LOG.info("EBOYLANLOGFOOTPRINT jacks movie: " + this.ctxBroker.lookup(ctxAttrMoviesJack.getStringValue()).get());
					CtxEvaluationResults ie = (this.ctxBroker.evaluateSimilarity(ids, attrib));
					LOG.info("EBOYLANGETRESULT t1: " + ie.getResult());
					LOG.info("EBOYLANGETSUMMARY t1: " + ie.getSummary());
					LOG.info("EBOYLANGETATTBREAKDOWN t1: " + ie.getAttBreakDown());
					assertEquals(false, ie.getResult());
				}
			}
			
			this.ctxBroker.unregisterFromChanges(listener, this.jackID);
			this.ctxBroker.unregisterFromChanges(listener, this.janeID);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Interrupted Exception");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Execution Exception");
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Context Exception");
			e.printStackTrace();
		}
	}
	
	@Test
	public void test2() {
		
		
		//pass if contextSimilarity returns true
		String[] ids = {jackID.toString(),janeID.toString(),kenID.toString(),kellyID.toString()};
		ArrayList<String> attrib = new ArrayList<String>();
		LOG.info("EBOYLANLOGFOOTPRINT starting test2");
		
		try {
			//to create movies attribute for Jack
			CtxAttribute ctxAttrMoviesKen = this.ctxBroker.createAttribute(kenID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesKen.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrMoviesKen = (CtxAttribute) this.ctxBroker.update(ctxAttrMoviesKen).get();
			final MyCtxAttrChangeEventListener listener = new MyCtxAttrChangeEventListener();
			this.ctxBroker.registerForChanges(listener, this.kenID);
			this.ctxBroker.updateAttribute(ctxAttrMoviesKen.getId(),ctxAttrMoviesKen.getStringValue());
			
			CtxAttribute ctxAttrMoviesKelly = this.ctxBroker.createAttribute(kellyID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesKelly.setStringValue("Jaws, Batman");
			ctxAttrMoviesKelly.setValueType(CtxAttributeValueType.STRING);
			//ctxAttrMoviesKelly = (CtxAttribute) this.ctxBroker.update(ctxAttrMoviesKelly).get();
			this.ctxBroker.registerForChanges(listener, this.kellyID);
			this.ctxBroker.updateAttribute(ctxAttrMoviesKelly.getId(),ctxAttrMoviesKelly.getStringValue());

			attrib.add("movies");
			attrib.add("occupation");
			
			List<CtxIdentifier> multAttrList = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.OCCUPATION).get();
			multAttrList.addAll(this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.MOVIES).get());
			LOG.info("multAttrList : " + multAttrList);
			if(!multAttrList.isEmpty() ){
				for(CtxIdentifier attMultID : multAttrList){
					CtxAttribute movieAttr = (CtxAttribute) this.ctxBroker.retrieve(attMultID).get();
					CtxEvaluationResults ie = (this.ctxBroker.evaluateSimilarity(ids, attrib));
					LOG.info("EBOYLANGETRESULT t2: " + ie.getResult());
					LOG.info("EBOYLANGETSUMMARY t2: " + ie.getSummary());
					LOG.info("EBOYLANGETATTBREAKDOWN t2: " + ie.getAttBreakDown());
					assertEquals(false, ie.getResult());
				}
			}
			
			this.ctxBroker.unregisterFromChanges(listener, this.kenID);
			this.ctxBroker.unregisterFromChanges(listener, this.kellyID);
			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Interrupted Exception");
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Execution Exception");
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			LOG.info("EBOYLANLOGFOOTPRINT Context Exception");
			e.printStackTrace();
		}
	}

}

class MyCtxAttrChangeEventListener implements CtxChangeEventListener {

	private CtxIdentifier receivedId;
	private String receivedValue;

	MyCtxAttrChangeEventListener() {
	}

	@Override
	public void onCreation(CtxChangeEvent event) {
		
		// TODO Auto-generated method stub
	}

	@Override
	public void onUpdate(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onModification(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRemoval(CtxChangeEvent event) {
		// TODO Auto-generated method stub
		
	}
}