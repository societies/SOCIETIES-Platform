package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.personalisation.common.api.model.IOutcome;

public interface ICRISTCommunityAction extends IOutcome{
	public Map<String, Serializable> getActionContext();

	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public String getActionID();

	public LinkedHashMap<ICRISTCommunitySituation, Double> getActionSituations();

	public int getConfidenceLevel();

	/**
	 * 
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel);

	public String toString();
}