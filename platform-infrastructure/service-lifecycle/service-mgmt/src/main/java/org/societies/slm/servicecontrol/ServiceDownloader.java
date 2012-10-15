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
package org.societies.slm.servicecontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.schema.servicelifecycle.model.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class ServiceDownloader {

	/** Size of buffer */
	private static final int BUFFER_SIZE = 1024 * 1024;
	
	private static final String CLIENTFOLDER = "3p-client";
	
	private static byte[] bytes = new byte[BUFFER_SIZE];  

	static final Logger logger = LoggerFactory.getLogger(ServiceDownloader.class);
	
	public static boolean deleteFile(String path){
		
		File file = new File(path);
		
		return file.delete();
	}
	
	public static String downloadServiceJar(URL jarURL, Service service){
		
		String serviceName = service.getServiceName().replace(' ', '-');
		String version = service.getServiceInstance().getServiceImpl().getServiceVersion();
		String fileName = serviceName+"-"+version+"-client.jar";
		String filePath = CLIENTFOLDER+"/"+fileName;
		
		if(logger.isDebugEnabled())
			logger.debug("Trying to download " + jarURL.toString() + " to " + filePath);
		
		try {
			File downloadedClient = writeFile(jarURL.openStream(),filePath);
			
			if(logger.isDebugEnabled())
				logger.debug("Jar downloaded to path: " + downloadedClient.getAbsolutePath());
			
			return downloadedClient.getAbsolutePath();
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	 * Write to file
	 * 
	 * @param is The source to read file contents from. The content will be written to given file.
	 * @param path File name, including path
	 * @throws IOException
	 */
	private static File writeFile(InputStream is, String path) throws IOException {

		File file;
		File directory;
		
		file = new File(path);
		directory = file.getParentFile();
		
		if (directory != null) {
			if(logger.isDebugEnabled())
				logger.debug("folder " + directory + " doesn't exist yet, creating it...");
			directory.mkdirs();
		}
		
		// Create the byte array to hold the data
		FileOutputStream os = new FileOutputStream(file);

		// Read in the bytes and write on the fly
		int numRead = 0;
		long totalRead = 0;
		while ((bytes.length > 0) && (numRead = is.read(bytes, 0, bytes.length)) >= 0) {
			os.write(bytes, 0, numRead);
			totalRead += numRead;
		}

		// Close input and output streams
		is.close();
		os.close();
		
		return file;

	}
}