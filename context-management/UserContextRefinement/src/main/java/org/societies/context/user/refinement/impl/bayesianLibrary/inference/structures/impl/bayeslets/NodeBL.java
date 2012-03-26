package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;
import java.util.ArrayList;
import java.util.Arrays;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.Util;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;



/**
 * @author gall_pa
 *
 */
public class NodeBL extends Node implements Comparable {
	
	private ArrayList incoming = new ArrayList();
	private ArrayList outgoing = new ArrayList();
	private ArrayList undirectedConnections = new ArrayList();
	private CliqueBL parentClique = null;
	
	protected String name;
	protected String[] states;
	private ProbabilityDistributionBL prob;
	private ProbabilityDistributionBL marginalization;
	private ProbabilityDistributionBL likelihood;
	

	
	
	
	public NodeBL(String variableName, String[] states){
		name = variableName;
		this.states = states;
	}
	
	/**
	 * FOR USE IN CLIQUE;
	 *
	 */
	public NodeBL(){		
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////Added by Pablo//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * This method combines the name of the node with the parameter string.
	 * It is used to store the name of those nodes whose combination gives as result the current node.
	 */
	public void updateName(String string) {
		this.name=this.name+"+"+string;
	}
	
	/**
	 * This method returns the position in the ArrayList containing all incoming edges of the incoming edge whose source is the parameter inputNode.
	 * ////////////Removing influence of a common parent////////////
	 * @param inputNode
	 * @return
	 */	
	public int getIncomingEdgePosition(NodeBL inputNode){
		int indexEdges=0;
		boolean founded=false;
	
		// Search the edge that connects inputNode with this one.
	
		while ((!founded)&&(indexEdges<this.incoming.size())){
			Edge tempEdge=(Edge) this.incoming.get(indexEdges);
			if (tempEdge.getSource()==inputNode){
				founded=true;
			}else{
				indexEdges++;
			}
		}
		return indexEdges;
	}
	
	/**
	 * This method returns the combination of states of all nodes places at the right side from the node placed in position nodeStatePosition
	 * at the probabilities table from the node.
	 * 
	 * @param inputNode
	 * @param nodeStatePosition
	 * @return
	 */
	public int getRightCases(int incomingEdgePosition){
		int toReturn=1;
		int indexEdges=incomingEdgePosition+1;
				
		// Calculate the combination of states of nodes at the right side of inputNode.
		
		while (indexEdges<this.incoming.size()){
			toReturn*=((Edge)this.incoming.get(indexEdges)).getSource().countStates();
			indexEdges++;
		}
		
		return(toReturn);
		
		
	}
	
	/**
	 * This method moves the incoming edge placed in statePos-1
	 * in the incoming edges arraylist to position 0 in that array.
	 *  
	 * @param statePos Position of the incoming edge to be moved.
	 */
	public void moveIncomingEdgePosition(int edgePos){
		ArrayList tempEdges=this.getIncoming();
		Edge tempE=(Edge) tempEdges.get(edgePos);
		tempEdges.remove(edgePos);
		tempEdges.add(0, tempE);
		
	}

	
	/**
	 * This method re-orders a probabilities table in order to have a certain variable listed just after the
	 * variable that owns the table.
	 * 
	 * @param input It is the table to be modified.
	 * @param casesRight It is the combination of the different states the variable to be moved has on its right side.
	 * In case the variable to be moved is at the last position (top right in the table), casesRight has to be assigned with 
	 * the value 1.
	 *  * @param statesCommonParent It is the number of different states of the variable to be moved.
	 * @param statesCombinedNode It is the number of different states of the variable that owns the table. 
	 * 	 
	 */
	
	/* This is an example of what this method does. 
	 * The table on the right side shows the result of moving variable C in the table on the left side.
	*	Table Configuration Example                 Table After Moving C
	*	A  B  C   Probability						A  C  B   Probability
	*	S1 S1 S1		P1							S1 S1 S1 	 P1					
	*	S1 S1 S2 		P2							S1 S1 S2	 P3
	*	S1 S2 S1		P3							S1 S1 S3 	 P5								
	*	S1 S2 S2 		P4							S1 S2 S1	 P2
	*	S1 S3 S1 		P5							S1 S2 S2 	 P4
	*	S1 S3 S2 		P6							S1 S2 S3	 P6
	*	S2 S1 S1 		P7							S2 S1 S1	 P7					
	*	S2 S1 S2 		P8							S2 S1 S2     P9
	*	S2 S2 S1 		P9							S2 S1 S3     P11
	*	S2 S2 S2 		P10							S2 S2 S1     P8
	*	S2 S3 S1 		P11							S2 S2 S2     P10
	*	S2 S3 S2 		P12							S2 S2 S3     P12

	*/
	
	public void moveVariableInTable(int casesRight, int statesCommonParent,int statesCombinedNode,int statePosition){
		
		Probability newProbTable[]=new Probability[this.getProbTable().getProbabilities().length];
		int totalCases=this.getProbTable().getProbabilities().length/statesCombinedNode;
		int index1=0;
		int jump=statesCommonParent*casesRight;
		boolean finished=false;
		int cont=0;
		int startingPosition=0;
		int index2=0;
		int cont2=0;
	
		while (!finished){
			
			index1=0;
			
			while (index1<totalCases){
					
				for (int j=0;j<casesRight;j++){
					double tempProb=this.getProbTable().getProbabilities()[index2+j].getProbability();
					Object[] newOrder = this.getProbTable().getProbabilities()[index2+j].getStates();
					ArrayList tempArrayList=Util.arrayToArrayList(newOrder);
					String temp=(String) tempArrayList.get(statePosition);
					tempArrayList.remove(statePosition);
					tempArrayList.add(1, temp);
									
				   newOrder=Util.convertToArrayOfStrings(tempArrayList);
					
				    newProbTable[j+index1+startingPosition]=new Probability((String[]) newOrder,tempProb);
				}
				index1=index1+casesRight;
				index2=index2+jump;
				
				if ((index2)>=(totalCases*(1+cont))){
					cont2++;
					index2=startingPosition+(cont2*casesRight);
					
				}
			}
			cont++;
		
			if (cont<statesCombinedNode){
				startingPosition=startingPosition+totalCases;
				index2=startingPosition;
				cont2=0;
			}else{
				finished=true;
			}
					
		}
		
		this.setProbDistribution(newProbTable);
		
	}
	/**
	 * This method reorders the probability table of its owner taking Node node to the second position (just after the owner), 
	 * making the necessary changes to do it.
	 * @param node 
	 */
	public void reorderProbTable(NodeBL node){
		int edgePos=this.getIncomingEdgePosition(node);
		int casesRight=this.getRightCases(edgePos);
		// Position in the table is (position in the incoming edges ArrayList +1), because the probabilities table also
		// includes its owner.
		this.moveVariableInTable(casesRight, node.countStates(), this.countStates(),edgePos+1);
		this.moveIncomingEdgePosition(edgePos);
	}
	
///////// Modification of method assignClique to consider node combined names
	/**
	 * This method is equivalent to assignClique (see above) but it takes into consideration the possibility of having
	 * combined nodes into the clique (whose names will be the combination of the names of the nodes that were combined)
	 */
	public CliqueBL assignCliqueCombinedNames(ArrayList cliques) {
		boolean fits=true;
		int indexFamily=0;
		ArrayList family = new ArrayList(Arrays.asList(getParticipants()));
		for (int i=0;i<cliques.size();i++)
		{
			fits=true;
			CliqueBL current = (CliqueBL)cliques.get(i);
			while ((fits)&&(indexFamily<family.size())){
				String wanted= ((NodeBL)family.get(indexFamily)).getName();
				if (current.getName().contains(wanted)){
					indexFamily++;
				}else{
					fits=false;
					indexFamily=0;
				}
			}
			if (fits){
				parentClique = current;
//				System.out.println("Node: " + name + ", parent Clique: " + parentClique);
				return current;
			}
		}	
		System.err.println("Node:assignClique(ArrayList): run through all cliques, but no parent one for Node "+this);
		return null;
	}
	
	public void setParentClique(CliqueBL input){
		this.parentClique=input;
	}

	public String printMarginalizationWithoutParents(){
		return(this.marginalization.toStringWithoutParents());
	}

	public void setName(String input){
		this.name=input;
	}

///////////////////////////////////////////////////////////////////////////////////////////////	
	//////////////////////Added by Sergio//////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	/**
	* This method returns the correct length for a node Probability Distribution.
	*   
	* @return 	correct length
	*/
	public int getCorrectProbDistrLength() {
	
		int testLength = countStates();
		
		for (int i=0; i<incoming.size();i++){
			Edge e = (Edge)incoming.get(i);
			testLength*=e.getSource().countStates();
		}
		
		return testLength;
	
	}
	
	public NodeBL[] getParents() {
		NodeBL[] result = new NodeBL[incoming.size()];
		for (int i=0;i<incoming.size();i++){
			Node n = ((Edge) incoming.get(i)).getSource();
			if (n instanceof NodeBL)
				result[i]= (NodeBL)n;
		}
		return result;
	}
	
	
	public ArrayList<NodeBL> getParentsArrayList() {
		ArrayList<NodeBL> result = new ArrayList<NodeBL>();
		for (int i=0;i<incoming.size();i++){
			Node n = ((Edge) incoming.get(i)).getSource();
			if (n instanceof NodeBL)
				result.add((NodeBL)n);
		}
		return result;
	}
	
	
	
	public ArrayList<NodeBL> getParticipantsArrayList() {
		ArrayList<NodeBL> result = new ArrayList<NodeBL>();
		result.add(this);
		for (int i=0;i<incoming.size();i++){
			Node n = ((Edge) incoming.get(i)).getSource();
			if (n instanceof NodeBL)
				result.add((NodeBL)n);
		}
		return result;
	}

	public ArrayList<NodeBL> getChildrensArrayList() {
		ArrayList<NodeBL> result = new ArrayList<NodeBL>();
		for (int i=0;i<outgoing.size();i++){
			Node n = ((Edge) outgoing.get(i)).getTarget();
			if (n instanceof NodeBL)
				result.add((NodeBL)n);
		}
		return result;
	}

	public void setProbTable(ProbabilityDistributionBL prob) {
		this.prob = prob;
	}

	public void setLikelihood(ProbabilityDistributionBL likelihood) {
		this.likelihood = likelihood;
	}

	public ProbabilityDistributionBL getLikelihood() {
		return likelihood;
	}


	


	
}
