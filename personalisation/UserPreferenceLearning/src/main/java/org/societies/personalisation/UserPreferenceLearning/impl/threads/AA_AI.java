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

package org.societies.personalisation.UserPreferenceLearning.impl.threads;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceLearning.impl.C45Output;
import org.societies.personalisation.UserPreferenceLearning.impl.CtxIdentifierCache;
import org.societies.personalisation.UserPreferenceLearning.impl.HistoryRetriever;
import org.societies.personalisation.UserPreferenceLearning.impl.PostProcessor;
import org.societies.personalisation.UserPreferenceLearning.impl.PreProcessor;
import org.societies.personalisation.preference.api.model.ActionSubset;
import org.societies.personalisation.preference.api.model.IC45Consumer;
import org.societies.personalisation.preference.api.model.IC45Output;
import org.societies.personalisation.preference.api.model.IPreferenceTreeModel;
import org.societies.personalisation.preference.api.model.ServiceSubset;

import weka.classifiers.trees.Id3;
import weka.core.Instances;


public class AA_AI extends Thread{

	private Logger LOG = LoggerFactory.getLogger(AA_AI.class);
	private IC45Consumer requestor;
	private Date startDate;
	private HistoryRetriever historyRetriever;
	private PreProcessor preProcessor;
	private PostProcessor postProcessor;

	//All actions - All identities
	public AA_AI(IC45Consumer requestor, Date startDate, ICtxBroker ctxBroker){
		this.requestor = requestor;
		this.startDate = startDate;
		
		historyRetriever = new HistoryRetriever(ctxBroker);
		preProcessor = new PreProcessor();
		postProcessor = new PostProcessor(); 
	}

	@Override
	public void run() {

		LOG.info("C45 REQUEST FROM: "+requestor.getClass().getName());
		LOG.info("Starting C45 learning process on all actions");

		//create new Cache for cycle
		CtxIdentifierCache cache = new CtxIdentifierCache();

		//Get all identities of the user
		//Set<IIdentity> historyOwners = commsMgr.getIdManager().getPublicIdentities();

		List<IC45Output> output = new ArrayList<IC45Output>();

		//For each DPI
		//for(int i=0; i<historyOwners.length; i++){

		//IIdentity nextHistoryOwner = (IIdentity)historyOwners[i];

		//get history
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = 
				historyRetriever.getFullHistory(startDate);
		LOG.info("Retrieved full history");

		//split history into subsets depending on serviceId and action
		if(history!=null && history.size()>0){          	

			//store context attribute identifiers with types
			cache.cacheCtxIdentifiers(history);
			LOG.info("Mapped context attribute identifiers with context snapshot types");

			LOG.info("Splitting history depending on serviceID and parameterName");
			List<ServiceSubset> splitHistory = preProcessor.splitHistory(history);

			//for each service Identifier
			Iterator<ServiceSubset> splitHistory_it = splitHistory.iterator();
			while(splitHistory_it.hasNext()){

				LOG.info("Getting next split subset...");
				ServiceSubset nextServiceSubset = (ServiceSubset)splitHistory_it.next();

				//remove consistent context attributes from history
				ServiceSubset trimmedHistory = preProcessor.trimServiceSubset(nextServiceSubset);
				LOG.info("Split subset has been trimmed!");

				ServiceResourceIdentifier nextServiceId = trimmedHistory.getServiceId();
				String nextServiceType = trimmedHistory.getServiceType();

				//create new IC45Output object
				IC45Output nextOutput = new C45Output(null, nextServiceId, nextServiceType);

				//run cycle for each action subset
				List<ActionSubset> actionSubsetList = trimmedHistory.getActionSubsets();
				Iterator<ActionSubset> actionSubsetList_it = actionSubsetList.iterator();
				while(actionSubsetList_it.hasNext()){
					ActionSubset nextActionSubset = (ActionSubset)actionSubsetList_it.next();
					if(nextActionSubset.size()>0){
						IPreferenceTreeModel treeModel = runCycle(nextActionSubset, cache, nextServiceId, nextServiceType);
						if(treeModel!=null){
							nextOutput.addTree(treeModel);
						}
					}
				}
				output.add(nextOutput);
			}
		}else{
			if (LOG.isDebugEnabled()){
			LOG.debug("No History found for this user!");
			}
		}
		//}
		//send DPI based output to requestor
		LOG.info("RETURNING C45 OUTPUT TO: "+requestor.getClass().getName());
		try{
			requestor.handleC45Output(output);
		}catch(Exception e){
			if (LOG.isDebugEnabled()){
				LOG.debug("The C45 requestor service is not available to handle response");
			}
		}
	}


	/*
	 * Algorithm methods
	 */

	private IPreferenceTreeModel runCycle(
			ActionSubset input, 
			CtxIdentifierCache cache,
			ServiceResourceIdentifier serviceId,
			String serviceType){

		//convert to Instances for each serviceId
		Instances instances = preProcessor.toInstances(input);

		//System.out.println("C45 executing...");
		String outputString = null;
		try{
			outputString = executeAlgorithm(instances);
		} catch (Exception e) {
			e.printStackTrace();
			if (LOG.isDebugEnabled()){
				LOG.debug("No rules could be learned from the current history set: \nException: "+e.toString());
			}
			return null;
		}

		//convert tree strings into JTrees for pref
		String paramName = input.getParameterName();
		return (IPreferenceTreeModel)postProcessor.process(paramName, outputString, cache, serviceId, serviceType);
	}

	private String executeAlgorithm(Instances input)throws Exception
	{
		Id3 id3 = new Id3();
		//c45 = new C45PruneableClassifierTree
		//(model, false, 0, false, false);

		input.setClassIndex(input.numAttributes()-1);

		//c45.buildClassifier(input);
		id3.buildClassifier(input);

		//System.out.println("ID3 output: "+id3.toString());

		return id3.toString();
	}
}
