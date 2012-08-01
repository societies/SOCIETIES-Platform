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
package org.societies.api.cis.directory;

import org.societies.utilities.annotations.SocietiesExternalInterface;
import org.societies.utilities.annotations.SocietiesExternalInterface.SocietiesInterfaceType;

import java.util.HashMap;
import org.societies.api.cis.attributes.MembershipCriteria;
import org.societies.api.context.model.CtxAttribute;

/**
 * This interface allows access to the CISAdvertisement record
 * 
 * @author Babak.Farshchian@sintef.no
 *
 */

@SocietiesExternalInterface(type=SocietiesInterfaceType.PROVIDED)
public interface ICisAdvertisementRecord {
	
	/**
     * Description: Gets the name of the CIS from the advertisement record
     *
     * @return String Name
     */
	public String getName();
	
	/**
     * Description: Sets the name of the CIS in the advertisement record
     *
     * @param String Name
     */
	public void setName(String name);

	/**
     * Description: Gets the Identity of the CIS from the advertisement record
     *
     * @return String ID 
     */
	public String getId(); //Can be used to query CIS owner for an ICis to get member list, if CIS is set to public

	
	/**
     * Description: Gets the Uri of the CIS from the advertisement record
     *
     * 
     * @return String Uri
     */
	public String getUri();
	
	
	/**
     * Description: Sets the Uri of the CIS from the advertisement record
     *
     * @param String Uri
     */
	public void setUri(String uri);
	
	/**
     * Description: Gets the password of the CIS from the advertisement record
     *
     * 
     * @return String password
     */
	public String getPassword();
	
	
	/**
     * Description: Sets the password of the CIS in the advertisement record
     *
     * @param String password
     * 
     * @return:
     */
	public String setPassword(String password);
	
	/**
     * Description: Gets the type of the CIS from the advertisement record
     *
     * @return String type
     */
	public String getType();
	
	/**
     * Description: Sets the type of the CIS in the advertisement record
     *
     * @param String type
     * 
     */
	public String setType(String type);
	
	/**
     * Description: Gets the mode of the CIS from the advertisement record
     *
     * 
     * @return String mode
     */
	public String getMode();
	
	/**
     *Description: Sets the mode of the CIS in the advertisement record
     *
     * @param int mode
     */
	public String setMode(int mode);
	
	/**
     * Description: Gets the description of the CIS from the advertisement record
     *
     * 
     * @return String Description
     */
	public String getDescription();
	
	/**
     * Description: Sets the Description of the CIS from the advertisement record
     *
     * 
     * @param String Description
     */
    public boolean setDescription(String description);
    
    /**
     * Description: Gets the membership criteria of the CIS from the advertisement record
     *
     * 
     * @return HashMap MembershipCriteria
     */
    public HashMap<String, MembershipCriteria> getMembershipCriteria();
    
    /**
     * Description: Sets the membership criteria of the CIS from the advertisement record
     *
     * 
     * @param HashMap membershipCriteria
     */
    public boolean setMembershipCriteria(HashMap<String, MembershipCriteria> membershipCriteria);
}
