
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Set;
import java.util.SortedSet;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.ParentsNotContainedException;


/**
* This Interface is used to represent a RV and its parents, the latter in a certain order
* so as to have a unique parent configuration index j.
* See �A Tutorial on Learning With Bayesian Networks�, David Heckerman, March 1995, MSR-TR-95-06
* for the notation.
*
* All nodes with index l have r_l range values.
* A node with index l can take on values from the range set: {x_l^1,x_l^2,x_l^3,...,x_l^(r_l)}
* The index i denotes the target node; it has r_i range values. the index i is implicit in the count table as
* the count table is associated with the node i.
* The index j denotes the j'th configuration of the parents of node i (1<=j<=q_i); where
* q_i is = PRODUCT_{over all parents of i}(r_l) where node l is a parent of node i.
* The index k denotes the k'th value of the range of node i (1<=k<=r_i).
*
* The parent configurations are built and indexed by following this order:
* j=1->(x_p1^1, x_p2^1, ..., x_pp^1), j=2->(x_p1^1, x_p2^1, ..., x_pp^2), ..., j=r_pp->(x_p1^1, x_p2^1, ..., x_pp^(r_pp)),
* j=1+r_pp->(x_p1^1, x_p2^1, ..., x_(pp-1)^2, x_pp^1), j=2+r_pp->(x_p1^1, x_p2^1, ..., x_(pp-1)^2, x_pp^2), ..., j=1+r_pp->(x_p1^1, x_p2^1, ..., x_(pp-1)^2, x_pp^(r_pp)),
* (x_p1^1, x_p2^1, ..., x_(pp-1)^3, x_pp^1), (x_p1^1, x_p2^1, ..., x_(pp-1)^3, x_pp^2), ..., (x_p1^1, x_p2^1, ..., x_(pp-1)^3, x_pp^(r_pp)),
* ...
* (x_p1^1, x_p2^1, ..., x_(pp-1)^(r_(pp-1)), x_pp^1), (x_p1^1, x_p2^1, ..., x_(pp-1)^r_(pp-1), x_pp^2), ..., (x_p1^1, x_p2^1, ..., x_(pp-1)^r_(pp-1), x_pp^(r_pp)),
* ...
* ...
* (x_p1^(r_p1), x_p2^(r_p2), ..., x_pp^1), (x_p1^(r_p1), x_p2^(r_p2),..., x_pp^2), ..., (x_p1^(r_p1), x_p2^(r_p2),..., x_pp^(r_pp))
*
* where p1 is the first parent node, x_p1 is the value of the first parent node.
* and pp the last parent node, x_pp is the value of the last parent node.
*
* In general, the index j of the j-th parent configuration is equal to:
*
* j = 1 + SUM_{n=1}^{n=pp}[(j_{pp-n+1}-1) PROD_{m=1}^{m=n-1} r_{pp-m}]
*
* where j_{l} is the index of the value of the l-th parent: x_pl = x_pl^{j_{l}}.
*
* @author robert_p
*
*/
public interface RVwithParents {

	

	/**
	 * @return the maximal value k can take i.e. r_i for i the index of the root RV of this
	 * RVwithParents
	 */
	public int getK_max();


	/**
	 * @return the maximal value of the parent configuration.
	 */
	public int getJ_max();


/**
 * This method returns the target RV. The resulting RandomVariable
 * should not be modified.
* returns the target RV (ie. the one with the index i.
* @returns the target RV (ie. the one with the index i.
*/
	public RandomVariable getTargetRV();

/**
* Computes the parent configuration index j for a given parent configuration as specified
* in the passed instantiatedMeasurement parameter.
*
* j = 1 + SUM_{n=1}^{n=pp}[(j_{pp-n+1}-1) PROD_{m=1}^{m=n-1} r_{pp-m}]
*
* where j_{l} is the index of the value of the l-th parent: x_pl = x_pl^{j_{l}}.
* And r_{l} is the number of range values of the node l.
* @param instantiatedMeasurement a JointMeasurement that must include all the parents.
* @param replaceMissingParentsWithNOOBs set true if the operation shall ignore missing parents from the
* instantiatedMeasurement and replace these with the NOOBs index of the affected parents.
* @return parent configuration j or -1 if a parent is not instantiated
* @throws ParentsNotContainedException if parent not in instantiatedMeasurements and replaceMissingParentsWithNOOBs is false
*/
	public int computeParentConfiguration(JointMeasurement instantiatedMeasurement,boolean replaceMissingParentsWithNOOBs)
			throws ParentsNotContainedException;

	/**
	* Computes the parent configuration j. See RVwithParents.computeParentConfiguration(JointMeasurement,boolean)
	* for a definition of the parent configuration index j.
	* @param instantiatedMeasurements a Set of InstantiatedRV that must include all the parents.
	* @param replaceMissingParentsWithNOOBs set true if the operation shall ignore missing parents from the
	* instantiatedMeasurements and replace these with the NOOBs index of the affected parents.
	* @return parent configuration j
	* @throws ParentsNotContainedException if parent not in instantiatedMeasurements and replaceMissingParentsWithNOOBs is false
	*/	
	public int computeParentConfiguration(Set<InstantiatedRV> instantiatedMeasurements, boolean replaceMissingParentsWithNOOBs)
			throws ParentsNotContainedException;
	

	/**
	 * Checks if the Vector obtained from calling getOrderedParents() contains all
	 * RandomVariable parent node elements that are in the passed Set parents_of_node_i,
	 * and no more elements.
	 * @param test_set the Set to check
	 * @return true iff exact match
	 */
	public boolean containsExactlyAllParents(Set<RandomVariable>test_set);


	/**
	 * Returns a unmodifiable SortedSet with the order of the parent nodes. The Set is sorted
	 * according to p1 (first entry) ... pp (last entry), according to the
	 * sorting order of RandomVariable. See Interface description.
	 * This method should be used in a read-only fashion
	 * 
	 * @returns an unmodifiableSortedSet SortedSet in the order of the parent nodes.
	 */
	public SortedSet<RandomVariable> getOrderedParents();
	
	/**
	 * Adds parent to the SortedSet of parents and updates any internal counters to compute the
	 * parent configuration. Care: the parent configurations will change after calling this
	 * method, of course.
	 * @param parent
	 */
	public void addParent(RandomVariable parent);
	
	/**
	 * Removes a parent from the SortedSet of parents and updates any internal counters to compute the
	 * parent configuration. Care: the parent configurations will change after calling this
	 * method, of course.
	 * @param parent
	 */
	public void removeParent(RandomVariable parent);	
	
	/**
	 * Removes all parent from the SortedSet of parents and updates any internal counters to compute the
	 * parent configuration. Care: the parent configurations will change after calling this
	 * method, of course.
	 * @param parent
	 */	
	public void removeAllParents();
}