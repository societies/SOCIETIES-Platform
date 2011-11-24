package org.societies.comorch.cscw.api;


/**
 * This interface allows a user to manually or automatically select some friends 
 * or CIS members to follow more closely. The interface can suggest a group of
 * CSSs to follow closely. The user can set some parameters to guide the suggestions
 * and adjust the results. The interface does not do anything to actually follow the 
 * CSSs closely. It only supports the user to sellect who to follow. Other applications
 * or components have to actually do the following. 
 * 
 * @author Babak.Farshchian@sintef.no
 * @version 0.1
 *
 */
public interface IMemberVisibilityManager {
	/**
	 * The three values here are currently used to configure the scores that decide the prioritization.
	 * Initially they are all equal to 5. The value can be from 1 to 10, 10 being highest.
	 * TODO: make this list more generic in order to allow arbitrary number of weight parameters.
	 */
	public int interactionFrequency=5, topicRelevance=5, serviceDataRelevance=5;
	/**
	 * This method is the one that returns the list of CSSs to follow. It uses the
	 * values of the prioritization scores and creates a list of CISs.
	 * TODO: change string to some CSSRecord or other data type.
	 * 
	 * @param cssId ID of the CSS asking for recommendations.
	 * @return list of recommended CSS IDs in form of String array.
	 */
	public String[] getRecommendedPeopleList(String cssId);
	public String[] getRecommendedPeopleList(String cssId, String[] weights);
	public void setWeights(String[] weights);
	public void getWeights(String[] weights);
}
