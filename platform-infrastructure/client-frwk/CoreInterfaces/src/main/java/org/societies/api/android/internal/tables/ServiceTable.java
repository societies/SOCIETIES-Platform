package org.societies.api.android.internal.tables;

/**
 * Simple description of Service Table Structure.
 * @author Luca (Telecomitalia)
 */

import org.societies.android.platform.interfaces.IContentProvider;


public class ServiceTable implements IContentProvider {
			
			// Database table
			public static final String TABLE_NAME		 = "service_data";
			public static final String KEY_ID 			 = "_ID";
			public static final String KEY_SERVICE 		 = "service";
			public static final String KEY_NAME 		 = "key";
			public static final String KEY_VALUE      	 = "value" ;
			
			
			public static String SERVICE_DATA_URI		 =	"content://" + AUTHORITY + "/" + TABLE_NAME;
			
			
			// Database creation SQL statement
			public static final String CREATE_SERVICE_TABLE = 
					"CREATE TABLE " 
					+ TABLE_NAME
					+ "(" 
					+ KEY_ID 		+ " integer primary key autoincrement, " 
					+ KEY_SERVICE 	+ " text not null, " 
					+ KEY_NAME	 	+ " text not null, " 
					+ KEY_VALUE		+ " text not null " 
					+ ");";
			
			
}
