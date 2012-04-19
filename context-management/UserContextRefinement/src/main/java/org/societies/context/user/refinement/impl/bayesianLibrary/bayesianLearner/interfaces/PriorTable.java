/**
 * 
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;

/**
 * @author robert_p
 *
 */
public interface PriorTable extends RVwithParents {

	/**
	 * Returns the value of the prior table N_{i,j,k} for i,j,k.
	 * The index i is the index of the node that "owns" the prior  table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @param node_i_range_value_index_k range_k value of the node left of the condition
	 * @return the virtual count N_{i,j,k}. See RVwithParents Interface description
	 * @throws ParentConfigurationNotApplicableException
	 * @throws RangeValueNotApplicableException
	 */
	public double getVirtualCount(int parent_configuration_j,
			int node_i_range_value_index_k)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException;

	/**
	 * Returns the value of the prior table N_{i,j} for i,j.
	 * The index i is the index of the node that "owns" the prior table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @return the virtual count N_{i,j}. See RVwithParents Interface description
	 * @throws ParentConfigurationNotApplicableException
	 */
	public double getVirtualCount(int parent_configuration_j)
			throws ParentConfigurationNotApplicableException;	
	
	
}
