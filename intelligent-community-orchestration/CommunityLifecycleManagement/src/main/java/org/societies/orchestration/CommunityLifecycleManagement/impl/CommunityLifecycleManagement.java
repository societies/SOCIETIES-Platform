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

package org.societies.orchestration.CommunityLifecycleManagement.impl;

import org.societies.api.internal.cis.cis_management.CisRecord;
import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.ICommunityCtxBroker;

import org.societies.api.mock.EntityIdentifier;

/**
 * This is the class for the Community Lifecycle Management component
 * 
 * Superclass of the three community lifecycle manager components, delegates CIS
 * lifecycle orchestration work to them.
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityLifecycleManagement {
	
	private AutomaticCommunityCreationManager autoCreationManager;
	private AutomaticCommunityConfigurationManager autoConfigurationManager;
	private AutomaticCommunityDeletionManager autoDeletionManager;
	
	//private Css linkedCss; // No datatype yet defined for CSS
	private EntityIdentifier linkedCss;
	
    private CisRecord linkedCis;
    
    //private Domain linkedDomain;  // No datatype yet representing a domain
	private EntityIdentifier linkedDomain;
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public CommunityLifecycleManagement(EntityIdentifier linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		else
			this.linkedDomain = linkedEntity;
	}
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/**
	 * User Interface method - trigger creation of CIS following UI request
	 */
	
	//public void createCiss() {
		
	//}
	
	/**
	 * User Interface method - trigger configuration of CIS following UI request.
	 */
	
	//public void configureCiss() {
		
	//}
	
	/**
	 * User Interface method - trigger deletion of CIS following UI request.
	 */
	
	//public void deleteCiss() {
		
	//}
	
	public void processPreviousLongTimeCycle() {
		autoCreationManager.identifyCissToCreate("extensive");
		autoConfigurationManager.identifyCissToConfigure();
		autoDeletionManager.identifyCissToDelete();
	}
	
	public void processPreviousShortTimeCycle() {
		autoCreationManager.identifyCissToCreate("not extensive");
		autoConfigurationManager.identifyCissToConfigure();
	}
	
	public void loop() {
		
		new ShortSleepThread().start();
        new LongSleepThread().start();
		
	}
	
	//public void stimulusForCommunityCreationDetected() {
		//autoCreationManager.determineCissToCreate();
	//}
	
	//public void stimulusForCommunityDeletionDetected() {
	//	autoDeletionManager.determineCissToDelete();
	//}
	
	class ShortSleepThread extends Thread {
		
		public void run() {
			while (true) {
				try {
					Thread.sleep(10000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				processPreviousShortTimeCycle();
		    }
		}
	}
	
    class LongSleepThread extends Thread {
		
		public void run() {
			while (true) {
				try {
					Thread.sleep(220000000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				processPreviousLongTimeCycle();
		    }
		}
	}
    
    public void intialiseCommunityLifecycleManagement() {
    	if (linkedCss != null) {
    		new CommunityLifecycleManagement(linkedCss, "CSS");
    		loop();
    	}
    	else if (linkedCis != null) {
    		new CommunityLifecycleManagement(linkedCis);
    		loop();
    	}
    	else if (linkedDomain != null) {
    		new CommunityLifecycleManagement(linkedCss, "Domain");
    		loop();
    	}
    }
    
    public EntityIdentifier getLinkedCss() {
    	return linkedCss;
    }
    
    public void setLinkedCss(EntityIdentifier linkedCss) {
    	this.linkedCss = linkedCss;
    }
    
    public CisRecord getLinkedCis() {
    	return linkedCis;
    }
    
    public void setLinkedCis(CisRecord linkedCis) {
    	this.linkedCis = linkedCis;
    }
    
    public EntityIdentifier getLinkedDomain() {
    	return linkedDomain;
    }
    
    public void setLinkedDomain(EntityIdentifier linkedDomain) {
    	this.linkedDomain = linkedDomain;
    }
    
    public AutomaticCommunityCreationManager getAutoCreationManager() {
    	return autoCreationManager;
    }
    
    public void setAutoCreationManager(AutomaticCommunityCreationManager autoCreationManager) {
    	this.autoCreationManager = autoCreationManager;
    }
    
    public AutomaticCommunityConfigurationManager getAutoConfigurationManager() {
    	return autoConfigurationManager;
    }
    
    public void setAutoConfigurationManager(AutomaticCommunityConfigurationManager autoConfigurationManager) {
    	this.autoConfigurationManager = autoConfigurationManager;
    }
    
    public AutomaticCommunityDeletionManager getAutoDeletionManager() {
    	return autoDeletionManager;
    }
    
    public void setAutoDeletionManager(AutomaticCommunityDeletionManager autoDeletionManager) {
    	this.autoDeletionManager = autoDeletionManager;
    }
    
}