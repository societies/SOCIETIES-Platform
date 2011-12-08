package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

import org.societies.api.mock.ServiceResourceIdentifier;
import org.societies.api.personalisation.model.Action;

/**
 * This class is used to define the data structure and methods for modelling and
 * managing community actions. Meanwhile, it is also responsible for
 * establishing the relationship between community actions and community
 * situations.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 21:18:50
 */
public class CRISTCommunityAction extends Action implements
		ICRISTCommunityAction {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Map<String, Serializable> communityActionContext;
	String communityActionID;
	/**
	 * A list of situations and the corresponding probability related to the
	 * CommunityIntentAction
	 */
	LinkedHashMap<ICRISTCommunitySituation, Double> communityActionSituations;
	/**
	 * The confidence level of the CommunityIntentAction
	 */
	private int confidenceLevel;
	public CRISTCommunityTask m_CRISTCommunityTask;

	public void finalize() throws Throwable {
		super.finalize();
	}

	/**
	 * Constructor
	 */
	public CRISTCommunityAction() {

	}

	/**
	 * Constructor
	 * 
	 * @param actionID
	 */
	public CRISTCommunityAction(String actionID) {

	}

	/**
	 * This method will link the given situations with the current community
	 * action
	 * 
	 * @param actionSituations
	 */
	public void addActionSituations(
			LinkedHashMap<ICRISTCommunitySituation, Double> actionSituations) {

	}

	/**
	 * This method will return the related context information of the current
	 * action
	 * 
	 * @return
	 */
	public Map<String, Serializable> getActionContext() {
		return this.communityActionContext;
	}

	/**
	 * This method will return the ID of the current action
	 * 
	 * @return
	 */
	public String getActionID() {
		return this.communityActionID;
	}

	/**
	 * This method will return the related situations of the current action
	 * 
	 * @return
	 */
	public LinkedHashMap<ICRISTCommunitySituation, Double> getActionSituations() {
		return this.communityActionSituations;
	}

	/**
	 * This method will return the confidence level of the current action
	 */
	public int getConfidenceLevel() {
		return this.confidenceLevel;
	}

	/**
	 * This method will set the related context information of the current
	 * action with the given context
	 * 
	 * @param context
	 */
	public void setActionContext(Map<String, Serializable> context) {
		this.communityActionContext = context;
	}

	/**
	 * This method will assign the given confidenceLevel as the confidence level
	 * of the current action
	 * 
	 * @param confidenceLevel
	 */
	public void setConfidenceLevel(int confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}

	/**
	 * This method will return the contents of this action in a String mode
	 */
	public String toString() {
		return this.toString();
	}

	/**
	 * @return the name of the action (i.e. volume)
	 */
	public String getparameterName() {
		return "";
	}

	/**
	 * @return any other names this action might also be called
	 */
	public ArrayList<String> getparameterNames() {
		return null;
	}

	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public ServiceResourceIdentifier getServiceID() {
		return null;
	}

	/**
	 * @return the type of service this action can be applied to
	 */
	public String getServiceType() {
		return "";
	}

	/**
	 * @return a list of alternative types of service this action can be applied
	 *         to.
	 */
	public ArrayList<String> getServiceTypes() {
		return null;
	}

	/**
	 * @return the value of this action (i.e. if the action is volume then the
	 *         value would be an int from 0 to 100
	 */
	public String getvalue() {
		return "";
	}

	/**
	 * 
	 * @param id
	 *            the identifier of the service this action is applied to
	 */
	public void setServiceID(ServiceResourceIdentifier id) {

	}

	/**
	 * 
	 * @param type
	 *            the type of service this action is applied to
	 */
	public void setServiceType(String type) {

	}

	/**
	 * 
	 * @param types
	 *            a list of alternative types this action can be applied to
	 */
	public void setServiceTypes(ArrayList<String> types) {

	}
}