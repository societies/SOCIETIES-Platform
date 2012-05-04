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

package org.societies.personalisation.socialprofiler.datamodel.utils;

/**
 * @author lucasimone
 */
public interface NodeProperties {
	
	
	public static final String NAME_PROPERTY 							= "name";
	public static final String DESCR_PROPERTY 							= "description";
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Person properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String CA_NAME_PROPERTY							 = "CA_Name";
	
	public static final String NARCISSISM_PERCENTAGE_PROPERTY 			= "narcissism";
	public static final String SUPER_ACTIVE_PERCENTAGE_PROPERTY 		= "superActive";
	public static final String PHOTO_PERCENTAGE_PROPERTY 				= "photo";
	public static final String SURF_PERCENTAGE_PROPERTY 				= "surf";
	public static final String QUIZ_PERCENTAGE_PROPERTY 				= "quiz";
	public static final String TOTAL_ACTIONS_PROPERTY 					= "totalActions";
	
	public static final String PARAM_BETWEEN_PROPERTY					= "betweenness_centrality";
	public static final String PARAM_EIGENVECTOR_PROPERTY				= "eigenvector_centrality";
	public static final String PARAM_CLOSENESS_PROPERTY					= "closeness_centrality";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Fan Page properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String REAL_NAME_PROPERTY						="realName";
	public static final String TYPE_PROPERTY							="type";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Group properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String CREATOR_PROPERTY							="creator";
	public static final String DESCRIPTION_PROPERTY						="description";
	public static final String SUBTYPE_PROPERTY							="subType";
	public static final String UPDATETIME_PROPERTY						="updateTime";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Profile properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String NUMBER_PROPERTY							="number";
	public static final String LAST_TIME_PROPERTY						="last_time";
	public static final String FREQUENCY_PROPERTY						="frequency";

	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Description properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String COST_PROPERTY								="cost";

	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//General Info properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String FIRST_NAME_PROPERTY							="firstName";
	public static final String LAST_NAME_PROPERTY							="lastName";
	public static final String BIRTHDAY_PROPERTY							="birthday";
	public static final String GENDER_PROPERTY									="sex";
	public static final String HOMETOWN_PROPERTY							="hometown";
	public static final String CURRENT_LOCATION_PROPERTY					="currentLocation";
	public static final String POLITICAL_PROPERTY							="political";
	public static final String RELIGION_PROPERTY							="religious";
	
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	//Interests properties
	//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
	public static final String ACTIVITIES_PROPERTY							="activities";
	public static final String INTERESTS_PROPERTY							="interests";
	public static final String MUSIC_PROPERTY								="music";
	public static final String MOVIES_PROPERTY								="movies";
	public static final String BOOKS_PROPERTY								="books";
	public static final String QUOTATIONS_PROPERTY							="quotations";
	public static final String ABOUT_ME_PROPERTY							="about me";
	public static final String PROFILE_UPDATETIME_PROPERTY					="profile update time";
}
