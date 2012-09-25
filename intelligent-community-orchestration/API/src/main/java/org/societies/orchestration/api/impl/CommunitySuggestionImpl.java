package org.societies.orchestration.api.impl;

import org.societies.api.internal.orchestration.ICommunitySuggestion;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created with IntelliJ IDEA.
 * User: Bjørn Magnus Mathisen
 * Date: 17.09.12
 * Time: 16:04
 */
public class CommunitySuggestionImpl implements ICommunitySuggestion, Serializable {
    private String suggestionType;
    private String name;
    private ArrayList<String> membersList;
    private ArrayList<String> conditionsList;
    public CommunitySuggestionImpl(){
        membersList = new ArrayList<String>();
        conditionsList = new ArrayList<String>();
    }
    @Override
    public String getSuggestionType() {
        return suggestionType;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public ArrayList<String> getMembersList() {
        return membersList;
    }

    @Override
    public ArrayList<String> getConditionsList() {
        return conditionsList;
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
    public void addMember(String member){
        membersList.add(member);
    }
}
