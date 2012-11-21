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

import org.jivesoftware.smack.packet.IQ;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAssociationIdentifier;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxAttributeIdentifier;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.android.api.context.ICtxClient;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.android.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.comm.xmpp.client.impl.ClientCommunicationMgr;
//import org.societies.context.broker.api.CtxBrokerException;

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
public class CtxClientBase implements ICtxClient {

	// TODO Remove and instantiate privateId properly so that privateId.toString() can be used instead
	private final String privateIdtoString = "myFooIIdentity@societies.local";

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
    
    private static final String LOG_TAG = CtxClientBase.class.getName();
//    private IBinder binder = null;
    
    public CtxClientBase(Context androidContext) {
    	Log.d(LOG_TAG, "CtxClientBase created");
    	
    	this.androidContext = androidContext;
    	
		try {
			//INSTANTIATE COMMS MANAGER
			this.commMgr = new ClientCommunicationMgr(androidContext);
		} catch (Exception e) {
			Log.e(LOG_TAG, e.getMessage());
        }    
	}

	public ACtxEntity createEntity(String client, Requestor requestor,
			IIdentity targetCss, String type) throws CtxException {

		Log.d(LOG_TAG, "CreateEntity called by client: " + client);
		
		IIdentity toIdentity;
		toIdentity = targetCss;
			
		CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
		cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);

		CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
		RequestorBean requestorBean = createRequestorBean(requestor);
		ctxBrokerCreateEntityBean.setRequestor(requestorBean);
		ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
		ctxBrokerCreateEntityBean.setType(type);
	
		cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);

		//COMMS STUFF
		ICommCallback ctxClientCallback = new CtxClientCallback(client, CREATE_ENTITY); 
		Stanza stanza = new Stanza(toIdentity);
		
		try {
			commMgr.register(ELEMENT_NAMES, ctxClientCallback);
			commMgr.sendIQ(stanza, IQ.Type.GET, cbPacket, ctxClientCallback);
			Log.d(LOG_TAG, "Sending stanza");
		} catch (Exception e) {

			Log.e(LOG_TAG, "ERROR sending message: " + e.getMessage());
			//throw new CtxBrokerException("Could not create remote entity: "
			//		+ e.getLocalizedMessage(), e);
		} 
		return null;
	}

	public ACtxAttribute createAttribute(String client, Requestor requestor,
			ACtxEntityIdentifier scope, String type) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxAssociation createAssociation(String client,
			Requestor requestor, IIdentity targetCss, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public List<ACtxIdentifier> lookup(String client, Requestor requestor,
			IIdentity target, CtxModelType modelType, String type)
			throws CtxException {
		// TODO Auto-generated method stub
		return null;
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
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject remove(String client, Requestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}

	public ACtxModelObject retrieve(String client, Requestor requestor,
			ACtxIdentifier identifier) throws CtxException {
		// TODO Auto-generated method stub
		return null;
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
			ACtxModelObject object) throws CtxException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private RequestorBean createRequestorBean(Requestor requestor){
		if (requestor instanceof RequestorCis){
			RequestorCisBean cisRequestorBean = new RequestorCisBean();
			cisRequestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			cisRequestorBean.setCisRequestorId(((RequestorCis) requestor).getCisRequestorId().getBareJid());
			return cisRequestorBean;
		}else if (requestor instanceof RequestorService){
			RequestorServiceBean serviceRequestorBean = new RequestorServiceBean();
			serviceRequestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			serviceRequestorBean.setRequestorServiceId(((RequestorService) requestor).getRequestorServiceId());
			return serviceRequestorBean;
		}else{
			RequestorBean requestorBean = new RequestorBean();
			requestorBean.setRequestorId(requestor.getRequestorId().getBareJid());
			return requestorBean;
		}
	}

	/**
	 * Callback required for Android Comms Manager
	 */
	private class CtxClientCallback implements ICommCallback {

		private String client;
		private String returnIntent;
		
		/**Constructor sets the calling client and Intent to be returned
		 * @param client
		 * @param returnIntent
		 */
		public CtxClientCallback(String client, String returnIntent) {
			this.client = client;
			this.returnIntent = returnIntent;
		}
		
		public List<String> getXMLNamespaces() {

			return NAME_SPACES;
		}

		public List<String> getJavaPackages() {

			return PACKAGES;
		}

		public void receiveResult(Stanza stanza, Object msgBean) {

			Log.d(LOG_TAG, "CtxClient Callback receiveResult");
			
			if (client != null) {
				Intent intent = new Intent(returnIntent);
				
				Log.d(LOG_TAG, "Return Stanza: " + stanza.toString());
				if (msgBean==null)
					Log.d(LOG_TAG, "msgBean is null");
				
				if (msgBean instanceof CtxBrokerResponseBean) {
					
					Log.d(LOG_TAG, "receiveResult CtxBrokerRespose");
					
					final CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
					final BrokerMethodBean method = payload.getMethod();
					try {
						switch (method) {
						
						case CREATE_ENTITY:
							
							Log.i(LOG_TAG, "inside receiveResult CREATE ENTITY");
							if (payload.getCreateEntityBeanResult() == null) {
								Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null");
								return;
							}
							final CtxEntityBean entityBean = payload.getCreateEntityBeanResult();
							final ACtxEntity entity = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(entityBean);
							//NOTIFY calling client
							intent.putExtra(ICtxClient.CREATE_ENTITY,entity);
						}
					}catch (Exception e) {

						Log.e(LOG_TAG, "Could not handle result bean " + msgBean + ": "
								+ e.getLocalizedMessage(), e);
					}
				}
				
				intent.setPackage(client);
				CtxClientBase.this.androidContext.sendBroadcast(intent);
				CtxClientBase.this.commMgr.unregister(ELEMENT_NAMES, this);
			}
		}

		public void receiveError(Stanza stanza, XMPPError error) {

			Log.d(LOG_TAG, "CtxClient Callback receiveError: " + error.getMessage());
		}

		public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {

			Log.d(LOG_TAG, "CtxClient Callback receiveInfo");
		}

		public void receiveItems(Stanza stanza, String node, List<String> items) {

			Log.d(LOG_TAG, "CtxClient Callback receiveItems");
		}

		public void receiveMessage(Stanza stanza, Object payload) {

			Log.d(LOG_TAG, "CtxClient Callback receiveMessage");
		}
	}

}
