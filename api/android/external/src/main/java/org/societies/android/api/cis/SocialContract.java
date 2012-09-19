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
package org.societies.android.api.cis;

import android.net.Uri;


/**
 * Provides constants for using SocialProvider. Please read 
 * information about content providers in Android in order to 
 * learn how to use this contract. Note that this contract is 
 * currently the only documentation of {@link SocialProvider} on
 * Android. All the functionality of CIS manager is currently being 
 * provided using this contract.
 * 
 * @author Babak dot Farshchian at sintef dot no
 *
 */
public final class SocialContract {

	/**
	 * The base URI used when calling SocialProvider:
	 */
	private static final String AUTHORITY_STRING = "content://org.societies.android.SocialProvider/";
	//The main authority, i.e. the base URI for all operations:
    public static final Uri AUTHORITY = 
            Uri.parse("content://org.societies.android.SocialProvider");
    //Constants used to define read and write permissions for social data:
    public static final String PROVIDER_READ_PERMISSION = "org.societies.android.SocialProvider.READ";
    public static final String PROVIDER_WRITE_PERMISSION = "org.societies.android.SocialProvider.WRITE";
    
    
    /**
	 * A utility class defining constants for the different 
	 * paths in the content provider URIs.
	 * 
	 * When you are calling content provider methods, use AUTHORITY_STRING
	 * plus one of the paths in this class so your code is protected
	 * against errors in paths and URIs, e.g.:
	 * 
	 *  query(SocialContract.AUTHORITY_STRING+SocialContract.UriPathIndex.ME,...)
	 * 
	 * @author Babak dot Farshchian at sintef dot no
	 *
	 */
	public static final class UriPathIndex{
		public static final String ME = "me";
		public static final String ME_SHARP = "me/#";
		public static final String PEOPLE = "people";
		public static final String PEOPLE_SHARP = "people/#";
		public static final String COMMINITIES = "communities";
		public static final String COMMINITIES_SHARP = "communities/#";
		public static final String SERVICES = "services";
		public static final String SERVICES_SHARP = "services/#";
		public static final String RELATIONSHIP = "relationship";
		public static final String RELATIONSHIP_SHARP = "relationship/#";
		public static final String MEMBERSHIP = "membership";
		public static final String MEMBERSHIP_SHARP = "membership/#";
		public static final String SHARING = "sharing";
		public static final String SHARING_SHARP = "sharing/#";
		public static final String PEOPLE_ACTIVITIY = "people/activity";
		public static final String PEOPLE_ACTIVITIY_SHARP = "people/activity/#";
		public static final String COMMUNITY_ACTIVITIY = "communities/activity";
		public static final String COMMUNITY_ACTIVITIY_SHARP = "communities/activity/#";
		public static final String SERVICE_ACTIVITY = "services/activity";
		public static final String SERVICE_ACTIVITY_SHARP = "services/activity/#";
	
	}

	/**
     * This is the URI you will use for accessing information about people.
     * Every CSS/person which is accessible from this {@link SocialProvider}
     * has an entry in this table. You can search for people using 
     * GLOBAL_ID. You can find information about:
     * 
     * - People you don't have a relationship to: Here you will get info
     * that you typically find in a CSS directory/yellow pages.
     * 
     * - People you have a relationship with: Here you might be able to get 
     * more information. Depending on where the information is fetched from.
     * 
     * The information stored about people will in future versions correspond
     * to what you typically find in social sites such as Facebook.
     * 
     * Note: CREATION_DATE, LAST_MODIFIED_DATE, and SYNC_STATUS are set by
     * SocialProvider. 
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class People {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING+ UriPathIndex.PEOPLE);
        /**
         *  Key local ID, used by content provider to denote the location of this
         *  person in the table. Row number.
         */
        public static final String _ID = "_id";
        /**
         * ID which globally identifies this person. Can be JID, email address 
         * etc. Note that if a person exists in more than one cloud directory
         * this GLOBAL_ID will be used to map info about the person.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Name of the person. 
         */
        public static final String NAME = "name";
        /**
         * Email address of the person.
         */
        public static final String EMAIL = "email";
        /**
         * Set by user or Sync adapter, telling the user where this person is
         * found. Can for instance be Facebook (if this is a Facebook contact)
         * or SOCIETIES etc.
         */
        public static final String ORIGIN = "origin";
        /**
         * A description of the user.
         */
        public static final String DESCRIPTION = "description";
        /**
         * The date this record was created by its origin.
         */
        public static final String CREATION_DATE = "creation_date";
        
        /**
         * The date this record was last modified. Used by sync adapters.
         */
        public static final String LAST_MODIFIED_DATE = "last_modified_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services.
         */
        public static final String SYNC_STATUS = "sync_status";
        
    }
    
    /**
     * This class allows you to see information about all communities (CISs).
     * This includes both communities you are a member of and communities
     * that are listed in some directory.
     * 
     * Note: CREATION_DATE, LAST_MODIFIED_DATE, and SYNC_STATUS are set by
     * SocialProvider.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Communities {
        /**
         * Use this Uri when calling queries on the content 
         * provider with the intention of working with community
         * data. 
         */
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING+ UriPathIndex.COMMINITIES);
        /**
         * Key local ID. Used by content provider as an index to the DB
         * table.
         */
        public static final String _ID = "_id"; 
        /**
         *  Global ID for the community, e.g. JID or URI.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         *  The type of the community being stored. E.g. "disaster".
         *  The type will be defined and used by applications.
         */
        public static final String TYPE = "type";
        /**
         *  Name of the community. Is user-given.
         */
        public static final String NAME = "name";
        /**
         * Global ID of the person who owns this community.
         */
        public static final String OWNER_ID = "owner_id";
        /**
         * Set by user or Sync adapter, telling the user where this community
         * is originating from. Can for instance be Facebook (if this is a 
         * Facebook group) or SOCIETIES etc.
         */
        public static final String ORIGIN = "origin";
        /**
         * A user-provided description of the community.
         */
        public static final String DESCRIPTION = "description";
        /**
         * The date this record was created by its origin.
         */
        public static final String CREATION_DATE = "creation_date";
        
        /**
         * The date this record was last modified. Used by sync adapters.
         */
        public static final String LAST_MODIFIED_DATE = "last_modified_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services. Its value can be "dirty", "clean", "new", "deleted".
         */
        public static final String SYNC_STATUS = "sync_status";
        
    }
    
    /**
     * Use this class when searching for services, any services. It
     * gives you information about services that are in the market
     * place, installed on your device, or that belong to you.
     * 
     * Note: CREATION_DATE, LAST_MODIFIED_DATE, and SYNC_STATUS 
     * are set by SocialProvider. 

     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Services {
        /**
         * Use this Uri to search in the content provider.
         */
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING+ UriPathIndex.SERVICES);
        /**
         * Key local ID used by content provider. Index to the
         *  table holding service info.
         */
        public static final String _ID = "_id";
        /**
         *  Global ID for the service. E.g. packagename+appname in Android.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         *  The type of the service being stored.
         */
        public static final String TYPE = "type";
        /**
         *  User/provided name of the service
         */
        public static final String NAME = "name";
        /**
         * Global ID of the person who owns this service.
         */
        public static final String OWNER_ID = "owner_id";
        /**
         * Where this service comes from, e.g. Android Market.
         */
        public static final String ORIGIN = "origin";
        /**
         * A user-provided description of the service.
         */
        public static final String DESCRIPTION = "description";
        /**
         * The date this record was created by its origin.
         */
        public static final String CREATION_DATE = "creation_date";
        
        /**
         * The date this record was last modified. Used by sync adapters.
         */
        public static final String LAST_MODIFIED_DATE = "last_modified_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services. Its value can be "dirty", "clean", "new", "deleted".
         */
        public static final String SYNC_STATUS = "sync_status";
        /**
         * Boolean telling whether this service is available on this device.
         * E.g. if the service is available through an app on an Android device
         * then this boolean tells whether the app is installed on this
         * device. Or if this is a cloud service accessible through
         * a REST API this boolean might tell if the API is accessible.
         * 
         * Value needs to be set and kept updated by clients.
         */
        public static final String AVAILABLE = "available";
        /**
         * A field telling whether this service depends on another 
         * service to function. The value is the global ID of the
         * other service or null.
         */
        public static final String DEPENDENCY = "dependnecy";
        
        /**
         * String that contains the intent to be used to launch the service
         * using Androdi intent mechanism.
         */
        public static final String CONFIG = "config";
        
        /**
         * A URL to the code to be downloaded or to a web service interface.
         */
        public static final String URL = "url";
    }
    
    /**
     * Class that provides information about relationships among
     * two people. If you want to know who is friends with whom or
     * who is following whom etc. use this.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Relationship {
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING + UriPathIndex.RELATIONSHIP);

        /**
         * Key local ID. Needed for using cursors in Android. Note that _id is
         * unique only locally on this device.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the relationship.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the first person in the relationship.
         */
        public static final String GLOBAL_ID_P1 = "global_id_p1";
        /**
         * Global ID for the second person in the relationship.
         */
        public static final String GLOBAL_ID_P2 = "global_id_p2";
        /**
         * Type of the relationship. Can be e.g. friend, follower.
         */
        public static final String TYPE = "type"; 
        /**
         *  The original service where the relationship is defined.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";
    }
    /**
     * Class that provides information about people's membership
     * in communities. Use this if you want to find out who is
     * member in a community, or which communities a user is 
     * member of.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Membership {
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING + UriPathIndex.MEMBERSHIP);

        /**
         * Key local ID for this membership. Needed for using 
         * content providers in Android. Note that _id is
         * unique only locally on this device.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the membership.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the member.
         */
        public static final String GLOBAL_ID_MEMBER = "global_id_member";
        /**
         * Global ID for the community.
         */
        public static final String GLOBAL_ID_COMMUNITY = "global_id_community";
        /**
         * Type of the membership. Application-defined. Can be used as
         * e.g. role in the community.
         */
        public static final String TYPE = "type"; 
        /**
         *  The original service where the membership is defined.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";
    }
    
    /**
     * Class that provides information about what services people have 
     * shared in different communities.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Sharing {
        public static final Uri CONTENT_URI = 
                Uri.parse(AUTHORITY_STRING + UriPathIndex.SHARING);

        /**
         * Key local ID for this sharing. Needed for using 
         * content providers in Android. Note that _id is
         * unique only locally on this device.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the sharing.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the service.
         */
        public static final String GLOBAL_ID_SERVICE = "global_id_service";
        /**
         * Global ID for the sharing.
         */
        public static final String OWNER_GLOBAL_ID = "owner_global_id";
        /**
         * Global ID for the community.
         */
        public static final String GLOBAL_ID_COMMUNITY = "global_id_community";
        /**
         * Global ID for the community.
         */
        public static final String GLOBAL_ID_PERSON = "global_id_person";
        /**
         * Type of the sharing. Application-defined.
         */
        public static final String TYPE = "type"; 
        /**
         *  The original service where the sharing is defined.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";
    }
    
    /**
     * Represents a table containing all the activities that are 
     * added to the activity feeds for people. You can search 
     * the table using a specific user's global ID as feed owner 
     * ID.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class PeopleActivity{
	    public static final Uri CONTENT_URI = 
	            Uri.parse(AUTHORITY_STRING + UriPathIndex.PEOPLE_ACTIVITIY);
        /**
         * Key local ID for the activity. Needed for using 
         * content providers in Android. Note that _id is
         * unique only locally on this device.
         * 
         * Note: CREATION_DATE, and SYNC_STATUS are set by SocialProvider.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the activity.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the owner of the feed where the
         * activity is added.
         */
        public static final String GLOBAL_ID_FEED_OWNER = "global_id_feed_owner";
        /**
         * Global ID for the Actor of the activity.
         */
        public static final String GLOBAL_ID_ACTOR = "global_id_ACTOR";
        /**
         * Global ID for the object of the activity.
         */
        public static final String GLOBAL_ID_OBJECT = "global_id_object";
        /**
         * Global ID for the verb of the activity.
         */
        public static final String GLOBAL_ID_VERB = "global_id_verb";
        /**
         * Global ID for the target of the activity.
         */
        public static final String GLOBAL_ID_TARGET = "global_id_target";
        /**
         *  The original service where the activity was created.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";	
        /**
         * The date this activity was created.
         */
        public static final String CREATION_DATE = "creation_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services.
         */
        public static final String SYNC_STATUS = "sync_status";
        

    	
    }
    /**
     * Represents a table containing all the activities that are 
     * added to the activity feeds for communities. You can search 
     * the table using a specific community's global ID as feed owner 
     * ID.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class CommunityActivity{
	    public static final Uri CONTENT_URI = 
	            Uri.parse(AUTHORITY_STRING + UriPathIndex.COMMUNITY_ACTIVITIY);
        /**
         * Key local ID for the activity. Needed for using 
         * content providers in Android. Note that _id is
         * unique only locally on this device.
         * 
         * Note: CREATION_DATE, and SYNC_STATUS are set by SocialProvider.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the activity.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the owner community of the feed where the
         * activity is added.
         */
        public static final String GLOBAL_ID_FEED_OWNER = "global_id_feed_owner";
        /**
         * Global ID for the Actor of the activity.
         */
        public static final String GLOBAL_ID_ACTOR = "global_id_ACTOR";
        /**
         * Global ID for the object of the activity.
         */
        public static final String GLOBAL_ID_OBJECT = "global_id_object";
        /**
         * Global ID for the verb of the activity.
         */
        public static final String GLOBAL_ID_VERB = "global_id_verb";
        /**
         * Global ID for the target of the activity.
         */
        public static final String GLOBAL_ID_TARGET = "global_id_target";
        /**
         *  The original service where the activity was created.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";	
        /**
         * The date this activity was created.
         */
        public static final String CREATION_DATE = "creation_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services.
         */
        public static final String SYNC_STATUS = "sync_status";
    }
    /**
     * Represents a table containing all the activities that are 
     * added to the activity feeds for services. You can search 
     * the table using a specific service's global ID as feed owner 
     * ID.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class ServiceActivity{
	    public static final Uri CONTENT_URI = 
	            Uri.parse(AUTHORITY_STRING + UriPathIndex.SERVICE_ACTIVITY);
        /**
         * Key local ID for the activity. Needed for using 
         * content providers in Android. Note that _id is
         * unique only locally on this device.
         * 
         * Note: CREATION_DATE, and SYNC_STATUS are set by SocialProvider.
         */
        public static final String _ID = "_id"; 
        /**
         * Global ID for the activity.
         */
        public static final String GLOBAL_ID = "global_id";
        /**
         * Global ID for the owner service of the feed where the
         * activity is added.
         */
        public static final String GLOBAL_ID_FEED_OWNER = "global_id_feed_owner";
        /**
         * Global ID for the Actor of the activity.
         */
        public static final String GLOBAL_ID_ACTOR = "global_id_ACTOR";
        /**
         * Global ID for the object of the activity.
         */
        public static final String GLOBAL_ID_OBJECT = "global_id_object";
        /**
         * Global ID for the verb of the activity.
         */
        public static final String GLOBAL_ID_VERB = "global_id_verb";
        /**
         * Global ID for the target of the activity.
         */
        public static final String GLOBAL_ID_TARGET = "global_id_target";
        /**
         *  The original service where the activity was created.
         *  E.g. Facebook, SOCIETIES
         */
        public static final String ORIGIN = "origin";	
        /**
         * The date this activity was created.
         */
        public static final String CREATION_DATE = "creation_date";

        /**
         * Field used by sync adapters when synchronizing with cloud 
         * services.
         */
        public static final String SYNC_STATUS = "sync_status";
    }

    /**
	 * Class that provides information about the owner of 
	 * the current device. This is mainly information that will
	 * be extracted from CSS Manager Light. Note that you can have 
	 * as many CSS IDs as you wish.
	 * 
	 * @author Babak dot Farshchian at sintef dot no
	 *
	 */
	public static final class Me {
	    public static final Uri CONTENT_URI = 
	            Uri.parse(AUTHORITY_STRING + UriPathIndex.ME);
	
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
	    
	    /**
	     * Name of the service this ID was created at. E.g. it might be 
	     * a Facebook ID or a SOCIETIES ID. 
	     */
	    public static final String ORIGIN = "origin";
	}

	
	/**
	 * A utility class defining constants for UriMatcher in the 
	 * content provider and for using in case/switch sentences.
	 * @author Babak dot Farshchian at sintef dot no
	 *
	 */
	public static final class UriMatcherIndex{
		public static final int ME = 1;
		public static final int ME_SHARP = 2;
		public static final int PEOPLE = 3;
		public static final int PEOPLE_SHARP = 4;
		public static final int COMMUNITIES = 5;
		public static final int COMMUNITIES_SHARP = 6;
		public static final int SERVICES = 7;
		public static final int SERVICES_SHARP = 8;
		public static final int RELATIONSHIP = 9;
		public static final int RELATIONSHIP_SHARP = 10;
		public static final int MEMBERSHIP = 11;
		public static final int MEMBERSHIP_SHARP = 12;
		public static final int SHARING = 13;
		public static final int SHARING_SHARP = 14;
		public static final int PEOPLE_ACTIVITY = 15;
		public static final int PEOPLE_ACTIVITY_SHARP = 16;
		public static final int COMMUNITY_ACTIVITIY = 17;
		public static final int COMMUNITY_ACTIVITIY_SHARP = 18;
		public static final int SERVICE_ACTIVITY = 19;
		public static final int SERVICE_ACTIVITY_SHARP = 20;

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
    @Deprecated
    public static final class MyCommunity {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING +"/me/communities");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the community, e.g. JID
        public static final String OWNER_ID = "owner"; //Person who owns the community
      //  public static final String DISPLAY_NAME = "display_name"; //Name of the community to be shown to the user
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
    
    @Deprecated
    public static final class Community {
        public static final Uri CONTENT_URI = 
                    Uri.parse(AUTHORITY_STRING+"/communities");
        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the community, e.g. JID
        public static final String TYPE = "type"; //The type of the community being stored. E.g. "disaster".
        										//The type will be defined and used by applications.
        public static final String NAME = "name"; //Name of the community
    //    public static final String DISPLAY_NAME = "display_name"; //Name of the community to be shown to the user
        public static final String OWNER_ID = "owner"; //Person who owns the community
    //    public static final String CREATION_DATE = "creation_date";	
     //   public static final String MEMBERSHIP_TYPE = "membership_type"; //TODO: need to decide types.
        public static final String DIRTY = "dirty"; //Used to indicate whether this community's data is changed locally.
    }

    /**
     * Class that defines constants related to CSSs:
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    @Deprecated
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
    @Deprecated
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
    @Deprecated
    public static final class MembershipRecord {
        public static final String _ID = "_id"; //Key local ID
        public static final String COMMUNITY_ID = "community_id"; //Key local ID for involved community
        public static final String PERSON_ID = "person_id"; //Key local ID for involved person
        public static final String ROLE = "role"; //The role the person plays in the community
    }
//TODO: Add service sharing records
    
    
}
