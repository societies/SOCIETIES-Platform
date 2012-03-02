package org.societies.platform.servicelifecycle.serviceRegistry;

import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.servicelifecycle.model.Service;
import org.societies.api.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.platform.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceResourceIdentiferDAO;

public class ServiceRegistry implements IServiceRegistry {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(ServiceRegistry.class);

	public ServiceRegistry() {
		log.info("Service registry bundle instantiated.");
	}

	@Override
	public void registerServiceList(List<Service> servicesList)
			throws ServiceRegistrationException {
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		try {
			for (Service service : servicesList) {

				tmpRegistryEntry = new RegistryEntry(service.getServiceIdentifier(), service.getServiceEndpoint(), service.getServiceName(), service.getServiceDescription(), service.getAuthorSignature(), service.getServiceType(), service.getServiceLocation(), service.getServiceInstance(),service.getServiceStatus());

				Transaction t = session.beginTransaction();
				session.save(tmpRegistryEntry);
				t.commit();
			}
			log.debug("Service list saved.");
		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	@Override
	public void unregisterServiceList(List<Service> servicesList)
			throws ServiceRegistrationException {
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		try {
			for (Service service : servicesList) {

				tmpRegistryEntry = new RegistryEntry(service.getServiceIdentifier(), service.getServiceEndpoint(), service.getServiceName(), service.getServiceDescription(), service.getAuthorSignature(), service.getServiceType(), service.getServiceLocation(), service.getServiceInstance(),service.getServiceStatus());
			//tmpRegistryEntry = (RegistryEntry) session.get(RegistryEntry.class,tmpRegistryEntry.getServiceIdentifier());
				Object obj=	session.load(RegistryEntry.class, tmpRegistryEntry.getServiceIdentifier());	
				Transaction t = session.beginTransaction();
				
				session.delete(obj);
				
				t.commit();
			}

		
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	@Override
	public List<Service> retrieveServicesSharedByCSS(String CSSID)
			throws ServiceRetrieveException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #retrieveServicesSharedByCIS(java.lang.String)
	 */
	@Override
	public List<Service> retrieveServicesSharedByCIS(String CISID)
			throws ServiceRetrieveException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #notifyServiceIsSharedInCIS(java.lang.String, java.lang.String)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #findServices(java.lang.Object)
	 */
	@Override
	public List<Service> findServices(Object filter)
			throws ServiceRetrieveException {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #
	 * notifyServiceIsSharedInCIS(org.societies.api.internal.servicelifecycle.model
	 * .ServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public void notifyServiceIsSharedInCIS(
			ServiceResourceIdentifier serviceIdentifier, String CISID)
			throws ServiceSharingNotificationException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #
	 * removeServiceSharingInCIS(org.societies.api.internal.servicelifecycle.model
	 * .ServiceResourceIdentifier, java.lang.String)
	 */
	@Override
	public void removeServiceSharingInCIS(
			ServiceResourceIdentifier serviceIdentifier, String CISID)
			throws ServiceSharingNotificationException {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #retrieveService(org.societies.api.internal.servicelifecycle.model.
	 * ServiceResourceIdentifier)
	 */
	@Override
	public Service retrieveService(ServiceResourceIdentifier serviceIdentifier)
			throws ServiceRetrieveException {
		Session session = sessionFactory.openSession();
		Service tmpService = null;
		RegistryEntry tmpRegistryEntry = null;
		try {
			tmpRegistryEntry = (RegistryEntry) session.get(RegistryEntry.class,
					new ServiceResourceIdentiferDAO(serviceIdentifier
							.getIdentifier().toString(),serviceIdentifier.getServiceInstanceIdentifier()));
			tmpService = tmpRegistryEntry.createServiceFromRegistryEntry();
		} catch (Exception e) {
			e.printStackTrace();
			throw new ServiceRetrieveException(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return tmpService;

	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
