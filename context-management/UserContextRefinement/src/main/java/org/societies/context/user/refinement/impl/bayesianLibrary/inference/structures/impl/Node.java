package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import java.util.ArrayList;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.ConnectingNodes;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;


/**
 * @author fran_ko
 *
 */
public class Node implements Comparable, HasProbabilityTable {
	
	private ArrayList incoming = new ArrayList();
	private ArrayList outgoing = new ArrayList();
	private ArrayList undirectedConnections = new ArrayList();
	private Clique parentClique = null;
	
	protected String name;
	protected String[] states;
	private ProbabilityDistribution prob;
	private ProbabilityDistribution marginalization;
	private ProbabilityDistribution likelihood;
	private boolean unobserved;
	

	private static Logger logger = LoggerFactory.getLogger(Node.class);
	
	public Node(String variableName, String[] states){
		name = variableName;
		this.states = states;
	}
	
	/**
	 * FOR USE IN CLIQUE;
	 *
	 */
	public Node(){		
	}
	
	/**
	 * Core idea: Run through the marginalisation. with hard evidence, only one value can be above 0.
	 * -> a positive value is marked, the second positive value already returns false then.
	 * 
	 * @return
	 */
	public boolean hasHardEvidence() {
		boolean onePositive = false;
		Probability[] marginalizedProbs = marginalization.getProbabilities();
		for(Probability p: marginalizedProbs)
		{
			if (p.getProbability()>0)
				if (onePositive) return false;
				else onePositive = true;
		}
		if (!onePositive) logger.error("All values are 0!");
		return onePositive;
	}

	public void setName(String name){
		this.name=name;
	}
	public void setProbDistribution(Probability[] twoDimensionalFixedOrder){
		int testLength = 1;
		for(int i=0; i<getParticipants().length;i++){
			testLength*=getParticipants()[i].countStates();
		}
		
		if (twoDimensionalFixedOrder!=null && 
				twoDimensionalFixedOrder.length == testLength &&
					twoDimensionalFixedOrder[0].getStates().length == (getParticipants().length))
			prob = new ProbabilityDistribution(this, twoDimensionalFixedOrder);
		else
		{ 
			logger .error("Node: setProbDistribution(Probability[]): Probability Table does not fit the structure of the network");
		
//			System.out.println(twoDimensionalFixedOrder.length);
		}
	}
	

	public void setProbDistribution(double[] pureProbs){
		int testLength = countStates();		
		for (int i=0; i<incoming.size();i++){
			Edge e = (Edge)incoming.get(i);
			testLength*=e.source.countStates();
		}
		
		if (pureProbs!=null && 
				pureProbs.length == testLength)
			{
				prob = new ProbabilityDistribution(this, pureProbs);
			}
		else{
			/*	Testing:
			 *	System.err.println(pureProbs + ", Lange: "+pureProbs.length+ ", #parents: "+incoming.size()+", testLength="+testLength); 
			 */
			logger.error("Probability Table does not fit the structure of the network");
		}
	}
	
	public ArrayList getIncoming(){
		return incoming;
	}
	
	public ArrayList getOutgoing(){
		return outgoing;
	}
	
	public ArrayList getUndirectedEdges(){
		return undirectedConnections;
	}

	public String getName(){
		return name;
	}
	

	public String[] getStates(){
		return states;
	}
	
	/**
	 * also known as WEIGHT of this node
	 * @return
	 */
	public int countStates(){
		return states.length;
	}
	
	/* (non-Javadoc)
	 * @see structures.ProbabilityTable#getProbTable()
	 */
	public ProbabilityDistribution getProbTable() {
		return prob;
	}

	/**
	 * @param dag
	 */
	public void updateProbTable(DAG dag) {
		// TODO Implementation of an updated Probability Table
		
	}

	/**
	 * @param e
	 */
	public void addIncoming(Edge e) {
		incoming.add(e);
	}

	/**
	 * @param e
	 */
	public void addOutgoing(Edge e) {
		outgoing.add(e);		
	}

	/**
	 * @param e
	 */
	public void removeIncoming(Edge e) {
		incoming.remove(e);
	}	/**
	 * @param e
	 */
	public void addConnection(ConnectingNodes e) {
		undirectedConnections.add(e);		
	}

	/**
	 * @param e
	 */
	public void removeConnection(ConnectingNodes e) {
		undirectedConnections.remove(e);
	}

	/**
	 * @param e
	 */
	public void removeOutgoing(Edge e) {
		outgoing.remove(e);
	}
	
	public String toString(){
		return name;
	}
	
	/**
	 * @return this node and all nodes connected to him
	 
	public Node[] getNeighbours(){
		Node[] result = new Node[1+incoming.size()+outgoing.size()+undirectedConnections.size()];
		
		int j = 0;
		result[j++] = this;
		
		for (int i=0;i<incoming.size();i++)
		{
			Node n = ((Edge) incoming.get(i)).getSource();
			result[j++]=n;
		}
		
		for (int i=0;i<outgoing.size();i++)
		{
			Node n = ((Edge) outgoing.get(i)).getTarget();
			result[j++]=n;
		}
		
		for (int i=0;i<undirectedConnections.size();i++)
		{
			UndirectedEdge e = (UndirectedEdge) undirectedConnections.get(i);
			Node n = (e.getBorder1().equals(this))? e.getBorder2() : e.getBorder1();
			result[j++]=n;
		}
		
		return result;
	}
*/
	
	/**
	 * @return all nodes connected to this node (This node NOT included)
	 */
	public Node[] getNeighbours(){
		Node[] result = new Node[incoming.size()+outgoing.size()+undirectedConnections.size()];
		
		int j = 0;
		
		for (int i=0;i<incoming.size();i++)
		{
			Node n = ((Edge) incoming.get(i)).getSource();
			result[j++]=n;
		}
		
		for (int i=0;i<outgoing.size();i++)
		{
			Node n = ((Edge) outgoing.get(i)).getTarget();
			result[j++]=n;
		}
		
		for (int i=0;i<undirectedConnections.size();i++)
		{
			ConnectingNodes e = (ConnectingNodes) undirectedConnections.get(i);
			Node n = (e.getBorder1().equals(this))? e.getBorder2() : e.getBorder1();
			result[j++]=n;
		}
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 */
	public int compareTo(Object arg0) {
		return name.compareTo(((Node)arg0).getName());
	}

	/** 
	 * The Node itself + its parents
	 * 
	 * @see de.dlr.kn.bayesianLibrary.inference.structures.interfaces.personalsmartspaces.cm.reasoning.bayesian.structures.HasProbabilityTable#getParticipants()
	 */
	public Node[] getParticipants() {
		Node[] result = new Node[1+incoming.size()];
		
		result[0] = this;
		
		for (int i=0;i<incoming.size();i++)
		{
			Node n = ((Edge) incoming.get(i)).getSource();
			result[i+1]=n;
		}
		
		return result;
	}

	/**
	 * assigns the family of this node to a clique that is representing it
	 * 
	 * @param cliques - all available cliques/clusters
	 */
	public Clique assignClique(ArrayList cliques) {

		ArrayList family = new ArrayList(Arrays.asList(getParticipants()));
		for (int i=0;i<cliques.size();i++)
		{
			Clique current = (Clique)cliques.get(i);
			ArrayList cliqueMembers = new ArrayList(Arrays.asList(current.getParticipants()));
			if(cliqueMembers.containsAll(family)){
				parentClique = current;
//				System.out.println("Node: " + name + ", parent Clique: " + parentClique);
				return current;
			}
		}
		logger.error("Node:assignClique(ArrayList): run through all cliques, but no parent one for Node "+this);
		return null;
	}
	
	public Clique getParentClique(){
		return parentClique;
	}

	/**
	 * @param distribution constructed by the method call marginalize(Node n)
	 * @see org.personalsmartspaces.cm.reasoning.bayesian.solving.JunctionTree#marginalize(Node n)
	 */
	public void setMarginalization(ProbabilityDistribution distribution) {
		marginalization = distribution;
	}
	
	/**
	 * @return marginalisation
	 */
	public ProbabilityDistribution getMarginalization() {
		return marginalization;
	}

	
	public String printMarginalization(){
		if (marginalization == null) return null;
		return marginalization.toString();
	}
	

	/**
	 * Sets the Likelihood-Table of this node to 1.
	 * Likelihood is represented by the private instance field "likelihood" and
	 * represented as a ProbabilityDistribution, for each state is assigned a Probability
	 */
	public void initializeObservation() {
		Probability[] neu = new Probability[states.length];
		for(int i=0;i<neu.length;i++){
			String[] temp = {states[i]};
			neu[i] = new Probability(temp,1);
		}
		
		likelihood = new ProbabilityDistribution(this, neu);
		unobserved = true;
	}
	
	public ProbabilityDistribution setObservation(String[] states, double[] probabilities){
		unobserved = true;
		if (!(states.length==probabilities.length)){
			logger.error("Node: SetObservation - Wrong Data");
			return null;
		}
		double probSum = 0;
		for (int i=0;i<probabilities.length;i++){ 
			probSum+=probabilities[i];
			if (probabilities[i]!=1) unobserved=false;
		}
		if (unobserved) return null;
		
		if (probSum!=1){		//normalizing the probability vector so that sum = 1
			double quotient = 1 / probSum;
			for (int i=0;i<probabilities.length;i++){ 
				probabilities[i]*=quotient;
			}
		}
		
		/* 
		 * Lambda_V = Lambda_V * Lambda_V^new
		 */
		Node[] self = {this};
		for (int i=0;i<states.length;i++){		
			String[] state_array = {states[i]};
			int[] toChange = likelihood.fitsIndex(self, state_array);

			if (!(toChange.length==1)){
				logger.error("Node: SetObservation - Value unknown; toChange.length="+toChange.length + " Node: "+name);
				logger.error("State_Array = ["+states[i]+"]");
				logger.error(likelihood.toString());
				for (int j=0;j<toChange.length;j++) logger.error(toChange[j]+"");
			}
			
			Probability change = likelihood.getProbabilities()[toChange[0]];
			change.multiplyProbability(probabilities[i]);
		}
		
		return likelihood;
	}
	

	public void setObservation(ProbabilityDistribution readyList){
		likelihood = readyList;
	}
	
	public ProbabilityDistribution getObservation(){
		return likelihood;
	}

	//ADDED by Maria:
	public String getHardEvidence(){
		String state;
		int index = 0;		
		int counter = 0;
		
		for(int i=0;i<this.likelihood.getProbabilities().length;i++){		
			if(likelihood.getProbabilities()[i].getProbability()==1){
				counter++;
				index = i;
			}
		}
		if (counter>1){
			System.out.println("ERROR!! The node was not observed or it does not have a HARD EVIDENCE!!");
			return null;
		}
		state = this.getStates()[index];		
		return state;
	}
	//End added by Maria
}
