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
package org.societies.api.context.model;

/**
 * This class defines common {@link CtxAttribute context attribute} types.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.7
 */
public class CtxAttributeTypes {

	/**
     * The string value of this attribute type contains information about the entity (e.g. desription of a user or community)
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String ABOUT = "aboutMe";
	 
	/**
     * This attribute type is used in order to model an action performed by an entity. 
     * For example, a user action can be related with the manipulation of a service or a device.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}, {@link CtxAttributeValueType.BINARY}
     *    
     * @since 0.0.8
     */
    public static final String ACTION = "action";
    
    /**
     * The value of this attribute type contains information about the age of an entity. 
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING},{@link CtxAttributeValueType.INTEGER},{@link CtxAttributeValueType.DOUBLE}  
     *  
     * @since 0.0.8
     */
    public static final String AGE = "age";
    
	/**
     * The string value of this attribute type contains information about the address home city of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_CITY = "addressHomeCity";
    
    /**
     * The string value of this attribute type contains information about the address home country of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_COUNTRY = "addressHomeCountry";
	
	/**
     * The string value of this attribute type contains information about the address home street name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_STREET_NAME = "addressHomeStreetName";
    
    /**
     * The integer value of this attribute type contains information about the address home number of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.INTEGER}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_STREET_NUMBER = "addressHomeStreetNumber";
    
    /**
     * The string value of this attribute type contains information about the work place city name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_CITY = "addressWorkCity";
    
    /**
     * The string value of this attribute type contains information about the work place country name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_COUNTRY = "addressWorkCountry";
	
	/**
     * The string value of this attribute type contains information about the work place street name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_STREET_NAME = "addressWorkStreetName";
    
    /**
     * The integer value of this attribute type contains information about the work place street number of the owner entity of the CSS.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.INTEGER}
     *   
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_STREET_NUMBER = "addressWorkStreetNumber";
	
    /**
     * The comma separated string values of this attribute type contains information about the affiliation of the an entity with other members of 
     * the organisation that may belong (e.g. "professor", "student", "employee").
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.4
     */
    public static final String AFFILIATION = "affiliation";
    
    /**
     * The string value of this attribute type contains information about the birthday of a person entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String BIRTHDAY = "birthday";
   
    /**
     * The comma separated string values of this attribute type contains information about the favorite books of a person. 
     * (e.g. "LeMisserables,MobyDick,Twenty Thousand Leagues Under the Sea")
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String BOOKS = "books";
   
   /**
     * TODO moved to platform CtxAttributeTypes
     */
    @Deprecated
    public static final String CAUI_MODEL = "caui_model";
    
    /**
     * TODO moved to platform CtxAttributeTypes
     */
    @Deprecated
    public static final String CRIST_MODEL = "crist_model";
        
    /**
     * The string value of this attribute type contains information about the email address of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String EMAIL = "email";
    
    /**
     * The comma separated string values of this attribute type contains information about the favorite quotes of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String FAVOURITE_QUOTES = "favoriteQuotes";
        
    /**
     * The comma separated string values of this attribute type contains information about the friend ids of an entity. 
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}, {@link CtxAttributeValueType.BINARY}
     * 
     * @since 0.0.8
     */
    public static final String FRIENDS = "friends";
    
	/**
     * The value of this attribute type contains information about the identity
     * of a {@link CtxEntity}.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}, 
     * {@link CtxAttributeValueType.BINARY}
     * 
     * @since 0.0.8
     */
    public static final String ID = "id";
    
    /**
     * The comma separated string values of this attribute type contains information about the interests of an entity. 
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String INTERESTS = "interests";
    
    /**
     * This attribute type is used to specify whether a {@link CtxEntity} of
     * type {@link CtxEntityTypes#CSS_NODE} is user interactable or not. 
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * <p>
     * Possible values: <code>"true"</code> or <code>"false"</code> (ignoring
     * case).
     *  
     * @since 1.0
     */
    public static final String IS_INTERACTABLE = "isInteractable";

    /**
     * The comma separated string values of this attribute type contains information about the languages used or spoken by an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String LANGUAGES = "languages";

    /**
     * The value of this attribute type contains information about the last action performed by an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}, {@link CtxAttributeValueType.BINARY}
     * 
     * @since 0.0.8
     */
    public static final String LAST_ACTION = "lastAction";
    
    /**
     * The coordinates of the current location of an entity (e.g. "123.45,124.39").
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String LOCATION_COORDINATES = "locationCoordinates";
    
    /**
     * The string value of this attribute type contains information about the location id of an entity.
     * This attribute type value is set by the Presence Zones location management system.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.4
     */
    public static final String LOCATION_ID = "locationId";
    
    /**
     * The string value of this attribute type contains information about the location parent id of an entity.
     * This attribute type value is set by the Presence Zones location management system.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.4
     */
    public static final String LOCATION_PARENT_ID = "locationParentId";
    
    /**
     * The string values of this attribute type contains information about the tags describing the location of an entity.
     * This attribute type value is set by the Presence Zones location management system.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.4
     */
    public static final String LOCATION_PERSONAL_TAGS = "locationPersonalTags";
    
    /**
     * The string values of this attribute type contains information about the tags describing the location of an entity.
     * This attribute type value is set by the Presence Zones location management system.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.4
     */
    public static final String LOCATION_PUBLIC_TAGS = "locationPublicTags";
    
    /**
     * The string value of this attribute type contains information about the location, described by a symbolic name, of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String LOCATION_SYMBOLIC = "locationSymbolic";
    
    /**
     * The string value of this attribute type contains information about the location type of an entity.
     * This attribute type value is set by the Presence Zones location management system.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.4
     */
    public static final String LOCATION_TYPE = "locationType";
        
    /**
     * The comma separated string values of this attribute type contains information about the favorite movies of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String MOVIES = "movies";
   
    /**
     * This attribute type contains information about the MAC address of a
     * {@link CtxEntity}. It is usually associated with an entity of type
     * {@link CtxEntityTypes#CSS_NODE}.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * <p>
     * Possible values: E.g. <code>"01-23-45-67-89-ab"</code> or 
     * <code>"01:23:45:67:89:ab"</code>
     * 
     * @since 1.0
     */
    public static final String MAC_ADDRESS = "macAddress";
    
    /**
     * The comma separated string values of this attribute type contains information about the favorite music (groups,songs) of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String MUSIC = "music";
    
    /**
     * The string value of this attribute type describes the name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String NAME = "name";
    
    /**
     * The string value of this attribute type describes the first name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String NAME_FIRST = "nameFirst";
    
    /**
     * The string value of this attribute type describes the last name of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String NAME_LAST = "nameLast";
    
    /**
     * The comma separated string values of this attribute type contains information about the occupation of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String OCCUPATION = "occupation";
    
    /**
     * The string value of this attribute type contains information about the parameters of a service modeled as an entity (entity type: service_parameter).
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *     
     * @since 0.0.8
     */
    public static final String PARAMETER_NAME = "parameterName";
    
    /**
     * The string value of this attribute type is used to store a password.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *     
     * @since 1.0
     */
    public static final String PASSWORD = "password";
    
    /**
     * The comma separated string values of this attribute type contains information about the political views of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String POLITICAL_VIEWS = "politicalViews";
    
    /**
     * The comma separated string values of this attribute type contains information about the religious views of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *      
     * @since 0.0.8 
     */
    public static final String RELIGIOUS_VIEWS = "religiouslViews";
 
    /**
     * The string value of this attribute type contains information about the sex of an entity (e.g. male, female).
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *      
     * @since 0.0.8
     */
    public static final String SEX = "sex";
  
    /**
     * The string value of this attribute type contains information about the status of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.0.8
     */
    public static final String STATUS = "status";
    
    /**
     * The comma separated string values of this attribute type contains information about the skills of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 0.0.8
     */
    public static final String SKILLS = "skills";
    
    
    /**
     * The string value of this attribute type contains information about the temperature of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING},{@link CtxAttributeValueType.INTEGER}, {@link CtxAttributeValueType.DOUBLE}
     * 
     * @since 0.0.8
     */
    public static final String TEMPERATURE = "temperature";
    
    /**
     * The string value of this attribute type contains information about the type of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     * 
     * @since 0.5
     */
    public static final String TYPE = "type";
   
    /**
     * The double value of this attribute type contains information about the weight of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}, {@link CtxAttributeValueType.DOUBLE}
     * 
     * @since 0.0.8
     */
    public static final String WEIGHT = "weight";
    
    /**
     * This attribute type contains information about the work position of an entity.
     * <p>
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String WORK_POSITION = "workPosition";
       
    /**
     * TODO moved to platform CtxAttributeTypes
     */
    @Deprecated
    public static final String PRIVACY_POLICY_REGISTRY = "privacyPolicyRegistry";
    
    /**
     * Privacy Policy Agreement Attribute type
     * To be moved in internal API
     */
    @Deprecated
    public static final String PRIVACY_POLICY_AGREEMENT = "privacy-policy-agreement-of_";   
    
    
    /**
     * This attribute type contains information about the food preferences of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String FOOD = "food";
    
    /**
     * This attribute type contains information about the profile image url of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String PROFILE_IMAGE_URL = "profile_image_url";
    
    /**
     * This attribute type contains information about the jobs interests of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String JOBS_INTERESTS = "job_interensts";
    
    
    /**
     * This attribute type contains information about the turnson of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String TURNSON = "turnson";
    
    
    /**
     * This attribute type contains information about the turnson of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String ACTIVITIES = "activities";
    
    
    /**
     * This attribute type contains information about the turnson of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String PHONES = "phones";
    
    /**
     * This attribute type contains information about the username of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String USERNAME = "username";
    
    
    /**
     * This attribute type contains information about the group is folowing  of an entity.
     * Possible value types: {@link CtxAttributeValueType.STRING}
     *  
     * @since 1.0
     */
    public static final String GROUP = "group";

    /**
     * All known types, as an array
     */
    public static final String[] ALL_TYPES = {
            ABOUT,
            ACTION,
            AGE,
            ADDRESS_HOME_CITY,
            ADDRESS_HOME_COUNTRY,
            ADDRESS_HOME_STREET_NAME,
            ADDRESS_HOME_STREET_NUMBER,
            ADDRESS_WORK_CITY,
            ADDRESS_WORK_COUNTRY,
            ADDRESS_WORK_STREET_NAME,
            ADDRESS_WORK_STREET_NUMBER,
            AFFILIATION,
            BIRTHDAY,
            BOOKS,
            EMAIL,
            FAVOURITE_QUOTES,
            FRIENDS,
            ID,
            INTERESTS,
            IS_INTERACTABLE,
            LANGUAGES,
            LAST_ACTION,
            LOCATION_COORDINATES,
            LOCATION_ID,
            LOCATION_PARENT_ID,
            LOCATION_PERSONAL_TAGS,
            LOCATION_PUBLIC_TAGS,
            LOCATION_SYMBOLIC,
            LOCATION_TYPE,
            MOVIES,
            MAC_ADDRESS,
            MUSIC,
            NAME,
            NAME_FIRST,
            NAME_LAST,
            OCCUPATION,
            PARAMETER_NAME,
            PASSWORD,
            POLITICAL_VIEWS,
            RELIGIOUS_VIEWS,
            SEX,
            STATUS,
            SKILLS,
            TEMPERATURE,
            TYPE,
            WEIGHT,
            WORK_POSITION};

}
