package org.societies.personalisation.CACI.api.CACITaskManager;

public interface ICACITaskManager {

	
	/**
	 * 
	 * @param type
	 */
	public void createCommAction(String type);

	/**
	 * 
	 * @param type
	 */
	public void createCommTask(String type);

	/**
	 * 
	 * @param actionID
	 */
	public double getActionCommLevel(String actionID);

	/**
	 * 
	 * @param commActID
	 * @param type
	 */
	public List <ICommAction> getCommActByType(String commActID, String type);

	/**
	 * 
	 * @param actionID
	 */
	public ICommIntentAction getCommAction(String actionID);

	/**
	 * 
	 * @param taskID
	 */
	public ICommIntentTask getCommTask(String taskID);

	/**
	 * 
	 * @param currentComAction
	 */
	public IUserIntentAction getNextCommAction(ICommIntentAction currentComAction);

	/**
	 * 
	 * @param currentComTask
	 */
	public IUserIntentTask getNextCommTask(ICommIntentTask currentComTask);

	/**
	 * 
	 * @param actionID
	 */
	public double getTaskCommLevel(String actionID);

	/**
	 * 
	 * @param commTaskID
	 * @param listCommActions
	 */
	public void populateCommTask(String commTaskID, List<ICommAction> listCommActions);

	public void resetTaskModel()();

	/**
	 * 
	 * @param modelId
	 */
	public TaskModelData retrieveModel(String modelId);

	/**
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setActionCommLevel(String actionID, Double commonalityLevel);

	/**
	 * 
	 * @param actionID
	 * @param commonalityLevel
	 */
	public void setTaskCommLevel(String actionID, Double commonalityLevel);

}
