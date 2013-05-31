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

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.domainauthority.IClientJarServerCallback;
import org.societies.api.internal.domainauthority.IClientJarServerRemote;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderRemote;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderSLMCallback;
import org.societies.api.internal.security.policynegotiator.INegotiationProviderServiceMgmt;
import org.societies.api.internal.security.policynegotiator.NegotiationException;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.security.digsig.DigsigException;
import org.societies.api.security.digsig.ISignatureMgr;
import org.societies.security.policynegotiator.util.FileName;
import org.societies.security.policynegotiator.util.Net;
import org.societies.security.policynegotiator.util.UrlParamName;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class ProviderServiceMgr implements INegotiationProviderServiceMgmt {

	private static Logger LOG = LoggerFactory.getLogger(ProviderServiceMgr.class);

	private IClientJarServerRemote clientJarServer;
	private ISignatureMgr signatureMgr;
	private INegotiationProviderRemote groupMgr;

	private HashMap<String, Service> services = new HashMap<String, Service>();

	public ProviderServiceMgr() {
		LOG.info("ProviderServiceMgr");
	}
	
	public IClientJarServerRemote getClientJarServer() {
		return clientJarServer;
	}
	public void setClientJarServer(IClientJarServerRemote clientJarServer) {
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
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI fileServer,
			List<String> files, INegotiationProviderSLMCallback callback) throws NegotiationException {
		
		LOG.info("addService({}, ..., {}, " + files + ")", serviceId, fileServer);
		
		IIdentity provider = groupMgr.getIdMgr().getThisNetworkNode();
		String signature;
		String dataToSign;
		String strippedFilePath;
		
		String idStr = serviceId.getIdentifier().toString();
		Service s = new Service(idStr, slaXml, fileServer, files);

		if (files != null && files.size() > 0) {
			dataToSign = serviceId.getIdentifier().toASCIIString();
	
			for (int k = 0; k < files.size(); k++) {
				if (files.get(k).startsWith("/")) {
					strippedFilePath = files.get(k).replaceFirst("/", "");
					files.set(k, strippedFilePath);
				}
				dataToSign += files.get(k);
			}
	
			try {
				signature = signatureMgr.sign(dataToSign, provider);
			} catch (DigsigException e) {
				throw new NegotiationException(e);
			}
			IClientJarServerCallback cb = new ClientJarServerCallback(callback);
			this.clientJarServer.shareFiles(groupMgr.getIdMgr().getDomainAuthorityNode(),
					serviceId.getIdentifier(), provider, getMyCertificate(), signature, files, cb);
			services.put(idStr, s);
		}
		else {
			services.put(idStr, s);
			callback.notifySuccess();
		}
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI fileServer,
			URL[] fileUrls, INegotiationProviderSLMCallback callback) throws NegotiationException {
		
		LOG.info("addService({}, ..., {}, " + fileUrls + ")", serviceId, fileServer);

		List<String> files = new ArrayList<String>();
		String tmpFile ="3p-service.tmp";
		String fileName;
		
		for (URL f : fileUrls) {
			fileName = FileName.getBasename(f.getPath());
			LOG.debug("addService(): Adding file: URL = {}, fileName = {}", f, fileName);
			files.add(fileName);
			
			Net net = new Net(f);
			if (!net.download(tmpFile)) {
				continue;
			}
			URI server;
			String uploadUri;
			uploadUri = uriForFileUpload(fileServer.toASCIIString(), fileName,
					serviceId.getIdentifier(), getMyCertificate());
			try {
				server = new URI(uploadUri);
			} catch (URISyntaxException e) {
				LOG.warn("Could not generate URI from {}", fileServer);
				throw new NegotiationException(e);
			}
			net.put(tmpFile, server);
		}
		if (fileUrls != null && fileUrls.length > 0) {
			File tmp = new File(tmpFile);
			tmp.delete();
		}

		addService(serviceId, slaXml, fileServer, files, callback);
	}

	@Override
	public void addService(ServiceResourceIdentifier serviceId, String slaXml, URI fileServer,
			String clientJarFilePath, INegotiationProviderSLMCallback callback) throws NegotiationException {

		LOG.info("addService({}, ..., {}, String file)", serviceId, fileServer);

		List<String> files = new ArrayList<String>();
		files.add(clientJarFilePath);
		addService(serviceId, slaXml, fileServer, files, callback);
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
	 * Get URIs for all files for given service.
	 * Signature is appended to each URI as the URL parameter.
	 * 
	 * @param serviceId ID of the service to get URIs for
	 * @return All URIs
	 * @throws NegotiationException When service is not found
	 */
	protected List<URI> getSignedUris(String serviceId) throws NegotiationException {

		List <URI> uri = new ArrayList<URI>();
		String uriStr;
		String host;
		String sig;
		List <String> filePath;
		Service s = getService(serviceId);
		
		if (s == null) {
			throw new NegotiationException("Service " + serviceId + " not found");
		}

		host = s.getFileServerHost().toString();
		filePath = s.getFiles();
		
		for (int k = 0; k < filePath.size(); k++) {
			try {
				sig = signatureMgr.sign(filePath.get(k), groupMgr.getIdMgr().getThisNetworkNode());
			} catch (DigsigException e) {
				LOG.error("Failed to sign file " + filePath.get(k) + " of service " + serviceId, e);
				throw new NegotiationException(e);
			}
			uriStr = uriForFileDownload(host, filePath.get(k), serviceId, sig);
			
			try {
				uri.add(new URI(uriStr));
			} catch (URISyntaxException e) {
				throw new NegotiationException(e);
			}
		}
		return uri;
	}
	
	private String uriForFileDownload(String host, String filePath, String serviceId, String sig) {
		
		String uriStr;
		
		LOG.debug("uriForFileDownload({}, {}, ...)", host, filePath);
		
		uriStr = host + UrlPath.BASE + UrlPath.PATH_FILES + "/" + filePath.replaceAll(".*/", "") +
				"?" + UrlPath.URL_PARAM_FILE + "=" + filePath +
				"&" + UrlPath.URL_PARAM_SERVICE_ID + "=" + serviceId +
				"&" + UrlPath.URL_PARAM_SIGNATURE + "=" + sig;

		LOG.debug("uriForFileDownload(): uri = {}", uriStr);
		return uriStr;
	}
	
	private String uriForFileUpload(String host, String filePath, URI serviceId, String pubkey) {
		
		String uriStr;

		LOG.debug("uriForFileUpload({}, {}, ...)", host, filePath);

		pubkey = UrlParamName.base64ToUrl(pubkey);
		
		uriStr = host + UrlPath.BASE + UrlPath.PATH_FILES + "/" + filePath.replaceAll(".*/", "") +
				"?" + UrlPath.URL_PARAM_FILE + "=" + filePath +
				"&" + UrlPath.URL_PARAM_PUB_KEY + "=" + pubkey + 
				"&" + UrlPath.URL_PARAM_SERVICE_ID + "=" + serviceId.toASCIIString();

		LOG.debug("uriForFileUpload(): uri = {}", uriStr);
		return uriStr;
	}
	
	private String getMyCertificate() throws NegotiationException {
		
		IIdentity myIdentity = groupMgr.getIdMgr().getThisNetworkNode();
		X509Certificate cert = signatureMgr.getCertificate(myIdentity);
		String certStr;
		try {
			certStr = signatureMgr.cert2str(cert);
		} catch (DigsigException e) {
			LOG.warn("getMyCertificate(): Could not get my own (provider's) certificate");
			throw new NegotiationException(e);
		}

		return certStr;
	}

	/**
	 * 
	 * @param id Service ID
	 * @return SLA / SOP options
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
