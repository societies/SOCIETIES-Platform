package org.societies.comorch.ui;

import java.util.ArrayList;
import java.util.List;

import org.societies.comorch.api.IUserInput;
import org.societies.comorch.api.IUserNotification;

public class UserInterface implements IUserInput, IUserNotification{

	@Override
	public boolean Configure(Object CIS, Object details) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean createCISs(ArrayList<Object> CISs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteCISs(ArrayList<Object> CISs) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object getCISInfo(Object CIS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getMyCISs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> getRecommendedCISs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getUserInfo(Object CSS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Object> searchAvailableCISs(String filter) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object sendInvitations(ArrayList<Object> CSSnodes, Object CIS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean setCSSStatus(String status) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setDeletedCISsNotification(boolean notifier) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setIntervalTrigger(long milliseconds) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setPermissions(Object CIS, Object permissions) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean showRecommendedCISes(List<Object> CISes) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeRecommendedCISes(List<Object> CISes) {
		// TODO Auto-generated method stub
		return false;
	}

}