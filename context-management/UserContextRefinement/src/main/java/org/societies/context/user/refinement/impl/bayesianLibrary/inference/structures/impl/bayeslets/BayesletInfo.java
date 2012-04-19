package org.societies.context.user.refinement.impl.bayesianLibrary.inference.structures.impl.bayeslets;

/**
 * This class is used to represent information about a certain connection. It contains the name of a bayeslet and the name of a certain interface node that is connected in that bayeslet.
 * @author gall_pa
 *
 */
public class BayesletInfo {
	
	private String bayesletName;
	private String nodeName;
	
	public BayesletInfo(){
		
	}
	
	public BayesletInfo(String nameB, String nameN){
		bayesletName=nameB;
		nodeName=nameN;
	}
		
	public String getName(){
		return (bayesletName);
	}
	
	public String getNodeName(){
		return (nodeName);
	}
	
	public void setBayesletName(String name){
		this.bayesletName=name;
	}
	
	public void setNodeName(String nodeName){
		this.nodeName=nodeName;
	}

}
