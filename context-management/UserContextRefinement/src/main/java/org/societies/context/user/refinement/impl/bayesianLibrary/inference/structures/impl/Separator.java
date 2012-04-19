package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;

/**
 * @author fran_ko
 *
 */
public class Separator implements ConnectingNodes, HasProbabilityTable {

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getSource()
	 */

	private Clique source;
	private Clique target;
	private Node[] label;
	private ProbabilityDistribution prob;
	private static Logger logger = LoggerFactory.getLogger(Separator.class);
	
	
	public Separator(Clique one, Clique two){
		source = one;
		target = two;
		
		calculateLabel();
	}
	
	/**
	 * 
	 */
	private void calculateLabel() {
		
		ArrayList<Node> targetNodes = new ArrayList<Node>();
		ArrayList<Node> sourceNodes = new ArrayList<Node>();
		
		for (int i=0;i<target.getParticipants().length;i++) targetNodes.add(target.getParticipants()[i]);
		for (int i=0;i<source.getParticipants().length;i++) sourceNodes.add(source.getParticipants()[i]);
		
		targetNodes.retainAll(sourceNodes);
		
		label = (Node[])targetNodes.toArray(new Node[0]);
		
	}
	
	public Node[] getLabel(){
		return label;
	}

	public Node getBorder1() {
		return source;
	}

	/* (non-Javadoc)
	 * @see structures.ConnectingNodes#getTarget()
	 */
	public Node getBorder2() {
		return target;
	}


	public String toString(){
		String labelString = "";
		for (int i=0;i<label.length;i++) labelString+=label[i];
		
		return labelString + ":\t"+source +" ---- "+ target + "\n";
	}

	/* (non-Javadoc)
	 * @see structures.ProbabilityTable#getProbTable()
	 */
	public ProbabilityDistribution getProbTable() {
		return prob;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		return toString().compareTo(((Separator)arg0).toString());
	}

	/* (non-Javadoc)
	 * @see structures.HasProbabilityTable#getName()
	 */
	public String getName() {
		return this.toString();
	}

	/* (non-Javadoc)
	 * @see structures.HasProbabilityTable#getParticipants()
	 */
	public Node[] getParticipants() {
		return getLabel();
	}

	/* (non-Javadoc)
	 * @see structures.HasProbabilityTable#setProbDistribution(structures.Probability[])
	 */
	public void setProbDistribution(Probability[] a) {
		int testLength = 1;
		for(int i=0; i<getParticipants().length;i++){
			testLength*=getParticipants()[i].countStates();
		}
		
		if (a!=null && 
				a.length == testLength &&
					a[0].getStates().length == (getParticipants().length))
			prob = new ProbabilityDistribution(this, a);
		else
		{ 
			logger .error("Node: setProbDistribution(Probability[]): Probability Table does not fit the structure of the network");
		
//			System.out.println(twoDimensionalFixedOrder.length);
		}
	}
///////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////Added by Pablo//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////
	
	public void setSource(Clique s){
		this.source=s;
		calculateLabel();
	}
	
	public void setTarget(Clique t){
		this.source=t;
		calculateLabel();
	}
	
}
