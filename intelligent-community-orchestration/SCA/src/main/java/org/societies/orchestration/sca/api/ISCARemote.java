package org.societies.orchestration.sca.api;

import org.societies.api.internal.orchestration.ICommunitySuggestion;


public interface ISCARemote {
	/**
	 * Sends an invitation for another user to join a CIS
	 * @param userJID    the JID of the user to receive the invitation
	 * @param suggestedCIS	the suggested CIS ID for the user to join
	 */
	public void sendJoinSuggestion(String userJID, String suggestedCIS);
	
	/**
	 * Sends an invitation for another user to leave a CIS
	 * @param userJID    the JID of the user to receive the invitation
	 * @param suggestedCIS	the suggested CIS ID for the user to leave
	 */
	public void sendLeaveSuggestion(String userJID, String suggestedCIS);

}
