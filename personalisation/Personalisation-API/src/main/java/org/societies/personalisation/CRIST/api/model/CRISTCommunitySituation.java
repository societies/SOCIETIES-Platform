package org.societies.personalisation.CRIST.api.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.societies.api.servicelifecycle.model.IServiceResourceIdentifier;

/**
 * This class is used to define the data structure and methods for modelling and
 * managing community situations. Meanwhile, it is also responsible for
 * establishing the relationship between community situation and community
 * actions as well as the relationship between community situations and
 * community tasks.
 * 
 * @author Zhu WANG
 * @version 1.0
 * @created 28-Nov-2011 21:08:55
 */
public class CRISTCommunitySituation implements ICRISTCommunitySituation {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	HashMap<ICRISTCommunityAction, Double> communitySituationActions;
	Map<String, Serializable> communitySituationContext;
	String communitySituationID;
	HashMap<ICRISTCommunityTask, Double> communitySituationTasks;
	public CRISTCommunityTask m_CRISTCommunityTask;

	public void finalize() throws Throwable {

	}

	/**
	 * Constructor
	 */
	public CRISTCommunitySituation() {

	}

	/**
	 * Constructor
	 * 
	 * @param situationID
	 */
	public CRISTCommunitySituation(String situationID) {

	}

	/**
	 * This method will link the given ICRISTCommunityAction list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityActions
	 */
	public void addActions(
			HashMap<ICRISTCommunityAction, Double> communityActions) {

	}

	/**
	 * This method will link the given ICRISTCommunityTask list to the current
	 * situation along with the corresponding transition probabilities
	 * 
	 * @param communityTasks
	 */
	public void addTasks(
			HashMap<ICRISTCommunityTask, Double> communityTasks) {

	}

	/**
	 * This method will return the related actions of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityAction, Double> getSituatioinActions() {
		return this.communitySituationActions;
	}

	/**
	 * This method will return the related context information of the current
	 * situation
	 * 
	 * @return
	 */
	public Map<String, Serializable> getSituationContext() {
		return this.communitySituationContext;
	}

	/**
	 * This method will return the ID of the current situation
	 * 
	 * @return
	 */
	public String getSituationID() {
		return this.communitySituationID;
	}

	/**
	 * This method will return the related tasks of the current situation
	 * 
	 * @return
	 */
	public HashMap<ICRISTCommunityTask, Double> getSituationTasks() {
		return this.communitySituationTasks;
	}

	/**
	 * This method will assign the current situation context with the given
	 * situationContext
	 * 
	 * @param situationContext
	 */
	public void setSituationContext(Map<String, Serializable> situationContext) {
		this.communitySituationContext = situationContext;
	}

	/**
	 * This method will return the contents of this task in a String mode
	 */
	public String toString() {
		return this.toString();
	}

	@Override
	public int getConfidenceLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

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
	public IServiceResourceIdentifier getServiceID() {
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
	public void setServiceID(IServiceResourceIdentifier id) {
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

}