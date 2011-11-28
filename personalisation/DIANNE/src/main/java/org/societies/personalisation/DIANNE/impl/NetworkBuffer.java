/**
 * Copyright 2009 PERSIST consortium
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at
 * 
 *  http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 * 
 **/

package org.societies.personalisation.DIANNE.impl;

import java.util.ArrayList;

import org.societies.personalisation.common.api.model.IAction;

public class NetworkBuffer 
{
	ArrayList<IAction> outcomeUpdates;
	ArrayList<IAction> contextUpdates;
	
	public NetworkBuffer()
	{
		outcomeUpdates = new ArrayList<IAction>();
		contextUpdates = new ArrayList<IAction>();
	}
	
	public synchronized void addContextUpdate(IAction update)
	{
		contextUpdates.add(update);
	}
	
	public synchronized void addOutcomeUpdate(IAction update)
	{
		outcomeUpdates.add(update);
	}
	
	public synchronized ArrayList<IAction>[] getSnapshot()
	{
		ArrayList<IAction> context_ss = contextUpdates;
		ArrayList<IAction> outcome_ss = outcomeUpdates;
		
		contextUpdates = new ArrayList<IAction>();
		outcomeUpdates = new ArrayList<IAction>();
		
		ArrayList[] snapshot = new ArrayList[]{context_ss, outcome_ss};
		
		return snapshot;
	}
}
