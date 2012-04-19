package org.societies.context.user.refinement.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.JunctionTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;


public class SimpleInference {
	private static String targetRV = "activity";
	private static boolean debug = false;
	private static File output = new File("resources/classification/OutputClassification.txt");

	public static void infer(DAG network, BufferedReader evidence){

		JunctionTree jTree = new JunctionTree(network);

		jTree.initialiseJTree();

		jTree.propagate();

		String measurement;		

		Node[] nodes = network.getNodes();
		Node targetNode = null; // targetNode will contain the node you want to inference. In our case, "activity"
		for(Node n:nodes){
			if (n.getName().equals(targetRV)){
				targetNode=n;
				break;
			}
		}
		if (targetNode==null){
			System.err.println("TargetNode is not existing!");
			System.exit(0);
		}
		//The inference starts:
		try{
			String[] nodeDefinitions = evidence.readLine().split("\t"); //nodeDefinitions has the tokens read in the file.
			//Node[] orderedNodes = new Node[nodeDefinitions.length -1];//TODO change, once we have evidence files
			Node[] orderedNodes = new Node[nodeDefinitions.length];
			for (int i=0; i<nodeDefinitions.length;i++) {
				for(Node n:nodes){
					//if (!n.getName().equalsIgnoreCase("activity") && n.getName().equalsIgnoreCase(nodeDefinitions[i])){//TODO change, once we have evidence files
					if (n.getName().equalsIgnoreCase(nodeDefinitions[i])){//TODO change, once we have evidence files
						orderedNodes[i] = n;
						//OrderedNodes will have the interval value of the feature.
						break;
					}
				}
			}

			int counter = 0; 
			while((measurement=evidence.readLine())!=null){
				if (debug) System.out.println(++counter);
				if (counter!=0 && measurement.contains(targetRV)) continue;


				if (true)//Testing ONLY: counter >10)
				{
					String[] values = measurement.split("\t");
					//<values.length-1;i++){//TODO change, once we have evidence files
					for (int i=0;i<values.length;i++){//TODO change, once we have evidence files
						jTree.addEvidence(orderedNodes[i],values[i]);
					}
					//				System.out.println("\n\n\n\n\n"+targetNode.printMarginalization());
					//System.out.println("\n\n"+jTree.getMarginalized(targetRV, 1));	
					FileWriter fwOutput = new FileWriter(output,true);
					fwOutput.write(jTree.getMarginalized(targetRV,1)+"\n");
					fwOutput.close();
				}
				jTree.removeAllEvidence();
			}

			/*
			String[] values = {"Home", "Office", "MeetingRoom", "Outdoor"};//{"off", "on"};
			double[] probs = {0,1,0,0};//{1,0};
			beispiel.addEvidence(bNet.getNodes()[7], values, probs);
			 */		

		}
		catch (IOException e){
			e.printStackTrace();
		}

	}


	public static void inferenceToFile(DAG network, BufferedReader evidence, File outputFile){

		JunctionTree jTree = new JunctionTree(network);

		jTree.initialiseJTree();

		jTree.propagate();

		String measurement;		

		Node[] nodes = network.getNodes();
		Node targetNode = null; // targetNode will contain the node you want to inference. In our case, "activity"
		for(Node n:nodes){
			if (n.getName().equals(targetRV)){
				targetNode=n;
				break;
			}
		}
		if (targetNode==null){
			System.err.println("TargetNode is not existing!");
			System.exit(0);
		}
		//The inference starts:
		try{
			String[] nodeDefinitions = evidence.readLine().split("\t"); //nodeDefinitions has the tokens read in the file.
			//Node[] orderedNodes = new Node[nodeDefinitions.length -1];//TODO change, once we have evidence files
			Node[] orderedNodes = new Node[nodeDefinitions.length];
			for (int i=0; i<nodeDefinitions.length;i++) {
				for(Node n:nodes){
					//if (!n.getName().equalsIgnoreCase("activity") && n.getName().equalsIgnoreCase(nodeDefinitions[i])){//TODO change, once we have evidence files
					if (n.getName().equalsIgnoreCase(nodeDefinitions[i])){//TODO change, once we have evidence files
						orderedNodes[i] = n;
						//OrderedNodes will have the interval value of the feature.
						break;
					}
				}
			}
			long executionTime;

			int counter = 0; 
			FileWriter fwOutput = new FileWriter(outputFile,true);
			while((measurement=evidence.readLine())!=null){
				//System.out.println(++counter);
				if (debug) System.out.println(++counter);
				if (counter!=0 && measurement.contains(targetRV)) continue;
				if (true)//Testing ONLY: counter >10)
				{
					executionTime = System.nanoTime();
					String[] values = measurement.split("\t");
					/*the next sentence was added by Maria*/
					if (values[0].equalsIgnoreCase(orderedNodes[0].getName())){ //It could be the definition line of the nodes
						if(debug){
							System.out.println("\n\n"+values[0]+" Ordered Node: "+orderedNodes[0].getName());
						}
					}else{

						for (int i=0;i<values.length;i++){//TODO change, once we have evidence files
							jTree.addEvidence(orderedNodes[i],values[i]);
						}
						////				System.out.println("\n\n\n\n\n"+targetNode.printMarginalization());
						//System.out.println("\n\n"+jTree.getMarginalized(targetRV, 1));						
						fwOutput.write(jTree.getMarginalized(targetRV,1)+"\n");			
						jTree.removeAllEvidence();	
						executionTime -= System.nanoTime();
						//System.out.println("Execution Time: "+(-executionTime));
					}

				}
				//jTree.removeAllEvidence();
			}
			fwOutput.close();
			/*
			String[] values = {"Home", "Office", "MeetingRoom", "Outdoor"};//{"off", "on"};
			double[] probs = {0,1,0,0};//{1,0};
			beispiel.addEvidence(bNet.getNodes()[7], values, probs);
			 */		

		}
		catch (IOException e){
			e.printStackTrace();
		}

	}
}
