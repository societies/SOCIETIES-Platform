/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru�be in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA��O, SA (PTIN), IBM Corp., 
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
package org.societies.android.platform.context;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.internal.context.IInternalCtxClient;
import org.societies.android.api.context.CtxException;
//import org.societies.android.api.context.ICtxClient;
import org.societies.android.api.comms.IMethodCallback;
import org.societies.android.api.comms.xmpp.Stanza;
import org.societies.android.api.comms.xmpp.XMPPError;
import org.societies.android.api.comms.xmpp.XMPPInfo;
import org.societies.android.api.comms.xmpp.CommunicationException;
import org.societies.android.api.comms.xmpp.ICommCallback;
import org.societies.android.api.css.manager.IServiceManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.context.contextmanagement.CreateAssociationBean;
import org.societies.api.schema.context.contextmanagement.CreateAttributeBean;
import org.societies.api.schema.context.contextmanagement.CreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.LookupBean;
import org.societies.api.schema.context.contextmanagement.RemoveBean;
import org.societies.api.schema.context.contextmanagement.RetrieveBean;
import org.societies.api.schema.context.contextmanagement.RetrieveCommunityEntityIdBean;
import org.societies.api.schema.context.contextmanagement.RetrieveIndividualEntityIdBean;
import org.societies.api.schema.context.contextmanagement.UpdateBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.content.Context;
import android.content.Intent;
import android.os.Parcelable;
import android.util.Log;

/**
 * @author pkosmides
 *
 */
public class ContextBrokerBase implements IInternalCtxClient{

	private static final List<String> ELEMENT_NAMES = Collections.unmodifiableList(Arrays.asList("ctxBrokerRequestBean","ctxBrokerResponseBean"));
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			Arrays.asList("http://societies.org/api/schema/context/model",
					"http://societies.org/api/schema/context/contextmanagement",
					"http://societies.org/api/schema/identity",
					"http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			Arrays.asList("org.societies.api.schema.context.model",
					"org.societies.api.schema.context.contextmanagement",
					"org.societies.api.schema.identity",
					"org.societies.api.schema.servicelifecycle.model"));

	//expiring cache with key value the identifier in string
    private static ExpiringCache<String, CtxModelObjectBean> cache = new ExpiringCache();

	//Logging tag
	private static final String LOG_TAG = ContextBrokerBase.class.getName();
	private Context applicationContext;
	private ClientCommunicationMgr commMgr;
	private boolean connectedToComms = false;
	private boolean restrictBroadcast;

/*	public ContextBrokerBase(Context applicationContext, ClientCommunicationMgr commMgr,
			boolean restrictBroadcast) {
		this.applicationContext = applicationContext;
		this.commMgr = commMgr;
		this.restrictBroadcast = restrictBroadcast;
	}
*/
	/*the other way */
	//Default constructor
    public ContextBrokerBase(Context applicationContext) {
    	this(applicationContext, true);
    }
    
    //Parameterised constructor
    public ContextBrokerBase(Context applicationContext, boolean restrictBroadcast) {
    	Log.d(LOG_TAG, this.getClass().getName() + " instantiated");
    	
    	this.applicationContext = applicationContext;
    	this.restrictBroadcast = restrictBroadcast;
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(applicationContext, true);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }
    }

    public boolean startService() {
    	if (!connectedToComms) {
        	//NOT CONNECTED TO COMMS SERVICE YET
        	Log.d(LOG_TAG, "ContextBrokerBase startService binding to comms");
	        this.commMgr.bindCommsService(new IMethodCallback() {	
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Connected to comms: " + resultFlag);
					if (resultFlag) {
						ContextBrokerBase.this.connectedToComms = true;
						//REGISTER NAMESPACES
						ContextBrokerBase.this.commMgr.register(
								ContextBrokerBase.ELEMENT_NAMES, 
								ContextBrokerBase.NAMESPACES, 
								ContextBrokerBase.PACKAGES, 
								new IMethodCallback() {
							@Override
							public void returnAction(boolean resultFlag) {
								Log.d(LOG_TAG, "Namespaces registered: " + resultFlag);
								//SEND INTENT WITH SERVICE STARTED STATUS
				        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
				        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, resultFlag);
				        		ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
							}
							@Override
							public void returnAction(String result) { }
							@Override
							public void returnException(String result) {
								// TODO Auto-generated method stub
								
							}
						});
					} else {
						Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
			    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, false);
			    		ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
					}
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
					
				}
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STARTED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
    	}
		return true;
    }
    
    public boolean stopService() {
    	if (connectedToComms) {
        	//UNREGISTER AND DISCONNECT FROM COMMS
        	Log.d(LOG_TAG, "ContextBrokerBase stopService unregistering namespaces");
        	this.commMgr.unregister(ELEMENT_NAMES, NAMESPACES, new IMethodCallback() {
				@Override
				public void returnAction(boolean resultFlag) {
					Log.d(LOG_TAG, "Unregistered namespaces: " + resultFlag);
					ContextBrokerBase.this.connectedToComms = false;
					
					ContextBrokerBase.this.commMgr.unbindCommsService();
					//SEND INTENT WITH SERVICE STOPPED STATUS
	        		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
	        		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
	        		ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
				}	
				@Override
				public void returnAction(String result) { }
				@Override
				public void returnException(String result) {
					// TODO Auto-generated method stub
					
				}
			});
        }
    	else {
    		Intent intent = new Intent(IServiceManager.INTENT_SERVICE_STOPPED_STATUS);
    		intent.putExtra(IServiceManager.INTENT_RETURN_VALUE_KEY, true);
    		applicationContext.sendBroadcast(intent);
    	}
    	return true;
    }
    
    /**
	 * @param client
	 */
	private void broadcastServiceNotStarted(String client, String method) {
		if (client != null) {
			Intent intent = new Intent(method);
			intent.putExtra(IServiceManager.INTENT_NOTSTARTED_EXCEPTION, true);
			intent.setPackage(client);
			ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
		}
	}	
	
	private void broadcastException(String client, String method, String message) {

		final Intent intent = new Intent(method);
		intent.putExtra(IInternalCtxClient.INTENT_EXCEPTION_VALUE_KEY, message);
		if (this.restrictBroadcast)
			intent.setPackage(client); 
		this.applicationContext.sendBroadcast(intent);
	}
	
	@Override
	public CtxEntityBean createEntity(String client, RequestorBean requestor,
			String targetCss, String type) throws CtxException {
		Log.d(LOG_TAG, "CreateEntity called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
	//			toIdentity = targetCss;
				toIdentity = idm.fromJid(targetCss);
/*				Stanza stanza = new Stanza(toIdentity);*/
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);
	
				CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
	//			RequestorBean requestorBean = createRequestorBean(requestor);
				ctxBrokerCreateEntityBean.setRequestor(requestor);
				ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
				ctxBrokerCreateEntityBean.setType(type);
	
				cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);
	
	//			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.CREATE_ENTITY);
				toIdentity = this.commMgr.getIdManager().getCloudNode();
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send CREATE_ENTITY request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.CREATE_ENTITY, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.CREATE_ENTITY);
		}
		return null;

	}

	@Override
	public CtxAttributeBean createAttribute(String client,
			RequestorBean requestor, CtxEntityIdentifierBean scope, String type)
			throws CtxException {
		Log.d(LOG_TAG, "CreateAttribute called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = this.commMgr.getIdManager().getCloudNode();
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.CREATE_ATTRIBUTE);

				CreateAttributeBean ctxBrokerCreateAttributeBean = new CreateAttributeBean();
				// 1. set requestorBean
				ctxBrokerCreateAttributeBean.setRequestor(requestor);
				// 2. set scope
				CtxEntityIdentifierBean ctxEntIdBean = new CtxEntityIdentifierBean();
				Log.d(LOG_TAG, "scope used: " + scope.getString());
				ctxEntIdBean.setString(scope.getString());
				ctxBrokerCreateAttributeBean.setScope(ctxEntIdBean);
				// 3. set type
				ctxBrokerCreateAttributeBean.setType(type);
					
				cbPacket.setCreateAttribute(ctxBrokerCreateAttributeBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.CREATE_ATTRIBUTE);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send CREATE_ATTRIBUTE request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.CREATE_ATTRIBUTE, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.CREATE_ATTRIBUTE);
		}
		return null;
	}

	@Override
	public CtxAssociationBean createAssociation(String client,
			RequestorBean requestor, String targetCss, String type)
			throws CtxException {
		Log.d(LOG_TAG, "CreateAssociation called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = idm.fromJid(targetCss);
//				toIdentity = this.commMgr.getIdManager().getCloudNode();
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.CREATE_ASSOCIATION);

				CreateAssociationBean ctxBrokerCreateAssociationBean = new CreateAssociationBean();
				// 1. set requestorBean
				ctxBrokerCreateAssociationBean.setRequestor(requestor);
				// 2. set type
				ctxBrokerCreateAssociationBean.setType(type);
				// 3. set targetCSS
				ctxBrokerCreateAssociationBean.setTargetCss(targetCss);
					
				cbPacket.setCreateAssociation(ctxBrokerCreateAssociationBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.CREATE_ASSOCIATION);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send CREATE_ASSOCIATION request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.CREATE_ASSOCIATION, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.CREATE_ASSOCIATION);
		}
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			RequestorBean requestor, String target, CtxModelTypeBean modelType,
			String type) throws CtxException {
		Log.d(LOG_TAG, "Lookup, giving target id, called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = idm.fromJid(target);
//				toIdentity = this.commMgr.getIdManager().getCloudNode();
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.LOOKUP);

				LookupBean ctxBrokerLookupBean = new LookupBean();
				// 1. set requestorBean
				ctxBrokerLookupBean.setRequestor(requestor);
				// 2. set target id
				ctxBrokerLookupBean.setTargetCss(target);
				// 3. set modelType
				ctxBrokerLookupBean.setModelType(modelType);
				// 4. set type
				ctxBrokerLookupBean.setType(type);
					
				cbPacket.setLookup(ctxBrokerLookupBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.LOOKUP);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send LOOKUP request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.LOOKUP, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.LOOKUP);
		}
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			RequestorBean requestor, CtxEntityIdentifierBean entityId,
			CtxModelTypeBean modelType, String type) throws CtxException {
		Log.d(LOG_TAG, "Lookup called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = this.commMgr.getIdManager().getCloudNode();
					
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.LOOKUP);
	
				LookupBean ctxBrokerLookupBean = new LookupBean();
				// 1. set requestorBean
				ctxBrokerLookupBean.setRequestor(requestor);
				// 2. set target id
				ctxBrokerLookupBean.setTargetCss(toIdentity.getBareJid());
				// 3. set modelType
				ctxBrokerLookupBean.setModelType(modelType);
				// 4. set type
				ctxBrokerLookupBean.setType(type);
						
				cbPacket.setLookup(ctxBrokerLookupBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.LOOKUP);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
					
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
				
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send LOOKUP request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.LOOKUP, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.LOOKUP);
		}
		return null;
	}

	@Override
	public List<CtxEntityIdentifierBean> lookupEntities(String client,
			RequestorBean requestor, String targetCss, String entityType,
			String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxModelObjectBean remove(String client, RequestorBean requestor,
			CtxIdentifierBean identifier) throws CtxException {
		Log.d(LOG_TAG, "Remove called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
//				toIdentity = this.commMgr.getIdManager().getCloudNode();
				toIdentity = this.commMgr.getIdManager().fromJid(identifier.getString());
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.REMOVE);

				RemoveBean ctxBrokerRemoveBean = new RemoveBean();
				// 1. set requestorBean
				ctxBrokerRemoveBean.setRequestor(requestor);
				// 2. set identifier
				ctxBrokerRemoveBean.setId(identifier);
					
				cbPacket.setRemove(ctxBrokerRemoveBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.REMOVE);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send REMOVE request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.REMOVE, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.REMOVE);
		}
		return null;
	}

	@Override
	public CtxModelObjectBean retrieve(String client, RequestorBean requestor,
			CtxIdentifierBean identifier) throws CtxException {
		Log.d(LOG_TAG, "Retrieve called by client: " + client);
		
		CtxModelObjectBean retrObj = cache.get(identifier.getString());

		Log.d(LOG_TAG, "Retrieved object from cache: " + retrObj);
		Log.d(LOG_TAG, "identifier used: " + identifier.getString() + " and getting: " + cache.get(identifier.getString()));
		Log.d(LOG_TAG, "cached objects: " + cache);
		
		//Checking first the cache to retrieve the object
		if (retrObj != null) {

			
			if (client != null) {
				final Intent intent = new Intent(IInternalCtxClient.RETRIEVE);
				intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) retrObj);
				
				intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
				ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
				Log.d(LOG_TAG, "SendBroadcast intent (from cache): " + intent);
			}			
		}
		else {
			if (this.connectedToComms) {
				try {
					IIdentityManager idm = this.commMgr.getIdManager();
					IIdentity toIdentity;
		
	/*				CtxEntityIdentifierBean entityId = new CtxEntityIdentifierBean();
					entityId.setString(identifier.getString());
					Log.d(LOG_TAG, "entityId used to retrieve model object: " + entityId.getString());
					toIdentity = this.commMgr.getIdManager().fromJid(entityId.getString());*/
					
					toIdentity = this.commMgr.getIdManager().getCloudNode();
					
					Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
					CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
					cbPacket.setMethod(BrokerMethodBean.RETRIEVE);
	
					RetrieveBean ctxBrokerRetrieveBean = new RetrieveBean();
					
					// 1. set identifier
					ctxBrokerRetrieveBean.setId(identifier);
					// 2. set requestor
					ctxBrokerRetrieveBean.setRequestor(requestor);
	
					cbPacket.setRetrieve(ctxBrokerRetrieveBean);
		
					ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.RETRIEVE);
					Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
					
					Stanza stanza = new Stanza(toIdentity);
					this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
					Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
				
				} catch (CommunicationException e) {
					Log.e(LOG_TAG, "Error sending XMPP IQ", e);
				} catch (Exception e) {
					final String exceptionMessage = 
							"Failed to send RETRIEVE request: "
							+ e.getMessage(); 
					Log.e(LOG_TAG, exceptionMessage, e);
					this.broadcastException(client, IInternalCtxClient.RETRIEVE, exceptionMessage);
		        } 						
			} else {
				broadcastServiceNotStarted(client, IInternalCtxClient.RETRIEVE);
			}
		}
		return null;
	}

	@Override
	public CtxEntityIdentifierBean retrieveIndividualEntityId(String client,
			RequestorBean requestor, String cssId) throws CtxException {
		Log.d(LOG_TAG, "RetrieveIndividualEntityId called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = this.commMgr.getIdManager().fromJid(cssId);
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.RETRIEVE_INDIVIDUAL_ENTITY_ID);

				RetrieveIndividualEntityIdBean retrieveIndEntBean = new RetrieveIndividualEntityIdBean();
				
				// 1. set requestor
				retrieveIndEntBean.setRequestor(requestor);
				// 2. set identifier
				retrieveIndEntBean.setTargetCss(cssId);

				cbPacket.setRetrieveIndividualEntityId(retrieveIndEntBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_INDIVIDUAL_ENTITY_ID request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.RETRIEVE_INDIVIDUAL_ENTITY_ID);
		}
		return null;

	}

	@Override
	public CtxEntityIdentifierBean retrieveCommunityEntityId(String client,
			RequestorBean requestor, String cisId) throws CtxException {
		Log.d(LOG_TAG, "RetrieveCommunityEntityId called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
				toIdentity = this.commMgr.getIdManager().fromJid(cisId);
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.RETRIEVE_COMMUNITY_ENTITY_ID);

				RetrieveCommunityEntityIdBean retrieveCommEntBean = new RetrieveCommunityEntityIdBean();
				
				// 1. set requestor
				retrieveCommEntBean.setRequestor(requestor);
				// 2. set identifier
				retrieveCommEntBean.setTarget(cisId);

				cbPacket.setRetrieveCommunityEntityId(retrieveCommEntBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.RETRIEVE_COMMUNITY_ENTITY_ID);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send RETRIEVE_COMMUNITY_ENTITY_ID request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.RETRIEVE_COMMUNITY_ENTITY_ID, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.RETRIEVE_COMMUNITY_ENTITY_ID);
		}
		return null;

	}

	@Override
	public CtxModelObjectBean update(String client, RequestorBean requestor,
			CtxModelObjectBean object) throws CtxException {
		Log.d(LOG_TAG, "Update called by client: " + client);
		
		if (this.connectedToComms) {
			try {
				IIdentityManager idm = this.commMgr.getIdManager();
				IIdentity toIdentity;
	
/*				Log.d(LOG_TAG, "before parsing to jid: " + object.getId().getString());
				toIdentity = this.commMgr.getIdManager().fromJid(object.getId().getString());*/
				toIdentity = this.commMgr.getIdManager().getCloudNode();
				
				Log.d(LOG_TAG, "identity used = " + toIdentity.getJid());
				CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
				cbPacket.setMethod(BrokerMethodBean.UPDATE);

				UpdateBean updateBean = new UpdateBean();
				
				// 1. set modelObject
				updateBean.setCtxModelOject(object);
				// 2. set requestor
				updateBean.setRequestor(requestor);

				cbPacket.setUpdate(updateBean);
	
				ICommCallback ctxBrokerCallBack = new ContextBrokerCallback(client, IInternalCtxClient.UPDATE);
				Log.d(LOG_TAG, "cloudNode= " + toIdentity.getJid());
				
				Stanza stanza = new Stanza(toIdentity);
				this.commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxBrokerCallBack);
				Log.d(LOG_TAG, "Sent IQ with stanza=" + stanza);
			
			} catch (CommunicationException e) {
				Log.e(LOG_TAG, "Error sending XMPP IQ", e);
			} catch (Exception e) {
				final String exceptionMessage = 
						"Failed to send UPDATE request: "
						+ e.getMessage(); 
				Log.e(LOG_TAG, exceptionMessage, e);
				this.broadcastException(client, IInternalCtxClient.UPDATE, exceptionMessage);
	        } 						
		} else {
			broadcastServiceNotStarted(client, IInternalCtxClient.UPDATE);
		}
		return null;
	}

	// LOCAL CONTEXT CLIENT
	@Override
	public CtxAssociationBean createAssociation(String client, String type)
			throws CtxException {
		if (type == null)
			throw new NullPointerException("type can't be null");

		createAssociation(client, null, null, type);

		return null;
	}

	@Override
	public CtxAttributeBean createAttribute(String client,
			CtxEntityIdentifierBean scope, String type) throws CtxException {

		if (scope == null)
			throw new NullPointerException("scope can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");
		
		createAttribute(client, null, scope, type);
		
		return null;
	}

	@Override
	public CtxEntityBean createEntity(String client, String type)
			throws CtxException {
		if (type == null)
			throw new NullPointerException("type can't be null");

		createEntity(client, null, null, type);
		
		return null;
	}

	@Override
	public List<CtxEntityIdentifierBean> lookupEntities(String client,
			String entityType, String attribType, Serializable minAttribValue,
			Serializable maxAttribValue) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<CtxIdentifierBean> lookup(String client,
			CtxModelTypeBean modelType, String type) throws CtxException {
		if (modelType == null)
			throw new NullPointerException("modelType can't be null");
		if (type == null)
			throw new NullPointerException("type can't be null");

		String target = null;
		
		lookup(client, null, target, modelType, type);
		
		return null;
	}

	@Override
	public CtxModelObjectBean remove(String client, CtxIdentifierBean identifier)
			throws CtxException {

		if (identifier == null)
			throw new NullPointerException("identifier can't be null");
		
		remove(client, null, identifier);
		
		return null;
	}

	@Override
	public CtxModelObjectBean retrieve(String client,
			CtxIdentifierBean identifier) throws CtxException {
		
		if (identifier == null)
			throw new NullPointerException("identifier can't be null");
		
		retrieve(client, null, identifier);

		return null;
	}

	@Override
	public CtxModelObjectBean update(String client,
			CtxModelObjectBean identifier) throws CtxException {

		if (identifier == null)
			throw new NullPointerException("identifier can't be null");
		
		update(client, null, identifier);
		
		return null;
	}

	@Override
	public CtxAttributeBean updateAttribute(String client,
			CtxAttributeIdentifierBean attributeId, Serializable value)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public CtxAttributeBean updateAttribute(String client,
			CtxAttributeIdentifierBean attributeId, Serializable value,
			String valueMetric) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Callback required for Android Comms Manager
	 */
	private class ContextBrokerCallback implements ICommCallback {

		private String client;
		private String returnIntent;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public ContextBrokerCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}
		
		@Override
		public List<String> getXMLNamespaces() {

			return ContextBrokerBase.NAMESPACES;
		}

		@Override
		public List<String> getJavaPackages() {

			return ContextBrokerBase.PACKAGES;
		}

		@Override
		public void receiveResult(Stanza stanza, Object msgBean) {

			Log.d(LOG_TAG, "ContextBroker Callback receiveResult: stanza=" + stanza
					+ ", msgBean=" + msgBean);
			
			if (client != null) {
				final Intent intent = new Intent(this.returnIntent);
				
				Log.d(LOG_TAG, "Return Stanza: " + stanza.toString());
				if (msgBean==null)
					Log.d(LOG_TAG, "msgBean is null");
				
				if (msgBean instanceof CtxBrokerResponseBean) {
					
					Log.d(LOG_TAG, "receiveResult CtxBrokerRespose");
					
					final CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
					final BrokerMethodBean method = payload.getMethod();
					Log.d(LOG_TAG, "calling method: " + method);
					try {
						switch (method) {
						
						case CREATE_ENTITY:
							
							Log.i(LOG_TAG, "inside receiveResult CREATE ENTITY");
							if (payload.getCreateEntityBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null");
								return;
							}
							final CtxEntityBean entityBean = payload.getCreateEntityBeanResult();

							//Caching created entity
							cache.put(entityBean.getId().getString(), entityBean);
							Log.d(LOG_TAG, "Entity cached - " + entityBean);
							
							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) entityBean);

							break;

						case CREATE_ATTRIBUTE:
							
							Log.i(LOG_TAG, "inside receiveResult CREATE ATTRIBUTE");
							if (payload.getCreateAttributeBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateAttributeBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getCreateAttributeBeanResult() is null");
								return;
							}

							final CtxAttributeBean attributeBean = payload.getCreateAttributeBeanResult();

							//Caching created attribute
							cache.put(attributeBean.getId().getString(), attributeBean);
							Log.d(LOG_TAG, "Attribute cached - " + attributeBean);
							
							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) attributeBean);

							break;

						case CREATE_ASSOCIATION:
							
							Log.i(LOG_TAG, "inside receiveResult CREATE ASSOCIATION");
							if (payload.getCreateAssociationBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateAssociationBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getCreateAssociationBeanResult() is null");
								return;
							}
							final CtxAssociationBean associationBean = payload.getCreateAssociationBeanResult();
							Log.d(LOG_TAG, "association.getId(): " + associationBean.getId().getString());

							//Caching created attribute
							cache.put(associationBean.getId().getString(), associationBean);
							Log.d(LOG_TAG, "Association cached - " + associationBean);

							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) associationBean);

							break;

						case LOOKUP:
							
							Log.i(LOG_TAG, "inside receiveResult LOOKUP");
							if (payload.getCtxBrokerLookupBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCtxBrokerLookupBeanResult() is null");
								return;
							}
							final List<CtxIdentifierBean> ctxIdsBeanList = payload.getCtxBrokerLookupBeanResult();

							if (ctxIdsBeanList != null) 
								intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, ctxIdsBeanList.toArray(new CtxIdentifierBean[ctxIdsBeanList.size()]));
							else
								intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, new CtxIdentifierBean[0]);
							
							break;

						case REMOVE:
							
							Log.i(LOG_TAG, "inside receiveResult REMOVE");
							if (payload.getRemoveBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRemoveBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getRemoveBeanResult() is null");
								return;
							}
							final CtxModelObjectBean removedModelObjectBean = payload.getRemoveBeanResult();

							//remove from cache
							if (cache.keySet().contains(removedModelObjectBean.getId().getString())) {
								cache.remove(removedModelObjectBean.getId().getString());
							}

							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) removedModelObjectBean);
							
							break;

						case RETRIEVE:
							
							Log.i(LOG_TAG, "inside receiveResult RETRIEVE");
							if (payload.getRetrieveBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getRetrieveBeanResult() is null");
								return;
							}
							final CtxModelObjectBean retrievedObjectBean = payload.getRetrieveBeanResult();
							
							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) retrievedObjectBean);
							
							break;

						case RETRIEVE_INDIVIDUAL_ENTITY_ID:
							
							Log.i(LOG_TAG, "inside receiveResult RETRIEVE_INDIVIDUAL_ENTITY_ID");
							if (payload.getRetrieveIndividualEntityIdBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveIndividualEntityIdBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getRetrieveIndividualEntityIdBeanResult() is null");
								return;
							}
							final CtxEntityIdentifierBean retrievedIndEntIdObjectBean = payload.getRetrieveIndividualEntityIdBeanResult();

							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) retrievedIndEntIdObjectBean);
							
							break;
						
						case RETRIEVE_COMMUNITY_ENTITY_ID:
							
							Log.i(LOG_TAG, "inside receiveResult RETRIEVE_COMMUNITY_ENTITY_ID");
							if (payload.getRetrieveCommunityEntityIdBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveCommunityEntityIdBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getRetrieveCommunityEntityIdBeanResult() is null");
								return;
							}
							final CtxEntityIdentifierBean retrievedCommEntIdObjectBean = payload.getRetrieveCommunityEntityIdBeanResult();

							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) retrievedCommEntIdObjectBean);
							
							break;
						
						case UPDATE:
							
							Log.i(LOG_TAG, "inside receiveResult UPDATE");
							if (payload.getUpdateBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getUpdateBeanResult() is null");
								ContextBrokerBase.this.broadcastException(client, this.returnIntent, 
										"Could not handle result bean: CtxBrokerResponseBean.getUpdateBeanResult() is null");
								return;
							}
							final CtxModelObjectBean updateBean = payload.getUpdateBeanResult();

							//caching updated object
							if (cache.keySet().contains(updateBean.getId().getString())) {
								cache.put(updateBean.getId().getString(), updateBean);
							}

							intent.putExtra(IInternalCtxClient.INTENT_RETURN_VALUE_KEY, (Parcelable) updateBean);
							
							break;
							
						default:
							Log.e(LOG_TAG, "Unsupported method in ContextBroker response bean: " + payload.getMethod());
						}
					}catch (Exception e) {

						Log.e(LOG_TAG, "Could not handle result bean " + msgBean + ": "
								+ e.getLocalizedMessage(), e);
					}
				} else {
					
					Log.e(LOG_TAG, "Received unexpected response bean in result: "
							+ ((msgBean != null) ? msgBean.getClass() : "null"));
					return;
				}
				
//				if (restrictBroadcast) {
//					intent.setPackage(client);
//				}
				intent.addFlags(Intent.FLAG_DEBUG_LOG_RESOLUTION);
				ContextBrokerBase.this.applicationContext.sendBroadcast(intent);
				Log.d(LOG_TAG, "SendBroadcast intent: " + intent);
			}
		}

		@Override
		public void receiveError(Stanza stanza, XMPPError error) {

			Log.d(LOG_TAG, "CtxClient Callback receiveError: stanza=" + stanza
					+ ", error=" + error + ", ApplicationError=" + error.getApplicationError());
		}

		@Override
		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {

			Log.d(LOG_TAG, "CtxClient Callback receiveInfo: stanza=" + stanza
					+ ", node=" + node + ", info=" +info);
		}

		@Override
		public void receiveItems(Stanza stanza, String node, List<String> items) {

			Log.d(LOG_TAG, "CtxClient Callback receiveItems: stanza=" + stanza
					+ ", node=" + node + ", items=" + items);
		}

		@Override
		public void receiveMessage(Stanza stanza, Object payload) {

			Log.d(LOG_TAG, "CtxClient Callback receiveMessage: stanza=" + stanza
					+ ", payload=" + payload);
		}
	}

}
