package org.societies.webapp.service;

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
