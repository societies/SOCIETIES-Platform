package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;

import java.util.ArrayList;
import java.util.Arrays;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.Util;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.ProbabilityDistribution;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;




/**
 * @author gall_pa, fran_ko, fort_se
 */
public class ProbabilityDistributionBL extends ProbabilityDistribution{

	 private HasProbabilityTable owner;
	 private Probability[] allCases;


	/**
	 * @param node
	 */
	public ProbabilityDistributionBL(HasProbabilityTable node) {
		super();
		setOwner(node);
	}

	public ProbabilityDistributionBL(HasProbabilityTable owner2,
			Probability[] newProbs) {
		super(owner2, newProbs);
	}			
	/**
	 * This method combines the probabilities tables from two nodes. It is used when interface nodes are combined.
	 */
	public void combineProbabilityDistribution(Node sourceNode, Node targetNode){
		
		Probability[] temp=getAllCases();
		Probability[] inputProbabilities=sourceNode.getProbTable().getProbabilities();
		int expandConstant=Util.getNumberStates(Util.getParents(sourceNode.getIncoming()));
		int initialNumberCases=Util.getNumberStates(Util.getParents(targetNode.getIncoming()));
		
		if (initialNumberCases==0){   
			
		//In this case nodeTarget has no parents. The probabilities from nodeSource will be copied 
		//to nodeTarget.  
			this.setAllCases(inputProbabilities);
			
		}else if((initialNumberCases!=0)&&(expandConstant!=0)){
			// both targetNode and sourceNode have  parents
			int cont=0;
			int newProbabilityLength=(temp.length)*expandConstant;
			int index=0;
			int writingIndex=0;
			int inputIndex=0;
			int inputIndexStartingPosition=0;
			//this.expandProbTable(expandConstant);
			setAllCases(new Probability[newProbabilityLength]);
			
		/* The next while structure provides with an extension of the original probability distribution
		 * conformed by a number of copies of it determined by expandConstant.
		 */
			
			while (index<temp.length){
				for (int j=0;j<expandConstant;j++){
					getAllCases()[writingIndex+j]=temp[index];
				}
				index++;
				writingIndex=writingIndex+expandConstant;
			}
			index=0;
		
			
		/* The next while structure updates the probability distribution with the states and the probabilities
		 * provided by the probability distribution to be combined.
		 */
			
			while(index<newProbabilityLength){
				
				double tempProbability=getAllCases()[index].getProbability();
				double tempInputProbability=inputProbabilities[inputIndex].getProbability();
				String[] tempInputStates=inputProbabilities[inputIndex].getStates();
				String[] tempStates=getAllCases()[index].getStates();
				String[] newStates=new String[tempStates.length+tempInputStates.length-1];
						
				for (int i=0;i<tempStates.length;i++){    //Original states                                         
					newStates[i]=tempStates[i];
				}
				
				for (int i=0;i<tempInputStates.length-1;i++){       //Adding new states                         
					newStates[i+tempStates.length]=tempInputStates[1+i];
				}
							
				tempProbability=(tempProbability)*(tempInputProbability);
				getAllCases()[index]=new Probability(newStates,tempProbability);
				inputIndex++;
				index++;
								
				if ((inputIndex%expandConstant)==0){      // Updating reading and writing indexes
					cont++;
				
					if (cont==initialNumberCases){
						inputIndexStartingPosition=inputIndexStartingPosition+expandConstant;
						cont=0;
					}
					
					inputIndex=inputIndexStartingPosition;
				}
			}
			
			//System.out.println("Before normalizing");
			//System.out.println(this.toString());
			this.normalize();
		}

	}


	/**
	 * This method combines two probabilities tables. It is used when combining nodes with common parents. The tables must have been previously reordered, so
	 * the common parents are just after the owner node from the respective probabilities table.
	 * @param input Node whose probabilities table we want to combine with this one.
	 * @param expand Number of combinations of the incoming edges of node input that are related to parents different from the common ones.
	 * @param position Position where the non-common parents start in the probabilities table.
	 */
	public void combineTable (Node input, int expand, int position){
		int indexTable=0;
		int indexInput=0;
		int cont=0;
		int casesR=((NodeBL)owner).getRightCases(0);
		
		int startingPositionInput=0;
		Probability[] inputP=input.getProbTable().getProbabilities();
	
		while (indexTable<this.getAllCases().length){
						
				String[] tempStates=inputP[indexInput+startingPositionInput].getStates();
				double tempInputProb = inputP[indexInput+startingPositionInput].getProbability();
				ArrayList<String[]> tempStatesArrayList=Util.arrayToArrayList(tempStates);
				for (int j=0;j<=position;j++){
					tempStatesArrayList.remove(0);
				}
				ArrayList<String[]> oldStates=Util.arrayToArrayList(getAllCases()[indexTable].getStates());
				oldStates.addAll(tempStatesArrayList);
				
				getAllCases()[indexTable].setStates(Util.convertToArrayOfStrings(oldStates));
				getAllCases()[indexTable].setProbability(getAllCases()[indexTable].getProbability()*tempInputProb);	
					
				indexInput++;
				
				if (indexInput==expand){
					
					cont++;
					indexInput=0;
					if (cont==casesR){
						startingPositionInput=startingPositionInput+expand;
						cont=0;
					}
				}
				indexTable++;
		}
		
		
		this.normalize();
	}
	
	/**
	 * Normalizes the probabilities table, so the sum of probabilities of every possible state from the
	 * owner node is one.
	 *
	 */
	public void normalize(){
		double sum=0;
		int jumpIndex=0;
		int statesNumber=((Node)owner).countStates();
		int index=0;
		while(index<(getAllCases().length/statesNumber)){
		
			
			sum=0;	
			for (jumpIndex=0;jumpIndex<statesNumber;jumpIndex++){
			
				int pos=index+(jumpIndex*(getAllCases().length/statesNumber));
				sum=sum+getAllCases()[pos].getProbability();
			
			}
		
			for (jumpIndex=0;jumpIndex<statesNumber;jumpIndex++){
			
				int pos2=index+(jumpIndex*(getAllCases().length/statesNumber));
				double tempProbability=getAllCases()[pos2].getProbability();
			
				tempProbability=(tempProbability)/sum;
			
				getAllCases()[pos2]=new Probability(getAllCases()[index+(jumpIndex*(getAllCases().length/statesNumber))].getStates(),tempProbability);
			}
					
			index++;
		}
	}
	
	
	public String toStringWithoutParents()
	{
		//String back = "Marginalization of Node " + owner.getName() +":\n\n";
		
		//back += "\t\t\tProbability\n\n";
		String back="";
		
		for (int i=0; i<getAllCases().length;i++){
			Probability pb = (Probability)getAllCases()[i];
			back += pb + "\n";
		}
		return back;
	}
	
	/**
	 * This method provides with an extension of the original probability distribution
	 * conformed by a number of copies of it determined by expandConstant.
	 * @param expandConstant
	 */
	public void expandProbTable(int expandConstant){
		Probability[] temp=getAllCases();
		int newProbabilityLength=(this.getProbabilities().length)*expandConstant;
		setAllCases(new Probability[newProbabilityLength]);
		int index=0;
		int writingIndex=0;
	
		while (index<temp.length){
			for (int j=0;j<expandConstant;j++){
				getAllCases()[writingIndex+j]=(Probability) temp[index].clone();
			}
			index++;
			writingIndex=writingIndex+expandConstant;
		}
		
	}
	
	///////////////////////////////////////////////////////////////////////////////////////////////	
	//////////////////////Added by Sergio//////////////////////////////////////////////////////////
	///////////////////////////////////////////////////////////////////////////////////////////////

	
	public Object clone(){
		Probability[] newProbs = new Probability[this.getAllCases().length];
		for (int i=0; i<this.getAllCases().length; i++){
			newProbs[i]=(Probability) this.getAllCases()[i].clone();
		}
		ProbabilityDistributionBL neu =  new ProbabilityDistributionBL(this.owner, newProbs);
		return neu;
	}
	
	/**
	 * This methods return the Probability of the most probability state.
	 * It is useful for likelihood, marginalization, accumulative inference,
	 *  ...
	 * @return
	 */
	public Probability getMostProbable(){
		Probability mostProbable= this.getProbabilities()[0];
		for (int k=0; k<this.getProbabilities().length; k++){
			if (this.getProbabilities()[k].getProbability()>mostProbable.getProbability()){
				mostProbable=this.getProbabilities()[k];
			}
		}
		return mostProbable;
	}
	
	/**
	 * A method to find all indices in this object's probability table that match with the value-configuration passed as parameters
	 * !!Only ok if the order have been saved.
	 * @param participants the family of the node, whose table are to be matched
	 * @param states: 1 Combination of values of a node's potential that is to be found in the clique's (clique = this) Potential
	 * @return
	 */
	public int[] fitsIndexJointTables(Node[] participants, String[] states) {
		
		Node[] representedInClique = getOwner().getParticipants();
		ArrayList<Node> riC_list = new ArrayList<Node>(Arrays.asList(representedInClique));
		ArrayList<Node> nodepart_list = new ArrayList<Node>(Arrays.asList(participants));
		
		
		if (!riC_list.containsAll(nodepart_list)){
			System.err.println("ProbabilityDistribution: fitsIndex: Node-family NOT CONTAINED IN CLIQUE!");
			return null;
		}
		
		
		//Calculate Positions
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		int[] positions = new int[participants.length];
		
		//The positions must be the same that the order of the provided nodes
		
		if (riC_list.size()>=participants.length){
			for (int i=0;i<positions.length;i++) positions[i]=i;
		}
		else {
			System.err.println("> fitsIndexJointTable : State configuration so much longer for this probTable");
		}
		
		
		/*
		 * 'Brute' method - indices are searched, not calculated.
		 * number of state * 'Wertigkeit' der stelle [= wieviele werte die hinteren variablen haben]
		 */
		for (int i=0;i<getAllCases().length;i++)
		{
			int equals = 0;
			String[] configuration = getAllCases()[i].getStates();
			
			for (int j=0;j<states.length;j++){
				if (!states[j].equals(configuration[positions[j]])) break;
				else equals++;
			}
			if (equals==states.length) results.add(new Integer(i));//[resultindex++]=i;
		}
		
		int[] resultarray = new int[results.size()];
		for (int i=0;i<resultarray.length;i++) resultarray[i]=((Integer)results.get(i)).intValue();
		return resultarray;
	}

	public void setOwner(HasProbabilityTable owner) {
		this.owner = owner;
	}

	public HasProbabilityTable getOwner() {
		return owner;
	}

	public void setAllCases(Probability[] allCases) {
		this.allCases = allCases;
	}

	public Probability[] getAllCases() {
		return allCases;
	}


	
}
