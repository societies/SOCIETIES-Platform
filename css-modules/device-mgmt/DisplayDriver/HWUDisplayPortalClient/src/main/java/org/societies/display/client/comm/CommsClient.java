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
package org.societies.display.client.comm;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.datatypes.Stanza;
import org.societies.api.comm.xmpp.datatypes.XMPPInfo;
import org.societies.api.comm.xmpp.exceptions.CommunicationException;
import org.societies.api.comm.xmpp.exceptions.XMPPError;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerIPAddressResultBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerMethodType;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerScreenLocationResultBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerScreenUseBean;
import org.societies.api.schema.css.devicemgmt.display.displayportalserverbean.DisplayPortalServerServiceIDResultBean;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.display.server.api.remote.IDisplayPortalServer;


/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class CommsClient implements IDisplayPortalServer, ICommCallback{


	private static final List<String> NAMESPACES = Collections.unmodifiableList(
            Arrays.asList("http://societies.org/api/schema/css/devicemgmt/display/displayportalserverbean",
                    "http://societies.org/api/schema/servicelifecycle/model"));
			 // Arrays.asList("http://societies.org/api/ext3p/schema/displayportalserverbean",
			//		  "http://societies.org/api/schema/servicelifecycle/model"));
	private static final List<String> PACKAGES = Collections.unmodifiableList(
		  Arrays.asList("org.societies.api.schema.css.devicemgmt.display.displayportalserverbean",//"org.societies.api.ext3p.schema.displayportalserverbean",
				  "org.societies.api.schema.servicelifecycle.model"));
				  
	private ICommManager commManager;
	private static Logger logging = LoggerFactory.getLogger(CommsClient.class);
	private IIdentityManager idMgr;

	private Hashtable<DisplayPortalServerMethodType, DisplayPortalServerIPAddressResultBean> ipAddressResults;
	private Hashtable<DisplayPortalServerMethodType, DisplayPortalServerServiceIDResultBean> serviceIDResults;
	private Hashtable<DisplayPortalServerMethodType, DisplayPortalServerScreenLocationResultBean> screenLocationsResults;
	private Hashtable<DisplayPortalServerMethodType, DisplayPortalServerScreenUseBean> screenUseResult;
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
		this.idMgr = commManager.getIdManager();
	}

	public CommsClient() 
	{	
		this.ipAddressResults = new Hashtable<DisplayPortalServerMethodType, DisplayPortalServerIPAddressResultBean>();
		this.serviceIDResults = new Hashtable<DisplayPortalServerMethodType, DisplayPortalServerServiceIDResultBean>();
		this.screenLocationsResults = new Hashtable<DisplayPortalServerMethodType, DisplayPortalServerScreenLocationResultBean>();
		this.screenUseResult = new Hashtable<DisplayPortalServerMethodType, DisplayPortalServerScreenUseBean>();
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
		return PACKAGES;
	}

	@Override
	public List<String> getXMLNamespaces() {
		// TODO Auto-generated method stub
		return NAMESPACES;
	}

	@Override
	public void receiveError(Stanza arg0, XMPPError arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveInfo(Stanza arg0, String arg1, XMPPInfo arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveItems(Stanza arg0, String arg1, List<String> arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveMessage(Stanza arg0, Object arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveResult(Stanza stanza, Object result) {
		if(logging.isDebugEnabled()) logging.debug("Received resultBean");
		if (result instanceof DisplayPortalServerServiceIDResultBean)
		{
			this.serviceIDResults.put(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID, (DisplayPortalServerServiceIDResultBean) result);
			synchronized (this.serviceIDResults){
				this.serviceIDResults.notifyAll();
			}
				
		}else if (result instanceof DisplayPortalServerScreenLocationResultBean){
				this.screenLocationsResults.put(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS, (DisplayPortalServerScreenLocationResultBean) result);
				synchronized (this.screenLocationsResults) {
					this.screenLocationsResults.notifyAll();
				}
		}else if (result instanceof DisplayPortalServerIPAddressResultBean){
				this.ipAddressResults.put(DisplayPortalServerMethodType.REQUEST_ACCESS, (DisplayPortalServerIPAddressResultBean) result);
				synchronized (ipAddressResults) {
					this.ipAddressResults.notifyAll();
				}
		}else if (result instanceof DisplayPortalServerScreenUseBean){
			this.screenUseResult.put(DisplayPortalServerMethodType.CHECK_SCREEN_USE, (DisplayPortalServerScreenUseBean) result);
			synchronized (screenUseResult) {
				this.screenUseResult.notifyAll();
			}
	}

	}
	

	@Override
	public boolean checkAccess(IIdentity serverIdentity, String location)
	{
		DisplayPortalServerBean bean = new DisplayPortalServerBean();
		bean.setMethod(DisplayPortalServerMethodType.CHECK_SCREEN_USE);
		bean.setLocation(location);
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			this.commManager.sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(!this.screenUseResult.containsKey(DisplayPortalServerMethodType.CHECK_SCREEN_USE))
		{
			try{
			synchronized(this.screenUseResult)
			{
				this.screenUseResult.wait();
			}
			
			DisplayPortalServerScreenUseBean resultBean = this.screenUseResult.get(DisplayPortalServerMethodType.CHECK_SCREEN_USE);
			if(logging.isDebugEnabled()) logging.debug("Returning frin checkScreen " + resultBean.isInUse());
			this.screenUseResult.remove(DisplayPortalServerMethodType.CHECK_SCREEN_USE);
			return resultBean.isInUse();
			
			}catch(Exception e)
			{
				
			}
		}
		return true;
	}

	@Override
	public String requestAccess(IIdentity serverIdentity, String identity, String location) {
		// TODO Auto-generated method stub
		DisplayPortalServerBean bean = new DisplayPortalServerBean();
		bean.setIdentity(identity);
		bean.setLocation(location);
		bean.setMethod(DisplayPortalServerMethodType.REQUEST_ACCESS);
		
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			this.commManager.sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return "";
		}
		
		while(!this.ipAddressResults.containsKey(DisplayPortalServerMethodType.REQUEST_ACCESS)){
			try {
				if(logging.isDebugEnabled()) logging.debug("waiting for results");
				synchronized(this.ipAddressResults){
				this.ipAddressResults.wait();
				}
				DisplayPortalServerIPAddressResultBean resultBean = this.ipAddressResults.get(DisplayPortalServerMethodType.REQUEST_ACCESS);
				
				String toReturn = new String(resultBean.getIpAddress());
				
				this.ipAddressResults.remove(DisplayPortalServerMethodType.REQUEST_ACCESS);
				return toReturn;
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return "REFUSED";
	}

	@Override
	public void releaseResource(IIdentity serverIdentity, String identity, String location) {
		
		if(logging.isDebugEnabled()) logging.debug("Releasing resource");
		DisplayPortalServerBean bean = new DisplayPortalServerBean();
		bean.setIdentity(identity);
		bean.setLocation(location);
		bean.setMethod(DisplayPortalServerMethodType.REQUEST_ACCESS);
		
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			this.commManager.sendMessage(stanza, bean);
			//this.commManager.sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public String[] getScreenLocations(IIdentity serverIdentity) {
		DisplayPortalServerBean bean = new DisplayPortalServerBean();
		bean.setMethod(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS);
		
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			this.commManager.sendIQGet(stanza, bean, this);
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		while(!this.screenLocationsResults.containsKey(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS)){
			try {
				if(logging.isDebugEnabled()) logging.debug("waiting for results");
				synchronized (this.screenLocationsResults){
					this.screenLocationsResults.wait();
				}
				DisplayPortalServerScreenLocationResultBean resultBean = this.screenLocationsResults.get(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS);
				
				byte[] bytearray = resultBean.getScreenLocations();
				
				String[] locations;
				try {
					locations = (String[]) SerialisationHelper.deserialise(bytearray, this.getClass().getClassLoader());
					this.screenLocationsResults.remove(DisplayPortalServerMethodType.GET_SCREEN_LOCATIONS);
					return locations;
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

		
		
		return new String[0];
	}

	@Override
	public ServiceResourceIdentifier getServerServiceId(IIdentity serverIdentity) {
		DisplayPortalServerBean bean = new DisplayPortalServerBean();
		bean.setMethod(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID);
		
		Stanza stanza = new Stanza(serverIdentity);
		
		try {
			if(logging.isDebugEnabled()) logging.debug("Requesting serviceID");
			this.commManager.sendIQGet(stanza, bean, this);
			while (!this.serviceIDResults.containsKey(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID)){
				try {
					if(logging.isDebugEnabled()) logging.debug("waiting for results");
					synchronized(this.serviceIDResults){
						this.serviceIDResults.wait();
					}
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			if(logging.isDebugEnabled()) logging.debug("Received serviceID");
			DisplayPortalServerServiceIDResultBean resultBean = this.serviceIDResults.get(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID);
			ServiceResourceIdentifier serviceId = resultBean.getServiceID();
			this.serviceIDResults.remove(DisplayPortalServerMethodType.GET_SERVER_SERVICE_ID);
			return serviceId;
		} catch (CommunicationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		

		return null;
		
		
	}

}
