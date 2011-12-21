/**
 * Is a feed of activities that are collected from CIS members and their shared services.
 * 
 * @link CISActivity
 * @author Babak.Farshchian@sintef.no
 * @version 0
 */
package org.societies.cis.management.api;

import java.util.ArrayList;
import java.util.List;

public class CISActivityFeed {

	//constructor
	public CISActivityFeed() {
		activities = new ArrayList<CISActivity>();
	}
	
	public List<CISActivity> activities; 
	//public CISActivity[] activities; 
	// I've changed the array into a list. I was unsure if this list needed to be synchronized and therefore
	// I did not create it as Vector (Thomas - Sintef)
	
	public void getActivities(String CssId, String timePeriod){};
	public void getActivities(String CssId, String query, String timePeriod){};
	public void addCISActivity(CISActivity activity){};
	public void cleanupFeed(String criteria){};

	
	
	
}
