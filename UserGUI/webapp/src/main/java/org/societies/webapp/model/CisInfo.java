/**
 * 
 */
package org.societies.webapp.model;

/**
 * @author mmanniox
 *
 */
public class CisInfo {
	
	String cisid;
	String cisname;
	
	
	public String getCisid() {
		return cisid;
	}
	public void setCisid(String cisid) {
		this.cisid = cisid;
	}
	public String getCisname() {
		return cisname;
	}
	public void setCisname(String cisname) {
		this.cisname = cisname;
	}
	
	
	public CisInfo() {
		super();
	}
	
	public CisInfo(String cisid, String cisname) {
		super();
		this.cisid = cisid;
		this.cisname = cisname;
	}
	

}
