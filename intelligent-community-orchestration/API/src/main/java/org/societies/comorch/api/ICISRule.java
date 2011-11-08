
/**
 * This class represents a CIS subscription record.
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package org.societies.comorch.api;

public interface ICISRule {
    
    private boolean notificationSetting;
    private char cssStatus;
    private boolean intervalTrigger;
    private boolean permissionSettings;
    private CIS cis;
    private Permissions permission;

	public ICISRule();

	public void setDeletedCISsNotification(boolean notificationSetting);
	
	public void setCSSStatus(char cssStatus);
	
	public void setIntervalTrigger(boolean intervalTrigger);
	
	public void setPermissions(boolean permissionSettings);
	
	public void setDeletedCISsNotification(CIS cis, Permissions permission);

}
