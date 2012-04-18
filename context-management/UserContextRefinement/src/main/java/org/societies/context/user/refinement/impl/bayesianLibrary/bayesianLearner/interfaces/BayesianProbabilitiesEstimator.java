package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.SortedSet;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeNotAvailableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesianNetworkCandidate;

/**
 * A Bayesian Probabilities Estimator takes measurements of RVs and allows count tables
 * and CPTs to be computed given some specific configuration of the RVs' instantiations.
 * 
 * To allow compatibility with Maximaization Expectation it includes the method
 * setCurrentBayesianNetworkStructure().
 * 
 * This Interface specifies how measurements enter the Estimator and how the Counts and the
 * CPT is returned in a query.
 * @author robert_p
 *
 */
public interface BayesianProbabilitiesEstimator {

		/**
		 * Adds a new joint measurement to the Learning Engine.
		 * @param meas the joint measurement to add
		 */
		public void addMeasurement(JointMeasurement meas);
		
		
		/**
		 *
		 * This method triggers re-learning of all currently cached Tables.
		 * @throws NodeNotAvailableException if target nodes are not in the measurement set
		 */
		public void refreshAllLearningTables() throws NodeNotAvailableException;				
		
		/**
		 * This method returns the raw counts in the form of a count table.
		 * It represents the counts for the states of the target node given all
		 * configurations of the passed parentNodes.
		 * @param targetNode for the counts (left of given dash)
		 * @param parentNodes for the counts (right of given dash)
		 * @return the count table that has been learnt so far in the parent configuration 
		 * order as specified in CountTable and the parentNodes indices.
		 */
		public CountTable getCounts(RandomVariable targetNode, 
				SortedSet<RandomVariable> parentNodes);
		
		
		/**
		 * This method returns P(targetNode | parentNodes).
		 * It represents the PDF for the states of the target node given all
		 * configurations of the passed parentNodes.
		 * @param targetNode for the CPT (left of given dash)
		 * @param parentNodes for the CPT (right of given dash)
		 * @param alphas for (target node | parentNodes) used to compute the CPT
		 * @return the CPT that has been learnt so far in the parent configuration 
		 * order as specified in ConditionalProbabilityTable.
		 * @throws CountsNotCompleteException 
		 */
		public ConditionalProbabilityTable getCPT(RandomVariable targetNode, 
				SortedSet<RandomVariable> parentNodes, PriorTable alphas) 
				throws CountsNotCompleteException;

		/**
		 * This method is a utility that returns P(targetNode | parentNodes) given
		 * the count table and prior table. It does not depend on the state of the
		 * underlying BayesianProbabilitiesEstimator; the result depends only on the passed
		 * parameters.
		 * 
		 * It represents the PDF for the states of the target node given all
		 * configurations of the passed parentNodes.
		 * @param targetNode for the CPT (left of given dash)
		 * @param parentNodes for the CPT (right of given dash)
		 * @param counts for (target node | parentNodes) used to compute the CPT  - in the parent configuration 
		 * order as specified in counts.
		 * @param alphas for (target node | parentNodes) used to compute the CPT
		 * @return the CPT based on the counts and priors
		 * @throws CountsNotCompleteException 
		 */
		public ConditionalProbabilityTable computeCPT(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes, 
						CountTable counts, PriorTable alphas) throws CountsNotCompleteException;

		/**
		 * Clear the internal count tables and CPTs
		 */
		public void resetTables();

		public void clearMeasurements();

		/**
		 * Sets the BN to be used for anything like EM for missing data.
		 */
		public void setCurrentBayesianNetworkStructure(BayesianNetworkCandidate bnc);
		
		/**
		 * Computes the uniform prior
		 * @param n_equiv
		 * @param rv
		 * @param parents
		 * @return
		 */
		public PriorTable getUniformPriors(int n_equiv, RandomVariable rv, SortedSet<RandomVariable> parents);
		
}