package org.societies.personalisation.DIANNE.impl.model;

import java.util.ArrayList;

public class Network 
{
	private ArrayList<ContextGroup> contextGroups;
	private ArrayList<OutcomeGroup> outcomeGroups;
	private ArrayList<Synapse> synapses;

	public Network()
	{
		contextGroups = new ArrayList<ContextGroup>();
		outcomeGroups = new ArrayList<OutcomeGroup>();
		synapses = new ArrayList<Synapse>();
	}
	
	public ArrayList<ContextGroup> getContextGroups(){
		return contextGroups;
	}
	
	public ArrayList<OutcomeGroup> getOutcomeGroups(){
		return outcomeGroups;
	}
	
	public ArrayList<Synapse> getSynapses(){
		return synapses;
	}
}