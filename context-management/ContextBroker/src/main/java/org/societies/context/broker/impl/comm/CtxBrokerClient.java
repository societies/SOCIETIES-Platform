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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CreateAssociationBean;
import org.societies.api.schema.context.contextmanagement.CreateAttributeBean;
import org.societies.api.schema.context.contextmanagement.CreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.LookupBean;
import org.societies.api.schema.context.contextmanagement.LookupByScopeBean;
import org.societies.api.schema.context.contextmanagement.RemoveBean;
import org.societies.api.schema.context.contextmanagement.RetrieveAllBean;
import org.societies.api.schema.context.contextmanagement.RetrieveBean;
import org.societies.api.schema.context.contextmanagement.RetrieveCommunityEntityIdBean;
import org.societies.api.schema.context.contextmanagement.RetrieveFutureBean;
import org.societies.api.schema.context.contextmanagement.RetrieveIndividualEntityIdBean;
import org.societies.api.schema.context.contextmanagement.UpdateAttributeBean;
import org.societies.api.schema.context.contextmanagement.UpdateBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.context.broker.api.CtxBrokerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

	private CtxBrokerCommCallback ctxBrokerCommCallback = new CtxBrokerCommCallback();

	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	/**
	 * The Ctx Broker Client service reference.
	 *
	 * @see {@link #setCtxBrokerClient()}
	 */
	@Autowired
	CtxBrokerClient(ICommManager commManager) throws CommunicationException {

		LOG.info(this.getClass() + " instanstiated");

		this.commManager = commManager;
		this.commManager.register(this);
		this.commManager.register(this.ctxBrokerCommCallback);
	}

	public void createEntity(Requestor requestor, IIdentity targetCss, 
			String type, ICtxCallback callback) throws CtxException {

		IIdentity toIdentity;
		try {
			toIdentity = targetCss;
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ENTITY);

			CreateEntityBean ctxBrokerCreateEntityBean = new CreateEntityBean();
			ctxBrokerCreateEntityBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			ctxBrokerCreateEntityBean.setTargetCss(toIdentity.getBareJid());
			ctxBrokerCreateEntityBean.setType(type);

			cbPacket.setCreateEntity(ctxBrokerCreateEntityBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not create remote entity: "
					+ e.getLocalizedMessage(), e);
		} 
	}

	public void createAttribute(Requestor requestor, IIdentity targetCss, CtxEntityIdentifier scope, String type, ICtxCallback callback) throws CtxBrokerException{

		IIdentity toIdentity ;
		try {
			toIdentity = targetCss;
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ATTRIBUTE);

			CreateAttributeBean ctxBrokerCreateAttributeBean = new CreateAttributeBean();
			// 1. set requestorBean
			ctxBrokerCreateAttributeBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// 2. set scope
			CtxEntityIdentifierBean ctxEntIdBean = new CtxEntityIdentifierBean();
			ctxEntIdBean.setString(scope.toString());
			ctxBrokerCreateAttributeBean.setScope(ctxEntIdBean);
			// 3. set type
			ctxBrokerCreateAttributeBean.setType(type);

			cbPacket.setCreateAttribute(ctxBrokerCreateAttributeBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not create remote attribute: "
					+ e.getLocalizedMessage(), e);
		} 

	}

	public void createAssociation(Requestor requestor, IIdentity targetCss, String type, ICtxCallback callback) throws CtxBrokerException {

		IIdentity toIdentity = targetCss;
		try {
			Stanza stanza = new Stanza(toIdentity);

			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.CREATE_ASSOCIATION);

			CreateAssociationBean ctxBrokerCreateAssociationBean = new CreateAssociationBean();
			ctxBrokerCreateAssociationBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			ctxBrokerCreateAssociationBean.setType(type);
			ctxBrokerCreateAssociationBean.setTargetCss(toIdentity.getBareJid());

			cbPacket.setCreateAssociation(ctxBrokerCreateAssociationBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not create remote association: "
					+ e.getLocalizedMessage(), e);

		}
	}

	public void retrieve(Requestor requestor, CtxIdentifier identifier,
			ICtxCallback callback) throws CtxBrokerException  {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Remote retrieve: requestor=" + requestor + ", identifier=" + identifier);
		
		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(identifier.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE);

			// use the method : retrieve
			RetrieveBean ctxBrokerRetrieveBean = new RetrieveBean();
			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			
			// add the method params
			ctxBrokerRetrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			
			CtxIdentifierBean ctxIdBean = ctxBeanTranslator.fromCtxIdentifier(identifier);
			ctxBrokerRetrieveBean.setId(ctxIdBean);

			cbPacket.setRetrieve(ctxBrokerRetrieveBean);
			
			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not retrieve remote ctx model object "
					+ identifier + ": " + e.getLocalizedMessage(), e);
		}
	}
	
	public void retrieve(final Requestor requestor, final IIdentity target, 
			final List<CtxIdentifier> ctxIdList, final ICtxCallback callback)
					throws CtxBrokerException  {
		
		LOG.debug("Remote retrieve: requestor={}, target={}, ctxIdList={}", 
				new Object[] { requestor, target, ctxIdList });
		try {
			final Stanza stanza = new Stanza(target);
			final CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE_ALL);
			final RetrieveAllBean ctxBrokerRetrieveBean = new RetrieveAllBean();
			// Method params
			// 1. requestor 
			ctxBrokerRetrieveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// 2. ctxIdList
			final List<CtxIdentifierBean> ctxIdBeanList = 
					new ArrayList<CtxIdentifierBean>(ctxIdList.size());
			for (final CtxIdentifier ctxId : ctxIdList) {
				ctxIdBeanList.add(CtxModelBeanTranslator.getInstance()
						.fromCtxIdentifier(ctxId));
			}
			ctxBrokerRetrieveBean.setIds(ctxIdBeanList);

			cbPacket.setRetrieveAll(ctxBrokerRetrieveBean);	

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not retrieve remote context model objects '"
					+ ctxIdList + "': " + e.getLocalizedMessage(), e);
		}
	}

	public void retrieveIndividualEntityId(Requestor requestor, 
			IIdentity targetCss, ICtxCallback callback) throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = targetCss;
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE_INDIVIDUAL_ENTITY_ID);

			RetrieveIndividualEntityIdBean retrieveIndEntBean = new RetrieveIndividualEntityIdBean();
			//1.requestor
			retrieveIndEntBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			//2. target id
			retrieveIndEntBean.setTargetCss(toIdentity.getJid());

			cbPacket.setRetrieveIndividualEntityId(retrieveIndEntBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (CommunicationException e) {

			throw new CtxBrokerException("Could not retrieve remote individual ctx entity : "
					+ e.getLocalizedMessage(), e);
		}

	}

	public void retrieveCommunityEntityId(Requestor requestor, IIdentity target,
			ICtxCallback callback) throws CtxBrokerException {

		try {
			final Stanza stanza = new Stanza(target);
			final CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE_COMMUNITY_ENTITY_ID);

			final RetrieveCommunityEntityIdBean methodBean = new RetrieveCommunityEntityIdBean();

			// 1. requestor
			methodBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// 2. target id
			methodBean.setTarget(target.getJid());
			cbPacket.setRetrieveCommunityEntityId(methodBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not retrieve remote community ctx entity for '"
					+ target + "': " + e.getLocalizedMessage(), e);
		}

	}

	public void update(Requestor requestor, CtxModelObject object, 
			ICtxCallback callback) throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(object.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			// create request bean
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			// method type
			cbPacket.setMethod(BrokerMethodBean.UPDATE);
			// method bean
			UpdateBean ctxBrokerUpdateBean = new UpdateBean();
			ctxBrokerUpdateBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			CtxModelObjectBean objectBean = ctxBeanTranslator.fromCtxModelObject(object);
			ctxBrokerUpdateBean.setCtxModelOject(objectBean);
			cbPacket.setUpdate(ctxBrokerUpdateBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not update context model object "
					+ object.getId() + ": " + e.getLocalizedMessage(), e);
		}
	}

	public void updateAttribute(Requestor requestor, 
			CtxAttributeIdentifier attributeId, Serializable value, 
			ICtxCallback callback) throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(attributeId.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			// create request bean
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			// method type
			// TODO UPDATE_ATTRIBUTE ??
			cbPacket.setMethod(BrokerMethodBean.UPDATE);
			// method bean
			UpdateAttributeBean ctxBrokerUpdateAttributeBean = new UpdateAttributeBean();
			// TODO
			/*CtxAttributeIdentifierBean ctxAttrIdBean = new CtxAttributeIdentifierBean();
			ctxAttrIdBean.setString(attributeId.toString());
			ctxBrokerUpdateAttributeBean.setAttrId(ctxAttrIdBean);

			//ctxBrokerUpdateAttributeBean.setRequester("FOO");
			RequestorBean requestorBean = createRequestorBean(requestor);
			ctxBrokerUpdateAttributeBean.setRequestor(requestorBean);

			ctxBrokerUpdateAttributeBean.setValue((byte[]) value);
			 */
			cbPacket.setUpdateAttribute(ctxBrokerUpdateAttributeBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, ctxBrokerUpdateAttributeBean, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not update context attribute "
					+ attributeId + ": " + e.getLocalizedMessage(), e);
		}

	}

	public void remove(Requestor requestor, CtxIdentifier identifier, 
			ICtxCallback callback) throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(identifier.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			// create request bean
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			// method type
			cbPacket.setMethod(BrokerMethodBean.REMOVE);
			// method bean
			RemoveBean ctxBrokerRemoveBean = new RemoveBean();
			ctxBrokerRemoveBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			ctxBrokerRemoveBean.setId(CtxModelBeanTranslator.getInstance().fromCtxIdentifier(identifier));
			cbPacket.setRemove(ctxBrokerRemoveBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not remove context model object "
					+ identifier + ": " + e.getLocalizedMessage(), e);
		}
	}

	public void lookup(Requestor requestor, IIdentity targetCss, 
			CtxModelType modelType, String type, ICtxCallback callback)
					throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = targetCss;
			Stanza stanza = new Stanza(toIdentity);
			// create request bean
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			// method type
			cbPacket.setMethod(BrokerMethodBean.LOOKUP);
			// method bean
			LookupBean ctxBrokerLookupBean = new LookupBean();

			// 1. requestor
			ctxBrokerLookupBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// 2. target id
			ctxBrokerLookupBean.setTargetCss(toIdentity.getBareJid());
			// 3. model type
			if (modelType != null) {
				ctxBrokerLookupBean.setModelType(
						CtxModelBeanTranslator.getInstance().ctxModelTypeBeanFromCtxModelType(modelType));
			}
			// 4 . type
			ctxBrokerLookupBean.setType(type);
			cbPacket.setLookup(ctxBrokerLookupBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not perform remote lookup: "
					+ e.getLocalizedMessage(), e);
		} 
	}
	
	public void lookup(Requestor requestor, CtxEntityIdentifier scope, 
			CtxModelType modelType, String type, ICtxCallback callback)
					throws CtxBrokerException {

		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(scope.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			// create request bean
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
			// method type
			cbPacket.setMethod(BrokerMethodBean.LOOKUP_BY_SCOPE);
			// method bean
			final LookupByScopeBean ctxBrokerLookupBean = new LookupByScopeBean();

			// 1. requestor
			ctxBrokerLookupBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			// 2. scope
			ctxBrokerLookupBean.setScope((CtxEntityIdentifierBean) 
					CtxModelBeanTranslator.getInstance().fromCtxIdentifier(scope));
			// 3. model type
			ctxBrokerLookupBean.setModelType(CtxModelBeanTranslator.getInstance()
					.ctxModelTypeBeanFromCtxModelType(modelType));
			// 4. type
			ctxBrokerLookupBean.setType(type);
			cbPacket.setLookupByScope(ctxBrokerLookupBean);

			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not perform remote lookup: "
					+ e.getLocalizedMessage(), e);
		} 
	}

	public void retrieveFuture(Requestor requestor, CtxAttributeIdentifier attrID ,Date date,
			ICtxCallback callback) throws CtxBrokerException  {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Remote future retrieve: requestor=" + requestor + ", identifier=" + attrID);
		
		IIdentity toIdentity = null;
		try {
			toIdentity = this.commManager.getIdManager().fromJid(attrID.getOwnerId());
			Stanza stanza = new Stanza(toIdentity);
			CtxBrokerRequestBean cbPacket = new CtxBrokerRequestBean();
		
			cbPacket.setMethod(BrokerMethodBean.RETRIEVE_FUTURE);

			// use the method : retrieve
			RetrieveFutureBean ctxBrokerRetrieveFutureBean = new RetrieveFutureBean();
			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			
			// add the method params
			ctxBrokerRetrieveFutureBean.setRequestor(RequestorUtils.toRequestorBean(requestor));
			
			CtxAttributeIdentifierBean ctxIdBean = (CtxAttributeIdentifierBean) ctxBeanTranslator.fromCtxIdentifier(attrID);
			
			ctxBrokerRetrieveFutureBean.setAttrId(ctxIdBean);

			GregorianCalendar c = new GregorianCalendar();
			c.setTime(date);
			XMLGregorianCalendar date2 = DatatypeFactory.newInstance().newXMLGregorianCalendar(c);
			ctxBrokerRetrieveFutureBean.setDate(date2);
						
			cbPacket.setRetrieveFuture(ctxBrokerRetrieveFutureBean);
			
			this.ctxBrokerCommCallback.addRequestingClient(stanza.getId(), callback);

			this.commManager.sendIQGet(stanza, cbPacket, this.ctxBrokerCommCallback);

		} catch (Exception e) {

			throw new CtxBrokerException("Could not retrieve remote ctx model object "
					+ attrID + ": " + e.getLocalizedMessage(), e);
		}
	}
	
	
	
	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		return PACKAGES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received error: stanza=" + stanza + ", error=" + error);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza stanza, String arg1, XMPPInfo info) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received info: stanza=" + stanza + ", info=" + info);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object object) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received message: stanza=" + stanza + ", object=" + object);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza stanza, Object result) {
		
		if (LOG.isDebugEnabled())
			LOG.debug("Received result: stanza=" + stanza + ", result=" + result);
	}
}