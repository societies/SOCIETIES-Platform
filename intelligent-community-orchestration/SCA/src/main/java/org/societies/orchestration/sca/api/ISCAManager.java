package org.societies.orchestration.sca.api;

import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseBean;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;

public interface ISCAManager {

	public void feedbackResult(String id, SCASuggestedResponseType feedbackResult);

	public void joinCIS(String cisID);

	public void leaveCISByInvitation(String requestID, String cisID);

	public INotificationHandler getNotificationHandler();

	public ISCARemote getSCARemote();

	public void handleInvitationResponse(String requestID, String fromJID, SCASuggestedResponseType response);

}