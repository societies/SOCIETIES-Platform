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

package org.societies.integration.test.bit.ctxSNDataMgmt;



import java.io.IOException;
import java.io.Serializable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import junit.framework.Assert;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.context.broker.ICtxBroker;
import org.springframework.scheduling.annotation.AsyncResult;


/**
 * 
 *
 * @author nikosk
 *
 */
public class Tester {

	private ICtxBroker externalCtxBroker;
	private ICommManager commMgr;

	private static Logger LOG = LoggerFactory.getLogger(Tester.class);

	private INetworkNode cssNodeId;
	private IIdentity cssOwnerId;

	private RequestorService requestorService = null;
	private IIdentity userIdentity = null;
	private IIdentity serviceIdentity = null;
	private ServiceResourceIdentifier myServiceID;


	CtxEntityIdentifier cssOwnerEntityId ;

	public Tester(){

	}

	@Before
	public void setUp(){

	}


	@Test
	public void Test(){

		this.externalCtxBroker = CtxSNDataMgmt.getCtxBroker();
		this.commMgr = CtxSNDataMgmt.getCommManager();

		LOG.info("*** " + this.getClass() + " instantiated");

		try {
			this.cssNodeId = commMgr.getIdManager().getThisNetworkNode();
			//LOG.info("*** cssNodeId = " + this.cssNodeId);

			final String cssOwnerStr = this.cssNodeId.getBareJid();
			this.cssOwnerId = commMgr.getIdManager().fromJid(cssOwnerStr);
			LOG.info("*** cssOwnerId = " + this.cssOwnerId);

			this.serviceIdentity = commMgr.getIdManager().fromJid("nikosk@societies.org");
			myServiceID = new ServiceResourceIdentifier();
			myServiceID.setServiceInstanceIdentifier("css://nikosk@societies.org/HelloEarth");
			myServiceID.setIdentifier(new URI("css://nikosk@societies.org/HelloEarth"));

		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.requestorService = new RequestorService(serviceIdentity, myServiceID);

		LOG.info("*** requestor service = " + this.requestorService);

		LOG.info("*** Starting examples...");

		
		// TODO createRemoteEntity();
		this.createFakeSNData();
	}


	/*
	 * this will be substituted by real fb data
	 */
	private void createFakeSNData(){
	
		
		CtxEntityIdentifier cssOwnerEntityId;
		try {
			cssOwnerEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);
			CtxEntity individualEntity = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, cssOwnerEntityId).get();
			CtxAssociation assoc = null;
			
		
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
		
	}
	
	
	
	

	private CtxEntity getSnCtxEntity() {

		IndividualCtxEntity individualEntity;
		CtxEntityIdentifier individualEntityId;
		
		CtxAssociation snsAssoc = null;
		CtxEntity socialNetwork = null;
		
		//fake sn name
		String snName = "FACEBOOK";
		//this.snName = connector.getConnectorName();
		
		try {
			individualEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();

			individualEntity = (IndividualCtxEntity) this.externalCtxBroker.retrieve(this.requestorService, individualEntityId).get();
			
			Set<CtxAssociationIdentifier> snsAssocSet = individualEntity.getAssociations(CtxAssociationTypes.IS_CONNECTED_TO_SNS);
			LOG.debug("There are "+ snsAssocSet.size() + " associations with SocialNetworks");
			
			if (snsAssocSet.size() > 0) {
				
				List<CtxAssociationIdentifier> snsAssocList = new ArrayList<CtxAssociationIdentifier>(snsAssocSet);
				for(CtxAssociationIdentifier assocID : snsAssocList ){
					snsAssoc = (CtxAssociation) this.externalCtxBroker.retrieve(this.requestorService, assocID).get();
					Set<CtxEntityIdentifier>  snsEntitiesSet 	= snsAssoc.getChildEntities(CtxEntityTypes.SOCIAL_NETWORK);
					List<CtxEntityIdentifier> snsEntitiesList 	= new ArrayList<CtxEntityIdentifier>(snsEntitiesSet);
				
					LOG.debug("lookup SN association" + snName);
					List<CtxEntityIdentifier> snEntList 		= this.lookupEntities(snsEntitiesList, CtxAttributeTypes.NAME, snName).get();
				
					if (snEntList.size() > 0) {
						socialNetwork = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, snEntList.get(0)).get();
						return socialNetwork;
					}
				}
			
			}
			//if (snsAssocSet.size() == 0) {

				snsAssoc = this.externalCtxBroker.createAssociation(this.requestorService,this.cssOwnerId , CtxAssociationTypes.IS_CONNECTED_TO_SNS).get();
				LOG.info("snsAssoc created ");
				
				List<CtxEntityIdentifier> snEntitiesList = this.externalCtxBroker.lookupEntities(this.requestorService,this.cssOwnerId ,CtxEntityTypes.SOCIAL_NETWORK,CtxAttributeTypes.NAME, snName, snName).get();

				if (snEntitiesList.size() == 0) {
					socialNetwork = this.externalCtxBroker.createEntity(this.requestorService,this.cssOwnerId, CtxEntityTypes.SOCIAL_NETWORK).get();
					LOG.info("SOCIAL_NETWORK entity created "+socialNetwork.getId());
					CtxAttribute snsNameAttr = this.externalCtxBroker.createAttribute(this.requestorService, socialNetwork.getId(), CtxAttributeTypes.NAME).get();
					snsNameAttr.setStringValue(snName);
					this.externalCtxBroker.update(this.requestorService, snsNameAttr);

				}
				
				else
					socialNetwork = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, snEntitiesList.get(0)).get();

					snsAssoc.addChildEntity(socialNetwork.getId());
					snsAssoc.addChildEntity(individualEntity.getId());
					snsAssoc.setParentEntity(individualEntity.getId());
					snsAssoc = (CtxAssociation) this.externalCtxBroker.update(this.requestorService,snsAssoc).get();
					this.externalCtxBroker.update(this.requestorService, individualEntity);
			//}

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

		return socialNetwork;
	}

	
	
	
	/*
	
	public void updateCtxProfile() {


		//LOG.debug(" Updating profile data for " + connector.getConnectorName());

		CtxEntityIdentifier cssOwnerEntityId;
		cssOwnerEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
		
		//this.snName = connector.getConnectorName();
		CtxEntity socialNetworkEntity = this.getSnCtxEntity();
		
		
		// Update Simple Profile
	//	PersonConverter parser   = PersonConverterFactory.getPersonConverter(connector);
//		Person profile			 = parser.load(connector.getUserProfile());
		
//		updateStringFieldIfExists(snName, CtxAttributeTypes.TYPE);
		updateStringFieldIfExist(profile.getAboutMe(), CtxAttributeTypes.ABOUT);		
		updateStringFieldIfExist(profile.getProfileUrl(), CtxAttributeTypes.PROFILE_IMAGE_URL);		
		updateStringFieldIfExist(profile.getDisplayName(), CtxAttributeTypes.NAME);
		
		if (profile.getName()!=null){
			updateStringFieldIfExist(profile.getName().getGivenName(),  CtxAttributeTypes.NAME_FIRST);
			updateStringFieldIfExist(profile.getName().getFamilyName(), CtxAttributeTypes.NAME_LAST);
		}
		//		updateStringFieldIfExist(profile.getPhoneNumbers(), CtxAttributeTypes.PHONES);
		updateStringFieldIfExist(profile.getPoliticalViews(), CtxAttributeTypes.POLITICAL_VIEWS);
		updateStringFieldIfExist(profile.getPreferredUsername(), CtxAttributeTypes.USERNAME);	
		updateStringFieldIfExist(profile.getThumbnailUrl(), CtxAttributeTypes.PROFILE_IMAGE_URL);
		updateStringFieldIfExist(profile.getRelationshipStatus(), CtxAttributeTypes.STATUS);
		updateStringFieldIfExist(profile.getReligion(), CtxAttributeTypes.RELIGIOUS_VIEWS);
		
		if (profile.getGender()!=null)
			updateStringFieldIfExist(profile.getGender().name(), CtxAttributeTypes.SEX);
		if (profile.getBirthday()!=null)
			updateStringFieldIfExist(profile.getBirthday().toGMTString(), CtxAttributeTypes.BIRTHDAY);
		if (profile.getCurrentLocation()!=null)
			updateStringFieldIfExist(profile.getCurrentLocation().getFormatted(), CtxAttributeTypes.LOCATION_SYMBOLIC);
		

		updateStringFieldIfExist(profile.getBooks(), 			 CtxAttributeTypes.BOOKS);
		updateStringFieldIfExist(profile.getMusic(),  			 CtxAttributeTypes.MUSIC);
		updateStringFieldIfExist(profile.getInterests(),  		 CtxAttributeTypes.INTERESTS);
		updateStringFieldIfExist(profile.getJobInterests(),  	 CtxAttributeTypes.JOBS_INTERESTS);
		updateStringFieldIfExist(profile.getLanguagesSpoken(),   CtxAttributeTypes.LANGUAGES);
		updateStringFieldIfExist(profile.getMovies(),  			 CtxAttributeTypes.MOVIES);
		updateStringFieldIfExist(profile.getTurnOns(),  		 CtxAttributeTypes.TURNSON);
		updateStringFieldIfExist(profile.getActivities(), 		 CtxAttributeTypes.ACTIVITIES);		
//		updateStringFieldIfExist(profile.getEmails(), 		 	 CtxAttributeTypes.EMAIL);	
	
		
		LOG.debug(" Updating Friends List ...");
		// Add Friends List
		
		FriendsConverter friendsParser = FriendsConveterFactory.getPersonConverter(connector);
		List<Person> friends= friendsParser.load(connector.getUserFriends());
	    storeFriendsIntoContextBroker(friends);
	    
	    
	    LOG.debug(" Updating Groups list ...");
	    // Add Group List
	    GroupConverter groupConverter = GroupConveterFactory.getPersonConverter(connector);
	    List<Group> groups = groupConverter.load(connector.getUserGroups());
	    storeGroupsIntoContextBroker(groups);
	}
	*/
	
	
	private void updateStringFieldIfExist(String value, String type) {
		try {
			if (value != null) {
				LOG.info("update " + type + " data" + value);
				//storeSocialDataIntoContextBroker(type, value);
			//	LOG.info(snName + " entity updated with " + type + " data "+ socialNetworkEntity.getId());
			}
			else LOG.debug(type + " value is NULL");
		} 
		catch (Exception ex) {
			LOG.error("Unable to store :" + type + " -> " + value + " because "+ ex, ex);
		}

	}
	
	private void updateStringFieldIfExist(List<String> listOfvalues, String type){
		 
		 if (listOfvalues != null) {
		//	String value = updateListData(listOfvalues);
		//	updateStringFieldIfExist(value, type);
		}
		else LOG.debug(type + " value is NULL");
	}
	

	

	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	private void updateOperatorAttributeLocation(){
		LOG.info("*** updateOperatorAttributes : updates an existing  Location attribute");

		CtxEntityIdentifier cssOwnerEntityId;
		try {
			cssOwnerEntityId = this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();

			List<CtxIdentifier> listAttrIds =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 

			if(listAttrIds.size() == 0){
				//create location attribute
				LOG.info("location attribute doesn't exist ... creating");
				this.externalCtxBroker.createAttribute(this.requestorService, cssOwnerEntityId, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			}

			List<CtxIdentifier> listAttrIds2 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			LOG.info("location attribute identifiers "+listAttrIds2);

			CtxAttributeIdentifier locationAttributeId = null;

			//the test will run correct for only one location attribute
			if(listAttrIds2.size() == 1){
				locationAttributeId = (CtxAttributeIdentifier) listAttrIds2.get(0);  
				CtxAttribute locationAttributeRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved.getId());
				String locationSymbolicValue  = locationAttributeRetrieved.getStringValue();
				LOG.info("locationAttributeRetrieved value (should be null) :" + locationSymbolicValue);

				locationAttributeRetrieved.setStringValue("ATHENS");
				LOG.info("value set...."+locationAttributeRetrieved.getStringValue()+" trying to update location attribute :" +locationAttributeRetrieved.getId());
				this.externalCtxBroker.update(this.requestorService, locationAttributeRetrieved );				
				LOG.info("update successfull");
			}	

			LOG.info("retrieve location attribute based on existing identifier:"+ locationAttributeId);
			CtxAttribute locationAttributeWithValue = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
			LOG.info("locationAttributeWithValue value :" + locationAttributeWithValue.getStringValue());

			LOG.info("retrieve location attribute based on lookup ids ************* ");
			List<CtxIdentifier> listAttrIds3 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get(); 
			LOG.info("retrieve location attribute based on lookup ids"+listAttrIds3);
			if(listAttrIds3.size() == 1){
				CtxAttributeIdentifier locationAttributeId3 = (CtxAttributeIdentifier) listAttrIds3.get(0); 
				CtxAttribute locationAttributeRetrieved3 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId3).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved3.getId());
				String locationSymbolicValue  = locationAttributeRetrieved3.getStringValue();
				LOG.info("locationAttributeRetrieved value :" + locationSymbolicValue);
			}

		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (CtxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void createOperatorAttributeBirthday(){

		LOG.info("createOperatorAttributeBirthday");
		try {
			CtxEntityIdentifier cssOwnerEntityId = 
					this.externalCtxBroker.retrieveIndividualEntityId(this.requestorService, this.cssOwnerId).get();
			LOG.info("*** Retrieved CSS owner context entity id " + cssOwnerEntityId);

			LOG.info("*** lookup for birthday attribute ");
			List<CtxIdentifier> listAttrBDays =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BIRTHDAY).get(); 
			LOG.info("this list should be zero :" + listAttrBDays); 

			//create attribute birthday
			LOG.info("*** create attribute birthday ");
			CtxAttribute ctxAttrBirthday = this.externalCtxBroker.createAttribute(this.requestorService, cssOwnerEntityId, CtxAttributeTypes.BIRTHDAY).get(); 

			CtxAttributeIdentifier bDayId = ctxAttrBirthday.getId();


			LOG.info("*** set value of attribute birthday and update");
			ctxAttrBirthday.setStringValue("today"); 
			ctxAttrBirthday.setValueType(CtxAttributeValueType.STRING);

			this.externalCtxBroker.update(this.requestorService, ctxAttrBirthday).get();

			LOG.info("*** lookup for birthday attribute 2 (after creation-update)");
			List<CtxIdentifier> listAttrBDays2 =  this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.BIRTHDAY).get(); 
			LOG.info("should not be zero listAttrBDays :" + listAttrBDays2); 


			CtxAttribute ctxAttrBirthdayRetrieved1 = null;
			CtxAttribute ctxAttrBirthdayRetrieved2 = null;
			LOG.info("*** retrieve birthday attribute from db ");

			//this one works
			ctxAttrBirthdayRetrieved1 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, bDayId).get();
			LOG.info("withoutLookup ctxAttrBirthdayRetrieved:" + ctxAttrBirthdayRetrieved1.getId());
			String ctxAttrBirthdayRetrievedValue  = ctxAttrBirthdayRetrieved1.getStringValue();
			LOG.info("withoutLookup ctxAttrBirthdayRetrieved value :" + ctxAttrBirthdayRetrievedValue);
			assertEquals("today",ctxAttrBirthdayRetrievedValue);
			//try this one
			if(listAttrBDays2.size()>0){
				CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) listAttrBDays2.get(0);
				ctxAttrBirthdayRetrieved2 = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService,attrId ).get();	
			}


			LOG.info("after lookup ctxAttrBirthdayRetrieved:" + ctxAttrBirthdayRetrieved2.getId());
			String ctxAttrBirthdayRetrievedValue2  = ctxAttrBirthdayRetrieved2.getStringValue();
			LOG.info("ctxAttrBirthdayRetrieved value :" + ctxAttrBirthdayRetrievedValue2);
			assertEquals("today",ctxAttrBirthdayRetrievedValue2);
		} catch (Exception e) {

			LOG.error("3P ContextBroker sucks: " + e.getLocalizedMessage(), e);
		}	
	}

			
	
	/*
	 * private void lookupRetrieveLocationAttributeValue(){
		List<CtxIdentifier> listAttrIds2;
		try {
			listAttrIds2 = this.externalCtxBroker.lookup(this.requestorService, this.cssOwnerId, CtxModelType.ATTRIBUTE, CtxAttributeTypes.LOCATION_SYMBOLIC).get();
			if(listAttrIds2.size() > 0){
				CtxAttributeIdentifier locationAttributeId = (CtxAttributeIdentifier) listAttrIds2.get(0);  
				CtxAttribute locationAttributeRetrieved = (CtxAttribute) this.externalCtxBroker.retrieve(this.requestorService, locationAttributeId).get();
				LOG.info("locationAttributeRetrieved  :" + locationAttributeRetrieved.getId());
				String locationSymbolicValue  = locationAttributeRetrieved.getStringValue();
				LOG.info("locationAttributeRetrieved value :" + locationSymbolicValue);
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
	}
	 */
	
	
	
	public Future<List<CtxEntityIdentifier>> lookupEntities(List<CtxEntityIdentifier> ctxEntityIDList, String ctxAttributeType, Serializable value){

		List<CtxEntityIdentifier> entityList = new ArrayList<CtxEntityIdentifier>(); 
		try {
			for(CtxEntityIdentifier entityId :ctxEntityIDList){
				CtxEntity entity = (CtxEntity) this.externalCtxBroker.retrieve(this.requestorService, entityId).get();

				Set<CtxAttribute> ctxAttrSet = entity.getAttributes(ctxAttributeType);
				for(CtxAttribute ctxAttr : ctxAttrSet){

					if(this.compareAttributeValues(ctxAttr,value)) {
						entityList.add(entityId);
					}
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
		}

		return new AsyncResult<List<CtxEntityIdentifier>>(entityList);
	}
	
	public static Boolean compareAttributeValues(CtxAttribute attribute, Serializable value){

		Boolean areEqual = false;
		if (value instanceof String ) {
			if (attribute.getStringValue()!=null) {
				String valueStr = attribute.getStringValue();
				if(valueStr.equalsIgnoreCase(value.toString())) return true;             			
			}
		} else if (value instanceof Integer) {
			if(attribute.getIntegerValue()!=null) {
				Integer valueInt = attribute.getIntegerValue();
				if(valueInt.equals((Integer)value)) return true;  
			}
		} else if (value instanceof Double) {
			if(attribute.getDoubleValue()!=null) {
				Double valueDouble = attribute.getDoubleValue();
				if(valueDouble.equals((Double) value)) return true;             			
			}
		} else {
			byte[] valueBytes;
			byte[] attributeValueBytes;
			try {
				valueBytes = attribute.getBinaryValue();
				attributeValueBytes = SerialisationHelper.serialise(value);
				if (Arrays.equals(valueBytes, attributeValueBytes)) {
					areEqual = true;
					return true;
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}                		
		}
		return areEqual;
	}
	
	
}