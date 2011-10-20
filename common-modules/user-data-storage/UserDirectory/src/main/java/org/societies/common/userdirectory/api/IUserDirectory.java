package org.societies.common.userdirectory.api;

import java.util.Collection;

public interface IUserDirectory {
	
	public Object createNewUserContact();
	
	public boolean insertUser(IUserProfile contact);
	
	public boolean updateUser(IUserProfile contact);
	
	public Collection<Object> queryUser(Object Parameter);
	
	public boolean isUserExists(Object parameter);
	
	public Collection<Object> retrieveAllUser();
	
	// org.societies.common.userdirectory

}
