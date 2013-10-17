/**
 * Copyright (c) 2011, SOCIETIES Consortium
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

package org.societies.personalisation.CRISTUserIntentDiscovery.impl;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.ICRISTUserIntentDiscovery;
import org.societies.personalisation.CRIST.api.model.CRISTUserAction;

public class CRISTUserIntentDiscovery implements ICRISTUserIntentDiscovery {

	private static final Logger LOG = LoggerFactory.getLogger(CRISTUserIntentDiscovery.class);
	public static final int MAX_PREDICTION_STEP = 1;

	private LinkedHashMap<String, Integer> intentModel = new LinkedHashMap<String, Integer>();

	public CRISTUserIntentDiscovery(){
		//LOG.info("Hello! I'm the CRIST User Intent Discovery!");
	}

	
	public void initialiseCRISTDiscovery() {
		//LOG.info("Yo!! I'm a brand new service and my interface is: "	+ this.getClass().getName());
		//registerForContextChanges();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.
	 * ICRISTUserIntentDiscovery#enableCRISTUIDiscovery(boolean)
	 */
	@Override
	public void enableCRISTUIDiscovery(boolean bool) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.
	 * ICRISTUserIntentDiscovery#generateNewCRISTUIModel()
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public LinkedHashMap generateNewCRISTUIModel() {
		// TODO Auto-generated method stub
		// get context from broker?
		return intentModel;
	}

	/*
	 * (non-Javadoc)
	 * update intentModel at the same time.
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.
	 * ICRISTUserIntentDiscovery#generateNewCRISTUIModel(java.util.ArrayList)
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public LinkedHashMap generateNewCRISTUIModel(ArrayList historyData) {
		
		// here we learn from the whole history, so create new intentModel.
		// further we can learn incrementally.
		intentModel = new LinkedHashMap<String, Integer>();
		
		// TODO Auto-generated method stub
		//ensure not return null
		ArrayList<CRISTHistoryData> historyList = historyData;

		int historySize = historyList.size();
		ArrayList<String> behaviorRecords_unique = new ArrayList<String>();
		ArrayList<String> behaviorRecords = new ArrayList<String>();

		// TODO
		// Construct User Intent Model based on one's history data
		// By mining user behavior patterns
		// Identify all the possible user behaviors
		String lastActionID = "STARTING_ACTION";
		for (int i = 0; i < historySize; i++) {
			CRISTHistoryData currentHisData = historyList.get(i);
			
			String currentHisSituation = currentHisData.getSituation().getSituationID();
			String currentBehavior = lastActionID + "@"
					+ currentHisSituation;
			behaviorRecords.add(currentBehavior);
			if (!behaviorRecords_unique.contains(currentBehavior)) {
				behaviorRecords_unique.add(currentBehavior);
			}
			lastActionID = ((CRISTUserAction)currentHisData.getAction()).getActionID();
		}

		// Identify patterns
		int behaviorNum = behaviorRecords_unique.size();
		for (int i = 0; i < behaviorNum; i++) {
			int[] indexList = getAllOccurences(behaviorRecords,
					behaviorRecords_unique.get(i));
			ArrayList<String> cadidateActionList = new ArrayList<String>();
			for (int j = 0; j < indexList.length; j++) {
				String currentCadidate = "";
				for (int k = 0; k < MAX_PREDICTION_STEP; k++) {
					int currentIndex = indexList[j] + k;
					if (currentIndex >= historyList.size()) {
					      break;
					     }
					String situationID = historyList.get(currentIndex).getSituation().getSituationID();
					if (situationID == null) {
						//LOG.debug("situationID is null. delete it in historyList");
						historyList.remove(currentIndex);
						continue;
					}
					
					if (indexList[j] + k < historySize
							&& behaviorRecords_unique.get(i).endsWith(situationID)) {
						currentCadidate = currentCadidate
								+ "#" + 
						((CRISTUserAction)historyList.get(currentIndex).getAction()).getActionID();
					} 
					else {
						break;
					}
				}
				if (currentCadidate.length() > 0){
					cadidateActionList.add(currentCadidate);
				}
			}

			for (int j = 0; j < cadidateActionList.size(); j++) {
				String currentActionPattern = behaviorRecords_unique.get(i)
						+ cadidateActionList.get(j);
				if (this.intentModel.containsKey(currentActionPattern)) {
					Integer currentScore = this.intentModel
							.get(currentActionPattern);
					this.intentModel
							.put(currentActionPattern, currentScore + 1);
				} else {
					this.intentModel.put(currentActionPattern, 1);
				}
			}
		}
		
		return intentModel;
	}

	private int[] getAllOccurences(ArrayList<String> strList, String str) {

		ArrayList<Integer> indexList = new ArrayList<Integer>();
		List<String> localList = strList;
		int currentOffset = 0;

		while (localList.size() > 0) {
			int aIndex = localList.indexOf(str);
			if (aIndex < 0) {
				break;
			} else {
				indexList.add(currentOffset + aIndex);
				currentOffset += aIndex + 1;
				localList = localList.subList(aIndex + 1,
						localList.size());
			}
		}
		int[] localIndex = new int[indexList.size()];
		for (int i = 0; i < indexList.size(); i++) {
			localIndex[i] = indexList.get(i);
		}
		return localIndex;
	}
	
	
	
	

	
	
	
}
