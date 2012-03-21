package org.societies.platform.servicelifecycle.serviceRegistry;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier;
import org.societies.api.schema.servicelifecycle.model.ServiceStatus;
import org.societies.platform.servicelifecycle.serviceRegistry.model.RegistryEntry;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceImplementationDAO;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceInstanceDAO;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceResourceIdentiferDAO;
import org.societies.platform.servicelifecycle.serviceRegistry.model.ServiceSharedInCISDAO;

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
		Transaction t = session.beginTransaction();
		try {
			for (Service service : servicesList) {

				tmpRegistryEntry = new RegistryEntry(
						service.getServiceIdentifier(),
						service.getServiceEndpoint(), service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature(), service.getServiceType(),
						service.getServiceLocation(),
						service.getServiceInstance(),
						service.getServiceStatus());

				session.save(tmpRegistryEntry);

			}
			t.commit();
			log.debug("Service list saved.");

		} catch (Exception e) {
			t.rollback();
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

				tmpRegistryEntry = new RegistryEntry(
						service.getServiceIdentifier(),
						service.getServiceEndpoint(), service.getServiceName(),
						service.getServiceDescription(),
						service.getAuthorSignature(), service.getServiceType(),
						service.getServiceLocation(),
						service.getServiceInstance(),
						service.getServiceStatus());
				// tmpRegistryEntry = (RegistryEntry)
				// session.get(RegistryEntry.class,tmpRegistryEntry.getServiceIdentifier());
				Object obj = session.load(RegistryEntry.class,
						tmpRegistryEntry.getServiceIdentifier());
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
		List<Service> returnedServiceList=new ArrayList<Service>();
		Session session=sessionFactory.openSession();
		try {
			
			List<RegistryEntry> tmpRegistryEntryList=session.createCriteria(RegistryEntry.class).createCriteria("serviceInstance").
				add(Restrictions.eq("fullJid", CSSID)).list();
			for (RegistryEntry registryEntry : tmpRegistryEntryList) {
				returnedServiceList.add(registryEntry.createServiceFromRegistryEntry());
			}
		} catch (Exception e) {
			throw new ServiceRetrieveException(e);
		}finally{
			session.close();
		}
		return returnedServiceList;
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
		List<Service> returnedServiceList=new ArrayList<Service>();
		Session session= sessionFactory.openSession();
		try{
		ServiceSharedInCISDAO filterServiceSharedCISDAO= new ServiceSharedInCISDAO();
		filterServiceSharedCISDAO.setCISId(CISID);
		
		List<ServiceSharedInCISDAO> serviceSharedInCISDAOList=session.createCriteria(ServiceSharedInCISDAO.class)
				.add(Example.create(filterServiceSharedCISDAO)).list();
		
		
		for (ServiceSharedInCISDAO serviceSharedInCISDAO : serviceSharedInCISDAOList) {
			returnedServiceList.add( ((RegistryEntry)session.get(RegistryEntry.class, serviceSharedInCISDAO.getServiceResourceIdentifier())).createServiceFromRegistryEntry());
		}
		}catch (Exception e){
			throw new ServiceRetrieveException(e);
		}finally{
			session.close();
		}
		return returnedServiceList;
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
	public List<Service> findServices(Service filter)
			throws ServiceRetrieveException {
		RegistryEntry filterRegistryEntry = new RegistryEntry();
		// Map and check null values
		if (filter.getServiceName() != null)
			filterRegistryEntry.setServiceName(filter.getServiceName());
		if (filter.getServiceDescription() != null)
			filterRegistryEntry.setServiceDescription(filter
					.getServiceDescription());
		if (filter.getAuthorSignature() != null)
			filterRegistryEntry.setAuthorSignature(filter.getAuthorSignature());
		if (filter.getServiceEndpoint() != null)
			filterRegistryEntry.setServiceEndPoint(filter.getServiceEndpoint());

		if (filter.getServiceIdentifier() != null) {
			ServiceResourceIdentiferDAO tmpServiceResourceIdentifierDAO = new ServiceResourceIdentiferDAO();

			if (filter.getServiceIdentifier().getIdentifier() != null) {
				tmpServiceResourceIdentifierDAO.setIdentifier(filter
						.getServiceIdentifier().getIdentifier().toString());
			}
			if (filter.getServiceIdentifier().getServiceInstanceIdentifier() != null) {
				tmpServiceResourceIdentifierDAO.setInstanceId(filter
						.getServiceIdentifier().getServiceInstanceIdentifier());
			}
			filterRegistryEntry
					.setServiceIdentifier(tmpServiceResourceIdentifierDAO);
		}
		if (filter.getServiceInstance() != null) {
			ServiceInstanceDAO tmpServiceInstanceDAO = new ServiceInstanceDAO();
			if (filter.getServiceInstance().getFullJid() != null) {
				tmpServiceInstanceDAO.setFullJid(filter.getServiceInstance()
						.getFullJid());

			}
			if (filter.getServiceInstance().getXMPPNode() != null) {
				tmpServiceInstanceDAO.setXMPPNode(filter.getServiceInstance()
						.getXMPPNode());

			}
			if (filter.getServiceInstance().getServiceImpl() != null) {
				ServiceImplementationDAO tmpServiceImplementationDAO = new ServiceImplementationDAO();
				if (filter.getServiceInstance().getServiceImpl()
						.getServiceNameSpace() != null) {
					tmpServiceImplementationDAO.setServiceNameSpace(filter
							.getServiceInstance().getServiceImpl()
							.getServiceNameSpace());
				}
				if (filter.getServiceInstance().getServiceImpl()
						.getServiceProvider() != null) {
					tmpServiceImplementationDAO.setServiceProvider(filter
							.getServiceInstance().getServiceImpl()
							.getServiceProvider());

				}
				if (filter.getServiceInstance().getServiceImpl()
						.getServiceVersion() != null) {
					tmpServiceImplementationDAO.setServiceVersion(filter
							.getServiceInstance().getServiceImpl()
							.getServiceVersion());
				}
			}
			filterRegistryEntry.setServiceInstance(tmpServiceInstanceDAO);
		}
		if (filter.getServiceLocation() != null) {
			filterRegistryEntry.setServiceLocation(filter.getServiceLocation()
					.toString());
		}
		if (filter.getServiceStatus() != null) {
			filterRegistryEntry.setServiceStatus(filter.getServiceStatus()
					.toString());

		}
		if (filter.getServiceType() != null) {
			filterRegistryEntry.setServiceType(filter.getServiceType()
					.toString());
		}

		List<RegistryEntry> tmpRegistryEntryList = sessionFactory.openSession()
				.createCriteria(RegistryEntry.class)
				.add(Example.create(filterRegistryEntry).enableLike()).list();
		return createListService(tmpRegistryEntryList);
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
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			if(session.get(RegistryEntry.class, new ServiceResourceIdentiferDAO(serviceIdentifier.getIdentifier().toString(), serviceIdentifier.getServiceInstanceIdentifier()))!=null){
			ServiceSharedInCISDAO tmpSharedInCIS = new ServiceSharedInCISDAO(
					CISID, new ServiceResourceIdentiferDAO(serviceIdentifier
							.getIdentifier().toString(),
							serviceIdentifier.getServiceInstanceIdentifier()));

			session.save(tmpSharedInCIS);
			t.commit();
			}else{throw new ServiceNotFoundException("The service doesn't exist in the registry.");
			}
			
		} catch (Exception e) {
			t.rollback();
			throw new ServiceSharingNotificationException(e);
		} finally {

			session.close();
		}
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
	public void removeServiceSharingInCIS(ServiceResourceIdentifier serviceIdentifier, String CISID) throws ServiceSharingNotificationException {

		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			ServiceSharedInCISDAO tmpSharedInCIS = new ServiceSharedInCISDAO(
					CISID, new ServiceResourceIdentiferDAO(serviceIdentifier
							.getIdentifier().toString(),
							serviceIdentifier.getServiceInstanceIdentifier()));
			Object obj = session.load(ServiceSharedInCISDAO.class,
					tmpSharedInCIS.getId());
			session.delete(obj);
			t.commit();

		} catch (Exception e) {
			t.rollback();
			throw new ServiceSharingNotificationException(e);
		} finally {

			session.close();
		}
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
			tmpRegistryEntry = (RegistryEntry) session.get(
					RegistryEntry.class,
					new ServiceResourceIdentiferDAO(serviceIdentifier
							.getIdentifier().toString(), serviceIdentifier
							.getServiceInstanceIdentifier()));
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
	
	@Override
	public boolean changeStatusOfService (ServiceResourceIdentifier serviceIdentifier,ServiceStatus serviceStatus) throws ServiceNotFoundException{
		Session session=sessionFactory.openSession();
		Transaction t=session.beginTransaction();
		try {
			RegistryEntry tmpRegistryEntry=(RegistryEntry)session.get(RegistryEntry.class, new ServiceResourceIdentiferDAO(serviceIdentifier.getIdentifier().toString(),serviceIdentifier.getServiceInstanceIdentifier()));
		tmpRegistryEntry.setServiceStatus(serviceStatus.toString());
		session.update(tmpRegistryEntry);
		t.commit();
		} catch (Exception e) {
			t.rollback();
			new ServiceNotFoundException(e);
		}finally{
			session.close();
		}
		return true;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/* Utility methods */
	private List<Service> createListService(
			List<RegistryEntry> inListRegistryEntry) {
		List<Service> returnedServiceList = new ArrayList<Service>();
		for (RegistryEntry registryEntry : inListRegistryEntry) {
			returnedServiceList.add(registryEntry
					.createServiceFromRegistryEntry());
		}
		return returnedServiceList;
	}

	/* (non-Javadoc)
	 * @see org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry#changeStatusOfService(org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier, org.societies.api.schema.servicelifecycle.model.ServiceStatus)
	 */
	
}
