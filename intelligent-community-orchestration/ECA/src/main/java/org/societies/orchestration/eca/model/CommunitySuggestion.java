package org.societies.orchestration.eca.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.societies.api.internal.orchestration.ICommunitySuggestion;

public class CommunitySuggestion implements ICommunitySuggestion, Serializable {

	private ArrayList<String> conditionsList;
	private ArrayList<String> membersList;
	private String name;
	private String suggestionType;

	@Override
	public ArrayList<String> getConditionsList() {
		// TODO Auto-generated method stub
		return this.conditionsList;
	}

	@Override
	public ArrayList<String> getMembersList() {
		// TODO Auto-generated method stub
		return this.membersList;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public String getSuggestionType() {
		// TODO Auto-generated method stub
		return this.suggestionType;
	}

	public void setConditionsList(ArrayList<String> conditionsList) {
		this.conditionsList = conditionsList;
	}

	public void setMembersList(ArrayList<String> membersList) {
		this.membersList = membersList;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setSuggestionType(String suggestionType) {
		this.suggestionType = suggestionType;
	}

	@Override
	public boolean equals(Object model) {
		if(model!=null) {
			if(model instanceof CommunitySuggestion) {
				if(((CommunitySuggestion) model).getName().equals(this.name)) {
					return true;
				}
			} else if(model instanceof ICommunitySuggestion) {
				if(((ICommunitySuggestion) model).getName().equals(this.name)) {
					return true;
				}
			}
		}
		return false;
	}
}
