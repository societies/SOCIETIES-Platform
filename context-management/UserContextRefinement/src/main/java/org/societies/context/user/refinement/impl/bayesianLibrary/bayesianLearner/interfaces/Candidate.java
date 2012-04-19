package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;
/**
 * This interface specifies a Candidate for greedy search. It must support making a random restart from its
 * current state, computing its fitness, importing its state (deep copy) from another Candidate, validating itself,
 * and rolling back from a last change-operation. The actual implementations will have to provide 
 * methods to make actual domains-specific changes to the candidate.
 * @author robert_p
 *
 */
public interface Candidate {

	/**
	 * Compute the fitness of the Candidate. Implementations are advised to cache the fitness and
	 * only re-compute when the Candidate is changed in between calls. It is OK for implementations 
	 * to need to re-compute their score in the next call to computeFitness() after a rollbackLastOperation() call.
	 * @return the fitness of the Candidate
	 */
	public double computeFitness();

	public void importFrom(Candidate newCandidate);
	
	/**
	 * Rollback the last operation that was done using domain specific operations provided vy
	 * the implementation. It is OK for implementations to need to re-compute their score in
	 * the next call to computeFitness() after a rollback.
	 * 
	 */
	public void rollbackLastOperation();

	/**
	 * @return
	 */
	public double getSecondaryFitness();

	/**
	 * Check if the candidate is a valid one
	 * @return true iff the candidate is a valid one
	 */
	public boolean isValid();
	
	/**
	 * The size of a network candidate, e.g. the number of nodes in a Bayesian network
	 * 
	 * @param cand
	 * @return
	 */
	public int candidateSize();

	/**
	 * This method can be used to clone a Candidate, for example when putting it in a store (e.g. Set) of found
	 * candidates.
	 * @return
	 */
	public Candidate cloneCandidate();

	/**
	 * Make a random restart. The candidate should be a valid one after this method is called.
	 */
	public void randomRestart();

	/**
	 * Make a random restart. The candidate should be a valid one after this method is called.
	 * Take into account the history, i.e. the number of unsuccessful last restarts
	 * 
	 * @param restartsAttemptedSinceLastBestFound Number of BN candidates searched and scored since the current optimum was found
	 * @param averageCacheHitRate is the recent rate with which duplicates where found after the restarts
	 */
	public void randomRestart(long restartsAttemptedSinceLastBestFound, double averageCacheHitRate);
}
