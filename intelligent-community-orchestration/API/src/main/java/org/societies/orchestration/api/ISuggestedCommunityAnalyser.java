/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske držbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOAÇÃO, SA (PTIN), IBM Corp., 
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

package org.societies.orchestration.api;

import java.util.ArrayList;

//import org.societies.api.internal.cis.management.CisRecord;
import org.societies.api.internal.cis.management.ICisRecord;
//import org.societies.api.cis.management.ICisRecord;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * This is the interface for the Suggested Community Analyser component,
 * and acts as the gateway to Community Lifecycle Management, which
 * the Egocentric Community Analyser, CSCW, and CSM Analyser send their
 * CIS recommendations to for it to analyse further and take action if 
 * deemed appropriate, either via user feedback or fully automated action.
 * 
 * @author Fraser Blackmun
 * @version 1
 * 
 */

public interface ISuggestedCommunityAnalyser {
	
	/**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Create CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to create, each of which must include its parent if it's a sub-CIS.
	 * 
	 * Key: "Delete CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to delete.
	 * 
	 */
    public void processCSCWRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations);
	
    /**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Configure CISs attributes" Value: Arraylist(1) of arraylist(2) of CisRecords. (1) has one entry per configuration recommendation.
	 * (2) Has two entries for each of (1) - the first is the CisRecord to be configured, and the second is the CisRecord that would 
	 * result if the configuration happens.
	 * 
	 *  Key: "Split CISs" Value: Arraylist(1) of arraylist(2) of CisRecords. (1) has one entry per configuration recommendation.
	 * (2) Has three entries for each of (1) - the first is the CisRecord to be split, and the other two are the new CISs to be
	 * created by the split.
	 * 
	 *  Key: "Merge CISs" Value: Arraylist(1) of arraylist(2) of CisRecords. (1) has one entry per configuration recommendation.
	 * (2) Has three entries for each of (1) - the first two are the CisRecords that are to be merged into one. The third is optional,
	 * and it is the CisRecord that is to be created by the merge. This could be used in order to specify
	 * for example who the new owner should be.
	 * 
	 * 
	 * 
	 */
    public void processCSCWConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations);
    
    /**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Create CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to create, each of which must include its parent if it's a sub-CIS.
	 * 
	 * Key: "Delete CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to delete.
	 * 
	 */
    public void processCSMAnalyserRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations);
    
    /**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Configure CISs" Value: Arraylist(1) of arraylist(2) of CisRecords. (1) has one entry per configuration recommendation.
	 * (2) Has two entries for each of (1) - the first is the CisRecord to be configured, and the second is the CisRecord that would 
	 * result if the configuration happens.
	 * 
	 */
    public void processCSMAnalyserConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations);
    /**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Create CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to create, each of which must include its parent if it's a sub-CIS.
	 * 
	 * Key: "Delete CISs" Value: Arraylist of CisRecords.  
	 * It contains all the CisRecords to delete.
	 * 
	 */
    public ArrayList<String> processEgocentricRecommendations(HashMap<String, ArrayList<ICisRecord>> cisRecommendations, ArrayList<String> cissToCreateMetadata);
    
    /**
	 * Takes as input a collection of CISs, and what they represent, and performs analysis on them
	 * which may lead to action being taken for some or all of them.
	 * 
	 * @param cisRecommendations
	 * Holds all the CIS recommendation information, as a String-to-Arraylist hashmap. The possible values are as follows:
	 * 
	 * Key: "Configure CISs" Value: Arraylist(1) of arraylist(2) of CisRecords. (1) has one entry per configuration recommendation.
	 * (2) Has two entries for each of (1) - the first is the CisRecord to be configured, and the second is the CisRecord that would 
	 * result if the configuration happens.
	 * 
	 */
    public ArrayList<String> processEgocentricConfigurationRecommendations(HashMap<String, ArrayList<ArrayList<ICisRecord>>> cisRecommendations, ArrayList<String> cissToCreateMetadata);

}