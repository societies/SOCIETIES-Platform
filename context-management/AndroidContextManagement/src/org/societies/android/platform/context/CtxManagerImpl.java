package org.societies.android.platform.context;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.societies.android.api.internal.context.broker.ICtxClientBroker;

/// allaksa se kanoniko api
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBond;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.personalisation.model.IAction;
//import org.societies.api.schema.useragent.monitoring.MethodType;
//import org.societies.api.schema.useragent.monitoring.UserActionMonitorBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.identity.IdentityManagerImpl;

import android.util.Log;

public class CtxManagerImpl  implements ICtxClientBroker {

	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";

	private static ExpiringCache<CtxIdentifier, CtxModelObject> cache = new ExpiringCache();

	private static final String LOG_TAG = ContextManagement.class.getName();

	ClientCommunicationMgr ccm;
	IIdentity destination;
	
	
	public CtxManagerImpl(ClientCommunicationMgr ccm, IIdentity destination) {
		this.ccm = ccm;
		this.destination = destination;
	}
	public CtxAssociation createAssociation(String type)
			throws CtxException {

		if (type == null)
			throw new NullPointerException("type can't be null");

		final CtxAssociationIdentifier identifier;
		
		identifier = new CtxAssociationIdentifier(this.privateIdtoString, 
				type, CtxModelObjectNumberGenerator.getNextValue());

		final CtxAssociation association = new  CtxAssociation(identifier);
		cache.put(association.getId(), association);		

		return association;
	}

	public CtxAttribute createAttribute(CtxEntityIdentifier scope,
			String type) throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

//		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);
		final CtxEntity entity = (CtxEntity) cache.get(scope);
		
		if (entity == null)	
			throw new NullPointerException("Scope not found: " + scope);
		
		CtxAttributeIdentifier attrIdentifier = new CtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final CtxAttribute attribute = new CtxAttribute(attrIdentifier);

//		this.modelObjects.put(attribute.getId(), attribute);
		cache.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		Log.d(LOG_TAG, "Attribute cached - " + attribute);
		
		return attribute;
		// on internalctxbroker
		//		return new AsyncResult<CtxAttribute>(attribute);
	}

	public CtxEntity createEntity(String type) throws CtxException {

		final CtxEntityIdentifier identifier;
		
		identifier = new CtxEntityIdentifier(this.privateIdtoString, 
					type, CtxModelObjectNumberGenerator.getNextValue());

		final CtxEntity entity = new  CtxEntity(identifier);
		if (entity.getId()!=null)
			Log.d(LOG_TAG, "Maps key is OK!!");
		else
			Log.d(LOG_TAG, "Problem with maps key!!");
	//	modelObjects.put(entity.getId(), entity);
		cache.put(entity.getId(), entity);
		Log.d(LOG_TAG, "Entity cached - " + entity);
		
		return entity;
	}

	public void disableCtxMonitoring(CtxAttributeValueType arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void disableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void enableCtxMonitoring(CtxAttributeValueType arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void enableCtxRecording() throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public List<CtxAttributeIdentifier> getHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxIdentifier> lookup(CtxModelType modelType, String type)
			throws CtxException {

		final List<CtxIdentifier> foundList = new ArrayList<CtxIdentifier>();
	
		for (CtxIdentifier identifier : cache.keySet()) {
			if (identifier.getModelType().equals(modelType) && identifier.getType().equals(type)) {
				foundList.add(identifier);
			}		
		}
		return foundList;
	}

	public List<CtxEntityIdentifier> lookupEntities(String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue)
			throws CtxException {

        final List<CtxEntityIdentifier> foundList = new ArrayList<CtxEntityIdentifier>();
        for (CtxIdentifier identifier : cache.keySet()) {
            if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)
                    && identifier.getType().equals(attribType)) {
                final CtxAttribute attribute = (CtxAttribute) cache.get(identifier);
//                if (attribute.getScope().getType().equals(entityType) && attribute.getValue().equals(minAttribValue)) {
                if (attribute.getScope().getType().equals(entityType)) {
                	if (minAttribValue instanceof String && maxAttribValue instanceof String) {
                		if (attribute.getStringValue()!=null) {
		                	String valueStr = attribute.getStringValue();
		                		if(valueStr.compareTo(minAttribValue.toString()) >=0 && valueStr.compareTo(maxAttribValue.toString()) <=0)
		               				foundList.add(attribute.getScope());                			
        				}
                	} else if (minAttribValue instanceof Integer && maxAttribValue instanceof Integer) {
                		if(attribute.getIntegerValue()!=null) {
		               		Integer valueInt = attribute.getIntegerValue();
		          			if(valueInt.compareTo((Integer) minAttribValue) >=0 && valueInt.compareTo((Integer) maxAttribValue) <=0)
		               			foundList.add(attribute.getScope());
                		}
                	} else if (minAttribValue instanceof Double && maxAttribValue instanceof Double) {
                		if(attribute.getDoubleValue()!=null) {
		               		Double valueDouble = attribute.getDoubleValue();
		           			if(valueDouble.compareTo((Double) minAttribValue) >= 0 && valueDouble.compareTo((Double) maxAttribValue) <= 0)
		               			foundList.add(attribute.getScope());                			
                		}
                	} else {
                		byte[] valueBytes;
                		byte[] minValueBytes;
                		byte[] maxValueBytes;
						try {
							minValueBytes = SerialisationHelper.serialise(minAttribValue);
							maxValueBytes = SerialisationHelper.serialise(maxAttribValue);
							valueBytes = SerialisationHelper.serialise(attribute.getBinaryValue());
							if (Arrays.equals(minValueBytes, maxValueBytes))
								if (Arrays.equals(valueBytes, minValueBytes))
									foundList.add(attribute.getScope());
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}                		
                	}
                	
                }
            }
        }
        return foundList;
	}

	public void registerForChanges(CtxChangeEventListener arg0,
			CtxIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void registerForChanges(CtxChangeEventListener arg0,
			CtxEntityIdentifier arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public CtxModelObject remove(CtxIdentifier arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean removeHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxModelObject retrieve(CtxIdentifier id)
			throws CtxException {

		return this.cache.get(id);
	}

	public IndividualCtxEntity retrieveAdministratingCSS(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Set<CtxBond> retrieveBonds(CtxEntityIdentifier arg0)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxEntityIdentifier> retrieveCommunityMembers(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxEntityIdentifier> retrieveSubCommunities(
			CtxEntityIdentifier arg0) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public Boolean setHistoryTuples(CtxAttributeIdentifier arg0,
			List<CtxAttributeIdentifier> arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public void unregisterFromChanges(CtxChangeEventListener arg0,
			CtxIdentifier arg1) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public void unregisterFromChanges(CtxChangeEventListener arg0,
			CtxEntityIdentifier arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		
	}

	public CtxModelObject update(CtxModelObject modelObject)
			throws CtxException {

		if (cache.keySet().contains(modelObject.getId())) {
			cache.put(modelObject.getId(), modelObject);
		}
		
		 if (modelObject instanceof CtxAssociation) {

			   CtxEntity ent = null;
			   CtxEntityIdentifier entId;

			   // Add association to parent entity
			   entId = ((CtxAssociation) modelObject).getParentEntity();
			   if (entId != null)
			     ent = (CtxEntity) this.retrieve(entId);
			     if (ent != null)
			       ent.addAssociation(((CtxAssociation) modelObject).getId());

			    // Add association to child entities
			    Set<CtxEntityIdentifier> entIds = ((CtxAssociation) modelObject).getChildEntities();
			    for (CtxEntityIdentifier entIdent : entIds) {
			    	//entIdent = ((CtxAssociation) modelObject).getParentEntity();
			    	ent = (CtxEntity) this.retrieve(entIdent);
			    	if (ent != null)
			    		ent.addAssociation(((CtxAssociation) modelObject).getId());
			    }
		}
			      
		return modelObject;
	}

	public CtxAttribute updateAttribute(CtxAttributeIdentifier arg0,
			Serializable arg1) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public CtxAttribute updateAttribute(CtxAttributeIdentifier arg0,
			Serializable arg1, String arg2) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<CtxAttributeIdentifier> updateHistoryTuples(
			CtxAttributeIdentifier arg0, List<CtxAttributeIdentifier> arg1)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}	

}
