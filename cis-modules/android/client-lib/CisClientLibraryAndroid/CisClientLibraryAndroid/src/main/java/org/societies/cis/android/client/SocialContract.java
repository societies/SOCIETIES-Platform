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
package org.societies.cis.android.client;

import android.net.Uri;


/**
 * Content provider contract file for CIS manager light.
 * 
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
            Uri.parse("content://org.societies.cis.android.SocialProvider");
    public static final String PROVIDER_READ_PERMISSION = "org.societies.cis.android.SocialProvider.READ";
    public static final String PROVIDER_WRITE_PERMISSION = "org.societies.cis.android.SocialProvider.WRITE";
    
    public static final class Groups {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.cis.android.SocialProvider/groups");
        public static final String _ID = "_id"; //Key column in the table
        public static final String GLOBAL_ID = "global_id"; //Global ID for the group.
        public static final String TYPE = "type"; //The type of the element being stored. CIS for groups.
        public static final String NAME = "name"; //Name column in the group
        public static final String DISPLAY_NAME = "display_name";
        public static final String OWNER_ID = "owner"; //Owner CSS jid of the group
        public static final String CREATION_DATE = "creation_date";	
    }

    /**
     * Provides constants for managing CIS-related queries.
     * 
     * @author Babak.Farshchian@sintef.no
     *
     */
    public static final class People {
        public static final Uri CONTENT_URI = 
                    Uri.parse("content://org.societies.cis.android.SocialProvider/people");
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
                Uri.parse("content://org.societies.cis.android.SocialProvider/services");
        public static final String _ID = "_id"; //Key column in the table
        public static final String GLOBAL_ID = "global_id"; //Global ID for the service. E.g. URL.
        public static final String TYPE = "type"; //The type of the element being stored. Service for service
        public static final String NAME = "name"; //Name of the service
        public static final String DISPLAY_NAME = "display_name";
        public static final String CREATION_DATE = "creation_date";		
    }

}
