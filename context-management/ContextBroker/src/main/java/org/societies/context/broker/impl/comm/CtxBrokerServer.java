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
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import javax.xml.datatype.DatatypeConfigurationException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.identity.RequestorCis;
import org.societies.api.identity.RequestorService;
import org.societies.api.schema.context.contextmanagement.CtxBrokerBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerBeanResult;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAssociationIdentifierBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.api.schema.context.model.CtxModelTypeBean;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.identity.RequestorCisBean;
import org.societies.api.schema.identity.RequestorServiceBean;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;

import org.societies.api.context.CtxException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.context.broker.impl.CtxBroker;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CtxBrokerServer implements IFeatureServer{

	private static Logger LOG = LoggerFactory.getLogger(CtxBrokerServer.class);

	private final static List<String> NAMESPACES = Collections
			.singletonList("http://jabber.org/protocol/pubsub");
	private static final List<String> PACKAGES = Collections
			.unmodifiableList(Arrays.asList("jabber.x.data",
					"org.jabber.protocol.pubsub",
					"org.jabber.protocol.pubsub.errors",
					"org.jabber.protocol.pubsub.owner",
					"org.jabber.protocol.pubsub.event"));

	private ICommManager endpoint;
	//private PubsubService impl;

	// need to instantiate the services
	private CtxBroker ctxbroker;
	private IIdentityManager identMgr = null;

	@Autowired
	public CtxBrokerServer(ICommManager endpoint) {
		this.endpoint = endpoint;
		//get the ctxBroker service
		ctxbroker = null;
		try {
			endpoint.register(this); // TODO unregister??
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
		}
	}


	// returns an object
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		CtxBrokerBeanResult createResponse = new CtxBrokerBeanResult();

		if (payload.getClass().equals(CtxBrokerBean.class)) {
			CtxBrokerBean cbPayload = (CtxBrokerBean) payload;

			//checks if the payload contains the createEntity method
			if (cbPayload.getCreate()!=null) {

				// get the identity based on Jid and the identity manager
				//String xmppIdentityJid = cbPayload.getCreate().getRequester();
				String targetIdentityString = cbPayload.getCreate().getTargetCss();
				RequestorBean reqBean = cbPayload.getCreate().getRequestor();
				Requestor requestor = getRequestorFromBean( reqBean);

				IIdentity requesterIdentity;
				try {
					requesterIdentity = this.identMgr.fromJid(targetIdentityString);

					Future<CtxEntity> newEntityFuture = ctxbroker.createEntity(requestor,requesterIdentity,cbPayload.getCreate().getType());
					CtxEntity newCtxEntity = newEntityFuture.get();
					// the entity is created
					//create the response based on the created CtxEntity - the response should be a result bean
					CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
					CtxEntityBean ctxBean = ctxBeanTranslator.fromCtxEntity(newCtxEntity);
					//setup the CtxEntityBean from CtxEntity				
					createResponse.setCtxBrokerCreateEntityBeanResult(ctxBean);

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

			//checks if the payload contains the createAssociation method
			if (cbPayload.getCreateAssoc()!=null) {



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
					createResponse.setCtxBrokerCreateAssociationBeanResult(ctxBean);

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
			if (cbPayload.getCreateAttr()!=null) {
				// get the identity based on Jid and the identity manager
				CtxEntityIdentifierBean entIdentBean = cbPayload.getCreateAttr().getScope();
				RequestorBean reqBean = cbPayload.getCreateAttr().getRequestor();
				Requestor requestor = getRequestorFromBean(reqBean);

				try {
					Future<CtxAttribute> newAttributeFuture = ctxbroker.createAttribute(requestor,
							new CtxEntityIdentifier(cbPayload.getCreateAttr().getType()), 
							cbPayload.getCreateAttr().getType());
					CtxAttribute newCtxAttribute= newAttributeFuture.get();
					// the attribute is created
					//create the response based on the created CtxAttribute
					CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
					CtxAttributeBean ctxBean = ctxBeanTranslator.fromCtxAttribute(newCtxAttribute);			
					createResponse.setCtxBrokerCreateAttributeBeanResult(ctxBean);

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


			}

			//checks if the payload contains the remove method
			if (cbPayload.getRemove()!=null) {

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
					createResponse.setCtxBrokerRemoveBeanResult(ctxBean);

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

			//checks if the payload contains the retrieve method
			if (cbPayload.getRetrieve()!=null) {
				RequestorBean reqBean = cbPayload.getRemove().getRequestor();
				Requestor requestor = getRequestorFromBean(reqBean);

				try {
					//IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);

					Future<CtxModelObject> retrieveModelObjectFuture = ctxbroker.retrieve(requestor,
							new CtxEntityIdentifier(cbPayload.getRetrieve().getId().toString()));

					CtxModelObject retrievedModelObject = retrieveModelObjectFuture.get();
					// the object has been retrieved

					//create the response based on the retrieved model object
					CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
					CtxIdentifierBean ctxBean = ctxBeanTranslator.fromCtxModelObject(retrievedModelObject);			
					createResponse.setCtxBrokerRetrieveBeanResult(ctxBean);

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

			//checks if the payload contains the lookup method
			if (cbPayload.getLookup()!=null) {

				// get the identity based on Jid and the identity manager
				//	String xmppIdentityJid = cbPayload.getLookup().getRequester();
				RequestorBean reqBean = cbPayload.getRemove().getRequestor();
				Requestor requestor = getRequestorFromBean(reqBean);

				try {
					//lookup(final Requestor requestor,	final IIdentity targetCss, final CtxModelType modelType,final String type) 
					String targetIdentityString = cbPayload.getCreateAssoc().getTargetCss();
					IIdentity requesterIdentity = this.identMgr.fromJid(targetIdentityString);

					Future<List<CtxIdentifier>> lookupObjectsFuture = ctxbroker.lookup(requestor, requesterIdentity,
							CtxModelType.valueOf(cbPayload.getLookup().getModelType().toString()), 
							cbPayload.getLookup().getType());

					List<CtxIdentifier> lookedupModelObjects = lookupObjectsFuture.get();
					// the object has been looked-up

					//create the response based on the looked-up model object
					//is the lookup null?
					if (lookedupModelObjects!=null) {
						List<CtxIdentifierBean> beans = new ArrayList<CtxIdentifierBean>();
						CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
						CtxIdentifierBean ctxBean = null;			
						//for each identifier create a bean and add it in the beans list
						for (CtxIdentifier lookedups : lookedupModelObjects) {
							ctxBean = ctxBeanTranslator.fromCtxIdentifier(lookedups);
							beans.add(ctxBean);
						}

						createResponse.setCtxBrokerLookupBeanResult(beans);
					}

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
				}
			}

			//checks if the payload contains the update method
			if (cbPayload.getUpdate()!=null) {


				//Future<CtxModelObject> update(final Requestor requestor, CtxModelObject object)
				// get the identity based on Jid and the identity manager
				//		String xmppIdentityJid = cbPayload.getUpdate().getRequester();
				RequestorBean reqBean = cbPayload.getRemove().getRequestor();
				Requestor requestor = getRequestorFromBean(reqBean);
				try {
					//IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);
					Future<CtxModelObject> updateModelObjectFuture;

					CtxEntityIdentifier innerIdentifier = new CtxEntityIdentifier(cbPayload.getUpdate().toString());
					CtxModelObject innerModelObject = new CtxEntity(innerIdentifier);

					updateModelObjectFuture = ctxbroker.update(requestor, innerModelObject);
					CtxModelObject updatedModelObject = updateModelObjectFuture.get();
					// the object has been updated
					//create the response based on the updated model object
					CtxModelBeanTranslator ctxBeanTranslator = CtxModelBeanTranslator.getInstance();
					CtxEntityIdentifierBean ctxBean = (CtxEntityIdentifierBean) ctxBeanTranslator.fromCtxModelObject(updatedModelObject);			
					createResponse.setCtxBrokerUpdateBeanResult(ctxBean);

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

			//checks if the payload contains the updateAttr method
			if (cbPayload.getUpdateAttr()!=null) {
				/*
				 * add the code for update attribute method here.
				 * the problem is that the external broker does not allow the update of an attribute, whereas the internal does allow that action.
				 * therefore no update attribute server side call can be implemented.
				 */

			}
		}


		//org.jabber.protocol.pubsub.owner.Pubsub ops = (org.jabber.protocol.pubsub.owner.Pubsub) payload;

		return createResponse;
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




}