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

package org.societies.context.broker.impl.comm;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.INetworkNode;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerCreateAssociationBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerCreateAttributeBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerCreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerLookupBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRemoveBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRetrieveBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerUpdateAttributeBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerUpdateBean;
import org.societies.api.schema.context.model.CtxAssociationIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
import org.societies.context.broker.api.CtxBrokerException;
import org.societies.context.broker.impl.comm.ICtxCallback;


@Service
public class CtxBrokerClient implements ICommCallback {

	private static Logger LOG = LoggerFactory.getLogger(CtxBrokerClient.class);

	private final static List<String> NAMESPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");

	private ICommManager commManager;

	private IIdentityManager idMgr;

	private CtxBrokerCommCallback ctxBrokerCommCallback = new CtxBrokerCommCallback();

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public CtxBrokerClient(){
		LOG.info(this.getClass() + " inside ctxBrokerClient class");
	}

	/**
	 * The Ctx Broker Client service reference.
	 *
	 * @see {@link #setCtxBrokerClient()}
	 */
	@Autowired
	CtxBrokerClient(ICommManager commManager) throws CommunicationException{
		this.commManager = commManager;
		this.commManager.register(this);
		idMgr = this.commManager.getIdManager();

	}

	//createEntity(final Requestor requestor,final IIdentity targetCss, final String type)
	public void createRemoteEntity(Requestor requestor,IIdentity targetCss, String type, ICtxCallback callback) throws CtxException{
		
		INetworkNode cssNodeId = this.commManager.getIdManager().getThisNetworkNode();
		final String cssOwnerStr = cssNodeId.getBareJid();
			
		IIdentity toIdentity;
		
		try {
			//to be removed
			toIdentity = this.commManager.getIdManager().fromJid(cssOwnerStr);
			LOG.error(" toIdentity " + toIdentity);
			
			//toIdentity = targetCss;
			//LOG.error("SKATA 2 toIdentity1 " + toIdentity);

			//create the message to be sent
			Stanza stanza = new Stanza(toIdentity);
			LOG.error("SKATA stanza " + stanza.getTo());
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);
			// use the create entity method : createCtxEntity(String type)

			CtxBrokerCreateEntityBean ctxBrokerCreateEntityBean = new CtxBrokerCreateEntityBean();
		
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerCreateEntityBean.setRequestor(requestorBean);

			ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());

			ctxBrokerCreateEntityBean.setType(type);

			cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);
			LOG.info("SKATA 3 before sendIQGet"+ctxBrokerCreateEntityBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
			LOG.info("SKATA 4 before addRequestingClient "+stanza.getId());

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);
			LOG.info("SKATA 5 CreateEntity send  ");
	
		} catch (Exception e) {

			throw new CtxBrokerException("Could not create remote entity: "
					+ e.getLocalizedMessage(), e);
		} 
	}

	public void createRemoteAttribute(Requestor requestor, IIdentity targetCss, CtxEntityIdentifier scope, String type, ICtxCallback callback) throws CtxBrokerException{

		IIdentity toIdentity ;
		// creating the identity of the local CtxBroker that will be contacted
		//INetworkNode cssNodeId = this.commManager.getIdManager().getThisNetworkNode();
		//final String cssOwnerStr = cssNodeId.getBareJid();
		//toIdentity = this.commManager.getIdManager().fromJid(cssOwnerStr);

		try {

			toIdentity = targetCss;
			//toIdentity = this.commManager.getIdManager().fromJid("john.societies.local");

			//create the message to be sent
			Stanza stanza = new Stanza(toIdentity);

			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();

			cbPacket.setMethod(BrokerMethodBean.CREATE_ATTRIBUTE);

			CtxBrokerCreateAttributeBean ctxBrokerCreateAttributeBean = new CtxBrokerCreateAttributeBean();
			// createAttribute(final Requestor requestor,final CtxEntityIdentifier scope, final String type)
			// add the signatures of the method (3 params)

			//1. set requestorBean
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerCreateAttributeBean.setRequestor(requestorBean);

			//2. set scope
			CtxEntityIdentifierBean ctxEntIdBean = new CtxEntityIdentifierBean();
			ctxEntIdBean.setString(scope.toString());
			ctxBrokerCreateAttributeBean.setScope(ctxEntIdBean);

			//3. set type
			ctxBrokerCreateAttributeBean.setType(type);


			cbPacket.setCreateAttribute(ctxBrokerCreateAttributeBean);
			LOG.info("1 ctxBrokerCreateAttributeBean ready "+ctxBrokerCreateAttributeBean.toString());

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
			LOG.info("2 before sendIQGet");

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not create remote attribute: "
					+ e.getLocalizedMessage(), e);
		} 

	}

	public void lookupRemote(Requestor requestor, IIdentity targetCss, CtxModelType modelType, String type, ICtxCallback callback){

		// creating the identity of the CtxBroker that will be contacted
		IIdentity toIdentity = null;

		try {
			//toIdentity = this.commManager.getIdManager().fromJid("john.societies.local");
			toIdentity = targetCss;

			//create the message to be sent
			Stanza stanza = new Stanza(toIdentity);

			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();

			cbPacket.setMethod(BrokerMethodBean.LOOKUP);
			CtxBrokerLookupBean ctxBrokerLookupBean = new CtxBrokerLookupBean();
			//lookup(final Requestor requestor,final IIdentity target, final CtxModelType modelType,final String type)
			// four params 

			//1.requestor
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerLookupBean.setRequestor(requestorBean);

			//2. target id
			ctxBrokerLookupBean.setTargetCss(toIdentity.getBareJid());
			LOG.info("2 CtxBrokerLookupBean toIdentity.getBareJid() "+toIdentity.getBareJid());

			//3. model type
			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			CtxModelTypeBean modelTypeBeanValue = ctxBeanTranslator.CtxModelTypeBeanFromCtxModelType(modelType);
			ctxBrokerLookupBean.setModelType(modelTypeBeanValue);

			// 4 . type
			ctxBrokerLookupBean.setType(type);
			LOG.info("1 CtxBrokerLookupBean type "+type);

			LOG.info("CtxBrokerLookupBean ready "+cbPacket.getLookup());
			cbPacket.setLookup(ctxBrokerLookupBean);

			LOG.info("3 CtxBrokerLookupBean before sendIQGet");

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
			LOG.info("4 CtxBrokerLookupBean before sendIQGet stanza.getId() "+stanza.getId());

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);
			LOG.info("5  IQGet send");
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	public void retrieveRemote(Requestor requestor, CtxIdentifier identifier, ICtxCallback callback){

		// creating the identity of the CtxBroker that will be contacted
		IIdentity toIdentity = null;
		//comment after testing
		try {
			//real code
			//toIdentity = this.commManager.getIdManager().fromJid(identifier.getOwnerId());

			//testing code
			toIdentity = this.commManager.getIdManager().fromJid("john.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}

		try {		
			//create the message to be sent
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE);

			// use the method : retrieve
			CtxBrokerRetrieveBean ctxBrokerRetrieveBean = new CtxBrokerRetrieveBean();

			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			// add the signatures of the method
			CtxIdentifierBean ctxIdBean = ctxBeanTranslator.fromCtxIdentifier(identifier);
			ctxBrokerRetrieveBean.setId(ctxIdBean);

			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerRetrieveBean.setRequestor(requestorBean);

			cbPacket.setRetrieve(ctxBrokerRetrieveBean);
			LOG.info("ctxBrokerRetrieveBean ready "+cbPacket.getRetrieve());

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);
		} catch (CommunicationException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	public void updateRemote(Requestor requestor, CtxModelObject object, ICtxCallback callback){
		
		IIdentity toIdentity = null;
		try {
			//real code
			//toIdentity = this.commManager.getIdManager().fromJid(object.getOwnerId());
			//testing code
			toIdentity = this.commManager.getIdManager().fromJid("john.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}

		try {
			//create the message to be sent
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.UPDATE);
			
			CtxBrokerUpdateBean ctxBrokerUpdateBean = new CtxBrokerUpdateBean();
			
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerUpdateBean.setRequestor(requestorBean);

			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			CtxModelObjectBean objectBean = ctxBeanTranslator.fromCtxModelObject(object);
			
			LOG.info("updateRemote 1 ctxBrokerUpdateBean  "+objectBean.getId());
			ctxBrokerUpdateBean.setCtxModelOject(objectBean);

			LOG.info("updateRemote 2 ctxBrokerUpdateBean  "+objectBean.getId());
			cbPacket.setUpdate(ctxBrokerUpdateBean);
			
			LOG.info("updateRemote 3 ctxBrokerUpdateBean  "+cbPacket.getUpdate());
			
			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}


	public void createRemoteAssociation(Requestor requestor, String type, ICtxCallback callback){

		final CtxAssociation association = null;

		// creating the identity of the CtxBroker that will be contacted
		IIdentity toIdentity = null;
		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}

		//create the message to be sent
		Stanza stanza = new Stanza(toIdentity);
		CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
		// use the method : createAssociation(String type)
		CtxBrokerCreateAssociationBean ctxBrokerCreateAssociationBean = new CtxBrokerCreateAssociationBean();
		// add the signatures of the method
		//ctxBrokerCreateAssociationBean.setRequester("FOO");
		RequestorBean requestorBean = createRequestorBean(requestor);
		ctxBrokerCreateAssociationBean.setRequestor(requestorBean);


		ctxBrokerCreateAssociationBean.setType(type);
		cbPacket.setCreateAssociation(ctxBrokerCreateAssociationBean);

		//CtxBrokerCommCallback commCallback = new CtxBrokerCommCallback(stanza.getId(), callback);
		this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
		//send the message
		try {
			this.commManager.sendIQGet(stanza, ctxBrokerCreateAssociationBean, this);
			//this.commManager.getIdManager().getThisNetworkNode()sendMessage(stanza, ctxBrokerCreateAssociationBean);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return new AsyncResult<CtxAssociation>(association);
	}

	public void /*Future<CtxModelObject>*/ removeRemote(Requestor requestor, CtxIdentifier identifier, ICtxCallback callback){
		//remove(Identity requester, CtxIdentifier identifier)
		final CtxModelObject model = null;

		// creating the identity of the CtxBroker that will be contacted
		IIdentity toIdentity = null;
		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}

		//create the message to be sent
		Stanza stanza = new Stanza(toIdentity);
		CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
		// use the method : remove(CtxIdentifier identifier)
		CtxBrokerRemoveBean ctxBrokerRemoveBean = new CtxBrokerRemoveBean();
		// add the signatures of the method
		//ctxBrokerRemoveBean.setRequester("FOO");
		RequestorBean requestorBean = createRequestorBean(requestor);
		ctxBrokerRemoveBean.setRequestor(requestorBean);

		//create the bean
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
		// add the signatures of the method
		CtxIdentifierBean ctxIdBean=ctxBeanTranslator.fromCtxIdentifier(identifier);

		ctxIdBean.setString(identifier.toString());
		ctxBrokerRemoveBean.setId(ctxIdBean);
		cbPacket.setRemove(ctxBrokerRemoveBean);

		//CtxBrokerCommCallback commCallback = new CtxBrokerCommCallback(stanza.getId(), callback);
		this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
		//send the message
		try {
			this.commManager.sendIQGet(stanza, ctxBrokerRemoveBean, this);
			//this.commManager.sendMessage(stanza, ctxBrokerRemoveBean);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return new AsyncResult<CtxModelObject>(model);
	}

	public void /*Future<CtxModelObject>*/ updateRemoteAttribute(Requestor requestor, CtxAttributeIdentifier attributeId, Serializable value, ICtxCallback callback){
		//updateAttribute(CtxAttributeIdentifier attributeId, Serializable value)

		final CtxModelObject model = null;

		// creating the identity of the CtxBroker that will be contacted
		IIdentity toIdentity = null;
		try {
			toIdentity = idMgr.fromJid("XCManager.societies.local");
		} catch (InvalidFormatException e1) {
			e1.printStackTrace();
		}

		//create the message to be sent
		Stanza stanza = new Stanza(toIdentity);
		CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
		// use the method : remove(CtxIdentifier identifier)
		CtxBrokerUpdateAttributeBean ctxBrokerUpdateAttributeBean = new CtxBrokerUpdateAttributeBean();
		// add the signatures of the method
		CtxAttributeIdentifierBean ctxAttrIdBean = new CtxAttributeIdentifierBean();
		ctxAttrIdBean.setString(attributeId.toString());
		ctxBrokerUpdateAttributeBean.setAttrId(ctxAttrIdBean);

		//ctxBrokerUpdateAttributeBean.setRequester("FOO");
		RequestorBean requestorBean = createRequestorBean(requestor);
		ctxBrokerUpdateAttributeBean.setRequestor(requestorBean);

		ctxBrokerUpdateAttributeBean.setValue((byte[]) value);
		cbPacket.setUpdateAttribute(ctxBrokerUpdateAttributeBean);

		//CtxBrokerCommCallback commCallback = new CtxBrokerCommCallback(stanza.getId(), callback);
		this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);
		
		//send the message
		try {
			this.commManager.sendIQGet(stanza, ctxBrokerUpdateAttributeBean, this);
			//this.commManager.sendMessage(stanza, ctxBrokerUpdateAttributeBean);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//return new AsyncResult<CtxModelObject>(model);
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

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveResult(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

}