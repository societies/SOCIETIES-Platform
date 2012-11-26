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
package org.societies.android.platform.ctxclient;


//import java.net.URL;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.societies.android.api.context.broker.ICtxClient;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAssociationIdentifier;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.CtxException;
import org.societies.api.context.event.CtxChangeEvent;
import org.societies.api.context.event.CtxChangeEventListener;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

/**
 * Describe your class here...
 *
 * @author pkosmides
 *
 */
public class ContextBrokerBase implements ICtxClient {

    private static final String LOG_TAG = ContextBrokerBase.class.getName();

    //COMMS REQUIRED VARIABLES
	private static final List<String> ELEMENT_NAMES = Arrays.asList("requestorServiceBean", "requestorBean", "dataIdentifier", 
			"requestorCisBean", "dataIdentifierScheme", "ctxIdentifierBean", "ctxEntityIdentifierBean", 
			"ctxAttributeIdentifierBean", "ctxAssociationIdentifierBean", "ctxModelObjectBean", "ctxEntityBean", 
			"ctxAssociationBean", "ctxAttributeBean", "ctxQualityBean", "communityMemberCtxEntityBean", 
			"individualCtxEntityBean", "communityCtxEntityBean", "ctxBondBean", "ctxHistoryAttributeBean", 
			"ctxModelTypeBean", "ctxBondOriginTypeBean", "ctxAttributeValueTypeBean", "ctxOriginTypeBean", "ctxUIElement", 
			"ctxBrokerRequestBean", "ctxBrokerResponseBean", "createEntityBean", "createAttributeBean", "createAssociationBean", 
			"retrieveBean", "retrieveIndividualEntityIdBean", "retrieveCommunityEntityIdBean", "updateBean", 
			"updateAttributeBean", "removeBean", "lookupBean", "brokerMethodBean");

	  
	private final static List<String> NAME_SPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");
	
	
    private ClientCommunicationMgr commMgr;
    private Context androidContext;

    private static ExpiringCache<ACtxIdentifier, ACtxModelObject> cache = new ExpiringCache();

	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";


	/**
	 * The IIdentity Mgmt service reference.
	 *
	 * @see {@link #setIdentityMgr(IIdentityManager)}
	 */
	private IIdentityManager idMgr;

    public ContextBrokerBase(Context androidContext) {

    	Log.d(LOG_TAG, "ContextBrokerBase created");
    	
    	this.androidContext = androidContext;
    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }  
		
    }

	public ACtxEntity createEntity(String client, String type) throws CtxException {


		return this.createEntity(client, null, null, type);
	}

	public ACtxEntity createEntity(String client, Requestor requestor,
			IIdentity targetCss, String type) throws CtxException {

		
		if (requestor == null) requestor = this.getLocalRequestor();
		if (targetCss == null) targetCss = this.getLocalIdentity();

		ACtxEntity entityResult = null;

		if (idMgr.isMine(targetCss)) {

			entityResult = this.userCtxDBMgr.createEntity(type);

		}else {

			final CreateEntityCallback callback = new CreateEntityCallback();
			this.ctxBrokerClient.createEntity(requestor, targetCss, type, callback);

			synchronized (callback) {
				try {
					callback.wait();
					entityResult = callback.getResult();
				} catch (InterruptedException e) {

					throw new CtxBrokerException("Interrupted while waiting for remote createEntity: "+e.getLocalizedMessage(),e);
				}
			}
		}

		return new AsyncResult<CtxEntity>(entityResult);
////////////////////////
		final ACtxEntityIdentifier identifier;

		identifier = new ACtxEntityIdentifier(this.privateIdtoString, 
					type, CtxModelObjectNumberGenerator.getNextValue());

		final ACtxEntity entity = new  ACtxEntity(identifier);
		if (entity.getId()!=null)
			Log.d(LOG_TAG, "Maps key is OK!!");
		else
			Log.d(LOG_TAG, "Problem with maps key!!");
	//	modelObjects.put(entity.getId(), entity);
		cache.put(entity.getId(), entity);
		Log.d(LOG_TAG, "Entity cached - " + entity);

		return entity;
	}

	public ACtxAttribute createAttribute(String client, Requestor requestor,
			ACtxEntityIdentifier scope, String type) throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

//		final CtxEntity entity = (CtxEntity) modelObjects.get(scope);
		final ACtxEntity entity = (ACtxEntity) cache.get(scope);

		if (entity == null)	
			throw new NullPointerException("Scope not found: " + scope);

		ACtxAttributeIdentifier attrIdentifier = new ACtxAttributeIdentifier(scope, type, CtxModelObjectNumberGenerator.getNextValue());
		final ACtxAttribute attribute = new ACtxAttribute(attrIdentifier);

//		this.modelObjects.put(attribute.getId(), attribute);
		cache.put(attribute.getId(), attribute);
		entity.addAttribute(attribute);
		Log.d(LOG_TAG, "Attribute cached - " + attribute);

		return attribute;

	}

	public ACtxAssociation createAssociation(String client,
			Requestor requestor, IIdentity targetCss, String type)
			throws CtxException {

		if (type == null)
			throw new NullPointerException("type can't be null");

		final ACtxAssociationIdentifier identifier;

		identifier = new ACtxAssociationIdentifier(this.privateIdtoString, 
				type, CtxModelObjectNumberGenerator.getNextValue());

		final ACtxAssociation association = new  ACtxAssociation(identifier);
		cache.put(association.getId(), association);		

		return association;

	}

	public List<ACtxIdentifier> lookup(String client, Requestor requestor,
			IIdentity target, CtxModelType modelType, String type)
			throws CtxException {

		final List<ACtxIdentifier> foundList = new ArrayList<ACtxIdentifier>();

		for (ACtxIdentifier identifier : cache.keySet()) {
			if (identifier.getModelType().equals(modelType) && identifier.getType().equals(type)) {
				foundList.add(identifier);
			}		
		}
		return foundList;
	}

	public List<ACtxIdentifier> lookup(String client, Requestor requestor,
			ACtxEntityIdentifier entityId, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxEntityIdentifier> lookupEntities(String client,
			Requestor requestor, IIdentity targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {

        final List<ACtxEntityIdentifier> foundList = new ArrayList<ACtxEntityIdentifier>();
        for (ACtxIdentifier identifier : cache.keySet()) {
            if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)
                    && identifier.getType().equals(attribType)) {
                final ACtxAttribute attribute = (ACtxAttribute) cache.get(identifier);
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

	public ACtxModelObject remove(String client, Requestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, Requestor requestor,
			ACtxIdentifier identifier) throws CtxException {

		return this.cache.get(identifier);
	}

	public ACtxEntityIdentifier retrieveIndividualEntityId(String client,
			Requestor requestor, IIdentity cssId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxEntityIdentifier retrieveCommunityEntityId(String client,
			Requestor requestor, IIdentity cisId) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject update(String client, Requestor requestor,
			ACtxModelObject modelObject) throws CtxException {

		if (cache.keySet().contains(modelObject.getId())) {
			cache.put(modelObject.getId(), modelObject);
		}

		 if (modelObject instanceof ACtxAssociation) {

			   ACtxEntity ent = null;
			   ACtxEntityIdentifier entId;

			   // Add association to parent entity
			   entId = ((ACtxAssociation) modelObject).getParentEntity();
			   if (entId != null)
			     ent = (ACtxEntity) this.retrieve(entId);
			     if (ent != null)
			       ent.addAssociation(((ACtxAssociation) modelObject).getId());

			    // Add association to child entities
			    Set<ACtxEntityIdentifier> entIds = ((ACtxAssociation) modelObject).getChildEntities();
			    for (ACtxEntityIdentifier entIdent : entIds) {
			    	//entIdent = ((CtxAssociation) modelObject).getParentEntity();
			    	ent = (ACtxEntity) this.retrieve(entIdent);
			    	if (ent != null)
			    		ent.addAssociation(((ACtxAssociation) modelObject).getId());
			    }
		}

		return modelObject;

	}
	
	public Requestor getLocalRequestor() throws CtxBrokerException {

		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();

		IIdentity cssOwnerId;
		Requestor requestor = null;
		try {
			cssOwnerId = this.idMgr.fromJid(cssNodeId.getBareJid());
			requestor = new Requestor(cssOwnerId);
		} catch (InvalidFormatException e) {

			throw new CtxBrokerException(" requestor could not be set: " + e.getLocalizedMessage(), e);

		}	

		return requestor;
	}


	private IIdentity getLocalIdentity() throws CtxBrokerException {

		IIdentity cssOwnerId = null;
		INetworkNode cssNodeId = this.idMgr.getThisNetworkNode();

		try {
			cssOwnerId = this.idMgr.fromJid(cssNodeId.getBareJid());

		} catch (InvalidFormatException e) {
			throw new CtxBrokerException(" cssOwnerId could not be set: " + e.getLocalizedMessage(), e);
		}

		return cssOwnerId;	
	}

	
	//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	/**
	 * Callback required for Android Comms Manager
	 */
	private class ContextBrokerCallback implements ICommCallback {
		private String returnIntent;
		private String client;

		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public ContextBrokerCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}

		public List<String> getXMLNamespaces() {
			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {
			return PACKAGES;
		}

		public void receiveError(Stanza stanza, XMPPError err) {
			Log.d(LOG_TAG, "Callback receiveError:" + err.getMessage());			
		}

		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
			Log.d(LOG_TAG, "Callback receiveInfo");
		}

		public void receiveItems(Stanza stanza, String node, List<String> items) {
			Log.d(LOG_TAG, "Callback receiveItems");
		}

		public void receiveMessage(Stanza stanza, Object payload) {
			Log.d(LOG_TAG, "Callback receiveMessage");	
			// TODO
			
		}

		public void receiveResult(Stanza returnStanza, Object msgBean) {

			//CHECK WHICH END SERVICE IS SENDING THE MESSAGE
			if (LOG.isInfoEnabled()) // TODO DEBUG
				LOG.info("receiveResult: stanza=" + returnStanza + ", msgBean=" + msgBean);
			
			if (!(msgBean instanceof CtxBrokerResponseBean)) {
				LOG.error("Could not handle result bean: Unexpected type: " 
						+ ((msgBean != null) ? msgBean.getClass() : "null"));
				return;
			}
			
			final ICtxCallback callbackClient = this.getRequestingClient(returnStanza.getId());
			if (callbackClient == null) {
				LOG.error("Could not handle result bean: No callback client found for stanza with id: " 
						+ returnStanza.getId());
				return;
			}
			final CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
			final BrokerMethodBean method = payload.getMethod();
			try {
				switch (method) {
				
				case CREATE_ENTITY:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult CREATE ENTITY");
					if (payload.getCreateEntityBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null");
						return;
					}
					final CtxEntityBean entityBean = payload.getCreateEntityBeanResult();
					final CtxEntity entity = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(entityBean);
					callbackClient.onCreatedEntity(entity);
				
					break;
				
				case CREATE_ATTRIBUTE:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult CREATE ATTRIBUTE");
					if (payload.getCreateAttributeBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getCreateAttributeBeanResult() is null");
						return;
					}
					final CtxAttributeBean attrBean = payload.getCreateAttributeBeanResult();
					final CtxAttribute attr = CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(attrBean);
					callbackClient.onCreatedAttribute(attr);

					break;
					
				case CREATE_ASSOCIATION:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult CREATE ASSOCIATION");
					if (payload.getCreateAssociationBeanResult() == null ) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getCreateAssociationBeanResult() is null");
						return;
					}
					final CtxAssociationBean assocBean = payload.getCreateAssociationBeanResult();
					final CtxAssociation assoc = CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(assocBean);
					callbackClient.onCreatedAssociation(assoc);

					break;
					
				case RETRIEVE:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult RETRIEVE");
					if (payload.getRetrieveBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getRetrieveBeanResult() is null");
						return;
					}
					final CtxModelObjectBean objectBean = payload.getRetrieveBeanResult();
					final CtxModelObject object = CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(objectBean);
					callbackClient.onRetrieveCtx(object);
					
					break;
					
				case UPDATE:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult UPDATE");
					if (payload.getUpdateBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getUpdateBeanResult() is null");
						return;
					}
					final CtxModelObjectBean updatedObjectBean = payload.getUpdateBeanResult();
					final CtxModelObject updatedObject = CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(updatedObjectBean);	
					callbackClient.onUpdateCtx(updatedObject); 

					break;
					
				case RETRIEVE_INDIVIDUAL_ENTITY_ID:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult RetrieveIndividualEntity");
					if (payload.getRetrieveIndividualEntityIdBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getRetrieveIndividualEntityIdBeanResult() is null");
						return;
					}
					final CtxEntityIdentifierBean individualEntIdBean = 
							payload.getRetrieveIndividualEntityIdBeanResult();
					final CtxIdentifier individualEntId = CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifierBean(individualEntIdBean);
					if (individualEntId instanceof CtxEntityIdentifier)
						callbackClient.onRetrievedEntityId(
								(CtxEntityIdentifier) individualEntId);
					else 
						LOG.error ("Returned ctxIdentifier is not a CtxEntityIdentifier");
					
					break;

				case RETRIEVE_COMMUNITY_ENTITY_ID:

					if (LOG.isInfoEnabled()) // TODO DEBUG
						LOG.info("receiveResult: method=RETRIEVE_COMMUNITY_ENTITY_ID");
					if(payload.getRetrieveCommunityEntityIdBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getRetrieveCommunityEntityIdBeanResult() is null");
						return;
					}
					final CtxEntityIdentifierBean communityEntityIdBean = 
							payload.getRetrieveCommunityEntityIdBeanResult();
					final CtxIdentifier communityEntityId = CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifierBean(communityEntityIdBean);
					if (communityEntityId instanceof CtxEntityIdentifier)
						callbackClient.onRetrievedEntityId(
								(CtxEntityIdentifier) communityEntityId);
					else 
						LOG.error ("Returned ctxIdentifier is not a CtxEntityIdentifier");
					
					break;
				
				case REMOVE:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult REMOVE");
					if (payload.getRemoveBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getRemoveBeanResult() is null");
						return;
					}
					final CtxModelObjectBean removedObjectBean = payload.getRemoveBeanResult();
					final CtxModelObject removedObject = (removedObjectBean != null) 
							? CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(removedObjectBean)
									: null;
					callbackClient.onRemovedModelObject(removedObject);
					
					break;
					
				case LOOKUP:
					
					if (LOG.isInfoEnabled())
						LOG.info("inside receiveResult LOOKUP");
					if (payload.getCtxBrokerLookupBeanResult() == null) {
						LOG.error("Could not handle result bean: CtxBrokerResponseBean.getCtxBrokerLookupBeanResult() is null");
						return;
					}
					final List<CtxIdentifierBean> ctxIdBeanList = payload.getCtxBrokerLookupBeanResult();
					final List<CtxIdentifier> ctxIdList = new ArrayList<CtxIdentifier>();
					for(final CtxIdentifierBean ctxIdBean : ctxIdBeanList) {
						final CtxIdentifier ctxId = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ctxIdBean);	
						ctxIdList.add(ctxId);
					}
					callbackClient.onLookupCallback(ctxIdList); 

					break;
				 
				default:
				
					LOG.error("Could not handle result bean: Unsupported method: " + method);
				}
				
			} catch (Exception e) {

				LOG.error("Could not handle result bean " + msgBean + ": "
						+ e.getLocalizedMessage(), e);
			}		
		}
		
		
		
	}//>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>> END COMMS CALLBACK >>>>>>>>>>>>>>>>>>>>>>>>>>>>>>
	
}
