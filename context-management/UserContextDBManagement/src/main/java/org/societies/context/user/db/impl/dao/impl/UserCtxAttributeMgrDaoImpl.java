package org.societies.context.user.db.impl.dao.impl;
 
import java.util.List;
 
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
 
import org.societies.context.user.db.impl.dao.UserCtxAttributeMgrDao;
import org.societies.context.user.db.impl.model.UserCtxAttributeMgr;
 
public class UserCtxAttributeMgrDaoImpl extends HibernateDaoSupport implements UserCtxAttributeMgrDao{
	 
	public void save(UserCtxAttributeMgr attributeDB){
		getHibernateTemplate().save(attributeDB);
	}
 
	public void update(UserCtxAttributeMgr attributeDB){
		getHibernateTemplate().update(attributeDB);
	}
 
	public void delete(UserCtxAttributeMgr attributeDB){
		getHibernateTemplate().delete(attributeDB);
	}
 
	public UserCtxAttributeMgr findByCode(String type){
		List list = getHibernateTemplate().find(
                      "from UserCtxAttributeMgr where type=?",type
                );
		return (UserCtxAttributeMgr)list.get(0);
	}
 
	public List findById(String type){
		List list = getHibernateTemplate().find(
                      "from UserCtxAttributeMgr where type=?",type
                );
		return (List) list.get(0);
	}

}