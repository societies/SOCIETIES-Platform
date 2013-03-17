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
package org.societies.platform.socialdata;

import java.util.Map;

import org.societies.api.internal.schema.sns.socialdata.ConnectorBean;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;
import org.societies.api.internal.sns.ISocialConnector;

/**
 * Social connector implemented as a data transfer object 
 * to store static data transfered using comms. 
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class SocialConnectorDTO implements ISocialConnector {

	private String id, token, name;
	private long expires;
	
	public SocialConnectorDTO(String id) {
		this.id = id;
	}
	
	@Override
	public String getID() {
		return id;
	}

	@Override
	public void setToken(String access_token) {
		token = access_token;
	}

	@Override
	public void setTokenExpiration(long expires) {
		this.expires = expires;
	}

	@Override
	public long getTokenExpiration() {
		return expires;
	}

	@Override
	public String getToken() {
		return token;
	}

	@Override
	public void setConnectorName(String name) {
		this.name = name;
	}

	@Override
	public String getConnectorName() {
		return name;
	}

	@Override
	public String getSocialData(String path) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public Map<String, String> requireAccessToken() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void disconnect() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void setMaxPostLimit(int postLimit) {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void setParameter(String key, String value) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	@Override
	public void resetParameters() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public String getUserProfile() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public String getUserFriends() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public String getUserActivities() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public String getUserGroups() {
		throw new UnsupportedOperationException("Not implemented.");
	}

	@Override
	public void post(String activityEntry) {
		throw new UnsupportedOperationException("Not implemented.");
	}
	
	public static ISocialConnector createFromBean(ConnectorBean bean) {
		ISocialConnector socialConnector = new SocialConnectorDTO(bean.getId());
		socialConnector.setConnectorName(bean.getName());
		socialConnector.setToken(bean.getToken());
		socialConnector.setTokenExpiration(bean.getExpires());
		return socialConnector;
	}

	@Override
	public SocialNetwork getSocialNetwork() {
	    return SocialNetwork.fromValue(this.name);
	}	
}
