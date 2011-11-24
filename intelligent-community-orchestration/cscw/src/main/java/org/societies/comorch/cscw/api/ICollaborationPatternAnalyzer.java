package org.societies.comorch.cscw.api;

import org.societies.cis.management.api.*;

/**
 * This interface gets a {@link CISActivityFeed} and can analyse it in order to find patterns
 * of collaboration. The input to the component is one or more CISActivityFeeds, and
 * the output is a list of users/CSSs. The component can be fine-tuned by changing the 
 * weighting in the selection algorithm, e.g. the user can specify that location messages
 * should weight more than activity messages when suggesting sub-CISs.
 * 
 * @author Babak.Farshchian@sintef.no
 * @version 0.1
 *
 */
public interface ICollaborationPatternAnalyzer {
	/**
	 * The three values here are currently used to configure the scores that decide the prioritization.
	 * Initially they are all equal to 5. The value can be from 1 to 10, 10 being highest.
	 */
	public int interactionFrequency=5, topicRelevance=5, serviceDataRelevance=5;
	/**
	 * This method is the one that returns the recommendation for new CISs. It uses the
	 * values of the prioritization scores and creates a list of CISs.
	 * 
	 * @param cssId ID of the CSS asking for recommendations.
	 * @return list of recommended CISs in form of CISRecord data type.
	 */
	public CISRecord[] getRecommendedCisList(String cssId);
	public CISRecord[] getRecommendedCisList(String cssId, String[] weights);
	public void setWeights(String[] weights);
	public void getWeights(String[] weights);
	
	

}
