/**
 * Is a feed of activities that are collected from CIS members and their shared services.
 * 
 * @link CISActivity
 * @author Babak.Farshchian@sintef.no
 * @version 0
 */
package org.societies.cis.management.api;

public class CISActivityFeed {
	public CISActivity[] activities;
	public void getActivities(String CssId, String timePeriod){};
	public void getActivities(String CssId, String query, String timePeriod){};
	public void addCISActivity(CISActivity activity){};
	public void cleanupFeed(String criteria){};

}
