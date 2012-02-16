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
package org.societies.personalisation.CRIST.api.model;

import java.util.HashMap;

/**
 * This class is responsible for defining the data structure of community user
 * intent model, which is mainly based on three basic classes:
 * CRISTCommunityAction, CRISTCommunitySituation, CRISTCommunityTask.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 17:56:58
 */
public class CRISTCommunityTaskModelData {

	/**
	 * Each CRISTCommunityAction object is linked with a list of other
	 * CRISTCommunityActions and the transition probability
	 */
	public HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>> communityActionList;
	/**
	 * Each CommunitySituationITSUD object is linked with a list of other
	 * CommunitySituations and the transition probability
	 */
	public HashMap<CRISTUserSituation, HashMap<ICRISTCommunitySituation, Double>> communitySituationList;
	/**
	 * Each CRISTCommunityTask object is linked with a list of other
	 * CRISTCommunityTasks and the transition probability
	 */
	public HashMap<CRISTUserTask, HashMap<CRISTUserTask, Double>> communityTaskList;

	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTCommunityTaskModelData() {

	}

	/**
	 * Constructor
	 * 
	 * @param taskList
	 * @param actionList
	 * @param situationList
	 */
	public CRISTCommunityTaskModelData(
			HashMap<ICRISTCommunityTask, HashMap<ICRISTCommunityTask, Double>> taskList,
			HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>> actionList,
			HashMap<ICRISTCommunitySituation, HashMap<ICRISTCommunitySituation, Double>> situationList) {

	}

	/**
	 * This method will retrieve all the ICIRSTCommunityActions in the current
	 * model, and each ICRISTCommunityAction is linked with a list of other
	 * ICRISTCommunityActions along the with transition probabilities
	 * 
	 * @return actionMap
	 */
	public HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>> getActionList() {
		HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>> actionMap = new HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>>();
		// TODO

		return actionMap;
	}

	/**
	 * This method will retrieve all the ICIRSTCommunitySituations in the current
	 * model, and each ICRISTCommunitySituatioins is linked with a list of other
	 * ICRISTCommunitySituations along the with transition probabilities
	 * 
	 * @return situationMap
	 */
	public HashMap<ICRISTCommunitySituation, HashMap<ICRISTCommunitySituation, Double>> getSituationList() {
		HashMap<ICRISTCommunitySituation, HashMap<ICRISTCommunitySituation, Double>> situationMap = new HashMap<ICRISTCommunitySituation, HashMap<ICRISTCommunitySituation, Double>>();
		// TODO

		return situationMap;
	}

	/**
	 * This method will retrieve all the ICIRSTCommunityTasks in the current
	 * model, and each ICRISTCommunityTasks is linked with a list of other
	 * ICRISTCommunityTasks along the with transition probabilities
	 * 
	 * @return taskMap
	 */
	public HashMap<ICRISTCommunityTask, HashMap<ICRISTCommunityTask, Double>> getTaskList() {
		HashMap<ICRISTCommunityTask, HashMap<ICRISTCommunityTask, Double>> taskMap = new HashMap<ICRISTCommunityTask, HashMap<ICRISTCommunityTask, Double>>();
		// TODO
		
		return taskMap;
	}

	/**
	 * This method sets the actionList of the current task model with the given actionList
	 * 
	 * @param actionList
	 */
	public void setActionList(
			HashMap<ICRISTCommunityAction, HashMap<ICRISTCommunityAction, Double>> actionList) {

	}

	/**
	 * This method sets the situationList of the current task model with the given situationList
	 * 
	 * @param situationList
	 */
	public void setSituationList(
			HashMap<ICRISTCommunitySituation, HashMap<ICRISTCommunitySituation, Double>> situationList) {

	}

	/**
	 * This method sets the taskList of the current task model with the given taskList
	 * 
	 * @param taskList
	 */
	public void setTaskList(
			HashMap<ICRISTCommunityTask, HashMap<ICRISTCommunityTask, Double>> taskList) {

	}

}