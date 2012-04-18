package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;

import java.util.ArrayList;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils.Util;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Clique;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
/**
 * @author gall_pa
 *
 */
public class CliqueBL extends Clique {

	public CliqueBL(Node[] nodes) {
		super(nodes);
	}

	NodeBL[] representedNodes;
	boolean marked = false;
	
///////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////Added by Pablo//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////	
	
	/**
	 * This method adds some nodes to the clique.
	 * It also updates its name and its states.
	 */

	 public void addNodes(ArrayList<NodeBL> input){
		
		representedNodes=new NodeBL[input.size()];
		representedNodes=Util.convertToArray(input);
	
		representedNodes = (NodeBL[])sort(representedNodes);
		
		String name = "";
		ArrayList<String> states = new ArrayList<String>();
		
		for(int i=0; i<representedNodes.length;i++){
			NodeBL n = representedNodes[i];
			name += n.getName();
			for(int j=0; j<n.getStates().length;j++){
				String s = n.getStates()[j];
				states.add(s);
			}
		
		}
				
		super.name = name;
		super.states = (String[]) states.toArray(new String[0]);
	}
	
	/**
	 * This method updates the nodes from the clique, and updates its name.
	 * @param nodes
	 */
	public void updateClique(NodeBL[] nodes){
		
		representedNodes = (NodeBL[])sort(nodes);
		
		String name = "";
		ArrayList<String> states = new ArrayList<String>();
		
		for(int i=0; i<nodes.length;i++){
			Node n = nodes[i];
			name +=n.getName()+"/";
			
			for(int j=0; j<n.getStates().length;j++){
				String s = n.getStates()[j];
				states.add(s);
			}
		}
		
		super.name = name;
		super.states = (String[]) states.toArray(new String[0]);
	}
	
	
///////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////NOT USED SO FAR//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////	
	
	/*public void addCombinedCliqueName(String input){
		this.combinedCliques.add(input);
	}*/
	
	
	/*public ArrayList getCombinedCliqueNames(){
		return this.combinedCliques;
	}*/

}
