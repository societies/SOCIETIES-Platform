package org.societies.personalisation.common.api;

/**
 * @author Elizabeth
 * @version 1.0
 * @created 08-Nov-2011 13:25:58
 */
public enum FeedbackTypes {
	/**
	 * IMPLEMENTED		is sent when the action was successfully implemented USER_ABORTED
	 * is sent when the user aborted the action using the Feedback GUI
	 * CONFLICT_RESOLVED is sent when the conflict resolution was used
	 * SERVICE_UNREACHABLE is sent when the DecisionImplementer was not able to
	 * contact the service SERVICE_DECISION is sent when the service has been
	 * contacted and the FeedbackEvent contains the result returned from the service
	 * SYSTEM_ERROR		is sent when the DecisionImplementer is not able to get
	 * references to Framework services IPssManager and IServiceLifeCycleManager or if
	 * the EventManagement system indicates that there has been an error when
	 * receiving the result from the service
	 */
	IMPLEMENTED,
	USER_ABORTED,
	CONFLICT_RESOLVED,
	SERVICE_UNREACHABLE,
	SERVICE_DECISION,
	SYSTEM_ERROR
}