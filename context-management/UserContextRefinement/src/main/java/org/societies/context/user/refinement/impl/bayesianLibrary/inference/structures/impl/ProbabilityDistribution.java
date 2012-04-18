package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.interfaces.HasProbabilityTable;


/**
 * @author fran_ko
 * 
 * //TODO probTableCheck implementieren: in pureProbs einfach, im Probability[] weniger, mal schaun. 
 */
public class ProbabilityDistribution implements Serializable{

	 private HasProbabilityTable owner;
	 private Probability[] allCases;
	 private static Logger logger = LoggerFactory.getLogger(ProbabilityDistribution.class);

	/**
	 * @param node 
	 * @param twoDimensionalFixedOrder
	 */
	public ProbabilityDistribution(HasProbabilityTable node, Probability[] twoDimensionalFixedOrder) {
		owner = node;
		allCases = twoDimensionalFixedOrder;
	}

	/**
	 * @param pureProbs
	 */
	public ProbabilityDistribution(Node node, double[] pureProbs) {
		owner = node;
		allCases = new Probability[pureProbs.length];

		ArrayList<Node> predecessors = new ArrayList<Node>();
		for (int i=0; i<((Node)owner).getIncoming().size();i++){
			Edge e = (Edge)((Node)owner).getIncoming().get(i);
			predecessors.add(e.source);
		}
		
		//******************* CREATION OF THE CROSS PRODUCT ******************
		String[] temp = new String[predecessors.size()+1];
		
		ArrayList<String[]> states = new ArrayList<String[]>();
		for (int i=0; i<((Node)owner).getStates().length;i++){
			String state = (String)((Node)owner).getStates()[i];
			temp[0] = state;
			if (predecessors.size()!=0) createStatePermutations(predecessors,states,temp,1);
			else states.add(temp.clone());
		}
		//*********************************************************************
		
		for (int i=0; i<pureProbs.length;i++)
			allCases[i] = new Probability((String[])states.get(i), pureProbs[i]);
	}

	/*
	 * needed for ProbabilityDistributionBL:ProbabilityDistributionBL(HasProbabilityTable)
	 */
	public ProbabilityDistribution() {
	}

	public static void createStatePermutations(List<Node> nodes, ArrayList<String[]> states, String[] temp, int position){
		if (nodes.size()==0) return;
//		System.out.println("called with Position: "+position + " First and last nodes are: " + ((Node)nodes.get(0)).getName() + " " + ((Node)nodes.get(nodes.size()-1)).getName());
		
		for (int i=0; i<(nodes.get(0)).getStates().length;i++){
			String s = (String)((Node)nodes.get(0)).getStates()[i];
			temp[position] = s;
			if (nodes.size()<=1){
				states.add(temp.clone());
/*				String lang = "";
				for(String str: temp) lang+=str+"\t";
				System.out.println(lang);
*/			}
			else {
				ArrayList<Node> eins = new ArrayList<Node>(nodes);
				ArrayList<Node> reduced = (ArrayList<Node>) eins.clone();
				reduced.remove(0);
				createStatePermutations(reduced,states,temp,position+1);
			}
		}
/*		String knoten = "";
		for (Node n: nodes) knoten+=n.getName() + " ";
		System.out.println("Rekursiver aufruf beendet. Beteiligte Knoten waren: "+knoten);
*/
	}
	
	public String toString()
	{
		String back = "Probability distribution of Node " + owner.getName() +":\n\n";

		for (int i=0; i<owner.getParticipants().length;i++){
			back += owner.getParticipants()[i].getName() + "\t\t";
		}
		back += "|\tProbability\n\n";
		
		for (int i=0; i<allCases.length;i++){
			Probability pb = (Probability)allCases[i];
			back += pb + "\n";
		}
		return back;
	}

	/**
	 * @return
	 */
	public Probability[] getProbabilities() {

		return allCases;
	}

	/**
	 * A method to find all indices in this object's probability table that match with the value-configuration passed as parameters
	 * 
	 * @param participants the family of the node, whose table are to be matched
	 * @param states: 1 Combination of values of a node's potential that is to be found in the clique's (clique = this) Potential
	 * @return
	 */
	public int[] fitsIndex(Node[] participants, String[] states) {
		
		Node[] representedInClique = owner.getParticipants();
		ArrayList<Node> riC_list = new ArrayList<Node>(Arrays.asList(representedInClique));
		ArrayList<Node> nodepart_list = new ArrayList<Node>(Arrays.asList(participants));
		
		
		if (!riC_list.containsAll(nodepart_list)){
			logger .error("ProbabilityDistribution: fitsIndex: Node-family NOT CONTAINED IN CLIQUE!");
			return null;
		}
		
		ArrayList<Integer> results = new ArrayList<Integer>();
		
		int[] positions = new int[participants.length];
		for (int i=0;i<positions.length;i++) positions[i]= riC_list.indexOf(nodepart_list.get(i));
		
		
		/*
		 * 'Brute' method - indices are searched, not calculated.
		 * number of state * 'Wertigkeit' der stelle [= wieviele werte die hinteren variablen haben]
		 */
		for (int i=0;i<allCases.length;i++)
		{
			int equals = 0;
			String[] configuration = allCases[i].getStates();
			
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
	
}
