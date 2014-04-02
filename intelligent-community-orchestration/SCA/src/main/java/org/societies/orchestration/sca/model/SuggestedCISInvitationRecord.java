package org.societies.orchestration.sca.model;

import java.util.HashMap;
import java.util.List;
import java.util.Set;

import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedResponseType;

/**
 * Describe your class here...
 *
 * @author Jiannis
 *
 */
public class SuggestedCISInvitationRecord {

	private String requestID;

	private HashMap<String, SCASuggestedResponseType> affectedMembers;

	private SCASuggestedMethodType methodType;

	public SuggestedCISInvitationRecord() {

	}

	public String getRequestID() {
		return requestID;
	}

	public void setRequestID(String requestID) {
		this.requestID = requestID;
	}

	public HashMap<String, SCASuggestedResponseType> getAffectedMembers() {
		return affectedMembers;
	}

	public void setAffectedMembers(List<String> affectedMembersList) {
		this.affectedMembers = new HashMap<String, SCASuggestedResponseType>();
		for(String user : affectedMembersList) {
			this.affectedMembers.put(user, SCASuggestedResponseType.PENDING);
		}
	}

	public boolean setUserResponse(String userJID, SCASuggestedResponseType response) {
		this.affectedMembers.put(userJID, response);
		Set<String> userJids = this.affectedMembers.keySet();
		boolean allResponded = true;
		for(String user : userJids) {
			if(this.affectedMembers.get(user).equals(SCASuggestedResponseType.PENDING)) {
				allResponded = false;
			}
		}
		return allResponded;
	}

	public SCASuggestedMethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(SCASuggestedMethodType methodType) {
		this.methodType = methodType;
	}










}