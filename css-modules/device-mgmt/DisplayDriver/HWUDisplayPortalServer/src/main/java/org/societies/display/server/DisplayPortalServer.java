/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFOD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
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
package org.societies.display.server;

import java.util.*;

import javax.swing.UIManager;









import org.societies.api.osgi.event.IEventMgr;
//import org.societies.display.server.dao.impl.MockScreenDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;



import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;
import org.societies.display.server.context.ContextRetriever;
import org.societies.api.css.devicemgmt.display.Screen;
/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class DisplayPortalServer implements IDisplayPortalServer{

	private static Logger LOG = LoggerFactory.getLogger(DisplayPortalServer.class);


	private Hashtable<String, String> currentlyUsedScreens;

	private IServices services;

	private ServiceResourceIdentifier myServiceId;

	private ICommManager commManager;
	private IIdentityManager idMgr;
	private ICtxBroker ctxBroker;

	private ContextRetriever contextRetriever;


	private IIdentity serverIdentity;

	private List<Screen> screens;


	public DisplayPortalServer(){
		screens = new ArrayList<Screen>();
	}

	public void initialiseServer(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		//SETUP UP TO GET SCREENS FROM DB
		contextRetriever = new ContextRetriever(this.ctxBroker, this.serverIdentity);
		currentlyUsedScreens = new Hashtable<String, String>();
		
		//SET THE SCREENS BY RETRIEVING FROM DB
		setScreens();
		if(LOG.isDebugEnabled()) LOG.debug("SCREENS : "  + screens.toString());


		//DO NOT NEED TO GET SCREENS FROM USER NOW
		//this.getScreenConfigurationFromUser();
	}

	//GET SCREENS FROM DB
	@Override
	public void setScreens()
	{
		if(LOG.isDebugEnabled()) LOG.debug("SETTING SCREENS");
		this.contextRetriever.getScreensFromContext();
		this.screens = this.contextRetriever.getScreens();
	}



	@Override
	public boolean checkInUse(String location)
	{
		if (this.currentlyUsedScreens.containsKey(location))
		{
			return true;
		}
		return false;
	}

	private Screen getScreenBasedOnID(String screenID){
		Screen screenToReturn = null;
		for(Screen screen : this.screens)
		{
			if(screen.getScreenID().equals(screenID))
			{
				screenToReturn = screen;
				break;
			}
		}
		return screenToReturn;
	}


	@Override
	public String requestAccess(String identity, String screenID) {
		try{
			if(LOG.isDebugEnabled()) LOG.debug("Request from: "+identity+" to use screen: "+screenID);
			if (this.currentlyUsedScreens.containsKey(screenID)){
				return "REFUSED";
			}else{
				Screen screen = getScreenBasedOnID(screenID);
				if (screen==null){
					return "REFUSED";
				}

				String ipAddress = screen.getIpAddress();

				if (ipAddress==null){
					if(LOG.isDebugEnabled()) LOG.debug("IP address for screen: "+screen.getScreenID()+" is null");
					return "REFUSED";
				}

				//ON RETURN IP ADDRESS, ADD THE MAPPING OF THE SCREEN
				this.currentlyUsedScreens.put(screenID, identity);
				return ipAddress;
			}
		}
		catch (Exception e){
			e.printStackTrace();
			if(LOG.isDebugEnabled()) LOG.debug("Unknown Exception occured: "+e.getMessage());
		}

		return "REFUSED";
	}



	@Override
	public void releaseResource(String identity, String screenID) {
		if (this.currentlyUsedScreens.containsKey(screenID)){
			String currentUserId = this.currentlyUsedScreens.get(screenID);
			if (identity.startsWith(currentUserId) || (currentUserId.startsWith(identity))){
				this.currentlyUsedScreens.remove(screenID);
			}
		}

	}
	
	//Release resource call from webapp
	@Override
	public void releaseResource(String location)
	{
		if(LOG.isDebugEnabled()) LOG.debug("CURRENTLY USED SCREENS: " + this.currentlyUsedScreens.keys().toString());
		if(LOG.isDebugEnabled()) LOG.debug("RELEASING RESOURCE : " + location);
		this.currentlyUsedScreens.remove(location);
		if(LOG.isDebugEnabled()) LOG.debug("USE SCREENS ARE NOW: " + this.currentlyUsedScreens.keys().toString());
	}

	@Override
	public ServiceResourceIdentifier getServerServiceId() {
		if (this.myServiceId==null){
			this.myServiceId = this.getServices().getMyServiceId(this.getClass());
			if (this.myServiceId==null){
				this.myServiceId = ServiceModelUtils.generateServiceResourceIdentifier(this.serverIdentity, this.getClass());

			}

			if (this.myServiceId==null){
				if(LOG.isDebugEnabled()) LOG.debug("ServiceID could not be retrieved");
			}else{
				if(LOG.isDebugEnabled()) LOG.debug("Returning serviceID :"+this.myServiceId);
			}	
		}
		return this.myServiceId;
	}

	/**
	 * @return the services
	 */
	public IServices getServices() {
		return services;
	}

	/**
	 * @param services the services to set
	 */
	public void setServices(IServices services) {
		this.services = services;
	}

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
		this.serverIdentity = this.idMgr.getThisNetworkNode();
	}
	
	/**
	 * @return the ctxBroker
	 */
	public ICtxBroker getCtxBroker() {
		return ctxBroker;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCtxBroker(ICtxBroker ctxBroker) {
		this.ctxBroker = ctxBroker;
	}
	


	@Override
	public String[] getScreenIDs() {
		String[] screenIDs = new String[this.screens.size()];
		int increment=0;
		for(Screen screen : this.screens)
		{
			screenIDs[increment] = screen.getScreenID();
		}
		return screenIDs;
	}

}
