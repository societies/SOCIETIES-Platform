package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.api.internal.personalisation.model.IOutcome;

public interface ICRISTUserSituation extends IOutcome{
	/**
	 * This method will link the given ICRISTUserAction list to the current situation
	 * along with the corresponding transition probabilities
	 * 
	 * @param userActions
	 */
	public void addActions(LinkedHashMap<ICRISTUserAction,Double> userActions);

	/**
	 * This method will link the given ICRISTUserTask list to the current situation
	 * along with the corresponding transition probabilities
	 * 
	 * @param userTasks
	 */
	public void addTasks(LinkedHashMap<ICRISTUserTask,Double> userTasks);

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTUserAction,Double> getSituatioinActions();

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
	public LinkedHashMap<ICRISTUserTask,Double> getSituationTasks();

	/**
	 * This method will assign the current situation context with the given situationContext  
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext);

	/**
	 * This method will assign the current situation ID with the given situationID  
	 * 
	 * @param situationID
	 */
	public void setSituationID(String situationID);

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString();
}