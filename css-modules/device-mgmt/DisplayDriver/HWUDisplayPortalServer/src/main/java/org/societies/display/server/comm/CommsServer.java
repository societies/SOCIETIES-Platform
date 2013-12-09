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
package org.societies.display.server.comm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.comm.xmpp.interfaces.IFeatureServer;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerIPAddressResultBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerMethodType;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerScreenLocationResultBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerScreenUseBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerServiceIDResultBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;


/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class CommsServer implements IFeatureServer{

	private static final List<String> NAMESPACES = Collections.unmodifiableList(

            Arrays.asList("http://societies.org/api/schema/css/devicemgmt/display/displayportalserverbean",
                    "http://societies.org/api/schema/servicelifecycle/model"));
			 // Arrays.asList("http://societies.org/api/ext3p/schema/displayportalserverbean",
				//	  "http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
		  Arrays.asList("org.societies.api.schema.css.devicemgmt.display.displayportalserverbean",//"org.societies.api.ext3p.schema.displayportalserverbean",
				  "org.societies.api.schema.servicelifecycle.model"));

	
	
	private ICommManager commManager;
	private IDisplayPortalServer displayPortalServer;
	
	private static Logger LOG = LoggerFactory.getLogger(CommsServer.class);
	
	/**
	 * @return the commManager
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param commManager the commManager to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	/**
	 * @return the displayPortalServer
	 */
	public IDisplayPortalServer getDisplayPortalServer() {
		return displayPortalServer;
	}

	/**
	 * @param displayPortalServer the displayPortalServer to set
	 */
	public void setDisplayPortalServer(IDisplayPortalServer displayPortalServer) {
		this.displayPortalServer = displayPortalServer;
	}

	//METHODS
	public CommsServer() {
	}
	
	public void InitService() {
		//REGISTER OUR ServiceManager WITH THE XMPP Communication Manager
		try {
			getCommManager().register(this);
		} catch (CommunicationException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public List<String> getJavaPackages() {
		// TODO Auto-generated method stub
		return this.PACKAGES;
	}

	/*
	 * (non-Javadoc)
	 * @see org.societies.api.comm.xmpp.interfaces.IFeatureServer#getQuery(org.societies.api.comm.xmpp.datatypes.Stanza, java.lang.Object)
	 */
	@Override
	public Object getQuery(Stanza stanza, Object payload) throws XMPPError {
		// TODO Auto-generated method stub
		if (payload instanceof DisplayPortalServerBean){
			
			if (((DisplayPortalServerBean) payload).getMethod().equals(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS)){
				if(LOG.isDebugEnabled()) LOG.debug("getQuery: "+DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS);
				String[] locations = this.displayPortalServer.getScreenLocations();
				
				try {
					byte[] serialisedObj = SerialisationHelper.serialise(locations);
					DisplayPortalServerScreenLocationResultBean resultBean = new DisplayPortalServerScreenLocationResultBean();
                    resultBean.setScreenLocations(serialisedObj);
					if(LOG.isDebugEnabled()) LOG.debug("Returning screen locations");
					return resultBean;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
			}
			if (((DisplayPortalServerBean) payload).getMethod().equals(DisplayPortalServerMethodType.CHECK_SCREEN_USE)){
				if(LOG.isDebugEnabled()) LOG.debug("getQuery: "+DisplayPortalServerMethodType.CHECK_SCREEN_USE);
			//	String identity = ((DisplayPortalServerBean) payload).getIdentity();
				String location = ((DisplayPortalServerBean) payload).getLocation();
				boolean inUse = this.displayPortalServer.checkInUse(location);
				//String ipAddress =  this.displayPortalServer.requestAccess(identity, location);
				DisplayPortalServerScreenUseBean resultBean = new DisplayPortalServerScreenUseBean();
				resultBean.setInUse(inUse);
				if(LOG.isDebugEnabled()) LOG.debug("Returning result of in user request: "+inUse);
				return resultBean;
			}
			
			if (((DisplayPortalServerBean) payload).getMethod().equals(DisplayPortalServerMethodType.REQUEST_ACCESS)){
				if(LOG.isDebugEnabled()) LOG.debug("getQuery: "+DisplayPortalServerMethodType.REQUEST_ACCESS);
				String identity = ((DisplayPortalServerBean) payload).getIdentity();
				String location = ((DisplayPortalServerBean) payload).getLocation();
				String ipAddress =  this.displayPortalServer.requestAccess(identity, location);
				if(LOG.isDebugEnabled()) LOG.debug("Got access response :"+ipAddress+" for: "+identity+" and location: "+location);
				DisplayPortalServerIPAddressResultBean resultBean = new DisplayPortalServerIPAddressResultBean();
				resultBean.setIpAddress(ipAddress);
				if(LOG.isDebugEnabled()) LOG.debug("Returning result of access request: "+ipAddress);
				return resultBean;
			}
			
			if (((DisplayPortalServerBean) payload).getMethod().equals(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID)){
				if(LOG.isDebugEnabled()) LOG.debug("getQuery: "+DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID);
				ServiceResourceIdentifier serviceId = displayPortalServer.getServerServiceId();
				DisplayPortalServerServiceIDResultBean resultBean = new DisplayPortalServerServiceIDResultBean();
				resultBean.setServiceID(serviceId);
				if(LOG.isDebugEnabled()) LOG.debug("Returning serviceID: "+serviceId.getServiceInstanceIdentifier()+" "+serviceId.getIdentifier());
				return resultBean;
				
			}
			/*
			 * unknown method called
			 */
			return null;
		}
		
		/*
		 * unknown payload object received
		 */
		return null;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return this.NAMESPACES;
	}

	@Override
	public void receiveMessage(Stanza stanza, Object payload) {
		if (payload instanceof DisplayPortalServerBean){
			String identity = ((DisplayPortalServerBean) payload).getIdentity();
			String location = ((DisplayPortalServerBean) payload).getLocation();
			this.displayPortalServer.releaseResource(identity, location);
		}
		
	}

	@Override
	public Object setQuery(Stanza arg0, Object arg1) throws XMPPError {
		// TODO Auto-generated method stub
		return null;
	}

}
