package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueIndexNotInNodeRangeException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeValueTextNotInNodeRangeException;

/**
 * This Interface specifies how a discrete random variable should behave. It has
 * a range of possible values and maintains the node values and their names. The
 * RV also has a name.
 * In addition, there are three controls that specify the order the random variable
 * may take in a causal network:
 * 	- AOOG: Allow Only OutGoing arcs from this RV
 *  - DNAOG: Don't Allow OutGoing arcs from this RV
 *  - _HIERARCHYx where x is an integer >= zero. A node can have an arc to another iff it has lower or equal hierarchy
 * 
 * @author robert_p
 * 
 */
public interface RandomVariable extends Comparable<RandomVariable> {

	public int[] getNodeRange();

	/**
	 * Reverse mapping of getNodeRange()
	 * 
	 * @param nodeValue
	 *            (must be zero or positive)
	 * @return the position in the NodeRange (from 0 to length-1) that
	 *         corresponds to the node value passed
	 * @throws NodeValueIndexNotInNodeRangeException
	 */
	public int getNodeRangePositionFromValue(int nodeValue)
			throws NodeValueIndexNotInNodeRangeException;

	public String getNodeValueText(int nodeValue)
			throws NodeValueIndexNotInNodeRangeException;

	/**
	 * We assume a unqiue name. Used in compareTo, hashCode and equals. Only the
	 * name counts - not the value!
	 * 
	 * @return the unique name of the Node
	 */
	public String getName();

	public int getNodeValueFromText(String nodeValueText)
			throws NodeValueTextNotInNodeRangeException;

	public String toStringLong();

	public boolean doesNotAllowOutgoingArrows();
	
	public int getHierarchy() ;

	public boolean allowsOnlyOutgoingArrows();

	public final String DoesNotAllowOutgoing = "DNAOG";

	public final String AllowsOnlyOutgoing = "AOOG";

	public final String HierarchyIndicator = "_HIERARCHY";

	/**
	 * Return the non observed value (must be negative and must not be zero)
	 * 
	 * @return the non observed value
	 */
	// public int getNOBSValue();
}
