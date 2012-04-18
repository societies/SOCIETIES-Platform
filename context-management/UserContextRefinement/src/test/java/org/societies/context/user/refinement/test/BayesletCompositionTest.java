package org.societies.context.user.refinement.test;
import java.io.File;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.JunctionTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.tools.NetworkConverter;


/**
 * @author fran_ko
 *
 */
public class BayesletCompositionTest {

	private static int repetitions = 1;
	private static String path = "resources";
	private static String fileName = 
								//	"superHuge.xdsl";
								//	"simple.xdsl";
								//	"superSimple.xdsl";
									"LCBayesletNoEvidence.xdsl";
								//	"LCBayesletNoEvidence_noSEnodes.xdsl";
	private static double a = 1.0; // ATTENTION: to get fractions, one of the numbers has to be a double in order to avoid integer division
	private static double b = 1;
	
	
	
	
	
	
	

	private static Logger logger = LoggerFactory.getLogger(BayesletCompositionTest.class);
	private static String filePath = path  +File.separator+ fileName ;
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		//System.out.println(doubleCheckEntropy(new double[]{0.93179,0.01768,0.05053}));
		//entropyWithEvidence();
		allTests();
	}
	
	private static void entropyWithEvidence() {
		DAG bNet = NetworkConverter.genieToJava(filePath );

		logger.debug(""+bNet);

		Node first = bNet.getNodes()[0];
		System.out.print("Entropy of "+first.getName()+" given "+bNet.getNodes()[2].getName()+","+bNet.getNodes()[3].getName()+","+bNet.getNodes()[4].getName()+
					": H("+first.getName()+"| e)=");

		bNet.getNodes()[3].setProbDistribution(new double[]{1.0,0.0,0,0,1,1});

		JunctionTree beispiel = new JunctionTree(bNet);
		beispiel.initialiseJTree();

//		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[1], "large");
//		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[2], bNet.getNodes()[2].getStates(), new double[]{0.8,0.1,0.1});
		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[4], "evidence");
		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[3], "evidence");
		beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[2], "medium");

		beispiel.propagate();

//		for(Node n:bNet.getNodes())			System.out.println(n.printMarginalization());
		
		System.out.println( entropy(first));
	}
	
	private static double doubleCheckEntropy(double[] probs){
		double result = 0.0;
		
		for (double d:probs){
			if (d==0) continue;
			result -= d*Math.log(d)/Math.log(2);
		}
		
		return result;
	}
	
	private static void allTests(){
		double i_XY;
		double h_Y;
		int sizeBL = 9;
		for (int i=0;i<repetitions;i++){

			System.out.println("Entropy of Bayeslets:\n");
			h_Y = doubleCheckEntropy(new double[]{0.33,0.034,0.033});
			System.out.println("Entropy of Radar Bayeslet (OutputNode): H(Y)="+h_Y);
			double cRadar = h_Y - (h_Y/(Math.pow(sizeBL, a)+b)); 
			h_Y = doubleCheckEntropy(new double[]{0.33,0.034,0.033});
			System.out.println("Entropy of V2V Bayeslet (OutputNode): H(Y)="+h_Y);
			double cV2V = h_Y - (h_Y/(Math.pow(sizeBL, a)+b));
			System.out.println(cRadar+"\n"+cV2V);
				

			System.out.println("\n\nNO EVIDENCE:\n");
			i_XY = evalMutualInformation(0,1,-1,null,-1,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cRadar));
			i_XY = evalMutualInformation(0,2,-1,null,-1,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cV2V));
			
			
			System.out.println("\n\nSoft Evidence of Priors:\n");
			i_XY = evalMutualInformation(0,1,4,null,-1,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cRadar));
//			i_XY = evalMutualInformationSE(0,1,1,new double[]{0.95,0.04,0.01},-1,null);
//			System.out.println(i_XY + " (SE)");
			i_XY = evalMutualInformation(0,2,3,null,-1,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cV2V));
//			i_XY = evalMutualInformationSE(0,1,2,new double[]{0.8,0.1,0.1},-1,null);
//			System.out.println(i_XY + " (SE)");

			
			
			System.out.println("\n\nSoft Evidence Representing Hard Evidence of Winner:\n");
			double[] shortCPT = {1.0,0.0,0,0,1,1};
			i_XY = evalMutualInformation(0,2,3,shortCPT,4,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cV2V));
			double[] mediumCPT = {0,1.0,0,1,0,1};
			i_XY = evalMutualInformation(0,2,3,mediumCPT,4,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cV2V));
			double[] largeCPT = {0,0.0,1.0,1.0,1,0};
			i_XY = evalMutualInformation(0,2,3,largeCPT,4,null);
			System.out.println(i_XY);
			System.out.println("\t=>\tNetEU="+(i_XY-cV2V));

		}
	}

	/**
	 * Loads a BN from a file (specified by the static fields), constructs the JTree and 
	 * calculates the mutual information I(X:Y) between the nodes specified by the parameters 
	 * with or without specified evidence.
	 * Output is in the console
	 * 
	 * @param firstIndex index of node X in the loaded BN
	 * @param secondIndex index of node Y in the loaded BN
	 * @param evidenceNodeIndex index of a soft evidence node in the loaded BN. -1 if unused
	 * @param newProbs new probability distribution of the node specified by {@value evidenceNodeIndex}. 
	 * 					If null, the original probabilityDistribution is not replaced.
	 * @param secondEvidenceIndex index of a second soft evidence node in the loaded BN. -1 if unused
	 * @param secondNewCPT new probability distribution of the node specified by {@param secondEvidenceIndex} 
	 * 					If null, the original probabilityDistribution is not replaced. 
	 * 
	 * @return mutual information I(X:Y)
	 */
	private static double evalMutualInformation(int firstIndex, int secondIndex,int evidenceNodeIndex, double[] newProbs, int secondEvidenceIndex, double[] secondNewCPT) {
		
		DAG bNet = NetworkConverter.genieToJava(filePath );

		logger.debug(""+bNet);

		Node first = bNet.getNodes()[firstIndex];
		Node second = bNet.getNodes()[secondIndex];
		System.out.print("Mutual Information between "+first.getName()+" and "+second.getName()+
					": I("+first.getName()+":"+second.getName()+ ")=");

		if (evidenceNodeIndex > -1 && newProbs!=null){
			logger.debug(""+bNet.getNodes()[evidenceNodeIndex].getProbTable());
			bNet.getNodes()[evidenceNodeIndex].setProbDistribution(newProbs);
			logger.debug(""+bNet.getNodes()[evidenceNodeIndex].getProbTable());
		}
		if (secondEvidenceIndex> -1 && secondNewCPT!=null){
			logger.debug(""+bNet.getNodes()[secondEvidenceIndex].getProbTable());
			bNet.getNodes()[secondEvidenceIndex].setProbDistribution(secondNewCPT);
			logger.debug(""+bNet.getNodes()[secondEvidenceIndex].getProbTable());
		}
		for (int n = 0;n<bNet.getNodes().length;n++){
			logger.debug(n+": "+bNet.getNodes()[n]);
		}

		JunctionTree beispiel = new JunctionTree(bNet);
		beispiel.initialiseJTree();

		if (evidenceNodeIndex > -1) beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[evidenceNodeIndex], "evidence");
		if (secondEvidenceIndex > -1) beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[secondEvidenceIndex], "evidence");

		beispiel.propagate();
		
		return mutualInformation(first,second, beispiel);
		//beispiel.propagate();
		//System.out.println(entropy(first));

	}

	/**
	 * @see BayesletCompositionTest.evalMutualInformation
	 * EvidenceParameters are for the softEvidence method calls
	 * 
	 * @param firstIndex
	 * @param secondIndex
	 * @param evidenceNodeIndex1
	 * @param softEvidence1
	 * @param evidenceNodeIndex2
	 * @param softEvidence2
	 * @return
	 */
	private static double evalMutualInformationSE(int firstIndex, int secondIndex,int evidenceNodeIndex1, double[] softEvidence1, int evidenceNodeIndex2, double[] softEvidence2) {
		DAG bNet = NetworkConverter.genieToJava(filePath );

		logger.debug(""+bNet);

		Node first = bNet.getNodes()[firstIndex];
		Node second = bNet.getNodes()[secondIndex];
		System.out.print("Mutual Information between "+first.getName()+" and "+second.getName()+
					": I("+first.getName()+":"+second.getName()+ ")=");

		JunctionTree beispiel = new JunctionTree(bNet);
		beispiel.initialiseJTree();

		if (evidenceNodeIndex1 > -1 && softEvidence1!=null) beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[evidenceNodeIndex1], bNet.getNodes()[evidenceNodeIndex1].getStates(), softEvidence1);
		if (evidenceNodeIndex2 > -1 && softEvidence2!=null) beispiel.addEvidenceWithoutPropagating(bNet.getNodes()[evidenceNodeIndex2], bNet.getNodes()[evidenceNodeIndex2].getStates(), softEvidence2);

		beispiel.propagate();
		
		return mutualInformation(first,second, beispiel);
		//beispiel.propagate();
		//System.out.println(entropy(first));

	}

	
	
	private static double mutualInformation(Node x, Node y, JunctionTree jTree) {
		return entropy(x) - conditionalEntropy(x,y,jTree);
	}

	private static double conditionalEntropy(Node x, Node y, JunctionTree jTree) {
		double result = 0.0;
		
		ProbabilityDistribution pdY = y.getMarginalization();
		String[] statesY = y.getStates();
		if (statesY.length!=pdY.getProbabilities().length)
			System.err.println("Passt NICHT!");
		int stateCounter = 0;
		
		double prob_y;
		double conditionalEntropyXy;

		if (y.hasHardEvidence()){
			if (logger.isDebugEnabled())	logger.debug("Node "+y.getName()+" hasHardEvidence=true");
			if (logger.isTraceEnabled())	logger.trace(y.printMarginalization());
			return entropy(x);
		}
		
		for (Probability cptYCase : pdY.getProbabilities()) {
			prob_y = cptYCase.getProbability();
			
			//jTree.removeEvidence(y);
			jTree.addEvidence(y, statesY[stateCounter++]);  // propagates after addition
			
			conditionalEntropyXy=entropy(x); // gets newest marginalization
			//System.out.println(prob_y *conditionalEntropyXy);
			
			result += prob_y * conditionalEntropyXy;
			jTree.removeEvidence(y);
		}

		return result;
	}

	private static double entropy(Node x) {
		double result = 0.0;
		ProbabilityDistribution pd = x.getMarginalization();
		double frequency; 
			
		for (Probability cptCase : pd.getProbabilities()) {
			frequency = cptCase.getProbability();
			if (frequency == 0) continue; // 0 * log(0) := 0
			result -= frequency * (Math.log(frequency) / Math.log(2));
		}
		return result;
	}

}
