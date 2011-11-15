package org.societies.personalisation.CAUI.api.CAUITaskManager;

/**
 * @since 0.0.1
 * @author nikosk(ICCS)
 * @version 1.0
 * @created 15-Nov-2011 1:42:10 PM
 */

public interface ICAUITaskManager {

	
	/**
	 * Adds a set of userActions and transition probabilities to a UserTask.
	 * @return UserTask object
	 * 
	 * @param userTask    the task object where the userActions set will be added
	 * @param userActions    a linked hashmap with the action objects and the the
	 * transition probabilities
	 */
	public IUserIntentTask addActionsToTask(IUserIntentTask userTask, LinkedHashMap<IUserIntentAction,Double> userActions);

	/**
	 * Creates an UserAction object based on the specified string ID.
	 * @param parameter name
	 * @val value
	 * @return the UserAction object
	 * 
	 * @param par
	 * @param val
	 */
	public IUserIntentAction createAction(String par, String val);

	/**
	 * Creates a task and assigns a task id to it.
	 * @return the UserTask
	 * 
	 * @param taskID
	 */
	public IUserIntentTask createTask(String taskID);

	/**
	 * Creates a task, assigns a task id to it and also adds a list of Actions and
	 * transition probabilities to this task.
	 * @return UserTask object
	 * 
	 * @param taskID    the task identifier
	 * @param userActions    a linked hashmap with the action objects and the the
	 * transition probabilities
	 */
	public IUserIntentTask createTask(String taskID, LinkedHashMap<IUserIntentAction,Double> userActions);

	/**
	 * Returns the UserAction object of the specified id.
	 * @return the action object
	 * 
	 * @param actionID
	 */
	public IUserIntentAction getAction(String actionID);

	/**
	 * Returns a list of Actions from the model of the same type and value with these
	 * defined in the parameters.
	 * @return list of actions
	 * 
	 * @param par    the parameter name
	 * @param val    the value
	 */
	public List<IUserIntentAction> getActionsByType(String par, String val);

	/**
	 * Allows any service to request an context-based evaluated preference outcome.
	 * @return					the outcome in the form of an IAction object
	 * 
	 * @param requestor    the DigitalIdentity of the service requesting the outcome
	 * @param ownerID    the DigitalIdentity of the owner of the preferences (i.e. the
	 * user of this service)
	 * @param serviceID    the service identifier of the service requesting the
	 * outcome
	 * @param preferenceName    the name of the preference requested
	 */
	public IUserIntentAction getCurrentIntentAction(EntityIdentifier requestor, EntityIdentifier ownerID, ServiceResourceIdentifier serviceID, String preferenceName);

	/**
	 * Returns a map of next userActions and the relevant probabilities given the
	 * current action. Next userActions are only one step from the given action.
	 * @return map
	 * 
	 * @param userAction
	 */
	public HashMap<IUserAction, Double> getNextAction(IUserIntentAction userAction);

	/**
	 * Returns a map of next userTasks and the relevant probabilities given the
	 * current task. Next tasks are only one step from the given task.
	 * @return map
	 * 
	 * @param userTask
	 */
	public HashMap<String, Double> getNextTask(IUserIntentTask userTask);

	/**
	 * Returns the UserTask specified by the taskID
	 * @return the user task object
	 * 
	 * @param taskID
	 */
	public IUserTask getTask(String taskID);

	/**
	 * Returns the TaskModelData java object that contains the user intent model.
	 * @return the TaskModelData object
	 */
	public TaskModelData getTaskModelData();

	/**
	 * Returns a list of Tasks containing the specified action.
	 * @return list
	 * 
	 * @param userAction
	 */
	public List<IUserIntentTask> getTasks(IUserIntentAction userAction);

	/**
	 * Identifies a IUserAction and the corresponding IUserTask that has the same
	 * parameter name, value and context in User Intent model.
	 * @return map
	 * 
	 * @param par
	 * @param val
	 * @param currentContext
	 * @param lastAction
	 */
	public Map<IUserAction, IUserTask> identifyActionTaskInModel(String par, String val, HashMap<String, Serializable> currentContext, String[] lastAction);

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
	public IUserIntentAction identifyNextAction(Map<IUserIntentAction,IUserIntentTask> identifiedActionTaskMap, String previousPredictionsTaskID, Map<String,Serializable> userCurrentContext);

	/**
	 * Resets the task model.
	 */
	public void resetTaskModelData();

	/**
	 * Creates a weighted link between two Actions.
	 * @param targetActionID
	 * 
	 * @param sourceAction
	 * @param targetAction
	 * @param weigth    the transition probability
	 */
	public void setNextActionLink(IUserIntentAction sourceAction, IUserIntentAction targetAction, Double weigth);

	/**
	 * Creates a weighted link between two Tasks.
	 * 
	 * @param sourceTask
	 * @param targetTaskID
	 * @param weigth    weigth
	 */
	public void setNextTaskLink(IUserIntentTask sourceTask, String targetTaskID, Double weigth);

	/**
	 * Sets the task model. The task model has been previously retrieved from the
	 * context data base.
	 * 
	 * @param taskModelData    taskModelData
	 */
	public void setTaskModel(TaskModelData taskModelData);
}
