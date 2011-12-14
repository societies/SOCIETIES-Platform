/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.societies.personalisation.dianne.model;

import java.util.ArrayList;
import java.util.Iterator;

import org.societies.api.mock.ServiceResourceIdentifier;

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