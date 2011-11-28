package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.personalisation.common.api.model.IOutcome;

public interface ICRISTUserTask extends IOutcome{
	/**
	 * This method will link the given ICRISTUserAction list to the current task
	 * along with the corresponding transition probabilities
	 * 
	 * @param userActions
	 */
	public void addActions(LinkedHashMap<ICRISTUserAction, Double> userActions);

	/**
	 * This method will link the given ICRISTUserSituation list to the current
	 * task along with the corresponding transition probabilities
	 * 
	 * @param taskSituations
	 */
	public void addSituations(
			LinkedHashMap<ICRISTUserSituation, Double> taskSituations);

	/**
	 * This method will return the related actions of the current task
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTUserAction, Double> getActions();

	/**
	 * This method will return the related context information of the current
	 * task
	 * 
	 * @return
	 */
	public Map<String, Serializable> getTaskContext();

	/**
	 * This method will return the ID of the current task
	 * 
	 * @return
	 */
	public String getTaskID();

	/**
	 * This method will return the related situations of the current task
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTUserSituation, Double> getTaskSituations();

	/**
	 * This method will set the related context information of the current task
	 * with the given taskContext
	 * 
	 * @param taskContext
	 */
	public void setTaskContext(Map<String, Serializable> taskContext);

	/**
	 * This method will set the ID of the current task with the given taskID
	 * 
	 * @param taskID
	 */
	public void setTaskID(String taskID);

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString();
}