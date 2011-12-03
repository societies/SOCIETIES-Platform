package org.societies.comorch.lifecyclemgmt.impl;

import org.societies.context.broker.api.IUserCtxBroker;
import org.societies.context.broker.api.ICommunityCtxBroker;

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
	
	private Css linkedCss;
	private EntityIdentifier dpi;
	
    private CisRecord linkedCis;
    
    private Domain linkedDomain;
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(Css linkedCss, EntityIdentifier dpi) {
		this.linkedCss = linkedCss;
		this.dpi = dpi;
	}
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				
	 */
	
	public CommunityLifecycleManagement(Domain linkedDomain) {
		this.linkedDomain = linkedDomain;
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
	
	public void createCiss() {
		
	}
	
	/**
	 * User Interface method - trigger configuration of CIS following UI request.
	 */
	
	public void configureCiss() {
		
	}
	
	/**
	 * User Interface method - trigger deletion of CIS following UI request.
	 */
	
	public void deleteCiss() {
		
	}
	
	public void processPreviousLongTimeCycle() {
		autoCreationManager.determinCissToCreate("extensive");
		autoConfigurationManager.determineCissToConfigure("extensive");
		autoDeletionManager.determineCissToDelete("extensive");
	}
	
	public void processPreviousShortTimeCycle() {
		autoCreationManager.determineCissToCreate("not extensive");
		autoConfigurationManager.determineCissToConfigure("not extensive");
	}
	
	public void loop() {
		
		new Thread.start() {
			while (true) {
				Thread.sleep(10000);
				processPreviousShortTimeCycle;
		    }
		};
		
		new Thread.start() {
			while (true) {
				Thread.sleep(220000000);
				processPreviousLongTimeCycle;
		    }
		};
		
	}
	
	public void stimulusForCommunityCreationDetected() {
		autoCreationManager.determineCissToCreate();
	}
	
	public void stimulusForCommunityDeletionDetected() {
		autoDeletionManager.determineCissToDelete();
		
	}
}