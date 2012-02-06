package org.societies.context.user.db.impl.dao.impl;
 
import java.util.List;
 
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
 
import org.societies.context.user.db.impl.dao.UserCtxEntityMgrDao;
import org.societies.context.user.db.impl.model.UserCtxEntityMgr;
 
public class UserCtxEntityMgrDaoImpl extends HibernateDaoSupport implements UserCtxEntityMgrDao{
	 
	public void save(UserCtxEntityMgr entityDB){
		getHibernateTemplate().save(entityDB);
	}
 
	public void update(UserCtxEntityMgr entityDB){
		getHibernateTemplate().update(entityDB);
	}
 
	public void delete(UserCtxEntityMgr entityDB){
		getHibernateTemplate().delete(entityDB);
	}
 
	public UserCtxEntityMgr findByCode(String type){
		List list = getHibernateTemplate().find(
                      "from UserCtxEntityMgr where type=?",type
                );
		return (UserCtxEntityMgr)list.get(0);
	}
 
	public List findById(String type){
		List list = getHibernateTemplate().find(
                      "from UserCtxEntityMgr where type=?",type
                );
		return (List) list.get(0);
	}

}