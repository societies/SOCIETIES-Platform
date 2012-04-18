/**
 * 
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Map;

/**
 * Represents a timestamped measurement of a range of random variables.
 * The random variables comprising the elements of the Joint Measurement
 * are represented as a Set of type InstantiatedRV.
 * @author robert_p
 *
 */
public interface JointMeasurement{

		/**
		 * Returns the time instance at which the measurement was made.
		 * To be used for windowing, note: we treat measurements as IID
		 * @return TimeStamp of measurement
		 */
		public TimeStamp getTimeStamp();
		
		/**
		 * Used to access the joint measurement.
		 * The Map is used for quick access.
		 * @return the Map mapping each RandomVariable in the JointMeasurement to instantiated RV
		 * of this Joint Measurement 
		 */
		public Map<RandomVariable, InstantiatedRV> getInstantiatedRV();
}
