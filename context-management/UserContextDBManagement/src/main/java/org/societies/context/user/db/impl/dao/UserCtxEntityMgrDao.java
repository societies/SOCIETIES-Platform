package org.societies.context.user.db.impl.dao;
 
import java.util.List;

import org.societies.context.user.db.impl.model.UserCtxEntityMgr;
 
public interface UserCtxEntityMgrDao {
	 
	void save(UserCtxEntityMgr entityDB);
	void update(UserCtxEntityMgr entityDB);
	void delete(UserCtxEntityMgr entityDB);
	UserCtxEntityMgr findByCode(String type);
	List findById(String type);
 
}