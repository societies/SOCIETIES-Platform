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
package org.societies.platform.servicelifecycle.servicecontrol.comms;

import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.springframework.scheduling.annotation.Async;

import org.societies.api.schema.servicelifecycle.servicecontrol.MethodType;
import org.societies.api.schema.servicelifecycle.servicecontrol.ServiceControlMsgBean;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.IServiceControlCallback;
import org.societies.api.internal.servicelifecycle.IServiceControlRemote;

/**
 * Comms Client that initiates the remote communication for the Service Lifecycle
 *
 * @author aleckey
 * @author Maria Mannion
 * @author <a href="mailto:sanchocsa@gmail.com">Sancho Rêgo</a> (PTIN)
 *
 */
public class CommsClient implements IServiceControlRemote, ICommCallback{
	private static final List<String> NAMESPACES = Collections.unmodifiableList(
			  Arrays.asList("http://societies.org/api/schema/servicelifecycle/model",
					  		"http://societies.org/api/schema/servicelifecycle/servicecontrol"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
			  Arrays.asList("org.societies.api.schema.servicelifecycle.model",
							"org.societies.api.schema.servicelifecycle.servicecontrol"));
	
	//PRIVATE VARIABLES
	private ICommManager commMngr;
	private static Logger LOG = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;
	private CommsServer slmCommManager;

	public CommsServer getSlmCommManager(){
		return slmCommManager;	
	}
	
	public void setSlmCommManager(CommsServer slmCommManager){
		this.slmCommManager = slmCommManager;	
	}
	
	public void setCommMngr(ICommManager commMngr) {
		this.commMngr = commMngr;
	}
	
	public ICommManager getCommMngr() {
		return commMngr;
	}
	public CommsClient() {	}

	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		
		if(LOG.isDebugEnabled()) LOG.debug("Registering the SLM Communication Manager with the XMPP Communication Manager");
		
		try {
			getCommMngr().register(this); 
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
		idMgr = commMngr.getIdManager();
	}
	

	@Override
	@Async
	public void startService(ServiceResourceIdentifier serviceId,
			IIdentity node, IServiceControlCallback serviceControlCallback) {
	
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: startService called");

		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.START_SERVICE);
		bean.setServiceId(serviceId);
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
	}

	@Override
	@Async
	public void stopService(ServiceResourceIdentifier serviceId,
			IIdentity node, IServiceControlCallback serviceControlCallback) {
		
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: stopService called");

		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.STOP_SERVICE);
		bean.setServiceId(serviceId);
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		
	}

	@Override
	@Async
	public void installService(URL bundleLocation, IIdentity node,
			IServiceControlCallback serviceControlCallback) {

		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: installService called");

		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.INSTALL_SERVICE);
		try {
		
			bean.setURL(bundleLocation.toURI());
		
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (Exception e) {
			LOG.warn(e.getMessage());
		};
		
		
		
	}

	@Override
	@Async
	public void uninstallService(ServiceResourceIdentifier serviceId,
			IIdentity node, IServiceControlCallback serviceControlCallback) {
		
		
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: uninstallService called");

		Stanza stanza = new Stanza(node);

		//SETUP CALC CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.UNINSTALL_SERVICE);
		bean.setServiceId(serviceId);
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
	}

	@Override
	@Async
	public void shareService(Service service, IIdentity node,
			IServiceControlCallback serviceControlCallback) {
		
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: shareService called");

		Stanza stanza = new Stanza(node);

		//SETUP CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.SHARE_SERVICE);
		bean.setService(service);
		bean.setShareJid(node.getJid());
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
		
		
	}

	@Override
	@Async
	public void unshareService(Service service, IIdentity node,
			IServiceControlCallback serviceControlCallback) {
		
		if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: unshareService called");

		Stanza stanza = new Stanza(node);

		//SETUP CLIENT RETURN STUFF
		CommsClientCallback callback = new CommsClientCallback(stanza.getId(), serviceControlCallback);

		//CREATE MESSAGE BEAN
		ServiceControlMsgBean bean = new ServiceControlMsgBean();
		bean.setMethod(MethodType.UNSHARE_SERVICE);
		bean.setService(service);
		bean.setShareJid(node.getJid());
		
		try {
			
			if(LOG.isDebugEnabled()) LOG.debug("SLM CommsClient: Sending Message...");

			//SEND INFORMATION QUERY - RESPONSE WILL BE IN "callback"
			getCommMngr().sendIQGet(stanza, bean, callback);
			
		} catch (CommunicationException e) {
			LOG.warn(e.getMessage());
		};
			
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getJavaPackages() */
	@Override
	public List<String> getJavaPackages() {
		return PACKAGES;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#getXMLNamespaces() */
	@Override
	public List<String> getXMLNamespaces() {
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) { }

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) { }

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) { }

	@Override
	public void receiveResult(Stanza arg0, Object arg1) { }

	/* (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.ICommCallback#receiveItems(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.String, java.util.List)
	 */
	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
	}

	@Override
	public void installService(Service service, IIdentity node,
			IServiceControlCallback callback) {
		
		if(LOG.isDebugEnabled())
			LOG.debug("SLM CommsClient: installService(service,node) called");		
	}

}
