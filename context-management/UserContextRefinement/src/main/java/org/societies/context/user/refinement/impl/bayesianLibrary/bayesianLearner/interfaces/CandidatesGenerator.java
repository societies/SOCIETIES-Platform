package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

import java.util.Enumeration;

/**
 * This interface specifies how Candidates for greedy search are generated. The interface
 * provides four functionalities: making a starting candidate, making an empty candidate, 
 * initialising with a certain candidate, and returning an
 * Enumeration over the current generation.
 * @author robert_p
 *
 */
public interface CandidatesGenerator {

	/**
	 * This method prepares an Enumeration that can be used to go through all
	 * candidates of this generation. Care: the Enumeration may contain null elements.
	 * @return an Enumeration over all possible modifications in this generation
	 */
	public Enumeration<Candidate> returnEnumerationOverModifiedCandidates();


	/**
	 * Initialise the CandidatesGenerator with a starting Candidate - called at each new generation; e.g. with a call like:
	 * this.candidatesGenerator.initialise(this.bestLocalCandidate);
	 * @param startingCandidate
	 */
	public void initialise(Candidate startingCandidate);


	/**
	 * @return a suitable starting candidate
	 */
	public Candidate makeStartingCandidate();


	/**
	 * @return an empty candidate instance
	 */
	public Candidate makeEmptyCandidate();
}
