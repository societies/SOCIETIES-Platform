package org.societies.comorch.api;

import java.util.ArrayList;

public interface IUserInput {
	
	public boolean Configure (Object CIS, Object details);
	
	public boolean createCISs (ArrayList<Object> CISs);
	
	public boolean deleteCISs (ArrayList<Object> CISs);
	
	public Object getCISInfo (Object CIS);
	
	public ArrayList<Object> getMyCISs ();
	
	public ArrayList<Object> getRecommendedCISs ();
	
	public Object getUserInfo (Object CSS);
	
	public ArrayList<Object> searchAvailableCISs (String filter);
	
	public Object sendInvitations (ArrayList<Object> CSSnodes, Object CIS);
	
	public boolean setCSSStatus (String status);
	
	public boolean setDeletedCISsNotification (boolean notifier);
	
	public boolean setIntervalTrigger (long milliseconds);
	
	public boolean setPermissions (Object CIS, Object permissions);
}
