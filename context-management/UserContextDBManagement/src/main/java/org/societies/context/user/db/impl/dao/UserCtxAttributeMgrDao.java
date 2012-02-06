package org.societies.context.user.db.impl.dao;
 
import java.util.List;

import org.societies.context.user.db.impl.model.UserCtxAttributeMgr;
 
public interface UserCtxAttributeMgrDao {
	 
	void save(UserCtxAttributeMgr attributeDB);
	void update(UserCtxAttributeMgr attributeDB);
	void delete(UserCtxAttributeMgr attributeDB);
	UserCtxAttributeMgr findByCode(String type);
	List findById(String type);
 
}