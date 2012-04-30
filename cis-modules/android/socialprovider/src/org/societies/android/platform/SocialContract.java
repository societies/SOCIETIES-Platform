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
    public static final Uri AUTHORITY = 
            Uri.parse("content://org.societies.android.platform.cis");
    public static final String PROVIDER_READ_PERMISSION = "org.societies.cis.android.SocialProvider.READ";
    public static final String PROVIDER_WRITE_PERMISSION = "org.societies.cis.android.SocialProvider.WRITE";
    
    public static final class Groups {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.android.platform.cis/groups");
        public static final String _ID = "_id"; //Key column in the table
        public static final String GLOBAL_ID = "global_id"; //Global ID for the group.
        public static final String TYPE = "type"; //The type of the element being stored. CIS for groups.
        public static final String NAME = "name"; //Name column in the group
        public static final String DISPLAY_NAME = "display_name";
        public static final String OWNER_ID = "owner"; //Owner CSS jid of the group
        public static final String CREATION_DATE = "creation_date";	
        public static final String MEMBERSHIP_TYPE = "membership_type";	

    }

    /**
     * Provides constants for managing CIS-related queries.
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class People {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.android.platform.cis/people");
        public static final String _ID = "_id"; //Key column in the table
        public static final String GLOBAL_ID = "global_id"; //Global ID for the person.
        public static final String TYPE = "type"; //The type of the element being stored. CSS for people
        public static final String NAME = "name"; //Name of the person
        public static final String EMAIL = "email"; //Owner CSS jid of the group
        public static final String DISPLAY_NAME = "display_name";
        public static final String CREATION_DATE = "creation_date";	
    }
    
    public static final class Services {
        public static final Uri CONTENT_URI = 
                Uri.parse("content://org.societies.android.platform.cis/services");
        public static final String _ID = "_id"; //Key column in the table
        public static final String GLOBAL_ID = "global_id"; //Global ID for the service. E.g. URL.
        public static final String TYPE = "type"; //The type of the element being stored. Service for service
        public static final String NAME = "name"; //Name of the service
        public static final String DISPLAY_NAME = "display_name";
        public static final String CREATION_DATE = "creation_date";		
    }

}
