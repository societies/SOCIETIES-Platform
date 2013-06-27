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

package org.societies.integration.test.bit.nonctx_personalisation;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.personalisation.model.IAction;
import org.societies.api.useragent.monitoring.IUserActionMonitor;


/**
 * Utility class that creates mock actions
 *
 * @author Eliza
 *
 */
public class Tester {
	private IUserActionMonitor uam;
	private IIdentityManager idm;
	private IHelloWorld helloWorldService;
	private IIdentity userId;
	private ICtxBroker ctxBroker;
	private CtxEntity person;
	private CtxAttribute symLocAttribute;
	private CtxAttribute statusAttribute;
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public Tester(){

	}

	@Before
	public void setUp(){
		try{
			this.uam = Test2010.getUam();
			this.idm = Test2010.getCommsMgr().getIdManager();
			this.helloWorldService = Test2010.getHelloWorld();
			this.ctxBroker = Test2010.getCtxBroker();
			//userId = idm.getThisNetworkNode();
			setupContext();

			logging.debug("2010SETUPCOMPLETE");
		}catch(Exception e){
			e.printStackTrace();
		}

	}


	@org.junit.Test
	public void Test(){
		try{
			logging.info("start performing actions ");
			if( !retrieveUserIntentModel() ){
				int i=0;
				
				//1 
				i++;
				setContext("loc1", "");
				Thread.sleep(2500);
				logging.info("action:"+i+" setBackgroundColour : red");
				this.helloWorldService.setBackgroundColour(userId, "red");
				Thread.sleep(4500);
				
				//2
				i++;
				logging.info("action:"+i+" setVolume : 10");
				this.helloWorldService.setVolume(userId, "10");
				Thread.sleep(5000);
				
				//3 
				i++;
				setContext("", "status1");
				Thread.sleep(2500);
				logging.info("action:"+i+" setBackgroundColour : red");
				this.helloWorldService.setBackgroundColour(userId, "red");
				Thread.sleep(4500);

				//4
				i++;
				logging.info("action:"+i+" setVolume : 10");
				this.helloWorldService.setVolume(userId, "10");
				Thread.sleep(5000);

				//5
				i++;
				setContext("loc2", "status2");
				Thread.sleep(2500);
				logging.info("action:"+i+" setBackgroundColour : red");
				this.helloWorldService.setBackgroundColour(userId, "red");
				Thread.sleep(4500);

				//6
				i++;
				logging.info("action:"+i+" setVolume : 10");
				this.helloWorldService.setVolume(userId, "10");
				Thread.sleep(5000);
			
				/*
				//7
				i++;
				logging.info("action:"+i+" setBackgroundColour : red");
				this.helloWorldService.setBackgroundColour(userId, "red");
				Thread.sleep(4500);

				//8
				i++;
				logging.info("action:"+i+" setVolume : 10");
				this.helloWorldService.setVolume(userId, "10");
				Thread.sleep(5000);
*/
				////////////////
				// at this point a new model will be learned
			
			}
			historyDataRetrieval();

			logging.info("start predictions ");
			
			setContext("loc2", "status1");
			Thread.sleep(2500);
			logging.info("1  set background colour (red)");
			this.helloWorldService.setBackgroundColour(userId, "red");
			Thread.sleep(15000);

			logging.info("...... dynamic change of volume... ");
			logging.info("...... volume should be 10 and it is:"+ this.helloWorldService.getVolume(userId));
			Assert.assertEquals("10", this.helloWorldService.getVolume(userId));
			
			
			setContext("loc1", "status100");
			Thread.sleep(2500);
			logging.info("2 set background colour (black)");
			this.helloWorldService.setBackgroundColour(userId, "black");
			logging.info("no prediction should be available for background 'black', volume value should be the same as before '10'");
			Assert.assertEquals("10", this.helloWorldService.getVolume(userId));
			//logging.info("1 Tester: volume equals: "+ this.helloWorldService.getVolume(userId));
			
			
		}

		catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}



	public void historyDataRetrieval() {

		//LOG.info("TestHistoryDataRetrieval ");

		List<CtxAttributeIdentifier> ls = new ArrayList<CtxAttributeIdentifier>();
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults;
		try {
			tupleResults = this.ctxBroker.retrieveHistoryTuples(CtxAttributeTypes.LAST_ACTION, ls, null, null).get();
			boolean success = false;
			if(tupleResults.size() >=0 )success= true;
			Assert.assertTrue(success);

			printHocTuplesDB(tupleResults);
			logging.info("number of actions in history "+ tupleResults.size());

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}





	public Boolean retrieveUserIntentModel() {

		System.out.println("Retrieve Model");

		Boolean modelExist = false;

		try {

			INetworkNode cssNodeId = this.idm.getThisNetworkNode();
			final String cssOwnerStr = cssNodeId.getBareJid();
			IIdentity cssOwnerId = this.idm.fromJid(cssOwnerStr);

			// add code for cssNodeID
			IndividualCtxEntity operator =  this.ctxBroker.retrieveIndividualEntity(cssOwnerId).get();

			Set<CtxAttribute> setAttr = operator.getAttributes(CtxAttributeTypes.CAUI_MODEL);

			//	Assert.assertNotNull(setAttr);
			//	Assert.assertEquals(1, setAttr.size());

			if(setAttr != null ){

				logging.info("attributes size refering to caui model "+setAttr.size());
				logging.info("attributes refering to caui model "+setAttr);

				for(CtxAttribute attrRetr : setAttr){
					if (attrRetr.getBinaryValue()!=null)	modelExist = true;
				}
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
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return modelExist;
	}



	private void setContext(String symLocValue, String statusValue){


		try {

			if( symLocValue != ""){
				CtxAttributeIdentifier locAttrId = symLocAttribute.getId();
				this.symLocAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(locAttrId, symLocValue).get();
			}
			if(statusValue != "" ){
				CtxAttributeIdentifier statusAttrId = statusAttribute.getId();
				this.statusAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(statusAttrId, statusValue).get();

			}
			//the code below was replaced by the call to updateAttribute with the id and value above. 
			//the code below doesn't work unless a fresh copy of the attribute is retrieved 
			//this.symLocAttribute.setStringValue(symLocValue);
			//this.symLocAttribute = (CtxAttribute) this.ctxBroker.update(symLocAttribute).get();

			//			this.statusAttribute.setStringValue(statusValue);
			//			this.statusAttribute = (CtxAttribute) this.ctxBroker.update(statusAttribute).get();
			logging.info("changeContext("+symLocValue+", "+statusValue+");");

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * PreTest setup:
	 */




	private void setupContext() {
		this.getPersonEntity();
		this.getSymLocAttribute();
		this.getStatusAttribute();
	}

	private void getPersonEntity(){
		try {
			this.userId = idm.getThisNetworkNode();
			Future<IndividualCtxEntity> futurePerson = this.ctxBroker.retrieveIndividualEntity(userId);
			person = futurePerson.get();


			/*Future<List<CtxIdentifier>> futurePersons = this.ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PERSON);
			List<CtxIdentifier> persons = futurePersons.get();
			if (persons.size() == 0){
				person = this.ctxBroker.createEntity(CtxEntityTypes.PERSON).get();

			}else{
				person = (CtxEntity) this.ctxBroker.retrieve(persons.get(0)).get();
			}*/

			if (person==null){
				log("Person CtxEntity is null");
			}else{
				log("Got Person CtxEntity - NOT NULL");
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	private void getSymLocAttribute(){
		try {

			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				symLocAttribute = this.ctxBroker.createAttribute(person.getId(), CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			}else{
				symLocAttribute = (CtxAttribute) this.ctxBroker.retrieve(attrs.get(0)).get();
			}
			if (symLocAttribute==null){
				log(CtxAttributeTypes.LOCATION_SYMBOLIC+" CtxAttribute is null");
			}else{
				log(CtxAttributeTypes.LOCATION_SYMBOLIC+" CtxAttribute - NOT NULL");
			}

		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch(Exception e){
			e.printStackTrace();
			this.log("EXCEPTION!");
		}

	}


	private void getStatusAttribute(){
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.STATUS);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				statusAttribute = this.ctxBroker.createAttribute(person.getId(), CtxAttributeTypes.STATUS).get();
			}else{
				statusAttribute = (CtxAttribute) this.ctxBroker.retrieve(attrs.get(0)).get();
			}

			if (statusAttribute==null){
				log(CtxAttributeTypes.STATUS+" CtxAttribute is null");
			}else{
				log(CtxAttributeTypes.STATUS+" CtxAttribute - NOT NULL");
				statusAttribute.setHistoryRecorded(true);
				statusAttribute = (CtxAttribute) ctxBroker.update(statusAttribute).get();
			}
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}	


	private void log(String msg){
		logging.debug(this.getClass().getName()+": "+msg);
	}
	/*
	private List<IIdentity> identities = new ArrayList<IIdentity>();
	private List<IAction> actions = new ArrayList<IAction>();
	private List<ServiceResourceIdentifier> services = new ArrayList<ServiceResourceIdentifier>();
	private List<String> names;
	private List<String> values = new ArrayList<String>();
	private String parameterName = "volume";








	public ActionSender(IUserActionMonitor uam, IIdentityManager idm){
		this.uam = uam;
		this.idm = idm;
		this.createIIdentities();
		this.createServiceIDs();
		this.createValues();
		this.createActions();

	}

	private void createValues() {
		values.add("0");
		values.add("50");
		values.add("100");

	}

	public void createActions(){

		for (ServiceResourceIdentifier serviceID : services){
			for (String value : values){
				actions.add(new Action(serviceID, "aServiceType", parameterName, value));
			}
		}
	}

	public void createServiceIDs(){

		for (String name : names){
			ServiceResourceIdentifier serviceID = new ServiceResourceIdentifier();
			serviceID.setServiceInstanceIdentifier("css://"+name+"/service");
		}

	}

	public static void main(String[] args){
		try {
			URI uri = new URI("css://eliza@societies.org");
			logging.debug(uri.toString());
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	private void createIIdentities(){

		names = new ArrayList<String>();
		names.add("Eliza@societies.org");
		names.add("Sarah@societies.org");
		names.add("Nicolas@societies.org");

		for (String name : names){
			try {
				identities.add(this.idm.fromJid(name));
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	 */
	protected void printHocTuplesDB(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> tupleResults){

		logging.info("printing Tuples");
		int i = 0;
		for (CtxHistoryAttribute primary : tupleResults.keySet()){
			try {
				IAction action = (IAction)SerialisationHelper.deserialise(primary.getBinaryValue(),this.getClass().getClassLoader());
				logging.info(i+ " action name: "+action.getparameterName()+" action value: "+action.getvalue()+ " action service "+action.getServiceID().getIdentifier());
				for(CtxHistoryAttribute escortingAttr: tupleResults.get(primary)){
					String result = this.getValue(escortingAttr);
					logging.info("escording attribute type: "+escortingAttr.getType()+" value:"+result);
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
