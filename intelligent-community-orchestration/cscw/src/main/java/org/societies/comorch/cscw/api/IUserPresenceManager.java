package org.societies.comorch.cscw.api;


/**
 * This interface allows a user to manually or automatically set the availability value
 * in multiple CISs. For instance, if the user is involved in one CIS and does not want
 * to be siturbed with information from other CISs then the presence/availability can be
 * set to unavailable in other CISs. The interface accepts an array of weighting 
 * parameters to be used to affect the automatic suggestions. Note that this interface
 * does not actually set the presence values in the CIS. That has to be done by others.
 * 
 * @author Babak.Farshchian@sintef.no
 * @version 0.1
 *
 */
public interface IUserPresenceManager {
	/**
	 * The three values here are currently used to configure the scores that decide the prioritization.
	 * Initially they are all equal to 5. The value can be from 1 to 10, 10 being highest.
	 * TODO: make this list more generic in order to allow arbitrary number of weight parameters.
	 * TODO: Remove these parameters. They are implementation details.
	 */
	public int interactionFrequency=5, topicRelevance=5, serviceDataRelevance=5;
	/**
	 * This method is the one that returns the list of presence/availability values to set.
	 * It uses the values of the prioritization scores and creates a list of CISs.
	 * TODO: change string to some CSSRecord or other data type.
	 * 
	 * @param cssId ID of the CSS asking for recommendations.
	 * @return list of recommended CSS IDs in form of String array.
	 */
	public String[] getRecommendedPresenceList(String cssId);
	public String[] getRecommendedPresenceList(String cssId, String[] weights);
	public void setWeights(String[] weights);
	public void getWeights(String[] weights);
}
