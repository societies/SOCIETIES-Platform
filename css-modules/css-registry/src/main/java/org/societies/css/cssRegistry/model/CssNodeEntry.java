package org.societies.css.cssRegistry.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;


/**
 * This is the Class accepted by the CssRegistry when a css wants to register nodes.
 * 
 */

@Entity
@Table(name = "CssNodeEntry")
public class CssNodeEntry implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8165777485272476371L;
	
	private String identity;
	private int status;
	private int type;
	private boolean archived;
	/**
	 * @return the identity
	 */
	@Column(name = "Identity")
	public String getIdentity() {
		return identity;
	}
	/**
	 * @param identity the identity to set
	 */
	public void setIdentity(String identity) {
		this.identity = identity;
	}
	/**
	 * @return the status
	 */
	@Column(name = "Status")
	public int getStatus() {
		return status;
	}
	/**
	 * @param status the status to set
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * @return the type
	 */
	@Column(name = "Type")
	public int getType() {
		return type;
	}
	/**
	 * @param type the type to set
	 */
	public void setType(int type) {
		this.type = type;
	}
	
	/**
	 * @return the archived flag
	 */
	@Column(name = "Archived")
	public boolean getArchived() {
		return archived;
	}
	/**
	 * @param type the type to set
	 */
	public void setArchived(boolean archived) {
		this.archived = archived;
	}
	
	/**
	 * @param identity
	 * @param status
	 * @param type
	 */
	public CssNodeEntry(String identity, int status, int type, boolean archived) {
		this.identity = identity;
		this.status = status;
		this.type = type;
		this.archived = archived;
	}
    

	

	
}
