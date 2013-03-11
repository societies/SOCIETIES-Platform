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
import java.util.List;
import java.util.Map;

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
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;
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

	private IIdentityManager identMgr = null;

	@Autowired
	public CtxBrokerServer(ICommManager commManager, 
			ICISCommunicationMgrFactory commMgrFactory, IEventMgr eventMgr, ICtxEventMgr ctxEventMgr)
					throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		this.commManager = commManager;
		this.identMgr = this.commManager.getIdManager();
		this.commMgrFactory = commMgrFactory;
		this.ctxEventMgr = ctxEventMgr;

		// Register to CSS Comm Mgr
		if (LOG.isInfoEnabled())
			LOG.info("Registering CtxBrokerServer to Comms Manager for CSS '"
					+ this.commManager.getIdManager().getThisNetworkNode() + "'");
		this.commManager.register(this);
		// Register to all available CIS Comm Mgrs
		for (final Map.Entry<IIdentity, ICommManager> entry : this.commMgrFactory.getAllCISCommMgrs().entrySet()) {
			if (LOG.isInfoEnabled())
				LOG.info("Registering CtxBrokerServer to Comms Manager for CIS '"
						+ entry.getKey() + "'");
			entry.getValue().register(this);
			if (LOG.isInfoEnabled())
				LOG.info("Creating event topics '" + Arrays.toString(InternalCtxBroker.EVENT_TOPICS) 
						+ "' for CIS " + entry.getKey());
			this.ctxEventMgr.createTopics(entry.getKey(), InternalCtxBroker.EVENT_TOPICS);
		}
		// Register for new/restored CISs
		if (LOG.isInfoEnabled())
			LOG.info("Registering for '" + Arrays.asList(EVENT_TYPES) + "' events");
		eventMgr.subscribeInternalEvent(new NewCisCommMgrHandler(), EVENT_TYPES, null);
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		if (LOG.isDebugEnabled())
			LOG.debug("getQuery: stanza=" + stanza + ", payload=" + payload);

		if (!(payload instanceof CtxBrokerRequestBean))
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: " + payload.getClass());

		final CtxBrokerRequestBean cbPayload = (CtxBrokerRequestBean) payload;
		if (cbPayload.getMethod() == null) {
			LOG.error("CtxBrokerRequestBean.getMethod() can't be null");
			throw new XMPPError(StanzaError.bad_request, "CtxBrokerRequestBean.getMethod() can't be null");
		}
		
		final CtxBrokerResponseBean beanResponse = new CtxBrokerResponseBean();
		beanResponse.setMethod(cbPayload.getMethod());

		switch (cbPayload.getMethod()) {

		case CREATE_ENTITY:

			if (LOG.isDebugEnabled())
				LOG.debug("CREATE_ENTITY");
			String targetIdentityString = cbPayload.getCreateEntity().getTargetCss().toString();

			RequestorBean reqBeanCreateEntity = cbPayload.getCreateEntity().getRequestor();
			Requestor requestorCreateEntity = getRequestorFromBean(reqBeanCreateEntity);

			IIdentity targetIdentity;
			try {
				targetIdentity = this.identMgr.fromJid(targetIdentityString);

				CtxEntity newCtxEntity = ctxbroker.createEntity(requestorCreateEntity, targetIdentity, cbPayload.getCreateEntity().getType().toString()).get();

				//create the response based on the created CtxEntity - the response should be a result bean
				CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
				CtxEntityBean ctxEntityBean = ctxBeanTranslator.fromCtxEntity(newCtxEntity);
			
				//setup the CtxEntityBean from CtxEntity				
				beanResponse.setCreateEntityBeanResult(ctxEntityBean);
				
			} catch (Exception e) {
				LOG.error("Failed to create entity: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			} 
			break;
			
		case CREATE_ATTRIBUTE:

			if (LOG.isDebugEnabled())
				LOG.debug("CREATE_ATTRIBUTE");
			try {
				String ctxEntityScopeBean =  cbPayload.getCreateAttribute().getScope().getString();
				CtxEntityIdentifier ctxEntityScope = new CtxEntityIdentifier(ctxEntityScopeBean);
				String type = cbPayload.getCreateAttribute().getType().toString();

				RequestorBean reqBeanCreateAttr = cbPayload.getCreateAttribute().getRequestor();
				Requestor requestorCreateAttr = getRequestorFromBean(reqBeanCreateAttr);

				CtxAttribute ctxAttribute = ctxbroker.createAttribute(requestorCreateAttr, ctxEntityScope, type).get();
				CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();

				CtxAttributeBean ctxAttributeBean = ctxBeanTranslator.fromCtxAttribute(ctxAttribute); 
				beanResponse.setCreateAttributeBeanResult(ctxAttributeBean);
				

			} catch (Exception e) {
				LOG.error("Failed to create attribute: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;


		case CREATE_ASSOCIATION:

			if (LOG.isDebugEnabled())
				LOG.debug("CREATE_ASSOCIATION");
			String targetIdentityStringAssoc = cbPayload.getCreateAssociation().getTargetCss().toString();

			RequestorBean reqBeanCreateAssoc = cbPayload.getCreateAssociation().getRequestor();
			Requestor requestorCreateAssoc = getRequestorFromBean(reqBeanCreateAssoc);
			String assocType = cbPayload.getCreateAssociation().getType();

			IIdentity targetIdentityAssoc;
			try {
				targetIdentityAssoc = this.identMgr.fromJid(targetIdentityStringAssoc);
				CtxAssociation association = ctxbroker.createAssociation(requestorCreateAssoc, targetIdentityAssoc, assocType).get();
				CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
				CtxAssociationBean ctxAssocBean = ctxBeanTranslator.fromCtxAssociation(association);
			
				beanResponse.setCreateAssociationBeanResult(ctxAssocBean);
			
			} catch (Exception e) {
				LOG.error("Failed to create association: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case RETRIEVE:

			if (LOG.isDebugEnabled())
				LOG.debug("RETRIEVE");
			try {
				RequestorBean reqBeanRetrieve = cbPayload.getRetrieve().getRequestor();
				Requestor requestorRetrieve = getRequestorFromBean(reqBeanRetrieve);
				
				CtxIdentifierBean ctxIdentRetrieveBean = cbPayload.getRetrieve().getId();
				CtxIdentifier ctxIdentifier = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ctxIdentRetrieveBean);
				
				CtxModelObject retrievedObj = this.ctxbroker.retrieve(requestorRetrieve, ctxIdentifier).get();
				// object retrieved locally 
				// create response bean
				CtxModelObjectBean ctxObjBean = CtxModelBeanTranslator.getInstance().fromCtxModelObject(retrievedObj);
				beanResponse.setRetrieveBeanResult(ctxObjBean);

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
			
		case RETRIEVE_INDIVIDUAL_ENTITY_ID:

			if (LOG.isDebugEnabled())
				LOG.debug("RETRIEVE_INDIVIDUAL_ENTITY_ID");
			CtxModelBeanTranslator ctxBeanTranslator4 = CtxModelBeanTranslator.getInstance();
			RequestorBean reqRetrieveIndiEntIDBean = cbPayload.getRetrieveIndividualEntityId().getRequestor();
			Requestor requestorRetrieveIndiEntID = getRequestorFromBean(reqRetrieveIndiEntIDBean);
			String individualEntityIdentityString = cbPayload.getRetrieveIndividualEntityId().getTargetCss().toString();

			IIdentity cssId;
			try {
				cssId = this.identMgr.fromJid(individualEntityIdentityString);
				CtxEntityIdentifier entID = this.ctxbroker.retrieveIndividualEntityId(requestorRetrieveIndiEntID, cssId).get();

				CtxEntityIdentifierBean ctxEntityIdentBean = (CtxEntityIdentifierBean) ctxBeanTranslator4.fromCtxIdentifier(entID);
				beanResponse.setRetrieveIndividualEntityIdBeanResult(ctxEntityIdentBean);

			} catch (Exception e) {
				LOG.error("Failed to retrieve individual entity id: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;
			
		case RETRIEVE_COMMUNITY_ENTITY_ID:

			if (LOG.isDebugEnabled())
				LOG.debug("RETRIEVE_COMMUNITY_ENTITY_ID");
		
			final RequestorBean retrieveCommunityEntityIdBeanRequestorBean = 
					cbPayload.getRetrieveCommunityEntityId().getRequestor();
			final Requestor retrieveCommunityEntityIdBeanRequstor = 
					getRequestorFromBean(retrieveCommunityEntityIdBeanRequestorBean);
			final String retrieveCommunityEntityIdTargetStr = 
					cbPayload.getRetrieveCommunityEntityId().getTarget().toString();

			final IIdentity retrieveCommunityEntityIdTarget;
			try {
				retrieveCommunityEntityIdTarget = 
						this.identMgr.fromJid(retrieveCommunityEntityIdTargetStr);
				final CtxEntityIdentifier communityEntityId = 
						this.ctxbroker.retrieveCommunityEntityId(
								retrieveCommunityEntityIdBeanRequstor, retrieveCommunityEntityIdTarget).get();

				final CtxEntityIdentifierBean communityEntityIdBean = 
						(CtxEntityIdentifierBean) CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifier(communityEntityId);
				beanResponse.setRetrieveCommunityEntityIdBeanResult(communityEntityIdBean);

			} catch (Exception e) {
				// TODO send application error when supported
				LOG.error("Failed to retrieve community entity id: " + e.getLocalizedMessage(), e);
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;

		case UPDATE:

			if (LOG.isDebugEnabled())
				LOG.debug("UPDATE");
			try {
				RequestorBean reqBeanUpdate = cbPayload.getUpdate().getRequestor();
				Requestor requestorUpdate = getRequestorFromBean(reqBeanUpdate);
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

			if (LOG.isDebugEnabled())
				LOG.debug("REMOVE");
			try {
				final RequestorBean removeBeanRequestorBean = 
						cbPayload.getRemove().getRequestor();
				final Requestor removeBeanRequestor = 
						this.getRequestorFromBean(removeBeanRequestorBean);
				final CtxIdentifier removeCtxId = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean( 
						cbPayload.getRemove().getId());

				final CtxModelObject removedModelObject = 
						this.ctxbroker.remove(removeBeanRequestor, removeCtxId).get();

				final CtxModelObjectBean removedModelObjectBean = (removedModelObject != null)  
						? CtxModelBeanTranslator.getInstance().fromCtxModelObject(removedModelObject) : null;
				beanResponse.setRemoveBeanResult(removedModelObjectBean);

			} catch (Exception e) {
				LOG.error("Failed to remove context model object: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;
			
		case LOOKUP:

			if (LOG.isDebugEnabled())
				LOG.debug("LOOKUP");
			try {
				RequestorBean reqBeanLookup = cbPayload.getLookup().getRequestor();
				Requestor requestor = getRequestorFromBean(reqBeanLookup);
				String targetCssString = cbPayload.getLookup().getTargetCss();
				IIdentity targetCss = this.identMgr.fromJid(targetCssString);
				CtxModelType modelType = 
						CtxModelBeanTranslator.getInstance().ctxModelTypeFromCtxModelTypeBean(cbPayload.getLookup().getModelType());
				String type = cbPayload.getLookup().getType().toString();
				List<CtxIdentifier> lookupResultsList = ctxbroker.lookup(requestor, targetCss, modelType, type).get();
				if (lookupResultsList.size() > 0) {
					List<CtxIdentifierBean> identBeanList = new ArrayList<CtxIdentifierBean>(); 
					for (CtxIdentifier identifier : lookupResultsList) {
						if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)){
							CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) identifier;
							CtxAttributeIdentifierBean attrIdBean = 
									(CtxAttributeIdentifierBean) CtxModelBeanTranslator.getInstance().fromCtxIdentifier(attrId);
							identBeanList.add(attrIdBean);
						}						
						//CtxIdentifierBean identityBean = ctxBeanTranslator.fromCtxIdentifier(identifier);
						// TODO why only attr ids???
					}
					beanResponse.setCtxBrokerLookupBeanResult(identBeanList);
				}

			} catch (CtxAccessControlException cace) {
				LOG.error("Failed to perfrom lookup: " + cace.getLocalizedMessage(), cace);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.not_authorized, cace.getLocalizedMessage(), null);
			} catch (Exception e) {
				LOG.error("Failed to perform lookup: " + e.getLocalizedMessage(), e);
				// TODO send application error when supported
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), null);
			}
			break;


		default: 
			throw new XMPPError(StanzaError.feature_not_implemented, 
					"Unsupported remote context method: " + cbPayload.getMethod());
		}
		
		if (LOG.isDebugEnabled())
			LOG.debug("beanResponse ready:" + beanResponse);
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


	public Requestor getRequestorFromBean(RequestorBean bean){
		IIdentityManager idm = this.identMgr;
		try {
			if (bean instanceof RequestorCisBean){
				RequestorCis requestor = new RequestorCis(idm.fromJid(bean.getRequestorId()), idm.fromJid(((RequestorCisBean) bean).getCisRequestorId()));
				return requestor;

			}else if (bean instanceof RequestorServiceBean){
				RequestorService requestor = new RequestorService(idm.fromJid(bean.getRequestorId()), ((RequestorServiceBean) bean).getRequestorServiceId());
				return requestor;
			}else{
				return new Requestor(idm.fromJid(bean.getRequestorId()));
			}
		} catch (InvalidFormatException e) {
			// TODO Auto-generated catch block
			LOG.error("Failed to instantiate Requestor from bean: " + e.getLocalizedMessage(), e);
			return null;
		}
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