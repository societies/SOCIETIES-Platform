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
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.platform.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceResourceIdentiferDAO;

public class ServiceRegistry implements IServiceRegistry {
	private SessionFactory sessionFactory;

	public ServiceRegistry() {
	}

	@Override
	public void registerServiceList(List<Service> servicesList) throws ServiceRegistrationException{
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		try {
		for (Service service : servicesList) {
			
				tmpRegistryEntry = new RegistryEntry(
						new ServiceResourceIdentifier(new URI(service
								.getServiceIdentifier().toString())),
						service.getCSSIDInstalled(), service.getVersion(),

						service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature());
			} 
			Transaction t = session.beginTransaction();
			session.save(tmpRegistryEntry);
			t.commit();
		}catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		}finally{
			if (session!=null){
				session.close();
			}
		}

		
	}

	@Override
	public void unregisterServiceList(List<Service> servicesList) throws ServiceRegistrationException{
		Session session = sessionFactory.openSession();
		RegistryEntry tmpRegistryEntry = null;
		try {
		for (Service service : servicesList) {
			
				tmpRegistryEntry = new RegistryEntry(
						new ServiceResourceIdentifier(new URI(service
								.getServiceIdentifier().toString())),
						service.getCSSIDInstalled(), service.getVersion(),

						service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature());
				
		}
			
			Transaction t = session.beginTransaction();
			session.delete(tmpRegistryEntry);
			t.commit();
		} catch (URISyntaxException e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		}catch (Exception e) {
			e.printStackTrace();
			throw new ServiceRegistrationException(e);
		}
		finally{
			if (session!=null){
			session.close();}
		}

		
	}

	@Override
	public List<Service> retrieveServicesSharedByCSS(String CSSID) throws ServiceRetrieveException{
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
	public List<Service> retrieveServicesSharedByCIS(String CISID) throws ServiceRetrieveException{
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
	public List<Service> findServices(Object filter) throws ServiceRetrieveException{
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
	public Service retrieveService(ServiceResourceIdentifier serviceIdentifier) throws ServiceRetrieveException{
		Session session = sessionFactory.openSession();
		Service tmpService=null;
		RegistryEntry tmpRegistryEntry=null;
		try{
		 tmpRegistryEntry = (RegistryEntry) session.get(RegistryEntry.class,
				 new ServiceResourceIdentiferDAO(serviceIdentifier.getIdentifier().toString()));
		tmpService=tmpRegistryEntry.createServiceFromRegistryEntry(); 
		}catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (session!=null){
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
