package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Map;


/**
 * @author fran_ko
 *
 */
public interface BayesLearner {
	
	public Map<RandomVariable, ConditionalProbabilityTable>runLearning(
			int millisecs, String data);

	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.impl.BayesianLearningIF#runBayesianLearning(int)
	 */
	public Map<RandomVariable, ConditionalProbabilityTable> runLearning(
			int millisecs, Candidate startingPoint);
}