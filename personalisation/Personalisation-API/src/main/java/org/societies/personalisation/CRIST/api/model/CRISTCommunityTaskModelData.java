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