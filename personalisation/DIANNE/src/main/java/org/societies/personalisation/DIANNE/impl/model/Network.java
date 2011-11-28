package org.societies.personalisation.DIANNE.impl.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.societies.personalisation.DIANNE.impl.model.OutcomeGroup;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

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
	
	public ContextGroup getContextGroup(String groupName){
		ContextGroup requestedGroup = null;
		Iterator<ContextGroup> list_it = contextGroups.iterator();
		while(list_it.hasNext())
		{
			ContextGroup group = (ContextGroup)list_it.next();
			if(group.getGroupName().equals(groupName))
			{
				requestedGroup = group;
				break;
			}
		}
		return requestedGroup;
	}
	
	public OutcomeGroup getOutcomeGroup(ServiceResourceIdentifier serviceId, String groupName){
		OutcomeGroup requestedGroup = null;

		Iterator <OutcomeGroup>list_it = outcomeGroups.iterator();
		while(list_it.hasNext())
		{
			OutcomeGroup group = (OutcomeGroup)list_it.next();
			if(group.getServiceId().equals(serviceId)){
				if(group.getGroupName().equals(groupName))
				{
					requestedGroup = group;
					break;
				}
			}
		}
		return requestedGroup;
	}
	
	public void addContextGroup(ContextGroup newContextGroup){
		contextGroups.add(newContextGroup);
	}
	
	public void addOutcomeGroup(OutcomeGroup newOutcomeGroup){
		outcomeGroups.add(newOutcomeGroup);
	}
	
	public void addSynapse(Synapse newSynapse){
		synapses.add(newSynapse);
	}
}