package org.societies.orchestration.sca.api;

import java.util.List;

import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;

public interface INotificationHandler {
	
	/**
	 * Create a Join UF Notification
	 * @param id    The UF Notification ID
	 * @param cisName	The name of the affected CIS
	 * @param affectedUsers	The list of affected users of the CIS manipulation. Use null if empty.
	 */
	public void sendJoinNotification(String id, String cisName, List<String> affectedUsers);
	
	/**
	 * Create a Create UF Notification
	 * @param id    The UF Notification ID
	 * @param cisName	The name of the affected CIS
	 * @param affectedUsers	The list of affected users of the CIS manipulation. Use null if empty.
	 */
	public void sendCreateNotification(String id, String cisName, List<String> affectedUsers);
	
	/**
	 * Create a Join Leave UF Notification
	 * @param id    The UF Notification ID
	 * @param cisName	The name of the affected CIS
	 * @param affectedUsers	The list of affected users of the CIS manipulation. Use null if empty.
	 */
	public void sendLeaveNotification(String id, String cisName, List<String> affectedUsers);
	
	/**
	 * Create a Join Delete UF Notification
	 * @param id    The UF Notification ID
	 * @param cisName	The name of the affected CIS
	 * @param affectedUsers	The list of affected users of the CIS manipulation. Use null if empty.
	 */
	public void sendDeleteNotification(String id, String cisName, List<String> affectedUsers);
	
	/**
	 * Create a UF notification inviting the user to CREATE/JOIN/LEAVE/DELETE a CIS.
	 * This method should be called when a message has been received from another nodes SCA
	 * manager.
	 * @param id    The UF Notification ID
	 * @param cisName	The name of the affected CIS
	 * @param fromJID	The JID of the user who send the invitation.
	 * @param methodType	The type of CIS manipulation requested.
	 */
	public void addInvitationNotification(String id, String cisName, String fromJID, SCASuggestedMethodType methodType);

	/**
	 * Sends a UF message to the current user
	 * @param message	The message to be sent
	 */
	public void sendMessage(String message);
	
	public void setClient(ISCAManagerClient scaMgrClient);
}
