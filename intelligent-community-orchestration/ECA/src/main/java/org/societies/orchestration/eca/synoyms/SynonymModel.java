package org.societies.orchestration.eca.synoyms;

import java.util.HashSet;
import java.util.Set;

public class SynonymModel {

	private final String keyword;
	private Set<String> synoyms;
	private String decision;


	public SynonymModel(String keyword) {
		this.synoyms = new HashSet<String>();
		this.synoyms.add(keyword);
		this.keyword = keyword;
		this.decision = null;
	}
	public void addSynoym(String synoym) {
		this.synoyms.add(synoym);
	}

	//TODO Quick conflict resolution for multiple css's
	public void setDecision(String decision) {
		if(this.decision==null) {
			this.decision=decision;
		} else {
			this.decision=this.keyword;
		}
	}

	public String getDecision() {
		return this.decision;
	}
	public String getKeyword() {
		return keyword;
	}
	public Set<String> getSynoyms() {
		//return synoymToMatches.keySet();
		return this.synoyms;
	}

}
