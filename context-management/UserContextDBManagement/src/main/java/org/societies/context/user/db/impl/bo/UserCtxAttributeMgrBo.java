package org.societies.context.user.db.impl.bo;

import java.util.List;

import org.societies.context.user.db.impl.model.UserCtxAttributeMgr;

public interface UserCtxAttributeMgrBo {
	
	void save(UserCtxAttributeMgr attributeDB);
	void update(UserCtxAttributeMgr attributeDB);
	void delete(UserCtxAttributeMgr attributeDB);
	UserCtxAttributeMgr findByCode(String type);
	List findById(String type);
}