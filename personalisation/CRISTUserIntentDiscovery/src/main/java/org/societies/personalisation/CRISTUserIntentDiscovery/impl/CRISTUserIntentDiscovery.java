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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.ICRISTUserIntentDiscovery;

public class CRISTUserIntentDiscovery implements ICRISTUserIntentDiscovery {

	List<MockHistoryData> historyList = null;
	LinkedHashMap<String, Integer> intentModel = null;

	public void initialiseCRISTDiscovery() {
		System.out.println("Yo!! I'm a brand new service and my interface is: "
				+ this.getClass().getName());
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
	@Override
	public void generateNewCRISTUIModel() {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.societies.personalisation.CRIST.api.CRISTUserIntentDiscovery.
	 * ICRISTUserIntentDiscovery#generateNewCRISTUIModel(List)
	 */
	@Override
	public void generateNewCRISTUIModel(List historyData) {
		// TODO Auto-generated method stub
		this.historyList = historyData;
		constructModel();
	}

	private void constructModel() {
		int historySize = this.historyList.size();
		List<String> behaviorRecords = null;
		List<String> historyRecords = null;
		int maxPredictionStep = 3;

		// TODO
		// Construct User Intent Model based on one's history data
		// By mining user behavior patterns
		// Identify all the possible user behaviors
		for (int i = 0; i < historySize; i++) {
			MockHistoryData currentHisData = historyList.get(i);
			String currentHisAction = currentHisData.getActionValue();
			String currentHisSituation = currentHisData.getSituationValue();
			String currentBehavior = currentHisAction + "#"
					+ currentHisSituation;
			if (!behaviorRecords.contains(currentBehavior)) {
				behaviorRecords.add(currentBehavior);
			}
		}

		// Convert history data to the "Action#Situation" format
		for (int i = 0; i < historySize; i++) {
			MockHistoryData currentHisData = historyList.get(i);
			String currentHisAction = currentHisData.getActionValue();
			String currentHisSituation = currentHisData.getSituationValue();
			String currentBehavior = currentHisAction + "#"
					+ currentHisSituation;
			historyRecords.add(currentBehavior);
		}

		// Identify patterns
		int behaviorNum = behaviorRecords.size();
		for (int i = 0; i < behaviorNum; i++) {
			int[] indexList = getAllOccurences(historyRecords,
					behaviorRecords.get(i));
			List<String> cadidateActionList = null;
			for (int j = 0; j < indexList.length; j++) {
				String currentCadidate = null;
				for (int k = 1; k <= 3; k++) {
					int currentIndex = indexList[j] + k;
					if (indexList[j] + k < historySize) {
						currentCadidate = currentCadidate
								+ "#"
								+ historyList.get(currentIndex)
										.getActionValue();
					} else {
						break;
					}
				}
				cadidateActionList.add(currentCadidate);
			}

			for (int j = 0; j < cadidateActionList.size(); j++) {
				String currentActionPattern = behaviorRecords.get(i)
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
	}

	private int[] getAllOccurences(List<String> strList, String str) {

		List<Integer> indexList = null;
		List<String> localList = strList;
		int currentOffset = 0;
		while (localList.size() > 0) {
			int aIndex = localList.indexOf(str);
			if (aIndex < 0) {
				break;
			} else {
				indexList.add(currentOffset + aIndex);
				currentOffset += aIndex + 1;
				localList = localList.subList(aIndex + 1, localList.size());
			}
		}
		int[] localIndex = new int[indexList.size()];
		for (int i = 0; i < indexList.size(); i++) {
			localIndex[i] = indexList.get(i);
		}
		return localIndex;
	}
}
