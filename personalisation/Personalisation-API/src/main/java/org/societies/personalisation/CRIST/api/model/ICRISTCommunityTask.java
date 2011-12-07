package org.societies.personalisation.CRIST.api.model;


import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface ICRISTCommunityTask extends IOutcome{
	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * community task along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			LinkedHashMap<ICRISTCommunityAction, Double> communityActions);

	/**
	 * This method will link the given ICRISTCommunitySituation list to the
	 * current community task along with the corresponding transition
	 * probabilities
	 * 
	 * @param taskSituations
	 */
	public void addSituations(
			LinkedHashMap<ICRISTCommunitySituation, Double> taskSituations);
	/**
	 * This method will return the related actions of the current community task
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTCommunityAction, Double> getActions();

	/**
	 * This method will return the related context information of the current
	 * community task
	 * 
	 * @return
	 */
	public Map<String, Serializable> getTaskContext();

	/**
	 * This method will return the ID of the current community task
	 * 
	 * @return
	 */
	public String getTaskID();
	
	/**
	 * This method will return the related situations of the current community
	 * task
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTCommunitySituation, Double> getTaskSituations();

	/**
	 * This method will set the related context information of the current
	 * community task with the given taskContext
	 * 
	 * @param taskContext
	 */
	public void setTaskContext(Map<String, Serializable> taskContext);

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString();
}