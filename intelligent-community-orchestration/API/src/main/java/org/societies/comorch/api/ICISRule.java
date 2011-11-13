
/**
 * This class represents a CIS subscription record.
 * (don't you mean CIS rule? - Fraser)
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package org.societies.comorch.api;

public interface ICISRule {
    
	// The current setting for notifying users on deleted CISs.
    private boolean notificationSetting;
    // The statuses of all CIS members.
    private char[] cssStatus;
    // The time interval that passes before the trigger to perform actions is invoked.
    private double intervalTrigger;
    // The possibility of each CIS member to alter the permission settings of the CIS.
    private boolean[] permissionSettings;
    // The CIS that the CISRule applies for.
    private CIS cis;
    // The current permissions for the CIS.
    private Permissions permission;

    /*
     * Constructur for ICISRule.
     */
    
	public ICISRule();

	/*
	 * Description: The setDeletedCISsNotification setting changes the current
	 *              setting for whether to notify  CIS members when relevant CISs are deleted(?),
	 *              based on the input.
	 * Parameters: 
	 * 				1) boolean - the new notification setting.
	 * Returns:
	 * 				* True if the method was able to set the deleted CISs notification setting.
	 *				* False if the method was unable to set the deleted CISs notification setting.
	 */
	
	public boolean setDeletedCISsNotification(boolean notificationSetting);
	
	/*
	 * Description: The setCSSStatus method changes the current status of a particular CSS
	 *              that is a member of the relevant CIS, based on the input.
	 * Parameters: 
	 * 				1) char - the new status of a CSS.
	 * Returns:
	 * 				* True if the method was able to set the CSS status.
	 *				* False if the method was unable to set the CSS status.
	 */
	
	public boolean setCSSStatus(char cssStatus);
	
	/*
	 * Description: The setIntervalTrigger method changes the current time interval
	 *              that must pass before a trigger is activated(?)
	 * Parameters: 
	 * 				1) boolean - the new interval trigger.
	 * Returns:
	 * 				* True if the method was able to set the interval trigger.
	 *				* False if the component was unable to set the interval trigger.
	 */
	
	public boolean setIntervalTrigger(double intervalTrigger);
	
	/*
	 * Description: The setPermissions method changes the current permission
	 *              settings for the CIS, based on the input.
	 * Parameters: 
	 * 				1) double - the new permission settings.
	 * Returns:
	 * 				* True if the method was able to set the permissions.
	 *				* False if the method was unable to set the permissions.
	 */
	
	public boolean setPermissions(boolean permissionSettings);
	
	/*
	 * Description: The setDeletedCISsNotification method invokes the configureCIS method 
	 * 				inside the Community Lifecycle Management component 
	 * 				in order to change parameters of a specific CIS.
	 * Parameters: 
	 * 				1) CIS - The CIS for which the setting is being changed.
	 * 				2) permission - The permission data necessary to carry out the operation.
	 * Returns:
	 * 				* True if the method was able to set the permissions.
	 *              * False if the method was unable to set the permissions.
	 */
	
	public boolean setDeletedCISsNotification(CIS cis, Permissions permission);

}
