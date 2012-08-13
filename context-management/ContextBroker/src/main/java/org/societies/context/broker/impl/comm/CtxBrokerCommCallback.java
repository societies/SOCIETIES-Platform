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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxModelBeanTranslator;
import org.societies.api.schema.context.contextmanagement.CtxBrokerResponseBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.context.broker.impl.comm.ICtxCallback;

public class CtxBrokerCommCallback implements ICommCallback {

	private static final Logger LOG = LoggerFactory.getLogger(CtxBrokerCommCallback.class);
	
	private final static List<String> NAMESPACES = Arrays.asList(
			"http://societies.org/api/schema/identity",
			"http://societies.org/api/schema/context/model",
			"http://societies.org/api/schema/context/contextmanagement");
	private static final List<String> PACKAGES = Arrays.asList(
			"org.societies.api.schema.identity",
			"org.societies.api.schema.context.model",
			"org.societies.api.schema.context.contextmanagement");
	
	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICtxCallback> ctxClients = new HashMap<String, ICtxCallback>();
	
	/*
	public void receiveResult(Stanza returnStanza, Object msgBean) {
		//CHECK WHICH END SERVICE IS SENDING US A MESSAGE
		if (msgBean.getClass().equals(CtxBrokerCreateEntityBean.class)) {
			CtxBrokerCreateEntityBean entityBean = 
					(CtxBrokerCreateEntityBean) msgBean;
			
			ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
			ctxCallbackClient.receiveCtxResult(entityBean);
		}
	}
	*/
	
	@Override
	public void receiveResult(Stanza returnStanza, Object msgBean) {
		//CHECK WHICH END SERVICE IS SENDING US A MESSAGE
		LOG.info("inside receiveResult 1");
		if (msgBean.getClass().equals(CtxBrokerResponseBean.class)) {
			
			CtxBrokerResponseBean payload = (CtxBrokerResponseBean) msgBean;
			try {
			
				LOG.info("inside receiveResult 2");
				if (payload.getCtxBrokerCreateEntityBeanResult()!=null) {
					CtxEntityBean bean = 
							(CtxEntityBean) payload.getCtxBrokerCreateEntityBeanResult();
					LOG.info("inside receiveResult 23");
					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					CtxEntity result = CtxModelBeanTranslator.getInstance().fromCtxEntityBean(bean);
					LOG.info("inside receiveResult 24 " +result);
					ctxCallbackClient.onCreatedEntity(result);
					LOG.info("inside receiveResult 25  ctxCallbackClient " +ctxCallbackClient);
				
				} else if (payload.getCtxBrokerCreateAttributeBeanResult()!=null) {
					LOG.info("inside receiveResult create Attribute");
					CtxAttributeBean attrBean = 
							(CtxAttributeBean) payload.getCtxBrokerCreateAttributeBeanResult();
					
					LOG.info("inside receiveResult create Attribute 1 " +attrBean.toString());
					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					
					CtxAttribute result = CtxModelBeanTranslator.getInstance().fromCtxAttributeBean(attrBean);
					LOG.info("inside receiveResult create Attribute 2" +result.getId());
					ctxCallbackClient.onCreatedAttribute(result);
				
				} else if (payload.getCtxBrokerCreateAssociationBeanResult()!=null){
					CtxAssociationBean bean = 
							(CtxAssociationBean) payload.getCtxBrokerCreateAssociationBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "association");
				} else if (payload.getCtxBrokerRemoveBeanResult()!=null){
					CtxEntityIdentifierBean bean = 
							(CtxEntityIdentifierBean) payload.getCtxBrokerRemoveBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "remove");
				} else if (payload.getCtxBrokerRetrieveBeanResult()!=null){
					CtxIdentifierBean bean = 
							(CtxIdentifierBean) payload.getCtxBrokerRetrieveBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "retrieve");
				} else if (payload.getCtxBrokerLookupBeanResult()!=null){
					List<CtxIdentifierBean> bean = new ArrayList<CtxIdentifierBean>();
					bean = (ArrayList<CtxIdentifierBean>)payload.getCtxBrokerLookupBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "lookup");
				} else if (payload.getCtxBrokerUpdateBeanResult()!=null){
					CtxEntityIdentifierBean bean = 
							(CtxEntityIdentifierBean) payload.getCtxBrokerUpdateBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "update");
				} else if (payload.getCtxBrokerUpdateAttributeBeanResult()!=null){
					CtxAttributeIdentifierBean bean = 
							(CtxAttributeIdentifierBean) payload.getCtxBrokerUpdateAttributeBeanResult();

					ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
					ctxCallbackClient.receiveCtxResult(bean, "update attribute");
				} else 
					LOG.error("The payload is not appropriate for the CtxBrokerCommCallback receiveResult method!");
			} catch (Exception e) {

				LOG.error("Could not parse result bean " + msgBean);
			}

			
			
		}
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
	public void receiveError(Stanza stanza, XMPPError error) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza stanza, String node, XMPPInfo info) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza stanza, String node, List<String> items) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		// TODO Auto-generated method stub
		
	}
	
	void addRequestingClient(String id, ICtxCallback callback) {
		this.ctxClients.put(id, callback);
	}
	
	private ICtxCallback getRequestingClient(String requestID) {
		ICtxCallback requestingClient = (ICtxCallback) ctxClients.get(requestID);
		ctxClients.remove(requestID);
		return requestingClient;
	}
}
