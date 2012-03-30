package org.societies.useragent.dmcomms;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.internal.personalisation.model.IOutcome;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class IOutcomeDO implements IOutcome{
	private ServiceResourceIdentifier serviceId;
	private String servieType;
	private String parameterName;
	private int confidenceLevel;
	private String value;
	
	public IOutcomeDO(ServiceResourceIdentifier serviceId, String servieType,
			String parameterName,  String value,int confidenceLevel) {
		super();
		this.serviceId = serviceId;
		this.servieType = servieType;
		this.parameterName = parameterName;
		this.confidenceLevel = confidenceLevel;
		this.value = value;
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
	public String getvalue() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setServiceID(ServiceResourceIdentifier arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServiceType(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setServiceTypes(List<String> arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getConfidenceLevel() {
		// TODO Auto-generated method stub
		return this.confidenceLevel;
	}

}
