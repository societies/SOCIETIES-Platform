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
package org.societies.display.client;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.eclipse.jetty.util.log.Log;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.display.DisplayEvent;
import org.societies.api.css.devicemgmt.display.DisplayEventConstants;
import org.societies.api.css.devicemgmt.display.IDisplayDriver;
import org.societies.api.css.devicemgmt.display.IDisplayableService;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.Requestor;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.useragent.feedback.IUserFeedback;
import org.societies.api.osgi.event.CSSEvent;
import org.societies.api.osgi.event.CSSEventConstants;
import org.societies.api.osgi.event.EMSException;
import org.societies.api.osgi.event.EventListener;
import org.societies.api.osgi.event.EventTypes;
import org.societies.api.osgi.event.IEventMgr;
import org.societies.api.osgi.event.InternalEvent;
import org.societies.api.services.ServiceMgmtEventType;
import org.societies.display.server.api.remote.IDisplayPortalServer;


/**
 * Describe your class here...
 *
 * @author Eliza
 *
 */

public class DisplayPortalClient extends EventListener implements IDisplayDriver {


	private ICommManager commManager;
	private IIdentityManager idMgr;
	private IIdentity userIdentity;
	private List<String> screenLocations;
	private IDisplayPortalServer portalServerRemote;
	private IIdentity serverIdentity;
	private ICtxBroker ctxBroker;
	private Requestor requestor;
	private IUserFeedback userFeedback;

	private boolean hasSession;
	private IEventMgr evMgr;
	private String currentUsedScreenIP = "";
	private String currentUsedScreenLocation = "";
	private static Logger LOG = LoggerFactory.getLogger(DisplayPortalClient.class);

	private HashMap <String, String> userLocation;
	private List<String> waitingRequests;

	private ServiceRuntimeSocketServer servRuntimeSocketThread;

	private UserSession userSession;
	private Integer serviceRuntimeSocketPort;

	public DisplayPortalClient(){
		this.screenLocations = new ArrayList<String>();
		this.servRuntimeSocketThread = new ServiceRuntimeSocketServer(this);
		this.serviceRuntimeSocketPort = this.servRuntimeSocketThread.setListenPort();
		this.servRuntimeSocketThread.start();
		userLocation = new HashMap<String, String>();
		waitingRequests = new ArrayList<String>();

	}


	public void Init(){
		if(LOG.isDebugEnabled()) LOG.debug("Initialising DisplayPortalClient");
		try {
			this.serverIdentity = this.idMgr.fromJid("university.societies.local.macs.hw.ac.uk");
		} catch(Exception e) {}
		//services.getServer(myClientServiceID);
		if(LOG.isDebugEnabled()) LOG.debug("Retrieved my server's identity: "+this.serverIdentity.getJid());


		//
		//  ctxEvListener = new ContextEventListener(this, getCtxBroker(), userIdentity, requestor);
		//   retrieveScreenLocations();
		/* String[] locs = this.portalServerRemote.getScreenLocations(serverIdentity);
        this.LOG.debug("Retrieved screen locations from my server");
        for (int i=0; i<locs.length; i++){
            this.screenLocations.add(locs[i]);
            this.LOG.debug("Screen location: "+i+": "+locs[i]);
        }*/

		//SERVICE REQUESTER NOT NEEDED - NOT A 3P SERVICE
		this.requestor = new Requestor(userIdentity);


		userSession = new UserSession(this.userIdentity.getJid(), this.serviceRuntimeSocketPort);

		if(LOG.isDebugEnabled()) LOG.debug("DisplayPortalClient initialised");

		//LISTEN TO NEW CONTEXT CHANGES
		new ContextEventListener(this, ctxBroker, userIdentity, requestor);//.registerForLocationEvents();


		//DOESN'T DO ANYTHING
		//registerForSLMEvents();

	}

	//METHOD TO RETRIEVE SCREEN LOCATIONS FROM SERVER PORTAL
	private void retrieveScreenLocations()
	{
		this.screenLocations.clear();
		String[] locs = this.portalServerRemote.getScreenLocations(serverIdentity);
		if(LOG.isDebugEnabled()) LOG.debug("Retrieved screen locations from my server");
		for (int i=0; i<locs.length; i++){
			this.screenLocations.add(locs[i]);
			if(LOG.isDebugEnabled()) LOG.debug("Screen location: "+i+": "+locs[i]);
		}
	}





	private void registerForSLMEvents() {
		String eventFilter = "(&" +
				"(" + CSSEventConstants.EVENT_NAME + "="+ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/service/lifecycle)" +
				")";
		this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		if(LOG.isDebugEnabled()) LOG.debug("Subscribed to "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");

	}
	private void unRegisterFromSLMEvents()
	{
		String eventFilter = "(&" + 
				"(" + CSSEventConstants.EVENT_NAME + "="+ ServiceMgmtEventType.NEW_SERVICE+")" +
				"(" + CSSEventConstants.EVENT_SOURCE + "=org/societies/service/lifecycle)" +
				")";

		this.evMgr.unSubscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		//this.evMgr.subscribeInternalEvent(this, new String[]{EventTypes.SERVICE_LIFECYCLE_EVENT}, eventFilter);
		if(LOG.isDebugEnabled()) LOG.debug("Unsubscribed from "+EventTypes.SERVICE_LIFECYCLE_EVENT+" events");
	}
	/*
	 * NOT USED
	 * (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleExternalEvent(org.societies.api.osgi.event.CSSEvent)
	 */
	@Override
	public void handleExternalEvent(CSSEvent arg0) {
		// TODO Auto-generated method stub

	}

	/*
	 * Used to receive SLM events and specifically, to know that this bundle has been started in osgi 
	 * so that it can retrieve it's generated SRI.
	 * (non-Javadoc)
	 * @see org.societies.api.osgi.event.EventListener#handleInternalEvent(org.societies.api.osgi.event.InternalEvent)
	 */
	@Override
	public void handleInternalEvent(InternalEvent event) {
		// TODO Auto-generated method stub
	}

	private boolean matchesLocation(String location){
		if(LOG.isDebugEnabled()) LOG.debug("User location length: "+location.length());

		for (int i=0; i<screenLocations.size(); i++){
			String scrLoc = screenLocations.get(i);
			if(LOG.isDebugEnabled()) LOG.debug("Screen location length: "+scrLoc.length());	
			if (scrLoc.trim().equalsIgnoreCase(location.trim())){
				if(LOG.isDebugEnabled()) LOG.debug(scrLoc+" matches "+location+". Returning true");
				return true;
			}
			if(LOG.isDebugEnabled()) LOG.debug(scrLoc+" doesn't match "+location);

		}
		if(LOG.isDebugEnabled()) LOG.debug("return false");
		return false;
	}

	public void sendStartSessionRequest(String location)
	{

		synchronized(userLocation)
		{
			synchronized(waitingRequests)
			{
				waitingRequests.remove(location);
			}
			if(userLocation.containsValue(location))
			{
				if(location.equals(this))
					if(LOG.isDebugEnabled()) LOG.debug("Requesting access to screen in location: "+location);
				//request access
				String reply = this.portalServerRemote.requestAccess(serverIdentity, userIdentity.getJid(), location);
				//if access refused do nothing
				if (reply.equals("REFUSED")){
					if(LOG.isDebugEnabled()) LOG.debug("Refused access to screen.");
					this.userFeedback.showNotification("Sorry, " + location + " is not available any more!");
				}
				else //if access is granted 
				{
					if(LOG.isDebugEnabled()) LOG.debug("Access to screen granted. IP Address is: "+reply);
					//now setup new screen
					SocketClient socketClient = new SocketClient(reply);

					if(socketClient.startSession(userSession))
					{
						//TODO: send services TO DISPLAY
						this.currentUsedScreenIP = reply;
						this.currentUsedScreenLocation = location;
						this.hasSession = true;
						DisplayEvent dEvent = new DisplayEvent(this.currentUsedScreenIP, DisplayEventConstants.DEVICE_AVAILABLE);
						InternalEvent iEvent = new InternalEvent(EventTypes.DISPLAY_EVENT, "displayUpdate", "org/societies/css/device", dEvent);
						try {
							this.evMgr.publishInternalEvent(iEvent);
						} catch (EMSException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						LOG.info(this.userIdentity.getBareJid() + " has started a screen session with " + location);
					}
					else
					{
						this.userFeedback.showNotification("Sorry, a session could not be started with + " + location + ". Is the portal on " + location + " running? Contact the SOCIETIES team!");
						if(LOG.isDebugEnabled()) LOG.debug("Comms with " + location + " could not be established");
						this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), location);
					}
				}
			}
			else
			{
				this.userFeedback.showNotification("Sorry, the session request for " + location + ", is no longer valid");
				if(LOG.isDebugEnabled()) LOG.debug("User is no longer near " + location);
			}
		}
	}

	public void acknowledgeRefuse(String location)
	{
		synchronized(waitingRequests)
		{
			waitingRequests.remove(location);
		}
	}

	public void updateUserLocation(String location){

		//FOR EVERY UPDATE ON USER LOCATION, GET UPTO DATE SCREEN LOCATIONS
		retrieveScreenLocations();
		String uuid = UUID.randomUUID().toString();
		synchronized(userLocation)
		{
			userLocation.clear();
			userLocation.put(uuid, location);
		}

		if(LOG.isDebugEnabled()) LOG.debug("location of user: "+location);
		if(LOG.isDebugEnabled()) LOG.debug("Location of screens: ");
		for (int i=0; i<screenLocations.size(); i++){
			if(LOG.isDebugEnabled()) LOG.debug("Screen location: "+i+": "+screenLocations.get(i));
		}

		if (!location.trim().equalsIgnoreCase(currentUsedScreenLocation.trim())){
			//if near a screen
			if (this.matchesLocation(location)){
				//check if the user is already using another screen
				if (this.hasSession){
					if(LOG.isDebugEnabled()) LOG.debug("Releasing previous screen session from: "+currentUsedScreenIP);
					//release currently used screen
					SocketClient sClient = new SocketClient(currentUsedScreenIP);
					sClient.logOut(userSession);
					if(LOG.isDebugEnabled()) LOG.debug("Sent logout msg to: "+currentUsedScreenIP);
					this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreenIP);
					if(LOG.isDebugEnabled()) LOG.debug("Released screen: "+currentUsedScreenIP);
					this.hasSession = false;
					LOG.info(this.userIdentity.getBareJid() + " has finished a session with " + currentUsedScreenLocation);
				}

				//REQUEST ACCESS - RETURNS FALSE IF NOT IN USE
				synchronized(this.waitingRequests)
				{
					if(!waitingRequests.contains(location))
					{
						if(LOG.isDebugEnabled()) LOG.debug("CURRENT LOCATION IS NOT STORED IN WAITING REQUESTS, CHECK ACCESS!");
						waitingRequests.add(location);
						if(!this.portalServerRemote.checkAccess(serverIdentity, location))
						{
							if(LOG.isDebugEnabled()) LOG.debug("START NOTIFICATION CONTROL THREAD");
							new Thread(new NotificationControl(uuid, this, this.userFeedback, location)).start();		
						}
					}
					if(LOG.isDebugEnabled()) LOG.debug("CURRENT LOCATION IS IN WAITING REQUESTS, DO NOTHING!");

				}


			}//user is not near a screen
			else{
				if(LOG.isDebugEnabled()) LOG.debug("User not near screen");
				//if he's using a screen
				if (this.hasSession){
					if(LOG.isDebugEnabled()) LOG.debug("User in session with portal GUI. Attempting to logout");
					//release resource
					this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreenLocation);

					this.hasSession = false;


					SocketClient socketClient = new SocketClient(this.currentUsedScreenIP);
					socketClient.endSession(this.userSession.getUserIdentity());
					this.currentUsedScreenIP = "";
					this.currentUsedScreenLocation = "";

					this.servRuntimeSocketThread.finalize();
					DisplayEvent dEvent = new DisplayEvent(this.currentUsedScreenIP, DisplayEventConstants.DEVICE_UNAVAILABLE);
					InternalEvent iEvent = new InternalEvent(EventTypes.DISPLAY_EVENT, "displayUpdate", "org/societies/css/device", dEvent);
					try {
						this.evMgr.publishInternalEvent(iEvent);
					} catch (EMSException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else{
					if(LOG.isDebugEnabled()) LOG.debug("User not in a session with the screen. Nothing to do.");
				}
			}
		}else{
			if(LOG.isDebugEnabled()) LOG.debug("Ignoring same value for symloc> new: "+location+" - current: "+this.currentUsedScreenLocation);
		}
	}


	@Override
	public void displayImage(String serviceName, String pathToFile){
		if (this.hasSession){

			BinaryDataTransfer dataTransfer = new BinaryDataTransfer(currentUsedScreenIP);
			dataTransfer.sendImage(this.userIdentity.getJid(), pathToFile);


		}

	}

	@Override
	public void displayImage(String serviceName, URL remoteImageLocation){
		if (this.hasSession){

			SocketClient socketClient = new SocketClient(currentUsedScreenIP);

			socketClient.sendImage(userSession, remoteImageLocation);


		}

	}


	@Override
	public void sendNotification(String serviceName, String text){
		if (this.hasSession){
			if (this.userSession.containsService(serviceName)){
				SocketClient socketClient = new SocketClient(currentUsedScreenIP);
				socketClient.sendText(serviceName, userSession, text);


			}
		}

	}

	@Override
	public void registerDisplayableService(IDisplayableService service, String serviceName, URL executableLocation, boolean requiresKinect){
		ServiceInfo sInfo  = new ServiceInfo(service, serviceName, executableLocation.toString(), 0, requiresKinect);
		this.userSession.addService(sInfo);
	}

	@Override
	public void registerDisplayableService(IDisplayableService service, String serviceName, URL executableLocation, int servicePortNumber, boolean requiresKinect) {
		ServiceInfo sInfo  = new ServiceInfo(service, serviceName, executableLocation.toString(), servicePortNumber, requiresKinect);
		this.userSession.addService(sInfo);

	}


	/*
	 * get/set methods
	 */


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
		this.userIdentity = idMgr.getThisNetworkNode();
	}

	/**
	 * @return the userFeedback
	 */
	public IUserFeedback getUserFeedback() {
		return userFeedback;
	}

	/**
	 * @param userFeedback the userFeedback to set
	 */
	public void setUserFeedback(IUserFeedback userFeedback) {
		this.userFeedback = userFeedback;
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

	/**
	 * @return the portalServerRemote
	 */
	public IDisplayPortalServer getPortalServerRemote() {
		return portalServerRemote;
	}

	/**
	 * @param portalServerRemote the portalServerRemote to set
	 */
	public void setPortalServerRemote(IDisplayPortalServer portalServerRemote) {
		this.portalServerRemote = portalServerRemote;
	}

	/**
	 * @return the evMgr
	 */
	public IEventMgr getEvMgr() {
		return evMgr;
	}

	/**
	 * @param evMgr the evMgr to set
	 */
	public void setEvMgr(IEventMgr evMgr) {
		this.evMgr = evMgr;
	}

	public void notifyServiceStarted(String serviceName) {
		if (this.userSession.containsService(serviceName)){
			if(LOG.isDebugEnabled()) LOG.debug("Found service: "+serviceName+". Calling serviceStarted method");
			ServiceInfo sInfo = this.userSession.getService(serviceName);
			if (sInfo!=null){
				IDisplayableService service = sInfo.getService();
				if (service!=null){
					service.serviceStarted(currentUsedScreenIP);
					return;
				}
			}
		}
		if(LOG.isDebugEnabled()) LOG.debug("Could not find service: "+serviceName);
	}
	public void notifyServiceStopped(String serviceName) {
		if (this.userSession.containsService(serviceName)){
			if(LOG.isDebugEnabled()) LOG.debug("Found service: "+serviceName+". Calling serviceStopped method");
			ServiceInfo sInfo = this.userSession.getService(serviceName);
			if (sInfo!=null){
				IDisplayableService service = sInfo.getService();
				if (service!=null){
					service.serviceStopped(currentUsedScreenIP);
					return;
				}
			}
		}

		if(LOG.isDebugEnabled()) LOG.debug("Could not find service: "+serviceName);

	}

	public void notifyLogOutEvent() {

		if (this.hasSession){
			//release resource
			this.portalServerRemote.releaseResource(serverIdentity, userIdentity.getJid(), currentUsedScreenLocation);

			this.hasSession = false;
			DisplayEvent dEvent = new DisplayEvent(this.currentUsedScreenIP, DisplayEventConstants.DEVICE_UNAVAILABLE);

			this.currentUsedScreenIP = "";
			this.currentUsedScreenLocation = "";
			InternalEvent iEvent = new InternalEvent(EventTypes.DISPLAY_EVENT, "displayUpdate", "org/societies/css/device", dEvent);
			try {
				this.evMgr.publishInternalEvent(iEvent);
			} catch (EMSException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

	}


}
