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
package org.societies.security.comms.policynegotiator;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.security.policynegotiator.INegotiationProvider;
import org.societies.api.internal.schema.security.policynegotiator.MethodType;
import org.societies.api.internal.schema.security.policynegotiator.ProviderBean;
import org.societies.api.internal.schema.security.policynegotiator.ProviderBeanResult;
import org.societies.api.internal.schema.security.policynegotiator.SlaBean;

//@Component
public class CommsServer implements IFeatureServer {

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/internal/schema/security/policynegotiator"
					  ));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.internal.schema.security.policynegotiator"
					  ));
	
	private ICommManager commMgr;
	private INegotiationProvider negotiationProvider;
	
//	@Autowired
//	public CommsServer(ICommManager commManager,
//			INegotiationProvider negotiationProvider) {
//		
//		this.commManager = commManager;
//		this.negotiationProvider = negotiationProvider;
//		
//		LOG.info("CommsServer({})", commManager + ", " + negotiationProvider);
//	}

	public CommsServer() {
		LOG.info("CommsServer()");
	}
	
//	@PostConstruct
	public void init() {
		
		LOG.debug("init()");
		
		try {
			commMgr.register(this);
			LOG.debug("init(): commMgr registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
	}

	// Getters and setters for beans
	public INegotiationProvider getNegotiationProvider() {
		return negotiationProvider;
	}
	public void setNegotiationProvider(INegotiationProvider negotiationProvider) {
		this.negotiationProvider = negotiationProvider;
		//LOG.debug("setNegotiationProvider()");
		//LOG.debug("setNegotiationProvider({})", negotiationProvider);
	}
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		this.commMgr = commMgr;
		//LOG.debug("setCommManager()");
		//LOG.debug("setCommManager({})", commManager);
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		
		LOG.debug("getJavaPackages()");
		
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object messageBean) throws XMPPError {

		// Put your functionality here if there IS a return object
		
		LOG.debug("getQuery({}, {})", stanza, messageBean);
		LOG.debug("getQuery(): stanza.id   = {}", stanza.getId());
		LOG.debug("getQuery(): stanza.from = {}", stanza.getFrom());
		LOG.debug("getQuery(): stanza.to   = {}", stanza.getTo());
		
		Future<SlaBean> resultFuture;
		SlaBean resultBean;
		ProviderBeanResult result = new ProviderBeanResult();
		
		if (messageBean != null && messageBean instanceof ProviderBean) {
			
			// Method parameters
			ProviderBean providerBean = (ProviderBean) messageBean;
			String serviceId = providerBean.getServiceId();
			int sessionId = providerBean.getSessionId();
			String signedPolicyOption = providerBean.getSignedPolicyOption();
			boolean isModified = providerBean.isModified();
			
			MethodType method = providerBean.getMethod();
			
			LOG.debug("getQuery(): NegotiationProvider. Method: " + method);
			LOG.debug("getQuery(): NegotiationProvider. Params: " + serviceId + ", " +
					isModified + ", " +	sessionId);

				switch (method) {
				case GET_POLICY_OPTIONS:
					LOG.debug("getQuery(): NegotiationProvider.getPolicyOptions({})", serviceId);
					resultFuture = negotiationProvider.getPolicyOptions(serviceId);
					break;
				case ACCEPT_POLICY_AND_GET_SLA:
					resultFuture = negotiationProvider.acceptPolicyAndGetSla(sessionId,
							signedPolicyOption, isModified);
					break;
				case REJECT:
					// LOG.warn("getQuery(): Method {} returns void and should not be handled here.",
					// method);
					resultFuture = negotiationProvider.reject(sessionId);
					break;
				default:
					LOG.warn("getQuery(): unrecognized method: {}", method);
					return null;
				}
			try {
				resultBean = resultFuture.get();
				result.setSlaBean(resultBean);
			} catch (InterruptedException e) {
				LOG.warn("getQuery()", e);
			} catch (ExecutionException e) {
				LOG.warn("getQuery()", e);
			}
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		
		LOG.debug("getXMLNamespaces()");
		
		return NAMESPACES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#receiveMessage(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza stanza, Object messageBean) {
		
		// Put your functionality here if there is NO return object, ie, VOID
		
		LOG.debug("receiveMessage({}, {})", stanza, messageBean);
		
		if (messageBean instanceof ProviderBean) {
			
			// Method parameters
			ProviderBean providerBean = (ProviderBean) messageBean;
			String serviceId = providerBean.getServiceId();
			int sessionId = providerBean.getSessionId();
			String signedPolicyOption = providerBean.getSignedPolicyOption();
			boolean isModified = providerBean.isModified();
			
			MethodType method = providerBean.getMethod();
			
			LOG.debug("receiveMessage(): NegotiationProvider. Method: " + method);
			LOG.debug("receiveMessage(): NegotiationProvider. Params: " + serviceId + ", " +
					isModified + ", " +	sessionId + ", " + signedPolicyOption);
			
			switch (method) {
			case GET_POLICY_OPTIONS:
				LOG.warn("receiveMessage(): Method {} returns a value and should not be handled here.", method);
				break;
			case ACCEPT_POLICY_AND_GET_SLA:
				LOG.warn("receiveMessage(): Method {} returns a value and should not be handled here.", method);
				break;
			case REJECT:
				//negotiationProvider.reject(sessionId);
				LOG.warn("receiveMessage(): Method {} returns a value and should not be handled here.", method);
				break;
			}
		}
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#setQuery(
	 * org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza stanza, Object messageBean) throws XMPPError {
		
		LOG.debug("setQuery()");
		
		return null;
	}
}
