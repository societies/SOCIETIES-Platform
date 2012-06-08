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
package org.societies.orchestration.api;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;
import org.societies.api.identity.IIdentity;
import java.util.ArrayList;
import java.util.Set;

import java.util.concurrent.Future;

/**
 * @author Babak.Farshchian@sintef.no
 *
 */



/**
 * MISSING_JAVADOCS
 * 
 * this will be changed on W20, please wait by then
 */
public class ICis {

	private String cisId;
    
	public ICis() {
		cisId = "";
	}
	
	/**
	 * Returns the jid of the CIS
	 * 
	 * @param 
	 * @return jid to address the CIS as a string
	 */
	public String getCisId() {
		return cisId;
	}
    
	/**
	 * Returns the Name of the CIS
	 * 
	 * @param 
	 * @return name of the CIS as a string
	 */ 
    public String getName() {
    	return "";
    }
    //public String getOwnerId();
    //public String setUserDefinedName(String _name);
   // public String getUserDefineName();
    //public String getCisType();
    
    public String getDescription() {
    	return "";
    }
    
    public Future<Set<ICisParticipant>> getMembersList() {
    	return null;
    }
    
    
    
    //public int getMembershipCriteria() {
    //	return 0;
    //}
    
    public ArrayList<String> getMembershipCriteria() {
    	return new ArrayList<String>();
    }
    
    public Future<Set<ICisParticipant>> getAdministrators() {
    	return null;
    }
    
    public Boolean addSubCis(ICis cis) {
    	return new Boolean(true);
    }
    
    public Boolean addParentCis(ICis cis) {
    	return new Boolean(true);
    }
    
    public Boolean removeSubCis(ICis cis) {
    	return new Boolean(true);
    }
    
    public Boolean removeParentCis(ICis cis) {
    	return new Boolean(true);
    }

}
