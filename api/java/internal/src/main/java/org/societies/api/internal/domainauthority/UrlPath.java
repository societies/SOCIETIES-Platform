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

import java.net.URLEncoder;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class UrlPath {
	
	/**
	 * Encoding for URL parameters. To be used with {@link URLEncoder#encode(String, String)}
	 */
	public static final String ENCODING = "UTF8";

	public static final String BASE = "/rest";
	
	/**
	 * URL parameter. File name, including relative path.
	 */
	public static final String URL_PARAM_FILE = "file";
	
	/**
	 * URL parameter. Digital signature of the uploader of the file (usually the provider).
	 */
	public static final String URL_PARAM_SIGNATURE = "sig";

	/**
	 * URL parameter. Operation to perform. Valid values:<br>
	 * - getfile (default)<br>
	 * - status<br>
	 */
	public static final String URL_PARAM_OPERATION = "operation";
	
	/**
	 * URL parameter. Digital certificate of the uploader of the file (usually the provider).
	 * Should include only the public key.
	 */
	public static final String URL_PARAM_CERT = "cert";
	
	/**
	 * URL parameter. ID of the service, not a service instance.
	 */
	public static final String URL_PARAM_SERVICE_ID = "service";
	
	/**
	 * URL parameter. Endpoint for notifying the uploader about future events, e.g. when the resource is modified.
	 * Supported protocol is HTTP. On event, a HTTP GET is performed on the given endpoint (HTTP URL).
	 */
	public static final String URL_PARAM_NOTIFICATION_ENDPOINT = "endpoint";
	
	/**
	 * URL parameter. Minimal number of signatures (threshold) for notifying the uploader about future sign events.
	 */
	public static final String URL_PARAM_NUM_SIGNERS_THRESHOLD = "minnumsig";

	/**
	 * Path for servlet that serves files.
	 */
	public static final String PATH_FILES = "/serviceclient";
	
	/**
	 * Path for servlet that serves xml documents.
	 */
	public static final String PATH_XML_DOCUMENTS = "/xmldocs";
}
