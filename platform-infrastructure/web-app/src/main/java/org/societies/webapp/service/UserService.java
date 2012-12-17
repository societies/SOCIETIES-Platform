package org.societies.webapp.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Maria Mannion
 * 
 */
@Service
@Scope("Session")  
public class UserService {

	private boolean userLoggedIn;

	public boolean isUserLoggedIn() {
		return userLoggedIn;
	}


	public void setUserLoggedIn(boolean userLoggedIn) {
		this.userLoggedIn = userLoggedIn;
	}


	private static Logger log = LoggerFactory.getLogger(UserService.class);
	
	public UserService() {
		log.info("UserService constructor");
		userLoggedIn = false;
	}
	
	
	public void destroyOnSessionEnd()
	{
		log.info("UserService destroyOnSessionEnd");
	}

	
}
