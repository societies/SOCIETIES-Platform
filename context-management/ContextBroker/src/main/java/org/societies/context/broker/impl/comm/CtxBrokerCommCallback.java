/** Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.context.CtxException;
import org.societies.api.context.broker.CtxAccessControlException;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.context.model.CtxModelObject;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;
import org.societies.context.broker.api.CtxBrokerException;

public class CtxBrokerCommCallback implements ICommCallback {

	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerCommCallback.class);

	private final static List<String> NAMESPACES = Collections.emptyList();
//	Arrays.asList(
//			"http://societies.org/api/schema/identity",
//			"http://societies.org/api/schema/context/model",
//			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Collections.emptyList();
//	Arrays.asList(
//			"org.societies.api.schema.identity",
//			"org.societies.api.schema.context.model",
//			"org.societies.api.schema.context.contextmanagement");

	//MAP TO STORE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICtxCallback> ctxClients = new ConcurrentHashMap<String, ICtxCallback>();

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza returnStanza, Object msgBean) {

		//CHECK WHICH END SERVICE IS SENDING THE MESSAGE
		LOG.debug("receiveResult: stanza={}, msgBean={}", returnStanza, msgBean);
		
		final ICtxCallback callbackClient = this.getRequestingClient(returnStanza.getId());
		if (callbackClient == null) {
			LOG.error("Could not handle result bean: No callback client found for stanza with id: " 
					+ returnStanza.getId());
			return;
		}
		
		if (!(msgBean instanceof CtxBrokerResponseBean)) {
			callbackClient.onException(new CtxBrokerException(
					"Could not handle result bean: Unexpected type: "
					+ ((msgBean != null) ? msgBean.getClass() : "null")));
			return;
		}
		
		final CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
		LOG.debug("receiveResult: method={}", payload.getMethod());
		try {
			switch (payload.getMethod()) {
			
			case CREATE_ENTITY:
				
				if (payload.getCreateEntityBeanResult() == null) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getCreateEntityBeanResult() is null"));
					return;
				}
				final CtxEntityBean entityBean = payload.getCreateEntityBeanResult();
				final CtxEntity entity = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(entityBean);
				callbackClient.onCreatedEntity(entity);
			
				break;
			
			case CREATE_ATTRIBUTE:
				
				if (payload.getCreateAttributeBeanResult() == null) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getCreateAttributeBeanResult() is null"));
					return;
				}
				final CtxAttributeBean attrBean = payload.getCreateAttributeBeanResult();
				final CtxAttribute attr = CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(attrBean);
				callbackClient.onCreatedAttribute(attr);

				break;
				
			case CREATE_ASSOCIATION:
				
				if (payload.getCreateAssociationBeanResult() == null ) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getCreateAssociationBeanResult() is null"));
					return;
				}
				final CtxAssociationBean assocBean = payload.getCreateAssociationBeanResult();
				final CtxAssociation assoc = CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(assocBean);
				callbackClient.onCreatedAssociation(assoc);

				break;
				
			case RETRIEVE:
				
				CtxModelObject object = null;
				if (payload.getRetrieveBeanResult() != null) {
					object = CtxModelBeanTranslator.getInstance()
							.fromCtxModelObjectBean(payload.getRetrieveBeanResult());
				}
				callbackClient.onRetrieveCtx(object);
				
				break;
				
			case RETRIEVE_ALL:

				if (payload.getRetrieveAllBeanResult() == null ) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getRetrieveAllBeanResult() is null"));
					return;
				}
				final List<CtxModelObject> ctxModelObjectList =
						new ArrayList<CtxModelObject>(payload.getRetrieveAllBeanResult().size());
				for (final CtxModelObjectBean ctxModelObjectBean : payload.getRetrieveAllBeanResult()) {
					ctxModelObjectList.add(CtxModelBeanTranslator.getInstance()
							.fromCtxModelObjectBean(ctxModelObjectBean));
				}
				callbackClient.onRetrievedAll(ctxModelObjectList);

				break;
				
			case RETRIEVE_FUTURE:
				
				if (payload.getRetrieveFutureBeanResult() == null ) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getRetrieveFutureBeanResult() is null"));
					return;
				}
				
				final List<CtxAttribute> ctxAttrFutureList = new ArrayList<CtxAttribute>();
				
				if (payload.getRetrieveFutureBeanResult() != null) {
					for (final CtxAttributeBean ctxAttrBean : payload.getRetrieveFutureBeanResult()) {
						final CtxAttribute ctxAttr = CtxModelBeanTranslator.getInstance()
								.fromCtxAttributeBean(ctxAttrBean);	
						ctxAttrFutureList.add(ctxAttr);
					}
				}
				callbackClient.onRetrieveFutureCtx(ctxAttrFutureList); 
				
				break;
				
			case UPDATE:
				
				if (payload.getUpdateBeanResult() == null) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getUpdateBeanResult() is null"));
					return;
				}
				final CtxModelObjectBean updatedObjectBean = payload.getUpdateBeanResult();
				final CtxModelObject updatedObject = CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(updatedObjectBean);	
				callbackClient.onUpdateCtx(updatedObject); 

				break;
				
			case RETRIEVE_INDIVIDUAL_ENTITY_ID:
				
				if (payload.getRetrieveIndividualEntityIdBeanResult() == null) {
					callbackClient.onException(new CtxBrokerException(
							"Could not handle result bean: CtxBrokerResponseBean.getRetrieveIndividualEntityIdBeanResult() is null"));
					return;
				}
				final CtxEntityIdentifierBean individualEntIdBean = 
						payload.getRetrieveIndividualEntityIdBeanResult();
				final CtxIdentifier individualEntId = CtxModelBeanTranslator.getInstance()
						.fromCtxIdentifierBean(individualEntIdBean);
				if (individualEntId instanceof CtxEntityIdentifier)
					callbackClient.onRetrievedEntityId(
							(CtxEntityIdentifier) individualEntId);
				else 
					callbackClient.onException(new CtxBrokerException(
							"Returned ctxIdentifier is not a CtxEntityIdentifier"));
				
				break;

			case RETRIEVE_COMMUNITY_ENTITY_ID:

				CtxEntityIdentifier communityCtxEntId = null;
				if (payload.getRetrieveCommunityEntityIdBeanResult() != null) {
					final CtxIdentifier ctxId = CtxModelBeanTranslator.getInstance()
							.fromCtxIdentifierBean(payload.getRetrieveCommunityEntityIdBeanResult());
					if (ctxId instanceof CtxEntityIdentifier) {
						communityCtxEntId = (CtxEntityIdentifier) ctxId;
					} else { 
						callbackClient.onException(new CtxBrokerException(
								"Returned ctxIdentifier is not a CtxEntityIdentifier"));
					}
				}
				callbackClient.onRetrievedEntityId(communityCtxEntId);

				break;
			
			case REMOVE:
				
				CtxModelObject removedObject = null;
				if (payload.getRemoveBeanResult() != null) {
					removedObject = CtxModelBeanTranslator.getInstance().
							fromCtxModelObjectBean(payload.getRemoveBeanResult());
				}
				callbackClient.onRemovedModelObject(removedObject);
				
				break;
				
			case LOOKUP:
			case LOOKUP_BY_SCOPE:
				
				final List<CtxIdentifier> ctxIdList = new ArrayList<CtxIdentifier>();
				if (payload.getCtxBrokerLookupBeanResult() != null) {
					for (final CtxIdentifierBean ctxIdBean : payload.getCtxBrokerLookupBeanResult()) {
						final CtxIdentifier ctxId = CtxModelBeanTranslator.getInstance()
								.fromCtxIdentifierBean(ctxIdBean);	
						ctxIdList.add(ctxId);
					}
				}
				callbackClient.onLookupCallback(ctxIdList); 

				break;
			 
			default:
			
				callbackClient.onException(new CtxBrokerException(
						"Could not handle result bean: Unsupported method: " + payload.getMethod()));
			}
			
		} catch (Exception e) {

			callbackClient.onException(new CtxBrokerException(
					"Could not handle result bean " + msgBean + " for method "
					+ payload.getMethod() + ": " + e.getLocalizedMessage(), e));
		}		
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {

		return NAMESPACES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {

		return PACKAGES;
	}

	/*
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org.societies.api.comm.xmpp.datatypes.Stanza, org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza stanza, XMPPError error) {
		
		LOG.debug("Received error: stanza={}, error={}", stanza, error);
		if (stanza.getId() == null) {
			LOG.error("Received error with null stanza id");
			return;
		}
		final ICtxCallback callbackClient = this.getRequestingClient(stanza.getId());
		if (callbackClient == null) {
			LOG.error("Received error with stanza id '" + stanza.getId()
					+ "' but no matching callback was found");
			return;
		}
		final String message = error.getGenericText();
		final CtxException exception;
		if (StanzaError.not_authorized.toString().equalsIgnoreCase(error.getStanzaErrorString()))
			exception = new CtxAccessControlException(message);
		else
			exception = new CtxBrokerException(message);
		callbackClient.onException(exception);
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

	void addRequestingClient(String id, ICtxCallback callback) {
		
		this.ctxClients.put(id, callback);
	}

	private ICtxCallback getRequestingClient(String requestID) {
	
		return this.ctxClients.remove(requestID);
	}
}