/**
 * 
 */
package org.societies.webapp.models;

/**
 * @author mmanniox
 *
 */
@Deprecated // No longer used after move from JSP to JSF
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
