
package org.societies.webapp.service;
/**
 * 
 * @author Perumal Kuppuudaiyar
 * 
 * This is a example UserAuthentication class, can be instantiated as bean to use internally.
 * to validate the user login credentials
 */
public class UserAuthentication {
	
	public boolean authenticate(String userId, String password){
		
		String uid = "userid";
		String pwd = "password";
				
		if (!userId.equals(uid) || !password.equals(pwd)) {
			
			return false;
		}		
		return true;		
	}

}
