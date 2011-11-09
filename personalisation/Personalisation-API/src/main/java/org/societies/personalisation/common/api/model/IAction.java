package org.societies.personalisation.common.api;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;

/**
 * @author Elizabeth
 *
 */
public interface IAction extends Serializable{
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
	

}

