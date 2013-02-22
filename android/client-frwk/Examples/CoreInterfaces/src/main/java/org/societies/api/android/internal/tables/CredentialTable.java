package org.societies.api.android.internal.tables;

import org.societies.android.platform.interfaces.IContentProvider;

/**
 * Simple description of Credential Table Structure.
 * @author Luca (Telecomitalia)
 */

public class CredentialTable implements IContentProvider {
			
	
			// Database table
			public static final String TABLE_NAME			 = "credential";
		
			public static final String KEY_ID 				 = "_id";
			public static final String KEY_USERNAME 		 = "username";
			public static final String KEY_PASSWORD 		 = "password" ;
			public static final String KEY_SERVICE 	  	 	= "service";
			public static final String KEY_FIRSTNAME 	 	= "firstname";
			public static final String KEY_LASTNAME	 	 	= "lastname";
			public static final String KEY_COMPANY	 	 	= "company";
			public static final String KEY_APPLICATION 	 	= "app";
			public static final String KEY_TOKEN		 	 = "token";
			public static final String KEY_USEPROXY 		 = "useproxy";
			public static final String KEY_PROXY_URL		 = "proxy_url";
			
			
			public static String CREDENTIAL_DATA_URI		 =	"content://"+ AUTHORITY + "/" + TABLE_NAME;
			
			
			// Database creation SQL statement
			public static final String CREATE_CREDENTIAL_TABLE = 
					"CREATE TABLE " 
					+ TABLE_NAME
					+ "(" 
					+ KEY_ID 		+ " integer primary key autoincrement, " 
					+ KEY_USERNAME 	+ " text not null, " 
					+ KEY_PASSWORD 	+ " text not null, " 
					+ KEY_SERVICE	+ " text not null, " 
					+ KEY_FIRSTNAME	+ " text, " 
					+ KEY_LASTNAME	+ " text, " 
					+ KEY_COMPANY	+ " text, " 
					+ KEY_APPLICATION	+ " text, " 
					+ KEY_TOKEN		+ " text, " 
					+ KEY_USEPROXY	+ " bool, " 
					+ KEY_PROXY_URL	+ " text " 
					+ ");";
			
}
