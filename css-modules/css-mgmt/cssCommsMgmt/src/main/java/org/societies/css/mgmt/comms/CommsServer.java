/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.css.mgmt.comms;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.internal.css.management.ICSSLocalManager;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssManagerMessageBean;
import org.societies.api.schema.cssmanagement.CssManagerResultBean;
import org.societies.api.schema.cssmanagement.CssProfile;
import org.societies.utilities.DBC.Dbc;

public class CommsServer implements IFeatureServer {
	private ICommManager commManager;
	private ICSSLocalManager cssManager;
	
	
	public static final List<String> MESSAGE_BEAN_NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/cssmanagement"));
	public static final List<String> MESSAGE_BEAN_PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.cssmanagement"));

	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);

	/**
	 * Default Constructor
	 */
	public CommsServer() {
	}
	/**
	 * Used by Spring to initialise bean
	 */
	public void initService() {
		try {
			LOG.debug("Initialise with Communication Manager");
			this.commManager.register(this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> getJavaPackages() {
		Dbc.ensure("Message bean Java packages list must have at least one member ", MESSAGE_BEAN_PACKAGES != null && MESSAGE_BEAN_PACKAGES.size() > 0);
		return MESSAGE_BEAN_PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		Dbc.ensure("Message bean namespaces list must have at least one member ", MESSAGE_BEAN_NAMESPACES != null && MESSAGE_BEAN_NAMESPACES.size() > 0);
		return MESSAGE_BEAN_NAMESPACES;
	}

	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		Dbc.require("Message stanza cannot be null", stanza != null);
		Dbc.require("Message payload cannot be null", payload != null);

		LOG.debug("CSSManager remote invocation with stanza length: " + stanza.toString().length());
		

		if (payload instanceof CssManagerMessageBean) {
			CssManagerMessageBean bean = (CssManagerMessageBean) payload;

			LOG.debug("CSSManager remote invocation");
			LOG.debug("CSSManager remote invocation on thread" + Thread.currentThread()  + " " + Thread.activeCount());

			Future<CssInterfaceResult> asyncResult = null;
			CssInterfaceResult result = null;
			
			switch (bean.getMethod()) {
			
				case REGISTER_XMPP_SERVER:
					LOG.debug("CSSManager remote invocation of method " + bean.getMethod().name());

					CssProfile profile = (CssProfile) bean.getProfile();
					asyncResult = this.cssManager.registerXMPPServer(profile);
					break;
				default:
					break;
			}
			
			try {
				result = asyncResult.get();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
			LOG.debug("CSSManager result");
			LOG.debug("CSSManager remote invocation on thread" + Thread.currentThread()  + " " + Thread.activeCount());

			CssManagerResultBean resultBean = new CssManagerResultBean();
			resultBean.setResult(result);

			Dbc.ensure("CSSManager result bean cannot be null", resultBean != null);
			return resultBean;
		}
		Dbc.ensure(this.getClass().getName() + " failure to interpret remote method invocation payload", true);
		return null;

	}


	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
	}

	@Override
	public Object setQuery(Stanza stanza, Object payload) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}
	
	/**
	 * Spring bean injection related method
	 * @return ICommManager
	 */
	public ICommManager getCommManager() {
		Dbc.ensure("Communication Manager cannot be null", this.commManager != null);
		return commManager;
	}
	/**
	 * Spring bean injection related method
	 * @param commManager
	 */
	public void setCommManager(ICommManager commManager) {
		Dbc.require(commManager != null);
		this.commManager = commManager;
	}
	public ICSSLocalManager getCssManager() {
		Dbc.ensure("CSS Manager cannot be null", this.cssManager != null);
		return cssManager;
	}
	public void setCssManager(ICSSLocalManager cssManager) {
		this.cssManager = cssManager;
	}

}
