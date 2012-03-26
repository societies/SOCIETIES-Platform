package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces;

import java.io.Serializable;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;


/**
 * @author fran_ko
 *
 */
public interface HasProbabilityTable extends Serializable{
	public ProbabilityDistribution getProbTable();

	public String getName();
	
	public Node[] getParticipants();
	
	public void setProbDistribution(Probability[] a);	

}
