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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.StanzaError;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.schema.sns.socialdata.SocialdataMessageBean;
import org.societies.api.internal.schema.sns.socialdata.SocialdataResultBean;
import org.societies.api.internal.sns.ISocialConnector.SocialNetwork;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.platform.socialdata.utils.SocialDataCommsUtils;

/**
 * Describe your class here...
 *
 * @author Edgar Domingues (PTIN)
 *
 */
public class SocialDataServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
				  Arrays.asList("http://societies.org/api/internal/schema/sns/socialdata"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
				  Arrays.asList("org.societies.api.internal.schema.sns.socialdata"));
	
	private static Logger LOG = LoggerFactory.getLogger(SocialDataServer.class);
	
	private ICommManager commManager;
	private ISocialData socialData;
	
	public ICommManager getCommManager() {
		return commManager;
	}
	
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
		
	public ISocialData getSocialData() {
		return socialData;
	}

	public void setSocialData(ISocialData socialData) {
		this.socialData = socialData;
	}

	public void InitService() {
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {

	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		return null;
	}

	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		if(payload instanceof SocialdataMessageBean)
			return setQuery(stanza, (SocialdataMessageBean)payload);
		return null;
	}
	
	private Object setQuery(Stanza stanza, SocialdataMessageBean messageBean) throws XMPPError {		
		SocialdataResultBean resultBean = new SocialdataResultBean();
		
		switch(messageBean.getMethod()) {
		case ADD_CONNECTOR:
			SocialNetwork socialNetwork = SocialDataCommsUtils.socialNetwork(messageBean.getSnName());
			long validity = messageBean.getValidity(); // TODO if 0 remove connector
			Map<String, String> params = new HashMap<String, String>();
			params.put(ISocialConnector.AUTH_TOKEN, messageBean.getToken());
			try {
				ISocialConnector connector = socialData.createConnector(socialNetwork, params);
				connector.setTokenExpiration(validity);
				socialData.addSocialConnector(connector);				
				resultBean.setId(connector.getID());
			} catch (Exception e) {
				LOG.error(e.getMessage(), e);
				throw new XMPPError(StanzaError.internal_server_error, e.getMessage());
			}
			break;
		}
		return resultBean;
	}

}
