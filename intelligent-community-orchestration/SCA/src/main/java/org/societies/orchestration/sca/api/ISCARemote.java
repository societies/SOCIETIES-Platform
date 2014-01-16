package org.societies.orchestration.sca.api;

import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.orchestration.sca.model.SCAInvitation;


public interface ISCARemote {
	/**
	 * Sends an invitation for another user to join a CIS
	 * @param userJID    the JID of the user to receive the invitation
	 * @param cisName	the CIS name of this suggestion
	 * @param suggestedCIS	the suggested CIS ID for the user to join
	 */
	public void sendJoinSuggestion(String ID, String userJID, String cisName, String suggestedCIS);
	
	/**
	 * Sends an invitation for another user to leave a CIS
	 * @param userJID    the JID of the user to receive the invitation
	 * @param suggestedCIS	the suggested CIS ID for the user to leave
	 */
	public void sendLeaveSuggestion(String ID, String userJID, String suggestedCIS);
	
	/**
	 * Sends an response for the invitation back to the original for another user
	 * @param ID	the original request ID
	 * @param toJID    the JID of the user to receive the response
	 * @param result	the response of the suggestion
	 */
	public void sendSuggestionResponse(String ID, String toJID, SCASuggestedResponseType response);
	

}
