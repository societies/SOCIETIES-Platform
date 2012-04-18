package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;


import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesLearner;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Candidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.SearchConsumer;

/**
 * Main class.
 */
public class BayesLearnerImpl implements SearchConsumer, BayesLearner{
	
	private static Logger logger = LoggerFactory.getLogger(BayesLearnerImpl.class);

	private BayesianProbabilitiesEstimator learningEngine;

	private BayesianNetworkCandidate bestCandidate;

	public BayesLearnerImpl() {
		this.learningEngine = new SimpleCountingLearningEngine();
	}
	public BayesLearnerImpl(BayesianProbabilitiesEstimator alt) {
		this.learningEngine = alt;
	}

	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.impl.BayesLearner#runLearning(int, java.lang.String)
	 */
	public Map<RandomVariable, ConditionalProbabilityTable> runLearning(int millisecs, String data){
		Map<String, RandomVariable> rvMap = new HashMap<String, RandomVariable>();
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement.computeFromData(rvMap, data);

		Set<RandomVariable>allnodesset = new HashSet<RandomVariable>();
		allnodesset.clear();
		
		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		int defaultmaxNumberParentsPerNode = allnodesset.size();
		
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			logger.debug("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset, defaultmaxNumberParentsPerNode);
		
		return runLearning(millisecs, starterBN, defaultmaxNumberParentsPerNode);

	}
	
	/* (non-Javadoc)
	 * @see de.kl.kn.bayesianLibrary.impl.BayesLearner#runLearning(int, de.kl.kn.searchLibrary.greedySearch.interfaces.Candidate)
	 */
	public Map<RandomVariable, ConditionalProbabilityTable> runLearning(int millisecs, Candidate startingPoint, int maxNumberParentsPerNode){
		
		if (startingPoint==null || !(startingPoint instanceof BayesianNetworkCandidate)) return null;
		
		BayesianNetworkCandidatesGenerator bcg = new BayesianNetworkCandidatesGenerator(this.learningEngine,
				((BayesianNetworkCandidate)startingPoint).getNodes(), maxNumberParentsPerNode);
		
		bcg.initialise(startingPoint);
		BasicGreedyHillClimber bghc = new BasicGreedyHillClimber(bcg, this);

		bghc.startSearch();

		try {
			Thread.sleep(millisecs);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		bghc.stopSearch();
		logger.debug("Stop! at: "+System.currentTimeMillis());

		if (this.bestCandidate != null) {
			Map<?, ?> segments = this.bestCandidate.getSegments();
			Map<RandomVariable, ConditionalProbabilityTable> finalNW = new HashMap<RandomVariable, ConditionalProbabilityTable>();
			Iterator<?> it = segments.keySet().iterator();
			while (it.hasNext()) {
				RandomVariable rv = (RandomVariable) it.next();
				BNSegment seg = (BNSegment) segments.get(rv);
				CountTable ct = this.learningEngine.getCounts(rv, seg
						.getOrderedParents());
				logger.debug(""+ct);

				try {
					ConditionalProbabilityTable cpt = this.learningEngine
							.getCPT(rv, seg.getOrderedParents(),
									this.learningEngine.getUniformPriors(
											this.bestCandidate.getN_equiv(),
											rv, seg.getOrderedParents()));
					finalNW.put(rv, cpt);
					
				} catch (CountsNotCompleteException e) {
					e.printStackTrace();
				}

			}
			
			return finalNW;
		}
		return null;
	}
	
	public Map<RandomVariable, ConditionalProbabilityTable> runLearning(int millisecs, Candidate startingPoint) {
		return runLearning(millisecs, startingPoint,startingPoint.candidateSize());
	}

	public void notifyNewSearchResult(Candidate newCandidate,
			double oldBestscore, boolean stoppedExternally, int counter,
			long genCounter, long randomRestartsCounter, boolean isAbsoluteBest,
			boolean foundSignificantlyBetter) {
		String filename = null;

		if (isAbsoluteBest) {
			filename = "."+java.io.File.separator+"bestfound.txt";
			BasicGreedyHillClimber.updateResultFiles(filename, oldBestscore,
					newCandidate, counter, genCounter, randomRestartsCounter,
					true);
			this.bestCandidate = (BayesianNetworkCandidate) newCandidate
					.cloneCandidate();
		}
		filename = "."+java.io.File.separator+"closefound.txt";
		BasicGreedyHillClimber.updateResultFiles(filename, oldBestscore,
				newCandidate, counter, genCounter, randomRestartsCounter,
				!foundSignificantlyBetter);

	}
	
	public BayesianProbabilitiesEstimator getLearningEngine() {
		return this.learningEngine;
	}
	
	public Map<RandomVariable, ConditionalProbabilityTable> learnParametersOnly(BayesianNetworkCandidate bestCandidate) {

		logger.info("BestCandidate = "+bestCandidate);
		if (bestCandidate == null) return null;

		double logScore = bestCandidate.computeFitness();
		logger.info("fitness: "+logScore);
		
		Map<RandomVariable, ConditionalProbabilityTable>finalNW = null;
		
		Map<RandomVariable, BNSegment> segments = bestCandidate.getSegments();
		finalNW = new HashMap<RandomVariable, ConditionalProbabilityTable>();
		Iterator<RandomVariable> it = segments.keySet().iterator();
		logger.debug("Is RV iterator non-empty (should be the case)? "+it.hasNext()); 
		while (it.hasNext()) {
			RandomVariable rv = it.next();
			BNSegment seg = (BNSegment) segments.get(rv);
			CountTable ct = this.learningEngine.getCounts(rv, seg
					.getOrderedParents());
			logger.debug(""+ct);

			try {
				ConditionalProbabilityTable cpt = this.learningEngine
				.getCPT(rv, seg.getOrderedParents(),
						this.learningEngine.getUniformPriors(
								bestCandidate.getN_equiv(),
								rv, seg.getOrderedParents()));
				finalNW.put(rv, cpt);

			} catch (CountsNotCompleteException e) {
				e.printStackTrace();
			}

		}
		
		return finalNW;
	}


}