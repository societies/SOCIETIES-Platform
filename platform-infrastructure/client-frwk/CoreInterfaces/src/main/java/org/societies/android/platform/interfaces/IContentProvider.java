package org.societies.android.platform.interfaces;

/**
 * Interface to get AUTORITY of Android Societies Content provider
 * 
 * @author Luca (Telecomitalia)
 */
public interface IContentProvider {

	// Content Provider Authority
	String AUTORITY = "org.societies.android.platform.contentprovider";
	
	String[] tables = {"credential", "service_data"};
	
}
