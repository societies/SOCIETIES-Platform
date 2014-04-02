package org.societies.orchestration.eca.model;

import java.util.Date;

public class VisitModel {
	
	private Date entryDate;
	private Date leaveDate;
	
	public VisitModel() {
		this.entryDate=null;
		this.leaveDate=null;
	}
	
	public Date getEntryDate() {
		return entryDate;
	}
	public void setEntryDate(Date entryDate) {
		this.entryDate = entryDate;
	}
	public Date getLeaveDate() {
		return leaveDate;
	}
	public void setLeaveDate(Date leaveDate) {
		this.leaveDate = leaveDate;
	}
	
	public boolean hasEntryDate() {
		if(this.entryDate==null) {
			return false;
		}
		return true;
	}
	
	

}
