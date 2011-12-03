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
 * This is the class for the Automatic Community Configuration Manager component
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the configuration of CISs.
 * Configuration here refers to the nature, or structure, of a CIS itself being changed, and not
 * a sub-CIS or parent CIS being changed in any way, although a CIS 'splitting' into two or more
 * CISs (where the original CIS is deleted in the process) is covered as configuration. It includes:
 * 
 *   - CIS members being added/removed
 *   - CIS attributes being changed, including its name, definition, membership criteria, goal/purpose (if any), etc.
 *   - CIS splitting or merging into/with other CISs.
 *   - And more
 *  
 * This is achieved by perform various forms of analysis on CSSs, CISs, their attributes, and their
 * connections, and using different algorithms. Social network analysis methods and similarity of users
 * -based approaches and algorithms will be used, including an
 * approach that views groups/CISs as either ongoing (non-terminating, with no deadline or 
 * fulfillable purpose for existing) or temporary (not going to last, e.g. because it exists just
 * for a goal that will be completed, or has a clear lifespan, or group breakdown is inevitable). 
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class AutomaticCommunityConfigurationManager {
	
	private Css linkedCss;
	private EntityIdentifier dpi;
	
    private CisRecord linkedCis;
    
    private Domain linkedDomain;
	
	/*
     * Constructor for AutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityConfigurationManager(Css linkedCss, EntityIdentifier dpi) {
		this.linkedCss = linkedCss;
		this.dpi = dpi;
	}
	
	/*
     * Constructor for AutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component abstractly at a domain/cloud-level.
	 * Parameters: 
	 * 				linkedDomain - the domain on behalf of which this object is to operate.
	 */
	
	public AutomaticCommunityConfigurationManager(Domain linkedDomain) {
	    this.linkedDomain = linkedDomain;
	}
	/*
     * Constructor for IAutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CIS.
	 * Parameters: 
	 * 				linkedCis - the Cis that this object will be used to check for configuration on.
	 */
	
	public IAutomaticCommunityConfigurationManager(Cis linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/*
	 * Description: The method looks for CISs to configure, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public void determineCissToConfigure() {
		if (linkedCss != null) {
			CISRecord[] records = ICISManager.getCisList(/** CISs administrated by the CSS */);
		}
		if (linkedCis != null) {
			CISRecord[] records = ICISManager.getCisList(/** This CIS */);
		}
		if (linkedDomain != null) {
			CISRecord[] records = ICISManager.getCisList(/** CISs in the domain */);
		}
		
		//process
		
		//invoke UserAgent suggestion GUI for configurations
		//OR
		//automatically call CIS management functions to configure CISs
		
	}
}