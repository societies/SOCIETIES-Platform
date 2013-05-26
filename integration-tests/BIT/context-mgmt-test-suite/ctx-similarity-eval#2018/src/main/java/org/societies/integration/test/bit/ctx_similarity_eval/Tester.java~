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
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.context.model.CtxEvaluationResults;
import org.springframework.stereotype.Service;
//import org.societies.context.similarity.api.similarity.CtxEvaluationResults;

/**
 * @author eboylan
 *
 */
@Service
public class Tester {

	private ICtxBroker ctxBroker;

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	CtxEntity jack = null;
	CtxEntity jane = null;
	CtxEntityIdentifier jackID = null;
	CtxEntityIdentifier janeID = null;
	CtxEvaluationResults ie = null;
	
	public Tester(){

	}

	@Before
	public void setUp(){
	
		this.ctxBroker = Test2018.getCtxBroker();
		
		try {
			jack = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
			jane = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();
		} catch (InterruptedException e) {
			e.printStackTrace(); 
		} catch (ExecutionException e) {
			e.printStackTrace(); 
		} catch (CtxException e) {
			e.printStackTrace();
		}
		jackID = jack.getId();
		janeID = jane.getId();	
	}

	@After
	public void tearDown(){

		try {
			LOG.info("*** tear down **** " );
			//if(serviceA != null ) this.ctxBroker.remove(serviceA.getId());
			//if(serviceB != null ) this.ctxBroker.remove(serviceB.getId());
			if(jack != null ) this.ctxBroker.remove(jackID);
			if(jane != null ) this.ctxBroker.remove(janeID);
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Test
	public void Test(){

		this.ctxBroker = Test2018.getCtxBroker();

		try {
			LOG.info("EBOYLANLOGFOOTPRINT starting user creation");
			//to create occupation attribute for Jack
			CtxAttribute ctxAttrOccupationJack = this.ctxBroker.createAttribute(jackID,CtxAttributeTypes.OCCUPATION).get();
			//set occupation attribute
			ctxAttrOccupationJack.setStringValue("Doctor");
			ctxAttrOccupationJack.setValueType(CtxAttributeValueType.STRING);
			LOG.info("EBOYLANLOGFOOTPRINT Jack: "+ctxAttrOccupationJack.getStringValue());

			//to create occupation attribute for Jane
			CtxAttribute ctxAttrOccupationJane = this.ctxBroker.createAttribute(janeID, CtxAttributeTypes.OCCUPATION).get();
			//set occupation attribute
			ctxAttrOccupationJane.setStringValue("Pilot");
			ctxAttrOccupationJane.setValueType(CtxAttributeValueType.STRING);
			LOG.info("EBOYLANLOGFOOTPRINT Jane: "+ctxAttrOccupationJane.getStringValue());
			
			String[] ids = {jackID.toString(),janeID.toString()};
			ArrayList<String> attrib = new ArrayList<String>();

			attrib.add("occupation");
			LOG.info("EBOYLANLOGFOOTPRINT attrib tested = " + attrib.get(0).toString());
			//LOG.info("EBOYLANLOGFOOTPRINT ID's: " + janeID.toString() + jackID.toString());
			LOG.info("EBOYLANLOGFOOTPRINT ID's: " + janeID + jackID);
			LOG.info("EBOYLANLOGFOOTPRINT broker: " + ctxBroker);
			
			//Future<List<Object>> areSimilar = (Future<List<Object>>) this.ctxBroker.evaluateSimilarity(ids, attrib);
			LOG.info("EBOYLANLOGFOOTPRINT : Start estimating similarity ...");
			//LOG.info("EBOYLANLOGFOOTPRINT occupation jack:"+  ctxAttrOccupationJack.getStringValue() +"occupation jane:"+ ctxAttrOccupationJane.getStringValue()+" are similar:"+ areSimilar);
			//assertEquals(areSimilar.toString(), "False");
			//LOG.info("EBOYLANLOGFOOTPRINT aresimilar: " + areSimilar.toString());
			
			//LOG.info("EBOYLANLOGFOOTPRINT : Start estimating similarity....");
			//Boolean sim = (Boolean) this.ctxBroker.evaluateSimilarity(ctxAttrOccupationJack.getStringValue(), ctxAttrOccupationJane.getStringValue()).get();
			//LOG.info("EBOYLANLOGFOOTPRINT" + sim.toString());
			
			CtxEvaluationResults ie = (this.ctxBroker.evaluateSimilarity(ids, attrib));
			LOG.info("EBOYLANLOGFOOTPRINT : " + ie.getResult().toString());
			assertFalse(ie.getResult());

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

	}

	@Test
	public void test1() {
		//pass if contextSimilarity returns true
		String[] ids = {jackID.toString(),janeID.toString()};
		ArrayList<String> attrib = new ArrayList<String>();
		LOG.info("EBOYLANLOGFOOTPRINT starting test 2");

		//to create movies attribute for Jack
		CtxAttribute ctxAttrMoviesJack;
		try {
			ctxAttrMoviesJack = this.ctxBroker.createAttribute(jackID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesJack.setStringValue("Dracula");
			ctxAttrMoviesJack.setValueType(CtxAttributeValueType.STRING);

			CtxAttribute ctxAttrMoviesJane = this.ctxBroker.createAttribute(janeID,CtxAttributeTypes.MOVIES).get();
			//set movie attribute
			ctxAttrMoviesJane.setStringValue("Dracula");
			ctxAttrMoviesJane.setValueType(CtxAttributeValueType.STRING);

			attrib.add("movies");
			LOG.info("EBOYLANLOGFOOTPRINT jacks movie: " + ctxAttrMoviesJack.toString());
			CtxEvaluationResults ie = (CtxEvaluationResults) ( this.ctxBroker.evaluateSimilarity(ids, attrib));
			LOG.info("EBOYLANLOGFOOTPRINT " + ie.getResult());
			assertFalse(ie.getResult());
			
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

		assertTrue(ie.getResult());
	}

}