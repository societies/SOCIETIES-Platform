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

package org.societies.integration.test.bit.caui_prediction;

import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.useragent.monitoring.IUserActionMonitor;

public class ContextStorageTest {

	private static Logger LOG = LoggerFactory.getLogger(TestCase1109.class);

	public void setUp(){
		System.out.println("Test 749 started : ContextStorageTest");
	}

	@Test
	public void TestMonitorActionsContext() {

		//create actions
		IIdentity identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
		ServiceResourceIdentifier serviceId1 = new ServiceResourceIdentifier();
		ServiceResourceIdentifier serviceId2 = new ServiceResourceIdentifier();
		try {
			serviceId1.setIdentifier(new URI("http://testService1"));
			serviceId2.setIdentifier(new URI("http://testService2"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}

		//create actions
		IAction action1 = new Action(serviceId1, "testService", "volume", "high");
		IAction action2 = new Action(serviceId1, "testService", "volume", "medium");
		IAction action3 = new Action(serviceId1, "testService", "volume", "low");
		IAction action4 = new Action(serviceId1, "testService", "colour", "blue");
		//IAction action5 = new Action(serviceId1, "testService", "colour", "green");
		//{volume=medium/2=null, volume=low/0={volume=high/1=1.0}, volume=high/1={volume=medium/2=1.0}} 
		//set context data
		setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "home");
		setContext(CtxAttributeTypes.TEMPERATURE, 25);
		setContext(CtxAttributeTypes.STATUS, "free");


		

			//send actions - 1 second apart
			LOG.info("Monitor services #749 - sending mock actions for storage");
			TestCase1109.uam.monitor(identity, action1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "office");
			setContext(CtxAttributeTypes.TEMPERATURE, 45);
			setContext(CtxAttributeTypes.STATUS, "busy");

			TestCase1109.uam.monitor(identity, action2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC,"park");
			setContext(CtxAttributeTypes.TEMPERATURE, 45);
			setContext(CtxAttributeTypes.STATUS, "busy");

			TestCase1109.uam.monitor(identity, action3);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "zoo");
			setContext(CtxAttributeTypes.TEMPERATURE, 45);
			setContext(CtxAttributeTypes.STATUS, "busy");

			TestCase1109.uam.monitor(identity, action4);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			setContext(CtxAttributeTypes.LOCATION_SYMBOLIC, "university");
			setContext(CtxAttributeTypes.TEMPERATURE, 45);
			setContext(CtxAttributeTypes.STATUS, "busy");

			TestCase1109.uam.monitor(identity, action1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}

			TestCase1109.uam.monitor(identity, action2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
			TestCase1109.uam.monitor(identity, action3);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
		
			TestCase1109.uam.monitor(identity, action4);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			TestCase1109.uam.monitor(identity, action1);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			
			TestCase1109.uam.monitor(identity, action2);
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
			}
			/*
		 * CHECK HISTORY DATA
		 */
		LOG.info("*********** CHECK HISTORY DATA ************");
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		try {
			tupleResults  = TestCase1109.ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, listOfEscortingAttributeIds, null, null).get();
			//Assert.assertTrue(tupleResults.size() == 5);
			LOG.info("hoc size:"+ tupleResults.size());
			printHocTuplesDB(tupleResults);

			
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	private CtxAttribute setContext(String type, Serializable value){

		CtxAttribute attr = null; 
		try {
			IndividualCtxEntity operator = TestCase1109.ctxBroker.retrieveCssOperator().get();

			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = TestCase1109.ctxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = TestCase1109.ctxBroker.createAttribute(operator.getId(), type).get();
				attr = TestCase1109.ctxBroker.updateAttribute(attr.getId(),value).get();
			}

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
		return attr;
	}


	private void printAttr(CtxEntity entity) throws InterruptedException, ExecutionException, CtxException{

		System.out.println("operator: "+entity);
		Set<CtxAttribute> attrSet = entity.getAttributes();
		System.out.println("operator attrs : "+attrSet);
		for(CtxAttribute attrs: attrSet){
			System.out.println("attr type: "+attrs.getType());
			if(attrs.getStringValue() != null) System.out.println(" value "+attrs.getStringValue());
		}
	}

	protected CtxAttribute lookupRetrieveAttrHelp(String type){
		CtxAttribute ctxAttr = null;
		try {
			List<CtxIdentifier> tupleAttrList = TestCase1109.ctxBroker.lookup(CtxModelType.ATTRIBUTE,type).get();
			if(tupleAttrList.size() >0 ){
				CtxIdentifier ctxId = tupleAttrList.get(0);
				ctxAttr =  (CtxAttribute) TestCase1109.ctxBroker.retrieve(ctxId).get();	
				System.out.println("lookupRetrieveAttrHelp "+ ctxAttr);
			}
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
		return ctxAttr;
	}


	protected void printHocTuplesDB(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		LOG.info("printing Tuples");
		int i = 0;
		for (CtxHistoryAttribute primary : tupleResults.keySet()){

			try {
				IAction action = (IAction)SerialisationHelper.deserialise(primary.getBinaryValue(),this.getClass().getClassLoader());
				LOG.info(i+ " action name: "+action.getparameterName()+" action value: "+action.getvalue()+ " action service "+action.getServiceID().getIdentifier());
				for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
					String result = getValue(escortingAttr);
					LOG.info("escording attribute type: "+escortingAttr.getType()+" value:"+result);
				}
				i++;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	protected String getValue(CtxHistoryAttribute attribute){

		String result = "";

		if (attribute.getStringValue()!=null) {
			result = attribute.getStringValue();
			return result;             			
		}
		else if(attribute.getIntegerValue()!=null) {
			Integer valueInt = attribute.getIntegerValue();
			result = valueInt.toString();
			return result; 
		} else if (attribute.getDoubleValue()!=null) {
			Double valueDouble = attribute.getDoubleValue();
			result = valueDouble.toString();  			
			return result; 
		} 
		return result; 
	}
}