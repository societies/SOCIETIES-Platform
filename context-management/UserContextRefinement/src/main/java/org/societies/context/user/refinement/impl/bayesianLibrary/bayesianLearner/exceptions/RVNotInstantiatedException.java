/**
 * 
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions;

/**
 * @author robert_p
 *
 */
public class RVNotInstantiatedException extends Exception {

	/**
	 * 
	 */
	public RVNotInstantiatedException() {
		super();
	}

	/**
	 * @param arg0
	 */
	public RVNotInstantiatedException(String arg0) {
		super(arg0);
	}

	/**
	 * @param arg0
	 * @param arg1
	 */
	public RVNotInstantiatedException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	/**
	 * @param arg0
	 */
	public RVNotInstantiatedException(Throwable arg0) {
		super(arg0);
	}

}
