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

import java.util.ArrayList;
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
import org.societies.api.internal.schema.sns.socialdata.ConnectorBean;
import org.societies.api.internal.schema.sns.socialdata.ConnectorsList;
import org.societies.api.internal.schema.sns.socialdata.SocialdataMessageBean;
import org.societies.api.internal.schema.sns.socialdata.SocialdataResultBean;
import org.societies.api.internal.sns.ISocialConnector;
import org.societies.api.internal.sns.ISocialData;
import org.societies.api.schema.sns.socialdata.model.SocialNetwork;


/**
 * Comms manager server for SociaData bundle.
 * Receives comms messages and calls the corresponding socialdata bundle methods. 
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
		if(payload instanceof SocialdataMessageBean)
			return getQuery(stanza, (SocialdataMessageBean)payload);

		throw new XMPPError(StanzaError.bad_request);
	}

	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		if(payload instanceof SocialdataMessageBean)
			return setQuery(stanza, (SocialdataMessageBean)payload);

		throw new XMPPError(StanzaError.bad_request);
	}
	
	private Object getQuery(Stanza stanza, SocialdataMessageBean messageBean) throws XMPPError {
		validateMessageBean(messageBean);
		
		try {
		
			SocialdataResultBean resultBean = new SocialdataResultBean();
			
			switch(messageBean.getMethod()) {	
			case GET_CONNECTOR_LIST:
				List<ISocialConnector> connectors = socialData.getSocialConnectors();
						
				List<ConnectorBean> connectorBeanList = new ArrayList<ConnectorBean>(connectors.size());
				
				for(ISocialConnector connector : connectors)  {
					connectorBeanList.add(SocialNetworkUtils.convertSocialConnectorToBean(connector));
				
				}
				ConnectorsList connectorsList = new ConnectorsList();				
				connectorsList.setConnectorBean(connectorBeanList);
				
				resultBean.setConnectorsList(connectorsList);				
			
				break;
			default:
				throw new XMPPError(StanzaError.bad_request);
			}
			return resultBean;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, e.getMessage());
		}
	}
	
	private Object setQuery(Stanza stanza, SocialdataMessageBean messageBean) throws XMPPError {
		validateMessageBean(messageBean);
		
		try {
		
			SocialdataResultBean resultBean = new SocialdataResultBean();
			
			switch(messageBean.getMethod()) {
			case ADD_CONNECTOR:
			    
	
				SocialNetwork socialNetwork = messageBean.getSnName();
				
				long validity = messageBean.getValidity(); // TODO if 0 remove connector
				Map<String, String> params = new HashMap<String, String>();
				params.put(ISocialConnector.AUTH_TOKEN, messageBean.getToken());
				params.put(ISocialConnector.IDENTITY,   messageBean.getIdentity());

				ISocialConnector connector = socialData.createConnector(socialNetwork, params);
				connector.setTokenExpiration(validity);
				
				socialData.addSocialConnector(connector);			
				
				resultBean.setId(connector.getID());

				break;
			case REMOVE_CONNECTOR:
				socialData.removeSocialConnector(messageBean.getId());
				break;
			default:
				throw new XMPPError(StanzaError.bad_request);
			}
			return resultBean;
			
		} catch (Exception e) {
			LOG.error(e.getMessage(), e);
			throw new XMPPError(StanzaError.internal_server_error, e.getMessage());
		}
	}
	
	private void validateMessageBean(SocialdataMessageBean messageBean) throws XMPPError {
		switch(messageBean.getMethod()) {
		case ADD_CONNECTOR:
			if(messageBean.getSnName() == null
			|| messageBean.getValidity() == null
			|| messageBean.getToken() == null)
				throw new XMPPError(StanzaError.bad_request);
			break;
		case REMOVE_CONNECTOR:
			if(messageBean.getId() == null)
				throw new XMPPError(StanzaError.bad_request);
			break;
		}
	}
}
