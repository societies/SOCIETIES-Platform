/**
 * 
 */
package org.societies.webapp.models;

/**
 * @author mmanniox
 *
 */
@Deprecated // No longer used after move from JSP to JSF
public class LoginForm {

	String username;
	String password;
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
}
