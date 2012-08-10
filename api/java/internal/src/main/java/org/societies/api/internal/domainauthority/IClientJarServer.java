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
package org.societies.api.internal.domainauthority;

import java.net.URI;
import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.identity.IIdentity;
import org.societies.api.internal.schema.domainauthority.rest.UrlBean;
import org.societies.api.security.digsig.ISignatureMgr;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public interface IClientJarServer {

	/**
	 * Add a key for given file.
	 * Any jar file can have multiple keys associated and this method may be
	 * called multiple times to add more keys for same jar file.
	 * 
	 * @param path Local path to the jar file to be served
	 * @param key The key to authenticate jar file downloads in future
	 */
	//public void addKey(String path, String key);
	
	/**
	 * Generate and add a new key for given file.
	 * Any jar file can have multiple keys associated and this method may be
	 * called multiple times to add more keys for same jar file.
	 * 
	 * @param hostname the main part of the URL where the JAR is supposed to be,
	 * including protocol and port if applicable, e.g., http://example.com:8080
	 * 
	 * @param filePath Local path to the jar file to be served
	 * 
	 * @return Full URL with path and authentication key to directly download the jar file.
	 */
	//public Future<UrlBean> addKey(URI hostname, String filePath);
	
	/**
	 * Notify the server about new files to be shared in relation to a service.
	 * Typically this should called to add a new service to server registry.
	 * It can also be used to change the list of files associated with an existing service.
	 * 
	 * <p>Digital signature parameter should be created with
	 * {@link ISignatureMgr#sign(byte[], java.security.PrivateKey)}.
	 * Data to sign are serviceId and all file names the order below.
	 * 
	 * <p>Example:
	 * <pre>
	 * String data = serviceId.toString();
	 * for (String s : files) {
	 * 	data += s;
	 * }
	 * String signature = sigMgr.sign(data, providerKey);
	 * </pre>
	 * 
	 * @param serviceId The ID of the service
	 * @param provider The service provider
	 * @param signature Digital signature of <b>serviceId</b> and <b>all file names</b>,
	 * created with provider's private key
	 * @param files List of files to be shared. All the files are associated to the given
	 * service and will not be shared for other services.
	 */
	public Future<UrlBean> shareFiles(URI serviceId, IIdentity provider, String signature, List<String> files);
}
