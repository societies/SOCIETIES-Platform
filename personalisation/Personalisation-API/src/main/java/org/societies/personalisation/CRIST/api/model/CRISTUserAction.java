package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.societies.personalisation.common.api.model.ServiceResourceIdentifier;

public class CRISTUserAction implements ICRISTUserAction{

	@Override
	public String getvalue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getparameterName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<String> getparameterNames() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ServiceResourceIdentifier getServiceID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getServiceType() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<String> getServiceTypes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceID(ServiceResourceIdentifier id) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServiceType(String type) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServiceTypes(List<String> types) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public HashMap<String, Serializable> getActionContext() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getActionID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public LinkedHashMap<ICRISTUserSituation, Double> getActionSituations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getConfidenceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setActionContext(HashMap<String, Serializable> context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int setConfidenceLevel(int confidenceLevel) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setActionSituations(
			HashMap<ICRISTUserSituation, Double> actionSituations) {
		// TODO Auto-generated method stub
		
	}
	
}