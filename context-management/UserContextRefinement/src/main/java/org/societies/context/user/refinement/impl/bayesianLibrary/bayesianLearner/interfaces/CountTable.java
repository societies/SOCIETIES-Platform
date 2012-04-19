
/**
 *
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentConfigurationNotApplicableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RangeValueNotApplicableException;


/**
 * This count table uses the format N_{i,j,k} to represent the counts.
 *
 * See the Interface specification of the Interface RVwithParents for description of the notation
 * of the indices and the parent configuration j.
 *
 * @author robert_p
 *
 */
public interface CountTable extends RVwithParents {

	/**
	 * Returns the value of the table N_{i,j,k} for i,j,k.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @param node_i_range_value_index_k range_k value of the node left of the condition
	 * @return the count N_{i,j,k}. See RVwithParents Interface description
	 * @throws ParentConfigurationNotApplicableException
	 * @throws RangeValueNotApplicableException
	 */
	public int getCount(int parent_configuration_j,
			int node_i_range_value_index_k)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException;

	/**
	 * Returns the value of the table N_{i,j} for i,j.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @return the count N_{i,j}. See RVwithParents Interface description
	 * @throws ParentConfigurationNotApplicableException
	 */
	public int getCount(int parent_configuration_j)
			throws ParentConfigurationNotApplicableException;

	/**
	 * Returns the value of the table N_{i,j,k} for i,j,k.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * The index k is computed from the actual value of the RV with index i.
	 * Here, j is computed from the actual instantiations of the parents.
	 * @param parents to the right of the condition as a Set of InstantiatedRV
	 * @param node_i_range_value value of the node left of the condition: values according to getRVValue() of InstantiatedRV.
	 * @return the count N_{i,j,k}. See RVwithParents Interface description
	 * @throws NodeValueIndexNotInNodeRangeException
	 * @throws ParentsNotContainedException
	 * @throws RVNotInstantiatedException
	 */
	public int getCount(Set<InstantiatedRV>parents, int node_i_range_value)
			throws NodeValueIndexNotInNodeRangeException,
			ParentsNotContainedException, RVNotInstantiatedException;

	/**
	 * Returns the value of the table N_{i,j} for i,j.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * Here, j is computed from the actual instantiations of the parents.
	 * @param parents to the right of the condition as a Set of InstantiatedRV
	 * @return the count N_{i,j,k}. See RVwithParents Interface description
	 * @throws ParentsNotContainedException
	 */
	public int getCount(Set<InstantiatedRV>parents)
			throws ParentsNotContainedException;

	/**
	 * Increments the value of the table N_{i,j,k} for i,j,k.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * @param parent_configuration_j to the right of the condition
	 * @param node_i_range_value_index_k range_k value of the node left of the condition
	 * @throws ParentConfigurationNotApplicableException
	 * @throws RangeValueNotApplicableException
	 */
	public void incrementCount(int parent_configuration_j,
			int node_i_range_value_index_k)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException;

	/**
	 * Increments the value of the table for i,j,k.
	 * The index i is the index of the node that "owns" the count table, and is thus implicit.
	 * @param countValue the new value of N_{i,j,k}
	 * @param parent_configuration_j to the right of the condition
	 * @param node_i_range_value_index_k range_k value of the node left of the condition
	 * @throws ParentConfigurationNotApplicableException
	 * @throws RangeValueNotApplicableException
	 */
	public void setCount(int countValue, int parent_configuration_j,
			int node_i_range_value_index_k)
			throws ParentConfigurationNotApplicableException,
			RangeValueNotApplicableException;

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

	/* (non-Javadoc)
	 * This method will throw a UnsupportedOperationException
	 * @see de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RVwithParents#removeAllParents(de.kl.kn.bayesianLibrary.bayesianLearner.interfaces.RandomVariable)
	 */
	public void removeAllParents();

	public boolean isCounted();

	public void setCounted(boolean counted);

}
