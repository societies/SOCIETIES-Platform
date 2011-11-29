package org.societies.personalisation.CACI.api.CACITaskManager;

import java.util.List;

import org.societies.personalisation.CAUI.api.model.CommunityIntentAction;
import org.societies.personalisation.CAUI.api.model.CommunityIntentTask;
import org.societies.personalisation.CAUI.api.model.TaskModelData;
import org.societies.personalisation.CAUI.api.model.UserIntentAction;
import org.societies.personalisation.CAUI.api.model.UserIntentTask;


/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICACITaskManager {

	
	/**
	 * This method allows the creation of a community Action
	 * @param type
	 */
	public void createCommAction(String type);

	/**
	 * This method allows the creation of a community Task
	 * @param type
	 */
	public void createCommTask(String type);

	/**
	 * This method returns the level of commonality for  a community action.
	 * 
	 * @param actionID
	 */
	public double getActionCommLevel(String actionID);

	/**
	 * This method returns a list of Community Action objects based on the defined type.
	 * 
	 * @param type
	 */
	public List <CommunityIntentAction> getCommActByType(String type);

	/**
	 * This method returns a Community Action object based on the action ID.
	 * 
	 * @param actionID
	 */
	public CommunityIntentAction getCommAction(String actionID);

	/**
	 * This method returns a Community Task object based on the action ID.
	 * 
	 * @param taskID
	 */
	public CommunityIntentTask getCommTask(String taskID);

	/**
	 * This method returns the next Community Action object as it is defined in CAUI Model.
	 * 
	 * @param currentComAction
	 */
	public UserIntentAction getNextCommAction(CommunityIntentAction currentComAction);

	/**
	 * This method returns the next Community Task object as it is defined in CAUI Model.
	 * 
	 * @param currentComTask
	 */
	public UserIntentTask getNextCommTask(CommunityIntentTask currentComTask);

	/**
	 * This method returns the level of commonality for  a community task.
	 * 
	 * @param actionID
	 */
	public double getTaskCommLevel(String actionID);

	/**
	 * This methods allows to add User Actions to User Task.
	 * 
	 * @param commTaskID
	 * @param listCommActions
	 */
	public void populateCommTask(String commTaskID, List<CommunityIntentAction> listCommActions);

	/**
	 * resets the active task model.
	 */
	public void resetTaskModel();

	/**
	 * Retrieves a model based on an id.
	 * 
	 * @param modelId
	 */
	public TaskModelData retrieveModel(String modelId);

	/**
	 * This method sets the level of commonality for  a community action.
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setActionCommLevel(String actionID, Double commonalityLevel);

	/**
	 *  This method sets the level of commonality for a community task.
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setTaskCommLevel(String actionID, Double commonalityLevel);

}
