package org.societies.platform.socialdata;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.sns.ISocialConnector;

import org.societies.platform.socialdata.converters.FriendsConverter;
import org.societies.platform.socialdata.converters.FriendsConveterFactory;
import org.societies.platform.socialdata.converters.GroupConverter;
import org.societies.platform.socialdata.converters.GroupConveterFactory;
import org.societies.platform.socialdata.converters.PersonConverter;
import org.societies.platform.socialdata.converters.PersonConverterFactory;

import org.apache.shindig.social.opensocial.model.Group;

public class ContextUpdater {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ContextUpdater.class);

	ICtxBroker internalCtxBroker = null;
	IIdentity cssId = null;
	String snName;
	
	CtxEntity socialNetworkEntity = null;
	

	public ContextUpdater(ICtxBroker internalCtxBroker, IIdentity cssId) {
		// LOG.info("updating user profile in context: broker service:"+internalCtxBroker);
		this.cssId = cssId;
		this.internalCtxBroker = internalCtxBroker;

	}

	

	public void updateCtxProfile(ISocialConnector connector) {

		this.snName = connector.getConnectorName();
		this.socialNetworkEntity = this.getSnCtxEntity();
		
		
		// Update Simple Profile
		PersonConverter parser   = PersonConverterFactory.getPersonConverter(connector);
		Person profile			 = parser.load(connector.getUserProfile());
		
//		updateStringFieldIfExists(snName, CtxAttributeTypes.TYPE);
		updateStringFieldIfExist(profile.getAboutMe(), CtxAttributeTypes.ABOUT);		
		updateStringFieldIfExist(profile.getProfileUrl(), CtxAttributeTypes.PROFILE_IMAGE_URL);		
		updateStringFieldIfExist(profile.getDisplayName(), CtxAttributeTypes.NAME);
		updateStringFieldIfExist(profile.getName().getGivenName(),  CtxAttributeTypes.NAME_FIRST);
		updateStringFieldIfExist(profile.getName().getFamilyName(), CtxAttributeTypes.NAME_LAST);
//		updateStringFieldIfExist(profile.getPhoneNumbers(), CtxAttributeTypes.PHONES);
		updateStringFieldIfExist(profile.getPoliticalViews(), CtxAttributeTypes.POLITICAL_VIEWS);
		updateStringFieldIfExist(profile.getPreferredUsername(), CtxAttributeTypes.USERNAME);	
		updateStringFieldIfExist(profile.getThumbnailUrl(), CtxAttributeTypes.PROFILE_IMAGE_URL);
		updateStringFieldIfExist(profile.getRelationshipStatus(), CtxAttributeTypes.STATUS);
		updateStringFieldIfExist(profile.getReligion(), CtxAttributeTypes.RELIGIOUS_VIEWS);
		if (profile.getGender()!=null)
		updateStringFieldIfExist(profile.getGender().name(), CtxAttributeTypes.SEX);
		updateStringFieldIfExist(profile.getBirthday().toGMTString(), CtxAttributeTypes.BIRTHDAY);
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
	
		// Add Friends List
		
		FriendsConverter friendsParser = FriendsConveterFactory.getPersonConverter(connector);
		List<Person> friends= friendsParser.load(connector.getUserProfile());
	    storeFriendsIntoContextBroker(friends);
	    
	    // Add Group List
	    GroupConverter groupConverter = GroupConveterFactory.getPersonConverter(connector);
	    List<Group> groups = groupConverter.load(connector.getUserGroups());
	    storeGroupsIntoContextBroker(groups);
	}
	
	
	
	private void updateStringFieldIfExist(String value, String type){
		try{
		if (value != null) {
			LOG.info("update "+ type + " data" + value);
			storeSocialDataIntoContextBroker(type, value);
			LOG.info(snName + " entity updated with "+ type + " data "+ socialNetworkEntity.getId());
		}
		}catch(Exception ex ){
			LOG.error("Unable to store :" + type + " -> "+value + " because "+ex, ex); 
		}
		
	}
	
	private void updateStringFieldIfExist(List<String> listOfvalues, String type){
		 
		 if (listOfvalues != null) {
			String value = updateListData(listOfvalues);
			updateStringFieldIfExist(value, type);
		}
	}
	
	
	
	
	

	private CtxEntity getSnCtxEntity() {

		IndividualCtxEntity individualEntity;
		CtxAssociation snsAssoc = null;
		CtxEntity socialNetwork = null;

		try {
			individualEntity = this.internalCtxBroker.retrieveIndividualEntity(this.cssId).get();
			Set<CtxAssociationIdentifier> snsAssocSet = individualEntity.getAssociations(CtxAssociationTypes.IS_CONNECTED_TO_SNS);
			LOG.debug("There are "+ snsAssocSet.size() + " associations with SocialNetworks");
			
			if (snsAssocSet.size() > 0) {
				
				List<CtxAssociationIdentifier> snsAssocList = new ArrayList<CtxAssociationIdentifier>(snsAssocSet);
				for(CtxAssociationIdentifier assocID : snsAssocList ){
					snsAssoc = (CtxAssociation) this.internalCtxBroker.retrieve(assocID).get();
					Set<CtxEntityIdentifier>  snsEntitiesSet 	= snsAssoc.getChildEntities(CtxEntityTypes.SOCIAL_NETWORK);
					List<CtxEntityIdentifier> snsEntitiesList 	= new ArrayList<CtxEntityIdentifier>(snsEntitiesSet);
				
					LOG.debug("lookup SN association" + snName);
					List<CtxEntityIdentifier> snEntList 		= this.internalCtxBroker.lookupEntities(snsEntitiesList, CtxAttributeTypes.NAME, snName).get();
				
					if (snEntList.size() > 0) {
						socialNetwork = (CtxEntity) this.internalCtxBroker.retrieve(snEntList.get(0)).get();
						return socialNetwork;
					}
				}
			
			}
			//if (snsAssocSet.size() == 0) {

				snsAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.IS_CONNECTED_TO_SNS).get();
				LOG.info("snsAssoc created ");
				
				List<CtxEntityIdentifier> snEntitiesList = this.internalCtxBroker.lookupEntities(CtxEntityTypes.SOCIAL_NETWORK,CtxAttributeTypes.NAME, snName, snName).get();

				if (snEntitiesList.size() == 0) {
					socialNetwork = this.internalCtxBroker.createEntity(CtxEntityTypes.SOCIAL_NETWORK).get();
					LOG.info("SOCIAL_NETWORK entity created "+socialNetwork.getId());
					CtxAttribute snsNameAttr = this.internalCtxBroker.createAttribute(socialNetwork.getId(), CtxAttributeTypes.NAME).get();
					snsNameAttr.setStringValue(snName);
					this.internalCtxBroker.update(snsNameAttr);

				}
				
				else
					socialNetwork = (CtxEntity) this.internalCtxBroker.retrieve(snEntitiesList.get(0)).get();

					snsAssoc.addChildEntity(socialNetwork.getId());
					snsAssoc.addChildEntity(individualEntity.getId());
					snsAssoc.setParentEntity(individualEntity.getId());
					snsAssoc = (CtxAssociation) this.internalCtxBroker.update(snsAssoc).get();
					this.internalCtxBroker.update(individualEntity);
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

	
	
	
	private CtxEntity storeSocialDataIntoContextBroker(String type,  Serializable value) {

		CtxAttribute attribute = null;
		
		try {
			

			if (socialNetworkEntity != null) {
				
				LOG.info(">>>>> Updating user profile in context " + type + " values" + value);
				List<CtxIdentifier> attributeIdentifiers = this.internalCtxBroker.lookup(socialNetworkEntity.getId(),CtxModelType.ATTRIBUTE, type).get();
				
				if (attributeIdentifiers.size() > 0) {
					
					LOG.info("updating "+snName+" profile in context 1");
					CtxIdentifier attrId = attributeIdentifiers.get(0);
					attribute = (CtxAttribute) this.internalCtxBroker.retrieve(attrId).get();
					attribute = setAttrValueType(attribute, type, value);
        			attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				}
				else {
					LOG.info("Insert attribute in user profile in context 2");
					attribute = this.internalCtxBroker.createAttribute( socialNetworkEntity.getId(), type).get();
					attribute = setAttrValueType(attribute, type, value);
					attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				}

				socialNetworkEntity = (CtxEntity) this.internalCtxBroker.update( socialNetworkEntity).get();
			}
			else LOG.warn("Social Network Entity is NULL");
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

		return socialNetworkEntity;
	}
	
	
	
	private CtxEntity storeFriendsIntoContextBroker(List<Person> friends) {

		CtxAttribute attribute = null;

    	try {
	
			if (socialNetworkEntity != null) {
				
				LOG.info(">>>>> Check if FRIENDS fiels is already stored ");
				List<CtxIdentifier> attributeFriendsIdentifiers = this.internalCtxBroker.lookup(socialNetworkEntity.getId(),CtxModelType.ATTRIBUTE, CtxAttributeTypes.FRIENDS).get();
				for (CtxIdentifier ctxID : attributeFriendsIdentifiers){
					this.internalCtxBroker.remove(ctxID);
				}
				
				for(Person friend : friends){	
			
					String value = friend.getDisplayName();
					LOG.info("Insert Frind:" + value +"+  to user profile");
					attribute = this.internalCtxBroker.createAttribute( socialNetworkEntity.getId(), CtxAttributeTypes.FRIENDS).get();
					attribute = setAttrValueType(attribute, CtxAttributeTypes.FRIENDS, value);
					attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				}

				socialNetworkEntity = (CtxEntity) this.internalCtxBroker.update( socialNetworkEntity).get();
			}
			else LOG.warn("Social Network Entity is NULL");
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
    	
    	return socialNetworkEntity;

	}
	
	
	private CtxEntity storeGroupsIntoContextBroker(List<Group> groups) {

		CtxAttribute attribute = null;

    	try {
	
			if (socialNetworkEntity != null) {
				
				LOG.info(">>>>> Check if GROUPS fiels is already stored ");
				List<CtxIdentifier> attributeGroupssIdentifiers = this.internalCtxBroker.lookup(socialNetworkEntity.getId(),CtxModelType.ATTRIBUTE, CtxAttributeTypes.GROUP).get();
				for (CtxIdentifier ctxID : attributeGroupssIdentifiers){
					this.internalCtxBroker.remove(ctxID);
				} 
				
				for(Group group : groups){	
			
					String value = group.getTitle();
					LOG.info("Insert Group:" + value +"+  to user profile");
					attribute = this.internalCtxBroker.createAttribute( socialNetworkEntity.getId(), CtxAttributeTypes.GROUP).get();
					attribute = setAttrValueType(attribute, CtxAttributeTypes.GROUP, value);
					attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				}

				socialNetworkEntity = (CtxEntity) this.internalCtxBroker.update( socialNetworkEntity).get();
			}
			else LOG.warn("Social Network Entity is NULL");
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
    	
    	return socialNetworkEntity;

	}


	private String updateListData(List<String> data) {

		String valueString = "";
		try {
			for (int i = 0; i < data.size(); i++) {
				JSONObject jsonResponse = new JSONObject(data.get(i));
				String value = jsonResponse.get("value").toString();
				if (valueString.length() > 0)
					valueString += ",";
				valueString += value;
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return valueString;
	}

	private CtxAttribute setAttrValueType(CtxAttribute attribute, String type, Serializable value) {

		CtxAttribute updatedAttr = attribute;

		if (value == null) {
			updatedAttr.setValueType(CtxAttributeValueType.EMPTY);
			return updatedAttr;
		} else if (value instanceof String) {
			updatedAttr.setValueType(CtxAttributeValueType.STRING);
			updatedAttr.setStringValue(value.toString());
			return updatedAttr;
		} else if (value instanceof Integer) {
			updatedAttr.setValueType(CtxAttributeValueType.INTEGER);
			updatedAttr.setIntegerValue((Integer) value);

			return updatedAttr;
		} else if (value instanceof Double) {
			updatedAttr.setValueType(CtxAttributeValueType.DOUBLE);
			updatedAttr.setDoubleValue((Double) value);

			return updatedAttr;
		} else if (value instanceof byte[]) {
			updatedAttr.setValueType(CtxAttributeValueType.BINARY);
			updatedAttr.setStringValue("_BLOB_");

			return updatedAttr;
		} else
			throw new IllegalArgumentException(value + ": Invalid value type");

	}

}