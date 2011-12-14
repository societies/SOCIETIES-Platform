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

package org.societies.orchestration.lifecyclemgmt.impl;

import org.societies.css.cssdirectory.api.ICssDirectoryCloud;
import org.societies.css.cssdirectory.api.ICssDirectoryRich;
import org.societies.css.cssdirectory.api.ICssDirectoryLight;

import org.societies.cssmgmt.cssdiscovery.api.ICssDiscovery;

import org.societies.cis.management.api.CisAcitivityFeed;
import org.societies.cis.management.api.ServiceSharingRecord;
import org.societies.cis.management.api.CisActivity;
import org.societies.cis.management.api.CisRecord;

import org.societies.context.user.similarity.api.platform.IUserCtxSimilarityEvaluator;

import org.societies.context.user.prediction.api.platform.IUserCtxPredictionMgr;

import org.societies.context.user.db.api.platform.IUserCtxDBMgr;

import org.societies.context.user.history.api.platform.IUserCtxHistoryMgr;

import org.societies.api.mock.EntityIdentifier;

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
	
	private Css linkedCss; // No datatype yet defined for CSS
	private EntityIdentifier dpi;
	
    private CisRecord linkedCis;
    
    private Domain linkedDomain;  // No datatype yet representing a domain
	
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
	
	public IAutomaticCommunityConfigurationManager(CisRecord linkedCis) {
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