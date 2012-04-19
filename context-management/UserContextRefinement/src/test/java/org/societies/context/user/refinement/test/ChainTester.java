package org.societies.context.user.refinement.test;

import java.io.File;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.exceptions.CountsNotCompleteException;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BNSegment;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BasicGreedyHillClimber;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesianNetworkCandidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.BayesianNetworkCandidatesGenerator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.SimpleCountingLearningEngine;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.impl.SimpleJointMeasurement;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianLearningClient;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.BayesianProbabilitiesEstimator;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.Candidate;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.ConditionalProbabilityTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.CountTable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.RandomVariable;
import org.societies.context.user.refinement.impl.bayesianLibrary.bayesianLearner.interfaces.SearchConsumer;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;

/**
 * Main class.
 */
public class ChainTester implements SearchConsumer {

	private BayesianProbabilitiesEstimator learningEngine;

	private BayesianNetworkCandidate startingPoint;
	private BayesianNetworkCandidate bestCandidate;

	private BayesianLearningClient learningClient;

	private boolean debug=false;

	public ChainTester(BayesianLearningClient learningClient) {
		this.learningEngine = new SimpleCountingLearningEngine();
		this.learningClient = learningClient;
	}
/* only relevant for BayesianPreferences
	public void setLearningData(List list){

		Map<String, RandomVariable> rvMap = new HashMap<String, RandomVariable>();
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement.computeFromHistory(rvMap, list);

		Set<RandomVariable>allnodesset = new HashSet();
		allnodesset.clear();
		
		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			//	System.out.println("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset);
		
		startingPoint = starterBN;

	}
*/
	public void learnFromFile(String filename, int maxNumberParentsPerNode) {
		learnFromFile(filename,60000, maxNumberParentsPerNode);
	}
	
	public Map learnFromFile(String filename, int milliseconds, int maxNumberParentsPerNode) {

		Map<String, RandomVariable> rvMap = new HashMap<String, RandomVariable>();

		//BayesianNetworkCandidate starterBN = importKidsData(rvMap);
		//BayesianNetworkCandidate starterBN = importCmcdataData(rvMap);
		//BayesianNetworkCandidate starterBN = importSprinklerData(rvMap);
		//BayesianNetworkCandidate starterBN = importHousingData(rvMap);
		//BayesianNetworkCandidate starterBN = importActivityLearningData(rvMap);
		if(debug) System.out.println(".\\resources\\activityData\\"+filename);
//		System.exit(0);
		
		BayesianNetworkCandidate starterBN ;
		if (filename==null || filename.equals(""))
			starterBN = importData(rvMap,".\\resources\\sarah.txt");
		else 
			starterBN = importData(rvMap,".\\resources\\activityData\\"+filename);
		
		BasicGreedyHillClimber bghc = new BasicGreedyHillClimber(
				new BayesianNetworkCandidatesGenerator(this.learningEngine,
						starterBN.getNodes(), maxNumberParentsPerNode), this);

		bghc.startSearch();

		try {
			Thread.sleep(milliseconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		bghc.stopSearch();

		if(debug) System.out.println("BGHC STOPPED!");
		Map<RandomVariable, ConditionalProbabilityTable>finalNW = null;
		if (this.bestCandidate != null) {
			Map segments = this.bestCandidate.getSegments();
			finalNW = new HashMap<RandomVariable, ConditionalProbabilityTable>();
			Iterator it = segments.keySet().iterator();
			while (it.hasNext()) {
				RandomVariable rv = (RandomVariable) it.next();
				BNSegment seg = (BNSegment) segments.get(rv);
				CountTable ct = this.learningEngine.getCounts(rv, seg
						.getOrderedParents());
				if(debug) System.out.println(ct);

				try {
					ConditionalProbabilityTable cpt = this.learningEngine
							.getCPT(rv, seg.getOrderedParents(),
									this.learningEngine.getUniformPriors(
											this.bestCandidate.getN_equiv(),
											rv, seg.getOrderedParents()));
					finalNW.put(rv, cpt);
					
					/* Testing
					if (rv.getName().equals("dow")) {
//						RandomVariable appsRV = (RandomVariable) rvMap.get("apps");
						RandomVariable volumeRV = (RandomVariable) rvMap.get("loc");
						try {
//							SimpleInstantiatedRV appsSIRV = new SimpleInstantiatedRV(appsRV, false, appsRV.getNodeValueFromText("pim"));
							SimpleInstantiatedRV volumeSIRV = new SimpleInstantiatedRV(volumeRV, false, volumeRV.getNodeValueFromText("home"));
							Set parents = new HashSet();
//							parents.add(appsSIRV);
							parents.add(volumeSIRV);
							double cptvalue = cpt.getProbability(parents, rv.getNodeValueFromText("Fri"));
							System.out.println("\n\n\n\n\n\n\nProbability for parents: " + //appsSIRV +
									" UUUUU " + volumeSIRV + " : " + cptvalue+"\n\n\n\n\n\n\n");
							
							
//							 appsSIRV = new SimpleInstantiatedRV(appsRV, false, appsRV.getNodeValueFromText("media"));
							 volumeSIRV = new SimpleInstantiatedRV(volumeRV, false, volumeRV.getNodeValueFromText("canteen"));
							 parents = new HashSet();
//							parents.add(appsSIRV);
							parents.add(volumeSIRV);
							 cptvalue = cpt.getProbability(parents, rv.getNodeValueFromText("Sat"));
							System.out.println("\n\n\n\n\n\n\nProbability for parents: " + //appsSIRV +
									" UUUUU " + volumeSIRV + " : " + cptvalue+"\n\n\n\n\n\n\n");
							
							
//							appsSIRV = new SimpleInstantiatedRV(appsRV, false, appsRV.getNodeValueFromText("pim"));
							 volumeSIRV = new SimpleInstantiatedRV(volumeRV, false, volumeRV.getNodeValueFromText("canteen"));
							 parents = new HashSet();
//							parents.add(appsSIRV);
							parents.add(volumeSIRV);
							 cptvalue = cpt.getProbability(parents, rv.getNodeValueFromText("Mo-Th"));
							System.out.println("\n\n\n\n\n\n\nProbability for parents: " + //appsSIRV +
									" UUUUU " + volumeSIRV + " : " + cptvalue+"\n\n\n\n\n\n\n");
							
							
//							appsSIRV = new SimpleInstantiatedRV(appsRV, false, appsRV.getNodeValueFromText("none"));
							 volumeSIRV = new SimpleInstantiatedRV(volumeRV, false, volumeRV.getNodeValueFromText("office"));
							 parents = new HashSet();
//							parents.add(appsSIRV);
							parents.add(volumeSIRV);
							 cptvalue = cpt.getProbability(parents, rv.getNodeValueFromText("Mo-Th"));
							System.out.println("\n\n\n\n\n\n\nProbability for parents: " + //appsSIRV +
									" UUUUU " + volumeSIRV + " : " + cptvalue+"\n\n\n\n\n\n\n");
							
						} catch (NodeValueIndexNotInNodeRangeException e) {
							e.printStackTrace();
						} catch (NodeValueTextNotInNodeRangeException e) {
							e.printStackTrace();
						} catch (ParentsNotContainedException e) {
							e.printStackTrace();
						} catch (PriorAndCountTablesMismatchException e) {
							e.printStackTrace();
						} catch (RVNotInstantiatedException e) {
							e.printStackTrace();
						}
								
					}
					/**/
				} catch (CountsNotCompleteException e) {
					e.printStackTrace();
				}

			}
			

			if(debug) System.out.println("vor setNetwork, finalNW="+finalNW);
			this.learningClient.setNetwork("Tester BN", finalNW);

			if(debug) System.out.println("about to finish");
		}
		return finalNW;

	}

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importCmcdataData(Map rvMap) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, ".\\resources\\cmcdata.txt");

		Set<RandomVariable>allnodesset = new HashSet();
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

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importActivityLearningData(Map rvMap) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, ".\\resources\\activityLearning simulation.txt");

		Set<RandomVariable>allnodesset = new HashSet();
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

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importHousingData(Map rvMap) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, ".\\resources\\housingdata.txt");

		Set<RandomVariable>allnodesset = new HashSet();
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

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importData(Map rvMap, String filename) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, filename);

		Set<RandomVariable>allnodesset = new HashSet();
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


	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importDataFiles(Map rvMap, File[] files) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFiles(rvMap, files);

		Set<RandomVariable>allnodesset = new HashSet();
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

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importSprinklerData(Map rvMap) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, ".\\resources\\b-courseSprinkler1.txt");

		Set<RandomVariable>allnodesset = new HashSet();
		allnodesset.clear();

		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			System.out.println("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset, allnodesset.size());

		RandomVariable weather = (RandomVariable) rvMap.get("weather");
		RandomVariable grass = (RandomVariable) rvMap.get("grass");
		RandomVariable neighbourGrass = (RandomVariable) rvMap.get("ng");
		RandomVariable mysprinkler = (RandomVariable) rvMap.get("mysprinkler");
		//	starterBN.addArc(grass, weather);
		//	starterBN.addArc(grass, mysprinkler);
		//	starterBN.addArc(neighbourGrass, weather);		

		return starterBN;
	}

	/**
	 * @param rvMap
	 * @return
	 */
	private BayesianNetworkCandidate importKidsData(Map rvMap) {
		SimpleJointMeasurement[] sjmFile = SimpleJointMeasurement
				.computeFromDataFile(rvMap, ".\\resources\\popkidsdata.txt");

		Set<RandomVariable>allnodesset = new HashSet();
		allnodesset.clear();

		this.learningEngine.resetTables();
		this.learningEngine.clearMeasurements();
		allnodesset.addAll(rvMap.values());
		for (int r = 0; r < sjmFile.length; r++) {
			//		System.out.println("Adding sjm to Learning engine: " + sjmFile[r]);
			this.learningEngine.addMeasurement(sjmFile[r]);
		}
		BayesianNetworkCandidate starterBN = new BayesianNetworkCandidate(
				this.learningEngine, allnodesset, allnodesset.size());

		RandomVariable Goals = (RandomVariable) rvMap.get("Goals");
		RandomVariable Grades = (RandomVariable) rvMap.get("Grades");
		RandomVariable Grade = (RandomVariable) rvMap.get("Grade");
		RandomVariable Race = (RandomVariable) rvMap.get("Race");

		RandomVariable Gender = (RandomVariable) rvMap.get("Gender");
		RandomVariable Looks = (RandomVariable) rvMap.get("Looks");
		RandomVariable School = (RandomVariable) rvMap.get("School");
		RandomVariable Sports = (RandomVariable) rvMap.get("Sports");

		RandomVariable Urban_Rural = (RandomVariable) rvMap.get("Urban/Rural");
		RandomVariable Age = (RandomVariable) rvMap.get("Age");
		RandomVariable Money = (RandomVariable) rvMap.get("Money");

		//		starterBN.addArc(Race, Urban_Rural);
		//		starterBN.addArc(Urban_Rural, School);
		//		starterBN.addArc(School, Grade);		
		//		starterBN.addArc(Age, Grade);	
		//		
		//		starterBN.addArc(Grade, Grades);			
		//		starterBN.addArc(Grades, Money);	
		//		starterBN.addArc(Looks, Grades);		
		//		starterBN.addArc(Looks, Money);	
		//		starterBN.addArc(Gender, Sports);
		//		starterBN.addArc(Sports, Grades);	
		//		starterBN.addArc(Sports, Looks);
		//		starterBN.addArc(Sports, Money);		
		//		starterBN.addArc(Goals, Gender);	

		return starterBN;
	}

	public void notifyNewSearchResult(Candidate newCandidate,
			double oldBestscore, boolean stoppedExternally, int counter,
			long genCounter, long randomRestartsCounter, boolean isAbsoluteBest,
			boolean foundSignificantlyBetter) {
		String filename = null;

		if (isAbsoluteBest) {
			filename = ".\\resources\\bestfound.txt";
			BasicGreedyHillClimber.updateResultFiles(filename, oldBestscore,
					newCandidate, counter, genCounter, randomRestartsCounter,
					true);
			this.bestCandidate = (BayesianNetworkCandidate) newCandidate
					.cloneCandidate();
		}
		filename = ".\\resources\\closefound.txt";
		BasicGreedyHillClimber.updateResultFiles(filename, oldBestscore,
				newCandidate, counter, genCounter, randomRestartsCounter,
				!foundSignificantlyBetter);

	}
	
	public DAG learnDAGFromFiles(File[] files, int milliseconds, boolean naiveBayes, int maxNumberParentsPerNode) {

		Map<String, RandomVariable>rvMap = new HashMap<String, RandomVariable>();

		if(debug) if (files==null) System.out.println("files==null"); else System.out.println("number of files="+files.length);
		
		BayesianNetworkCandidate starterBN ;
		if (files==null || files.length==0)
			starterBN = importDataFiles(rvMap,new File[]{new File(".\\resources\\sarah.txt")});
		else 
			starterBN = importDataFiles(rvMap,files);
		
		
		if (naiveBayes){
			String cause = "activity";
			RandomVariable causeRV = rvMap.get(cause);
			
			for(RandomVariable effect:rvMap.values()){
				if (effect!=causeRV) 			starterBN.addArc(effect, causeRV);
			}
			double fitness = starterBN.computeFitness();
			
			boolean foundAbsoluteBest = true;
			boolean foundSignificantBetter = true;
			boolean stoppedExternally = false;
			notifyNewSearchResult(starterBN, 0, stoppedExternally, 
					1, 1, 0, 
					foundAbsoluteBest, foundSignificantBetter);
		}
		else{
			BasicGreedyHillClimber bghc = new BasicGreedyHillClimber(
					new BayesianNetworkCandidatesGenerator(this.learningEngine,
							starterBN.getNodes(), maxNumberParentsPerNode), this);

			bghc.startSearch();

			try {
				Thread.sleep(milliseconds);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
			bghc.stopSearch();

			if(debug) System.out.println("BGHC STOPPED!");
		}


		
		Map<RandomVariable, ConditionalProbabilityTable>finalNW = null;
		DAG newNetwork = null;
		
		if (this.bestCandidate != null) {
			Map segments = this.bestCandidate.getSegments();
			finalNW = new HashMap<RandomVariable, ConditionalProbabilityTable>();
			Iterator it = segments.keySet().iterator();
			while (it.hasNext()) {
				RandomVariable rv = (RandomVariable) it.next();
				BNSegment seg = (BNSegment) segments.get(rv);
				CountTable ct = this.learningEngine.getCounts(rv, seg
						.getOrderedParents());
				if(debug) System.out.println(ct);

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
			

			newNetwork = NetworkConverter.convertStructures(finalNW);
		}
		return newNetwork;
	}
}