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

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
package org.societies.userguiserver;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.schema.usergui.ComponentParameters;
import org.societies.api.internal.schema.usergui.UserGuiBean;
import org.societies.api.internal.schema.usergui.UserGuiBeanResult;
import org.societies.userguiserver.handler.CisManagerHandler;
import org.societies.userguiserver.handler.CssManagerHandler;
import org.societies.userguiserver.handler.UserGuiHandler;





public class UserGuiCommsServer implements IFeatureServer {

	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/internal/schema/userguiserver"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.internal.schema.userguiserver"));
	
	
	private ICommManager commManager;
	
	

	private CisManagerHandler cisManagerHandler;
	private CssManagerHandler cssManagerHandler;
	
	
	private static Logger LOG = LoggerFactory.getLogger(UserGuiCommsServer.class);

	private String currentRegisteredCommId;
	
	public ICommManager getCommManager() {
		return commManager;
	}

	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}
	
	
	
	
	
	


	/**
	 * @return the cisManagerHandler
	 */
	public CisManagerHandler getCisManagerHandler() {
		return cisManagerHandler;
	}

	/**
	 * @param cisManagerHandler the cisManagerHandler to set
	 */
	public void setCisManagerHandler(CisManagerHandler cisManagerHandler) {
		this.cisManagerHandler = cisManagerHandler;
	}

	/**
	 * @return the cssManagerHandler
	 */
	public CssManagerHandler getCssManagerHandler() {
		return cssManagerHandler;
	}

	/**
	 * @param cssManagerHandler the cssManagerHandler to set
	 */
	public void setCssManagerHandler(CssManagerHandler cssManagerHandler) {
		this.cssManagerHandler = cssManagerHandler;
	}

	/**
	 * @return the currentRegisteredCommId
	 */
	public String getCurrentRegisteredCommId() {
		return currentRegisteredCommId;
	}

	/**
	 * @param currentRegisteredCommId the currentRegisteredCommId to set
	 */
	public void setCurrentRegisteredCommId(String currentRegisteredCommId) {
		this.currentRegisteredCommId = currentRegisteredCommId;
	}

	//METHODS
	public UserGuiCommsServer() {
	}
	
	public void InitService() {
		//Registry Our User Gui Server with the Comms Manager
		try {
			getCommManager().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	

	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}
	
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}
	
	
	
	/* Put your functionality here if there is NO return object, ie, VOID  */
	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		
		UserGuiBean messageBean = (UserGuiBean) payload;
	
			
		try
		{
			switch (messageBean.getTargetcomponent()) {
				case USERGUI :
					this.handleUserGuiMessage(stanza,messageBean);
					break;
				case CISMANAGER :
					this.handleCisManagerReceiveMessage(stanza,messageBean);
					break;
				case CSSMAMANGER :
					this.handleCssManagerReceiveMessage(stanza,messageBean);
					break;
			};
		} catch (Exception e) {
				e.printStackTrace();
		};
		
	}


	/* Put your functionality here if there IS a return object */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {

		UserGuiBean messageBean = (UserGuiBean) payload;
		UserGuiBeanResult resultBean ; 
		
		return null;
	}

	/* (non-Javadoc)
	 * @see org.societies.comm.xmpp.interfaces.FeatureServer#setQuery(org.societies.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	private void handleCisManagerReceiveMessage(Stanza stanza, UserGuiBean messageBean) {
		this.getCisManagerHandler().handleReceiveMessage(stanza, messageBean);
		
	}

	private void handleCssManagerReceiveMessage(Stanza stanza,
			UserGuiBean messageBean) {
		this.getCssManagerHandler().handleReceiveMessage(stanza, messageBean);
		
	}
	
	public void handleUserGuiMessage(Stanza stanza, UserGuiBean messageBean) {
		
		ComponentParameters commParam = messageBean.getTargetcomponentparameter();
		
		
		switch(commParam.getComponentmethod()) {
			case INFORM:
				
				//TODO Add calls to da register to check coomid
				//Assuming at this stage it's ok
				
				this.setCurrentRegisteredCommId(commParam.getStringList().get(0));
				
				break;
		}
		
	}
	
}
