package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Candidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CandidatesGenerator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.SearchConsumer;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Searcher;
import org.societies.context.user.refinement.impl.tools.LearningPropertyLoader;

/**
 * This is the implementation of a Hill Climbing search algorithm. The main method
 * is the private method processNextGeneration() that is called from the endless loop
 * in the run() method.
 * @author robert_p
 *
 */
public class BasicGreedyHillClimber implements Searcher{
	
	private static double RestartConfiguration_PercentageOfGlobalRandomRestarts = 90.0;
	
	private static Logger log4j = LoggerFactory.getLogger(BasicGreedyHillClimber.class);
	Logger restartModeLog4j = LoggerFactory.getLogger("modeSwitching");

	private Thread thread;
	private boolean toStop = true;

	private Candidate bestCandidate;
	private Candidate bestLocalCandidate;
	private Candidate tempBestCandidate;

	private SearchConsumer searchConsumer;
	private CandidatesGenerator candidates;
	
	private Set<Candidate> databaseOfBest;
	private Set<Candidate> databaseOfRandomRestartedCandidates;

	private int counter;
	private int genCounter;
	private long randomRestartCounter;
	private long numberOfcandidatesSearchedSinceLastBestFound;
	private long randomRestartAttemptedCounter;
	private double averageCacheHitRate;

	private int restartsAttemptedSinceLastBestFound;
	private long timeOfLastSuccessOrTravelStart;
	private int millisForTravellingRestartModePeriod;
	private int millisForLocalRestartModePeriod;

	private int currentRestartState = 1;

	private int localBestRestarts;


	public static final int RestartState_SearchSpaceAroundTempBest = 1;
	public static final int RestartState_TravelAway = 2;
	public static final int RestartState_StopTravelling = 3;

	private static final boolean AllowDuplicates = true;
	private static boolean IAmProcessingADuplicate = false;

	private static int maxLocalRestarts = 0;
	
	
	/**
	 * @param candidatesGenerator
	 * @param bestCandidateMemory
	 * @param startingCandidate
	 * @param searchConsumer
	 */
	public BasicGreedyHillClimber(CandidatesGenerator candidatesGenerator, SearchConsumer searchConsumer) {
		this();
		this.initialise(candidatesGenerator, searchConsumer);
	}

	public BasicGreedyHillClimber() {

		RestartConfiguration_PercentageOfGlobalRandomRestarts = LearningPropertyLoader.getRestartConfiguration_PercentageOfGlobalRandomRestarts();
		millisForLocalRestartModePeriod = (int) Math.round(LearningPropertyLoader.getRestartConfiguration_DurationOfRandomRestartsWithAbsoluteBestInHours()*60*60*1000);
		millisForTravellingRestartModePeriod = (int) Math.round(LearningPropertyLoader.getRestartConfiguration_DurationOfRandomRestartsWithLocalBestInHours()*60*60*1000);
		maxLocalRestarts = LearningPropertyLoader.getRestartConfiguration_maxLocalRestarts();

		this.databaseOfBest = new HashSet<Candidate>();
		this.databaseOfRandomRestartedCandidates = new HashSet<Candidate>();
	}	
	
	public BasicGreedyHillClimber(CandidatesGenerator candidatesGenerator, Candidate startingCandidate, SearchConsumer searchConsumer) {
		this();
		this.initialise(candidatesGenerator, startingCandidate, searchConsumer);
	}
	
	public void initialise(CandidatesGenerator candidatesGenerator, Candidate startingCandidate, 
			SearchConsumer searchConsumer) {
		synchronized (this) {
			if (this.toStop==false) {
				this.stopSearch();
			}
			this.candidates = candidatesGenerator;
			this.databaseOfRandomRestartedCandidates.clear();
			this.bestCandidate = candidatesGenerator.makeEmptyCandidate();
			this.tempBestCandidate = candidatesGenerator.makeEmptyCandidate();
			this.bestCandidate.importFrom(startingCandidate);
			this.bestLocalCandidate = candidatesGenerator.makeEmptyCandidate();
			this.bestLocalCandidate.importFrom(startingCandidate);
			this.searchConsumer = searchConsumer;
			this.numberOfcandidatesSearchedSinceLastBestFound = 0;
			this.randomRestartAttemptedCounter = 0;
			this.randomRestartCounter = 0;
			this.timeOfLastSuccessOrTravelStart = System.currentTimeMillis();
			this.candidates.initialise(startingCandidate);
			this.databaseOfBest.clear();
		}
	}	

	public void initialise(CandidatesGenerator candidatesGenerator, SearchConsumer searchConsumer) {
			Candidate startingCandidate = candidatesGenerator.makeStartingCandidate();
			this.initialise(candidatesGenerator, startingCandidate, searchConsumer);
	}

	public synchronized void startSearch() {
		this.toStop = false;
		this.thread = new Thread(this);
		this.thread.setName("BasicGreedyHillClimber Thread");
		this.thread.start();
	}

	public synchronized void stopSearch() {
		this.toStop = true;
		this.thread.interrupt();
		log4j.info(randomRestartCounter+" random restarts executed. Duplicates:" + (this.randomRestartAttemptedCounter-this.randomRestartCounter));
	}
	
	public Candidate returnBestCandidateSoFar() {
		synchronized (this) {
			return this.bestCandidate;
		}
	}

	public long numberOfcandidatesSearchedSinceLastBestFound() {
		synchronized (this) {
			return this.numberOfcandidatesSearchedSinceLastBestFound;
		}
	}

	public double computeFitnessRatioToBestCandidateSoFar(Candidate otherCandidate) {
		synchronized (this) {
			return this.bestCandidate.computeFitness() / otherCandidate.computeFitness();
		}
	}

	public void run() {
		try {
			this.counter=0;
			while (!this.toStop) {
				try {
					this.processNextGeneration();
					this.counter++;
				} catch (InterruptedException e) {
					System.err.println(this.thread.getName() + " was interrupted. To-stop is: " + this.toStop);
				}
		}
		log4j.debug("Really Stopped at: "+System.currentTimeMillis());

		System.err.println(" Stopped " + this.thread.getName());
		} catch (Throwable t) {
			t.printStackTrace();
		}
	}
	
	/**
	 * This method is called from the endless loop in run(). It takes the this.bestLocalCandidate
	 * and initializes the this.candidates pool from it, that is then used for the current generation.
	 * this.candidates.returnEnumerationOverModifiedCandidates() is then used to go over all
	 * possible candidates which are evaluated in terms of their fitness.
	 * @throws InterruptedException
	 */
	private void processNextGeneration() throws InterruptedException{
	//	System.out.println(this.thread.getName() + " processing "+ this.genCounter +  "  generation. Total analysed " + counter);		
		boolean stuckInLocalMinimum = true;
		this.genCounter++;
		
		this.candidates.initialise(this.bestLocalCandidate);
		
		restartModeLog4j.debug(""+this.bestLocalCandidate);
		
		
		Enumeration<Candidate> enumer = this.candidates.returnEnumerationOverModifiedCandidates();
		while (enumer.hasMoreElements()) {
			this.counter++;
			Candidate newCandidate = (Candidate) enumer.nextElement(); 
			if (newCandidate==null) continue;
			
			double fitness = newCandidate.computeFitness();
			this.numberOfcandidatesSearchedSinceLastBestFound++;
			if (newCandidate.isValid()) {
				double epsilon = 0.5;
				if (this.counter==1 || this.bestCandidate.computeFitness() < fitness + epsilon ||  
						((this.bestCandidate.computeFitness() == fitness) && 
								newCandidate.getSecondaryFitness() > this.bestCandidate.getSecondaryFitness())) {
					boolean foundSignificantBetter =  (this.bestCandidate.computeFitness() < fitness - epsilon || this.counter==1);
					boolean foundAbsoluteBest = (this.bestCandidate.computeFitness() < fitness) || this.counter==1; 
					
					if (!this.databaseOfBest.contains(newCandidate)) {
						Candidate storedCandidate = newCandidate.cloneCandidate();
						this.databaseOfBest.add(storedCandidate);
						synchronized (this) {
							double oldBestscore = this.bestCandidate.computeFitness();
							if (foundAbsoluteBest) {
								
								this.averageCacheHitRate = 0.0;
								this.bestCandidate.importFrom(newCandidate);
								this.tempBestCandidate.importFrom(bestCandidate);
								this.numberOfcandidatesSearchedSinceLastBestFound = 0;
								this.restartsAttemptedSinceLastBestFound = 0;
								this.timeOfLastSuccessOrTravelStart = System.currentTimeMillis();
								this.localBestRestarts =0;
								this.currentRestartState = RestartState_SearchSpaceAroundTempBest;
								if (restartModeLog4j.isDebugEnabled()) 
									restartModeLog4j.debug("Switched Search mode to state="+currentRestartState+ " at "+new Date(System.currentTimeMillis()));
								
								if (IAmProcessingADuplicate){
									restartModeLog4j.error("Found new absolute best while processing a duplicate candidate:\n");
									restartModeLog4j.error(""+bestCandidate);
									restartModeLog4j.error("Fitness="+fitness);
									restartModeLog4j.error("\nExiting now!");
									System.exit(9);
								}
							}
							this.searchConsumer.notifyNewSearchResult(newCandidate, oldBestscore, false, 
									this.counter, this.genCounter, this.randomRestartCounter, 
									foundAbsoluteBest, foundSignificantBetter);
						} 
					} 
				}
				// if we have improved the best local candidate in this generation, then we
				// are not stuck in a local minimum!
				if (this.bestLocalCandidate.computeFitness() < fitness) {
					stuckInLocalMinimum = false;
					this.bestLocalCandidate.importFrom(newCandidate);
				}
			}
			else {
			//	System.out.println("new found candidate has arcs:");
			//	System.err.println("new found candidate has arcs:");
			}
//			System.out.println("Current fitness: " + fitness + " Best Fitness so far: " + this.bestCandidate.computeFitness() + 
//					" for: this candidate:\n" + this.bestCandidate + "\n best local for this: " +
//					this.bestLocalCandidate.computeFitness() + " for : " + this.bestLocalCandidate);

			//	System.out.println(this.thread.getName() + " doing rollback");		
		//	System.out.println(this.thread.getName() + " done rollback");		
		}	
		if (log4j.isDebugEnabled()) log4j.debug(this.thread.getName()+ " best score: " + this.bestCandidate.computeFitness() 
				+ ". Local score: " + this.bestLocalCandidate.computeFitness() + " processed "+ this.genCounter + 
				" generation. Total analysed: " + counter + " restarts: " + this.randomRestartCounter);	
		if (log4j.isDebugEnabled()) 
			if (this.genCounter % 10 == 0) 
				log4j.debug(this.thread.getName() + " Time:  " + new Date(System.currentTimeMillis()) + " Total Memory: " + 
							Runtime.getRuntime().totalMemory()/1024 + " Free Memory: " + Runtime.getRuntime().freeMemory()/1024);	

		// if we are stuck in a local minimum for thw whole generation then we do a random restart.
		if (stuckInLocalMinimum) {	
	//		System.exit(-1);
			this.randomRestartCounter++;
			
			//switch localRestart mode
			if (currentRestartState==RestartState_SearchSpaceAroundTempBest){
				this.bestLocalCandidate.importFrom(this.tempBestCandidate);
				if ( System.currentTimeMillis() > (this.timeOfLastSuccessOrTravelStart + millisForLocalRestartModePeriod)) {
					currentRestartState = RestartState_TravelAway;
					timeOfLastSuccessOrTravelStart = System.currentTimeMillis(); 
					if (restartModeLog4j.isDebugEnabled()){
						restartModeLog4j.debug("Switched Search mode to state="+currentRestartState+ " with cache/hit="+averageCacheHitRate+" at "+new Date(System.currentTimeMillis()));
					}
				}
			}
			else if (currentRestartState==RestartState_StopTravelling){
				this.bestLocalCandidate.importFrom(this.bestCandidate);
				timeOfLastSuccessOrTravelStart = System.currentTimeMillis(); 
				this.averageCacheHitRate = 0.0;
				this.localBestRestarts =0;
				currentRestartState = RestartState_TravelAway;
				if (restartModeLog4j.isDebugEnabled()) restartModeLog4j.debug("Switched Search mode to state="+currentRestartState+ " with cache/hit="+averageCacheHitRate+ " at "+new Date(System.currentTimeMillis()));
			}
			else if (currentRestartState==RestartState_TravelAway){ 
				 
				if (maxLocalRestarts < localBestRestarts) {
					currentRestartState = RestartState_StopTravelling;
					if (restartModeLog4j.isDebugEnabled()) restartModeLog4j.debug("Switched Search mode to state="+currentRestartState+ " with cache/hit="+averageCacheHitRate+ " at "+new Date(System.currentTimeMillis()));
				}
				else if ( System.currentTimeMillis() > (this.timeOfLastSuccessOrTravelStart + millisForTravellingRestartModePeriod)) { //todo change
					currentRestartState = RestartState_SearchSpaceAroundTempBest;
					tempBestCandidate.importFrom(bestLocalCandidate);
					timeOfLastSuccessOrTravelStart = System.currentTimeMillis();
					localBestRestarts++;
					if (restartModeLog4j.isDebugEnabled()) restartModeLog4j.debug("Switched Search mode to state="+currentRestartState+ " with cache/hit="+averageCacheHitRate+ " at "+new Date(System.currentTimeMillis()));
				}
			} 

			
//			if (this.randomForRandomRestartStartingPoint.nextDouble() <= RestartConfiguration_PercentageOfGlobalRandomRestarts/100.0) {

			
			boolean duplicate = false;
			IAmProcessingADuplicate = false;
			
			//ignore already visited duplicates!
			do {
				this.bestLocalCandidate.randomRestart(restartsAttemptedSinceLastBestFound++, this.averageCacheHitRate);
				this.randomRestartAttemptedCounter++;
				duplicate = this.databaseOfRandomRestartedCandidates.contains(this.bestLocalCandidate);
				if (duplicate && AllowDuplicates) IAmProcessingADuplicate = true;
				
					
				// TODO ERROR SEARCHING: REMOVE: START
				if (duplicate){
					boolean allDifferent = true;
					for (Candidate bnc : databaseOfRandomRestartedCandidates){
						if (bestLocalCandidate.toString().equals(bnc.toString())){
							restartModeLog4j.debug("\n\n\nDuplicate found in database\n"+bnc.toString()+" equals to:");
							restartModeLog4j.debug(bestLocalCandidate.toString() + "\n\n\n");

							allDifferent = false;
							break;
						}
					}
					if (allDifferent){
						restartModeLog4j.error("Error: Duplicate=true, but all database entries are different:\n\n"+bestLocalCandidate);
						restartModeLog4j.error("\n\nDatabase:\n");
						for (Candidate bnc : databaseOfRandomRestartedCandidates){
							restartModeLog4j.error(""+bnc);
						}
						System.exit(1);
					}
				}
				// TODO ERROR SEARCHING: REMOVE: END
				
				//Leaky bucket filter to estimate the local cache:hit rate
				if (duplicate) {
					if (restartModeLog4j.isDebugEnabled()) restartModeLog4j.debug(" found Duplicate candidate after restart. numberOfcandidatesSearchedSinceLastBestFound: " + this.numberOfcandidatesSearchedSinceLastBestFound);
					this.averageCacheHitRate = 0.99 * this.averageCacheHitRate + 0.01;
				} else {
					this.averageCacheHitRate = 0.99 * this.averageCacheHitRate;
				}
				
				if (AllowDuplicates) duplicate=false;
				
			} while (duplicate);
			
			this.databaseOfRandomRestartedCandidates.add(this.bestLocalCandidate);
			restartModeLog4j.debug("Adding to DB:\n"+this.bestLocalCandidate+"\nDB size="+databaseOfRandomRestartedCandidates.size()+"\n");
			
			if (log4j.isDebugEnabled())	log4j.debug("Doing the " + this.randomRestartCounter + " 'th random restart");
	//		System.exit(-1);
		} 
	
	//	System.exit(-1); 
	}

	
	/**
	 * @param bestCandidate
	 * @param counter
	 * @param genCounter
	 * @param randomRestartsCounter
	 */
	public static void updateResultFiles(String filename, double oldBestscore, Candidate bestCandidate, int counter, long genCounter, 
			long randomRestartsCounter, boolean append) {
		if (log4j.isDebugEnabled()) log4j.debug("Best candidate: " + bestCandidate + " fitness: " + bestCandidate.computeFitness());
		
		FileWriter fos = null;
		
		
		try {
			fos = new FileWriter(filename, append);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		try {
			//MODIFIED by Maria
			fos.write(new Date(System.currentTimeMillis()).toString()+" ");
			fos.write((genCounter + "/" + counter+"/" + randomRestartsCounter + " Best candidate: " + bestCandidate + "\n fitness: " + 
					+ bestCandidate.computeFitness() + " old best fitness: " + oldBestscore + "\n").toCharArray());
			//END MODIFIED by Maria
			//fos.write((genCounter + "/" + counter+"/" + randomRestartsCounter + " Best candidate: " + bestCandidate + "\n fitness: " + 
			//		+ bestCandidate.computeFitness() + " old best fitness: " + oldBestscore + "\n").toCharArray());
			fos.close();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
	}
}
