package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Candidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CandidatesGenerator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;

/**
 * This class contains the Enumeration generator for producing new Candidate BNs.
 * @author robert_p
 *
 */
public class BayesianNetworkCandidatesGenerator implements CandidatesGenerator{

	private CandidateEnumeration enumeration;
	private BayesianProbabilitiesEstimator bpe;
	private Set<RandomVariable> allnodesset;
	private int maxNumberParentsPerNode;
	//private int maxNumberSonsPerNode;

	
		public BayesianNetworkCandidatesGenerator(BayesianProbabilitiesEstimator bpe, Set<RandomVariable> allnodesset, int maxNumberParentsPerNode) {
			this.enumeration = new CandidateEnumeration(bpe);
			this.bpe = bpe;
			this.maxNumberParentsPerNode = maxNumberParentsPerNode;
			this.allnodesset = new HashSet<RandomVariable>(allnodesset);
			
	}



	public Enumeration returnEnumerationOverModifiedCandidates() {
		this.enumeration.reset();
		return this.enumeration;
	}

	
	public void initialise(Candidate startingCandidate) {
		if (startingCandidate instanceof BayesianNetworkCandidate) {
			this.enumeration.baseBN.importFrom((BayesianNetworkCandidate) startingCandidate);
		}
		else throw new IllegalArgumentException("\n Cannot initialize BayesianNetworkCandidate from " + startingCandidate.getClass());
	}
	
	/**
	 * This inner class is the Enumeration that does the work of returning BayesianNetworkCandidates
	 * by starting with the BayesianNetworkCandidate baseBN that is set during 
	 * initialise(Candidate startingCandidate) in the outer class, and adding and removing and
	 * swapping arcs.
	 * 
	 * @author robert_p
	 *
	 */	
	private class CandidateEnumeration implements Enumeration{

		private RandomVariable[] arc_target_array;
		
		private int state;
		private static final int InitState = 0;
		private static final int AddingArcsState = 1;
		private static final int RemovingArcsState = 2;	
		private static final int SwappingOrderOfArcsState = 3;
		private static final int FinishedState = 4;
		
		private BayesianNetworkCandidate baseBN;
		
		private boolean xx_willstop = false;

		private int target_counter;

		private RandomVariable[] arc_source_array;

		private int source_counter;		
		
		/**
		 *@see java.lang.Object#Object()
		 */
		public CandidateEnumeration(BayesianProbabilitiesEstimator bpe) {
			this.baseBN = new BayesianNetworkCandidate(bpe, maxNumberParentsPerNode);
		}
//		 TODO: fix problem with enumeration returning null if there are no arcs to delete and none to swap!
		public boolean hasMoreElements() {
			return (! (this.state == CandidateEnumeration.FinishedState));
		}
		
		/**
		 * This resets the class before we can return new candidates from a fresh Enumeration.
		 */
		public void reset() {
			this.state = CandidateEnumeration.InitState;
	//		System.out.println(" Beginning with Enumeration. Basis is: \n" + this.baseBN);
		}

		/* (non-Javadoc)
		 * This method starts by returning all candidates with a new added arc, then removed
		 * arcs, then swapped arcs.
		 * @see java.util.Enumeration#nextElement()
		 */
		public Object nextElement() {
			//System.out.println(" getting next element, state is: " + this.state);
			if (this.state == InitState) {
				this.target_counter = 0;
				this.source_counter = 0;
				this.state = AddingArcsState;
			} else {
				this.baseBN.rollbackLastOperation();
			}
			if (this.state == AddingArcsState) {
				BayesianNetworkCandidate nextcandidate = this.returnNextAddedArc();
				if (nextcandidate != null) {
					return nextcandidate;
				}
				else {
//					if (1.0/this.baseBN.getSecondaryFitness() > 4.0) { // test for more that 4 arcs
//						System.out.println("\n\n\n\n\n Will stop soon with getSecondaryFitness "+ 
//								this.baseBN.getSecondaryFitness() + " and with score: "  + this.baseBN.score + "\n" + this.baseBN);
//						this.xx_willstop = true;
//					//	System.exit(0);
//					}
					this.target_counter = -1;
					this.source_counter = -1;
					this.state = RemovingArcsState;
				}
			}
			if (this.state == RemovingArcsState) {
	//			System.out.println(" 22 getting next element, state is: " + this.state + " "+ this.target_counter + " " + this.source_counter);

				BayesianNetworkCandidate nextcandidate = this.returnNextRemovedArc();
				if (nextcandidate != null) {
					return nextcandidate;
				}
				else {
					this.target_counter = -1;
					this.source_counter = -1;
//					if (this.xx_willstop) System.exit(-1);
					this.state = SwappingOrderOfArcsState;
				}
			}
			if (this.state == SwappingOrderOfArcsState) {
	//			System.out.println(" 33 getting next element, state is: " + this.state + " "+ this.target_counter + " " + this.source_counter);
				if (!this.hasMoreArcsToSwap()) {
					this.state = FinishedState;
	//				System.out.println(" 44 getting next element, state is: " + this.state + " "+ this.target_counter + " " + this.source_counter);
				}
				else {
					return this.returnNextSwappedArc();
				}
			}		
			return null;
		}
		
		private BayesianNetworkCandidate returnNextAddedArc() {
			// TODO try and be more efficient when detecting that arc added to itself
			// TODO try and be more efficient when detecting that arc already present
			if (!computeNext_RV_and_Arc_to_Add()) return null;
			
			int current_size_of_target_node_parent_set = 
				this.baseBN.getBNSegment(this.arc_target_array[this.target_counter]).
						getOrderedParents().size();		
			
			if (current_size_of_target_node_parent_set >= maxNumberParentsPerNode) {
				this.source_counter++;
				return this.baseBN;
			}
			
			/*if (this.baseBN.getBNSegment(this.arc_source_array[this.source_counter]).
					getTargetRV().doesNotAllowOutgoingArrows()) return this.baseBN;

			if (this.baseBN.getBNSegment(this.arc_target_array[this.target_counter]).
				getTargetRV().allowsOnlyOutgoingArrows()) return this.baseBN;*/
			
			//if (current_size_of_target_node_parent_set >= maxNumberParentsPerNode) return this.baseBN;
			
			
		//		System.out.println("Adding an arc");
			return this.baseBN.addArc(this.arc_target_array[this.target_counter], this.arc_source_array[this.source_counter++]);
			
		}

		private BayesianNetworkCandidate returnNextRemovedArc() {
			if (!computeNext_RV_and_Arc_to_Remove_or_Swap()) return null;
			else {
		//		System.out.println("Removing an arc");
				return this.baseBN.removeArc(this.arc_target_array[this.target_counter], 
						this.arc_source_array[this.source_counter]);
			}
		}	
		
		private BayesianNetworkCandidate returnNextSwappedArc() {
		//	System.out.println("Swapping an arc");
			
			/*ADDED by Maria:
			 * 
			 *At the outset, the source node is a parent of the target node. In this method, 
			 *the other combination should be checked in order to see if the score is bigger
			 *or not.
			 *
			 *The problem is that, as we would like to limit the number of parents, first 
			 *we have to check if the source  node has already a complete parent set, meaning,
			 *the maximum number of parents possible. If it has it, the target node cannot become
			 *a parent. This condition is checked by the next part of the code. If it is not possible
			 *to swap the arc, it will return that base BN. 
			 * 
			 * */
			//ADDED by Maria:
			int current_size_of_source_node_parent_set = 
				this.baseBN.getBNSegment(this.arc_source_array[this.source_counter]).
						getOrderedParents().size();		
			
			
			if (current_size_of_source_node_parent_set >= maxNumberParentsPerNode) return this.baseBN;
			//END "added by Maria"
	
			/*if (this.baseBN.getBNSegment(this.arc_source_array[this.source_counter]).
					getTargetRV().allowsOnlyOutgoingArrows()) return this.baseBN;

			if (this.baseBN.getBNSegment(this.arc_target_array[this.target_counter]).
				getTargetRV().doesNotAllowOutgoingArrows()) return this.baseBN;*/

			return this.baseBN.swapArc(this.arc_target_array[this.target_counter], 
					this.arc_source_array[this.source_counter]);
		}		
	
		private boolean computeNext_RV_and_Arc_to_Add() {
			// TODO merge with computeNext_RV_and_Arc_to_Remove_or_Swap
			if (this.target_counter==0) {
				this.arc_target_array = (RandomVariable[]) this.baseBN.getSegments().keySet().toArray(new RandomVariable[0]);
				this.arc_source_array = (RandomVariable[]) this.baseBN.getSegments().keySet().toArray(new RandomVariable[0]);
			}		
			if (this.source_counter==this.arc_source_array.length) {
				this.source_counter = 0;
				this.target_counter++;
			}
			if (this.target_counter==this.arc_target_array.length) {
	//			System.out.println("In computeNext_RV_and_Arc_to_Add. Finished. this.target_counter: " + 
	//					this.target_counter +  " this.arc_target_array.length " + this.arc_target_array.length + " this.source_counter " + this.source_counter);

				return false;
			}
	//		System.out.println("In computeNext_RV_and_Arc_to_Add. this.target_counter: " + 
	//				this.target_counter +  " this.arc_target_array.length " + this.arc_target_array.length + " this.source_counter " + this.source_counter);
			return true;
		}				
		
		private boolean computeNext_RV_and_Arc_to_Remove_or_Swap() {
			if (this.target_counter<0) {
				this.arc_target_array = (RandomVariable[]) this.baseBN.getSegments().keySet().toArray(new RandomVariable[0]);
				this.target_counter++;
			}	
			do {
				source_counter++;
				
				if (source_counter==0) {
					BNSegment bns = (BNSegment) this.baseBN.getSegments().get(this.arc_target_array[target_counter]);
					this.arc_source_array = (RandomVariable[]) bns.getOrderedParents().toArray(new RandomVariable[0]);	
				}
//				System.out.println("In computeNext_RV_and_Arc_to_Remove_or_Swap. In do while loop . this.target_counter: " + 
//									this.target_counter +  " this.arc_target_array.length " + 
//									this.arc_target_array.length + " this.source_counter " + this.source_counter + 
//									" arc_source_array length: " + this.arc_source_array.length );
				if (this.source_counter==this.arc_source_array.length) {
					this.target_counter++;
					source_counter = -1;
				}
				if (this.target_counter==this.arc_target_array.length) {
//								System.out.println("In computeNext_RV_and_Arc_to_Remove_or_Swap. Finished . this.target_counter: " + 
//										this.target_counter +  " this.arc_target_array.length " + this.arc_target_array.length + " this.source_counter " + this.source_counter);
					return false;
				}			
			}
			while (source_counter == -1); 				
			
	//		System.out.println("In computeNext_RV_and_Arc_to_Remove_or_Swap. this.target_counter: " + 
	//				this.target_counter +  " this.arc_target_array.length " + this.arc_target_array.length + " this.source_counter " + this.source_counter);
			return true;
		}		
		
		
		private boolean hasMoreArcsToSwap() {
			return (this.computeNext_RV_and_Arc_to_Remove_or_Swap());
		}		
	} 

	public Candidate makeStartingCandidate() {
		if (this.enumeration.baseBN != null) return this.enumeration.baseBN;
		return new BayesianNetworkCandidate(this.bpe, this.allnodesset, this.maxNumberParentsPerNode);
	}

	public Candidate makeEmptyCandidate() {
		return new BayesianNetworkCandidate(this.bpe, this.maxNumberParentsPerNode);
	}
	

}
