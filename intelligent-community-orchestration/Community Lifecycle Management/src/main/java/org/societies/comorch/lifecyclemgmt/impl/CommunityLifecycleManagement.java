package org.societies.comorch.lifecyclemgmt.impl;

import org.societies.context.broker.api.IUserCtxBroker;
import org.societies.context.broker.api.ICommunityCtxBroker;

/**
 * This is the class for the Community Lifecycle Management component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class CommunityLifecycleManagement {
	
	private AutomaticCommunityCreationManager autoCreationManager;
	private AutomaticCommunityConfigurationManager autoConfigurationManager;
	private AutomaticCommunityDeletionManager autoDeletionManager;
	
	/*
     * Constructor for CommunityLifecycleManagement
     * 
	 * Description: The constructor creates the CommunityLifecycleManagement
	 *              component either on a given CSS, or abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				?
	 */
	
	public CommunityLifecycleManagement() {
		
	}
	
	
	
	public void createCiss() {
		
	}
	
	public void configureCiss() {
		
	}
	
	public void deleteCiss() {
		
	}
	
	public void processPreviousLongTimeCycle() {
		AutomaticCommunityCreationManager();
	}
	
	public void processPreviousShortTimeCycle() {
		
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
		
	}
	
	public void stimulusForCommunityDeletionDetected() {
		
	}
}