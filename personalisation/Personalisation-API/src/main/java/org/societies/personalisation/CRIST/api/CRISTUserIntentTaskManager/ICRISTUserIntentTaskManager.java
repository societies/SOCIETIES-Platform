package org.societies.personalisation.CRIST.api.CRISTUserIntentTaskManager;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import org.societies.personalisation.common.api.model.EntityIdentifier;
import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;
import org.societies.personalisation.CRIST.api.model.ICRISTUserAction;
import org.societies.personalisation.CRIST.api.model.ICRISTUserTask;
import org.societies.personalisation.CRIST.api.model.ICRISTUserSituation;
import org.societies.personalisation.CRIST.api.model.CRISTUserTaskModelData;

/**
* @author Zhu WANG
* @version 1.0
* @created 14-Nov-2011 18:15:15
*/
public interface ICRISTUserIntentTaskManager {
	/**
	 * This method will link the related user actions and user situations to the given user task
	 * 
	 * @param userTask			- the task object where the userActions set will be added
	 * @param userActions		- a linked hashmap with the action objects and the transition probabilities
	 * @param userSituations	- a linked hashmap with the situation objects and the transition probabilities 
	 */
	public ICRISTUserTask addSituationsAndActionsToTask(ICRISTUserTask userTask, LinkedHashMap<ICRISTUserAction,Double> userActions, LinkedHashMap<ICRISTUserSituation,Double> userSituations);

	/**
	 * This method will retrieve the user action based on the given action ID
	 * 
	 * @param actionID		- the ID of the given action
	 */
	public ICRISTUserAction getAction(String actionID);
	
	/**
	 * This method will retrieve a list of user actions based on the given action type and value
	 * 
	 * @param actionType	- the type of user action
	 * @param actionValue	- the value of user action
	 */
	public ArrayList<ICRISTUserAction> getActionsByType(String actionType, String actionValue);

	/**
	 * This method will retrieve the user's current intent action
	 * 
	 * @param requestor		- the ID of the requestor of the current user intent action
	 * @param ownerID		- the ID of the owner of the current user intent action
	 * @param serviceID		- the ID of the related service 
	 */
	public ICRISTUserAction getCurrentIntentAction(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID);

	/**
	 * This method will retrieve the user's current action
	 */
	public ICRISTUserAction getCurrentUserAction();

	/**
	 * This method will retrieve the user's current situation
	 */
	public ICRISTUserSituation getCurrentUserSituation();

	/**
	 * This method returns a map of next userActions and the relevant probabilities given the
	 * current action. Next userActions are only one step from the given action.
	 *  
	 * @param userAction	- the given user action
	 */
	public HashMap<ICRISTUserAction, Double> getNextActions(ICRISTUserAction userAction);

	/**
	 * This method returns a map of next user tasks and the relevant probabilities given the
	 * current task. Next user tasks are only one step from the given task.
	 * 
	 * @param userTask		- the given user task
	 */
	public HashMap<ICRISTUserTask, Double> getNextTasks(ICRISTUserTask userTask);
	
	/**
	 * This method returns the user task according to the given task ID
	 * 
	 * @param taskID		- the ID of the given task
	 */
	public ICRISTUserTask getTask(String taskID);
	
	/**
	 * This method returns the CRIST User Task Model Data object that contains the user intent model.
	 * 
	 * @return the CRISTUserTaskModelData object
	 */
	public CRISTUserTaskModelData getTaskModelData();

	/**
	 * This method returns the related user tasks according to the given user action and the user situation
	 * 
	 * @param userAction	- the given user action
	 * @param userSituation	- the given user situation
	 */
	public ArrayList<ICRISTUserTask> getTasks(ICRISTUserAction userAction, ICRISTUserSituation userSituation);
	
	/**
	 * This method will identify all the user actions based on historical recordings 
	 */
	public ArrayList<ICRISTUserAction> identifyActions();

	/**
	 * This method identifies user tasks accroding to the given action type and value
	 * 
	 * @param actionType	- the type of the given user action
	 * @param actionValue	- the value of the gien user actioin
	 * @param currentContext	- a set of the current context 
	 */
	public HashMap<ICRISTUserAction, ICRISTUserTask> identifyActionTaskInModel(String actionType, String actionValue, HashMap<String, Serializable> currentContext);

	/**
	 * This method will identify all the user situations based on historical recordings 
	 */
	public ArrayList<ICRISTUserSituation> identifySituations();

	/**
	 * This method will identify all the user tasks 
	 */
	public ArrayList<ICRISTUserTask> identifyTasks();

	/**
	 * This method resets the task model.
	 */
	public void resetTaskModelData();

	/**
	 * This method will set up the link between the two given actions 
	 * 
	 * @param sourceAction	- the source action
	 * @param targetAction	- the target action
	 * @param weigth		- the probability that the target action becomes the 
	 * next action of the given source action
	 */
	public void setNextActionLink(ICRISTUserAction sourceAction, ICRISTUserAction targetAction, Double weigth);

	/**
	 * This method will set up the link between the two given situations 
	 * 
	 * @param sourceSituation	- the source situation
	 * @param targetSituation	- the target situation
	 * @param weigth			- the probability that the target situation becomes the 
	 * next situation of the given source situation
	 */
	public void setNextSituationLink(ICRISTUserSituation sourceSituation, ICRISTUserSituation targetSituation, Double weigth);
	
	/**
	 * This method will set up the link between the two given tasks 
	 * 
	 * @param sourceTask	- the source task
	 * @param targetTask	- the target task
	 * @param weigth		- the probability that the target task becomes the 
	 * next task of the given source task
	 */
	public void setNextTaskLink(ICRISTUserTask sourceTask, ICRISTUserTask targetTask, Double weigth);
}
