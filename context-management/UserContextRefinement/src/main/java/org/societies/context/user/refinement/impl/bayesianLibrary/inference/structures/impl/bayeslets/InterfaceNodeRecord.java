package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;

import java.util.ArrayList;

/**
 * 
 * @author gall_pa
 * @param name Name of the interface node.
 * @param incomingInterfaceNode Defines whether the interface node is an input or an output node.
 * @param connectedBayeslets Stores the information related to all bayeslets connected to this interface node.
 * @param toBeUpdatedNode It is used in the second approach (big bayesian network) to describe whether this node was already combined or not.
 */

public class InterfaceNodeRecord {

	private String name;
	private boolean incomingInterfaceNode;
	private ArrayList <BayesletInfo> connectedBayeslets=new ArrayList<BayesletInfo>();
	private boolean toBeUpdatedNode;
	
	/**
	 * Constructor. This constructor is called when the first bayeslet is connected to this interface node.
	 * @param name Name of the new InterfaceNodeRecord
	 * @param connectedBayeslet Information about the first bayeslet to be connected to that interface node. 
	 * @param incoming
	 */
	public InterfaceNodeRecord(String name,BayesletInfo connectedBayeslet,boolean incoming){
		this.name=name;
		connectedBayeslets.add(connectedBayeslet);
		incomingInterfaceNode=incoming;
		toBeUpdatedNode=true;
	}
	
	/**
	 * This method adds the information about a new connected bayeslet.
	 * @param connectedBayeslet
	 */
	public void addConnectedBayeslet(BayesletInfo connectedBayeslet){
		connectedBayeslets.add(connectedBayeslet);
		toBeUpdatedNode=true;
	}
	
	public String getName(){
		return(this.name);
	}
	public boolean isIncomingInterfaceNode(){
		return (incomingInterfaceNode);
	}
	public ArrayList<BayesletInfo> getConnectedBayeslets(){
		return(connectedBayeslets);
	}

	public void setToBeUpdatedNode(boolean input){
		this.toBeUpdatedNode=input;
	}
	
	public boolean getToBeUpdatedNode(){
		return(this.toBeUpdatedNode);
	}

}
