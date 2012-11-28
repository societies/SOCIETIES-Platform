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

package org.societies.android.platform.ctxclient.comm;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.android.api.context.model.ACtxAssociation;
import org.societies.android.api.context.model.ACtxAttribute;
import org.societies.android.api.context.model.ACtxEntity;
import org.societies.android.api.context.model.ACtxEntityIdentifier;
import org.societies.android.api.context.model.ACtxIdentifier;
import org.societies.android.api.context.model.CtxModelBeanTranslator;
import org.societies.android.api.context.model.ACtxModelObject;
import org.societies.api.schema.context.contextmanagement.BrokerMethodBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.api.schema.context.model.CtxModelObjectBean;

import android.util.Log;

public class CtxBrokerCommCallback implements ICommCallback {

	private static final String LOG_TAG = CtxBrokerCommCallback.class.getName();

	private static final List<String> ELEMENT_NAMES = Arrays.asList("BrokerMethodBean", "CtxBrokerResponseBean", 
			"CtxAssociationBean", "CtxAttributeBean", "CtxEntityBean", "CtxEntityIdentifierBean", 
			"CtxIdentifierBean", "CtxModelObjectBean");

	private final static List<String> NAMESPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");

	//MAP TO STORE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICtxCallback> ctxClients = new HashMap<String, ICtxCallback>();

	//synch issue?

	public void receiveResult(Stanza returnStanza, Object msgBean) {

		//CHECK WHICH END SERVICE IS SENDING THE MESSAGE
		Log.i(LOG_TAG, "receiveResult: stanza=" + returnStanza + ", msgBean=" + msgBean);
		
		if (!(msgBean instanceof CtxBrokerResponseBean)) {
			Log.e(LOG_TAG, "Could not handle result bean: Unexpected type: " 
					+ ((msgBean != null) ? msgBean.getClass() : "null"));
			return;
		}
		
		final ICtxCallback callbackClient = this.getRequestingClient(returnStanza.getId());
		if (callbackClient == null) {
			Log.e(LOG_TAG, "Could not handle result bean: No callback client found for stanza with id: " 
					+ returnStanza.getId());
			return;
		}
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
				callbackClient.onCreatedEntity(entity);
			
				break;
			
			case CREATE_ATTRIBUTE:
				
				Log.i(LOG_TAG, "inside receiveResult CREATE ATTRIBUTE");
				if (payload.getCreateAttributeBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateAttributeBeanResult() is null");
					return;
				}
				final CtxAttributeBean attrBean = payload.getCreateAttributeBeanResult();
				final ACtxAttribute attr = CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(attrBean);
				callbackClient.onCreatedAttribute(attr);

				break;
				
			case CREATE_ASSOCIATION:
				
				Log.i(LOG_TAG, "inside receiveResult CREATE ASSOCIATION");
				if (payload.getCreateAssociationBeanResult() == null ) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCreateAssociationBeanResult() is null");
					return;
				}
				final CtxAssociationBean assocBean = payload.getCreateAssociationBeanResult();
				final ACtxAssociation assoc = CtxModelBeanTranslator.getInstance().fromCtxAssociationBean(assocBean);
				callbackClient.onCreatedAssociation(assoc);

				break;
				
			case RETRIEVE:
				
				Log.i(LOG_TAG, "inside receiveResult RETRIEVE");
				if (payload.getRetrieveBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveBeanResult() is null");
					return;
				}
				final CtxModelObjectBean objectBean = payload.getRetrieveBeanResult();
				final ACtxModelObject object = CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(objectBean);
				callbackClient.onRetrieveCtx(object);
				
				break;
				
			case UPDATE:
				
				Log.i(LOG_TAG, "inside receiveResult UPDATE");
				if (payload.getUpdateBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getUpdateBeanResult() is null");
					return;
				}
				final CtxModelObjectBean updatedObjectBean = payload.getUpdateBeanResult();
				final ACtxModelObject updatedObject = CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(updatedObjectBean);	
				callbackClient.onUpdateCtx(updatedObject); 

				break;
				
			case RETRIEVE_INDIVIDUAL_ENTITY_ID:
				
				Log.i(LOG_TAG, "inside receiveResult RetrieveIndividualEntity");
				if (payload.getRetrieveIndividualEntityIdBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveIndividualEntityIdBeanResult() is null");
					return;
				}
				final CtxEntityIdentifierBean individualEntIdBean = 
						payload.getRetrieveIndividualEntityIdBeanResult();
				final ACtxIdentifier individualEntId = CtxModelBeanTranslator.getInstance()
						.fromCtxIdentifierBean(individualEntIdBean);
				if (individualEntId instanceof ACtxEntityIdentifier)
					callbackClient.onRetrievedEntityId(
							(ACtxEntityIdentifier) individualEntId);
				else 
					Log.e(LOG_TAG, "Returned ctxIdentifier is not a CtxEntityIdentifier");
				
				break;

			case RETRIEVE_COMMUNITY_ENTITY_ID:

				Log.i(LOG_TAG, "receiveResult: method=RETRIEVE_COMMUNITY_ENTITY_ID");
				if(payload.getRetrieveCommunityEntityIdBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRetrieveCommunityEntityIdBeanResult() is null");
					return;
				}
				final CtxEntityIdentifierBean communityEntityIdBean = 
						payload.getRetrieveCommunityEntityIdBeanResult();
				final ACtxIdentifier communityEntityId = CtxModelBeanTranslator.getInstance()
						.fromCtxIdentifierBean(communityEntityIdBean);
				if (communityEntityId instanceof ACtxEntityIdentifier)
					callbackClient.onRetrievedEntityId(
							(ACtxEntityIdentifier) communityEntityId);
				else 
					Log.e(LOG_TAG, "Returned ctxIdentifier is not a CtxEntityIdentifier");
				
				break;
			
			case REMOVE:
				
				Log.i(LOG_TAG, "inside receiveResult REMOVE");
				if (payload.getRemoveBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getRemoveBeanResult() is null");
					return;
				}
				final CtxModelObjectBean removedObjectBean = payload.getRemoveBeanResult();
				final ACtxModelObject removedObject = (removedObjectBean != null) 
						? CtxModelBeanTranslator.getInstance().fromCtxModelObjectBean(removedObjectBean)
								: null;
				callbackClient.onRemovedModelObject(removedObject);
				
				break;
				
			case LOOKUP:
				
				Log.i(LOG_TAG, "inside receiveResult LOOKUP");
				if (payload.getCtxBrokerLookupBeanResult() == null) {
					Log.e(LOG_TAG, "Could not handle result bean: CtxBrokerResponseBean.getCtxBrokerLookupBeanResult() is null");
					return;
				}
				final List<CtxIdentifierBean> ctxIdBeanList = payload.getCtxBrokerLookupBeanResult();
				final List<ACtxIdentifier> ctxIdList = new ArrayList<ACtxIdentifier>();
				for(final CtxIdentifierBean ctxIdBean : ctxIdBeanList) {
					final ACtxIdentifier ctxId = CtxModelBeanTranslator.getInstance().fromCtxIdentifierBean(ctxIdBean);	
					ctxIdList.add(ctxId);
				}
				callbackClient.onLookupCallback(ctxIdList); 

				break;
			 
			default:
			
				Log.e(LOG_TAG, "Could not handle result bean: Unsupported method: " + method);
			}
			
		} catch (Exception e) {

			Log.e(LOG_TAG, "Could not handle result bean " + msgBean + ": "
					+ e.getLocalizedMessage(), e);
		}		
	}

	public List<String> getXMLNamespaces() {

		return NAMESPACES;
	}

	public List<String> getJavaPackages() {

		return PACKAGES;
	}

	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub

	}

	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub

	}

	public void receiveItems(Stanza stanza, String node, List<String> items) {
		// TODO Auto-generated method stub

	}

	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub

	}

	void addRequestingClient(String id, ICtxCallback callback) {
		
		this.ctxClients.put(id, callback);
	}

	private ICtxCallback getRequestingClient(String requestID) {
	
		return this.ctxClients.remove(requestID);
	}
}