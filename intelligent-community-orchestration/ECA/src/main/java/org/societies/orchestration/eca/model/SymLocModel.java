package org.societies.orchestration.eca.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class SymLocModel {
	
	private List<VisitModel> myVisits;
	private HashMap<String, List<VisitModel>> userVisits;
	
	
	public SymLocModel() {
		myVisits = new ArrayList<VisitModel>();
		userVisits = new HashMap<String,  List<VisitModel>>();
	}
	
	public void addCurrentUserVisit(VisitModel model) {
		myVisits.add(model);
	}
	
	public void addRelatedUserVisit(String userJID, VisitModel model) {
		if(userVisits.containsKey(userJID)) {
			userVisits.get(userJID).add(model);
		} else {
			List<VisitModel> userVisitModel = new ArrayList<VisitModel>();
			userVisitModel.add(model);
			userVisits.put(userJID, userVisitModel);
		}
	}
	
	public List<VisitModel> getMyVisits() {
		return myVisits;
	}

	public void setMyVisits(List<VisitModel> myVisits) {
		this.myVisits = myVisits;
	}

	public HashMap<String, List<VisitModel>> getUserVisits() {
		return userVisits;
	}

	public void setUserVisits(HashMap<String, List<VisitModel>> userVisits) {
		this.userVisits = userVisits;
	}	
	
	

}
