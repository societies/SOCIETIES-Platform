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
import java.util.concurrent.ExecutionException;


import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerRequestBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;

import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.MalformedCtxIdentifierException;
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

	private ICommManager commManager;

	@Autowired(required=true)
	private CtxBroker ctxbroker;

	private IIdentityManager identMgr = null;

	@Autowired
	public CtxBrokerServer(ICommManager commManager) throws CommunicationException {
		LOG.info(this.getClass() +" instantiated");
		this.commManager = commManager;
		this.identMgr = this.commManager.getIdManager();

		this.commManager.register(this);
	}

	// returns an object
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		//		LOG.error("Context Broker server received stanza:"+ stanza );
		//		LOG.error("Context Broker server received payload: "+payload.getClass() );
		CtxBrokerResponseBean beanResponse = new CtxBrokerResponseBean();
		CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();

		if (!(payload instanceof CtxBrokerRequestBean))
			throw new XMPPError(StanzaError.bad_request, "Unknown request bean class: " + payload.getClass());

		CtxBrokerRequestBean cbPayload = (CtxBrokerRequestBean) payload;
		if (cbPayload.getMethod() == null) {
			LOG.error("CtxBrokerRequestBean.getMethod() can't be null");
			throw new XMPPError(StanzaError.bad_request, "CtxBrokerRequestBean.getMethod() can't be null");
		}

		switch (cbPayload.getMethod()) {
		//BrokerMethodBean.RETRIEVEINDIVIDUALENTITYID
		case CREATE_ENTITY:
			
			LOG.info("CREATE_ENTITY");
			String targetIdentityString = cbPayload.getCreateEntity().getTargetCss().toString();

			RequestorBean reqBeanCreateEntity = cbPayload.getCreateEntity().getRequestor();
			Requestor requestorCreateEntity = getRequestorFromBean(reqBeanCreateEntity);

			//LOG.info("CREATE_ENTITY   brokerserver targetIdentityString: "+targetIdentityString);
			IIdentity targetIdentity;
			try {
				targetIdentity = this.identMgr.fromJid(targetIdentityString);

				CtxEntity newCtxEntity = ctxbroker.createEntity(requestorCreateEntity, targetIdentity, cbPayload.getCreateEntity().getType().toString()).get();
				//LOG.info("CREATE_ENTITY  brokerserver id :" +newCtxEntity.getId());
				//LOG.info("CREATE_ENTITY  brokerserver getLastModified :" +newCtxEntity.getLastModified());
				//LOG.info("CREATE_ENTITY  brokerserver getLastModified :" +newCtxEntity.getOwnerId());
				//LOG.info("CREATE_ENTITY  brokerserver newCtxEntity :" +newCtxEntity);

				//create the response based on the created CtxEntity - the response should be a result bean
				//CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
				CtxEntityBean ctxBean = ctxBeanTranslator.fromCtxEntity(newCtxEntity);
				//				LOG.info("CREATE_ENTITY  ctxBean created :" +ctxBean);
				//setup the CtxEntityBean from CtxEntity				
				beanResponse.setCtxBrokerCreateEntityBeanResult(ctxBean);
				//			LOG.info("CREATE_ENTITY entity beanResponse:" +beanResponse);

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
			//LOG.info(" beanResponse ready:" +beanResponse.getMethod().toString());
			try {
				String ctxEntityScopeBean =  cbPayload.getCreateAttribute().getScope().getString();
				CtxEntityIdentifier ctxEntityScope = new CtxEntityIdentifier(ctxEntityScopeBean);
				String type = cbPayload.getCreateAttribute().getType().toString();

				RequestorBean reqBeanCreateAttr = cbPayload.getCreateAttribute().getRequestor();
				Requestor requestorCreateAttr = getRequestorFromBean(reqBeanCreateAttr);

				CtxAttribute ctxAttribute = ctxbroker.createAttribute(requestorCreateAttr, ctxEntityScope, type).get();
				//LOG.error("CREATE_ATTRIBUTE 1 attribute created in remote cm :" +ctxAttribute.getId());
				//CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();

				CtxAttributeBean ctxAttributeBean = ctxBeanTranslator.fromCtxAttribute(ctxAttribute); 
				//LOG.error("CREATE_ATTRIBUTE 2 attr translated to bean :" +ctxAttributeBean);
				//LOG.error("CREATE_ATTRIBUTE 3 attr bean has a null value:" +ctxAttributeBean.getStringValue());
				//LOG.error("CREATE_ATTRIBUTE 4 attr bean everything else not null (id):" +ctxAttributeBean.getId());
				//LOG.error("CREATE_ATTRIBUTE   ctxAttributeBean get string value:" +ctxAttributeBean.getStringValue());
				beanResponse.setCtxBrokerCreateAttributeBeanResult(ctxAttributeBean);
				//LOG.info("CREATE_ATTRIBUTE 5 beanResponse completed:" +beanResponse);

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

		case LOOKUP:

			LOG.info("LOOKUP");
			//	LOG.info("LOOKUP 1 :");
			//CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();

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

		case RETRIEVE:

			LOG.info("RETRIEVE");
			CtxModelBeanTranslator ctxBeanTranslator2 = CtxModelBeanTranslator.getInstance();

			try {
				RequestorBean reqBeanRetrieve = cbPayload.getRetrieve().getRequestor();
				Requestor requestorRetrieve = getRequestorFromBean(reqBeanRetrieve);

				CtxIdentifierBean ctxIdentRetrieveBean = cbPayload.getRetrieve().getId();
				CtxIdentifier ctxIdentifier = ctxBeanTranslator2.fromCtxIdentifierBean(ctxIdentRetrieveBean);

				CtxModelObject retrievedObj = this.ctxbroker.retrieve(requestorRetrieve, ctxIdentifier).get();
				//	LOG.info("retrieved object "+ retrievedObj.getId().toString());
				// object retrieved locally 
				// create response bean

				CtxModelObjectBean ctxObjBean = ctxBeanTranslator2.fromCtxModelObject(retrievedObj);
				beanResponse.setCtxBrokerRetrieveBeanResult(ctxObjBean);
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
				beanResponse.setCtxBrokerUpdateBeanResult(ctxObjBean);

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
			LOG.info("RETRIEVEINDIVIDUALENTITYID skata 1");
			
			RequestorBean reqRetrieveIndiEntIDBean = cbPayload.getRetrieveIndividualEntityId().getRequestor();
			LOG.info("RETRIEVEINDIVIDUALENTITYID skata 2 "+reqRetrieveIndiEntIDBean.toString());
			
			Requestor requestorRetrieveIndiEntID = getRequestorFromBean(reqRetrieveIndiEntIDBean);
			LOG.info("RETRIEVEINDIVIDUALENTITYID skata 3 "+requestorRetrieveIndiEntID.toString());
			
			String individualEntityIdentityString = cbPayload.getRetrieveIndividualEntityId().getTargetCss().toString();
			LOG.info("RETRIEVEINDIVIDUALENTITYID skata 4 "+individualEntityIdentityString);
			
			IIdentity cssId;
			try {
				cssId = this.identMgr.fromJid(individualEntityIdentityString);
				LOG.info("identity "+cssId );
				LOG.info("requestorRetrieveIndiEntID "+requestorRetrieveIndiEntID.toString() );
				CtxEntityIdentifier entID = this.ctxbroker.retrieveIndividualEntityId(requestorRetrieveIndiEntID, cssId).get();
				LOG.info("retrieved entity id  local:  "+entID.toString() );
			
				CtxIdentifierBean ctxIdentBean = ctxBeanTranslator4.fromCtxIdentifier(entID);
				LOG.info("identifier converted to bean :  "+ctxIdentBean.toString() );
				
				beanResponse.setRetrieveIndividualEntityIdBeanResult(ctxIdentBean);
				
				//CtxIdentifierBean entIdBean = beanResponse.getRetrieveIndividualEntityIdBeanResult();
				
				//LOG.info("verify bean :  "+ entIdBean.toString());
				
			} catch (InvalidFormatException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		default: 
			throw new XMPPError(StanzaError.bad_request, "Nothing to do");
		}
		//LOG.error(" beanResponse ready:" +beanResponse.getMethod().toString());
		return beanResponse;
	}
	/*
		else if (cbPayload.getCreateAssoc()!=null) {
			RequestorBean reqBean = cbPayload.getCreateAssoc().getRequestor();
			Requestor requestor = getRequestorFromBean(reqBean);

			try {
				String targetIdentityString = cbPayload.getCreateAssoc().getTargetCss();
				IIdentity requesterIdentity = this.identMgr.fromJid(targetIdentityString);

				Future<CtxAssociation> newAssociationFuture = ctxbroker.createAssociation(requestor, requesterIdentity, cbPayload.getCreateAssoc().getType());
				CtxAssociation newCtxAssociation = newAssociationFuture.get();
				// the association is created
				//create the response based on the created CtxAssociation - the response should be a result bean
				CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
				CtxAssociationBean ctxBean = ctxBeanTranslator.fromCtxAssociation(newCtxAssociation);			
				beanResponse.setCtxBrokerCreateAssociationBeanResult(ctxBean);

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
		}

		//checks if the payload contains the createAttribute method


		//checks if the payload contains the remove method
		else if (cbPayload.getRemove()!=null) {

			RequestorBean reqBean = cbPayload.getRemove().getRequestor();
			Requestor requestor = getRequestorFromBean(reqBean);
			// get the identity based on Jid and the identity manager
			//				String xmppIdentityJid = cbPayload.getRemove().getRequester();
			try {

				//				IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);
				Future<CtxModelObject> removeModelObjectFuture = ctxbroker.remove(requestor,new CtxEntityIdentifier(cbPayload.getRemove().getId().getString()));
				//new CtxEntityIdentifier(cbPayload.getRemove().toString()));

				CtxModelObject removedModelObject = removeModelObjectFuture.get();
				// the object has been removed

				//create the response based on the removed model object
				CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
				CtxEntityIdentifierBean ctxBean = (CtxEntityIdentifierBean) ctxBeanTranslator.fromCtxModelObject(removedModelObject);			
				beanResponse.setCtxBrokerRemoveBeanResult(ctxBean);

			} catch (CtxException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}


		*/


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

}