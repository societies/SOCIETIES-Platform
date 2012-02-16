package org.societies.api.android.internal.tables;

/**
 * Simple description of Service Table Structure.
 * @author Luca (Telecomitalia)
 */

import org.societies.android.platform.interfaces.IContentProvider;


public class CssNodeTable implements IContentProvider {
			
			// Database table
			public static final String TABLE_NAME		 = "css_node_table";
			public static final String KEY_ID 			 = "_ID";
			public static final String KEY_IDENTITY 	 = "identity";
			public static final String KEY_STATUS 		 = "status";
			public static final String KEY_TYPE      	 = "type" ;
			
			public static String CSS_NODE_URI		     =	"content://" + AUTHORITY + "/" + TABLE_NAME;
			
			
			// Database creation SQL statement
			public static final String CREATE_CSS_NODE_TABLE = 
					"CREATE TABLE " 
					+ TABLE_NAME
					+ "(" 
					+ KEY_ID 		+ " integer primary key autoincrement, " 
					+ KEY_IDENTITY 	+ " text not null, " 
					+ KEY_STATUS	+ " integer, " 
					+ KEY_TYPE		+ " integer  " 
					+ ");";
			
			
}
