package org.societies.platform.servicelifecycle.serviceRegistry;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.societies.api.internal.servicelifecycle.model.Service;
import org.societies.api.internal.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.platform.servicelifecycle.serviceRegistry.model.RegistryEntry;

public class ServiceRegistry implements IServiceRegistry {
	private SessionFactory sessionFactory;

	public ServiceRegistry() {
	}

	@Override
	public void registerServiceList(List<Service> servicesList) {
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		
		for (Service service : servicesList) {
			try {
				tmpRegistryEntry = new RegistryEntry(
						new ServiceResourceIdentifier(new URI(service
								.getServiceIdentifier().toString())),
						service.getCSSIDInstalled(), service.getVersion(),

						service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature());
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			Transaction t = session.beginTransaction();
			session.save(tmpRegistryEntry);
			t.commit();
		}

		session.close();
	}

	@Override
	public void unregisterServiceList(List<Service> servicesList) {
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		
		for (Service service : servicesList) {
			try {
				tmpRegistryEntry = new RegistryEntry(
						new ServiceResourceIdentifier(new URI(service
								.getServiceIdentifier().toString())),
						service.getCSSIDInstalled(), service.getVersion(),

						service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature());
				
			} catch (URISyntaxException e) {
				e.printStackTrace();
			}
			
			Transaction t = session.beginTransaction();
			session.delete(tmpRegistryEntry);
			t.commit();
		}

		session.close();
	}

	@Override
	public List<Service> retrieveServicesSharedByCSS(String CSSID) {
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
	public List<Service> retrieveServicesSharedByCIS(String CISID) {
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
	public List<Service> findServices(Object filter) {
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
	public Service retrieveService(ServiceResourceIdentifier serviceIdentifier) {
		/* Session session = sessionFactory.openSession();
		
		Service tmpService = (Service) session.get(ServiceResourceIdentifier.class, serviceIdentifier);
		session.close();
		
		return tmpService; */
		
		return null;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

}
