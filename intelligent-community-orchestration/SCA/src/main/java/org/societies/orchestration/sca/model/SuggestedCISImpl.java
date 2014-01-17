package org.societies.orchestration.sca.model;

import java.io.Serializable;
import java.util.ArrayList;

import org.societies.api.internal.orchestration.ICommunitySuggestion;
import org.societies.api.schema.orchestration.sca.scasuggestedcisbean.SCASuggestedMethodType;

public class SuggestedCISImpl implements ICommunitySuggestion, Serializable {

	private String suggestionType;
	private String name;
	private ArrayList<String> membersList;
	private ArrayList<String> conditionsList;
	
	public SuggestedCISImpl() {
		
	}	

	@Override
	public String getSuggestionType() {
		// TODO Auto-generated method stub
		return this.suggestionType;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public ArrayList<String> getMembersList() {
		// TODO Auto-generated method stub
		return this.membersList;
	}

	@Override
	public ArrayList<String> getConditionsList() {
		// TODO Auto-generated method stub
		return this.conditionsList;
	}
	
	public void setSuggestionType(String suggestionType) {
		this.suggestionType = suggestionType;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setMembersList(ArrayList<String> membersList) {
		this.membersList = membersList;
	}

	public void setConditionsList(ArrayList<String> conditionsList) {
		this.conditionsList = conditionsList;
	}
	

	

}
