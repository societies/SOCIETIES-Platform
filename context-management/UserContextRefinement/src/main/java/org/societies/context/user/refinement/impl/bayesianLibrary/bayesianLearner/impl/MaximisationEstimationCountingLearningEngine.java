package org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl;

import java.util.SortedSet;

import org.societies.context.user.refinement.impl.BayesEngine;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.NodeNotAvailableException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.JointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.PriorTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;



//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
/**
 * This class implements a simple counting learning engine. See BayesianProbabilitiesEstimator
 * Interface for details.
 * @author robert_p
 *
 */
public class MaximisationEstimationCountingLearningEngine implements BayesianProbabilitiesEstimator{
// TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	 TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	 TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	 TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	 TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
//	 TODO Baustelle!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
	
	private BayesianNetworkCandidate bnc;
	
	private SimpleCountingLearningEngine hardCountsLearningEngine;
	
	

	public MaximisationEstimationCountingLearningEngine(BayesEngine bi_eng) {
		super();
		// TODO Auto-generated constructor stub
	}


	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#resetTables()
	 */
	public void resetTables() {
		this.hardCountsLearningEngine.countTablesUnderLearning.clear();
		this.resetSoftTables();
		// TODO: clear other stuff
		// TODO: call any registered consumers that want to listen to this event. Eg cache in BNCandidate local fitness
	}
	
	
	private void resetSoftTables() {
		// TODO Auto-generated method stub
		
	}


	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#getCounts(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet)
	 */
	public CountTable getCounts(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes) {
		// TODO
		return null;
	}
	

	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#getCPT(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet)
	 */
	public ConditionalProbabilityTable getCPT(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes, PriorTable alphas) 
					throws CountsNotCompleteException{		
		 // TODO: 
		return null;
	}
	
	/* (non-Javadoc)
	 * @see eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator#computeCPT(eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.RandomVariable, java.util.SortedSet, eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.CountTable, eu.ist.daidalos.pervasive.bayesianLibrary.bayesianLearner.interfaces.PriorTable)
	 */
	public ConditionalProbabilityTable computeCPT(RandomVariable targetNode, SortedSet<RandomVariable> parentNodes, 
			CountTable counts, PriorTable alphas) throws CountsNotCompleteException{

	 // TODO: 
		return null;
	}

	public void setCurrentBayesianNetworkStructure(BayesianNetworkCandidate bnc) {
		this.bnc = bnc;
		this.recomputeSoftCounts();
	}


	private void recomputeSoftCounts() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void addMeasurement(JointMeasurement meas) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void clearMeasurements() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public PriorTable getUniformPriors(int nEquiv, RandomVariable rv,
			SortedSet parents) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public void refreshAllLearningTables() throws NodeNotAvailableException {
		// TODO Auto-generated method stub
		
	}	
	
	
}
