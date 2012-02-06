package org.societies.context.user.db.impl.bo.impl;

import java.util.List;

import org.societies.context.user.db.impl.bo.UserCtxAttributeMgrBo;
import org.societies.context.user.db.impl.dao.UserCtxAttributeMgrDao;
import org.societies.context.user.db.impl.model.UserCtxAttributeMgr;

public class UserCtxAttributeMgrBoImpl implements UserCtxAttributeMgrBo{
	 
	UserCtxAttributeMgrDao attributeDao;
 
	public void setAttributeDao(UserCtxAttributeMgrDao attributeDao) {
		this.attributeDao = attributeDao;
	}
 
	public void save(UserCtxAttributeMgr attributeDB){
		attributeDao.save(attributeDB);
	}
 
	public void update(UserCtxAttributeMgr attributeDB){
		attributeDao.update(attributeDB);
	}
 
	public void delete(UserCtxAttributeMgr attributeDB){
		attributeDao.delete(attributeDB);
	}
 
	public UserCtxAttributeMgr findByCode(String type){
		return attributeDao.findByCode(type);
	}
	
	public List findById(String type) {
		return attributeDao.findById(type);
	}
}