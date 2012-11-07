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


public class ContextUpdater {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ContextUpdater.class);


	ICtxBroker internalCtxBroker = null;
	IIdentity cssId = null;


	public ContextUpdater(ICtxBroker internalCtxBroker, IIdentity cssId){
		//LOG.info("updating user profile in context: broker service:"+internalCtxBroker);
		this.cssId = cssId;
		this.internalCtxBroker = internalCtxBroker;
	}

	public void updateCtxProfile(Person profile) {

		//LOG.info("update music data");
		if(profile.getMusic() != null){
			String music = updateFBData(profile.getMusic());
			LOG.info("update music data"+ music );
			CtxEntity facebookEntity =  storeContextFB(CtxAttributeTypes.MUSIC, music);
			LOG.info("facebook entity updated with music data "+facebookEntity.getId());
		}

		//	LOG.info("update books data");
		if(profile.getBooks() != null){
			String books = updateFBData(profile.getBooks());
			LOG.info("update books data "+ books );
			CtxEntity facebookEntity = storeContextFB(CtxAttributeTypes.BOOKS, books);
			LOG.info("facebook entity updated with books data "+facebookEntity.getId());
		}
	}


	private CtxEntity getFBCtxEntity(){

		IndividualCtxEntity individualEntity ; 
		CtxAssociation snsAssoc = null;
		CtxEntity facebook = null;

		try {
			individualEntity = this.internalCtxBroker.retrieveIndividualEntity(this.cssId).get();
			Set<CtxAssociationIdentifier> snsAssocSet = individualEntity.getAssociations(CtxAssociationTypes.IS_CONNECTED_TO_SNS);

			if(snsAssocSet.size()>0) {
				List<CtxAssociationIdentifier> snsAssocList = new ArrayList<CtxAssociationIdentifier>(snsAssocSet);
				CtxAssociationIdentifier assocID = snsAssocList.get(0);
				snsAssoc = (CtxAssociation) this.internalCtxBroker.retrieve(assocID).get();
				Set<CtxEntityIdentifier> snsEntitiesSet = snsAssoc.getChildEntities(CtxEntityTypes.SOCIAL_NETWORK);
				List<CtxEntityIdentifier> snsEntitiesList = new ArrayList<CtxEntityIdentifier>(snsEntitiesSet);
			
				List<CtxEntityIdentifier> facebookEntList = this.internalCtxBroker.lookupEntities(snsEntitiesList, CtxAttributeTypes.NAME, "Facebook").get();
			
				if(facebookEntList.size()>0){

					facebook = (CtxEntity) this.internalCtxBroker.retrieve(facebookEntList.get(0)).get();
				
					return facebook;
				}				

			} if(snsAssocSet.size() == 0){

				snsAssoc = this.internalCtxBroker.createAssociation(CtxAssociationTypes.IS_CONNECTED_TO_SNS).get();
				//LOG.info("snsAssoc created ");
				List<CtxEntityIdentifier> facebookEntitiesList = this.internalCtxBroker.lookupEntities(CtxEntityTypes.SOCIAL_NETWORK, CtxAttributeTypes.NAME, "facebook","facebook").get();

				if(facebookEntitiesList.size() == 0 ){
					facebook = this.internalCtxBroker.createEntity(CtxEntityTypes.SOCIAL_NETWORK).get();
					//LOG.info("SOCIAL_NETWORK entity created "+facebook.getId());
					CtxAttribute snsNameAttr = this.internalCtxBroker.createAttribute(facebook.getId(), CtxAttributeTypes.NAME).get();
					snsNameAttr.setStringValue("facebook");

					this.internalCtxBroker.update(snsNameAttr);

				} else facebook = (CtxEntity) this.internalCtxBroker.retrieve(facebookEntitiesList.get(0)).get();

				snsAssoc.addChildEntity(facebook.getId());
				snsAssoc.addChildEntity(individualEntity.getId());
				snsAssoc.setParentEntity(individualEntity.getId());
				snsAssoc = (CtxAssociation) this.internalCtxBroker.update(snsAssoc).get();
				this.internalCtxBroker.update(individualEntity);
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

		return facebook;
	}


	private CtxEntity storeContextFB(String type, Serializable value){

		CtxAttribute attribute = null;

		CtxEntity facebook = null;
		CtxEntity updatedFacebook = null;

		try {
			facebook = this.getFBCtxEntity();

			LOG.info("updating user profile in context "+ type+" values"+ value);

			if(facebook != null){

				List<CtxIdentifier> attributeIdentifiers = this.internalCtxBroker.lookup(facebook.getId(), CtxModelType.ATTRIBUTE, type).get();
				if (attributeIdentifiers.size()>0){
				//	LOG.info("updating fb profile in context 1");
					CtxIdentifier attrId = attributeIdentifiers.get(0);
					attribute = (CtxAttribute) this.internalCtxBroker.retrieve(attrId).get();
					attribute = setAttrValueType(attribute, type,  value);

					attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				} else {
					//LOG.info("updating user profile in context 2");
					attribute = this.internalCtxBroker.createAttribute(facebook.getId(), type).get();
					attribute = setAttrValueType(attribute, type,  value);
					attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
				}

				updatedFacebook = (CtxEntity) this.internalCtxBroker.update(facebook).get();
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

		return updatedFacebook;
	}


	private String updateFBData (List<String> data) {

		String valueString = "";
		try {				
			for(int i=0; i<data.size();i++){
				JSONObject jsonResponse = new JSONObject(data.get(i));
				String value = jsonResponse.get("value").toString();
				if (valueString.length()>0) valueString+=",";
				valueString += value;	
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return valueString;
	}


	private CtxAttribute setAttrValueType(CtxAttribute attribute, String type, Serializable value){

		CtxAttribute updatedAttr = attribute;

		if (value == null){
			updatedAttr.setValueType(CtxAttributeValueType.EMPTY);
			return updatedAttr;
		}
		else if (value instanceof String){
			updatedAttr.setValueType(CtxAttributeValueType.STRING);
			updatedAttr.setStringValue(value.toString());
			return updatedAttr;
		}
		else if (value instanceof Integer){
			updatedAttr.setValueType(CtxAttributeValueType.INTEGER);
			updatedAttr.setIntegerValue((Integer) value);

			return updatedAttr;
		}
		else if (value instanceof Double){
			updatedAttr.setValueType(CtxAttributeValueType.DOUBLE);
			updatedAttr.setDoubleValue((Double) value);

			return updatedAttr;
		}
		else if (value instanceof byte[]){
			updatedAttr.setValueType(CtxAttributeValueType.BINARY);
			updatedAttr.setStringValue("_BLOB_");

			return updatedAttr;
		}
		else
			throw new IllegalArgumentException(value + ": Invalid value type");

	}


	/*
	private static String updateBooks(List<String> data) {

		String booksString = "";
		try {				
			for(int i=0; i<data.size();i++){
				JSONObject jsonResponse = new JSONObject(data.get(i));
				String  books = jsonResponse.get("value").toString();
				if (booksString.length()>0) booksString+=",";
				booksString +=  books;	
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return booksString;
	}



	private String updateMusic(List<String> data) {

		String musicString = "";
		try {				
			for(int i=0; i<data.size();i++){
				JSONObject jsonResponse = new JSONObject(data.get(i));
				String music = jsonResponse.get("value").toString();
				if (musicString.length()>0) musicString+=",";
				musicString += music;	
			}
		}
		catch (JSONException e) {
			e.printStackTrace();
		}
		return musicString;
	}


	 */
}