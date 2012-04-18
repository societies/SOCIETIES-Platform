package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.JointProbabilityDistributionSolver;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;

/**
 * This class gives an output file with the result of the classification
 * Usage:
 * 		InferenceUniverseProbability.inferenceToFile(this.net, evidence,this.outputClassifier, setEquiprobability);
 * 
 */
public class InferenceUniverseProbability {

	private static String targetRV = "activity_AOOG";
//	private	static double[] priorsActivity = {0.17,0.17,0.17,0.17,0.17,0.005,0.14};
	private	static double[] priorsActivity = {0.195,0.245,0.295,0.1,0.01,0.005,0.14,0.010};
	private static boolean debug = false;
	private static boolean setPriorsActivity = false;

	public static void inferenceToFile(DAG network, BufferedReader evidence, File outputFile, boolean setEquiprobability){

		JointProbabilityDistributionSolver solverUniverse = new JointProbabilityDistributionSolver(network);	
		String measurement;		

		solverUniverse.initializeStructure();
		solverUniverse.setTargetRV(targetRV);
		String stateWanted = null;
		Node[] nodes = network.getNodes();
		Node targetRVNode = null;

		//Set the priors of activity equiprobable in function of the boolean variable:
		if(setEquiprobability){
			for(Node n:nodes){
				if (n.getName().equalsIgnoreCase(targetRV)){
					targetRVNode = n;
					break;
				}
			}
			double lengthProbabilityTable = targetRVNode.getProbTable().getProbabilities().length; //In a cause network, if the target RV is the cause, this number is equal to the number of states.
			for(int i=0;i<lengthProbabilityTable;i++){
				targetRVNode.getProbTable().getProbabilities()[i].setProbability(1/lengthProbabilityTable);	
			}
		}else{
			if(setPriorsActivity){
				for(Node n:nodes){
					if (n.getName().equalsIgnoreCase(targetRV)){
						targetRVNode = n;
						break;
					}
				}
				double lengthProbabilityTable = targetRVNode.getProbTable().getProbabilities().length; //In a cause network, if the target RV is the cause, this number is equal to the number of states.
				for(int i=0;i<lengthProbabilityTable;i++){
					targetRVNode.getProbTable().getProbabilities()[i].setProbability(priorsActivity[i]);	
				}
			}
		}

		//The inference starts:
		try{
			//System.out.println(evidence.readLine());
			String[] nodeDefinitions = evidence.readLine().split("\t"); //nodeDefinitions has the tokens read in the file.		
			Node[] orderedNodes = new Node[nodeDefinitions.length];
			for (int i=0; i<nodeDefinitions.length;i++) {
				for(Node n:nodes){
					if (n.getName().equalsIgnoreCase(nodeDefinitions[i])){
						orderedNodes[i] = n;
						break;
					}
				}
			}
			long executionTime;

			int counter = 0; 
			FileWriter fwOutput = new FileWriter(outputFile,true);
			while((measurement=evidence.readLine())!=null){

				if (debug) System.out.println(++counter);
				if (counter!=0 && measurement.contains(targetRV)) continue;

				if (true)//Testing ONLY: counter >10)
				{
					executionTime = System.nanoTime();
					String[] values = measurement.split("\t");
					if (values[0].equalsIgnoreCase(orderedNodes[0].getName())){ //It could be the definition line of the nodes
						if(debug){
							System.out.println("\n\n"+values[0]+" Ordered Node: "+orderedNodes[0].getName());
						}
					}else{						
						for (int i=0;i<values.length;i++){
							solverUniverse.addEvidence(orderedNodes[i],values[i]);
						}
						stateWanted = solverUniverse.getJPD(1);
						if(debug){
							System.out.println("\n\n"+stateWanted);
						}
						fwOutput.write(stateWanted+"\n");			
						solverUniverse.removeAllEvidence();	
						executionTime -= System.nanoTime();
						if(debug){
							System.out.println("Execution Time: "+(-executionTime));
						}	
					}


				}

			}
			fwOutput.close();

		}
		catch (IOException e){
			e.printStackTrace();
			if(debug) System.out.println("IOException in inferenceToFile");
		}

	}
}
