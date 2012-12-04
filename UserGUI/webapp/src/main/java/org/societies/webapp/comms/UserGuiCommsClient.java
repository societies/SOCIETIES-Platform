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
package org.societies.webapp.comms;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.internal.schema.usergui.ComponentParameters;
import org.societies.api.internal.schema.usergui.ComponentType;
import org.societies.api.internal.schema.usergui.Method;
import org.societies.api.internal.schema.usergui.UserGuiBean;
import org.societies.api.internal.schema.usergui.UserGuiBeanResult;



/**
 * Comms Client that initiates the remote communication for the css discovery
 * 
 * @author Maria Mannion
 * 
 */
public class UserGuiCommsClient implements ICommCallback {
	
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/internal/schema/usergui"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.internal.schema.usergui"));


	private ICommManager commManager;
	private static Logger LOG = LoggerFactory.getLogger(UserGuiCommsClient.class);
	private UserGuiBeanResult beanResult;
	

	// PROPERTIES
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}


	public UserGuiCommsClient(ICommManager commManager) {
		LOG.info("UserGuiCommsClient Comstructor");
		setCommManager(commManager);
		
		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		LOG.info("UserGuiCommsClient Constructor end");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages()
	 */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces()
	 */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveError(org
	 * .societies.api.comm.xmpp.datatypes.Stanza,
	 * org.societies.api.comm.xmpp.exceptions.XMPPError)
	 */
	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveInfo(org.
	 * societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * org.societies.api.comm.xmpp.datatypes.XMPPInfo)
	 */
	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.String,
	 * java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveMessage(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveResult(org
	 * .societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public void receiveResult(Stanza arg0, Object msgBean) {
		// TODO Auto-generated method stub
		beanResult = (UserGuiBeanResult) msgBean;
		synchronized (this) {
            notifyAll( );
        }
		

	}


	public void informUserServer(String userid, String commsMgrId)
	{
		// We want to sent all messages for CssDirectory to the domain authority Node
		IIdentity toIdentity = null;
		try {
			toIdentity = getCommManager().getIdManager().fromJid(userid);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		Stanza stanza = new Stanza(toIdentity);

		// CREATE MESSAGE BEAN
		UserGuiBean msgBean = new UserGuiBean();
		msgBean.setTargetcomponent(ComponentType.USERGUI);
		ComponentParameters comParams = new ComponentParameters();
		comParams.setComponentmethod(Method.INFORM);
		List<String> params = new ArrayList<String>();
		params.add(commsMgrId);
		comParams.setStringList(params);
		msgBean.setTargetcomponentparameter(comParams);
		
		try {
			commManager.sendMessage(stanza, msgBean);
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
	}
		
	
	
	public void getMyCisList(String userid, UserGuiCommsResult callback)
	{
		LOG.info("UserGuiCommsClient getMyCisList called for" + userid);
		
		// We want to sent all messages for CssDirectory to the domain authority Node
		IIdentity toIdentity = null;
		
		try {
			toIdentity = getCommManager().getIdManager().fromJid(userid);
		} catch (InvalidFormatException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		catch( Exception e)
		{
			e.printStackTrace();
		}
		
		Stanza stanza = new Stanza(toIdentity);
		UserGuiCommsClientCallback userCallback = new UserGuiCommsClientCallback(stanza.getId(), callback);

		// CREATE MESSAGE BEAN
		UserGuiBean msgBean = new UserGuiBean();
		msgBean.setTargetcomponent(ComponentType.CISMANAGER);
		ComponentParameters comParams = new ComponentParameters();
		comParams.setComponentmethod(Method.GET_MY_CIS_LIST);
		msgBean.setTargetcomponentparameter(comParams);
		
		LOG.info("UserGuiCommsClient About to send message ");
		try {
			commManager.sendIQGet(stanza, msgBean, userCallback);
		} catch (CommunicationException e) {
			LOG.error(e.getMessage());
			e.printStackTrace();
		} catch (Exception e1) {
			LOG.error(e1.getMessage());
			e1.printStackTrace();
		};
	}
		
	public void getSuggestedCisList(String userid, UserGuiCommsResult callback)
	{
		LOG.info("UserGuiCommsClient getSuggestedCisList called for" + userid);
			
			// We want to sent all messages for CssDirectory to the domain authority Node
		IIdentity toIdentity = null;
			
		try {
				toIdentity = getCommManager().getIdManager().fromJid(userid);
			} catch (InvalidFormatException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			catch( Exception e)
			{
				e.printStackTrace();
			}
			
			Stanza stanza = new Stanza(toIdentity);
			UserGuiCommsClientCallback userCallback = new UserGuiCommsClientCallback(stanza.getId(), callback);

			// CREATE MESSAGE BEAN
			UserGuiBean msgBean = new UserGuiBean();
			msgBean.setTargetcomponent(ComponentType.CISMANAGER);
			ComponentParameters comParams = new ComponentParameters();
			comParams.setComponentmethod(Method.GET_SUGGESTED_CIS_LIST);
			msgBean.setTargetcomponentparameter(comParams);
			
			LOG.info("UserGuiCommsClient About to send message ");
			try {
				commManager.sendIQGet(stanza, msgBean, userCallback);
			} catch (CommunicationException e) {
				LOG.error(e.getMessage());
				e.printStackTrace();
			} catch (Exception e1) {
				LOG.error(e1.getMessage());
				e1.printStackTrace();
			};

			
	}

}
