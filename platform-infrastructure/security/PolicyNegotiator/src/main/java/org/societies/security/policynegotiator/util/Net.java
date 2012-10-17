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
package org.societies.security.policynegotiator.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class Net {

	private static Logger LOG = LoggerFactory.getLogger(Net.class);

	private URL source;
	
	public Net(URL source) {
		this.source = source;
	}
	
	public boolean download(String fileName) {
		
		LOG.debug("download({})", fileName);
		
		long startTime = System.currentTimeMillis();
		
		try {
			source.openConnection();
			InputStream reader = source.openStream();
			FileOutputStream writer = new FileOutputStream(fileName);
			byte[] buffer = new byte[153600];
			int totalBytesRead = 0;
			int bytesRead = 0;

			LOG.debug("Reading file {} 150KB blocks at a time.", source);

			while ((bytesRead = reader.read(buffer)) > 0)
			{  
				writer.write(buffer, 0, bytesRead);
				buffer = new byte[153600];
				totalBytesRead += bytesRead;
			}

			long endTime = System.currentTimeMillis();

			LOG.info("File " + source + " downloaded. " + (new Integer(totalBytesRead).toString()) +
					" bytes read (" + (new Long(endTime - startTime).toString()) + " ms).");
			writer.close();
			reader.close();
		} catch (IOException e) {
			LOG.warn("download(): " + source, e);
			return false;
		}
		return true;
	}
	
	public boolean post(String fileName, URI destination) {

		LOG.debug("post({}, {})", fileName, destination);

        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpPost httppost = new HttpPost(destination);

            FileBody bin = new FileBody(new File(fileName));
            StringBody comment = new StringBody("A binary file of some kind");

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("bin", bin);
            reqEntity.addPart("comment", comment);

            httppost.setEntity(reqEntity);

            LOG.debug("Executing request {}", httppost.getRequestLine());
            HttpResponse response = httpclient.execute(httppost);
            HttpEntity resEntity = response.getEntity();

            LOG.debug("Status: {}", response.getStatusLine().toString());
            if (resEntity != null) {
            	LOG.debug("Response content length: " + resEntity.getContentLength());
            }
            EntityUtils.consume(resEntity);
        } catch (IOException e) {
        	LOG.warn("post(): " + fileName, e);
        	return false;
        } finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
        }
        return true;
	}
	
	public boolean put(String fileName, URI destination) {

		LOG.debug("put({}, {})", fileName, destination);

        HttpClient httpclient = new DefaultHttpClient();

        try {
            HttpPut httpput = new HttpPut(destination);

            FileBody bin = new FileBody(new File(fileName));
            StringBody comment = new StringBody("A binary file of some kind");

            MultipartEntity reqEntity = new MultipartEntity();
            reqEntity.addPart("bin", bin);
            reqEntity.addPart("comment", comment);

            httpput.setEntity(reqEntity);

            LOG.debug("Executing request {}", httpput.getRequestLine());
            HttpResponse response = httpclient.execute(httpput);
            HttpEntity resEntity = response.getEntity();

            LOG.debug("Status: {}", response.getStatusLine().toString());
            if (resEntity != null) {
            	LOG.debug("Response content length: " + resEntity.getContentLength());
            }
            EntityUtils.consume(resEntity);
        } catch (IOException e) {
        	LOG.warn("put(): " + fileName, e);
        	return false;
        } finally {
			try {
				httpclient.getConnectionManager().shutdown();
			} catch (Exception e) {
			}
        }
        return true;
	}
	
	public void downloadAndPost(URI destination) {
		// TODO
	}
}
