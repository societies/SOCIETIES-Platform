package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils;

import java.util.ArrayList;

public class EvidencePack {
	
	String bayesletID;
	ArrayList<EvidenceRecord> evidencesList;

	EvidencePack(String bayesletName){
		bayesletID=bayesletName;
		evidencesList=new ArrayList();
	}
	void setBayesletID(String input){
		bayesletID=input;
	}
	
	void addEvidence(EvidenceRecord nE){
		evidencesList.add(nE);
	}
	
	ArrayList getEvidences(){
		return evidencesList;
	}
	
	String getBayesletName(){
		return bayesletID;
	}
}
