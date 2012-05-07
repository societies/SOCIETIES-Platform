/* Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.datatype.DatatypeConfigurationException;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.schema.context.contextmanagement.CtxBrokerBeanResult;
import org.societies.api.schema.context.contextmanagement.CtxBrokerCreateEntityBean;
import org.societies.api.schema.context.contextmanagement.CtxBrokerLookupBean;
import org.societies.api.schema.context.model.CtxAssociationBean;
import org.societies.api.schema.context.model.CtxAttributeBean;
import org.societies.api.schema.context.model.CtxAttributeIdentifierBean;
import org.societies.api.schema.context.model.CtxEntityBean;
import org.societies.api.schema.context.model.CtxEntityIdentifierBean;
import org.societies.api.schema.context.model.CtxIdentifierBean;
import org.societies.context.broker.api.ICtxCallback;

public class CtxBrokerCommCallback {

	//MAP TO STORE THE ALL THE CLIENT CONNECTIONS
	private final Map<String, ICtxCallback> ctxClients = new HashMap<String, ICtxCallback>();
		
	
	public CtxBrokerCommCallback(String clientID, ICtxCallback ctxBrokerClient) {
		//STORE THIS CALLBACK WITH THIS REQUEST ID
		ctxClients.put(clientID, ctxBrokerClient);
	}
	
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
	
	public void receiveResult(Stanza returnStanza, Object msgBean) throws Exception {
		//CHECK WHICH END SERVICE IS SENDING US A MESSAGE
		if (msgBean.getClass().equals(CtxBrokerBeanResult.class)) {
			
			CtxBrokerBeanResult payload = new CtxBrokerBeanResult();
			
			//what is the payload?
			//create entity
			if (payload.getCtxBrokerCreateEntityBeanResult()!=null){
				CtxEntityBean bean = 
						(CtxEntityBean) payload.getCtxBrokerCreateEntityBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "entity");
			}
			//create attribute
			else if (payload.getCtxBrokerCreateAttributeBeanResult()!=null){
				CtxAttributeBean bean = 
						(CtxAttributeBean) payload.getCtxBrokerCreateAttributeBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "attribute");
			}
			//create association
			else if (payload.getCtxBrokerCreateAssociationBeanResult()!=null){
				CtxAssociationBean bean = 
						(CtxAssociationBean) payload.getCtxBrokerCreateAssociationBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "association");
			}
			//remove
			else if (payload.getCtxBrokerRemoveBeanResult()!=null){
				CtxEntityIdentifierBean bean = 
						(CtxEntityIdentifierBean) payload.getCtxBrokerRemoveBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "remove");
			}
			//retrieve
			else if (payload.getCtxBrokerRetrieveBeanResult()!=null){
				CtxIdentifierBean bean = 
						(CtxIdentifierBean) payload.getCtxBrokerRetrieveBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "retrieve");
			}
			//lookup
			else if (payload.getCtxBrokerLookupBeanResult()!=null){
				List<CtxIdentifierBean> bean = new ArrayList<CtxIdentifierBean>();
				bean = (ArrayList<CtxIdentifierBean>)payload.getCtxBrokerLookupBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "lookup");
			}
			else if (payload.getCtxBrokerUpdateBeanResult()!=null){
				CtxEntityIdentifierBean bean = 
						(CtxEntityIdentifierBean) payload.getCtxBrokerUpdateBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "update");
			}
			else if (payload.getCtxBrokerUpdateAttributeBeanResult()!=null){
				CtxAttributeIdentifierBean bean = 
						(CtxAttributeIdentifierBean) payload.getCtxBrokerUpdateAttributeBeanResult();
				
				ICtxCallback ctxCallbackClient = getRequestingClient(returnStanza.getId());
				ctxCallbackClient.receiveCtxResult(bean, "update attribute");
			}
			
			else 
				throw new Exception("The payload is not appropriate for the CtxBrokerCommCallback receiveResult method!");
			
			
			
			
		}
	}
	
	private ICtxCallback getRequestingClient(String requestID) {
		ICtxCallback requestingClient = (ICtxCallback) ctxClients.get(requestID);
		ctxClients.remove(requestID);
		return requestingClient;
	}
}
