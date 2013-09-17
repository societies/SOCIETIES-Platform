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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.XMLGregorianCalendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.context.api.event.ICtxEventMgr;
import org.societies.context.broker.impl.CtxBroker;
import org.societies.context.broker.impl.InternalCtxBroker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CtxBrokerServer implements IFeatureServer{

	private static Logger LOG = LoggerFactory.getLogger(CtxBrokerServer.class);

	private final static List<String> NAMESPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");

	private static final String[] EVENT_TYPES = { EventTypes.CIS_CREATION, EventTypes.CIS_RESTORE };

	private ICommManager commManager;

	private ICISCommunicationMgrFactory commMgrFactory;

	@Autowired(required=true)
	private CtxBroker ctxbroker;

	/** The Context Event Mgmt service reference. TODO remove once pubsub persistence is enabled. */
	private ICtxEventMgr ctxEventMgr;

	@Autowired
	public CtxBrokerServer(ICommManager commManager, 
			ICISCommunicationMgrFactory commMgrFactory, IEventMgr eventMgr, ICtxEventMgr ctxEventMgr)
					throws Exception {

		LOG.info("{} instantiated", this.getClass());
		this.commManager = commManager;
		this.commMgrFactory = commMgrFactory;
		this.ctxEventMgr = ctxEventMgr;

		// Register to CSS Comm Mgr
		LOG.info("Registering CtxBrokerServer to Comms Manager for CSS '{}'",
				this.commManager.getIdManager().getThisNetworkNode());
		this.commManager.register(this);
		// Register to all available CIS Comm Mgrs
		for (final Map.Entry<IIdentity, ICommManager> entry : this.commMgrFactory.getAllCISCommMgrs().entrySet()) {
			LOG.info("Registering CtxBrokerServer to Comms Manager for CIS '{}'",
					entry.getKey());
			entry.getValue().register(this);
			LOG.info("Creating event topics '{}' for CIS '{}'", 
					Arrays.toString(InternalCtxBroker.EVENT_TOPICS), entry.getKey());
			this.ctxEventMgr.createTopics(entry.getKey(), InternalCtxBroker.EVENT_TOPICS);
		}
		// Register for new/restored CISs
		LOG.info("Registering for '{}' events", Arrays.asList(EVENT_TYPES));
		eventMgr.subscribeInternalEvent(new NewCisCommMgrHandler(), EVENT_TYPES, null);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		LOG.debug("getQuery: stanza={}, payload={}", stanza, payload);

		if (!(payload instanceof CtxBrokerRequestBean)) {
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: " + payload.getClass());
		}

		final CtxBrokerRequestBean cbPayload = (CtxBrokerRequestBean) payload;
		if (cbPayload.getMethod() == null) {
			LOG.error("CtxBrokerRequestBean.getMethod() can't be null");
			throw new XMPPError(StanzaError.bad_request, "CtxBrokerRequestBean.getMethod() can't be null");
		}

		final CtxBrokerResponseBean beanResponse = new CtxBrokerResponseBean();
		beanResponse.setMethod(cbPayload.getMethod());

		LOG.debug("getQuery: method={}", cbPayload.getMethod());
		switch (cbPayload.getMethod()) {

		case CREATE_ENTITY:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getCreateEntity().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. target (required)
				final String targetString = cbPayload.getCreateEntity().getTargetCss();
				final IIdentity target = this.commManager.getIdManager().fromJid(targetString);
				// 3. type (required)
				final String type = cbPayload.getCreateEntity().getType();

				// request
				final CtxEntity ctxEntity = ctxbroker.createEntity(requestor,
						target, type).get();

				// response bean
				if (ctxEntity != null) {
					beanResponse.setCreateEntityBeanResult(CtxModelBeanTranslator.getInstance()
							.fromCtxEntity(ctxEntity));
				}
			} catch (Exception e) {
				LOG.error("Failed to create entity: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			} 
			break;

		case CREATE_ATTRIBUTE:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getCreateAttribute().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. scope (required)
				final CtxEntityIdentifier scope = (CtxEntityIdentifier) 
						CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(
								cbPayload.getCreateAttribute().getScope());
				// 3. type
				final String type = cbPayload.getCreateAttribute().getType();

				// request
				final CtxAttribute ctxAttribute = this.ctxbroker.createAttribute(
						requestor, scope, type).get();

				// response
				if (ctxAttribute != null) {
					beanResponse.setCreateAttributeBeanResult(CtxModelBeanTranslator.getInstance()
							.fromCtxAttribute(ctxAttribute));
				}

			} catch (Exception e) {
				LOG.error("Failed to create attribute: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;


		case CREATE_ASSOCIATION:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getCreateAssociation().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. target (required)
				final String targetString = cbPayload.getCreateAssociation().getTargetCss();
				final IIdentity target = this.commManager.getIdManager().fromJid(targetString);
				// 3. type (required)
				final String type = cbPayload.getCreateAssociation().getType();

				// request
				final CtxAssociation ctxAssociation = this.ctxbroker.createAssociation(
						requestor, target, type).get();

				// response
				if (ctxAssociation != null) {
					beanResponse.setCreateAssociationBeanResult(CtxModelBeanTranslator.getInstance()
							.fromCtxAssociation(ctxAssociation));
				}
			} catch (Exception e) {
				LOG.error("Failed to create association: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case RETRIEVE:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getRetrieve().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. ctxId (required)
				final CtxIdentifierBean ctxIdBean = cbPayload.getRetrieve().getId();
				final CtxIdentifier ctxId = CtxModelBeanTranslator.getInstance().
						fromCtxIdentifierBean(ctxIdBean);
				// request
				final CtxModelObject ctxModelObject = this.ctxbroker.retrieve(
						requestor, ctxId).get();
				// response bean
				if (ctxModelObject != null) {
					beanResponse.setRetrieveBeanResult(CtxModelBeanTranslator.getInstance()
							.fromCtxModelObject(ctxModelObject));
				}
			} catch (CtxAccessControlException cace) {
				LOG.error("Failed to retrieve context model object: " + cace.getLocalizedMessage(), cace);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.not_authorized, cace.getLocalizedMessage(), null);
			} catch (Exception e) {
				LOG.error("Failed to retrieve context model object: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;
			
		case RETRIEVE_ALL:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getRetrieveAll().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. ctxIds (required)
				final List<CtxIdentifierBean> ctxIdBeans = cbPayload.getRetrieveAll().getIds();
				final List<CtxIdentifier> ctxIds = new ArrayList<CtxIdentifier>(ctxIdBeans.size());
				for (final CtxIdentifierBean ctxIdBean : ctxIdBeans) {
					ctxIds.add(CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ctxIdBean));
				}
				// request
				final List<CtxModelObject> ctxModelObjects = this.ctxbroker.retrieve(
						requestor, ctxIds).get();
				// response bean
				final List<CtxModelObjectBean> ctxModelObjectBeans =
						new ArrayList<CtxModelObjectBean>(ctxModelObjects.size());
				for (final CtxModelObject ctxModelObject : ctxModelObjects) {
					ctxModelObjectBeans.add(CtxModelBeanTranslator.getInstance()
							.fromCtxModelObject(ctxModelObject));
				}
				beanResponse.setRetrieveAllBeanResult(ctxModelObjectBeans);
				
			} catch (CtxAccessControlException cace) {
				LOG.error("Failed to retrieve context model objects: " + cace.getLocalizedMessage(), cace);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.not_authorized, cace.getLocalizedMessage(), null);
			} catch (Exception e) {
				LOG.error("Failed to retrieve context model objects: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case RETRIEVE_INDIVIDUAL_ENTITY_ID:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getRetrieveIndividualEntityId().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. cssId (required)
				final String cssIdString = cbPayload.getRetrieveIndividualEntityId().getTargetCss();
				final IIdentity cssId = this.commManager.getIdManager().fromJid(cssIdString);

				// request
				final CtxEntityIdentifier indCtxEntId = this.ctxbroker.retrieveIndividualEntityId(
						requestor, cssId).get();

				// response
				if (indCtxEntId != null) {
					beanResponse.setRetrieveIndividualEntityIdBeanResult(
							(CtxEntityIdentifierBean) CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifier(indCtxEntId));
				}
			} catch (Exception e) {
				LOG.error("Failed to retrieve individual entity id: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case RETRIEVE_COMMUNITY_ENTITY_ID:

			try {
				// 1. requestor
				final RequestorBean requestorBean =	cbPayload.getRetrieveCommunityEntityId().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. cisId (required)
				final String cisIdString = cbPayload.getRetrieveCommunityEntityId().getTarget();
				final IIdentity cisId = this.commManager.getIdManager().fromJid(cisIdString);

				// request
				final CtxEntityIdentifier communityCtxEntId = 
						this.ctxbroker.retrieveCommunityEntityId(requestor, cisId).get();

				// response
				if (communityCtxEntId != null) {
					beanResponse.setRetrieveCommunityEntityIdBeanResult(
							(CtxEntityIdentifierBean) CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifier(communityCtxEntId));
				}
			} catch (Exception e) {
				// TODO send application error when supported
				LOG.error("Failed to retrieve community entity id: " + e.getLocalizedMessage(), e);
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case UPDATE:

			try {
				RequestorBean reqBeanUpdate = cbPayload.getUpdate().getRequestor();
				Requestor requestorUpdate = RequestorUtils.toRequestor(
						reqBeanUpdate, this.commManager.getIdManager());
				CtxModelObjectBean ctxModelObjBean = cbPayload.getUpdate().getCtxModelOject();
				CtxModelObject ctxModelObject = 
						CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(ctxModelObjBean);
				CtxModelObject updatedObj = this.ctxbroker.update(requestorUpdate, ctxModelObject).get();
				CtxModelObjectBean ctxObjBean = 
						CtxModelBeanTranslator.getInstance().fromCtxModelObject(updatedObj);
				beanResponse.setUpdateBeanResult(ctxObjBean);

			} catch (CtxAccessControlException cace) {
				LOG.error("Failed to update context model object: " + cace.getLocalizedMessage(), cace);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.not_authorized, cace.getLocalizedMessage(), null);
			} catch (Exception e) {
				LOG.error("Failed to update context model object: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case REMOVE:

			try {
				final RequestorBean removeBeanRequestorBean = 
						cbPayload.getRemove().getRequestor();
				final Requestor removeBeanRequestor = RequestorUtils.toRequestor( 
						removeBeanRequestorBean, this.commManager.getIdManager());
				final CtxIdentifier removeCtxId = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean( 
						cbPayload.getRemove().getId());

				final CtxModelObject removedModelObject = 
						this.ctxbroker.remove(removeBeanRequestor, removeCtxId).get();

				final CtxModelObjectBean removedModelObjectBean = (removedModelObject != null)  
						? CtxModelBeanTranslator.getInstance().fromCtxModelObject(removedModelObject) : null;
						beanResponse.setRemoveBeanResult(removedModelObjectBean);

			} catch (CtxAccessControlException cace) {
				LOG.error("Failed to remove context model object: " + cace.getLocalizedMessage(), cace);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.not_authorized, cace.getLocalizedMessage(), null);
			} catch (Exception e) {
				LOG.error("Failed to remove context model object: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case LOOKUP:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getLookup().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. target (required)
				final String targetString = cbPayload.getLookup().getTargetCss();
				final IIdentity target = this.commManager.getIdManager().fromJid(targetString);
				// 3. modelType (optional)
				final CtxModelType modelType = (cbPayload.getLookup().getModelType() != null)
						? CtxModelBeanTranslator.getInstance().ctxModelTypeFromCtxModelTypeBean(cbPayload.getLookup().getModelType())
								: null;
						// 4. type (required)
						final String type = cbPayload.getLookup().getType();

						// request
						final List<CtxIdentifier> lookupResult = (modelType != null)
								? this.ctxbroker.lookup(requestor, target, modelType, type).get()
										: this.ctxbroker.lookup(requestor, target, type).get();
								LOG.debug("lookupResult={}", lookupResult);

								// response bean
								if (lookupResult.size() > 0) {
									final List<CtxIdentifierBean> lookupResultBean = new ArrayList<CtxIdentifierBean>(); 
									for (final CtxIdentifier identifier : lookupResult) {
										final CtxIdentifierBean ctxIdBean = 
												CtxModelBeanTranslator.getInstance().fromCtxIdentifier(identifier);
										lookupResultBean.add(ctxIdBean);
									}
									beanResponse.setCtxBrokerLookupBeanResult(lookupResultBean);
								}

			} catch (Exception e) {
				LOG.error("Failed to perform lookup: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case LOOKUP_BY_SCOPE:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getLookupByScope().getRequestor();
				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());
				// 2. scope (required)
				final CtxEntityIdentifierBean scopeBean = cbPayload.getLookupByScope().getScope();
				final CtxEntityIdentifier scope = (CtxEntityIdentifier) CtxModelBeanTranslator.getInstance()
						.fromCtxIdentifierBean(scopeBean);
				// 3. modelType (required)
				final CtxModelType modelType = CtxModelBeanTranslator.getInstance()
						.ctxModelTypeFromCtxModelTypeBean(cbPayload.getLookupByScope().getModelType());
				// 4. type (required)
				final String type = cbPayload.getLookupByScope().getType();

				// request
				final List<CtxIdentifier> lookupResult = this.ctxbroker.lookup(
						requestor, scope, modelType, type).get();
				LOG.debug("lookupResult={}", lookupResult);

				// response bean
				if (lookupResult.size() > 0) {
					final List<CtxIdentifierBean> lookupResultBean = new ArrayList<CtxIdentifierBean>(); 
					for (final CtxIdentifier identifier : lookupResult) {
						final CtxIdentifierBean ctxIdBean = 
								CtxModelBeanTranslator.getInstance().fromCtxIdentifier(identifier);
						lookupResultBean.add(ctxIdBean);
					}
					beanResponse.setCtxBrokerLookupBeanResult(lookupResultBean);
				}

			} catch (Exception e) {
				LOG.error("Failed to perform lookup: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case RETRIEVE_FUTURE:

			try {
				// 1. requestor
				final RequestorBean requestorBean = cbPayload.getRetrieveFuture().getRequestor();

				final Requestor requestor = RequestorUtils.toRequestor(
						requestorBean, this.commManager.getIdManager());

				// 2. attrID 
				final CtxIdentifier ctxId = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean( 
						cbPayload.getRetrieveFuture().getAttrId());



				CtxAttributeIdentifier attrID = null;
				Date date = null;

				if(ctxId instanceof CtxAttributeIdentifier ){
					attrID = (CtxAttributeIdentifier) ctxId;
					XMLGregorianCalendar cal = cbPayload.getRetrieveFuture().getDate();
					date = cal.toGregorianCalendar().getTime();
				}

				// request
				final List<CtxAttribute> predictedAttsList = this.ctxbroker.retrieveFuture(requestor, 
						attrID, date ).get();

				List<CtxAttributeBean> predictedAttsListBean = new ArrayList<CtxAttributeBean>();

				// response
				// convert predictedAttsList to bean 
				if (predictedAttsList.size() > 0 ) {

					for(CtxAttribute attr : predictedAttsList ){

						CtxAttributeBean attrBean = CtxModelBeanTranslator.getInstance().fromCtxAttribute(attr);
						predictedAttsListBean.add(attrBean);
					}					
					beanResponse.setRetrieveFutureBeanResult(predictedAttsListBean);
				}

			} catch (Exception e) {
				LOG.error("Failed to retrieve future attribute: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;
		default: 
			throw new XMPPError(StanzaError.feature_not_implemented, 
					"Unsupported remote context method: " + cbPayload.getMethod());
		}

		LOG.debug("getQuery: beanResponse={}", beanResponse);
		return beanResponse;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {

		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {

		return PACKAGES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

	private class NewCisCommMgrHandler extends EventListener {

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
		 */
		@Override
		public void handleExternalEvent(CSSEvent event) {

			if (LOG.isWarnEnabled())
				LOG.warn("Received unexpected external '" + event.geteventType() + "' event: " + event);
		}

		/*
		 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
		 */
		@Override
		public void handleInternalEvent(InternalEvent event) {

			if (LOG.isDebugEnabled())
				LOG.debug("Received internal event: " + this.eventToString(event));

			if (EventTypes.CIS_CREATION.equals(event.geteventType())
					|| EventTypes.CIS_RESTORE.equals(event.geteventType())) {

				if (!(event.geteventInfo() instanceof Community)) {

					LOG.error("Could not handle internal " + event.geteventType() + " event: " 
							+ "Expected event info of type " + Community.class.getName()
							+ " but was " + event.geteventInfo().getClass());
					return;
				}

				final String cisIdStr = ((Community) event.geteventInfo()).getCommunityJid();
				try {
					final IIdentity cisId = commManager.getIdManager().fromJid(cisIdStr);
					final ICommManager cisCommMgr = commMgrFactory.getAllCISCommMgrs().get(cisId);
					if (cisCommMgr == null) {
						LOG.error("Could not register CtxBrokerServer to Comms Manager for CIS '" 
								+ cisId + "': Comms Manager not found" );
						return;
					}
					if (LOG.isInfoEnabled())
						LOG.info("Registering CtxBrokerServer to Comms Manager for CIS '"
								+ cisId + "'");
					cisCommMgr.register(CtxBrokerServer.this);
					if (EventTypes.CIS_RESTORE.equals(event.geteventType())) {
						if (LOG.isInfoEnabled())
							LOG.info("Creating event topics '" + Arrays.toString(InternalCtxBroker.EVENT_TOPICS) 
									+ "' for CIS " + cisId);
						ctxEventMgr.createTopics(cisId, InternalCtxBroker.EVENT_TOPICS);
					}

				} catch (Exception e) {
					LOG.error("Could not register CtxBrokerServer to Comms Manager for CIS '" 
							+ cisIdStr + "': " + e.getLocalizedMessage(), e);
				}

			} else {

				if (LOG.isWarnEnabled())
					LOG.warn("Received unexpected event of type '" + event.geteventType() + "'");
			}
		}

		private String eventToString(final InternalEvent event) {

			final StringBuffer sb = new StringBuffer();
			sb.append("[");
			sb.append("name=");
			sb.append(event.geteventName());
			sb.append(",");
			sb.append("type=");
			sb.append(event.geteventType());
			sb.append(",");
			sb.append("source=");
			sb.append(event.geteventSource());
			sb.append(",");
			sb.append("info=");
			sb.append(event.geteventInfo());
			sb.append("]");

			return sb.toString();
		}
	}
}