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
     *  @since 0.0.8
     */
    public static final String ABOUT = "aboutMe";
	 
	/**
     *  @since 0.0.8
     */
    public static final String ACTION = "action";
    
	/**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_CITY = "addressHomeCity";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_COUNTRY = "addressHomeCountry";
	
	/**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_STREET_NAME = "addressHomeStreetName";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_HOME_STREET_NUMBER = "addressHomeStreetNumber";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_CITY = "addressWorkCity";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_COUNTRY = "addressWorkCountry";
	
	/**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_STREET_NAME = "addressWorkStreetName";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String ADDRESS_WORK_STREET_NUMBER = "addressWorkStreetNumber";
	
    /**
     * 
     * @since 0.4
     */
    public static final String AFFILIATION = "affiliation";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String BIRTHDAY = "birthday";
   
    /**
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
     * @since 0.0.8
     */
    public static final String EMAIL = "email";
    
    /**
     * @since 0.0.8
     */
    public static final String FAVOURITE_QUOTES = "favoriteQuotes";
        
	/**
     * @since 0.0.8
     */
    public static final String ID = "id";
    
    /**
     * @since 0.0.8
     */
    public static final String INTERESTS = "interests";

    /**
     * @since 0.0.8
     */
    public static final String LANGUAGES = "languages";

    
    /**
     * @since 0.0.8
     */
    public static final String LAST_ACTION = "lastAction";
    
    /**
     * @since 0.0.8
     */
    public static final String LOCATION_COORDINATES = "locationCoordinates";
    
    /**
     * @since 0.4
     */
    public static final String LOCATION_ID = "locationId";
    
    /**
     * @since 0.4
     */
    public static final String LOCATION_PARENT_ID = "locationParentId";
    
    /**
     * @since 0.4
     */
    public static final String LOCATION_PERSONAL_TAGS = "locationPersonalTags";
    
    /**
     * @since 0.4
     */
    public static final String LOCATION_PUBLIC_TAGS = "locationPublicTags";
    
    /**
     * @since 0.0.8
     */
    public static final String LOCATION_SYMBOLIC = "locationSymbolic";
    
    /**
     * @since 0.4
     */
    public static final String LOCATION_TYPE = "locationType";
        
    /**
     * @since 0.0.8
     */
    public static final String MOVIES = "movies";
   
    /**
     * @since 0.0.8
     */
    public static final String MUSIC = "music";
    
    /**
     * @since 0.0.8
     */
    public static final String NAME = "name";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String NAME_FIRST = "nameFirst";
    
    /**
     * 
     * @since 0.0.8
     */
    public static final String NAME_LAST = "nameLast";
    
    /**
     * @since 0.0.8
     */
    public static final String OCCUPATION = "occupation";
    
    
    /**
     * @since 0.0.8
     */
    public static final String PARAMETER_NAME = "parameterName";
    
    /**
     * @since 0.0.8
     */
    public static final String POLITICAL_VIEWS = "politicalViews";
 
    /**
     * @since 0.0.8
     */
    public static final String RELIGIOUS_VIEWS = "religiouslViews";
 
    /**
     * @since 0.0.8
     */
    public static final String SEX = "sex";
  
    /**
     * @since 0.0.8
     */
    public static final String STATUS = "status";
    
    /**
     * @since 0.0.8
     */
    public static final String SKILLS = "skills";
    
    
    /**
     * @since 0.0.8
     */
    public static final String TEMPERATURE = "temperature";
   
    /**
     * @since 0.0.8
     */
    public static final String WEIGHT = "weight";
       
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
}