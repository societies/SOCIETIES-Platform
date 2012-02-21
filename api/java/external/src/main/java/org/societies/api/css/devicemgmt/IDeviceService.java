package org.societies.api.css.devicemgmt;


public interface IDeviceService {
	
	 /**
     * 
     * @param actionName
     * @return null if no action matches the argument actionName.
     */
    public IAction getAction (String actionName);
    
    /**
     * 
     * @return
     */
    public IAction [] getActions();
    
    /**
     * 
     * @param stateVariableName
     * @return null if no state variable matches the agrument stateVariableName
     */
    public IDeviceStateVariable getStateVariable (String stateVariableName);
    
    /**
     * 
     * @return
     */
    public IDeviceStateVariable [] getStateVariables ();

    
    /**
     * 
     * @return
     */
    public String getId ();
    
}
