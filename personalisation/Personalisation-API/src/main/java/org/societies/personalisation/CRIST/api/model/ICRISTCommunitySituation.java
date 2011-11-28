package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.personalisation.common.api.model.IOutcome;

public interface ICRISTCommunitySituation extends IOutcome{
	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			LinkedHashMap<ICRISTCommunityAction, Double> communityActions);

	/**
	 * This method will link the given ICRISTCommunityTask list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityTasks
	 */
	public void addTasks(
			LinkedHashMap<ICRISTCommunityTask, Double> communityTasks);

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTCommunityAction, Double> getSituatioinActions();

	/**
	 * This method will return the related context information of the current
	 * situation
	 * 
	 * @return
	 */
	public Map<String, Serializable> getSituationContext();

	/**
	 * This method will return the ID of the current situation
	 * 
	 * @return
	 */
	public String getSituationID();

	/**
	 * This method will return the related tasks of the current situation
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTCommunityTask, Double> getSituationTasks();

	/**
	 * This method will assign the current situation context with the given
	 * situationContext
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext);

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString();
}