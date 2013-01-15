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
import java.security.PublicKey;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.domainauthority.IClientJarServer;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;
import org.societies.api.security.digsig.DigsigException;
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

	private static HashMap<String, Resource> resources = new HashMap<String, Resource>();

	private static ISignatureMgr sigMgr;
	private static boolean accessControlEnabled;

	public ServiceClientJarAccess() {
		
		LOG.info("Constructor");
	}

	public void init() {

		LOG.debug("init()");
	}

	public static ISignatureMgr getSigMgr() {
		return sigMgr;
	}
	public void setSigMgr(ISignatureMgr sigMgr) {
		LOG.info("setSigMgr()");
		ServiceClientJarAccess.sigMgr = sigMgr;
	}
	public boolean isAccessControlEnabled() {
		return accessControlEnabled;
	}
	public void setAccessControlEnabled(boolean accessControlEnabled) {
		LOG.debug("setAccessControlEnabled({})", accessControlEnabled);
		ServiceClientJarAccess.accessControlEnabled = accessControlEnabled;
	}
	
	@Override
	public Future<UrlBean> shareFiles(URI serviceId, IIdentity provider, String providerCertStr,
			String signature, List<String> files) {
		
		UrlBean result = new UrlBean();
		Resource resource;
		String dataToVerify;
		X509Certificate providerCert;
		
		try {
			providerCert = sigMgr.str2cert(providerCertStr);
		} catch (DigsigException e) {
			LOG.warn("Could not deserialize provider's certificate from: " + providerCertStr, e);
			result.setSuccess(false);
			return new AsyncResult<UrlBean>(result);
		}
		
		dataToVerify = serviceId.toASCIIString();
		for (String file : files) {
			dataToVerify += file;
		}
		if (sigMgr.verify(dataToVerify, signature, providerCert.getPublicKey())) {
			String fileList = "";
			for (String f : files) {
				resource = new Resource(f, providerCert.getPublicKey());
				resources.put(resource.getPath(), resource);
				fileList += f;
			}
			result.setSuccess(true);
			LOG.info("Registered new files for sharing. Service: {}. Files: {}", serviceId, fileList);
		}
		else {
			LOG.warn("Unauthorized attempt to share files for service {}. Data = {}. Signature = " +
					signature, serviceId, dataToVerify);
			result.setSuccess(false);
		}
		
		
		return new AsyncResult<UrlBean>(result);
	}
	
	public static boolean isAuthorized(String filePath, String signature) {
		
		LOG.debug("isAuthorized({}, {})", filePath, signature);
		
		if (!accessControlEnabled) {
			return true;
		}

		for (Resource r : resources.values()) {
			if (r.getPath().equals(filePath)) {
				LOG.debug("isAuthorized(): file {} found", filePath);
				return sigMgr.verify(filePath, signature, r.getOwnerKey());
			}
		}
		LOG.debug("isAuthorized(): file {} NOT found", filePath);
		return false;
	}
	
	public static void addResource(String path, String certStr) throws DigsigException {
		
		X509Certificate cert = sigMgr.str2cert(certStr);
		PublicKey ownerKey = cert.getPublicKey();
		Resource resource = new Resource(path, ownerKey);
		
		resources.put(resource.getPath(), resource);
	}
}
