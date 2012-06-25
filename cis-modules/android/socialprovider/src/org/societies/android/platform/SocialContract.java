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
package org.societies.android.platform;

import android.net.Uri;


/**
 * Content provider contract file for CIS manager light.
 * 
 *
 * @author Babak dot Farshchian at sintef dot no
 *
 */
public final class SocialContract {

    /**
     * Provides constants for managing CIS-related queries. Please read 
     * information about content providers in Android in order to 
     * learn how to use this contract. Note that this contract is 
     * currently the only documentation of {@link SocialProvider} on
     * Android. All the functionality of CIS manager is currently being 
     * provided using this contract. 
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
	private static final String AUTHORITY_STRING = "content://org.societies.android.SocialProvider";
	//The main authority, i.e. the base URI for all operations:
    public static final Uri AUTHORITY = 
            Uri.parse("content://org.societies.android.SocialProvider");
    //Constants used to define read and write permissions for social data:
    public static final String PROVIDER_READ_PERMISSION = "org.societies.android.SocialProvider.READ";
    public static final String PROVIDER_WRITE_PERMISSION = "org.societies.android.SocialProvider.WRITE";
    
    
    /**
     * Class that provides information about the owner of 
     * the current CSS. This is mainly information that will
     * be extracted from CSS Manager Light. Note that you can have 
     * as many CSS IDs as you wish. 
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Me {
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING +"/me");

        /**
         * Key local ID. Needed for using cursors in Android. Note that _id is
         * unique only locally on this device.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for my identity, e.g. JID.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * My name to be used with this CSS ID.
         */
        public static final String NAME = "name"; 
        /**
         *  My alternative name, e.g. nickname.
         */
        public static final String DISPLAY_NAME = "display_name";
        /**
         *  Login user name if different from GLOBAL_ID
         */
        public static final String USER_NAME = "user_name";
        /**
         *  Possible password to be used with user name
         */
        public static final String PASSWORD = "password";
    	
    }
    /**
     * Class that defines a pointer (record) to a CIS that I own or am member of.
     * The CIS itself can be retrieved from Community using GLOBAL_ID that
     * you get here. MyCommunity gives you a list of CIS records that you
     * are a member of. You can use this list to look up the actual CIS in 
     * Community. 
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class MyCommunity {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING +"/me/communities");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the community, e.g. JID
        public static final String OWNER_ID = "owner"; //Person who owns the community
        public static final String DISPLAY_NAME = "display_name"; //Name of the community to be shown to the user
    }
    /**
     * Class that stores metadata about CISs, both those I am
     * a member of and those I am not a member of. Use MyCommunity
     * to find out which CISs you are a member of, then look the CIS
     * up in this table.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Community {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING+"/communities");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the community, e.g. JID
        public static final String TYPE = "type"; //The type of the community being stored. E.g. "disaster".
        										//The type will be defined and used by applications.
        public static final String NAME = "name"; //Name of the community
        public static final String DISPLAY_NAME = "display_name"; //Name of the community to be shown to the user
        public static final String OWNER_ID = "owner"; //Person who owns the community
        public static final String CREATION_DATE = "creation_date";	
        public static final String MEMBERSHIP_TYPE = "membership_type"; //TODO: need to decide types.
        public static final String DIRTY = "dirty"; //Used to indicate whether this community's data is changed locally.
    }

    /**
     * Class that defines constants related to CSSs:
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Person {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING+"/people");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the person, e.g. JID
//        public static final String TYPE = "type"; //The type of the element being stored. CSS for people
        public static final String NAME = "name"; //Name of the person
        public static final String EMAIL = "email"; //Owner CSS jid of the group
        public static final String DISPLAY_NAME = "display_name";
        public static final String CREATION_DATE = "creation_date";
    }
    
    /**
     * Class that defines constants for services that can be shared
     * in communities.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Service {
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING+"/services");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the service. E.g. URL in an app store.
        public static final String TYPE = "type"; //The type of the service being stored.
        public static final String NAME = "name"; //Name of the service
        public static final String DISPLAY_NAME = "display_name";
        public static final String CREATION_DATE = "creation_date";		
    }
    /**
     * Class that defines constants for memberships in communities
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class MembershipRecord {
        public static final String _ID = "_id"; //Key local ID
        public static final String COMMUNITY_ID = "community_id"; //Key local ID for involved community
        public static final String PERSON_ID = "person_id"; //Key local ID for involved person
        public static final String ROLE = "role"; //The role the person plays in the community
    }
//TODO: Add service sharing records
}
