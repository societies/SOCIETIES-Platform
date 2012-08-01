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
package org.societies.security.policynegotiator.provider;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.domainauthority.IClientJarServer;
import org.societies.api.internal.domainauthority.IClientJarServerRemote;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.policynegotiator.exception.NegotiationException;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class ProviderServiceMgr implements INegotiationProviderServiceMgmt {

	private static Logger LOG = LoggerFactory.getLogger(INegotiationProviderServiceMgmt.class);

//	private IClientJarServerRemote clientJarServer;
	private IClientJarServer clientJarServer;
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;

	private HashMap<String, Service> services = new HashMap<String, Service>();

	public ProviderServiceMgr() {
		LOG.info("ProviderServiceMgr");
	}
	
//	public IClientJarServerRemote getClientJarServer() {
	public IClientJarServer getClientJarServer() {
		return clientJarServer;
	}
//	public void setClientJarServer(IClientJarServerRemote clientJarServer) {
	public void setClientJarServer(IClientJarServer clientJarServer) {
		LOG.debug("setClientJarServer()");
		this.clientJarServer = clientJarServer;
	}
	public ISignatureMgr getSignatureMgr() {
		return signatureMgr;
	}
	public void setSignatureMgr(ISignatureMgr signatureMgr) {
		LOG.debug("setSignatureMgr()");
		this.signatureMgr = signatureMgr;
	}
	public INegotiationProviderRemote getGroupMgr() {
		return groupMgr;
	}
	public void setGroupMgr(INegotiationProviderRemote groupMgr) {
		LOG.debug("setGroupMgr()");
		this.groupMgr = groupMgr;
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI clientJar) {
		
		String idStr = serviceId.getIdentifier().toString();
		Service s = new Service(idStr, slaXml, clientJar, null);
		
		services.put(idStr, s);
	}
	
	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, IIdentity clientJarServer) {
		
		String idStr = serviceId.getIdentifier().toString();
		Service s = new Service(idStr, slaXml, null, clientJarServer);
		
		services.put(idStr, s);
	}

	@Override
	public void removeService(ServiceResourceIdentifier serviceId) {
		
		String idStr = serviceId.getIdentifier().toString();
		
		services.remove(idStr);
	}

	protected HashMap<String, Service> getServices() {
		return services;
	}
	
	protected Service getService(String id) {
		
		Service s = services.get(id);
		
		if (s == null) {
			LOG.warn("getService({}): service not found", id);
		}
		
		return s;
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws NegotiationException When service is not found
	 */
	protected URI getClientJarUri(String id) throws NegotiationException {

		// FIXME
		String host = "http://localhost:8080";
		String filePath = "Calculator.jar";
		URI uri;
		String sig;
		try {
			sig = signatureMgr.sign(id, groupMgr.getIdMgr().getThisNetworkNode());  // TODO: sign also client ID
		} catch (DigsigException e) {
			LOG.warn("Failed to sign service " + id + " for client", e);
			throw new NegotiationException(e);
		}
		String uriStr = host + UrlPath.BASE + UrlPath.PATH + "/" + filePath +
				"?" + UrlPath.URL_PARAM_SERVICE_ID + "=" + id +
				"&" + UrlPath.URL_PARAM_SIGNATURE + "=" + sig;
		
		try {
			uri = new URI(uriStr);
//			Future<UrlBean> urlBeanFuture = clientJarServer.shareFiles();
//			UrlBean urlBean = urlBeanFuture.get();
//			uri = urlBean.getUrl();
			return uri;
		} catch (URISyntaxException e) {
			throw new NegotiationException(e);
//		} catch (InterruptedException e) {
//			throw new NegotiationException(e);
//		} catch (ExecutionException e) {
//			throw new NegotiationException(e);
		}
		
//		Service s = getService(id);
//		
//		if (s != null) {
//			return s.getClientJarUri();
//		}
//		else {
//			throw new NegotiationException("Service " + id + " not found");
//		}
	}

	/**
	 * 
	 * @param id
	 * @return
	 * @throws NegotiationException When service is not found
	 */
	protected String getSlaXmlOptions(String id) throws NegotiationException {
		
		Service s = getService(id);
		
		if (s != null) {
			return s.getSlaXmlOptions();
		}
		else {
			throw new NegotiationException("Service " + id + " not found");
		}
	}
}
