package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces;
/**
 * This Interface specifies which functions a greedy Searcher must provide. Note that
 * the Searcher is Runnable so it will have its own Thread once startSearch() is called.
 * The searcher communicates with the SearchConsumer via a callback method notifyNewSearchResult() when new candidates
 * are found.
 * 
 * Implemementations can call  the appropriate initialise() method automatically in their constructor!
 * 
 * Searcher distinguishes between the best found Candidate and Candidates that have a score
 * (almost) as good as the best one, in order to find equivalent search results: see SearchConsumer.
 * @author robert_p
 *
 */
public interface Searcher extends Runnable{
	
	/**
	 * Initialises the Searcher, but does not start it.
	 * Implemementations can call this method automatically in their constructor!
	 * @param candidatesGenerator the CandidatesGenerator used to search in.
	 * @param searchConsumer the search consumer to call back with search results
	 */
	public void initialise(CandidatesGenerator candidatesGenerator, SearchConsumer searchConsumer);
	
	/**
	 * Initialises the Searcher, but does not start it.
	 * Implemementations can call this method automatically in their constructor!
	 * @param candidatesGenerator the CandidatesGenerator to search in.
	 * @param startingCandidate the starting candidate
	 * @param searchConsumer the search consumer to call back with search results
	 */
	public void initialise(CandidatesGenerator candidatesGenerator, Candidate startingCandidate, SearchConsumer searchConsumer);
		
	/**
	 * Starts the search
	 */
	public void startSearch();
	
	/**
	 * Stops the search. This will force a call to the callback method notifyNewSearchResult() with stoppedExternally true.
	 */
	public void stopSearch();
	
	/**
	 * Used to access the best Candidate found so far indepedently from the callback
	 * @return the best Candidate found so far
	 */
	public Candidate returnBestCandidateSoFar();
	
	/**
	 * @return the number of candidates searched for since the last best Candidate was found
	 */
	public long numberOfcandidatesSearchedSinceLastBestFound();
	
	/**
	 * Tests the fitness of the otherCandidate against the current best one
	 * @param otherCandidate to be evaluated
	 * @return the quotient (score best candidate / score otherCandidate)
	 */
	public double computeFitnessRatioToBestCandidateSoFar(Candidate otherCandidate);
	
}