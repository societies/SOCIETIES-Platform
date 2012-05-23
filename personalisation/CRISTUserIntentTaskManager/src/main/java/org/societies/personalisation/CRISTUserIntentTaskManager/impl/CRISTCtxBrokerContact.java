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
package org.societies.personalisation.CRISTUserIntentTaskManager.impl;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.Action;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.useragent.monitoring.IUserActionMonitor;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.personalisation.CRISTUserIntentDiscovery.impl.CRISTHistoryData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is responsible to communicate with CtxDB using CtxBroker in OSGi
 * on behalf of CRIST.
 * 
 * @author Zhiyong Yu
 * 
 */

public class CRISTCtxBrokerContact {

	private final static Logger LOG = LoggerFactory.getLogger(CRISTCtxBrokerContact.class);

	private ICtxBroker internalCtxBroker;
	private IndividualCtxEntity operator;
	private IUserActionMonitor uam;
	private IIdentity identity;

	@Autowired(required = true)
	public CRISTCtxBrokerContact(ICtxBroker ctxBroker) {

		internalCtxBroker = ctxBroker;

		try {
			operator = this.internalCtxBroker.retrieveCssOperator().get();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		
		

		// this.registerForContextChangeEvent();

	}
	
	public IIdentity getIdentity()
	{
		if (identity == null)
		{
			identity = new MockIdentity(IdentityType.CSS, "user", "societies.org");
			LOG.info("First time, create identity: " + identity);
		}
		return identity ;
	}

	public CtxEntityIdentifier getContextEntity(String ctxEntityType) {
		if (lookupContextEntity(ctxEntityType) == null || lookupContextEntity(ctxEntityType).size() == 0) {
			return createContextEntity(ctxEntityType);
		}
		return (CtxEntityIdentifier) lookupContextEntity(ctxEntityType).get(0);// return
																				// the
																				// first
																				// one

	}

	public CtxEntityIdentifier createContextEntity(String ctxEntityType) {
		LOG.info("*** createContextEntity");
		Future<CtxEntity> futureEnt;
		try {
			futureEnt = this.internalCtxBroker.createEntity(ctxEntityType);
			CtxEntity ctxEntity = (CtxEntity) futureEnt.get();
			return ctxEntity.getId();
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CtxAttributeIdentifier getContextAttr(
			CtxEntityIdentifier ctxEntityIdentifier, String ctxAttrType) {
		if (lookupContextAttr(ctxAttrType) == null) {
			return createContextAttr(ctxEntityIdentifier, ctxAttrType);
		}
		List<CtxIdentifier> ctxAttributeIdList = lookupContextAttr(ctxAttrType);
		for (int i = 0; i < ctxAttributeIdList.size(); i++) {
			CtxAttribute ctxAttribute = retrieveContextAttr((CtxAttributeIdentifier) ctxAttributeIdList
					.get(i));

			if (ctxAttribute.getScope().equals(ctxEntityIdentifier))
				return ctxAttribute.getId();// return the first one
		}
		return createContextAttr(ctxEntityIdentifier, ctxAttrType);

	}

	public CtxAttributeIdentifier createContextAttr(
			CtxEntityIdentifier ctxEntityIdentifier, String ctxAttrType) {
		LOG.info("*** createContextAttr");
		try {
			Future<CtxAttribute> futureCtxAttrString = this.internalCtxBroker
					.createAttribute(ctxEntityIdentifier, ctxAttrType);
			// get the object of the created CtxAttribute
			CtxAttribute ctxAttribute = (CtxAttribute) futureCtxAttrString
					.get();

			// by setting this flag to true the CtxAttribute values will be
			// stored to Context History Database upon update
			ctxAttribute.setHistoryRecorded(true);
			return ctxAttribute.getId();
		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public void updateContextValue(
			CtxAttributeIdentifier ctxAttributeIdentifier, String ctxAttrValue) {

		LOG.info("*** updateContextValue");

		try {
			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker
					.retrieve(ctxAttributeIdentifier);
			CtxAttribute ctxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture
					.get();

			// set a string value to CtxAttribute
			ctxAttribute.setStringValue(ctxAttrValue);

			// with this update the attribute is stored in Context DB
			this.internalCtxBroker.update(ctxAttribute);//ConcurrentModificationException
			
			LOG.info("ctxAttribute: " + ctxAttribute + ", updated value: " + ctxAttribute.getStringValue());

		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}

	}

	/**
	 * This method demonstrates how to look up context Ids from the context
	 * database
	 */

	private List<CtxIdentifier> lookupContextEntity(String ctxEntityType) {
		try {

			List<CtxIdentifier> idsEntities = this.internalCtxBroker.lookup(
					CtxModelType.ENTITY, ctxEntityType).get();
			return idsEntities;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;
	}

	private List<CtxIdentifier> lookupContextAttr(String ctxAttrType) {
		try {
			List<CtxIdentifier> idsAttribute = this.internalCtxBroker.lookup(
					CtxModelType.ATTRIBUTE, ctxAttrType).get();
			return idsAttribute;

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * This method demonstrates how to retrieve context data from the context
	 * database
	 */
	public String retrieveContext(CtxAttributeIdentifier ctxAttributeIdentifier) {

		LOG.info("*** retrieveContext");

		try {

			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker
					.retrieve(ctxAttributeIdentifier);
			CtxAttribute retrievedCtxAttribute = (CtxAttribute) ctxAttributeRetrievedStringFuture
					.get();
			return retrievedCtxAttribute.getStringValue();

		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	public CtxAttribute retrieveContextAttr(
			CtxAttributeIdentifier ctxAttributeIdentifier) {
		try {

			Future<CtxModelObject> ctxAttributeRetrievedStringFuture = this.internalCtxBroker
					.retrieve(ctxAttributeIdentifier);
			return (CtxAttribute) ctxAttributeRetrievedStringFuture.get();

		} catch (CtxException e) {
			e.printStackTrace();
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
	}

	/*
	@Deprecated
	public ArrayList<CtxAttributeIdentifier> storeTuplesCtxHistory(CRISTHistoryData historyData) {


		LOG.debug("Let's check step by step, what's the problem.");
		
		ArrayList<String> registeredContext = CRISTHistoryData.getRegisteredContext();
		
		ArrayList<String> context = historyData.getContext(); // context = ["100","30","22","N/A"]
		String actionValue = historyData.getActionValue();
		String situationValue = historyData.getSituationValue();

		CtxEntityIdentifier ctxEntityIdentifier_UserContext = getContextEntity("UserContext");

		LOG.debug("ctxEntityIdentifier_UserContext: " + ctxEntityIdentifier_UserContext);
		
		ArrayList<CtxAttributeIdentifier> ctxAttributeIdentifierList = new ArrayList<CtxAttributeIdentifier>();
		
		try {
			boolean firstData = true;
			CtxAttributeIdentifier primaryAttribute_Action_Id = getContextAttr(
					ctxEntityIdentifier_UserContext, "Action");
			
			LOG.debug("primaryAttribute_Action_Id: " + primaryAttribute_Action_Id);

			ctxAttributeIdentifierList.add(primaryAttribute_Action_Id);
			//here is very suspicious
			updateContextValue(primaryAttribute_Action_Id, actionValue);
			
			
			
			CtxAttributeIdentifier escortingAttribute_Situation_Id = getContextAttr(
					ctxEntityIdentifier_UserContext, "Situation");
			
			LOG.debug("escortingAttribute_Situation_Id: " + escortingAttribute_Situation_Id);
			
			List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
			listOfEscortingAttributeIds.add(escortingAttribute_Situation_Id);
			
			LOG.debug("getHistoryTuples: " + internalCtxBroker.getHistoryTuples(primaryAttribute_Action_Id, null).get() //the second para is not used
					+", size: "+internalCtxBroker.getHistoryTuples(primaryAttribute_Action_Id, null).get().size());

	
			//if (firstData)
			{
				LOG.debug("add escortingAttribute: " + escortingAttribute_Situation_Id);
				internalCtxBroker.setHistoryTuples(primaryAttribute_Action_Id,
						listOfEscortingAttributeIds).get(); // updateHistoryTuples does not work. what if already has this attr? ConcurrentModificationException
			
			}
			
			//here is very suspicious
			updateContextValue(escortingAttribute_Situation_Id, situationValue);
			
			for (int i = 0; i < context.size(); i++)
			{

				CtxAttributeIdentifier escortingAttribute_Id = getContextAttr(
						ctxEntityIdentifier_UserContext, registeredContext.get(i));
				//if (firstData)
				{
					LOG.debug("add escortingAttribute: " + escortingAttribute_Id);
					listOfEscortingAttributeIds.add(escortingAttribute_Id);
					
					//here is very suspicious
					updateContextValue(escortingAttribute_Id, context.get(i));
					
					
					internalCtxBroker.updateHistoryTuples(primaryAttribute_Action_Id,
							listOfEscortingAttributeIds).get();
				}
				
				//internalCtxBroker.update(primaryAttribute_Action);// needed? ConcurrentModificationException
				
			}
			ctxAttributeIdentifierList.addAll(listOfEscortingAttributeIds);

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return ctxAttributeIdentifierList;
	}
*/
		
	
	public void storeActionHistory(CRISTHistoryData historyData)
	{
		ArrayList<String> context = historyData.getContext(); // context = ["100","30","22","N/A"]
		IAction action = historyData.getAction();
		String situationValue = historyData.getSituationValue();
		
		//set context data
		setContext("SITUATION", situationValue);
		setContext("LIGHT", context.get(0));
		setContext("SOUND", context.get(1));
		setContext(CtxAttributeTypes.TEMPERATURE, context.get(2));
		setContext("GPS", context.get(3));

		LOG.info("uam: " + uam);
		uam.monitor(getIdentity(), action);
		
	}
	
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveActionHistory()
	{
		List<CtxAttributeIdentifier> listOfEscortingAttributeIds = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = new HashMap<CtxHistoryAttribute, List<CtxHistoryAttribute>>();
		try {
			tupleResults  = internalCtxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, listOfEscortingAttributeIds, null, null).get();
			return tupleResults;
		
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		}
		return null;
		
	}
	
	/*
	@Deprecated
	public Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> retrieveTuplesCtxHistory(ArrayList<CtxAttributeIdentifier> ctxAttributeIdentifierList)
	{
		if (ctxAttributeIdentifierList == null || ctxAttributeIdentifierList.size() == 0)
			return null;
		
		CtxAttributeIdentifier primaryAttribute_Action_Id = ctxAttributeIdentifierList.get(0);
		
		LOG.debug("Retrieve Para: primaryAttribute_Action_Id = " + primaryAttribute_Action_Id);

		
		ArrayList<CtxAttributeIdentifier> listOfEscortingAttributeIds = ctxAttributeIdentifierList;//ref or value?
		listOfEscortingAttributeIds.remove(0);
		
		LOG.debug("Retrieve Para: listOfEscortingAttributeIds = " + listOfEscortingAttributeIds);

		
		try {
			
			LOG.debug("Futrue is: " + internalCtxBroker.retrieveHistoryTuples(primaryAttribute_Action_Id,
					listOfEscortingAttributeIds, null, null).get());
			
			Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = internalCtxBroker
					.retrieveHistoryTuples(primaryAttribute_Action_Id,
							listOfEscortingAttributeIds, null, null).get(); //[]
			return tupleResults;
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return null;
	}
	*/
	
	public CtxAttribute setContext(String type, Serializable value){

		CtxAttribute attr = null; 
		try {
			Set<CtxAttribute> ctxAttrSet = operator.getAttributes(type);
			if(ctxAttrSet.size()>0 ){
				ArrayList<CtxAttribute> ctxAttrList = new ArrayList<CtxAttribute>(ctxAttrSet);	
				attr = ctxAttrList.get(0);
				attr = internalCtxBroker.updateAttribute(attr.getId(), value).get();
			} else {
				attr = internalCtxBroker.createAttribute(operator.getId(), type).get();
				attr = internalCtxBroker.updateAttribute(attr.getId(),value).get();
			}

		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
		return attr;
	}
	
	
	

	private class MyCtxChangeEventListener implements CtxChangeEventListener {

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onCreation
		 * (org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onCreation(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** CREATED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onModification
		 * (org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onModification(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** MODIFIED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onRemoval(
		 * org.societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onRemoval(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** REMOVED event ***");
		}

		/*
		 * (non-Javadoc)
		 * 
		 * @see
		 * org.societies.api.context.event.CtxChangeEventListener#onUpdate(org
		 * .societies.api.context.event.CtxChangeEvent)
		 */
		@Override
		public void onUpdate(CtxChangeEvent event) {

			LOG.info(event.getId() + ": *** UPDATED event ***");
		}
	}

	public ICtxBroker getCtxBroker() {
		return internalCtxBroker;
	}

	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.internalCtxBroker = ctxBroker;
	}
	
	public IUserActionMonitor getUam(){
		return this.uam;
	}
	
	public void setUam(IUserActionMonitor uam){
		this.uam = uam;
	}


	// internalCtxBroker.registerForChanges
	void registerForContextChangeEvent() {

		CtxAttributeIdentifier uiModelAttributeId = null;
		try {

			List<CtxIdentifier> ls = this.internalCtxBroker.lookup(
					CtxModelType.ATTRIBUTE, CtxAttributeTypes.CRIST_MODEL)
					.get();
			if (ls.size() > 0) {
				uiModelAttributeId = (CtxAttributeIdentifier) ls.get(0);
			} else {
				CtxAttribute attr = this.internalCtxBroker.createAttribute(
						operator.getId(), CtxAttributeTypes.CRIST_MODEL).get();
				uiModelAttributeId = attr.getId();
			}
			if (uiModelAttributeId != null) {
				this.internalCtxBroker.registerForChanges(
						new MyCtxChangeEventListener(), uiModelAttributeId);
			}

			LOG.info("registration for context attribute updates of type "
					+ uiModelAttributeId);
		} catch (InterruptedException e) {
			e.printStackTrace();
		} catch (ExecutionException e) {
			e.printStackTrace();
		} catch (CtxException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void initializeHistory(ArrayList<String> registeredContext, CRISTCtxBrokerContact ctxBrokerContact)
	{
		
		ArrayList<IAction> historyAction = new ArrayList<IAction>();
		ArrayList<String> historySituation = new ArrayList<String>();
		ArrayList<ArrayList<String>> historyContext = new ArrayList<ArrayList<String>>();
		// Assuming that the following sensors are available: light, sound,
		// temperature, gps


		ArrayList<String> historyContextClique = new ArrayList<String>();
		historyAction.add(new Action(CRISTHistoryData.getServiceId_music(), "musicService", "switch", "on"));
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		//LOG.info("historyContext: "+historyContext.toString());
		//LOG.info("clique 0: "+historyContextClique.toString());
		
		historyContextClique = new ArrayList<String>();
		historyAction.add(new Action(CRISTHistoryData.getServiceId_music(), "musicService", "volume", "low"));
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		//LOG.info("historyContext: "+historyContext.toString());
		//LOG.info("clique 1: "+historyContextClique.toString());
		
		historyContextClique = new ArrayList<String>();
		historyAction.add(new Action(CRISTHistoryData.getServiceId_music(), "musicService", "switch", "next"));
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		//LOG.info("historyContext: "+historyContext.toString());
		//LOG.info("clique 2: "+historyContextClique.toString());
		
		historyContextClique = new ArrayList<String>();
		historyAction.add(new Action(CRISTHistoryData.getServiceId_music(), "musicService", "switch", "off"));
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add(new Action(CRISTHistoryData.getServiceId_checkin(), "checkinService", "GPS", "on"));
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		/*
		historyContextClique = new ArrayList<String>();
		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn up volume");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Switch songs");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn off MP3 Player");
		historySituation.add("Office");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("26");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Start the GPS navigator");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Input a location name");
		historySituation.add("Outdoor");
		historyContextClique.add("120");
		historyContextClique.add("60");
		historyContextClique.add("15");
		historyContextClique.add("55,1.33");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Close the GPS navigator");
		historySituation.add("Shopping Mall");
		historyContextClique.add("100");
		historyContextClique.add("80");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Switch songs");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn off MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn on MP3 Player");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		
		historyContextClique = new ArrayList<String>();
		historyAction.add("Turn down volume");
		historySituation.add("Study Hall");
		historyContextClique.add("100");
		historyContextClique.add("30");
		historyContextClique.add("22");
		historyContextClique.add("N/A");
		historyContext.add(historyContextClique);
		*/
		
		for (int i = 0; i < historyAction.size(); i++) {
			CRISTHistoryData currentHisData = new CRISTHistoryData(
					historyAction.get(i), historySituation.get(i),
					historyContext.get(i));
			LOG.info("i = " + i);

			ctxBrokerContact.storeActionHistory(currentHisData);
			
			/*
			for (CtxAttributeIdentifier ctxAttributeIdentifier : ctxAttributeIdentifierList)
			{
				LOG.info(" --i=-- " + i + ctxAttributeIdentifier + " --Type is-- " + ctxAttributeIdentifier.getType());
			}
			*/
		}
		
	}
	
	public static ArrayList<CRISTHistoryData> retrieveHistoryData(CRISTCtxBrokerContact ctxBrokerContact)
	{
		
		ArrayList<CRISTHistoryData> historyList = new ArrayList<CRISTHistoryData>();
		
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults = ctxBrokerContact.retrieveActionHistory();

		LOG.info("tupleResults.size() = " + tupleResults.size());
		
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			IAction action = null;
			String situationName = null;
			ArrayList<String> contexts = new ArrayList<String>();
			
			try {
				action = (IAction)SerialisationHelper.deserialise(primary.getBinaryValue(),CRISTHistoryData.class.getClassLoader());
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}

			List<CtxHistoryAttribute> escortingAttrList = tupleResults.get(primary);
			situationName = escortingAttrList.get(0).getStringValue();

			for(int j = 1; j < escortingAttrList.size(); j++){
				contexts.add(escortingAttrList.get(j).getStringValue());
			}
			CRISTHistoryData currentHisData = new CRISTHistoryData(
					action, situationName, contexts);
			historyList.add(currentHisData);
		}
		
		return historyList;
	}
	


}
