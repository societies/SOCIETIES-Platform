package org.societies.context.user.db.impl.model;
 
import java.io.Serializable;
import java.util.Date;
 
public class UserCtxEntityMgr implements Serializable {
	 
	private static final long serialVersionUID = 1L;

	private String operatorId;
	private String type;
	private long objectNumber;
	private Date lastModified;
	
	
	public UserCtxEntityMgr() {
	}

	public UserCtxEntityMgr(String operatorId, String type, long objectNumber, Date lastModified) {
		this.operatorId = operatorId;
		this.type = type;
		this.objectNumber = objectNumber;
		this.lastModified = lastModified;
	}

	public String getOperatorId() {
		return this.operatorId;
	}
	public void setOperatorId(String operatorId) {
		this.operatorId = operatorId;
	}

	public String getType() {
		return this.type;
	}
	
	public void setType(String type) {
		this.type = type;
	}

	public long getObjectNumber() {
		return this.objectNumber;
	}
	
	public void setObjectNumber(long objectNumber) {
		this.objectNumber = objectNumber;
	}
	
	public Date getLastModified () {
		return this.lastModified;
	}
	
	public void setLastModified (Date lastModified) {
		this.lastModified = lastModified;
	}

	@Override
	public String toString() {
		return "Entity [OperatorId=" + operatorId + ", Type=" + type
				+ ", ObjectNumber=" + objectNumber + ", LastModified=" + lastModified + "]";
	}

	
}