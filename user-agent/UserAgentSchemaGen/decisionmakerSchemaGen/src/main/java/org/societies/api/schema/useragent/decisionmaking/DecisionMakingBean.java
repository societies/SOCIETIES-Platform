package org.societies.api.schema.useragent.decisionmaking;

import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public class DecisionMakingBean {
	private List<ServiceResourceIdentifier> intentServiceIds;
	private List<String> intentServiceTypes;
	private List<String> intentParameterNames; //IOutcome.parameterName
	private List<String> intentValues;  //IOutcome.value
	private List<ServiceResourceIdentifier> preferenceServiceIds;
	private List<String> preferenceServiceTypes;
	private List<String> preferenceParameterNames; //IOutcome.parameterName
	private List<String> preferenceValues;  //IOutcome.value
	private List<Integer> preferenceConfidenceLevel;
	private List<Integer> intentConfidenceLevel;
	private int intentSize;
	private int preferenceSize;
	public  DecisionMakingBean(){}
//	public  DecisionMakingBean(List<IOutcome> intents,List<IOutcome> preferences){
//		this.intentSize=intents.size();
//		this.preferenceSize=preferences.size();
//		this.intentServiceIds=new ArrayList<ServiceResourceIdentifier>();
//		this.intentParameterNames=new ArrayList<String>();
//		this.intentServiceTypes=new ArrayList<String>();
//		this.preferenceServiceIds=new ArrayList<ServiceResourceIdentifier>();
//		this.preferenceParameterNames=new ArrayList<String>();
//		this.preferenceServiceTypes=new ArrayList<String>();
//		for(int i=0;i<intentSize;i++){
//			this.intentServiceIds.add(intents.get(i).getServiceID());
//			this.intentServiceTypes.add(intents.get(i).getServiceType());
//			this.intentParameterNames.add(intents.get(i).getparameterName());
//			this.intentConfidenceLevel.add(intents.get(i).getConfidenceLevel());
//		}
//		for(int i=0;i<preferenceSize;i++){
//			this.preferenceServiceIds.add(preferences.get(i).getServiceID());
//			this.preferenceServiceTypes.add(preferences.get(i).getServiceType());
//			this.preferenceParameterNames.add(preferences.get(i).getparameterName());
//			this.preferenceConfidenceLevel.add(preferences.get(i).getConfidenceLevel());
//		}
//	}

	public int getIntentSize() {
		return intentSize;
	}

	public void setIntentSize(int intentSize) {
		this.intentSize = intentSize;
	}

	public int getPreferenceSize() {
		return preferenceSize;
	}

	public void setPreferenceSize(int preferenceSize) {
		this.preferenceSize = preferenceSize;
	}
	
	public List<ServiceResourceIdentifier> getIntentServiceIds() {
		return intentServiceIds;
	}
	public void setIntentServiceIds(List<ServiceResourceIdentifier> intentServiceIds) {
		this.intentServiceIds = intentServiceIds;
	}
	public List<String> getIntentServiceTypes() {
		return intentServiceTypes;
	}
	public void setIntentServiceTypes(List<String> intentServiceTypes) {
		this.intentServiceTypes = intentServiceTypes;
	}
	public List<String> getIntentParameterNames() {
		return intentParameterNames;
	}
	public void setIntentParameterNames(List<String> intentParameterNames) {
		this.intentParameterNames = intentParameterNames;
	}
	public List<String> getIntentValues() {
		return intentValues;
	}
	public void setIntentValues(List<String> intentValues) {
		this.intentValues = intentValues;
	}
	public List<ServiceResourceIdentifier> getPreferenceServiceIds() {
		return preferenceServiceIds;
	}
	public void setPreferenceServiceIds(
			List<ServiceResourceIdentifier> preferenceServiceIds) {
		this.preferenceServiceIds = preferenceServiceIds;
	}
	public List<String> getPreferenceServiceTypes() {
		return preferenceServiceTypes;
	}
	public void setPreferenceServiceTypes(List<String> preferenceServiceTypes) {
		this.preferenceServiceTypes = preferenceServiceTypes;
	}
	public List<String> getPreferenceParameterNames() {
		return preferenceParameterNames;
	}
	public void setPreferenceParameterNames(List<String> preferenceParameterNames) {
		this.preferenceParameterNames = preferenceParameterNames;
	}
	public List<String> getPreferenceValues() {
		return preferenceValues;
	}
	public void setPreferenceValues(List<String> preferenceValues) {
		this.preferenceValues = preferenceValues;
	}
	public List<Integer> getPreferenceConfidenceLevel() {
		return preferenceConfidenceLevel;
	}
	public void setPreferenceConfidenceLevel(List<Integer> preferenceConfidenceLevel) {
		this.preferenceConfidenceLevel = preferenceConfidenceLevel;
	}
	public List<Integer> getIntentConfidenceLevel() {
		return intentConfidenceLevel;
	}
	public void setIntentConfidenceLevel(List<Integer> intentConfidenceLevel) {
		this.intentConfidenceLevel = intentConfidenceLevel;
	}

}
