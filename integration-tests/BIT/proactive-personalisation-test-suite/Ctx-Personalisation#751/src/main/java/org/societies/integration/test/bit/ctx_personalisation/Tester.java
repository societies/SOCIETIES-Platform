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

package org.societies.integration.test.bit.ctx_personalisation;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;

import org.junit.Before;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.context.broker.ICtxBroker;
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
	//private CtxAttribute statusAttribute;
	private CtxAttribute hodAttribute;
	private CtxAttribute dowAttribute;
	
	private Logger logging = LoggerFactory.getLogger(this.getClass());

	public Tester(){

	}
	
	@Before
	public void setUp(){
		try{
		this.uam = Test751.getUam();
		this.idm = Test751.getCommsMgr().getIdManager();
		this.helloWorldService = Test751.getHelloWorld();
		this.ctxBroker = Test751.getCtxBroker();
		//userId = idm.getThisNetworkNode();
		setupContext();
		
		logging.debug("751SETUPCOMPLETE");
		}catch(Exception e){
			e.printStackTrace();
		}
			
	}
	
	
	@org.junit.Test
	public void Test(){
	try{
		//changeContext("home", "free");
		
		for (int i=0; i<4; i++){
			log("Step: "+i);
			
			setContext("home", "Monday",1);
			Thread.sleep(1000);
			this.helloWorldService.setBackgroundColour(userId, "red");
			Thread.sleep(5000);
			
			setContext("home", "Tuesday",2);
			Thread.sleep(1000);
			this.helloWorldService.setVolume(userId, "10");
			
			Thread.sleep(5000);
			setContext("work", "Wednesday",3);
			Thread.sleep(1000);
			this.helloWorldService.setBackgroundColour(userId, "black");
			Thread.sleep(5000);
			setContext("work", "Thursday",4);
			Thread.sleep(1000);
			this.helloWorldService.setVolume(userId, "50");
			Thread.sleep(5000);
			
			
		}
		
		Thread.sleep(20000);
		this.helloWorldService.setReceivedAction(false);
		//System.out.println("1Tester: set context home free");
		setContext("home", "Monday",1);
		logging.info("context set to 'loc=home, dow=Monday,hod=1' ");
		
		logging.info("1Tester: get background colour (red)");
		
		while (!this.helloWorldService.isReceivedAction()){
			Thread.sleep(5000);
		}
		Assert.assertEquals("red", this.helloWorldService.getBackgroundColour(userId));
		
		
		Thread.sleep(20000);
		this.helloWorldService.setReceivedAction(false);
		//System.out.println("2Tester: set context work busy");
		setContext("work", "Wednesday",3);
		logging.info("context set to 'loc=work, dow=Wednesday,hod=3' ");
		while (!this.helloWorldService.isReceivedAction()){
			Thread.sleep(5000);
		}
		logging.info("2Tester: get background colour (black)");
		Assert.assertEquals("black", this.helloWorldService.getBackgroundColour(userId));
	
	
		// setting colour to red, predicted action should set volume to 10
		Thread.sleep(20000);
		this.helloWorldService.setReceivedAction(false);
		this.helloWorldService.setBackgroundColour(userId, "red");
		logging.info("3Tester: set background colour (red), wait for a prediction based on performed action:setBackgroundColour(userId, 'red') ");
		
		while (!this.helloWorldService.isReceivedAction()){
			Thread.sleep(5000);
		}
		logging.info("3Tester: get Volume ...should be 10 and it is: " +this.helloWorldService.getVolume(userId));
		Assert.assertEquals("10", this.helloWorldService.getVolume(userId));
	
	}
	catch (InterruptedException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}
	
	}

	
	
	private void setContext(String symLocValue, String dowValue, Integer hodValue){
		try {
			CtxAttributeIdentifier locAttrId = symLocAttribute.getId();
			this.symLocAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(locAttrId, symLocValue).get();
			
			Thread.sleep(5000);
			//the code below was replaced by the call to updateAttribute with the id and value above. 
			//the code below doesn't work unless a fresh copy of the attribute is retrieved 
			//this.symLocAttribute.setStringValue(symLocValue);
			//this.symLocAttribute = (CtxAttribute) this.ctxBroker.update(symLocAttribute).get();
			
			CtxAttributeIdentifier dowAttrId = dowAttribute.getId();
			this.dowAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(dowAttrId, dowValue).get();
//			this.statusAttribute.setStringValue(statusValue);
//			this.statusAttribute = (CtxAttribute) this.ctxBroker.update(statusAttribute).get();
			//logging.debug("changeContext("+symLocValue+", "+dowValue+");");
			Thread.sleep(5000);
			CtxAttributeIdentifier hodAttrId = hodAttribute.getId();
			this.hodAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(hodAttrId, hodValue).get();
			logging.debug("changeContext("+symLocValue+", "+dowValue+", "+ hodValue+");");
			Thread.sleep(5000);
			
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

	
	
	
	/*
	private void setContext(String symLocValue, String statusValue){
		try {
			CtxAttributeIdentifier locAttrId = symLocAttribute.getId();
			this.symLocAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(locAttrId, symLocValue).get();
			
			
			//the code below was replaced by the call to updateAttribute with the id and value above. 
			//the code below doesn't work unless a fresh copy of the attribute is retrieved 
			//this.symLocAttribute.setStringValue(symLocValue);
			//this.symLocAttribute = (CtxAttribute) this.ctxBroker.update(symLocAttribute).get();
			
			CtxAttributeIdentifier statusAttrId = statusAttribute.getId();
			this.statusAttribute = (CtxAttribute) this.ctxBroker.updateAttribute(statusAttrId, statusValue).get();
//			this.statusAttribute.setStringValue(statusValue);
//			this.statusAttribute = (CtxAttribute) this.ctxBroker.update(statusAttribute).get();
			logging.debug("changeContext("+symLocValue+", "+statusValue+");");

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
	*/
	/**
	 * PreTest setup:
	 */
	
	
	
	
	private void setupContext() {
		this.getPersonEntity();
		this.getSymLocAttribute();
		this.getDowAttribute();
		this.getHodAttribute();
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
			
			this.userId = idm.getThisNetworkNode();
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(this.userId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC);
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
	
	
	private void getHodAttribute(){
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(CtxModelType.ATTRIBUTE, CtxAttributeTypes.HOUR_OF_DAY);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				hodAttribute = this.ctxBroker.createAttribute(person.getId(), CtxAttributeTypes.HOUR_OF_DAY).get();
			}else{
				hodAttribute = (CtxAttribute) this.ctxBroker.retrieve(attrs.get(0)).get();
			}
			
			if (hodAttribute==null){
				log(CtxAttributeTypes.HOUR_OF_DAY+" CtxAttribute is null");
			}else{
				log(CtxAttributeTypes.HOUR_OF_DAY+" CtxAttribute - NOT NULL");
				hodAttribute.setHistoryRecorded(true);
				hodAttribute = (CtxAttribute) ctxBroker.update(hodAttribute).get();
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
	
	
	
	
	private void getDowAttribute(){
		try {
			Future<List<CtxIdentifier>> futureAttrs = this.ctxBroker.lookup(person.getId(),CtxModelType.ATTRIBUTE, CtxAttributeTypes.DAY_OF_WEEK);
			List<CtxIdentifier> attrs = futureAttrs.get();
			if (attrs.size() == 0){
				dowAttribute = this.ctxBroker.createAttribute(person.getId(), CtxAttributeTypes.DAY_OF_WEEK).get();
			}else{
				dowAttribute = (CtxAttribute) this.ctxBroker.retrieve(attrs.get(0)).get();
			}
			
			if (dowAttribute==null){
				log(CtxAttributeTypes.DAY_OF_WEEK+" CtxAttribute is null");
			}else{
				log(CtxAttributeTypes.DAY_OF_WEEK+" CtxAttribute - NOT NULL");
				dowAttribute.setHistoryRecorded(true);
				dowAttribute = (CtxAttribute) ctxBroker.update(dowAttribute).get();
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
	
	
	/*
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
	*/
	
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
	
}
