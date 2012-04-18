package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.SortedSet;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeNotAvailableException;
/**
 * @author robert_p
 *
 */
public interface BayesianProbabilitiesEstimatorOldAPI {

	/**
	 * Adds a new joint measurement to the Learning Engine.
	 * @param meas the joint measurement to add
	 */
	public void addMeasurement(JointMeasurement meas);

	/**
	 *
	 * This method prepares for learning of the distribution or expected value of P(node_i|parents_of_node_i).
	 * This method is non-blocking
	 */
	public void requestPDFLearning(RandomVariable node_i, SortedSet<RandomVariable> parents_of_node_i);
	/**
	 *
	 * Add a prior table (the parent set and target node is obtainable from
	 * the PriorTable by using its getOrderedParents() method and getTargetRV() method.
	 * @param alphas the prior table
	 */
	public void setPrior(PriorTable alphas);

	/**
	 *
	 * This method actually triggers learning of requested nodes and parents (see requestLearning method).
	 * This method is non-blocking
	 * @throws NodeNotAvailableException if target nodes are not in the measurement set
	 */
	public void triggerPDFLearning() throws NodeNotAvailableException;				
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
			SortedSet<RandomVariable>parentNodes);

	/**
	 * This method returns the Priors in the form of a count table.
	 * It represents the Priorts for the states of the target node given all
	 * configurations of the passed parentNodes.
	 * @param targetNode for the prior (left of given dash)
	 * @param parentNodes for the prior (right of given dash)
	 * @return the prior table that has been entered for the parent
	 * configuration using setPrior(PriorTable) and in the
	 * order as specified in CountTable and the parentNodes indices.
	 */
	public PriorTable getPriors(RandomVariable targetNode,
			SortedSet<RandomVariable>parentNodes);

	/**
	 * This method returns P(targetNode | parentNodes).
	 * It represents the PDF for the states of the target node given all
	 * configurations of the passed parentNodes.
	 * @param targetNode for the CPT (left of given dash)
	 * @param parentNodes for the CPT (right of given dash)
	 * @return the CPT that has been learnt so far in the parent configuration
	 * order as specified in ConditionalProbabilityTable.
	 * @throws CountsNotCompleteException
	 */
	public ConditionalProbabilityTable getCPT(RandomVariable targetNode,
			SortedSet<RandomVariable>parentNodes)
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
	public ConditionalProbabilityTable computeCPT(RandomVariable targetNode,
			SortedSet<RandomVariable>parentNodes, CountTable counts,
			PriorTable alphas) throws CountsNotCompleteException;

	/**
	 *
	 */
	public void resetTables();

	public void clearMeasurements();

}