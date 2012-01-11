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

import org.societies.api.internal.cis.cis_management.CisActivityFeed;
import org.societies.api.internal.cis.cis_management.ServiceSharingRecord;
import org.societies.api.internal.cis.cis_management.CisActivity;
import org.societies.api.internal.cis.cis_management.CisRecord;
import org.societies.api.internal.cis.cis_management.ICisManager;

import org.societies.api.internal.context.user.similarity.IUserCtxSimilarityEvaluator;

import org.societies.api.internal.context.user.prediction.IUserCtxPredictionMgr;

import org.societies.api.internal.context.user.db.IUserCtxDBMgr;

import org.societies.api.internal.context.user.history.IUserCtxHistoryMgr;

import org.societies.api.internal.context.broker.IUserCtxBroker;
import org.societies.api.internal.context.broker.ICommunityCtxBroker;
import org.societies.api.internal.context.broker.IUserCtxBrokerCallback;

import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.CtxIdentifier;

import org.societies.api.mock.EntityIdentifier;

import java.sql.Timestamp;
import java.util.Date;

/**
 * This is the class for the Automatic Community Deletion Manager component
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 * The component is responsible for automating, and triggering the process of 
 * suggesting to one or more relevant CSSs, the deletion of CISs. This 
 * is achieved by perform various forms of analysis on CSSs, CISs, their attributes, and their
 * connections, and using different algorithms. Social network analysis methods and similarity of users
 * -based approaches and algorithms will be used, including an
 * approach that views groups/CISs as either ongoing (non-terminating, with no deadline or 
 * fulfillable purpose for existing) or temporary (not going to last, e.g. because it exists just
 * for a goal that will be completed, or has a clear lifespan, or group breakdown is inevitable). 
 * 
 */

public class AutomaticCommunityDeletionManager {

	private EntityIdentifier linkedCss; // No datatype yet defined for CSS
	
    private CisRecord linkedCis;
    
    //private Domain linkedDomain;  // No datatype yet representing a domain
	private EntityIdentifier linkedDomain;
	
	private int longestTimeWithoutActivity; //measured in minutes
	
	/*
     * Constructor for AutomaticCommunityConfigurationManager
     * 
	 * Description: The constructor creates the AutomaticCommunityConfigurationManager
	 *              component on a given CSS.
	 * Parameters: 
	 * 				linkedEntity - the non-CIS entity, either a user CSS or a domain deployment,
	 *              that this object will operate on behalf of.
	 */
	
	public AutomaticCommunityDeletionManager(EntityIdentifier linkedEntity, String linkType) {
		if (linkType.equals("CSS"))
			this.linkedCss = linkedEntity;
		else
			this.linkedDomain = linkedEntity;
	}
	
	/*
     * Constructor for AutomaticCommunityDeletionManager
     * 
	 * Description: The constructor creates the AutomaticCommunityDeletionManager
	 *              component on a CIS, either at a domain/cloud level or for an administrating CSS.
	 * Parameters: 
	 * 				linkedCis - the CIS on behalf of which this object is to operate, i.e.
	 *                          continually checking for whether to delete it/suggest deleting it.
	 */
	
	public AutomaticCommunityDeletionManager(CisRecord linkedCis) {
		this.linkedCis = linkedCis;
	}
	
	/*
	 * Description: The method looks for CISs to delete, using as a base the CIS records relevant
	 *              to this object's 'linked' component (see the fields). If the linked component
	 *              is just a CIS, it will only perform the check on that CIs. If the linked component
	 *              is a CSS, it will check all CISs they administrate. If the linked component is 
	 *              a domain, the check is done on all CISs in that domain.
	 */
	
	public void identifyCissToDelete() {
		if (linkedCss != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs administrated by the CSS */);
		}
		if (linkedCis != null) {
			//CisRecord james = new CisRecord();
			//CisRecord[] records = ICisManager.getCisList(/** This CIS */new CisRecord());
		}
		if (linkedDomain != null) {
			//CisRecord[] records = ICisManager.getCisList(/** CISs in the domain */);
		}
		
		//process
		
		CisRecord record;
		
		// VERY SIMPLISTIC v0.1 ALGORITHM
		//if (theCisRecord.getActivityFeed().getHistory().latestDate() <= Date.timestamp() - 5) {
		//    if (theCisRecord.getActivityFeed().getHistory().latestDate() <= Date.timestamp() - (longestTimeWithoutActivity/1440)) {
		//
		//        If the CIS has never gone such a long period without activity before, 
		//        suggest deletion via User Agent to CIS owner/administrators, i.e. whoever
		//        this deployment runs on behalf of.
		//  
		//        Date date= new java.util.Date();
		//        System.out.println(new Timestamp(date.getTime()));
		//
		//        Future directions here can include - being able to identify CISs to delete very soon,
		//        or at more flexible time than just the 5 days as above,
		//        after lack of activity or other key event e.g. purpose fulfillment or location change.
		
		//    }
		//}
		//invoke UserAgent suggestion GUI for deletions
				//OR
				//automatically call CIS management functions to delete CISs

		//    
		
		
		
		
	}
	
    public void intialiseAutomaticCommunityDeletionManager() {
    	
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
    
}