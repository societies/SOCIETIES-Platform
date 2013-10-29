package org.societies.webapp;

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

import org.hibernate.SessionFactory;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.css.devicemgmt.display.IDisplayPortalServer;
import org.societies.webapp.dao.ScreenDAO;
import org.societies.webapp.model.ScreenDataModel;
import org.societies.webapp.model.Screen;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.context.FacesContext;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@ManagedBean(name = "screenData", eager = true)
public class ScreenData implements Serializable {


    private Logger log = LoggerFactory.getLogger(ScreenData.class);

    private ScreenDataModel screenDataModel;
    private ScreenDAO screenDAO;
    private List<Screen> screenList;

    //@ManagedProperty(value = "#{displayPortalServer}")
    private IDisplayPortalServer displayPortalServer;

    private Pattern pattern;
    private Matcher matcher;

    private Screen[] selectedScreens;
    
   // @ManagedProperty(value = "#{sessionFactory}")
    private SessionFactory sessionFactory;

    private static final String IPADDRESS_PATTERN =
            "^([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])\\."
                    + "([01]?\\d\\d?|2[0-4]\\d|25[0-5])$";

    public ScreenData() {
    }

   // @PostConstruct
    public void init() {
        this.screenDAO = new ScreenDAO(sessionFactory);    
        refreshScreens();
    }

    //CHECKS IP IS VALID IP
    public boolean checkIp(String sip) {
    	if(log.isDebugEnabled())
        log.debug(sip);
        pattern = Pattern.compile(IPADDRESS_PATTERN);
        matcher = pattern.matcher(sip);
        return matcher.matches();
    }
    
    public void releaseResource(String location)
    {
    	displayPortalServer.releaseResource(location);
    }


    //CHECKS USER INPUT THEN ADDS SCREEN TO DB
    public void addScreen(Screen screen) {
        if(log.isDebugEnabled()) log.debug("IN VALIDATION");
        RequestContext context = RequestContext.getCurrentInstance();
        FacesMessage msg = null;
        boolean screenAdded = false;

        String screenID = screen.getScreenID();
        String locationID = screen.getLocationID();
        String ipAddress = screen.getIpAddress();

         if(!screenID.isEmpty()  && !locationID.isEmpty()  && !ipAddress.isEmpty()) {
            if(checkIp(ipAddress))
            {
                screenAdded = true;
                screenDAO.save(screen);
                refreshScreens();
                if(log.isDebugEnabled()) log.debug("Screen: " + screenID + " added to DB.");
                msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Screen successfully added", "Screen: " + screenID + " has successfully been " +
                        "added to the database.");
            }
            else
            {
                if(log.isDebugEnabled()) log.debug("Screen: " + screenID + " does NOT have valid IP - NOT added to DB.");
                screenAdded = false;
                msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid IP", "Please give a valid IP address for: " + screenID +".");
            }
        }
        else {
            if(log.isDebugEnabled()) log.debug("User left all fields empty.");
            screenAdded = false;
            msg = new FacesMessage(FacesMessage.SEVERITY_WARN, "Invalid fields!", "Please ensure all fields are filled in.");
        }
        FacesContext.getCurrentInstance().addMessage(null, msg);
        context.addCallbackParam("screenAdded", screenAdded);
    }

    //RETRIEVES SCREEN FROM DB TO BE DISPLAYED
    public void refreshScreens()
    {
        screenList = screenDAO.getAllScreens();
        this.screenDataModel = new ScreenDataModel(screenList);
        this.displayPortalServer.setScreens();
        if(log.isDebugEnabled()) log.debug("Current Screen List: " + screenList.toString());
    }


    public void setSelectedScreens(Screen[] selectedScreens) {
        this.selectedScreens = selectedScreens;
    }

    public Screen[] getSelectedScreens() {
        return selectedScreens;
    }

    public ScreenDataModel getScreenDataModel() {
        return screenDataModel;
    }

    //DELETES USER SELECTED SCREENS FROM DB
    public void delete() {
        int count = 0;
        FacesMessage msg = null;
        for(Screen screens : selectedScreens)
        {
            if(log.isDebugEnabled()) log.debug("Deleting screen with ID: " + screens.getScreenID());
            screenDAO.deleteScreens(screens);
            count++;
        }
        msg = new FacesMessage(FacesMessage.SEVERITY_INFO, "Delete Screens", String.valueOf(count) + " screens have been deleted!");
        FacesContext.getCurrentInstance().addMessage(null, msg);
        refreshScreens();
        selectedScreens = null;
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
    
	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory
	 *            the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


}
