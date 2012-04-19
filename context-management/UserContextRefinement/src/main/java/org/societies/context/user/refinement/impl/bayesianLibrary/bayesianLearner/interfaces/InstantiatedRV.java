/**
 *
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.RVNotInstantiatedException;

/**
 * @author robert_p
 *
 */
public interface InstantiatedRV extends RandomVariable {

	/**
	 * A InstantiatedRV can only take discrete values, hence this method returns integer values.
	 * These values are consistent with RandomVariable.getNodeRange() of the corresponding RandomVariable returned
	 * with the getRVName() method of InstantiatedRV.
	 * @author robert_p
	 *
	 */
	public int getRVValue() throws RVNotInstantiatedException;

	public RandomVariable getRV();
	
	public boolean isMissingInstantiation();

}
