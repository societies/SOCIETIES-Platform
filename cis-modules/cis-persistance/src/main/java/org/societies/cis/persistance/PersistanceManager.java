package org.societies.cis.persistance;

import javax.persistence.EntityManager;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PersistanceManager {
	private EntityManager em = null;
	@Autowired
	public PersistanceManager(EntityManager em){
		this.em = em;
	}
	
	public void persist(Object o){
		em.persist(o);
	}
}
