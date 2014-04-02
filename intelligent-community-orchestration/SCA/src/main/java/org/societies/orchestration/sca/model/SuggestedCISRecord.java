package org.societies.orchestration.sca.model;

import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;

public class SuggestedCISRecord {

	private ICommunitySuggestion cisSuggestion;
	private SCASuggestedMethodType methodType;
	private String cisID;

	public SuggestedCISRecord(ICommunitySuggestion cisSuggestion) {

		this.cisSuggestion = cisSuggestion;

	}

	public ICommunitySuggestion getCisSuggestion() {
		return cisSuggestion;
	}

	public void setCisSuggestion(ICommunitySuggestion cisSuggestion) {
		this.cisSuggestion = cisSuggestion;
	}

	public SCASuggestedMethodType getMethodType() {
		return methodType;
	}

	public void setMethodType(SCASuggestedMethodType methodType) {
		this.methodType = methodType;
	}

	/**
	 * @return the cisID
	 */
	public String getCisID() {
		return cisID;
	}

	/**
	 * @param cisID the cisID to set
	 */
	public void setCisID(String cisID) {
		this.cisID = cisID;
	}




}