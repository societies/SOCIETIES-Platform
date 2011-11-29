package org.societies.comorch.lifecyclemgmt;

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
 * This is the class for the Automatic Community Creation Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class AutomaticCommunityCreationManager {
	
	/*
     * Constructor for AutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityCreationManager(Css linkedCss) {
		
	}
	
	/*
     * Constructor for AutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public AutomaticCommunityCreationManager(Domain linkedDomain) {
		
	}
	
	public return ArrayList<String> getIDsOfInteractingCsss() {
		
	}
	
	public ArrayList<Cis> identifyCissToCreate() {
		ArrayList<String> cisIDs = getIDsOfInteractingCsss();
		
		ArrayList<Cis> cissToCreate;
		// processing
		
		return cissToCreate;
		
	}
	
	public boolean isSituationSuggestiveOfTemporaryCISCreation() {
		boolean tempCisPossibility = true;
		return tempCisPossibility;
	}
}