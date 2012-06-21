package org.societies.android.platform;

import android.net.Uri;


/**
 * Content provider contract file for CIS manager light.
 * 
 *TODO: change the names of the content urls to the new project-standarad package names.
 * @author Babak.Farshchian@sintef.no
 *
 */
public final class SocialContract {

    /**
     * Provides constants for managing CIS-related queries.
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
	
	//The main authority, i.e. the base URI for all operations:
    public static final Uri AUTHORITY = 
            Uri.parse("content://org.societies.android.platform.cis");
    //Constants used to define read and write permissions for social data:
    public static final String PROVIDER_READ_PERMISSION = "org.societies.cis.android.SocialProvider.READ";
    public static final String PROVIDER_WRITE_PERMISSION = "org.societies.cis.android.SocialProvider.WRITE";
    
    
    /**
     * Class that provides information about the owner of 
     * the current CSS.
     * 
     * @author Babak dot Farshchian at sintef dot no
     *
     */
    public static final class Me {
        public static final Uri CONTENT_URI = 
                Uri.parse("content://org.societies.android.platform.cis/me");

        public static final String _ID = "_id"; //Key local ID
        public static final String GLOBAL_ID = "global_id"; //Global ID for the community, e.g. JID
        public static final String NAME = "name"; //Name of the community
        public static final String DISPLAY_NAME = "display_name"; //Name of the community to be shown to the user
    	
    }
    /**
     * Class that defines a pointer (record) to a CIS that I own or am member of.
     * The CIS itself can be retrieved from Community using GLOBAL_ID that
     * you get here. MyCommunity gives you a list of CIS records that you
     * are a member of. You can use this list to look up the actual CIS in 
     * Community. 
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class MyCommunity {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.android.platform.cis/me/communities");
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
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class Community {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.android.platform.cis/communities");
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
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class Person {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.android.platform.cis/people");
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
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class Service {
        public static final Uri CONTENT_URI = 
                Uri.parse("content://org.societies.android.platform.cis/services");
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
     * @author Babak.Farshchian@sintef.no
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
