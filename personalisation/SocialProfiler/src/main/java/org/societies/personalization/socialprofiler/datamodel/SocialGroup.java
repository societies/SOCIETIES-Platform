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
package org.societies.personalization.socialprofiler.datamodel;


public interface SocialGroup {
	
	/**
     * returns the name of the group, a.k.a Group ID from facebook actually
     * @return group name
     */
	public String getName();
		
	
	/**
     * Sets the group name , a.k.a Group ID from facebook actually
     * @param name
     *            name of group
     */
	public void setName( String name );

	/**
	 * returns the real name of the group , this is actually the group name tag from facebook
	 * @return group real_name
	 */
	public String getRealName();

	/**
	 * sets the real name of the group, his is actually the group name tag from facebook
	 * @param realName
	 * 			real_name of the group
	 */
	public void setRealName(String realName);

	/**
	 * returns the type of the group
	 * @return String group type
	 */
	public String getGroupType();
	
	/**
	 * sets the type of the group
	 * @param groupType
	 */
	public void setGroupType(String groupType);
	
	/**
	 * returns the subtype of the group
	 * @return String group subtype
	 */
	public String getGroupSubType();
	
	/**
	 * set the group SubType
	 * @param groupSubType
	 */
	public void setGroupSubType(String groupSubType);
	
	/**
	 * returns the description of the group
	 * @return String groupDescription
	 */
	public String getDescription();
	
	/**
	 * get the description of the group
	 * @param description
	 */
	public void setDescription(String description);
	
	/**
	 * returns the update time of the group
	 * @return String update time
	 */
	public String getUpdateTime();
	
	/**
	 * set the update time of the group
	 * @param updateTime
	 */
	public void setUpdateTime(String updateTime);
	
	/**
	 * returns the creator of the group
	 * @return String creator 
	 */
	public String getCreator();
	
	/**
	 * sets the creator of the group
	 * @param creator
	 */
	public void setCreator(String creator);



}
