package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Map;


public interface BayesianLearningClient {
	
	/**
	 * This is used to pass results to a client of Bayesian learning.
	 * @param rv
	 * @param cpt
	 */
	public void updateNode(String network_ID, RandomVariable rv, ConditionalProbabilityTable cpt); 
	
	
	/**
	 * This is used to pass results to a client of Bayesian learning.
	 * @param rv
	 * @param cpt
	 */
	public void setNetwork(String network_ID, Map<RandomVariable, ConditionalProbabilityTable> rv_cpt_map); 
	
}
