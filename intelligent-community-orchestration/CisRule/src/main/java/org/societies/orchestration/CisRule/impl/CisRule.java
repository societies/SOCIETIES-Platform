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

/**
 * This class represents a CIS rule, which applies for a specific, existing CIS
 * 
 * @author Fraser Blackmun
 * @version 0
 * 
 */

package org.societies.orchestration.CisRule.impl;

import java.lang.String;
import java.util.HashMap;

public interface CisRule {
    
	//States the type of the rule, which is one of the following three:
	//  Lifecycle:  Relates to the creation, configuration,
	//              and deletion of the CIS that the rule applies for
	//  Membership: Relates to criteria for CIS membership and user 
	//              recommendation for joining
	//  Merge:      Specifies if and/or when a CIS should be merged with other CISs.

	public String ruleType;
	
	//The rule embodied by the CISRule object that this interface represents.
	//The 'keys' in the HashMap may be the following (still in progress):
	//  "CIS deletion criteria" - value = array of conditions for a CIS to be deleted,
	//                                    where each element of the array is a sufficient
	//                                    condition for deletion.
	//  "CIS configuration criteria" - value = array of conditions for a CIS to be configured,
	//                                         and details on the resultant configuration for 
	//                                         each array element
	//  "CIS merging criteria" - value = array of conditions for a CIS to be merged with another
	//  "CIS membership criteria" - value = the membership criteria for a CIS
	//  
	public HashMap ruleDetails;

    /*
     * Constructor for CISRule.
     * 
	 * Description: The constructor creates a new CISRule with the details
	 *              passed to it.
	 * Parameters: 
	 * 				1) ruleType - The kind of rule being created, which
	 *                            is one of: Lifecycle, Membership, Merge.
	 * 				2) ruleToCreate - The details on the rule that are needed
	 *                                in order to create it. 
	 */
	
	public CisRule(String ruleType, HashMap ruleToCreate);
	
	/*
	 * Description: The modifyRule method alters the rule that the CISRule 
	 *              object represents, using the details in the input HashMap.
	 * Parameters: 
	 * 				1) changesToRule - The changes that are to be made to the CIS rule.
	 * Returns:
	 * 				* True if the method was able to modify the CIS rule.
	 *				* False if the method was unable to modify the CIS rule.
	 */
	
    public boolean modifyRule(HashMap changesToRule);
	
	/*
	 * Description: The deleteRule method causes the CISRule object
	 *              that the method is invokved on to delete itself.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				* True if the method was able to delete the CIS rule.
	 *				* False if the method was unable to delete the CIS rule.
	 */
	
	public boolean deleteRule();
	
	/*
	 * Description: The getRule method returns this rule.
	 * Parameters: 
	 * 				none
	 * Returns:
	 * 				The rule represented by the object implementing this interface.
	 */
	
	public ICisRule getRule();

}