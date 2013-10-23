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




import org.hibernate.SessionFactory;
//import org.societies.display.server.dao.impl.MockScreenDAO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.services.IServices;



import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;
import org.societies.display.server.dao.impl.ScreenDAO;
import org.societies.display.server.model.Screen;
import org.societies.display.server.model.ScreenConfiguration;
/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */
public class DisplayPortalServer implements IDisplayPortalServer{



	private List<String> screenIPAddresses;

	private static Logger LOG = LoggerFactory.getLogger(DisplayPortalServer.class);


	private Hashtable<String, String> currentlyUsedScreens;

	private ScreenConfiguration screenconfig;

	private IServices services;



	private ServiceResourceIdentifier myServiceId;

	private ICommManager commManager;
	private IIdentityManager idMgr;

	private IIdentity serverIdentity;

	private List<Screen> screens;
	private ScreenDAO screenDAO;

	private SessionFactory sessionFactory;



	public DisplayPortalServer(){
		screenIPAddresses = new ArrayList<String>();
		screens = new ArrayList<Screen>();

	}

	public void initialiseServer(){
		UIManager.put("ClassLoader", ClassLoader.getSystemClassLoader());
		//SETUP UP TO GET SCREENS FROM DB
		screenDAO = new ScreenDAO(sessionFactory);
		currentlyUsedScreens = new Hashtable<String, String>();
		//SET UP A NEW SCREEN CONFIGURATION
		this.screenconfig = new ScreenConfiguration();
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
		this.screens=screenDAO.getAllScreens();
		//REMOVE ALL SCREENS FROM SCREEN CONFIG
		screenconfig.removeAllScreens();
		//GET A NEW SCREEN CONFIGURATION (ORGINALLY CALLED FROM SCREENCONFIGFIDALOGUE)
		//AND ADD THE SCREENS TO THE SCREENCONFIG
		for(Screen screen : screens)
		{
			screenconfig.addScreen(screen);
		}
		if(LOG.isDebugEnabled()) LOG.debug(this.toString() + " " + screens.toString());
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



	@Override
	public String requestAccess(String identity, String location) {
		try{
			if(LOG.isDebugEnabled()) LOG.debug("Request from: "+identity+" to use screen in location: "+location);
			if (this.currentlyUsedScreens.containsKey(location)){
				return "REFUSED";
			}else{
				Screen screen = this.screenconfig.getScreenBasedOnLocation(location);
				if (screen==null){
					LOG.debug("There is no screen at location: "+location+"\n. Available locations are: \n"+this.screenconfig.toString());
					return "REFUSED";
				}

				String ipAddress = screen.getIpAddress();

				if (ipAddress==null){
					if(LOG.isDebugEnabled()) LOG.debug("IP address for screen: "+screen.getScreenId()+" is null");
					return "REFUSED";
				}

				//ON RETURN IP ADDRESS, ADD THE MAPPING OF THE SCREEN
				this.currentlyUsedScreens.put(location, identity);
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
	public void releaseResource(String identity, String location) {
		if (this.currentlyUsedScreens.containsKey(location)){
			String currentUserId = this.currentlyUsedScreens.get(location);
			if (identity.startsWith(currentUserId) || (currentUserId.startsWith(identity))){
				this.currentlyUsedScreens.remove(location);
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
	public String[] getScreenLocations() {
		return this.screenconfig.getLocations();
	}

	public static void main(String[] args){
		DisplayPortalServer server = new DisplayPortalServer();
		server.initialiseServer();

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
		serverIdentity = this.idMgr.getThisNetworkNode();
	}

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}
	/**
	 * @param sessionFactory the services to sessionFactory
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	/* public void setScreenDAO(IScreenDAO screenDAO) {
        this.screenDAO = screenDAO;
    }

    public IScreenDAO getScreenDAO() {
        return screenDAO;
    }*/
}
