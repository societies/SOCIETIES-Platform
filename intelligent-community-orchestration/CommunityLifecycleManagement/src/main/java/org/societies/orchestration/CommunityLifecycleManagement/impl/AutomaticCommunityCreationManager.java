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

import org.societies.api.internal.css_modules.css_directory.ICssDirectory;

import org.societies.api.internal.css_modules.css_discovery.ICssDiscovery;

import org.societies.api.internal.cis.management.CisActivityFeed;
import org.societies.api.internal.cis.management.ServiceSharingRecord;
import org.societies.api.internal.cis.management.CisActivity;
import org.societies.api.internal.cis.management.CisRecord;

import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

import org.societies.api.mock.EntityIdentifier;

/**
 * This is the class for the Automatic Community Creation Manager component.
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the creation of CISs and sub-CISs. This 
 * is achieved by perform various forms of analysis on CSSs, CISs, their attributes, and their
 * connections, and using different algorithms. Social network analysis methods and similarity of users
 * -based approaches and algorithms will be used, including an
 * approach that views groups/CISs as either ongoing (non-terminating, with no deadline or 
 * fulfillable purpose for existing) or temporary (not going to last, e.g. because it exists just
 * for a goal that will be completed, or has a clear lifespan, or group breakdown is inevitable). 
 * 
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

public class AutomaticCommunityCreationManager {
	
	private Css linkedCss; // No datatype yet defined for CSS
	private EntityIdentifier dpi;
	
    private CisRecord linkedSuperCis;
    
    private Domain linkedDomain; // No datatype yet representing a domain
	
	/*
     * Constructor for AutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedCSS - the CSS that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityCreationManager(Css linkedCss, EntityIdentifier dpi) {
		this.linkedCss = linkedCss;
		this.dpi = dpi;
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
		this.linkedDomain = linkedDomain;
	}
	
	/*
     * Constructor for AutomaticCommunityCreationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityCreationManager
	 *              component abstractly at a CIS level.
	 * Parameters: 
	 * 				linkedSuperCis - the CIS on behalf of which this object is to operate, by
	 *                               suggesting sub-CISs on it.
	 */
	
	public AutomaticCommunityCreationManager(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	public return ArrayList<EntityIdentifier> getIDsOfInteractingCsss() {
		
	}
	
	/*
	 * Description: The method looks for CISs to create, using as a base the information related to
	 *              this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only try to create sub-CISs on it. If the linked component
	 *              is a CSS, it will check all information relevant to that CSS to create
	 *              CISs that would be relevant to them. If the linked component is 
	 *              a domain (or something else like a local area?), the checks are not 'selfish'
	 *              but try to objectively identify appropriate CISs for groups of people, based
	 *              on collective aspects like context attributes.
	 */
	
	public void identifyCissToCreate(String evaluationType) {
		
		ArrayList<EntityIdentifier> interactedCssIDs = null;
		ArrayList<EntityIdentifier> friendCssIDs = null;
		
		// ...
		
		ArrayList<CisRecord> cissToCreate = null;
		
		
		if (evaluationType.equals("extensive")) {
			if (linkedCss != null) {
				interactedCssIDs = getIDsOfInteractingCsss();
				//retrieve as much context data on CSS user and inter-CSS connections 
				//amongst their immediate connection neighbourhood as possible.
				
				// processing - here or delegated to local method
			}
		}
			
		else {
			if (linkedCss != null) {
				interactedCssIDs = getIDsOfInteractingCsss();
				//retrieve recent history of certain kinds of context data on CSS user and inter-CSS connections 
				//amongst their immediate connection neighbourhood as possible.
				
				// processing - here or delegated to local method
			}
		}
		
		
		
		
		
		//invoke UserAgent suggestion GUI for creation of CISs
		//OR
		//automatically call CIS management functions to create CISs
		
	}
	
	public boolean isSituationSuggestiveOfTemporaryCISCreation() {
		boolean tempCisPossibility = true;
		return tempCisPossibility;
	}
}