package org.societies.api.android.internal.tables;

/**
 * Simple description of Service Table Structure.
 * @author Luca (Telecomitalia)
 */

import org.societies.android.platform.interfaces.IContentProvider;


public class CssNodesTable implements IContentProvider {
			
			// Database table
			public static final String TABLE_NAME_ARCH_NODE   =  "css_arch_nodes_table";
			public static final String TABLE_NAME_NODES		  = "css_nodes_table";
			
			public static final String KEY_ID 			 = "_ID";
			public static final String KEY_NODE_ID 		 = "node_id";
			public static final String KEY_CSS_ID  		 = "css_id";
			public static final String KEY_TIMESTAMP     = "timestamp" ;
			
			public static String SS_NODES_ARCHIV_URI	 =	"content://" + AUTHORITY + "/" + TABLE_NAME_ARCH_NODE;
			public static String CSS_NODES_URI	 		 =	"content://" + AUTHORITY + "/" + TABLE_NAME_NODES;
			
			
			// Database creation SQL statement
			public static final String CREATE_CSS_NODES_TABLE = 
					"CREATE TABLE " 
					+ TABLE_NAME_NODES
					+ "(" 
					+ KEY_ID 		+ " integer primary key autoincrement, " 
					+ KEY_NODE_ID 	+ " integer, " 
					+ KEY_CSS_ID	+ " integer, " 
					+ KEY_TIMESTAMP		+ " text " 
					+ ");";
			
			// Database creation SQL statement
			public static final String CREATE_CSS_ARCH_NODES_TABLE = 
								"CREATE TABLE " 
								+ TABLE_NAME_ARCH_NODE
								+ "(" 
								+ KEY_ID 		+ " integer primary key autoincrement, " 
								+ KEY_NODE_ID 	+ " integer, " 
								+ KEY_CSS_ID	+ " integer, " 
								+ KEY_TIMESTAMP		+ " text " 
								+ ");";
			
			
}
