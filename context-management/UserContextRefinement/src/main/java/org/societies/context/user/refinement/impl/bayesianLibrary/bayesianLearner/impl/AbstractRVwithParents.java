package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.InstantiatedRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.JointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RVwithParents;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;

/**
 * This class implements a general RVwithParents
 * 
 * @author robert_p
 * 
 */
public abstract class AbstractRVwithParents implements RVwithParents,
		Serializable {

	private SortedSet<RandomVariable>orderedParents;
	private SortedSet<RandomVariable>unmodifiableSortedSetofParents;
	private RandomVariable targetRV;

	private int j_max;
	private int k_max;
	protected RandomVariable[] parentArray;

	/**
*
*/
	public AbstractRVwithParents(RandomVariable targetRV,
			Collection<RandomVariable>parents_of_targetRV) {
		super();
		this.makeOrderedParents(parents_of_targetRV);
		this.targetRV = targetRV;
		this.j_max = this.computeMaximumParentConfigutionIndex_j_max(this
				.getOrderedParents());
		this.k_max = this.computeMaximumNodeConfigutionIndex_k_max(targetRV);
	}

	public int computeParentConfiguration(
			Set<InstantiatedRV>instantiatedMeasurements,
			boolean replaceMissingParentsWithNOOBs)
			throws ParentsNotContainedException {
		SimpleJointMeasurement jm = new SimpleJointMeasurement();
		jm.addAll(instantiatedMeasurements);
		return this.computeParentConfiguration(jm,
				replaceMissingParentsWithNOOBs);
	}

	public SortedSet<RandomVariable>getOrderedParents() {
		return this.unmodifiableSortedSetofParents;
	}

	public RandomVariable getTargetRV() {
		return this.targetRV;
	}

	/**
	 * Computes the value (as an integer from 1 to r_{parentIndex}) of the node
	 * with index parentIndex (from 1 to pp). r_{parentIndex} is the number of
	 * range values of the node with index parentIndex.
	 * 
	 * @param parentIndex
	 *            parent node index (from 1 to pp)
	 * @param instantiatedMeasurements
	 *            JointMeasurement containing the actual node values
	 * @return value (as an integer from 1 to r_{parentIndex}) of the node with
	 *         index parentIndex. Returns 0 in error case.
	 */
	private int getParentValueAtPosition(int parentIndex,
			JointMeasurement instantiatedMeasurements) {

		// Get the right parent we are looking at:
		RandomVariable parentNode = this.parentArray[parentIndex - 1];
		try {
			// Get the instantiated RV in the measurement:
			InstantiatedRV parentMeasured = (InstantiatedRV) ((JointMeasurement) instantiatedMeasurements)
					.getInstantiatedRV().get(parentNode);
			if (parentMeasured != null) {
				if (!parentMeasured.isMissingInstantiation()) {
					try {
						return 1 + parentNode
								.getNodeRangePositionFromValue(parentMeasured
										.getRVValue());
					} catch (RVNotInstantiatedException e) {
						System.err
								.println("In getParentValueAtPosition() catch (RVNotInstantiatedException e); this should not happen.");
						e.printStackTrace();
					}
				}
				return 0; // If it is not instantiated
			} else {
				return 0;
				// return 1 +
				// parentNode.getNodeRangePositionFromValue(parentNode
				// .getNOBSValue());
			}
		} catch (NodeValueIndexNotInNodeRangeException e) {
			e.printStackTrace();
			return 0;
		}
	}

	/**
	 * 
	 * Computes PROD_{m=1}^{m=n-1} r_{pp-m}] r_{l} is the number of range values
	 * of the node l.
	 * 
	 * @param n
	 * @param pp
	 *            number of parents
	 * @return PROD_{m=1}^{m=n-1} r_{pp-m}]
	 */
	private int computeProd(int n, int pp) {
		int prod = 1;
		for (int m = 1; m <= n - 1; m++) {
			// System.out.println(" in computeProd. n: " + n + " pp: " + pp
			// + " m: " + m);
			RandomVariable parentNode = this.parentArray[pp - m];
			prod *= parentNode.getNodeRange().length;
		}
		return prod;
	}

	/**
	 * Check if instantiatedMeasurements contain all the parents in this
	 * CountTable
	 * 
	 * @param instantiatedMeasurements
	 * @return true iff instantiatedMeasurements contains all the parents in
	 *         this CountTable
	 */
	private boolean measurementContainsTheseParentInstantiations(
			Collection<InstantiatedRV>instantiatedMeasurements) {
		return instantiatedMeasurements.containsAll(this.getOrderedParents());
	}

	/*
	 * Computes:
	 * 
	 * j_max = 1 + SUM_{n=1}^{n=pp}[(r_{pp-n} PROD_{m=1}^{m=n-1} r_{pp-m}]
	 * 
	 * where r_{l} is the number of range values of the node l.
	 */
	private int computeMaximumParentConfigutionIndex_j_max(
			SortedSet<RandomVariable>parents_of_node_i) {
		int pp = this.getOrderedParents().size();
		int max_parentConfiguration_j = 1;
		for (int n = 1; n <= pp; n++) {
			RandomVariable parentNode = this.parentArray[pp - n];
			int r_pp_minus_n = parentNode.getNodeRange().length;
			max_parentConfiguration_j += (r_pp_minus_n - 1)
					* this.computeProd(n, pp);
		}
		return max_parentConfiguration_j;
	}

	/**
	 * Example: If node_i has range members from node_i.getNodeRange() as 5,7,9
	 * then we have k_max is 3.
	 * 
	 * @param node_i
	 * @return the number of range members of node_i
	 */
	private int computeMaximumNodeConfigutionIndex_k_max(RandomVariable node_i) {
		return node_i.getNodeRange().length;
	}

	/*
	 * Computes the parent configuration index j for a given parent
	 * configuration as specified in the passed instantiatedMeasurements
	 * parameter.
	 * 
	 * In general, the index j of the j-th parent configuration is equal to:
	 * 
	 * j = 1 + SUM_{n=1}^{n=pp}[(j_{pp-n+1}-1) PROD_{m=1}^{m=n-1} r_{pp-m}]
	 * 
	 * where j_{l} is the index of the value of the l-th parent: x_pl =
	 * x_pl^{j_{l}}. And r_{l} is the number of range values of the node l.
	 * 
	 * @seede.kl.kn.bayesianLibrary.bayesianLearner.interfaces.CountTable#
	 * computeParentConfiguration(java.util.Set)
	 */
	public int computeParentConfiguration(
			JointMeasurement instantiatedMeasurements,
			boolean replaceMissingParentsWithNOOBs)
			throws ParentsNotContainedException {
		if (!replaceMissingParentsWithNOOBs) {
			if (!this
					.measurementContainsTheseParentInstantiations(((JointMeasurement) instantiatedMeasurements)
							.getInstantiatedRV().values())) {
				/*
				 * WAS: if (!this.measurementContainsTheseParentInstantiations(
				 * // Check if measurement contains all // parents in this RV
				 * with parents
				 * instantiatedMeasurement.getInstantiatedRV().values())) {
				 */
				throw new ParentsNotContainedException(
						"\nThis NaiveCountTable does not contain instantiatedParents: "
								+ instantiatedMeasurements);
			}
		}
		int pp = this.getOrderedParents().size();
		int parentConfiguration_j = 1;
		for (int n = 1; n <= pp; n++) {
			int j_pp_minus_n_plus_1 = this.getParentValueAtPosition(pp - n + 1,
					instantiatedMeasurements);
			if (j_pp_minus_n_plus_1 <= 0)
				return -1;
			parentConfiguration_j += (j_pp_minus_n_plus_1 - 1)
					* this.computeProd(n, pp);
		}
		return parentConfiguration_j;
	}

	public int getK_max() {
		return this.k_max;
	}

	public int getJ_max() {
		return this.j_max;
	}

	private void makeOrderedParents(
			Collection<RandomVariable>parents_of_node_i) {
		this.orderedParents = new TreeSet<RandomVariable>(
				parents_of_node_i);
		this.unmodifiableSortedSetofParents = Collections
				.unmodifiableSortedSet/*<RandomVariable>*/(this.orderedParents);
		this.parentArray = (RandomVariable[]) this.getOrderedParents().toArray(
				new RandomVariable[0]);
	}

	public void addParent(RandomVariable parent) {
		if (parent.equals(this.targetRV)) {
			throw new IllegalArgumentException("\n Cannot add RV to itself :"
					+ this.targetRV);
		}
		this.orderedParents.add(parent);
		this.parentArray = (RandomVariable[]) this.getOrderedParents().toArray(
				new RandomVariable[0]);
		this.j_max = this.computeMaximumParentConfigutionIndex_j_max(this
				.getOrderedParents());
	}

	public void removeParent(RandomVariable parent) {
		if (parent.equals(this.targetRV)) {
			throw new IllegalArgumentException(
					"\n Cannot remove RV from itself :" + this.targetRV);
		}
		this.orderedParents.remove(parent);
		this.parentArray = (RandomVariable[]) this.getOrderedParents().toArray(
				new RandomVariable[0]);
		this.j_max = this.computeMaximumParentConfigutionIndex_j_max(this
				.getOrderedParents());
	}

	public void removeAllParents() {
		this.orderedParents.clear();
		this.parentArray = (RandomVariable[]) this.getOrderedParents().toArray(
				new RandomVariable[0]);
		this.j_max = this.computeMaximumParentConfigutionIndex_j_max(this
				.getOrderedParents());
	}

	public boolean equals(Object o) {
		if (o instanceof AbstractRVwithParents) {
			AbstractRVwithParents orv = (AbstractRVwithParents) o;
			if (!(orv.getTargetRV().equals(this.getTargetRV())))
				return false;
			if (orv.getOrderedParents().equals(this.getOrderedParents()))
				return true;
		}
		return false;
	}

	public int hashCode() {
		// System.err.println("hc for: " + this.toString() + " = " +
		// (this.getTargetRV().hashCode() + 31 *
		// this.getOrderedParents().hashCode()));
		return this.getTargetRV().hashCode() + 31
				* this.getOrderedParents().hashCode();
	}

	public boolean containsExactlyAllParents(Set<RandomVariable>test_set) {
		SortedSet<RandomVariable>ordered_parents = this
				.getOrderedParents();

		if (ordered_parents.containsAll(test_set)) {
			if (test_set.containsAll(ordered_parents)) {
				return true;
			}
		}
		return false;
	}

}
