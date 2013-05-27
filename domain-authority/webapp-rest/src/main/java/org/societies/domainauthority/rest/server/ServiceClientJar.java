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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.domainauthority.LocalPath;
import org.societies.api.internal.domainauthority.UrlPath;
import org.societies.api.security.digsig.DigsigException;
import org.societies.domainauthority.rest.control.ServiceClientJarAccess;
import org.societies.domainauthority.rest.util.FileName;
import org.societies.domainauthority.rest.util.Files;
import org.societies.domainauthority.rest.util.UrlParamName;

/**
 * Class for hosting jar files for clients of 3rd party services.
 * 
 * @author Mitja Vardjan
 */
@Path(UrlPath.PATH_FILES)
public class ServiceClientJar extends HttpServlet {

	private static final long serialVersionUID = 4625772782444356957L;

	private static Logger LOG = LoggerFactory.getLogger(ServiceClientJar.class);

	public ServiceClientJar() {
		LOG.info("Constructor");
	}

	/**
	 * Method processing HTTP GET requests, producing "application/java-archive" MIME media type.
	 * HTTP response: the requested file, e.g., service client in form of jar file.
	 * Error 401 if file name or signature not valid.
	 * Error 500 on server error.
	 */
//	@Path("{name}")
//	@GET
//	@Produces("application/java-archive")
//	public byte[] doGet(@PathParam("name") String name,
//			@QueryParam(UrlPath.URL_PARAM_FILE) String path,
//			@QueryParam(UrlPath.URL_PARAM_SERVICE_ID) String serviceId,
//			@QueryParam(UrlPath.URL_PARAM_SIGNATURE) String signature) {
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {

		//String path = name + ".jar";
		if (request.getPathInfo() == null) {
			LOG.warn("HTTP GET: request.getPathInfo() is null");
			return;
		}
		String[] name = request.getPathInfo().split("/");
		String path = request.getParameter(UrlPath.URL_PARAM_FILE);
		String serviceId = request.getParameter(UrlPath.URL_PARAM_SERVICE_ID);
		String signature = request.getParameter(UrlPath.URL_PARAM_SIGNATURE);
		
		LOG.info("HTTP GET: path = {}, service ID = {}, signature = " + signature, path, serviceId);

		byte[] file;

		if (!ServiceClientJarAccess.isAuthorized(path, signature)) {
			LOG.warn("Invalid filename or key");
			// Return HTTP status code 401 - Unauthorized
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			return;
			//throw new WebApplicationException(HttpServletResponse.SC_UNAUTHORIZED);
		}

		try {
			file = Files.getBytesFromFile(get3PServicePath(serviceId) + path);
		} catch (FileNotFoundException e) {
			try {
				file = Files.getBytesFromFile(path);
			} catch (IOException e2) {
				LOG.warn("Could not open file {}", path, e2);
				// Return HTTP status code 500 - Internal Server Error
				response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				return;
//				throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			}
		} catch (IOException e) {
			LOG.warn("Could not open file {}", path, e);
			// Return HTTP status code 500 - Internal Server Error
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
//			throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}

		LOG.info("Serving {}", path);
		
		response.setContentLength(file.length);
		//response.setContentType("application/java-archive");
		try {
			ServletOutputStream stream = response.getOutputStream();
			stream.write(file);
			stream.flush();
		} catch (IOException e) {
			LOG.warn("Could not write response", e);
			response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
			return;
//			throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * Method processing HTTP POST requests.
	 */
//	@Path("{name}")
//	@POST
//	public void postIt(@PathParam("name") String name,
//			InputStream is,
//			@Context HttpServletRequest request,
//			@QueryParam(UrlPath.URL_PARAM_FILE) String path,
//			@QueryParam(UrlPath.URL_PARAM_SERVICE_ID) String serviceId,
//			@QueryParam(UrlPath.URL_PARAM_PUB_KEY) String pubKey) {
	@Override
	public void doPost(HttpServletRequest request, HttpServletResponse response) {

		String path = request.getParameter(UrlPath.URL_PARAM_FILE);
		String serviceId = request.getParameter(UrlPath.URL_PARAM_SERVICE_ID);
		String pubKey = request.getParameter(UrlPath.URL_PARAM_PUB_KEY);
	
		
		LOG.info("HTTP POST from {}; path = {}, service ID = " + serviceId + ", pubKey = " + pubKey,
				request.getRemoteHost(), path);
		LOG.warn("HTTP POST is not implemented. For uploading files, use HTTP PUT instead.");
	}

	/**
	 * Method processing HTTP PUT requests.
	 */
//	@Path("{name}")
//	@PUT
//	public void puIt(@PathParam("name") String name,
//			InputStream is,
//			@Context HttpServletRequest request,
//			@QueryParam(UrlPath.URL_PARAM_FILE) String path,
//			@QueryParam(UrlPath.URL_PARAM_SERVICE_ID) String serviceId,
//			@QueryParam(UrlPath.URL_PARAM_PUB_KEY) String cert) {
	@Override
	public void doPut(HttpServletRequest request, HttpServletResponse response) {
		
		String path = request.getParameter(UrlPath.URL_PARAM_FILE);
		String serviceId = request.getParameter(UrlPath.URL_PARAM_SERVICE_ID);
		String cert = request.getParameter(UrlPath.URL_PARAM_PUB_KEY);

		LOG.info("HTTP PUT from {}; path = {}, service ID = " + serviceId + ", pubKey = " + cert,
				request.getRemoteHost(), path);

		cert = UrlParamName.url2Base64(cert);
		LOG.debug("HTTP PUT: cert fixed to {}", cert);

		// Create a factory for disk-based file items
		FileItemFactory factory = new DiskFileItemFactory();

		// Create a new file upload handler
		ServletFileUpload upload = new ServletFileUpload(factory);

		// Parse the request
		List<FileItem> items;
		try {
			items = upload.parseRequest(request);
		} catch (FileUploadException e) {
			response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
			return;
//			throw new WebApplicationException(HttpServletResponse.SC_BAD_REQUEST);
		}

		// Process the uploaded items
		Iterator iter = items.iterator();
		while (iter.hasNext()) {
			FileItem item = (FileItem) iter.next();

			if (item.isFormField()) {
				// Process FormField;
			} else {
				// Process Uploaded File
				//path = path.replaceAll("[/\\\\]", File.separator);
				path = get3PServicePath(serviceId) + path;
				LOG.debug("Saving to file {}", path);
				try {
					Files.writeFile(item.getInputStream(), path);
					ServiceClientJarAccess.addResource(path, cert);
				} catch (IOException e) {
					LOG.warn("Could not write to file {}", path, e);
					// Return HTTP status code 500 - Internal Server Error
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
//					throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				} catch (DigsigException e) {
					LOG.warn("Could not store public key", e);
					// Return HTTP status code 500 - Internal Server Error
					response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					return;
//					throw new WebApplicationException(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				}
			}
		}
	}

	private String get3PServicePath(String serviceId) {

		serviceId = FileName.removeUnsupportedChars(serviceId);

		return LocalPath.PATH_3P_SERVICES + File.separator + serviceId + File.separator;
	}
}
