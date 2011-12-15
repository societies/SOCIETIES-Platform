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
import java.util.List;
import java.util.Map;

import org.societies.api.context.model.CtxHistoryAttribute;
import org.societies.api.mock.EntityIdentifier;
import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.personalisation.UserPreferenceLearning.impl.HistoryRetriever;
import org.societies.personalisation.preference.api.UserPreferenceLearning.IC45Consumer;

//import weka.classifiers.trees.Id3;
//import weka.core.Instances;
//import weka.core.UnsupportedAttributeTypeException;

public class SA_SI extends Thread{

	private IC45Consumer requestor;
	private Date startDate;
	private EntityIdentifier historyOwner;
	private ServiceResourceIdentifier serviceId;
	private String parameterName;
	private HistoryRetriever historyRetriever;
	//private OutputResponse responder;
	//private PreProcessor preProcessor;
	//private PostProcessor postProcessor;

	public SA_SI(IC45Consumer requestor, Date startDate, EntityIdentifier historyOwner, 
			ServiceResourceIdentifier serviceId, String parameterName, HistoryRetriever historyRetriever){
		this.requestor = requestor;
		this.startDate = startDate;
		this.historyOwner = historyOwner;
		this.serviceId = serviceId;
		this.parameterName = parameterName;
		this.historyRetriever = historyRetriever;

		//historyRetriever = new HistoryRetriever(bc);
		//responder = new OutputResponse();

		//preProcessor = new PreProcessor();
		//postProcessor = new PostProcessor(); 
	}

	@Override
	public void run(){
		/*System.out.println("C45 REQUEST FROM: "+requestor.getClass().getName());
		logging.info("Starting C45Learning process for DPI: "+dpi.toString()+" on action: "+parameterName+" for serviceId: "+serviceId.toString());

		//create new Cache for cycle
		CtxIdentifierCache cache = new CtxIdentifierCache();

		List<IC45Output> output = new ArrayList<IC45Output>();
*/
		
		//get history
		Map<CtxHistoryAttribute, List<CtxHistoryAttribute>> history = 
			historyRetriever.getHistory();
/*
		if(history != null && history.size()>0){
			//store context attribute identifiers with types
			cache.cacheCtxIdentifiers(dpi, history);

			//extract instances with serviceId and action
			logging.info("Extracting "+parameterName+" actions for service "+serviceId.toString()+" from history");
			ServiceSubset serviceSubset = 
				preProcessor.extractServiceActions(history, serviceId, parameterName);

			//remove consistent context attributes from history
			ServiceSubset trimmedHistory = preProcessor.trimServiceSubset(serviceSubset);

			List<ActionSubset> actionSubsetList = trimmedHistory.getActionSubsets();
			ActionSubset actionSubset = (ActionSubset)actionSubsetList.get(0);

			IC45Output nextOutput = new C45Output(dpi, serviceId, trimmedHistory.getServiceType());

			if(actionSubset.size()>0){
				IPreferenceTreeModel treeModel = runCycle(dpi, actionSubset, cache, serviceId, trimmedHistory.getServiceType());
				if(treeModel!=null){
					nextOutput.addTree(treeModel);
				}
				output.add(nextOutput);
			}
		}else{
			logging.warn("No History found DPI: "+dpi.toString());
		}
		//send DPI based output to requestor
		System.out.println("RETURNING C45 OUTPUT TO: "+requestor.getClass().getName());
		responder.sendC45Output(requestor, output);*/
	}

	/*
	 * Algorithm methods
	     
	private IPreferenceTreeModel runCycle(
			IDigitalPersonalIdentifier dpi, 
			ActionSubset input, 
			CtxIdentifierCache cache,
			IServiceIdentifier serviceId,
			String serviceType){

		//convert to Instances for each serviceId
		Instances instances = preProcessor.toInstances(input);

		logging.info("C45 executing...");
		String outputString = null;
		try{
			outputString = executeAlgorithm(instances);
		} catch (Exception e) {
			System.out.println("No rules could be learned from the current history set");
			return null;
		}

		//convert tree strings into JTrees for output
		String paramName = input.getParameterName();
		return (IPreferenceTreeModel)postProcessor.process(dpi, paramName, outputString, cache, serviceId, serviceType);
	}

	private String executeAlgorithm(Instances input)throws Exception
	{
		Id3 id3 = new Id3();
		//c45 = new C45PruneableClassifierTree
		//(model, false, 0, false, false);

		input.setClassIndex(input.numAttributes()-1);

		//c45.buildClassifier(input);
		id3.buildClassifier(input);

		System.out.println("ID3 output: "+id3.toString());

		return id3.toString();
	}*/
}
