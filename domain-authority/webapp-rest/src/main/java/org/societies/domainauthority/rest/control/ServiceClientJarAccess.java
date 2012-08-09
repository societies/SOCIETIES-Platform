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
package org.societies.domainauthority.rest.control;

import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.domainauthority.IClientJarServer;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;
import org.societies.api.security.digsig.ISignatureMgr;
import org.springframework.scheduling.annotation.AsyncResult;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class ServiceClientJarAccess implements IClientJarServer {

	private static Logger LOG = LoggerFactory.getLogger(ServiceClientJarAccess.class);

	private static HashMap<URI, Service> services = new HashMap<URI, Service>();

	private ICommManager commMgr;
	private static ISignatureMgr sigMgr;

	public ServiceClientJarAccess() {
		
		LOG.info("Constructor");
	}

	public void init() {

		LOG.debug("init()");
	}

	// Getters and setters for beans
	public ICommManager getCommMgr() {
		return commMgr;
	}
	public void setCommMgr(ICommManager commMgr) {
		LOG.info("setCommMgr()");
		this.commMgr = commMgr;
	}
	public ISignatureMgr getSigMgr() {
		return sigMgr;
	}
	public void setSigMgr(ISignatureMgr sigMgr) {
		LOG.info("setSigMgr()");
		ServiceClientJarAccess.sigMgr = sigMgr;
	}
	
//	@Override
//	public Future<UrlBean> addKey(URI hostname, String filePath) {
//		
//		String key = generateKey();
//		UrlBean result = new UrlBean();
//		URI url;
//		String urlStr;
//
//		List<String> fileKeys = keys.get(filePath);
//		
//		if (fileKeys == null) {
//			LOG.debug("Adding key {} for new file {}", key, filePath);
//			fileKeys = new ArrayList<String>();
//			fileKeys.add(key);
//			keys.put(filePath, fileKeys);
//		}
//		else {
//			if (fileKeys.contains(key)) {
//				LOG.warn("Key {} for file {} already exists", key, filePath);
//			}
//			else {
//				LOG.debug("Adding key {} for existing file {}", key, filePath);
//				fileKeys.add(key);
//			}
//		}
//		urlStr = hostname + Path.BASE + ServiceClientJar.PATH + "/" + filePath +
//				"?" + ServiceClientJar.URL_PARAM_SERVICE_ID + "=" + key;
//		try {
//			url = new URI(urlStr);
//			result.setUrl(url);
//			result.setSuccess(true);
//		} catch (URISyntaxException e) {
//			LOG.warn("Could not create URI from {}", urlStr, e);
//			result.setSuccess(false);
//		}
//		
//		return new AsyncResult<UrlBean>(result);
//	}
	
	@Override
	public Future<UrlBean> shareFiles(URI serviceId, IIdentity provider, String signature, List<String> files) {
		
		UrlBean result = new UrlBean();
		Service service;
		String dataToVerify;
		
		dataToVerify = serviceId.toASCIIString();
		for (String file : files) {
			dataToVerify += file;
		}
		if (sigMgr.verify(dataToVerify, signature, provider)) {
			service = new Service(serviceId, provider, files);
			services.put(serviceId, service);
			result.setSuccess(true);
			String fileList = "";
			for (String f : files) {
				fileList += f;
			}
			LOG.info("Registered new files for sharing. Service: {}. Files: {}", serviceId, fileList);
		}
		else {
			LOG.warn("Unauthorized attempt to share files for service {}. Data = {}. Signature = " +
					signature, serviceId, dataToVerify);
			result.setSuccess(false);
		}
		
		
		return new AsyncResult<UrlBean>(result);
	}
	
	private boolean isOwner(URI serviceId, IIdentity provider) {
		
		Service s = services.get(serviceId);
		
		if (s == null) {
			return false;
		}
		else {
			return s.getProvider().getJid().equals(provider.getJid());
		}
	}

//	private String generateKey() {
//		
//		Random rnd = new Random();
//		int num;
//		String key;
//		
//		num = rnd.nextInt();
//		if (num < 0) {
//			num = -num;
//		}
//		key = String.valueOf(num);
//		if (key.length() > 5) {
//			key = key.substring(0, 5);
//		}
//		return key;
//	}

	public static boolean isAuthorized(String filePath, String signature) {
		
		LOG.debug("isAuthorized({}, {})", filePath, signature);

		for (Service s : services.values()) {
			for (String file : s.getFiles()) {
				if (file.equals(filePath)) {
					LOG.debug("isAuthorized(): file {} found", filePath);
					return sigMgr.verify(filePath, signature, s.getProvider());
				}
			}
		}
		LOG.debug("isAuthorized(): file {} NOT found", filePath);
		return false;
	}
}
