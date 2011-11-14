package org.societies.css.serregistry.db.impl;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;
import org.societies.css_serregistry.db.model.Service;

public class ServiceRegistryDaoImpl implements IServiceRegistryDao {

	
	private static final SessionFactory sessionFactory;
	static {
		try {
			// Create the SessionFactory from hibernate.cfg.xml
			sessionFactory = new Configuration().configure()
					.buildSessionFactory();
		} catch (Throwable ex) {
			// Make sure you log the exception, as it might be swallowed
			System.err.println("Initial SessionFactory creation failed." + ex);
			throw new ExceptionInInitializerError(ex);
		}
	}
	

	public void create(List<Service> listServices) {
		Session session = sessionFactory.openSession();
		session.getTransaction().begin();
		for (Service service : listServices) {
			session.save(service);
		}
		session.getTransaction().commit();

	}

	public List<Service> findAll() {
		Session session = sessionFactory.openSession();
		@SuppressWarnings("unchecked")
		List<Service> list = session.createQuery("From Service").list();
		return list;
	}

}
