package org.societies.platform.socialdata;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.shindig.social.opensocial.model.Person;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.context.model.CtxAttributeValueType;
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
		LOG.info("updating user profile in context: broker service:"+internalCtxBroker);
		this.cssId = cssId;
		this.internalCtxBroker = internalCtxBroker;
	}

	public void updateCtxProfile(Person profile){

		LOG.info("update music data");
		if(profile.getMusic() != null){
			String music = updateFBData(profile.getMusic());
			LOG.info("update music data"+ music );
			CtxAttribute updatedMusicdAtt = storeContext(CtxAttributeTypes.MUSIC, music);
			LOG.info("update music data "+updatedMusicdAtt);
		}
		
		LOG.info("update books data");
		if(profile.getBooks() != null){
			String books = updateFBData(profile.getBooks());
			LOG.info("update books data"+ books );
			CtxAttribute updatedBooksdAtt = storeContext(CtxAttributeTypes.BOOKS, books);
			LOG.info("update books data "+updatedBooksdAtt);
		}
	}


	private CtxAttribute storeContext(String type, Serializable value){

		CtxAttribute attribute = null;
		IndividualCtxEntity individualEntity ; 

		try {
			individualEntity = this.internalCtxBroker.retrieveIndividualEntity(this.cssId).get();

			LOG.info("updating user profile in context "+ type+" values"+ value);

			List<CtxIdentifier> attributeIdentifiers = this.internalCtxBroker.lookup(individualEntity.getId(), CtxModelType.ATTRIBUTE, CtxAttributeTypes.BOOKS).get();
			if (attributeIdentifiers.size()>0){
				LOG.info("updating user profile in context 1");
				CtxIdentifier attrId = attributeIdentifiers.get(0);
				attribute = (CtxAttribute) this.internalCtxBroker.retrieve(attrId).get();
				attribute = setAttrValueType(attribute, type,  value);

				attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
			} else {
				LOG.info("updating user profile in context 2");
				attribute = this.internalCtxBroker.createAttribute(individualEntity.getId(), CtxAttributeTypes.BOOKS).get();
				attribute = setAttrValueType(attribute, type,  value);
				attribute = (CtxAttribute) this.internalCtxBroker.update(attribute).get();
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
		return attribute;
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