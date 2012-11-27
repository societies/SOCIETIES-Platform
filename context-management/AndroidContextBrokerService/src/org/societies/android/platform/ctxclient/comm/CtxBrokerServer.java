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
package org.societies.android.platform.ctxclient.comm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
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
import org.societies.context.broker.impl.CtxBroker;
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
	
	private static final String[] EVENT_TYPES = { EventTypes.CIS_CREATION };

	private ICommManager commManager;
	
	private ICISCommunicationMgrFactory commMgrFactory;

	@Autowired(required=true)
	private CtxBroker ctxbroker;

	private IIdentityManager identMgr = null;

	@Autowired
	public CtxBrokerServer(ICommManager commManager, 
			ICISCommunicationMgrFactory commMgrFactory, IEventMgr eventMgr)
					throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		this.commManager = commManager;
		this.identMgr = this.commManager.getIdManager();
		this.commMgrFactory = commMgrFactory;

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
		}
		// Register for new CISs
		if (LOG.isInfoEnabled())
			LOG.info("Registering for '" + Arrays.asList(EVENT_TYPES) + "' events");
		eventMgr.subscribeInternalEvent(new NewCisHandler(), EVENT_TYPES, null);
	}

	// returns an object
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("getQuery: stanza=" + stanza + ", payload=" + payload);

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

			LOG.info("CREATE_ENTITY");
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
				
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
			
		case CREATE_ATTRIBUTE:

			LOG.info("CREATE_ATTRIBUTE");
			
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
				

			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;


		case CREATE_ASSOCIATION:

			LOG.info("CREATE_ASSOCIATION");

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
			
			} catch (InvalidFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DatatypeConfigurationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			break;

		case RETRIEVE:

			LOG.info("RETRIEVE");
			CtxModelBeanTranslator ctxBeanTranslator2 = CtxModelBeanTranslator.getInstance();
			try {
				RequestorBean reqBeanRetrieve = cbPayload.getRetrieve().getRequestor();
				Requestor requestorRetrieve = getRequestorFromBean(reqBeanRetrieve);

				CtxIdentifierBean ctxIdentRetrieveBean = cbPayload.getRetrieve().getId();
				CtxIdentifier ctxIdentifier = ctxBeanTranslator2.fromCtxIdentifierBean(ctxIdentRetrieveBean);

				CtxModelObject retrievedObj = this.ctxbroker.retrieve(requestorRetrieve, ctxIdentifier).get();
					LOG.info("it indi entity retrieved object? "+ retrievedObj.getId().toString());
				// object retrieved locally 
				// create response bean

				CtxModelObjectBean ctxObjBean = ctxBeanTranslator2.fromCtxModelObject(retrievedObj);
				beanResponse.setRetrieveBeanResult(ctxObjBean);
				//	LOG.info("retrieved object beanResponse.setCtxBrokerRetrieveBeanResult "+ beanResponse.getCtxBrokerRetrieveBeanResult().toString());

			} catch (MalformedCtxIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

			break;
			
		case RETRIEVE_INDIVIDUAL_ENTITY_ID:

			LOG.info("RETRIEVEINDIVIDUALENTITYID");
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

			} catch (InvalidFormatException e) {
				LOG.error(e.getLocalizedMessage());
			} catch (CtxException e) {
				LOG.error(e.getLocalizedMessage());
			} catch (InterruptedException e) {
				LOG.error(e.getLocalizedMessage());
			} catch (ExecutionException e) {
				LOG.error(e.getLocalizedMessage());
			}

			break;
			
		case RETRIEVE_COMMUNITY_ENTITY_ID:

			if (LOG.isInfoEnabled())
				LOG.info("RETRIEVE_COMMUNITY_ENTITY_ID");
		
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
		
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), e);
			}

			break;

		case UPDATE:

			LOG.info("UPDATE");
			CtxModelBeanTranslator ctxBeanTranslator3 = CtxModelBeanTranslator.getInstance();
			//	LOG.info("UPDATE 1  "+ cbPayload.getUpdate().toString());

			RequestorBean reqBeanUpdate = cbPayload.getUpdate().getRequestor();
			//	LOG.info("UPDATE 2 reqBeanUpdate "+ reqBeanUpdate);
			Requestor requestorUpdate = getRequestorFromBean(reqBeanUpdate);

			CtxModelObjectBean ctxModelObjBean = cbPayload.getUpdate().getCtxModelOject();
			//		LOG.info("UPDATE 3 reqBeanUpdate "+ ctxModelObjBean);
			CtxModelObject ctxModelObject = ctxBeanTranslator3.fromCtxModelObjectBean(ctxModelObjBean);

			//		LOG.info("UPDATE 4 reqBeanUpdate ctxModelObject "+ ctxModelObject);
			try {
				CtxModelObject updatedObj = this.ctxbroker.update(requestorUpdate, ctxModelObject).get();
				//			LOG.info("UPDATE 5 locally updated object "+ updatedObj.getId().toString());

				CtxModelObjectBean ctxObjBean = ctxBeanTranslator3.fromCtxModelObject(updatedObj);
				//		LOG.info("UPDATE 6 locally object converter to bean "+ ctxObjBean.getId().toString());
				beanResponse.setUpdateBeanResult(ctxObjBean);

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

			break;
			
		case REMOVE:

			if (LOG.isInfoEnabled())
				LOG.info("REMOVE");

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
		
				throw new XMPPError(StanzaError.internal_server_error, e.getLocalizedMessage(), e);
			}

			break;
			
		case LOOKUP:

			LOG.info("LOOKUP");
			
			CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
			RequestorBean reqBeanLookup = cbPayload.getLookup().getRequestor();
			Requestor requestor = getRequestorFromBean(reqBeanLookup);

			String targetCssString = cbPayload.getLookup().getTargetCss();
			IIdentity targetCss;

			try {
				targetCss = this.identMgr.fromJid(targetCssString);

				//	LOG.info("LOOKUP 1 :" +beanResponse);
				CtxModelType modelType = ctxBeanTranslator.CtxModelTypeFromCtxModelTypeBean(cbPayload.getLookup().getModelType());

				//LOG.info("LOOKUP 2 modelType:" +modelType);

				String type = cbPayload.getLookup().getType().toString();
				//LOG.info("LOOKUP 3 type:" +type);

				List<CtxIdentifier> lookupResultsList = ctxbroker.lookup(requestor, targetCss, modelType, type).get();

				//LOG.info("LOOKUP 4 final local results size:" +lookupResultsList.size());

				if(	lookupResultsList.size()>0){
					List<CtxIdentifierBean> identBeanList = new ArrayList<CtxIdentifierBean>(); 

					for(CtxIdentifier identifier :lookupResultsList){
						///	LOG.info("LOOKUP 5  identifier.toString():" +identifier.toString());
						if (identifier.getModelType().equals(CtxModelType.ATTRIBUTE)){
							CtxAttributeIdentifier attrId = (CtxAttributeIdentifier) identifier;
							CtxAttributeIdentifierBean attrIdBean = (CtxAttributeIdentifierBean) ctxBeanTranslator.fromCtxIdentifier(attrId);
							//CtxIdentifierBean identityBean = ctxBeanTranslator.fromCtxIdentifier(identifier);
							identBeanList.add(attrIdBean);
						}						
						//CtxIdentifierBean identityBean = ctxBeanTranslator.fromCtxIdentifier(identifier);
					}
					//LOG.info("LOOKUP 6 identBeanList :" +identBeanList);
					beanResponse.setCtxBrokerLookupBeanResult(identBeanList);
					//	LOG.info("LOOKUP 7 beanResponse get lookupResult:" +beanResponse.getCtxBrokerLookupBeanResult());
				}

			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
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

			break;


		default: 
			throw new XMPPError(StanzaError.feature_not_implemented, 
					"Unsupported remote context method: " + cbPayload.getMethod());
		}
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("beanResponse ready:" + beanResponse);
		return beanResponse;
	}

	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}


	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}


	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

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
			e.printStackTrace();
			return null;
		}
	}

	private class NewCisHandler extends EventListener {

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
				LOG.debug("Received internal " + event.geteventType() + " event: " + event);
			
			if (EventTypes.CIS_CREATION.equals(event.geteventType())) {
				
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
				
				} catch (Exception e) {
					LOG.error("Could not register CtxBrokerServer to Comms Manager for CIS '" 
							+ cisIdStr + "': " + e.getLocalizedMessage(), e);
				}
				
			} else {
				
				if (LOG.isWarnEnabled())
					LOG.warn("Received unexpeted event of type '" + event.geteventType() + "'");
			}
		}
	}
}