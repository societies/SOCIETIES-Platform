package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.societies.personalisation.CAUI.api.model.IUserIntentAction;
import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

/**
 * This class is responsible for defining the data structure of individual user
 * intent model, which is mainly based on three basic classes: CRISTUserAction,
 * CRISTUserSituation, CRISTUserTask.
 * @author Zhu WANG
 * @version 1.0
 * @created 14-Nov-2011 18:15:15
 */
public class CRISTUserTaskModelData {

	/**
	 * Each CRISTUserAction object is linked with a list of other CRISTUserActions and
	 * the transition probability
	 */
	public HashMap<CRISTUserAction,HashMap<CRISTUserAction,Double>> actionList;
	/**
	 * Each CRISTUserSituation object is linked with a list of other
	 * CRISTUserSituations and the transition probability
	 */
	public HashMap<CRISTUserSituation,HashMap<CRISTUserSituation,Double>> situationList;
	/**
	 * Each CRISTUserTask object is linked with a list of other CRISTUserTasks and the
	 * transition probability
	 */
	public HashMap<CRISTUserTask,HashMap<CRISTUserTask,Double>> taskList;



	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTUserTaskModelData(){

	}

	/**
	 * Constructor
	 * 
	 * @param taskList
	 * @param actionList
	 * @param situationList
	 */
	public CRISTUserTaskModelData(HashMap<ICRISTUserTask,HashMap<ICRISTUserTask, Double>> taskList, HashMap<ICRISTUserAction,HashMap<ICRISTUserAction,Double>> actionList, HashMap<ICRISTUserSituation,HashMap<ICRISTUserSituation,Double>> situationList){

	}

	/**
	 * 
	 * @param userTask    the task object where the userActions set will be added
	 * @param userActions    a linked hashmap with the action objects and the the
	 * transition probabilities
	 * @param userSituations
	 */
	public ICRISTUserTask addSituationsAndActionsToTask(ICRISTUserTask userTask, LinkedHashMap<ICRISTUserAction,Double> userActions, LinkedHashMap<ICRISTUserSituation,Double> userSituations){
		return null;
	}

	public HashMap<ICRISTUserAction,HashMap<ICRISTUserAction,Double>> getActionList(){
		return null;
	}

	/**
	 * 
	 * @param requestor
	 * @param ownerID
	 * @param serviceID
	 * @param name
	 */
	public ICRISTUserAction getCurrentIntentAction(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID, String name){
		return null;
	}

	public ICRISTUserSituation getCurrentUserSituation(){
		return null;
	}

	/**
	 * Returns a map of next userActions and the relevant probabilities given the
	 * current action. Next userActions are only one step from the given action.
	 * @return map
	 * 
	 * @param userAction
	 */
	public HashMap<ICRISTUserAction, Double> getNextAction(ICRISTUserAction userAction){
		return null;
	}

	public HashMap<CRISTUserSituation,HashMap<ICRISTUserSituation,Double>> getSituationList(){
		return null;
	}

	public HashMap<ICRISTUserTask,HashMap<String,Double>> getTaskList(){
		return null;
	}

	/**
	 * Returns the TaskModelData java object that contains the user intent model.
	 * @return the TaskModelData object
	 */
	public CRISTUserTaskModelData getTaskModelData(){
		return null;
	}

	/**
	 * 
	 * @param par
	 * @param val
	 */
	public ICRISTUserAction identifyAction(String par, String val){
		return null;
	}

	/**
	 * Given the current actions in model determine the next action based on
	 * transition probabilities, if previous action was in the same task  and action's
	 * associated context  Score(A->B) = a*P(A->B) + b*(belongInSameTask) +
	 * c*contextSimilarity  The confidence level of the returned action is set based
	 * on calculated score.
	 * @return predicted action
	 * 
	 * @param identifiedActionTaskMap
	 * @param previousPredictionsTaskID
	 * @param userCurrentContext
	 */
	public ICRISTUserAction identifyNextAction(HashMap<ICRISTUserAction,ICRISTUserTask> identifiedActionTaskMap, String previousPredictionsTaskID, HashMap<String,Serializable> userCurrentContext){
		return null;
	}
	
	/**
	 * 
	 * @param par
	 * @param val
	 */
	public ICRISTUserSituation identifySituation(String par, String val){
		return null;
	}

	/**
	 * 
	 * @param par
	 * @param val
	 */
	public ICRISTUserTask identifyTask(String par, String val){
		return null;
	}

	public ICRISTUserAction processRequest(){
		return null;
	}

	/**
	 * Resets the task model.
	 */
	public void resetTaskModelData(){

	}

	/**
	 * 
	 * @param actionList
	 */
	public void setActionList(HashMap<ICRISTUserAction,HashMap<ICRISTUserAction,Double>> actionList){

	}

	/**
	 * 
	 * @param situationList
	 */
	public void setSituationList(HashMap<ICRISTUserSituation,HashMap<CRISTUserSituation,Double>> situationList){

	}

	/**
	 * 
	 * @param taskList
	 */
	public void setTaskList(HashMap<ICRISTUserTask,HashMap<ICRISTUserTask,Double>> taskList){

	}

}