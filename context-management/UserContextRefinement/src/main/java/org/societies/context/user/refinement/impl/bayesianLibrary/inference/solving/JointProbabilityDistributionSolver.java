package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.DAG;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.StateRV;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;

public class JointProbabilityDistributionSolver {

	private DAG dagNetwork;
	private String targetRV;
	private Node targetRVNode;
	private ArrayList<HasProbabilityTable> nodes = new ArrayList<HasProbabilityTable>();
	private static boolean debug = false;
	
	private ArrayList<HasProbabilityTable> observedNodes = new ArrayList<HasProbabilityTable>();
	
	public JointProbabilityDistributionSolver(DAG dag){
		this.dagNetwork = dag;
		
		for(int i=0;i<this.dagNetwork.getNodes().length;i++) {
			nodes.add(this.dagNetwork.getNodes()[i]);
		}
	}
	
	public void initializeStructure(){
		Node[] nodes = this.dagNetwork.getNodes();
		for(int i=0;i<nodes.length;i++){
			nodes[i].initializeObservation();
		}
	}
	public void setTargetRV(String nameRV){		
		
		Node[] nodes = this.dagNetwork.getNodes();
		Node targetNode = null; // targetNode will contain the node you want to inference. In our case, "activity"
		for(Node n:nodes){
			if (n.getName().equals(nameRV)){
				targetNode=n;
				break;
			}
		}
		if (targetNode==null){
			System.err.println("TargetNode is not existing!");
			System.exit(0);
		}
		this.targetRVNode = targetNode;
		this.targetRV = nameRV;
	}
	
	public Node getTargetRVNode(){
		return this.targetRVNode;
	}
	
	public String getTargetRV(){
		return this.targetRV;
	}		
	
	public boolean addEvidence(Node node, String state) {
		boolean found = false;
		String[] values = node.getStates();
		double[] probs = new double[values.length];
		
		if (debug){
			System.out.println("ADD EVIDENCE(node, state) called. Node ="+node.getName());
			System.out.println("States: "+node.getStates());
			System.out.println("Evidence: "+state);
		}
		for (int i=0;i<values.length;i++){
			if (state.equals(values[i])){
				probs[i]=1;
				found = true;
				break;
			}
			else probs[i]=0;
		}
		
		if (debug)
		System.out.println("ADD EVIDENCE(node, state) called. FOUND="+found);
		
		
		if (found) return addEvidence(node, values, probs);
		else return false;
	}
	
	public boolean addEvidence(Node node, String[] values, double[] perCents) {
		//Node[] nArray = {node};
		ProbabilityDistribution nodeLikelihood = node.setObservation(values, perCents);
		if (nodeLikelihood == null){
			observedNodes.remove(node);
			return false;
		}

		if (!observedNodes.contains(node)) observedNodes.add(node);
		//Probability[] nodeProbs = nodeLikelihood.getProbabilities();
				
		return true;
	}
	
	public void removeAllEvidence(){
		for (Object node: observedNodes) ((Node) node).initializeObservation();
		
		observedNodes = new ArrayList<HasProbabilityTable>();
	}
	
	public String getJPD(int probabilityPosition){
		
		String stateWanted = null;
		
		Node targetNode = null; // targetNode will contain the node you want to inference. In our case, "activity"
		Node[] nodes = this.dagNetwork.getNodes();
		
		
		if(observedNodes.size()!=nodes.length-1){ //If not all the nodes except one have observations
			System.err.println("STILL NOT ALL THE NODES ARE OBSERVED!!");
			return null;
		}
		for(HasProbabilityTable n:observedNodes){
			if (((HasProbabilityTable) n).getName().equals(targetRV)){
				System.err.println("TARGET NODE IS OBSERVED!!");
				return null;
			}
		}
		for(Node n:nodes){
			if (n.getName().equals(targetRV)){
				targetNode=n;
				break;
			}
		}

		/*ALGORITHM: we are going to compute the JPD probability 
		 * To do that, we assume all the possible states of the target Random Variable,
		 * and introducing that evidences, we will check the probability tables of all the nodes
		 * in the network and multiplying their values.
		 */
		String[] possibleStatesTarget = targetNode.getStates();
		double universe;
		
		Hashtable<String, Double> universeProbabilityForStates = new Hashtable<String, Double>();/*<States,UniverseProbability>*/
		double[] originalOrder = new double[possibleStatesTarget.length];
		double[] sorted = new double[possibleStatesTarget.length];
		
		for(int i=0;i<possibleStatesTarget.length;i++){
			this.addEvidence(targetNode,possibleStatesTarget[i]);
			universe = this.computeJPD();
			universeProbabilityForStates.put(possibleStatesTarget[i],universe);
			originalOrder[i] = universe;
			sorted[i] = universe;
			this.removeEvidence(targetNode);			
		}
		Arrays.sort(sorted);
		if(debug){
			for (double d:sorted) System.out.println(d);
		}
				
		// find the probability
		if (probabilityPosition==0) probabilityPosition = 1;
		double searchedProb = sorted[sorted.length - probabilityPosition];
		int counter = 0;
		
		for(int i=0;i<possibleStatesTarget.length;i++){
			if(searchedProb==universeProbabilityForStates.get(possibleStatesTarget[i])){
				stateWanted = possibleStatesTarget[i];
				counter++;
			}
		}
		
		if (stateWanted==null || counter > 1) System.err.println("error with computing the Universe!!! stateWanted="+ stateWanted + " counter ="+counter);
		
		return stateWanted;
		
	}
	
	public Probability[] getUniverse(StateRV[] statesWanted){
		
		Node[] nodes = this.dagNetwork.getNodes();
		boolean stateExists = false;
		Probability[] probs = new Probability[statesWanted.length];
		double[] universesProbabilities = new double[statesWanted.length];
		
		
		if(observedNodes.size()!=nodes.length-1){ //If not all the nodes except one have observations
			System.err.println("STILL NOT ALL THE NODES ARE OBSERVED!!");
			return null;
		}
		for(HasProbabilityTable n:observedNodes){
			if (((HasProbabilityTable) n).getName().equals(targetRV)){
				System.err.println("TARGET NODE IS OBSERVED!!");
				return null;
			}
		}
		
		for(int j=0;j<statesWanted.length;j++){
			for (int i=0;i<this.targetRVNode.getStates().length;i++){// For all the states of the random variable
				String stateTargetNodeName = targetRVNode.getStates()[i];
				if(stateTargetNodeName.equalsIgnoreCase(statesWanted[j].getNameState())){
					stateExists = true;
					break;
				}
			}
			if(!(stateExists)){
				System.out.println("The state "+ statesWanted[j] +" is not a state of the target random variable!!!");
				return null;
			}
			stateExists = false;
		}				

		/*ALGORITHM: we are going to compute the JPD probability 
		 * To do that, we assume all the possible states of the target Random Variable,
		 * and introducing that evidences, we will check the probability tables of all the nodes
		 * in the network and multiplying their values.
		 */			
		
		for(int i=0;i<statesWanted.length;i++){
			
			this.addEvidence(targetRVNode,statesWanted[i].getNameState());
			universesProbabilities[i] = this.computeJPD();				
			this.removeEvidence(targetRVNode);		
			
		}
						
		//Now we normalized the probabilities if we want all the states:
		if(statesWanted.length == this.targetRVNode.getStates().length){
			double totalSumProbabilities = 0;
			for(int i = 0;i<universesProbabilities.length;i++){
				totalSumProbabilities += universesProbabilities[i];
			}
			
			for(int i = 0;i<universesProbabilities.length;i++){
				universesProbabilities[i] = universesProbabilities[i]/totalSumProbabilities;
			}
		}else{
			System.out.println("Not all the states were given!");
		}
		
		
		for(int i=0;i<statesWanted.length;i++){
			String states[] = new String[1];
			states[0] = statesWanted[i].getNameState();
		probs[i] = new Probability(states,universesProbabilities[i]);
		}
		return probs;
		
	}

	private void removeEvidence(Node node) {
		node.initializeObservation();
		observedNodes.remove(node);		
	}

	private double computeJPD() {
		double universe = 1;
		double universeTemporal = 0;
		double currentProbValue = 0;
		double temporal = 0;
		String[] hardEvidenceState = null;
		String[] temp = null;
		
		/*We dont check that all the nodes are observed because it is already checked in getUniverse method*/
		Node[] nodes = this.dagNetwork.getNodes();
		Node[] participants;
		
		Probability[] conditionalProbabilities = null;
		
		for(HasProbabilityTable n:nodes){			
			participants = ((Node) n).getParticipants(); //We get the node and its parents
			hardEvidenceState = new String[participants.length];
			for(int k=0;k<participants.length;k++){ //For all of them, we are going to collect the hard evidence, their current states
				hardEvidenceState[k] = participants[k].getHardEvidence();
			}
			conditionalProbabilities = n.getProbTable().getProbabilities(); //We have the conditional probability table in a vector
			for(int i=0;i<conditionalProbabilities.length;i++){//Now, we look for that parent configuration and that node state
				temp = conditionalProbabilities[i].getStates();
				if(temp.length!=hardEvidenceState.length){
					System.out.println("SOMETHING IS WRONG!! SEE COMPUTE UNIVERSE METHOD...");
				}
				if(Arrays.equals(temp,hardEvidenceState)){
					//We got the parent configuration and the node state value! :D
					currentProbValue = conditionalProbabilities[i].getProbability();
					break;
				}
			}
			temporal = Math.log10(currentProbValue);
			universeTemporal += temporal; //We add the log of the conditional probabilities given the evidences
			//universe *= currentProbValue; //We multiply all the conditional probabilities given the evidences of the network of all the nodes of the network
		}
		universe = Math.pow(10, universeTemporal);
				
		return universe;
	}
}
