package org.societies.android.api.useragent.model;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;

public interface IOutcome extends Serializable{
	
	public int getConfidenceLevel();
	/**
	 * 
	 * @return the value of this action (i.e. if the action is volume then the value would be an int from 0 to 100
	 */
	public String getvalue();
	
	/**
	 * 
	 * @return the name of the action (i.e. volume)
	 */
	public String getparameterName();
	
	/**
	 * 
	 * @return any other names this action might also be called
	 */
	public ArrayList<String> getparameterNames();
	
	/**
	 * @return the identifier of the service to which this action is applied to
	 */
	public ServiceResourceIdentifier getServiceID();
	
	/**
	 * 
	 * @return the type of service this action can be applied to
	 */
	public String getServiceType();
	
	/**
	 * 
	 * @return a list of alternative types of service this action can be applied to.
	 */
	public List<String> getServiceTypes();
	
	/**
	 * @param id	the identifier of the service this action is applied to
	 */
	public void setServiceID(ServiceResourceIdentifier id);
	
	/**
	 * 
	 * @param type 	the type of service this action is applied to
	 */
	public void setServiceType(String type);
	
	/**
	 * 
	 * @param types		a list of alternative types this action can be applied to
	 */
	public void setServiceTypes(List<String> types);
	

	/**
	 * Indicates if this action can be implemented or is only used as a conditional action for 
	 * triggering User Intent sequences. These types of actions are created by the UAM and represent 
	 * actions such as joined/left CIS, started/stopped service etc. Other such functions of the 
	 * platform may be added in the future
	 * @return	true if the action can be implemented, false if not. 
	 */
	public boolean isImplementable();
	
	/**
	 * Indicates whether this action should be implemented proactively (by the DecisionMaker) or not. 
	 * The user will have the ability to make this change manually using the preference GUI (T6.5 webapp Profile Settings).
	 * 3p services can also indicate if this action should be implemented when they create actions and send them to 
	 * the UAM component.  
	 * @return	true if the action should be proactively implemented, false if not. 
	 */
	public boolean isProactive();
	
	/**
	 * Indicates whether this action should be learnt or stored as a static preference. 3p services can create 
	 * static preferences (such as configuration settings) that do not depend on changing context. The UAM makes sure not to 
	 * store this action in the context history and avoid learning on this.  
	 * @return	true if the action should be context dependent, false if it shouldn't. Note that this will not return false 
	 * if the action does not currently have context conditions attached but was created as contextDependent 
	 */
	public boolean isContextDependent();
	
	
	
}
