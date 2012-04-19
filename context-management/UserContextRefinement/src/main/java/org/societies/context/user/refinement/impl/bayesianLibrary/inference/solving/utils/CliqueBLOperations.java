package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils;

import java.util.ArrayList;

import org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.BayesletJTree;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Edge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Node;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.Probability;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.UndirectedEdge;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.Bayeslet;
import org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets.NodeBL;


/**
 * This class contains a set of methods that are used in several approaches.
 * @author gall_pa
 *
 */
public class CliqueBLOperations {
	
	

	/** This method returns whether nodes wanted and n are compatible or not. To do that it checks if their states are equal.
	*/
	public static boolean checkNodeCompatibility(NodeBL wanted,NodeBL n){
		boolean screenEnabled=false;
		String[] tempName1=(wanted.getName()).split(":");
		String[] tempName2=n.getName().split(":");
		boolean equalNames=tempName1[tempName1.length-1].endsWith(tempName2[tempName2.length-1]);
		if ((compareStringArrays(wanted.getStates(), n.getStates()))&& equalNames){
			if (screenEnabled) System.out.println("Nodes "+wanted.getName()+" and "+n.getName()+" are compatible!!");
			return (true);
		}else{
			if (screenEnabled)System.out.println("Nodes "+wanted.getName()+" and "+n.getName()+" are NOT compatible!!");
			return (false);
		}
		
	}
	
	public static void printJunctionTree (BayesletJTree input){
		
		
		for (int i=0;i<input.getCliquesArrayList().size();i++){
			System.out.println(input.getCliquesArrayList().get(i));
		}
		
		for (int i=0; i<input.getSeparatorsArrayList().size();i++){
			System.out.println(input.getSeparatorsArrayList().get(i));
		}
	}
	
	/**
	 * This method returns the position of the object whose name is wanted in the ArrayList input.
	 * @param wanted
	 * @param input
	 * @return
	 */
	public static int searchPosition(String wanted, ArrayList input){        
	
		boolean found=false;
		int i=0;
		
		
		while((!found)&&(i<input.size())){
			if (((Node)input.get(i)).getName().contains(wanted)){
				found=true;
				 
			}else{
				i++;
			}
		}
		
		if (found) return i;
		else return -1;
		
	}
	
	/**
	 * This method converts an ArrayList into an array.
	 * @param input
	 * @return
	 */
	public static NodeBL[] convertToArray(ArrayList input){
		NodeBL[] toReturn=new NodeBL[input.size()];
		int index=0;
		while(index<input.size()){
			toReturn[index]=(NodeBL)input.get(index);
			index++;
		}
		return(toReturn);
	}
	
	/**
	 * This method searchs for a node within an ArrayList containing a set of nodes.
	 * @param wanted
	 * @param allNodes
	 * @return The searched node, or null.
	 */
	public static NodeBL searchNode(String wanted, ArrayList allNodes){        
		
		boolean found=false;
		int i=0;
		
		
		while(!found){
			if (((NodeBL) allNodes.get(i)).getName().contains(wanted)){
				found=true;
				return (NodeBL) (allNodes.get(i));
			}else{
				i++;
			}
		}
		return null;
	}
	
	/**
	 * This method removes node called wanted from ArrayList input.
	 * @param wanted
	 * @param input
	 */
	public static void removeNode(String wanted,ArrayList input){
		int position=0;
		boolean found=false;
		while((!found)&&(position<input.size())){
			if (((NodeBL)input.get(position)).getName().equals(wanted)){
				found=true;
			}
			if (found){
				input.remove(position);
			}else{
				position++;
			}
			
		}
		
	}
	
	/**
	 * This method searchs a certain bayeslet within an array of bayeslets.
	 * @param name
	 * @param bayeslets
	 * @return The searched bayeslet, or null.
	 */
	public static Bayeslet searchBayeslet(String name,Bayeslet bayeslets[]){
		
		boolean found=false;
		int i=0;
		
		while((found==false)&&(i<bayeslets.length)){
			if (((Bayeslet) bayeslets[i]).getBName().equals(name)){
				found=true;
			}else{
				i++;
			}
		}
		if (found){
			return (((Bayeslet)bayeslets[i]));
		}else{
			return(null);
		}
	}
	
	/**
	 * This method searchs a certain c within an array of readyFlags.
	 * @param name
	 * @param bayeslets
	 * @return The searched readyFlag, or null.
	 */
	public static ReadyFlag searchSemaphore(String name,ReadyFlag semaphores[]){
		
		boolean found=false;
		int i=0;
		
		while((found==false)&&(i<semaphores.length)){
			if (((ReadyFlag) semaphores[i]).getName().equals(name)){
				found=true;
			}else{
				i++;
			}
		}
		if (found){
			return (((ReadyFlag)semaphores[i]));
		}else{
			return(null);
		}
	}
	
	/**
	 * This method searchs for a String withing an ArrayList containing strings.
	 * @param name
	 * @param input
	 * @return
	 */
	public static String searchString(String name,ArrayList input){
		
		boolean found=false;
		int i=0;
		
		while((found==false)&&(i<input.size())){
			if (((String) input.get(i)).equals(name)){
				found=true;
			}else{
				i++;
			}
		}
		if (found){
			return ((String)input.get(i));
		}else{
			return(null);
		}
	}
	
	/**
	 * This method converts an ArrayList containing strings into an array of strings.
	 * @param input
	 * @return
	 */
	
	public static String[] convertToArrayOfStrings(ArrayList input){
		String[] toReturn=new String[input.size()];
		int index=0;
		while(index<input.size()){
			toReturn[index]=(String)input.get(index);
			index++;
		}
		return(toReturn);
	}
	
	/**
	 * This method converts an array into an ArrayList.
	 * @param myArray
	 * @return
	 */
	static public ArrayList arrayToArrayList(Object[] myArray){
		
		int i=0;
		
		ArrayList out=new ArrayList();
	
		for (i=0;i<myArray.length;i++){
			Object extract=myArray[i];
			out.add(extract);
		}
		return (out); 
	}

	/**
	 * This method compares two arrays of strings.
	 * @param s1
	 * @param s2
	 * @return A boolean defining if they are equal.
	 */
	static public boolean compareStringArrays(String[] s1,String[] s2){
		boolean equal=true;
		int index=0;
		if (s1.length==s2.length){
			while ((index<s1.length)&&(equal)){
				if (s1[index].equals(s2[index])){
					index++;
				}else{
					equal=false;
				}
			}
			if (index==s1.length) return true;
			else return false;
		}else{
			return false;
		}
	}
	
	static public boolean compareProbabilityArrays(Probability[] s1, Probability[] s2){
		boolean equal=true;
		int index=0;
		if (s1.length==s2.length){
			while ((index<s1.length)&&(equal)){
				if (s1[index].getProbability()==(s2[index].getProbability())){
					index++;
				}else{
					equal=false;
				}
			}
			return equal;
		}else{
			return false;
		}
		
	}
	
	
	static public void printDouble(double[] salida){
		int indexPrint=0;
		
		while (indexPrint<salida.length){
			System.out.println(salida[indexPrint]+" ");
			indexPrint++;
		}
	}
	
	static public void printQuantification(double[] input1,String[] input2){
		int indexPrint=0;
		
		while (indexPrint<input1.length){
			System.out.println(input1[indexPrint]+" "+input2[indexPrint]);
		
			indexPrint++;
		}
	
	}
	
	/**
	 * This method gets the probabilities from an array of Probability and introduces them into an array of double.
	 * @param input
	 * @return
	 */
	
	static public double[] probabilityToDouble(Probability[] input){
		double[] toReturn=new double[input.length];
		int i=0;
		while (i<input.length){
			toReturn[i]=input[i].getProbability();
		i++;
		}
		return(toReturn);
	}	
	
	/**
	 * This method returns the amount of possible combinations between the states of nodes contained in an ArrayList.
	 * @param input
	 * @return
	 */
	
	public static int getNumberStates(ArrayList input){
		int i=0;
		int sum=1;
		while(i<input.size()){
			sum=sum*((Node)input.get(i)).countStates();
			i++;
		}
		return (sum);
	}
	/**
	 * This method transfer the edges related to one node to another one, and also updates an ArrayList containing all edges.
	 * @param sourceNode
	 * @param target
	 * @param combinedEdges
	 */
	public static void transferEdges(NodeBL sourceNode, NodeBL target, ArrayList combinedEdges){
		
		int index=0;
		Edge tempEdge;
		Edge newEdge;
		ArrayList tempIncoming;
		ArrayList tempOutgoing;
		tempIncoming=sourceNode.getIncoming();
		
		while (index<tempIncoming.size()){
			tempEdge=(Edge)tempIncoming.get(index);
			newEdge=new Edge(tempEdge.getSource(),target);
			tempEdge.getSource().removeOutgoing(tempEdge);
			combinedEdges.add(newEdge);
			combinedEdges.remove(tempEdge);
			index++;
		}
		index=0;
				
		tempOutgoing=sourceNode.getOutgoing();
		
		while (index<tempOutgoing.size()){
			tempEdge=(Edge)tempOutgoing.get(index);
			newEdge=new Edge(target,tempEdge.getTarget());
			tempEdge.getTarget().removeIncoming(tempEdge);
			combinedEdges.add(newEdge);
			combinedEdges.remove(tempEdge);
			index++;
		}
			
	}
	
	/** 
	 * This method transfer the undirected edges related to one node to another one, and also updates an ArrayList containing all edges.
	 * 
	 */
	public static void transferUndirectedEdges(NodeBL source, NodeBL target, ArrayList combinedEdges){
		
		int index=0;
		UndirectedEdge tempUEdge;
		UndirectedEdge newUEdge;
		ArrayList temp;
		temp=source.getUndirectedEdges();
		
		while (index<temp.size()){
			tempUEdge=(UndirectedEdge)temp.get(index);
			if (tempUEdge.getBorder1()==source){
				newUEdge=new UndirectedEdge(target,tempUEdge.getBorder2());	
				
			}else{
				newUEdge=new UndirectedEdge(tempUEdge.getBorder1(),target);
				
			}
			target.addConnection(newUEdge);
			
			combinedEdges.remove(tempUEdge);
			
			combinedEdges.add(newUEdge);
			
			index++;
		}
			
	}
	
	/**
	 * This method returns an ArrayList containing all sources from the edges contained in the ArrayList passed as a parameter.
	 * @param incomingEdges
	 * @return
	 */
	public static ArrayList getParents(ArrayList incomingEdges){
		int i=0;
		Edge tempEdge;
		ArrayList toReturn=new ArrayList();
		while (i<incomingEdges.size()){
			tempEdge=(Edge)incomingEdges.get(i);
			toReturn.add(tempEdge.getSource());
			i++;
		}
		return(toReturn);
	}

	/**
	 * This method transfer the edges from node source to node target.
	 * @param source
	 * @param target
	 */
	public static void transferEdgesBetweenNodes(NodeBL source, NodeBL target){
		
		int index=0;
		Edge tempEdge;
		Edge newEdge;
		UndirectedEdge tempUE;
		
		ArrayList temp=source.getIncoming();
		
		while (index<temp.size()){
			tempEdge=(Edge)temp.get(index);
			newEdge=new Edge(tempEdge.getSource(),target);
			tempEdge.getSource().removeOutgoing(tempEdge);
			//target.getIncoming().add(newEdge); it is already done with the previous instruction
			index++;
		}
		
		index=0;
				
		temp=source.getOutgoing();
		
		while (index<temp.size()){
			tempEdge=(Edge)temp.get(index);
			newEdge=new Edge(target,tempEdge.getTarget());
			tempEdge.getTarget().removeIncoming(tempEdge);
			//target.getOutgoing().add(newEdge); it is already done with the previous instruction
			index++;
		}
		
		/*temp=source.getUndirectedEdges();
		index=0;
		while (index<temp.size()){
			
			tempUE=(UndirectedEdge) temp.get(index);

			if (tempUE.getBorder1()==source){
				
				UndirectedEdge newUE=new UndirectedEdge(target,tempUE.getBorder2());
				target.addConnection(newUE);

				
			}else{
				UndirectedEdge newUE=new UndirectedEdge(tempUE.getBorder1(),target);
				target.addConnection(newUE);
				
			}
			index++;
		}*/
		
	}
	
	/**
	 * This method transfer the undirected edges related to node source to node target.
	 * @param source
	 * @param target
	 */
	public static void transferUndirectedEdgesBetweenNodes(NodeBL source, NodeBL target){
		
		ArrayList temp=source.getUndirectedEdges();
		int index=0;
		
		while (index<temp.size()){
			
			UndirectedEdge tempUE=(UndirectedEdge) temp.get(index);

			if (tempUE.getBorder1()==source){
				
				
				UndirectedEdge newUE=new UndirectedEdge(target,tempUE.getBorder2());
				target.addConnection(newUE);

				
			}else{
				UndirectedEdge newUE=new UndirectedEdge(tempUE.getBorder1(),target);
				target.addConnection(newUE);
				
			}
			index++;
		}
		
	}

	/**
	 * This method normalizes the input vector to 1.
	 */
	public static void normalizeVector(double[] input){
		int index=0;
		double sum=0;
		while (index<input.length){
			sum+=input[index];
			index++;
		}
		index=0;
		while (index<input.length){
			input[index]=input[index]/sum;
			index++;
		}

	}
	public static void normalizeVectorMax(double[] input){
		double max=getMax(input);
		int index=0;
		while(index<input.length){
			input[index]=input[index]/max;
			index++;
		}
	}
	public static double getMax(double[] input){
		double output=0;
		for (int i=0;i<input.length;i++){
			if (input[i]>output){
				output=input[i];
			}
		}
		return output;
	}
///////////////////////////////////////////////////////////////////////////////////////////////	
//////////////////////NOT USED SO FAR//////////////////////////////////////////////////////////
///////////////////////////////////////////////////////////////////////////////////////////////		
//
	/*
	 * 
	public static void transferEdgesBackUp(Node backUpNode, Node target, ArrayList combinedNodes, ArrayList combinedEdges){
		
		
		
		int index=0;
		Edge tempEdge;
		Edge newEdge;
		ArrayList tempIncoming;
		ArrayList tempOutgoing;
		tempIncoming=backUpNode.getIncoming();
		
		while (index<tempIncoming.size()){
			tempEdge=(Edge)tempIncoming.get(index);
			String tempSourceName=tempEdge.getSource().getName();
			Node edgeSource=searchNode(tempSourceName, combinedNodes);
			newEdge=new Edge(edgeSource,target);
			//tempEdge.getSource().removeOutgoing(tempEdge);
			
			//target.addIncoming(newEdge);
			combinedEdges.add(newEdge);
			combinedEdges.remove(tempEdge);
			index++;
		}
		index=0;
				
		tempOutgoing=backUpNode.getOutgoing();
		
		while (index<tempOutgoing.size()){
			tempEdge=(Edge)tempOutgoing.get(index);
			String tempTargetName=tempEdge.getTarget().getName();
			Node edgeTarget=searchNode(tempTargetName, combinedNodes);
			newEdge=new Edge(target,edgeTarget);
			//tempEdge.getTarget().removeIncoming(tempEdge);
			
			combinedEdges.add(newEdge);
			combinedEdges.remove(tempEdge);
			index++;
		}
			
	}
	 * 
	 */

	
	
	
}
