package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.personalisation.common.api.model.IOutcome;

/**
 * This interface consists of the public methods to manage the CRISTUserAction
 * class.
 * @author Zhu WANG
 * @version 1.0
 * @created 14-Nov-2011 16:53:13
 */
public interface ICRISTUserAction extends IOutcome {

	public Map<String, Serializable> getActionContext();

	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public String getActionID();

	public LinkedHashMap<ICRISTUserSituation, Double> getActionSituations();

	public int getConfidenceLevel();

	/**
	 * 
	 * @param context
	 */
	public void setActionContext(HashMap<String, Serializable> context);

	/**
	 * 
	 * @param actionSituations
	 */
	public void setActionSituations(HashMap<ICRISTUserSituation,Double> actionSituations);

	/**
	 * 
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel);

	public String toString();

}