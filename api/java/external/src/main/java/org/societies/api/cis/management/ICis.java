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
package org.societies.api.cis.management;

import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.Future;

import org.societies.api.activity.IActivity;
import org.societies.api.activity.IActivityFeed;
import org.societies.api.schema.cis.community.Community;
import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;
import org.societies.api.cis.attributes.MembershipCriteria;

/**
 * @author Babak.Farshchian@sintef.no
 *
 */


@SocietiesExternalInterface(type = SocietiesInterfaceType.PROVIDED)
public interface ICis {

    
	/**
	 * Returns the jid of the CIS
	 * 
	 * @param 
	 * @return jid to address the CIS as a string
	 */
	public String getCisId();
    
	/**
	 * Returns the Name of the CIS
	 * 
	 * @param 
	 * @return name of the CIS as a string
	 */ 
    public String getName();
    //public String getOwnerId();
    //public String setUserDefinedName(String _name);
   // public String getUserDefineName();
    //public String getCisType();
    
	/**
	 * Returns the membership criteria of the CIS
	 * 
	 * @param 
	 * @return name of the CIS as a string
	 */ 
 /*   public Hashtable<String,MembershipCriteria> getMembershipCriteria();
	public boolean addCriteria(String contextAtribute, MembershipCriteria m);
	public boolean removeCriteria(String contextAtribute, MembershipCriteria m);*/

	/**
	 * Get membership criteria from a CIS.
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * it has the  info from the CIS
	 * 
	 * Notice that the membership criteria will be formated as the marshable object
	 * 
	 * org.societies.api.schema.cis.community.MembershipCrit
	 * @param callback callback function
	 */
	public void getMembershipCriteria(ICisManagerCallback callback);

	
	/**
	 * Get info from a CIS.
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * it has the  info from the CIS
	 * 
	 * 
	 * @param callback callback function
	 */
    public void getInfo(ICisManagerCallback callback);
    

	/**
	 * Get list of members from a CIS.
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * which will have a Who with a list of Participant objects
	 * 
	 *  IMPORTANT: this function is still under tests
	 * 
	 * @param callback callback function
	 */
    public void getListOfMembers(ICisManagerCallback callback);

    
	/**
	 * 
	 * AT THE MOMENT YOU MUST BE THE OWNER OF THE CIS IN ORDER TO SET INFO in it
	 * 
	 * Set info into a CIS. 
	 * The callback must be able to retrieve a community object
	 * defined at org.societies.api.schema.cis.community 
	 * it has the  info from the CIS
	 * 
	 * @param c org.societies.api.schema.cis.community marshalled object containing information
	 * to be set at the CIS. However, it is worth to mention that at the moment, we are allowing
	 * to set only the CIS description and type.
	 * @param callback callback function
	 */
    public void setInfo(Community c, ICisManagerCallback callback);
    
    /**
	 * Get a handler of the activityfeed of the CIS. Then you can use
	 * the interface to search/add/delete activities
	 *  
	 * 
	 * @return {@link IActivityFeed}
	 */
    public IActivityFeed getActivityFeed();
    
    
    
}
