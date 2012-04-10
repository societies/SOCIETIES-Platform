package org.societies.cis.persistance;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.stereotype.Component;

@Component
public class PersistanceManager implements IPersistanceManager {
	private EntityManager em = null;
	@Autowired
	public PersistanceManager(){
		LocalContainerEntityManagerFactoryBean lcemfb;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.cis.persistance.IPersistanceManager#persist(java.lang.Object)
	 */
	/* (non-Javadoc)
	 * @see org.societies.cis.persistance.IPersistanceManager#persist(java.lang.Object)
	 */
	@Override
	public void persist(Object o){
		em.persist(o);
	}
}
