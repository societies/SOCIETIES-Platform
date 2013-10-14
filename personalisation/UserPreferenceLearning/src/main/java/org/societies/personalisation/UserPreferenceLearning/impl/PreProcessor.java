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

package org.societies.personalisation.UserPreferenceLearning.impl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

//import org.societies.api.context.CtxException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.internal.servicelifecycle.ServiceModelUtils;
import org.societies.api.personalisation.model.Action;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.preference.api.model.ActionSubset;
import org.societies.personalisation.preference.api.model.ServiceSubset;

import weka.core.Attribute;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;

public class PreProcessor {

	Map<String, ArrayList<String>> ruleVariables; //paramName, list of values
	private Logger LOG = LoggerFactory.getLogger(this.getClass());
	public ServiceSubset extractServiceActions
	(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> data, ServiceResourceIdentifier serviceId, String parameterName){

		ActionSubset actionSubset = new ActionSubset(parameterName);
		String serviceType = null;

		Iterator<CtxHistoryAttribute> data_it = data.keySet().iterator();
		while(data_it.hasNext()){
			try {
				CtxHistoryAttribute nextActionAttr = (CtxHistoryAttribute)data_it.next();
				Action nextAction = (Action) SerialisationHelper.deserialise(nextActionAttr.getBinaryValue(), this.getClass().getClassLoader());
				if (ServiceModelUtils.compare(nextAction.getServiceID(), serviceId)){
				// 31/1/13 eliza comments out: if(nextAction.getServiceID().getServiceInstanceIdentifier().equals(serviceId.getServiceInstanceIdentifier())){
					if (LOG.isDebugEnabled()){
						this.LOG.debug("Found history for this serviceId: "+ServiceModelUtils.serviceResourceIdentifierToString(serviceId)+" action: "+nextAction.getparameterName()+"->"+nextAction.getvalue());
					}
					if(nextAction.getparameterName().equals(parameterName)){
						//add action and snapshot to extracted list
						actionSubset.put(nextActionAttr, data.get(nextActionAttr));
						if(serviceType == null){
							serviceType = nextAction.getServiceType();
						}
						if (LOG.isDebugEnabled()){
							this.LOG.debug("Adding history record to serviceSubset"+ServiceModelUtils.serviceResourceIdentifierToString(serviceId)+" action: "+nextAction.getparameterName()+"->"+nextAction.getvalue());
						}
					}
				}
			} catch (Exception e) {  //CtxException
				e.printStackTrace();
			}
		}
		List<ActionSubset> actionSubsetList = new ArrayList<ActionSubset>();
		actionSubsetList.add(actionSubset);
		ServiceSubset serviceSubset = new ServiceSubset(serviceId, serviceType, actionSubsetList);

		return serviceSubset;        
	}

	public List<ServiceSubset> splitHistory (Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> data)
	{
		//System.out.println("Splitting history!! *****");
		//System.out.println("History size = "+data.size());
		List<ServiceSubset> serviceSubsets = new ArrayList<ServiceSubset>();

		Iterator<CtxHistoryAttribute> data_it = data.keySet().iterator();
		while(data_it.hasNext()){

			//get next key in data - value is action
			try {
				CtxHistoryAttribute nextActionAttr = (CtxHistoryAttribute)data_it.next();

				//extract action from nextActionAttr
				Action nextAction = (Action) SerialisationHelper.deserialise(nextActionAttr.getBinaryValue(), this.getClass().getClassLoader());
				ServiceResourceIdentifier serviceId = nextAction.getServiceID();
				String serviceType = nextAction.getServiceType();

				//check if service id exists in subsets
				ServiceSubset serviceSubset = getServiceSubset(serviceSubsets, serviceId);
				if(serviceSubset!=null){
					//System.out.println("***Already seen this serviceId: "+serviceId);
					//already exists
					List<ActionSubset> actionSubsets = serviceSubset.getActionSubsets();
					// search for subset with same paramName as nextAction
					ActionSubset actionSubset = getActionSubset(actionSubsets, nextAction.getparameterName());
					if(actionSubset!=null){
						//System.out.println("***Already seen this action: "+nextAction.getparameterName()+"="+nextAction.getvalue());
						actionSubset.put(nextActionAttr, data.get(nextActionAttr));                        
					}else{
						//ActionSubset doesn't exist - create new
						//System.out.println("***New action: "+nextAction.getparameterName()+"="+nextAction.getvalue());
						ActionSubset newActionSubset =  new ActionSubset(nextAction.getparameterName());
						newActionSubset.put(nextActionAttr, data.get(nextActionAttr));
						//add to list of actionSubsets
						actionSubsets.add(newActionSubset);
					}
				}else{
					//System.out.println("***New serviceId, creating new subset for serviceId: "+serviceId);
					//ServiceSubset doesn't exist - create new
					//System.out.println("***New action: "+nextAction.getparameterName()+"="+nextAction.getvalue());
					ActionSubset newActionSubset = new ActionSubset(nextAction.getparameterName());
					newActionSubset.put(nextActionAttr, data.get(nextActionAttr));

					List<ActionSubset> newActionSubsetList = new ArrayList<ActionSubset>();
					newActionSubsetList.add(newActionSubset);

					ServiceSubset newServiceSubset = new ServiceSubset(serviceId, serviceType, newActionSubsetList);
					serviceSubsets.add(newServiceSubset);
				}
			} catch (Exception e) {  //CtxException
				e.printStackTrace();
			}
		}
		return serviceSubsets;
	}


	/*public List<ActionSubset> splitPrivacyHistory
	(Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> data){

		List<ActionSubset> actionSubsetList = new ArrayList<ActionSubset>();

		Iterator<CtxHistoryAttribute> data_it = data.keySet().iterator();
		while(data_it.hasNext()){
			try {
				CtxHistoryAttribute nextAttr = (CtxHistoryAttribute)data_it.next();
				Action nextAction = (Action)nextAttr.getBlobValue(this.getClass().getClassLoader());
				//System.out.println("Next Action: "+nextAction.toString());
				ActionSubset actionSubset = getActionSubset(actionSubsetList, nextAction.getparameterName());
				if(actionSubset!=null){
					actionSubset.put(nextAttr, data.get(nextAttr));
				}else{
					ActionSubset newActionSubset = new ActionSubset(nextAction.getparameterName());
					newActionSubset.put(nextAttr, data.get(nextAttr));
					actionSubsetList.add(newActionSubset);
				}
			} catch (Exception e) {  //CtxException
				e.printStackTrace();
			}
		}
		return actionSubsetList;
	}*/


	public ServiceSubset trimServiceSubset(ServiceSubset history){

		//System.out.println("Inside trimServiceSubset");
		ServiceSubset newServiceSubset = new ServiceSubset(history.getServiceId(), history.getServiceType(), new ArrayList<ActionSubset>());

		List<ActionSubset> newActionSubsets = new ArrayList<ActionSubset>();
		List<ActionSubset> actionSubsets = history.getActionSubsets();
		Iterator<ActionSubset> actionSubsets_it = actionSubsets.iterator();
		while(actionSubsets_it.hasNext()){
			ActionSubset nextSubset = (ActionSubset)actionSubsets_it.next();
			
			//get trimmed ActionSubset
			ActionSubset trimmedSubset = trimActionSubset(nextSubset);

			//add trimmed ActionSubset to list
			newActionSubsets.add(trimmedSubset);
		}
		
		//add ActionSubset list to ServiceSubset
		newServiceSubset.setActionSubsets(newActionSubsets);

		return newServiceSubset;
	}

	public ActionSubset trimActionSubset(ActionSubset actionSubset){
		//System.out.println("Inside trimActionSubset");
		//check if each ActionSubset snapshot variable has single value
		ActionSubset newActionSubset = new ActionSubset(actionSubset.getParameterName());
		
		Map<String, List<String>> ctxValues = new LinkedHashMap<String, List<String>>();
		Iterator<List<CtxHistoryAttribute>> actionSubset_it = actionSubset.values().iterator();
		while(actionSubset_it.hasNext()){
			List<CtxHistoryAttribute> snapshot = (List<CtxHistoryAttribute>)actionSubset_it.next();
			Iterator<CtxHistoryAttribute> snapshot_it = snapshot.iterator();
			while(snapshot_it.hasNext()){
				CtxHistoryAttribute ctxAttrib = (CtxHistoryAttribute)snapshot_it.next();
				//System.out.println("Searching ctxValues for: "+ctxAttrib.getType());
				if(ctxValues.containsKey(ctxAttrib.getType())){
					//System.out.println(ctxAttrib.getType()+" found");
					List<String> values = ctxValues.get(ctxAttrib.getType());
					if(!values.contains(ctxAttrib.getStringValue())){
						//add new value
						values.add(ctxAttrib.getStringValue());
						ctxValues.put(ctxAttrib.getType(), values);
					}
				}else{
					//System.out.println(ctxAttrib.getType()+" not found");
					List<String> values = new ArrayList<String>();
					values.add(ctxAttrib.getStringValue());
					ctxValues.put(ctxAttrib.getType(), values);
				}
			}			
		}
		
		//identify singletons
		List<String> singletons = new ArrayList<String>();
		Iterator<String> ctxValues_it = ctxValues.keySet().iterator();
		while(ctxValues_it.hasNext()){
			String nextType = (String)ctxValues_it.next();
			List<String> values = ctxValues.get(nextType);
			if(values.size()<=1){
				singletons.add(nextType);
			}
		}
		
		/*
		 * test
		 */
		/*System.out.println("Printing singletons...");
		Iterator singletons_it = singletons.iterator();
		while(singletons_it.hasNext()){
			System.out.println((String)singletons_it.next());
		}
		System.out.println("--------------");*/
		/*
		 * end
		 */
		
		if(singletons.size()>0){
			//remove singletons from ActionSubset snapshots
			Iterator<CtxHistoryAttribute> actionSubset_iter = actionSubset.keySet().iterator();
			while(actionSubset_iter.hasNext()){
				CtxHistoryAttribute action = (CtxHistoryAttribute)actionSubset_iter.next();
				List<CtxHistoryAttribute> snapshot = actionSubset.get(action);
				List<CtxHistoryAttribute> newSnapshot = new ArrayList<CtxHistoryAttribute>();
				Iterator<CtxHistoryAttribute> snapshot_it = snapshot.iterator();
				while(snapshot_it.hasNext()){
					CtxHistoryAttribute ctxAttrib = (CtxHistoryAttribute)snapshot_it.next();
					if(!singletons.contains(ctxAttrib.getType())){
						newSnapshot.add(ctxAttrib);
					}
				}if(newSnapshot.size()>0){  //only add action and snapshot if snapshot is not empty
					newActionSubset.put(action, newSnapshot);
				}
			}
		}else{
			newActionSubset = actionSubset;
		}
		
		return newActionSubset;
	}

	public Instances toInstances(ActionSubset actionSubset)
	{      
		Instances populatedInstances = null;

		//re-initialise HashTable
		ruleVariables = new LinkedHashMap<String, ArrayList<String>>();

		//change each record in actionSubset into Instance
		Iterator<CtxHistoryAttribute> actionSubset_it = actionSubset.keySet().iterator();
		while(actionSubset_it.hasNext()){

			try {
				CtxHistoryAttribute nextActionAttr = (CtxHistoryAttribute)actionSubset_it.next();
				Action outcome = (Action) SerialisationHelper.deserialise(nextActionAttr.getBinaryValue(), this.getClass().getClassLoader());

				List<CtxHistoryAttribute> context = 
					(ArrayList<CtxHistoryAttribute>)actionSubset.get(nextActionAttr);

				//write info to temporary LinkedHasMap for translation
				writeToHashMap(outcome, context);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		//create Instances for subset of subset size
		Instances instances = createInstances(actionSubset.size());

		//populate Instances with subset data
		populatedInstances = populateInstances(instances, actionSubset);

		return populatedInstances;
	}

	private void writeToHashMap
	(Action outcome, List<CtxHistoryAttribute> context){

		//write context
		Iterator<CtxHistoryAttribute> context_it = context.iterator();
		while(context_it.hasNext()){

			CtxHistoryAttribute contextAttr = (CtxHistoryAttribute)context_it.next();
			String cParam = contextAttr.getType();
			String cValue = contextAttr.getStringValue();

			updateHashMap(cParam, cValue);
		}

		//write outcome
		String param = outcome.getparameterName();
		String value = outcome.getvalue();
		updateHashMap(param, value);
	}    

	private void updateHashMap(String param, String value){

		//see if parameter name exists
		if(ruleVariables.containsKey(param)){
			//yes - check if value exists for parameter
			ArrayList<String> values = 
				(ArrayList<String>)ruleVariables.get(param);
			if(!values.contains(value)){
				//no - add new outcome to fast vector
				values.add(value);
				ruleVariables.put(param, values);
			}
		}else{
			//no - add parameter and value
			ArrayList<String> values = new ArrayList<String>();
			values.add(value);
			ruleVariables.put(param, values);

		}
	}

	private Instances createInstances(int instanceCount){

		Instances instances = null;
		ArrayList<Attribute> tmpAtts = new ArrayList<Attribute>();

		/*
		 * Print ruleVariables
		 */
		/*Iterator rule_it = ruleVariables.keySet().iterator();
		while(rule_it.hasNext()){
			String key = (String)rule_it.next();
			System.out.println("Key = "+key);
			ArrayList<String> values = ruleVariables.get(key);
			Iterator values_it = values.iterator();
			while(values_it.hasNext()){
				System.out.println("next value = "+(String)values_it.next());
			}
		}*/
		/*
		 * end
		 */

		//for each parameter, value set - create instance
		//create Elements fastVectors
		Iterator<String> ruleVariables_it = ruleVariables.keySet().iterator();
		while(ruleVariables_it.hasNext()){
			String fastVectorName = (String)ruleVariables_it.next();
			ArrayList<String> elements = ruleVariables.get(fastVectorName);

			FastVector vector = new FastVector(elements.size());
			Iterator<String> elements_it = elements.iterator();
			while(elements_it.hasNext()){
				vector.addElement((String)elements_it.next());
			}
			Attribute attribute = new Attribute(fastVectorName, vector);
			tmpAtts.add(attribute);
		}

		//create Attributes fastVector
		FastVector attributes = new FastVector(tmpAtts.size());
		Iterator<Attribute> tmpAtts_it = tmpAtts.iterator();
		while(tmpAtts_it.hasNext()){
			attributes.addElement(tmpAtts_it.next());
		}

		//create Instances
		instances = new Instances("Instances", attributes, instanceCount);

		return instances;
	}

	private Instances populateInstances
	(Instances instances, Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> subset){

		Iterator<CtxHistoryAttribute> subset_it = subset.keySet().iterator();
		while(subset_it.hasNext()){
			try {
				//get information from subset to populate Instance
				CtxHistoryAttribute actionAttr = (CtxHistoryAttribute)subset_it.next();
				Action outcome = (Action) SerialisationHelper.deserialise(actionAttr.getBinaryValue(), this.getClass().getClassLoader());

				ArrayList<CtxHistoryAttribute> context = 
					(ArrayList<CtxHistoryAttribute>)subset.get(actionAttr);

				//create new Instance
				Instance instance = new Instance(context.size()+1);

				//add all context info
				Iterator<CtxHistoryAttribute> context_it = context.iterator();
				while(context_it.hasNext()){
					CtxHistoryAttribute contextAttr = (CtxHistoryAttribute)context_it.next();

					//get appropriate attribute (CtxIdentifier) from Instances
					Attribute attrib = instances.attribute(contextAttr.getType().toString());

					//get context value from contextAttr value
					String cValue = contextAttr.getStringValue();
					instance.setValue(attrib, cValue);
				}

				//add outcome at end
				Attribute attrib = instances.attribute(outcome.getparameterName());
				instance.setValue(attrib, outcome.getvalue());
				instance.setDataset(instances);
				instances.add(instance);

			} catch (Exception e) {
				e.printStackTrace();
			}   
		}

		return instances;
	}


	private ServiceSubset getServiceSubset(List<ServiceSubset> subsets, ServiceResourceIdentifier serviceId){
		ServiceSubset serviceSubset = null;

		Iterator<ServiceSubset> subsets_it = subsets.iterator();
		while(subsets_it.hasNext()){
			ServiceSubset nextSubset = (ServiceSubset)subsets_it.next();
			if (ServiceModelUtils.compare(nextSubset.getServiceId(), serviceId)){
			//31/1/13 eliza comments out: if(nextSubset.getServiceId().getIdentifier().equals(serviceId.getIdentifier())){
				serviceSubset = nextSubset;
				break;
			}
		}
		return serviceSubset;
	}

	private ActionSubset getActionSubset(List<ActionSubset> actionSubsets, String parameterName){
		ActionSubset actionSubset = null;

		Iterator<ActionSubset> actionSubsets_it = actionSubsets.iterator();
		while(actionSubsets_it.hasNext()){
			ActionSubset nextSubset = (ActionSubset)actionSubsets_it.next();
			if(nextSubset.getParameterName().toString().equals(parameterName)){
				actionSubset = nextSubset;
			}
		}
		return actionSubset;
	}
}
