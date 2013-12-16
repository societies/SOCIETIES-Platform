package org.societies.webapp.controller.displayscreens;

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

import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;
import org.societies.api.internal.context.broker.ICtxBroker;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;

import org.societies.api.css.devicemgmt.display.Screen;
import org.societies.webapp.controller.rfid.RFidServerController;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "screenData", eager = true)
@SessionScoped
public class ScreenData {


	private Logger log = LoggerFactory.getLogger(ScreenData.class);

	@ManagedProperty(value="#{internalCtxBroker}")
	private ICtxBroker ctxBroker;

	@ManagedProperty(value="#{commMngrRef}")
	private ICommManager commManager;
	
	@ManagedProperty(value = "#{rfidServerController}")
	private RFidServerController rfidServerController;

	private ContextRetriever contextRetriever;

	private ArrayList<Screen> screenList;
	private List<String> screenIDs;
	private ArrayList<String> otherLocations;


	private String newScreenIP;

	private String newScreenID;

	private String newScreenLocationDesc;
	private String newOtherLocation;

	private Screen selectedScreen;
	private String selectedOtherLoc;

	private Pattern pattern;
	private Matcher matcher;


	private static final String IPADDRESS_PATTERN =
			"^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
					+ "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

	public ScreenData() {
	}

	@PostConstruct
	public void init() {
		this.contextRetriever = new ContextRetriever(getCtxBroker(), getCommManager().getIdManager().getThisNetworkNode());
		// this.screenToIP = this.contextRetriever.getScreenToIP();
		log.debug("Setup contextRe");
		this.screenList = new ArrayList<Screen>();
		this.screenIDs = new ArrayList<String>();
		this.otherLocations = new ArrayList<String>();
		this.newScreenID="";
		this.newScreenIP="";
		this.newScreenLocationDesc="";

		//GET SCREENS FROM CONTEXT
		this.screenList = this.contextRetriever.getScreens();
		this.otherLocations = this.contextRetriever.getOtherLocations();
		updateScreenIDs();
	}

	//UPDATE LIST OF SCREEN ID'S
	public void updateScreenIDs() {
		this.screenIDs.clear();
		for(Screen screen : this.screenList)
		{
			this.screenIDs.add(screen.getScreenID());
		}

	}

	public void addOtherLocation() {
		if(!this.otherLocations.contains(this.newOtherLocation)) this.otherLocations.add(this.newOtherLocation);
		this.newOtherLocation = "";
	}



	//CHECKS IP IS VALID IP
	public boolean checkIp(String sip) {
		if(log.isDebugEnabled())
			log.debug(sip);
		pattern = Pattern.compile(IPADDRESS_PATTERN);
		matcher = pattern.matcher(sip);
		return matcher.matches();
	}




	//CHECKS USER INPUT THEN ADDS SCREEN TO DB
	public void addScreen() {
		if(log.isDebugEnabled()) log.debug("IN VALIDATION");
		RequestContext context = RequestContext.getCurrentInstance();
		FacesMessage msg = null;
		boolean screenAdded = false;
		if(!this.newScreenID.isEmpty() && !this.newScreenIP.isEmpty() && !this.newScreenLocationDesc.isEmpty()) {
			if(checkIp(this.newScreenIP))
			{
				if(!this.screenIDs.contains(this.newScreenID))
				{
					screenAdded = true;
					this.screenList.add(new Screen(this.newScreenID, this.newScreenIP, this.newScreenLocationDesc));
					this.contextRetriever.updateContext(this.screenList, this.otherLocations);
					this.screenIDs.add(this.newScreenID);
					if(log.isDebugEnabled()) log.debug("Screen: " + this.newScreenID + " added to DB.");
					msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Screen successfully added", "Screen: " + this.newScreenID + " has successfully been " +
							"added to the database.");
				}
				else 
				{
					if(log.isDebugEnabled()) log.debug("Screen: " + this.newScreenID + " already exists!");
					screenAdded = false;
					msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Screen Exists!", "Screen: " + this.newScreenID +" already exists!");

				}
			}
			else
			{
				if(log.isDebugEnabled()) log.debug("Screen: " + this.newScreenID + " does NOT have valid IP - NOT added to DB.");
				screenAdded = false;
				msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid IP", "Please give a valid IP address for: " + this.newScreenID +".");
			}
		}
		else {
			if(log.isDebugEnabled()) log.debug("User left all fields empty.");
			screenAdded = false;
			msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid fields!", "Please ensure all fields are filled in.");
		}
		this.newScreenID="";
		this.newScreenIP="";
		this.newScreenLocationDesc="";

		FacesContext.getCurrentInstance().addMessage(null, msg);
		context.addCallbackParam("screenAdded", screenAdded);        
	}

	public void deleteOtherLoc() {
		checkWakeupUnit(this.selectedOtherLoc);
		this.otherLocations.remove(this.selectedOtherLoc);
		this.contextRetriever.updateContext(this.screenList, this.otherLocations);
	}

	//DELETES USER SELECTED SCREENS FROM DB
	public void deleteScreen() {
		FacesMessage msg = null;
		this.screenList.remove(this.selectedScreen);
		this.screenIDs.remove(this.selectedScreen.getScreenID());
		checkWakeupUnit(this.selectedScreen.getScreenID());
		this.contextRetriever.updateContext(this.screenList, this.otherLocations);
		msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Delete Screen", this.selectedScreen.getScreenID() + " has been deleted!");
		FacesContext.getCurrentInstance().addMessage(null, msg);
	}
	
	public void checkWakeupUnit(String location) {
		this.rfidServerController.deleteWakeupWithLoc(location);
	}

	public ArrayList<String> getAllLocations() {
		ArrayList<String> allLocs = new ArrayList<String>(this.screenIDs);
		allLocs.addAll(this.otherLocations);
		return allLocs;
	}

	public String getNewScreenIP() {
		return newScreenIP;
	}

	public void setNewScreenIP(String newScreenIP) {
		this.newScreenIP = newScreenIP;
	}

	public String getNewScreenID() {
		return newScreenID;
	}

	public void setNewScreenID(String newScreenID) {
		this.newScreenID = newScreenID;
	}

	public List<Screen> getScreenList() {
		return screenList;
	}


	public String getNewScreenLocationDesc() {
		return newScreenLocationDesc;
	}

	public void setNewScreenLocationDesc(String newScreenLocationDesc) {
		this.newScreenLocationDesc = newScreenLocationDesc;
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
	 * @return the ctxBroker
	 */
	public ICommManager getCommManager() {
		return commManager;
	}

	/**
	 * @param ctxBroker the ctxBroker to set
	 */
	public void setCommManager(ICommManager commManager) {
		this.commManager = commManager;
	}

	public List<String> getScreenIDs() {
		return screenIDs;
	}

	public void setScreenIDs(List<String> screenIDs) {
		this.screenIDs = screenIDs;
	}


	public ArrayList<String> getOtherLocations() {
		return otherLocations;
	}

	public String getNewOtherLocation() {
		return newOtherLocation;
	}

	public void setNewOtherLocation(String newOtherLocation) {
		this.newOtherLocation = newOtherLocation;
	}
	
	public Screen getSelectedScreen() {
		return selectedScreen;
	}

	public void setSelectedScreen(Screen selectedScreen) {
		this.selectedScreen = selectedScreen;
	}

	public String getSelectedOtherLoc() {
		return selectedOtherLoc;
	}

	public void setSelectedOtherLoc(String selectedOtherLoc) {
		this.selectedOtherLoc = selectedOtherLoc;
	}
	
	public RFidServerController getRfidServerController() {
		return rfidServerController;
	}

	public void setRfidServerController(RFidServerController rfidServerController) {
		this.rfidServerController = rfidServerController;
	}


}
