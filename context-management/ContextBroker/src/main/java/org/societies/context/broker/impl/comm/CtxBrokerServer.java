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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.schema.context.contextmanagement.CtxBrokerBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerBeanResult;
import org.societies.api.schema.context.model.CtxEntityBean;

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
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.context.model.CtxModelType;
import org.societies.context.broker.impl.CtxBroker;


import org.springframework.beans.factory.annotation.Autowired;


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

		CtxBrokerBean response = null;

		if (payload.getClass().equals(CtxBrokerBean.class)) {
			CtxBrokerBean cbPayload = (CtxBrokerBean) payload;

			//checks if the payload contains the createEntity method
			if (cbPayload.getCreate()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getCreate().getRequester();
				IIdentity requesterIdentity;
				try {
					requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);

					Future<CtxEntity> newEntityFuture;
					newEntityFuture = ctxbroker.createEntity(requesterIdentity, cbPayload.getCreate().getType());
					CtxEntity newCtxEntity = newEntityFuture.get();
					// the entity is created

					//create the response based on the created CtxEntity - the response should be a result bean
					CtxBrokerBeanResult createResponse = new CtxBrokerBeanResult();
					CtxEntityBean ctxBean = new CtxEntityBean();
					/*
					ctxBean.setId(newCtxEntity.getId().toString());
					
					createResponse.setCtxBrokerCreateEntityBeanResult()
					createResponse.setType(newCtxEntity.getType());
					createResponse.setObjectNumber(newCtxEntity.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());

					response.setCreate(createResponse);
					*/
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

			//checks if the payload contains the createAssociation method
	/*		if (cbPayload.getCreateAssoc()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getCreateAssoc().getOperatorIdjid();
				try {
					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);


					Future<CtxAssociation> newAssociationFuture;
					newAssociationFuture = ctxbroker.createAssociation(requesterIdentity, cbPayload.getCreateAssoc().getType());
					CtxAssociation newCtxAssociation = newAssociationFuture.get();
					// the association is created

					//create the response based on the created CtxEntity
					CtxBrokerCreateAssociationBean createResponse = new CtxBrokerCreateAssociationBean();
					createResponse.setType(newCtxAssociation.getType());
					createResponse.setObjectNumber(newCtxAssociation.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());
					//

					response.setCreateAssoc(createResponse);

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

			//checks if the payload contains the createAttribute method
			if (cbPayload.getCreateAttr()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getCreateAttr().getOperatorIdjid();
				try {
					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);


					Future<CtxAttribute> newAttributeFuture;
					newAttributeFuture = ctxbroker.createAttribute(requesterIdentity,
							new CtxEntityIdentifier(requesterIdentity.toString(), cbPayload.getCreateAttr().getType(), cbPayload.getCreateAttr().getObjectNumber()),
							cbPayload.getCreateAttr().getType());
					CtxAttribute newCtxAttribute= newAttributeFuture.get();
					// the attribute is created

					//create the response based on the created CtxEntity
					CtxBrokerCreateAttributeBean createResponse = new CtxBrokerCreateAttributeBean();
					createResponse.setType(newCtxAttribute.getType());
					createResponse.setObjectNumber(newCtxAttribute.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());
					//

					response.setCreateAttr(createResponse);

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

			//checks if the payload contains the remove method
			if (cbPayload.getRemove()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getRemove().getOperatorIdjid();


				try {

					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);
					Future<CtxModelObject> removeModelObjectFuture;
					removeModelObjectFuture = ctxbroker.remove(requesterIdentity,
							new CtxEntityIdentifier(requesterIdentity.toString(), cbPayload.getRemove().getType(), cbPayload.getRemove().getObjectNumber()) );

					CtxModelObject removedModelObject = removeModelObjectFuture.get();
					// the object has been removed

					//create the response based on the removed model object
					CtxBrokerRemoveBean createResponse = new CtxBrokerRemoveBean();
					createResponse.setType(removedModelObject.getType());
					createResponse.setObjectNumber(removedModelObject.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());
					//

					response.setRemove(createResponse);

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

			//checks if the payload contains the retrieve method
			if (cbPayload.getRetrieve()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getRetrieve().getOperatorIdjid();
				try {
					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);


					Future<CtxModelObject> retrieveModelObjectFuture;
					retrieveModelObjectFuture = ctxbroker.retrieve(requesterIdentity,
							new CtxEntityIdentifier(requesterIdentity.toString(), cbPayload.getRetrieve().getType(), cbPayload.getRetrieve().getObjectNumber()) );

					CtxModelObject retrievedModelObject = retrieveModelObjectFuture.get();
					// the object has been retrieved

					//create the response based on the retrieved model object
					CtxBrokerRetrieveBean createResponse = new CtxBrokerRetrieveBean();
					createResponse.setType(retrievedModelObject.getType());
					createResponse.setObjectNumber(retrievedModelObject.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());
					//

					response.setRetrieve(createResponse);

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

			//checks if the payload contains the lookup method
			if (cbPayload.getLookup()!=null) {

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getLookup().getOperatorIdjid();

				try {

					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);
					Future<List<CtxIdentifier>> lookupObjectsFuture;
					//find out which enum we have
					CtxModelType modelType = null;
					for (CtxModelType t: CtxModelType.values()){
						if((t.name()).equals(cbPayload.getLookup().getModelTypeName()))
							modelType = t;
					}
					//get the lookup
					lookupObjectsFuture = ctxbroker.lookup(requesterIdentity, modelType, cbPayload.getLookup().getType());

					List<CtxIdentifier> lookedupModelObjects = lookupObjectsFuture.get();
					// the object has been looked-up

					//create the response based on the looked-up model object
					CtxBrokerLookupBean createResponse = new CtxBrokerLookupBean();

					//is the lookup null?
					if (lookedupModelObjects!=null) {
						//from the first element in the list set up the object
						createResponse.setType(lookedupModelObjects.get(0).getType());
						createResponse.setObjectNumber(lookedupModelObjects.get(0).getObjectNumber());
						createResponse.setModelTypeName(lookedupModelObjects.get(0).getModelType().name());
						createResponse.setOperatorIdjid(requesterIdentity.toString());
					}
					//

					response.setLookup(createResponse);

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

				// get the identity based on Jid and the identity manager
				String xmppIdentityJid = cbPayload.getUpdate().getOperatorIdjid();
		
				
				try {

					IIdentity requesterIdentity = this.identMgr.fromJid(xmppIdentityJid);
					Future<CtxModelObject> updateModelObjectFuture;

					CtxEntityIdentifier innerIdentifier = new CtxEntityIdentifier(requesterIdentity.toString(), cbPayload.getUpdate().getType(),
							cbPayload.getUpdate().getObjectNumber());
					CtxModelObject innerModelObject = new CtxEntity(innerIdentifier);

					updateModelObjectFuture = ctxbroker.update(requesterIdentity, innerModelObject);

					CtxModelObject updatedModelObject = updateModelObjectFuture.get();
					// the object has been updated

					//create the response based on the updated model object
					CtxBrokerUpdateBean createResponse = new CtxBrokerUpdateBean();
					createResponse.setType(updatedModelObject.getType());
					createResponse.setObjectNumber(updatedModelObject.getObjectNumber());
					createResponse.setOperatorIdjid(requesterIdentity.toString());
					createResponse.setModelTypeName(updatedModelObject.getModelType().name());
					//

					response.setUpdate(createResponse);

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


			}*/

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

		return response;
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

}