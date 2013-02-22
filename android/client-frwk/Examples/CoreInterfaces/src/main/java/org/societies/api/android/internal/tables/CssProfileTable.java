package org.societies.api.android.internal.tables;

/**
 * Simple description of Service Table Structure.
 * @author Luca (Telecomitalia)
 */

import org.societies.android.platform.interfaces.IContentProvider;


public class CssProfileTable implements IContentProvider {
			
			// Database table
			public static final String TABLE_NAME		 = "css_profile_table";
			
			
			
			public static final String KEY_ID 			 = "_ID";
			public static final String KEY_ENTITY		 = "entity";
			public static final String KEY_FORE_NAME	 = "fore_name";
			public static final String KEY_NAME			 = "name";
			public static final String KEY_ENTITY_NAME	 = "entity_name";
			public static final String KEY_PASSWORD		 = "password";
			
			public static final String KEY_SEX			 = "sex";
			public static final String KEY_HOMELOCATION	 = "home_location";
			
			public static final String KEY_STATUS 		 = "status";
			
			public static final String KEY_ENTITY_TYPE   = "entity_type";
			public static final String KEY_GENDER_TYPE   = "gender_type";
			public static final String KEY_PRESENCE_TYPE = "presence_type";
			
			public static final String KEY_IDENTITY_NAME = "identity_name";
			public static final String KEY_EMAIL_ID		 = "email_id";
			public static final String KEY_IM_ID		 = "im_id";
			public static final String KEY_SOCIAL_URI	 = "social_uri";
			
			public static final String KEY_CSS_IDENTITY	 = "css_identity";
			public static final String KEY_CSS_REGISTR	 = "css_registration";
			
			public static final String KEY_UPTIME		 = "css_uptime";
			public static final String KEY_PRESENCE		 = "presence";
			public static final String KEY_CSS_INACTIV   = "css_inactivation";
			
			public static final String KEY_CSS_NODES	 	= "css_nodes";
			public static final String KEY_CSS_NODES_ARCH   = "css_node_archive";
			
			
			
			
			public static String CSS_PROFILE_URI		 =	"content://" + AUTHORITY + "/" + TABLE_NAME;
			
			
			
			// Database creation SQL statement
			public static final String CREATE_CSS_PROFILE_TABLE = 
					"CREATE TABLE " 
					+ TABLE_NAME
					+ "(" 
					+ KEY_ID 			+ " integer primary key autoincrement, " 
					+ KEY_ENTITY 		+ " integer, " 
					+ KEY_FORE_NAME 	+ " text not null, " 
					+ KEY_NAME		 	+ " text not null, " 
					+ KEY_ENTITY_NAME 	+ " text not null, " 
					+ KEY_PASSWORD	 	+ " text not null, " 
					+ KEY_SEX		 	+ " integer, " 
					+ KEY_HOMELOCATION 	+ " text not null, " 
					+ KEY_STATUS 		+ " text not null, " 
					+ KEY_ENTITY_TYPE 	+ " text not null, " 
					+ KEY_GENDER_TYPE 	+ " text not null, " 
					+ KEY_PRESENCE_TYPE + " text not null, " 
					+ KEY_IDENTITY_NAME + " text not null, " 
					+ KEY_EMAIL_ID	 	+ " text not null, " 
					+ KEY_IM_ID		 	+ " text not null, " 
					+ KEY_SOCIAL_URI 	+ " text not null, " 
					+ KEY_CSS_IDENTITY 	+ " text not null, " 
					+ KEY_CSS_INACTIV 	+ " text not null, " 
					+ KEY_CSS_REGISTR 	+ " text not null, " 
					+ KEY_CSS_NODES		+ " text, "
					+ KEY_CSS_NODES_ARCH+ " text, "
					+ KEY_PRESENCE 	+ " integer, "
					+ KEY_UPTIME 	+ " integer  " 
					+ ");";
			
			
}
