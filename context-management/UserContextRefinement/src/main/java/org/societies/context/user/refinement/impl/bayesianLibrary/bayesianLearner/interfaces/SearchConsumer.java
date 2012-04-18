/**
 * 
 */
package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;

/**
 * This Interface specifies how search results are passed back to the SearchConsumer.
 * The Interface has one callback method notifyNewSearchResult().
 * @author robert_p
 *
 */
public interface SearchConsumer {
	
	/**
	 * This method is called when a new Candidate is found. It is called in two cases:
	 * 1) if a new absolute best scoring candidate is found. 2) if a new candidate is found
	 * that scores (almost) as well as the currect best candidate found - i.e. equivalent search
	 * results.
	 * 
	 * @param newCandidate the newly found candidate
	 * @param oldBestscore the previous best score
	 * @param stoppedExternally true iff this callback is the result of stopping the Searcher
	 * @param counter the number of candidates searched (search space explored so far)
	 * @param genCounter the number of greedy search generations done
	 * @param randomRestartCounter the number of random restarts done
	 * @param isAbsoluteBest true iff newCandidate is the absolute best so far
	 * @param foundSignificantBetter true iff isAbsoluteBest and newCandidate is significantly better than
	 * last absolutely best one
	 */
	public void notifyNewSearchResult(Candidate newCandidate, 
			double oldBestscore, boolean stoppedExternally, int counter, 
			long genCounter, long randomRestartCounter, boolean isAbsoluteBest,
			boolean foundSignificantBetter);

}
