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
package org.societies.platform.servicelifecycle.servicecontrol;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.eclipse.jetty.util.log.Log;
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
	
	private static byte[] bytes = new byte[BUFFER_SIZE];  

	static final Logger logger = LoggerFactory.getLogger(ServiceDownloader.class);
	
	public static void deleteFile(URI path){
		
		logger.debug("Attempting to delete file with path: {}",path);
		
		if(path == null)
			return;
		
		File file = new File(path);
		if(file.isFile()){
			boolean delete = file.delete();
			if(!delete){
				logger.debug("First delete attempt of {} failed, so asking Java to delete on JVM exit!",file.getName());
				file.deleteOnExit();
			}		
		}
	}
	
	public static URI downloadClientJar(URL jarURL, Service service, String user, String serviceDir){
		
		String serviceName = service.getServiceName().replace(' ', '-').toLowerCase();
		String version = service.getServiceInstance().getServiceImpl().getServiceVersion();
		
		StringBuilder filePathBuilder = new StringBuilder();	
		filePathBuilder.append(serviceDir).append('/').append(user).append('/').append(serviceName).append(version).append("-client");
		
		// Is it a war or a jar?
		if(jarURL.getFile().contains(".war"))
			filePathBuilder.append(".war");
		else
			filePathBuilder.append(".jar");
		
		String filePath = filePathBuilder.toString();
		
		logger.debug("Trying to download {} to {}",jarURL.toString(),filePath);
		
		try {
			File downloadedClient = writeFile(jarURL.openStream(),filePath);
			
			logger.debug("Jar downloaded to path: {}", downloadedClient.getAbsolutePath());
			
			return downloadedClient.toURI();
			
		} catch (IOException e) {
			logger.error("Exception occured while trying to download Client Jar: {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static URI downloadServerJar(URL jarURL, String user, String serviceDir){
		
		String remoteFileName = jarURL.getFile();

		StringBuilder filePathBuilder = new StringBuilder();	
		filePathBuilder.append(serviceDir).append('/').append(user).append('/');
		
		if(jarURL.getProtocol().toLowerCase().equals("file")){
			filePathBuilder.append(remoteFileName.substring(remoteFileName.lastIndexOf('/')+1));
		} else
			filePathBuilder.append(remoteFileName);
		
		
		String filePath = filePathBuilder.toString();		
		logger.debug("Trying to download {} to {}",jarURL.toString(),filePath);;
		
		try {
			File downloadedClient = writeFile(jarURL.openStream(),filePath);
			
			logger.debug("Jar downloaded to path: {}", downloadedClient.getAbsolutePath());
			
			return downloadedClient.toURI();
			
		} catch (IOException e) {
			logger.error("Exception occured while trying to download Service Jar: {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static URI downloadServerJar(InputStream is, String fileName, String user, String serviceDir){
		
		StringBuilder filePathBuilder = new StringBuilder();	
		filePathBuilder.append(serviceDir).append('/').append(user).append('/').append(fileName);
		
		String filePath = filePathBuilder.toString();	
		logger.debug("Trying to download {} inputStream to {}",fileName,filePath);
		
		try {
			File downloadedClient = writeFile(is,filePath);
				
			logger.debug("Jar downloaded to path: {}", downloadedClient.getAbsolutePath());
			
			return downloadedClient.toURI();
			
		} catch (IOException e) {
			logger.error("Exception occured while trying to download Service Jar: {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static URI storeClient(InputStream is, String fileName, String bundleName, String user, String serviceDir){
		
		StringBuilder filePathBuilder = new StringBuilder();
		filePathBuilder.append(serviceDir).append('/').append(user).append('/').append("clientStorage");
		filePathBuilder.append('/').append(bundleName.replace('.','-')).append('/').append(fileName);
		
		String filePath = filePathBuilder.toString();	
		logger.debug("Trying to download {} inputStream to {}",fileName,filePath);
		
		try {
			File downloadedClient = writeFile(is,filePath);
				
			logger.debug("Client jar stored in path: {}", downloadedClient.getAbsolutePath());
			
			return downloadedClient.toURI();
			
		} catch (IOException e) {
			logger.error("Exception occured while trying to download Service Jar: {}",e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static void cleanClients(String bundleName, String user, String serviceDir){
		
		StringBuilder filePathBuilder = new StringBuilder();
		filePathBuilder.append(serviceDir).append('/').append(user).append('/').append("clientStorage");
		filePathBuilder.append('/').append(bundleName.replace('.','-'));
		
		String filePath = filePathBuilder.toString();	
		logger.debug("Trying to clean client storage for {}, at {}",bundleName,filePath);
		
		try {
			File directory = new File(filePath);
			
			if(!directory.exists()){
				logger.debug("{} does not have a client storage to delete.",bundleName);
				return;
			}
			
			File[] clientFiles = directory.listFiles();
			
			if (clientFiles != null){
				for(int i = 0; i < clientFiles.length; i++){
					
					File file = clientFiles[i];
					logger.debug("Trying to delete {}", file.getName());
					if(file.isFile()){
						boolean delete = file.delete();
						if(!delete){
							logger.debug("First delete attempt of {} failed, so asking Java to delete on JVM exit!",file.getName());
							file.deleteOnExit();
						}		
					}
				}
				
			} else{
				logger.debug("{} is not a directory and we couldn't find files!",filePath);
			}
			
			logger.debug("Now we need to try to delete directory itself: {}",filePath);
			
			boolean deleteFolder = directory.delete();
			
			if(!deleteFolder){
				logger.debug("First delete attempt of {} failed, so asking Java to delete on JVM exit!",directory.getName());
				directory.deleteOnExit();
			}	
			
		} catch(Exception e){
			logger.error("Exception occured while trying to download Service Jar: {}",e.getMessage());
			e.printStackTrace();
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
		
		if (!directory.isDirectory()) {
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