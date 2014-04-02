package org.societies.orchestration.eca;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.orchestration.eca.model.SymLocModel;
import org.societies.orchestration.eca.model.VisitModel;

public class ModelManager {
	
	private Logger log = LoggerFactory.getLogger(ModelManager.class);
	
	private HashMap<String, SymLocModel> symlocModels;
	
	
	//MODEL MANAGER NEEDS TO BE SYNCHRONIZED AS IT IS CALLED FROM CTXCHANGELISTENERS
	public ModelManager() {
		this.symlocModels = new HashMap<String, SymLocModel>();
	}
	
	public synchronized boolean locationWatched(String location) {
		return this.symlocModels.keySet().contains(location);
	}
	
	public synchronized void addCurrentUserModel(VisitModel visitModel, String location) {
		if(!locationWatched(location)) {
			log.debug("Adding a new model to start watching this location for this local node!");
			SymLocModel symLocModel = new SymLocModel();
			symLocModel.addCurrentUserVisit(visitModel);
			this.symlocModels.put(location, symLocModel);
		} else {
			log.debug("This location is being watched for this location, adding the dates to model");
			this.symlocModels.get(location).getMyVisits().add(visitModel);
		}
	}
	
	public synchronized void addRemoteUserModel(VisitModel visitModel, String location, String userJID) {
		if(locationWatched(location)) {
			log.debug("Adding a remote user entry to our watched location!");
			this.symlocModels.get(location).addRelatedUserVisit(userJID, visitModel);
		}
	}


}
