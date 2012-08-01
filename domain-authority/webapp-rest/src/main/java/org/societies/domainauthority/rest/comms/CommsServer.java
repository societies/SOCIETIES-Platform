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
package org.societies.domainauthority.rest.comms;

import java.net.URI;
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
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.domainauthority.IClientJarServer;
import org.societies.api.internal.schema.domainauthority.rest.ClientJarBean;
import org.societies.api.internal.schema.domainauthority.rest.ClientJarBeanResult;
import org.societies.api.internal.schema.domainauthority.rest.MethodType;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class CommsServer implements IFeatureServer {

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/internal/schema/domainauthority/rest"
					  ));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.internal.schema.domainauthority.rest"
					  ));
	
	private ICommManager commMgr;
	private IClientJarServer clientJarServer;
	private IIdentityManager idMgr;
	
	public CommsServer() {
		LOG.info("CommsServer()");
	}
	
	public void init() {
		
		LOG.debug("init(): commMgr = {}", commMgr.toString());
		
		try {
			commMgr.register(this);
			LOG.debug("init(): commMgr registered");
		} catch (CommunicationException e) {
			LOG.error("init(): ", e);
		}
		
		idMgr = commMgr.getIdManager();

		if (idMgr == null) {
			LOG.error("init({}): Could not get IdManager from ICommManager");
		}
	}

	// Getters and setters for beans
	public IClientJarServer getNegotiationProvider() {
		return clientJarServer;
	}
	public void setNegotiationProvider(IClientJarServer negotiationProvider) {
		this.clientJarServer = negotiationProvider;
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
		
		Future<UrlBean> resultFuture;
		UrlBean resultBean;
		ClientJarBeanResult result = new ClientJarBeanResult();
		
		if (messageBean != null && messageBean instanceof ClientJarBean) {
			
			// Method parameters
			ClientJarBean clientJarBean = (ClientJarBean) messageBean;
			List<String> files = clientJarBean.getFiles();
			URI serviceId = clientJarBean.getServiceId();
			String providerIdentity = clientJarBean.getProviderIdentity();
			String signature = clientJarBean.getSignature();
			
			MethodType method = clientJarBean.getMethod();
			
			LOG.debug("getQuery(): ClientJarBean. Method: " + method);
			LOG.debug("getQuery(): ClientJarBean. Params: " + serviceId + ", " + providerIdentity);

			switch (method) {
				case SHARE_FILES:
					LOG.debug("getQuery(): ClientJarBean.shareFiles()");
					IIdentity provider;
					try {
						provider = idMgr.fromJid(providerIdentity);
						resultFuture = clientJarServer.shareFiles(serviceId, provider, signature, files);
					} catch (InvalidFormatException e) {
						LOG.warn("Could not get identity", e);
						return failure();
					}
					break;
				default:
					LOG.warn("getQuery(): unrecognized method: {}", method);
					return null;
				}
			try {
				resultBean = resultFuture.get();
				result.setUrlBean(resultBean);
			} catch (InterruptedException e) {
				LOG.warn("getQuery()", e);
			} catch (ExecutionException e) {
				LOG.warn("getQuery()", e);
			}
		}
		
		return result;
	}
	
	private ClientJarBeanResult failure() {
		
		UrlBean urlBean = new UrlBean();
		ClientJarBeanResult result = new ClientJarBeanResult();

		urlBean.setSuccess(false);
		result.setUrlBean(urlBean);
		
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
		
		if (messageBean instanceof ClientJarBean) {
			
			// Method parameters
			ClientJarBean providerBean = (ClientJarBean) messageBean;
			
			MethodType method = providerBean.getMethod();
			
			LOG.debug("receiveMessage(): NegotiationProvider. Method: " + method);
			
			switch (method) {
			case SHARE_FILES:
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
