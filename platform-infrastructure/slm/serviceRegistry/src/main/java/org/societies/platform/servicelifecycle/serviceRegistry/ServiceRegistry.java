package org.societies.platform.servicelifecycle.serviceRegistry;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.QuerySubjectType;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.RegistryEntryOut;
import org.societies.api.internal.servicelifecycle.serviceRegistry.model.ServiceResourceIdentifier;
import org.societies.comm.identity.Identity;

public class ServiceRegistry implements IServiceRegistry {
	// static private Logger log = LoggerFactory.getLogger(ServiceRegistry.class);
	private SessionFactory sessionFactory;
	
	public ServiceRegistry() {
		sessionFactory = HibernateUtil.getSessionFactory();

		// log.info("Service Registry created");
	}

	@Override
	public List<ServiceResourceIdentifier> registerServiceList(
			List<RegistryEntry> servicesList) {

		Session session = sessionFactory.openSession();

		for (RegistryEntry registryEntry : servicesList) {
			Transaction t = session.beginTransaction();
			session.save(registryEntry);
			t.commit();
		}

		session.close();

		return null;
	}

	@Override
	public List<ServiceResourceIdentifier> unregisterServiceList(
			List<ServiceResourceIdentifier> servicesList) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean syncRemoteCSSRegistry(Identity CSSID) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<RegistryEntryOut> retrieveServicesSharedByCSS(Identity CSS,
			QuerySubjectType type) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistryEntryOut> retrieveServicesInCSS(Identity CSS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistryEntryOut> retrieveServicesInCIS(Identity CIS) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<RegistryEntryOut> retrieveServicesSharedToCSS(Identity CSS,
			QuerySubjectType type) {
		// TODO Auto-generated method stub
		return null;
	}

}
