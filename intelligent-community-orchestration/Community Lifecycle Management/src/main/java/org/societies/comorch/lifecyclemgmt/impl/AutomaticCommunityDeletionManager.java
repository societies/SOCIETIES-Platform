package org.societies.comorch.lifecyclemgmt.impl;

import org.societies.css.cssdirectory.api.ICSSDirectoryCloud;
import org.societies.css.cssdirectory.api.ICSSDirectoryRich;
import org.societies.css.cssdirectory.api.ICSSDirectoryLight;

import org.societies.cssmgmt.cssdiscovery.api.ICSSDiscovery;

import org.societies.cis.management.api.CISAcitivityFeed;
import org.societies.cis.management.api.ServiceSharingRecord;
import org.societies.cis.management.api.CISActivity;
import org.societies.cis.management.api.CISRecord;

import org.societies.context.user.similarity.api.platform.IUserCtxSimilarityEvaluator;

import org.societies.context.user.prediction.api.platform.IUserCtxPredictionMgr;

import org.societies.context.user.db.api.platform.IUserCtxDBMgr;

import org.societies.context.user.history.api.platform.IUserCtxHistoryMgr;

/**
 * This is the class for the Automatic Community Deletion Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class AutomaticCommunityDeletionManager {

	/*
     * Constructor for AutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityDeletionManager(Css linkedCss) {
		
	}
	
	/*
     * Constructor for AutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public AutomaticCommunityDeletionManager(Domain linkedDomain) {
		
	}
	
	public void determineCissToDelete() {
		
	}

}