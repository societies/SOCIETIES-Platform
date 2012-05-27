/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.platform.servicelifecycle.serviceRegistry;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CISNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.CSSNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceNotFoundException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRegistrationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceRetrieveException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceSharingNotificationException;
import org.societies.api.internal.servicelifecycle.serviceRegistry.exception.ServiceUpdateException;
import org.societies.api.schema.servicelifecycle.model.Service;
import org.societies.api.schema.servicelifecycle.model.ServiceImplementation;
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
				// tmpRegistryEntry = (RegistryEntry)
				// session.get(RegistryEntry.class,tmpRegistryEntry.getServiceIdentifier());
				Object obj = session.load(RegistryEntry.class,
						tmpRegistryEntry.getServiceIdentifier());

				// Delete the corresponding entry for service shared in CIS
				// sorry for criterion name ;)
				Criterion a = Restrictions.eq(
						"serviceResourceIdentifier.identifier", service
								.getServiceIdentifier().getIdentifier()
								.toString());
				Criterion b = Restrictions.eq(
						"serviceResourceIdentifier.instanceId", service
								.getServiceIdentifier()
								.getServiceInstanceIdentifier());

				List<ServiceSharedInCISDAO> serviceSharedInCISList = (List<ServiceSharedInCISDAO>) session
						.createCriteria(ServiceSharedInCISDAO.class)
						.add(Restrictions.and(a, b)).list();
				for (ServiceSharedInCISDAO serviceSharedInCISDAO : serviceSharedInCISList) {
					session.delete(serviceSharedInCISDAO);
				}

				session.delete(obj);

			}
			t.commit();

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
	public List<Service> retrieveServicesSharedByCSS(String CSSID)
			throws ServiceRetrieveException {
		List<Service> returnedServiceList = new ArrayList<Service>();
		
		Session session = sessionFactory.openSession();
		
		try {

			List<RegistryEntry> tmpRegistryEntryList = session
					.createCriteria(RegistryEntry.class)
					.createCriteria("serviceInstance")
					.add(Restrictions.eq("fullJid", CSSID)).list();
						
			for (RegistryEntry registryEntry : tmpRegistryEntryList) {
				returnedServiceList.add(registryEntry
						.createServiceFromRegistryEntry());
			}
		} catch (Exception e) {
			throw new ServiceRetrieveException(e);
		} finally {
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
		List<Service> returnedServiceList = new ArrayList<Service>();
		Session session = sessionFactory.openSession();
		try {
			ServiceSharedInCISDAO filterServiceSharedCISDAO = new ServiceSharedInCISDAO();
			filterServiceSharedCISDAO.setCISId(CISID);

			List<ServiceSharedInCISDAO> serviceSharedInCISDAOList = session
					.createCriteria(ServiceSharedInCISDAO.class)
					.add(Example.create(filterServiceSharedCISDAO)).list();

			for (ServiceSharedInCISDAO serviceSharedInCISDAO : serviceSharedInCISDAOList) {
				returnedServiceList.add(((RegistryEntry) session.get(
						RegistryEntry.class,
						serviceSharedInCISDAO.getServiceResourceIdentifier()))
						.createServiceFromRegistryEntry());
			}
		} catch (Exception e) {
			throw new ServiceRetrieveException(e);
		} finally {
			session.close();
		}
		return returnedServiceList;
	}

	private Criteria createCriteriaFromService(Service filter, Session session) {
		Criteria c = session.createCriteria(RegistryEntry.class);
		
		// Direct Attributes
		if (filter.getServiceName() != null)
			c.add(Restrictions.like("serviceName", filter.getServiceName()));
		if (filter.getServiceDescription() != null)
			c.add(Restrictions.like("serviceDescription", filter.getServiceDescription()));
		if (filter.getAuthorSignature() != null)
			c.add(Restrictions.like("authorSignature", filter.getAuthorSignature()));
		if (filter.getServiceEndpoint() != null)
			c.add(Restrictions.like("serviceEndPoint", filter.getServiceEndpoint()));
		if (filter.getServiceLocation() != null)
			c.add(Restrictions.like("serviceLocation", filter.getServiceLocation().toString()));
		if (filter.getServiceStatus() != null) 
			c.add(Restrictions.like("serviceStatus", filter.getServiceStatus().toString()));
		if (filter.getServiceType() != null) 
			c.add(Restrictions.like("serviceType", filter.getServiceType().toString()));
		
		//Service Identifier
		ServiceResourceIdentifier servId = filter.getServiceIdentifier(); 
		if (servId != null) {
			//c.createAlias("serviceIdentifier", "sri");
			if (servId.getIdentifier() != null) {
				c.add(Restrictions.like("serviceIdentifier.identifier", servId.getIdentifier().toString()));
			}
			if (servId.getServiceInstanceIdentifier() != null) {
				c.add(Restrictions.like("serviceIdentifier.instanceId", servId.getServiceInstanceIdentifier()));
			}
		}		
		//Service Instance
		ServiceInstance servInst = filter.getServiceInstance(); 
		if (servInst != null) {
			c.createAlias("serviceInstance", "servInst");
			if (servInst.getFullJid() != null) {
				c.add(Restrictions.like("servInst.fullJid", servInst.getFullJid()));
			}
			if (servInst.getXMPPNode() != null) {
				c.add(Restrictions.like("servInst.XMPPNode", servInst.getXMPPNode()));
			}
			ServiceImplementation si = servInst.getServiceImpl();
			if ( si != null) {
				c.createAlias("servInst.serviceImpl", "servImpl");
				if (si.getServiceNameSpace() != null) {
					c.add(Restrictions.like("servImpl.serviceNameSpace", si.getServiceNameSpace()));
				}
				if (si.getServiceProvider() != null) {
					c.add(Restrictions.like("servImpl.serviceProvider", si.getServiceProvider()));
				}
				if (si.getServiceVersion() != null) {
					c.add(Restrictions.like("servImpl.serviceVersion", si.getServiceVersion()));
				}
			}
		}
		return c;
	}
	
	@Override
	public List<Service> findServices(Service filter)
			throws ServiceRetrieveException {
		
		//RegistryEntry filterRegistryEntry = this.map2registryEntry(filter);
				
		Session session = sessionFactory.openSession();
		
		//Example e = Example.create(filterRegistryEntry).enableLike();
		Criteria c = this.createCriteriaFromService(filter, session);//session.createCriteria(RegistryEntry.class);
		//c.add(e);
		List<RegistryEntry> tmpRegistryEntryList = c.list();
		
		session.close();
		
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
			if (session.get(
					RegistryEntry.class,
					new ServiceResourceIdentiferDAO(serviceIdentifier
							.getIdentifier().toString(), serviceIdentifier
							.getServiceInstanceIdentifier())) != null) {
				ServiceSharedInCISDAO tmpSharedInCIS = new ServiceSharedInCISDAO(
						CISID, new ServiceResourceIdentiferDAO(
								serviceIdentifier.getIdentifier().toString(),
								serviceIdentifier
										.getServiceInstanceIdentifier()));

				session.save(tmpSharedInCIS);
				t.commit();
			} else {
				throw new ServiceNotFoundException(
						"The service doesn't exist in the registry.");
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
	public void removeServiceSharingInCIS(
			ServiceResourceIdentifier serviceIdentifier, String CISID)
			throws ServiceSharingNotificationException {

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
			if (tmpRegistryEntry != null) {
				tmpService = tmpRegistryEntry.createServiceFromRegistryEntry();
			}
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
	public boolean changeStatusOfService(
			ServiceResourceIdentifier serviceIdentifier,
			ServiceStatus serviceStatus) throws ServiceNotFoundException {
		Session session = sessionFactory.openSession();
		Transaction t = session.beginTransaction();
		try {
			RegistryEntry tmpRegistryEntry = (RegistryEntry) session.get(
					RegistryEntry.class, new ServiceResourceIdentiferDAO(
							serviceIdentifier.getIdentifier().toString(),
							serviceIdentifier.getServiceInstanceIdentifier()));
			tmpRegistryEntry.setServiceStatus(serviceStatus.toString());
			session.update(tmpRegistryEntry);
			t.commit();
		} catch (Exception e) {
			t.rollback();
			new ServiceNotFoundException(e);
		} finally {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #deleteServiceCSS(java.lang.String)
	 */
	@Override
	public boolean deleteServiceCSS(String CSSId) throws CSSNotFoundException {
		Session session = sessionFactory.openSession();

		boolean returnedValue = false;
		try {
			List<RegistryEntry> tmpRegistryEntryList = session
					.createCriteria(RegistryEntry.class)
					.createCriteria("serviceInstance")
					.add(Restrictions.eq("fullJid", CSSId)).list();
			if (tmpRegistryEntryList.size() == 0) {
				throw new CSSNotFoundException("The CSS with Id: " + CSSId
						+ " is not present in the Registry");
			}
			List<Service> tmpServiceList = new ArrayList<Service>();
			for (RegistryEntry registryEntry : tmpRegistryEntryList) {
				tmpServiceList.add(registryEntry
						.createServiceFromRegistryEntry());
			}
			this.unregisterServiceList(tmpServiceList);
			returnedValue = true;
		} catch (CSSNotFoundException ex) {

			log.error(ex.getMessage());
			throw ex;
		} catch (Exception e) {

			log.error(e.getMessage());
		} finally {
			session.close();
		}
		return returnedValue;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry
	 * #clearServiceSharedCIS(java.lang.String)
	 */
	@Override
	public boolean clearServiceSharedCIS(String CISId)
			throws CISNotFoundException {
		Session session = sessionFactory.openSession();
		Transaction t = null;
		try {
			List<ServiceSharedInCISDAO> tmpServiceSharedCIS = session
					.createCriteria(ServiceSharedInCISDAO.class)
					.add(Restrictions.eq("CISId", CISId)).list();
			if (tmpServiceSharedCIS.size() == 0) {
				throw new CISNotFoundException("The CIS with id: " + CISId
						+ " is not in the Registry.");
			}
			t = session.beginTransaction();
			for (ServiceSharedInCISDAO serviceSharedInCISDAO : tmpServiceSharedCIS) {
				session.delete(serviceSharedInCISDAO);
			}
			t.commit();
		} catch (CISNotFoundException ex) {

			log.error(ex.getMessage());
			throw ex;
		} catch (Exception e) {
			if (t != null) {
				t.rollback();
			}
			log.error(e.getMessage());
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry#updateRegisteredService(org.societies.api.schema.servicelifecycle.model.Service)
	 */
	@Override
	public boolean updateRegisteredService(Service service)
			throws ServiceUpdateException {
		boolean returnedStatus=false;
		Session session = sessionFactory.openSession();
		Transaction t = null;
		try {
		 RegistryEntry retrievedRegistryEntry= (RegistryEntry)session.get(RegistryEntry.class, new ServiceResourceIdentiferDAO(service.getServiceIdentifier().getIdentifier().toString(),service.getServiceIdentifier().getServiceInstanceIdentifier()));
	     if(retrievedRegistryEntry!=null){
	     retrievedRegistryEntry.updateRegistryEntry(service);
	     t=session.beginTransaction();
	     session.update(retrievedRegistryEntry);
	     t.commit();
	     }
	    returnedStatus=true;
		} catch (HibernateException he) {
			t.rollback();
			log.error(he.getMessage());
			throw new ServiceUpdateException(he);
		}
		catch (Exception e) {
			log.error(e.getMessage());
			throw new ServiceUpdateException(e);
		}
		finally{
			if (session!=null){
				session.close();
			}
		}
		return returnedStatus;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.servicelifecycle.serviceRegistry.IServiceRegistry#retrieveCISSharedService(org.societies.api.schema.servicelifecycle.model.ServiceResourceIdentifier)
	 */
	@Override
	public List<String> retrieveCISSharedService(
			ServiceResourceIdentifier serviceIdentifier) {
		// TODO Auto-generated method stub
		return null;
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

	

	
	

}
