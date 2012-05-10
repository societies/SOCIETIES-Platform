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
package org.societies.personalisation.socialprofiler.datamodel.behaviour;


public interface Profile {
	
	public enum Type {
		EGO_CENTRIC, 
		PHOTO_MANIAC,
		QUIZ_MANIAC,
		SUPER_ACTIVE,
		SURF_MANIAC
		};
		
		
	/**
	 * Set the profile name
	 * @param name
	 */
	public void setName(String name);
	
	
	/**
	 * Profile name, that define in a single string the main character of the profile
	 * @return Profile string name
	 */
	public String getName();
	
	/**
	 * Short description about the behavior of this Profile
	 * @return Profile string description
	 */
	public String getDescription();
	
	/**
	 * Unique Identifier of a Profile for matching and internal purposes
	 * @return id (int)
	 */
	public Profile.Type getType();
	
	/**
	 * Get the frequency on how the action of a Person reflect this profile
	 * @return an integer (0-100) about the frequency value
	 */
	public long getFrequency();

	/**
	 * Set a value of the frequency about this profile
	 * @param frequency inter value [0-100]
	 */
	public void setFrequency(String frequency);

	/**
	 * Calculate a new frequency value based on the number of actions done related to this profile.
	 * @param numberOfActions executed
	 */
	public void updateFrequency(int numberOfActions);
	
	
	/**
	 * returns the last time of the  profile, this is actually the last timestamp
	 * @return String last timestamp
	 */
	public String getLastTime();

	/**
	 * sets the last time of the  profile, this is actually the last timestamp
	 * @param lastime
	 * 			last Timestamp
	 */
	public void setLastTime(String lastTime);

	/**
	 * returns the number of times which concluded to this profile - number of points of the analysis
	 * @return String number
	 */
	public int getNumber();
	
	/**
	 * sets sets the number of times which concluded to this profile - number of points of the analysis
	 * @param String number
	 */
	public void setNumber(String number);
	
	
	/**
	 * increment number with one unity
	 */
	public void incrementNumber();
	
	
	/**
	 * Set a Profile Description
	 * @param description
	 */
	public void setDescription(String description);
	
}
