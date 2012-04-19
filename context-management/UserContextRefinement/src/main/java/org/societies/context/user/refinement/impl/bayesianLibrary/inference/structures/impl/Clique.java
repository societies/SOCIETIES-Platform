package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;


/**
 * @author fran_ko
 *
 */
public class Clique extends Node {

	Node[] representedNodes;
	boolean marked = false;
	
	public Clique(Node[] nodes) {
		
		representedNodes = sort(nodes);
		
		String name = "";
		ArrayList states = new ArrayList();
		
		for(int i=0; i<nodes.length;i++){
			Node n = nodes[i];
			name += n.getName();
			for(int j=0; j<n.getStates().length;j++){
				String s = n.getStates()[j];
				states.add(s);
			}
		}
		
		super.name = name;
		super.states = (String[]) states.toArray(new String[0]);
		
	}
	
	protected Node[] sort(Node[] unsorted){
		
		List result = Arrays.asList(unsorted);
		Collections.sort(result);
		return (Node[])result.toArray(new Node[0]);
	}
	
	public Node[] getParticipants(){
//		System.out.println("HIER ist CLIQUE-getParticipants()");
		return representedNodes;
	}

	/**
	 * @param b
	 */
	public void mark(boolean b) {
		marked = b;
	}
	
	public boolean isMarked(){
		return marked;
	}

	/**
	 * 
	 */
	public void collectEvidence(Clique caller) {
		mark(true);
		
		Node[] neighbours = getNeighbours();
		for (int i=0;i<neighbours.length;i++){
			if (neighbours[i] instanceof Clique && !((Clique)neighbours[i]).isMarked()) ((Clique)neighbours[i]).collectEvidence(this);
			/* Testing
 			else System.out.println("\n\n\nNICHT!!!!!!! Clique?" + (neighbours[i] instanceof Clique));
			/**/
 		}
		if (caller!=null) passMessageTo(caller);
		
	}

	/**
	 * 5.3.1 Single Message Pass
	 * 		- Projection
	 * 		- Absorption
	 * 
	 * @param caller
	 */
	private void passMessageTo(Clique target) {
		/*
		 * Find separator:
		 * link := R in Huang/Darwiche, S.19f
		 */
		Separator link = null;
		for (int i=0;i<getUndirectedEdges().size();i++){
			link = (Separator)getUndirectedEdges().get(i);
			if (link.getBorder1().equals(target) || link.getBorder2().equals(target)) break;
		}
		
		/*
		 * Projection:
		 */
			
		Probability[] linkTable = link.getProbTable().getProbabilities();
		Probability[] link_alt_probs = new Probability[linkTable.length];
		for(int y=0;y<linkTable.length;y++){
			link_alt_probs[y] = (Probability)linkTable[y].clone();
		}
		
		ProbabilityDistribution ownTable = getProbTable();
		
		for (int i=0;i<linkTable.length;i++){
			
			/* Testing 
			if ((eu.ist.daidalos.pervasive.bayesianLibrary.solving.JunctionTree.messageNumber)==4)System.out.println(ownTable);
			/**/
			
			int[] touched = ownTable.fitsIndex(link.getParticipants(), linkTable[i].getStates());
			
			double sumFromCaller = 0;
			for (int j=0;j<touched.length;j++){
				sumFromCaller += ownTable.getProbabilities()[touched[j]].getProbability();
			}
			
			/* Testing
			System.out.println("Separator "+link.getName()+" OLD:" + linkTable[i].getProbability() + " and NEW: "+sumFromCaller);
			/**/
			linkTable[i].setProbability(sumFromCaller);
		}
		
		
		/*
		 * Absorption:
		 * 
		 * Potential_target = Potential_target * (linkTable/linkTable_alt)
		 */
		
		ProbabilityDistribution targetTable = target.getProbTable();
		Probability[] targetProbs = targetTable.getProbabilities();
		
		for (int i=0;i<linkTable.length;i++){
			int[] belongsToTarget = targetTable.fitsIndex(link.getParticipants(), linkTable[i].getStates());
			
			double quotient = (link_alt_probs[i].getProbability()==0)?0:linkTable[i].getProbability() / link_alt_probs[i].getProbability();

			for (int j=0;j<belongsToTarget.length;j++){
				targetProbs[belongsToTarget[j]].multiplyProbability(quotient);
			}
			/*Testing
			if ((eu.ist.daidalos.pervasive.bayesianLibrary.solving.JunctionTree.messageNumber)==9)
				System.out.println("Target-Table nach Message: "+eu.ist.daidalos.pervasive.bayesianLibrary.solving.JunctionTree.messageNumber);
			if ((eu.ist.daidalos.pervasive.bayesianLibrary.solving.JunctionTree.messageNumber)==9)		System.out.println(targetTable);
			/**/
		}
	}

	/**
	 * 
	 */
	public void distributeEvidence() {
		mark(true);
		
		Node[] neighbours = getNeighbours();
		for (int i=0;i<neighbours.length;i++){
			if (neighbours[i] instanceof Clique && !((Clique)neighbours[i]).isMarked()) passMessageTo((Clique)neighbours[i]);
		}
		
		for (int i=0;i<neighbours.length;i++){
			if (neighbours[i] instanceof Clique && !((Clique)neighbours[i]).isMarked()) ((Clique)neighbours[i]).distributeEvidence();
		}
	}
	
	
}
