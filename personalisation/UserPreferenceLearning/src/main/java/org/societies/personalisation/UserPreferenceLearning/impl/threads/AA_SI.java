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
import org.societies.api.identity.IIdentity;
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

public class AA_SI extends Thread{

	Logger LOG = LoggerFactory.getLogger(AA_SI.class);
	private IC45Consumer requestor;
	private Date startDate;
	private IIdentity historyOwner;
	private HistoryRetriever historyRetriever;
	private PreProcessor preProcessor;
	private PostProcessor postProcessor;

	public AA_SI(IC45Consumer requestor, Date startDate, IIdentity historyOwner, ICtxBroker ctxBroker){
		this.requestor = requestor;
		this.startDate = startDate;
		this.historyOwner = historyOwner;
		
		historyRetriever = new HistoryRetriever(ctxBroker);
		preProcessor = new PreProcessor();
		postProcessor = new PostProcessor();
	}

	@Override
	public void run(){
		LOG.info("C45 REQUEST FROM: "+requestor.getClass().getName());
		LOG.info("Starting C45 learning process on all actions for history owner: "+historyOwner.toString());

		//create new Cache for cycle
		CtxIdentifierCache cache = new CtxIdentifierCache();

		List<IC45Output> output = new ArrayList<IC45Output>();

		//get history
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = 
				historyRetriever.getFullHistory(startDate);

		if(history != null && history.size()>0){
			//store context attribute identifiers with types
			cache.cacheCtxIdentifiers(history);

			//System.out.println("Splitting history depending on serviceId and action");
			List<ServiceSubset>splitHistory = preProcessor.splitHistory(history);

			//for each service Identifier
			Iterator<ServiceSubset> splitHistory_it = splitHistory.iterator();
			while(splitHistory_it.hasNext()){

				ServiceSubset nextServiceSubset = (ServiceSubset)splitHistory_it.next();

				//remove consistent context attributes from history
				ServiceSubset trimmedHistory = preProcessor.trimServiceSubset(nextServiceSubset);

				//create new IC45Output object
				IC45Output nextOutput = new C45Output(historyOwner, 
						trimmedHistory.getServiceId(), 
						trimmedHistory.getServiceType());

				//run cycle for each action subset
				List<ActionSubset> actionSubsetList = trimmedHistory.getActionSubsets();
				Iterator<ActionSubset> actionSubsetList_it = actionSubsetList.iterator();
				while(actionSubsetList_it.hasNext()){
					ActionSubset nextActionSubset = (ActionSubset)actionSubsetList_it.next();

					//add subset tree to IC45Output object
					IPreferenceTreeModel treeModel = runCycle(nextActionSubset, cache, trimmedHistory.getServiceId(), trimmedHistory.getServiceType());
					//translate tree branches into ICtxIdentifiers
					if(treeModel!=null){
						nextOutput.addTree(treeModel);
					}
				}
				output.add(nextOutput);
			}
		}else{
			if (LOG.isDebugEnabled()){
				LOG.debug("No History found for historyOwner: "+historyOwner.toString());
			}
		}
		//send DPI based output to requestor
		LOG.info("RETURNING C45 OUTPUT TO: "+requestor.getClass().getName());
		try{
			requestor.handleC45Output(output);
        }catch(Exception e){
        	e.printStackTrace();
        	
        	if (LOG.isDebugEnabled()){
        		LOG.debug("The C45 requestor service is not available to handle response\nException: "+e.toString());
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
			System.out.println("No rules could be learned from the current history set\nException: "+e.toString());
			return null;
		}

		//convert tree strings into JTrees for output
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
