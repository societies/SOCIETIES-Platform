/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.android.api.contentproviders;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Content Provider contract class for CSSManager content provider
 * Provides the base URI and for the content provider and the various table instances
 *
 */
public class CSSContentProvider {

	public static final String AUTHORITY = "org.societies.android.platform.content.androidcssmanager";
	
	public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY);

	public static final class CssNodes implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/CssNode");
		
		public final static String CSS_NODE_IDENTITY = "identity";
		public final static String CSS_NODE_TYPE = "type";
		public final static String CSS_NODE_STATUS = "status";
		public final static String CSS_NODE_RECORD = "record";

	}
	
	public static final class CssArchivedNodes implements BaseColumns {

		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/ArchivedCssNode");
		
		public final static String CSS_NODE_IDENTITY = "identity";
		public final static String CSS_NODE_TYPE = "type";
		public final static String CSS_NODE_STATUS = "status";
		public final static String CSS_NODE_RECORD = "record";

	}

	public static final class CssRecord implements BaseColumns {
		
		public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/cssRecord");
		
		public final static String CSS_RECORD_DOMAIN_SERVER = "domainServer";
		public final static String CSS_RECORD_CSS_HOSTING_LOCATION = "cssHostingLocation";
		public final static String CSS_RECORD_ENTITY = "entity";
		public final static String CSS_RECORD_FORENAME = "foreName";
		public final static String CSS_RECORD_NAME = "name";
		public final static String CSS_RECORD_IDENTITY_NAME = "identityName";
		public final static String CSS_RECORD_EMAILID = "emailID";
		public final static String CSS_RECORD_IMID = "imID";
		public final static String CSS_RECORD_SOCIALURI = "socialURI";
		public final static String CSS_RECORD_SEX = "sex";
		public final static String CSS_RECORD_HOME_LOCATION = "homeLocation";
		public final static String CSS_RECORD_CSS_IDENTITY = "cssIdentity";
		public final static String CSS_RECORD_STATUS = "status";
		public final static String CSS_RECORD_REGISTRATION = "cssRegistration";
		public final static String CSS_RECORD_INACTIVATION = "cssInactivation";
		public final static String CSS_RECORD_UPTIME = "cssUpTime";
	}
}
