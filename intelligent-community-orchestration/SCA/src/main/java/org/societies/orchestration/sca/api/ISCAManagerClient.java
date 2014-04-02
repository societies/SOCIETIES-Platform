package org.societies.orchestration.sca.api;

import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;
import org.societies.orchestration.sca.model.SCAInvitation;

public interface ISCAManagerClient {

	public void addInvitation(String requestID, SCAInvitation invitation); 

	public void processInvitation(String requestID, SCASuggestedResponseType result);
}