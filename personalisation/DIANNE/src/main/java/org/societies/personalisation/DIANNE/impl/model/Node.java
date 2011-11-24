package org.societies.personalisation.DIANNE.impl.model;

import java.util.ArrayList;

public class Node 
{
	int ID;
	String nodeName;
	String groupName;
	ArrayList<Synapse> synapses;
	Boolean active;
	
	public Node(int ID, String groupName, String nodeName)
	{
		this.ID = ID;
		this.groupName = groupName;
		this.nodeName = nodeName;
		synapses = new ArrayList<Synapse>();
		active = false;
	}
	
	public void addSynapse(Synapse synapse)
	{
		synapses.add(synapse);
	}
	
	public int getID(){
		return this.ID;
	}
	
	public String getNodeName()
	{
		return nodeName;
	}
	
	public String getGroupName()
	{
		return groupName;
	}
	
	public ArrayList<Synapse> getSynapses()
	{
		return synapses;
	}
	
	public boolean getActive()
	{
		return active;
	}
	
	public void activate()
	{
		active = true;
	}
	
	public void deactivate()
	{
		active = false;
	}
}
