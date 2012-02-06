package org.societies.context.user.db.impl.bo.impl;

import java.util.List;

import org.societies.context.user.db.impl.bo.UserCtxEntityMgrBo;
import org.societies.context.user.db.impl.dao.UserCtxEntityMgrDao;
import org.societies.context.user.db.impl.model.UserCtxEntityMgr;

public class UserCtxEntityMgrBoImpl implements UserCtxEntityMgrBo{
	 
	UserCtxEntityMgrDao entityDao;
 
	public void setEntityDao(UserCtxEntityMgrDao entityDao) {
		this.entityDao = entityDao;
	}
 
	public void save(UserCtxEntityMgr entityDB){
		entityDao.save(entityDB);
	}
 
	public void update(UserCtxEntityMgr entityDB){
		entityDao.update(entityDB);
	}
 
	public void delete(UserCtxEntityMgr entityDB){
		entityDao.delete(entityDB);
	}
 
	public UserCtxEntityMgr findByCode(String type){
		return entityDao.findByCode(type);
	}
	
	public List findById(String type) {
		return entityDao.findById(type);
	}
}