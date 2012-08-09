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
package org.societies.domainauthority.rest.server;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.domainauthority.rest.control.ServiceClientJarAccess;
import org.societies.domainauthority.rest.util.Files;

/**
 * Class for hosting jar files for clients of 3rd party services.
 * 
 * @author Mitja Vardjan
 */
@Path(UrlPath.PATH)
public class ServiceClientJar {
    
	private static Logger LOG = LoggerFactory.getLogger(ServiceClientJar.class);
	
	public ServiceClientJar() {
		LOG.info("Constructor");
	}
	
	/**
     * Method processing HTTP GET requests, producing "application/java-archive" MIME media type.
     * 
     * @return Service client in form of jar file.
     * Error 401 if file or key not valid.
     * Error 500 on server error.
     */
	@Path("{name}.jar")
    @GET
    @Produces("application/java-archive")
    public byte[] getJar(@PathParam("name") String name,
    		@QueryParam(UrlPath.URL_PARAM_SERVICE_ID) String serviceId,
    		@QueryParam(UrlPath.URL_PARAM_SIGNATURE) String signature) {

		String path = name + ".jar";

		LOG.debug("HTTP GET: path = {}, service ID = {}, signature = " + signature, path, serviceId);
		
		byte[] file;
		
		if (!ServiceClientJarAccess.isAuthorized(path, signature)) {
			LOG.warn("Invalid filename or key");
			// Return HTTP status code 401 - Unauthorized
			throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
		}
		
		try {
			file = Files.getBytesFromFile(path);
		} catch (IOException e) {
			LOG.warn("Could not open file {}", path, e);
			// Return HTTP status code 500 - Internal Server Error
			throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
		
		LOG.info("Serving {}", path);
		return file;
    }

	/**
     * Method processing HTTP POST requests.
     */
	@Path("{name}.jar")
    @POST
    public void postIt(@PathParam("name") String name,
    		InputStream is,
    		@Context HttpServletRequest request,
    		@QueryParam(UrlPath.URL_PARAM_SERVICE_ID) String serviceId,
    		@QueryParam(UrlPath.URL_PARAM_SIGNATURE) String signature) {

		LOG.debug("HTTP POST: name = {}, service ID = {}, signature = " + signature, name, serviceId);
		
		String path = name + ".jar";
		
		// TODO: verify signature, authorization
		
		try {
			//Files.writeFile(is, request.getContentLength(), path);
			Files.writeFile(is, path);
		} catch (IOException e) {
			LOG.warn("Could not write to file {}", path, e);
			// Return HTTP status code 500 - Internal Server Error
			throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
    }
}
