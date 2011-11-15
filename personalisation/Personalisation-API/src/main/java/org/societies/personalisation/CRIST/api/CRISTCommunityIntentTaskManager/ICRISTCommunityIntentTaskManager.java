package org.societies.personalisation.CRIST.api.CRISTCommunityIntentTaskManager;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;

import org.societies.personalisation.CRIST.api.model.CRISTCommunityTaskModelData;
import org.societies.personalisation.CRIST.api.model.ICRISTCommunityAction;
import org.societies.personalisation.CRIST.api.model.ICRISTCommunitySituation;
import org.societies.personalisation.CRIST.api.model.ICRISTCommunityTask;

/**
 * @author Zhu WANG
 * @version 1.0
 */
public interface ICRISTCommunityIntentTaskManager {

	/**
	 * This method will retrieve the community action based on the given action ID
	 * 
	 * @param actionID		- the ID of the given community action
	 */
	public ICRISTCommunityAction getCommunityAction(String actionID);
	
	/**
	 * This method will retrieve a list of community actions based on the given action type and value
	 * 
	 * @param actionType	- the type of user action
	 * @param actionValue	- the value of user action
	 */
	public ArrayList<ICRISTCommunityAction> getCommunityActionsByType(String actionType, String actionValue);
	
	/**
	 * This method will retrieve the community situation based on the given situation ID
	 * 
	 * @param situationID		- the ID of the given community situation
	 */
	public ICRISTCommunitySituation getCommunitySituation(String situationID);
	
	/**
	 * This method returns the community task according to the given task ID
	 * 
	 * @param taskID		- the ID of the given community task
	 */
	public ICRISTCommunityTask getCommunityTask(String taskID);
	
	/**
	 * This method returns a map of next communityActions and the relevant probabilities given the
	 * current community action. Next communityActions are only one step from the given action.
	 *  
	 * @param communityAction	- the given community action
	 */
	public HashMap<ICRISTCommunityAction, Double> getNextCommunityActions(ICRISTCommunityAction communityAction);
	
	/**
	 * This method returns a map of next community tasks and the relevant probabilities given the
	 * current community task. Next community tasks are only one step from the given task.
	 * 
	 * @param communityTask		- the given community task
	 */
	public HashMap<ICRISTCommunityTask, Double> getNextCommunityTasks(ICRISTCommunityTask communityTask);
	
	/**
	 * This method will identify all the community actions based on historical recordings 
	 */
	public ArrayList<ICRISTCommunityAction> identifyCommunityActions();
	
	/**
	 * This method will identify all the community situations based on historical recordings 
	 */
	public ArrayList<ICRISTCommunitySituation> identifyCommunitySituations();
	
	/**
	 * This method will identify all the community tasks 
	 */
	public ArrayList<ICRISTCommunityTask> identifyCommunityTasks();
	
	/**
	 * This method resets the task model.
	 */
	public void resetTaskModelData();
	
	/**
	 * This method sets the task model according to the given model.
	 * 
	 * @param taskModelData		- the given task model
	 */
	public void setTaskModelData(CRISTCommunityTaskModelData taskModelData);
}
