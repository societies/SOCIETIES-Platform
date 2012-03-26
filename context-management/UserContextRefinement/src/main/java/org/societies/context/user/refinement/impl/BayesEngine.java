package org.societies.context.user.refinement.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesLearnerImpl;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesianNetworkCandidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.SimpleJointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;
import org.societies.context.user.refinement.impl.tools.SetStarterBN;


/**
 * @author fran_ko
 *
 */
public class BayesEngine{
	
	private static Logger logger = LoggerFactory.getLogger(BayesEngine.class);

	private static BayesEngine instance;

	private ArrayList<String> identifiers = new ArrayList<String>();
	private HashMap<String,Map<RandomVariable, ConditionalProbabilityTable>> networks = new HashMap<String,Map<RandomVariable, ConditionalProbabilityTable>>();
	private HashMap<String,DAG> graphs = new HashMap<String,DAG>();
	
	private BayesLearnerImpl learner;
	private BayesianProbabilitiesEstimator learningEngine;


	private BayesEngine(  ){		
		this.learner = new BayesLearnerImpl();
		this.learningEngine = learner.getLearningEngine();
	}
	
	public static BayesEngine getInstance(){
		if (instance==null) return new BayesEngine();
		return instance;
	}

	public DAG learnDAGFromDataFile(File file, int milliseconds, int maxNumberParentsPerNode) {
		Map<RandomVariable, ConditionalProbabilityTable> map = learnFromDataFileName(file.getAbsolutePath(),milliseconds,maxNumberParentsPerNode);
		DAG dag = NetworkConverter.convertStructures(map);
		
		String name = dag.getName();
		networks.put(name,map);
		graphs.put(name,dag);
		identifiers.add(name);
		return dag;
	}

	public DAG learnDAGFromDataFiles(File[] files, int milliseconds, boolean naiveBayes, String cause, int maxNumberParentsPerNode) {

		Map<RandomVariable, ConditionalProbabilityTable> finalNW = null;
		DAG newNetwork = null;

		Map<String, RandomVariable>rvMap = new HashMap<String, RandomVariable>();

		if(logger.isDebugEnabled()) if (files==null) logger.debug("files==null"); else logger.debug("number of files="+files.length);

		BayesianNetworkCandidate starterBN ;
		if (files==null || files.length==0)
			// starterBN = importDataFiles(rvMap,new File[]{new File(".\\resources\\sarah.txt")});
			return null;
		else 
			starterBN = importDataFiles(rvMap,files, maxNumberParentsPerNode);

		if (naiveBayes){
			RandomVariable causeRV = rvMap.get(cause);

			for(RandomVariable effect:rvMap.values()){
				if (effect!=causeRV) 			starterBN.addArc(effect, causeRV);
			}
			
			logger.debug("StarterBN = "+starterBN);
			finalNW = learner.learnParametersOnly(starterBN);

		}
		else{
			finalNW = this.learner.runLearning(milliseconds, starterBN, maxNumberParentsPerNode);
//			finalNW = this.learner.runLearning(milliseconds, starterBN);
		}

		logger.trace("vor setNetwork, finalNW="+finalNW);
		newNetwork = NetworkConverter.convertStructures(finalNW);


		String name = newNetwork.getName();
		networks.put(name,finalNW);
		graphs.put(name,newNetwork);
		identifiers.add(name);
		
		logger.trace("about to finish");
		return newNetwork;
	}

	/**
	 * Learns the DAG from the files and also saves the BayesianNetworkCandidate that gives 
	 * that DAG in the file specified by fileBestCandidate.
	 * @param files
	 * @param milliseconds
	 * @param naiveBayes
	 * @param maxNumberParentsPerNode
	 * @return
	 * 
	 * @author vera_ma
	 */
	public DAG learnDAGFromFilesAndSetStarterFromFile(File[] files, int milliseconds, boolean naiveBayes, String cause, int maxNumberParentsPerNode, boolean buildNetworkAndNoLearning,String filenameStarterNetwork) {

		Map<RandomVariable, ConditionalProbabilityTable>finalNW = null;
		Map<String, RandomVariable>rvMap = new HashMap<String, RandomVariable>();

		if(logger.isDebugEnabled()) if (files==null) logger.debug("files==null"); else logger.debug("number of files="+files.length);

		BayesianNetworkCandidate starterBN ;
		if (files==null || files.length==0){
			//starterBN = importDataFiles(rvMap,new File[]{new File(".\\resources\\sarah.txt")});
			logger.error("No files specified for learning.");
			return null;
		}
		else 
			starterBN = importDataFiles(rvMap,files, maxNumberParentsPerNode);

		if(buildNetworkAndNoLearning){
			if (naiveBayes)
				starterBN = SetStarterBN.setNaiveBayesStarterBN(rvMap, starterBN, cause);
			else starterBN = SetStarterBN.setStarterBN(rvMap,starterBN,filenameStarterNetwork);
			finalNW = learner.learnParametersOnly(starterBN);
		}
		else{
			if (naiveBayes)
				starterBN = SetStarterBN.setNaiveBayesStarterBN(rvMap, starterBN, cause);
			else starterBN = SetStarterBN.setStarterBN(rvMap,starterBN,filenameStarterNetwork);			

			finalNW = learner.runLearning(milliseconds, starterBN, maxNumberParentsPerNode);
		}


		DAG newNetwork = null;

		logger.trace("vor setNetwork, finalNW="+finalNW);
		newNetwork = NetworkConverter.convertStructures(finalNW);


		String name = newNetwork.getName();
		networks.put(name,finalNW);
		graphs.put(name,newNetwork);
		identifiers.add(name);
		
		logger.trace("about to finish");

		return newNetwork;
	}
	
	/**
	 * Takes as input a BN in DAG format with complete structure, ignoring the parameters.
	 * Learns only parameters or structure with given start point, following the parameter settings.
	 *
	 * @param files		input to learn from, tabulator separated files, having the RVs in the first line.
	 * @param milliseconds Time to run the greedy hill climber in ms, ignored in case 'structureLearning=false;'.
	 * @param structureLearning If 'true' the starterNetwork is used as starting point for the greedy hill climber. If 'false' only the parameters are learnt.
	 * @param maxNumberParentsPerNode Also relevant in case 'structureLearning=false', as it impacts also setting the starterCandidate;
	 * @param starterNetwork BN used to start the greedy hill climber or to fill with parameters. Existing parameters are ignored.
	 * @return
	 * 
	 * @author fran_ko
	 */
	public DAG learnDAGFromFilesGivenDAGStructure(File[] files, int milliseconds, int maxNumberParentsPerNode, boolean structureLearning, DAG starterNetwork) {

		Map<RandomVariable, ConditionalProbabilityTable>finalNW = null;
		Map<String, RandomVariable>rvMap = new HashMap<String, RandomVariable>();

		if(logger.isDebugEnabled()) if (files==null) logger.debug("files==null"); else logger.debug("number of files="+files.length);

		BayesianNetworkCandidate starterBN ;
		if (files==null || files.length==0){
			//starterBN = importDataFiles(rvMap,new File[]{new File(".\\resources\\sarah.txt")});
			logger.error("No files specified for learning.");
			return null;
		}
		else 
			starterBN = importDataFiles(rvMap,files, maxNumberParentsPerNode);

		starterBN = SetStarterBN.setStarterBN(rvMap,starterBN,starterNetwork);
		if (starterBN==null)
			logger.error("starterBN="+starterBN);

		if(!structureLearning){
			finalNW = learner.learnParametersOnly(starterBN);
		}
		else{		
			finalNW = learner.runLearning(milliseconds, starterBN, maxNumberParentsPerNode);
		}


		DAG newNetwork = null;
		newNetwork = NetworkConverter.convertStructures(finalNW);


		String name = newNetwork.getName();
		networks.put(name,finalNW);
		graphs.put(name,newNetwork);
		identifiers.add(name);

		return newNetwork;
	}

	private Map<RandomVariable, ConditionalProbabilityTable> learnFromDataFileName(String filename, int milliseconds, int maxNumberParentsPerNode) {

		logger.debug("Learning from: "+filename);

		BayesianNetworkCandidate starterBN ;
		if (filename==null || filename.equals(""))
//			starterBN = importData(rvMap,".\\resources\\sarah.txt");
			return null;
		else 
			starterBN = importData(filename, maxNumberParentsPerNode);

		return this.learner.runLearning(milliseconds, starterBN, maxNumberParentsPerNode);
	}

	private BayesianNetworkCandidate importData(String filename, int maxNumberParentsPerNode) {
		Map<String, RandomVariable> rvMap = new HashMap<String, RandomVariable>();

		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
		.computeFromDataFile(rvMap, filename);

		Set<RandomVariable> allnodesset = new HashSet<RandomVariable>();
		allnodesset.clear();

		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			//	System.out.println("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset, allnodesset.size());

		return starterBN;
	}

	private BayesianNetworkCandidate importDataFiles(Map<String, RandomVariable> rvMap, File[] files, int maxNumberParentsPerNode) {

		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement.computeFromDataFiles(rvMap, files);

		Set<RandomVariable>allnodesset = new HashSet<RandomVariable>();
		allnodesset.clear();

		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			//	System.out.println("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset, maxNumberParentsPerNode);

		return starterBN;
	}


}