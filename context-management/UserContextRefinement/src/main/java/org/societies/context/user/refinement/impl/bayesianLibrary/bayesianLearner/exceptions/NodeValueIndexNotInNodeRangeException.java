package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions;
/**
 * @author robert_p
 *
 */
public class NodeValueIndexNotInNodeRangeException extends Exception {

	public NodeValueIndexNotInNodeRangeException(String msg) {
		super(msg);
	}

	public NodeValueIndexNotInNodeRangeException(RangeValueNotApplicableException e) {
		super(e);
	}

}