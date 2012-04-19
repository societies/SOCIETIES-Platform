package org.societies.context.user.refinement.impl.bayesianLibrary.inference.solving.utils;

public class EvidenceRecord {
	String nodeName;
	String evidence;
	
	EvidenceRecord(String nN,String e){
		nodeName=nN;
		evidence=e;
	}
	
	public String getNodeName(){
		return nodeName;
	}
	
	public String getEvidence(){
		return evidence;
	}
}
