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
package org.societies.impl;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

import android.util.Log;

/**
 * Android Agent XMPP Configuration
 *
 * @author Edgar Domingues (PTIN)
 *
 */
class Configuration {
	
	private static final String LOG_TAG = Configuration.class.getName();
	private ResourceBundle configutationBundle;
	
	public Configuration(ResourceBundle configutationBundle) {
		Log.d(LOG_TAG, "Configuration constructor");
		this.configutationBundle = configutationBundle;
	}
	
	public String getServer() {
		Log.d(LOG_TAG, "getServer server: " + configutationBundle.getString("server"));
		return configutationBundle.getString("server");
	}
	
	public int getPort() {
		Log.d(LOG_TAG, "getPort port: " + Integer.parseInt(configutationBundle.getString("port")));
		return Integer.parseInt(configutationBundle.getString("port"));
	}
	
	public String getUsername() {
		Log.d(LOG_TAG, "getUsername username: " + configutationBundle.getString("username"));
		return configutationBundle.getString("username");
	}
	
	public String getPassword() {
		Log.d(LOG_TAG, "getPassword password: " + configutationBundle.getString("password"));
		return configutationBundle.getString("password");
	}
	
	public String getResource() {
		Log.d(LOG_TAG, "getResource resource: " + configutationBundle.getString("resource"));
		return configutationBundle.getString("resource");
	}
	
	public boolean getDebug() {
		Log.d(LOG_TAG, "getDebug");
		boolean debug;
		try {
			debug = configutationBundle.getString("debug").equalsIgnoreCase("true");
		} catch(MissingResourceException e) {
			debug = false;
		}
		return debug;
	}
	
	public String getDomainAuthorityNode() {
		String daNode = null;
		try {
			daNode = configutationBundle.getString("daNode");
		} catch(MissingResourceException e) {
		}
		return daNode;
	}
}
