/**
 * 
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.PriorAndCountTablesMismatchException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;


/**
*
* This Conditional Probability Table uses the format Prob{i,j,k} to represent aand index the values.
* 
* See the Interface specification of the Interface RVwithParents for description of the notation 
* of the indices and the parent configuration j.
* 
 * @author robert_p
 *
 */
public interface ConditionalProbabilityTable extends RVwithParents {
	

	/**
	 * Returns the value of the Probability Prob{x_i = x_i^k|parentConfiguration = j} for i,j,k.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @param node_i_range_value_index_k range_k value of the node left of the condition (x_i = x_i^k)
	 * @return The probability Prob{x_i = x_i^k|parentConfiguration = j}.
	 * @throws ParentConfigurationNotApplicableException if parentConfiguration does not fit with the Table
	 * @throws RangeValueNotApplicableException if node_i_range_value_k does not fit the Table 
	 * (i.e. incompatible with ConditionalProbabilityTable.getTargetRV())
	 * @throws PriorAndCountTablesMismatchException 
	 */
	public double getProbability(int parentConfiguration, int node_i_range_value_index_k) 
				throws ParentConfigurationNotApplicableException, RangeValueNotApplicableException, PriorAndCountTablesMismatchException;

	
	/**
	* Returns the value of the Probability Prob{x_i = x_i^k|parents} for i,parents and ,k.
	* The index i is the index of the node that "owns" the count table, and is thus implicit.
	* The index k is computed from the actual value of the RV with index i using node_i_range_value.
	* @param parents to the right of the condition as a Set of InstantiatedRV
	* @param node_i_range_value value of the node left of the condition: values according to getRVValue() of InstantiatedRV.
	* @return The probability Prob{x_i = x_i^k|parents}
	* @throws NodeValueIndexNotInNodeRangeException
	* @throws ParentsNotContainedException
	 * @throws PriorAndCountTablesMismatchException 
	 * @throws RVNotInstantiatedException 
	*/
		public double getProbability(Set<InstantiatedRV> parents, int node_i_range_value) throws
					NodeValueIndexNotInNodeRangeException, ParentsNotContainedException, PriorAndCountTablesMismatchException, RVNotInstantiatedException;	
		
		
	/**
	 * Compute and return the CP Table in the form of an array. The array[k][j] is over the two dimensions needed:
	 * k specifies the index of the target RV i (from 1 ... r_i); j is the parent configuration, also
	 * counting from 1. See Interface specification of the Interface RVwithParents.
	 * Note: the array is computed fresh each time this method is invoked and a new instance of this array is returned.
	 * @return the CP Table in the form of an array.
	 * @throws PriorAndCountTablesMismatchException 
	 */
	public double[][] getProbabilityTable() throws PriorAndCountTablesMismatchException;


	/* (non-Javadoc)
	 * This method will throw a UnsupportedOperationException
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#addParent(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void addParent(RandomVariable parent);

	/* (non-Javadoc)
	 * This method will throw a UnsupportedOperationException
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#removeParent(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void removeParent(RandomVariable parent);

}
