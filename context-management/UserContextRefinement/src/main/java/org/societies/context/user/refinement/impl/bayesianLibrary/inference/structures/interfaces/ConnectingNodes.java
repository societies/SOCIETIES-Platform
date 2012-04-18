package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces;

import java.io.Serializable;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;


/**
 * @author fran_ko
 *
 */
public interface ConnectingNodes extends Comparable, Serializable {

	public Node getBorder1();
	public Node getBorder2();
}
