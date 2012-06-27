/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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

package org.societies.orchestration.EgocentricCommunityAnalyser.impl;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import static org.mockito.Mockito.*;

//import org.societies.api.internal.cis.management.ICisManager;
//import org.societies.api.cis.management.ICisManager;
//import org.societies.api.cis.management.ICis;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommCallback;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
//import org.societies.api.internal.cis.management.ICis;
import org.societies.api.internal.context.broker.ICtxBroker;
//import org.societies.api.internal.css.devicemgmt.devicemanager.IDeviceManager;
import org.societies.api.internal.servicelifecycle.IServiceDiscovery;
import org.societies.api.internal.servicelifecycle.IServiceDiscoveryCallback;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
//import org.societies.api.cis.management.ICis;

/**import org.societies.api.cis.management.ICis;
import org.societies.api.cis.management.ICisManager;
import org.societies.api.cis.management.ICisOwned;
import org.societies.api.cis.management.ICisEditor;*/

import org.societies.orchestration.api.ICis;
import org.societies.orchestration.api.ICisManager;
import org.societies.orchestration.api.ICisOwned;
import org.societies.orchestration.api.ICisParticipant;
//import org.societies.orchestration.api.ICisEditor;

//import org.societies.api.internal.context.broker.ICommunityCtxBroker;

import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
//import org.societies.api.comm.xmpp.datatypes.Identity;
import org.societies.api.identity.IIdentityManager;

//import org.societies.api.comm.xmpp.datatypes.Identity;
//import org.societies.comm.examples.commsmanager.impl.CommsServer;
//import org.societies.comm.xmpp.interfaces.ICommCallback;

/**
 * This is the class for the Egocentric Community Analyser component
 * 
 * Works on a user's CSS to identify potential CIS creation,
 * configuration, and deletion opportunities that aim to benefit
 * both the user and as many other users as possible given
 * the constraints on data available to do so.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class EgocentricCommunityAnalyser //implements ICommCallback
{
	
	//private static Logger LOG = LoggerFactory.getLogger(EgocentricCommunityAnalyser.class);
	
	private EgocentricCommunityCreationManager egocentricCreationManager;
	private EgocentricCommunityConfigurationManager egocentricConfigurationManager;
	private EgocentricCommunityDeletionManager egocentricDeletionManager;
	
	private ArrayList<ICis> userCiss;
	private HashMap<IIdentity, String> userCissMetadata;
	
	private IIdentity linkedCss;
	
	private Date lastTemporaryCheck;
	private Date lastOngoingCheck;
	
	private ICisManager cisManager;
	
	private ICommManager commManager;
    private ICommCallback commCallback;
    private IIdentityManager identityManager;
	
	private IServiceDiscovery serviceDiscovery;
	private IServiceDiscoveryCallback serviceDiscoveryCallback;
	
//	private IDeviceManager deviceManager;
	
	private int temporaryCheckInterval;
	private int ongoingCheckInterval;
	
	/*
     * Constructor for EgocentricCommunityAnalyser
     * 
	 * Description: The constructor creates the EgocentricCommunityAnalyser
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of. (Currently can only be a user CSS)
	 */
	
	public EgocentricCommunityAnalyser(IIdentity linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		
		userCiss = new ArrayList<ICis>();
		userCissMetadata = new HashMap<IIdentity, String>();
		
		lastTemporaryCheck = new Date();
		lastOngoingCheck = new Date();
		
		temporaryCheckInterval = 1000 * 60 * 3;
		ongoingCheckInterval = 1000 * 60 * 60 * 24;
		//else
		//	this.linkedDomain = linkedEntity;
	}
	
	public void removeObsoleteRecordedCiss() {
		ICis[] ciss = cisManager.getCisList(null);
		boolean notFound = true;
		for (int m = 0; m < ciss.length; m++) {
		    if (userCissMetadata.get(ciss[m].getCisId()) == null)
		        userCissMetadata.remove(ciss[m]);
		}
	}
	
	public void addNewCissToRecords(ArrayList<String> newCissMetadata) {
		for (int i = 0; i < newCissMetadata.size(); i++) {
				    if ((userCissMetadata.get(newCissMetadata.get(i).split("---")[0]) == null)) {
				        try {
							userCissMetadata.put(identityManager.fromJid(newCissMetadata.get(i).split("---")[0]), newCissMetadata.get(i));
						} catch (InvalidFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
				    else if (userCissMetadata.get(newCissMetadata.get(i).split("---")[0]) != null) {
		                try {
							userCissMetadata.put(identityManager.fromJid(newCissMetadata.get(i).split("---")[0]), newCissMetadata.get(i));
						} catch (InvalidFormatException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				    }
	    }
	}
	
	public void processPreviousLongTimeCycle() {
		removeObsoleteRecordedCiss();
		
		addNewCissToRecords(egocentricCreationManager.identifyCissToCreate("extensive", userCissMetadata));
		addNewCissToRecords(egocentricConfigurationManager.identifyCissToConfigure(userCissMetadata));
		removeObsoleteRecordedCiss();
		
		egocentricDeletionManager.identifyCissToDelete(userCissMetadata);
	}
	
	public void processPreviousShortTimeCycle() {
		removeObsoleteRecordedCiss();
		addNewCissToRecords(egocentricCreationManager.identifyCissToCreate("not extensive", userCissMetadata));
		//egocentricConfigurationManager.identifyCissToConfigure();
	}
	
	public void loop() {
		
		new ShortSleepThread().start();
        new LongSleepThread().start();
		
	}
	
	class ShortSleepThread extends Thread {
		
		public void run() {
			while (true) {
				//try {
					Date date = new Date();
					if (date.getTime() >= (lastTemporaryCheck.getTime() + (temporaryCheckInterval))) {
						processPreviousShortTimeCycle();
						lastTemporaryCheck.setTime(date.getTime());
					}
					//Thread.sleep(10000);
				//} //catch (InterruptedException e) {
					// TODO Auto-generated catch block
					//e.printStackTrace();
				//}
				
		    }
		}
	}
	
    class LongSleepThread extends Thread {
		
		public void run() {
			while (true) {
				Date date = new Date();
				if (date.getTime() >= (lastOngoingCheck.getTime() + (ongoingCheckInterval))) {
					processPreviousLongTimeCycle();
					lastOngoingCheck.setTime(date.getTime());
				}
				//try {
				//	Thread.sleep(220000000);
				//} catch (InterruptedException e) {
					// TODO Auto-generated catch block
				//	e.printStackTrace();
				//}
				
		    }
		}
	}
    
    public void initialiseEgocentricCommunityAnalyser() {
    	//getCommManager().register(this);

    	if (linkedCss != null) {
    		new EgocentricCommunityAnalyser(linkedCss, "CSS");
    		loop();
    	}
    	/**else if (linkedDomain != null) {
    		new CommunityLifecycleManagement(linkedCss, "Domain");
    		loop();
    	}*/
    }
    
    public IIdentity getLinkedCss() {
    	return linkedCss;
    }
    
    public void setLinkedCss(IIdentity linkedCss) {
    	this.linkedCss = linkedCss;
    }
    
    /**public IIdentity getLinkedDomain() {
    	return linkedDomain;
    }
    
    public void setLinkedDomain(IIdentity linkedDomain) {
    	this.linkedDomain = linkedDomain;
    }*/
    
    public EgocentricCommunityCreationManager getEgocentricCreationManager() {
    	return egocentricCreationManager;
    }
    
    public void setEgocentricCreationManager(EgocentricCommunityCreationManager egocentricCreationManager) {
    	this.egocentricCreationManager = egocentricCreationManager;
    }
    
    public EgocentricCommunityConfigurationManager getEgocentricConfigurationManager() {
    	return egocentricConfigurationManager;
    }
    
    public void setEgocentricConfigurationManager(EgocentricCommunityConfigurationManager egocentricConfigurationManager) {
    	this.egocentricConfigurationManager = egocentricConfigurationManager;
    }
    
    public EgocentricCommunityDeletionManager getEgocentricDeletionManager() {
    	return egocentricDeletionManager;
    }
    
    public void setEgocentricDeletionManager(EgocentricCommunityDeletionManager egocentricDeletionManager) {
    	this.egocentricDeletionManager = egocentricDeletionManager;
    }
    
    public ICommManager getCommManager() {
    	return commManager;
    }
    
    public void setCommManager(ICommManager commManager) {
    	this.commManager = commManager;
    }
    
    public ICommCallback getCommCallback() {
    	return commCallback;
    }
    
    public void setCommCallback(ICommCallback commCallback) {
    	this.commCallback = commCallback;
    }
    
    public IServiceDiscovery getServiceDiscovery() {
    	return serviceDiscovery;
    }
    
    public void setServiceDiscovery(IServiceDiscovery serviceDiscovery) {
    	this.serviceDiscovery = serviceDiscovery;
    }
    
    public IServiceDiscoveryCallback getServiceDiscoveryCallback() {
    	return serviceDiscoveryCallback;
    }
    
    public void setServiceDiscoveryCallback(IServiceDiscoveryCallback serviceDiscoveryCallback) {
    	this.serviceDiscoveryCallback = serviceDiscoveryCallback;
    }
    
//    public IDeviceManager getDeviceManager() {
//    	return deviceManager;
//    }
//    
//    public void setDeviceManager(IDeviceManager deviceManager) {
//    	this.deviceManager = deviceManager;
//    }
    
    /**Returns the list of package names of the message beans you'll be passing*/
    public List<String> getJavaPackages() {
		return null;
    	
    }
    
    /**Returns the list of namespaces for the message beans you'll be passing*/
    public List<String> getXMLNamespaces() {
    	return null;
    }
    
    /** Put your functionality here if there is NO return object, ie, VOID */
    //public void receiveMessage(Stanza stanza, Object messageBean) {
    //	return null;
    //}
    
    /** Put your functionality here if there IS a return object */
    //public Object getQuery(Stanza stanza, Object messageBean) {
    //	return null;
    //}
    
    /** Put your functionality here if there IS a return object and you are updating also */
    //public Object setQuery(Stanza arg0, Object arg1) {
    //	return null;
    //}

    
}
