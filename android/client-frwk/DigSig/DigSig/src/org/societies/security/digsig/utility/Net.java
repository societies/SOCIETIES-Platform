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
package org.societies.security.digsig.utility;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import android.util.Log;

/**
 * HTTP methods
 *
 * @author Mitja Vardjan
 *
 */
public class Net {

	private static final String TAG = Net.class.getSimpleName();
	
	private URI uri;
	
	public Net(URI uri) {
		Log.d(TAG, "Constructor: " + uri);
		this.uri = uri;
	}
	
	public boolean get(OutputStream os) {
		
		Log.d(TAG, "download()");
		
		long startTime = System.currentTimeMillis();
		
		try {
			uri.toURL().openConnection();
			InputStream reader = uri.toURL().openStream();
			byte[] buffer = new byte[153600];
			int totalBytesRead = 0;
			int bytesRead = 0;

			if (os != null) {
				while ((bytesRead = reader.read(buffer)) > 0)
				{  
					os.write(buffer, 0, bytesRead);
					buffer = new byte[153600];
					totalBytesRead += bytesRead;
				}
			}

			long endTime = System.currentTimeMillis();

			Log.i(TAG, "File " + uri + " downloaded. " + String.valueOf(totalBytesRead) +
					" bytes read (" + String.valueOf(endTime - startTime) + " ms).");
			reader.close();
		} catch (IOException e) {
			Log.w(TAG, "download(): " + uri, e);
			return false;
		}
		return true;
	}
	
	/**
	 * Perform HTTP GET without downloading the resource
	 * @return true for success, false for error
	 */
	public boolean get() {
		return get((OutputStream) null);
	}
	
	/**
	 * Perform HTTP GET
	 * @return the downloaded resource
	 */
	public byte[] getByteArray() {
		
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		
		get(os);
		return os.toByteArray();
	}
	
	/**
	 * Perform HTTP GET
	 * @return the downloaded resource
	 */
	public String getString() {
		byte[] ba = getByteArray();
		try {
			String s = new String(ba, "UTF-8");
			return s;
		} catch (UnsupportedEncodingException e) {
			Log.w(TAG, e);
			return null;
		}
	}
	
	/**
	 * Perform a HTTP PUT
	 * 
	 * @param contents The contents to write. Keep it short.
	 * 
	 * @return True for success, false for error
	 */
	public boolean put(String contents) {
		Log.d(TAG, "put()");
		HttpPut put = new HttpPut(uri);
		try {
			StringEntity entity = new StringEntity(contents, "UTF-8");
			entity.setContentType("text/xml; charset=UTF-8");
			put.setEntity(entity);
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse response = httpclient.execute(put);
			boolean success = 200 == response.getStatusLine().getStatusCode();
			Log.d(TAG, "put to " + uri + " finished, success = " + success);
			return success;
		} catch (Exception e) {
			Log.w(TAG, e);
			return false;
		}
	}
	
	/**
	 * Perform a HTTP POST
	 * 
	 * @param contents The contents to write. Keep it short.
	 * 
	 * @return True for success, false for error
	 */
	public boolean post(List<NameValuePair> parameters) {

		Log.d(TAG, "post()");

		HttpPost post;
		try {
			post = new HttpPost(uri);
			post.setEntity(new UrlEncodedFormEntity(parameters));
			HttpClient httpclient = new DefaultHttpClient();

			HttpResponse response = httpclient.execute(post);
			int status = response.getStatusLine().getStatusCode();
			Log.v(TAG, "status = " + status);
			boolean success = (200 == status);
			Log.d(TAG, "post to " + uri + " finished, success = " + success);
			return success;
		} catch (Exception e) {
			Log.w(TAG, e);
			return false;
		}
	}
}
