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
package org.societies.domainauthority.rest.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.domainauthority.rest.model.Resource;;

/**
 * DAO for {@link Resource}
 *
 * @author Mitja Vardjan
 *
 */
public class ResourceDao {

	private static Logger log = LoggerFactory.getLogger(ResourceDao.class);
	
	private SessionFactory sessionFactory;

	/**
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		log.info("setSessionFactory()");
		this.sessionFactory = sessionFactory;
	}

	public List<Resource> getAll() throws HibernateException {
		
		Session session = null;
		List<Resource> result;
		
		try {
			session = sessionFactory.openSession();
			Query query = session.createQuery("SELECT r FROM " + Resource.class.getSimpleName() + " r");
			result = (List<Resource>) query.list();
		} catch (HibernateException e) {
			log.warn("Could not read from data source", e);
			throw e;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		return result;
	}

	/**
	 * Convenience method to return a single instance that matches the query, or null if the query returns no results.
	 *  
	 * @param path
	 * @return the single result or null 
	 * @throws HibernateException
	 */
	public Resource get(String path) throws HibernateException {
		
		log.debug("get: {}", path);

		Session session = null;
		Resource result;
		
		try {
			session = sessionFactory.openSession();
			
			Query query = session.createQuery("FROM " + Resource.class.getSimpleName() + " WHERE path = :myPath");
			query.setParameter("myPath", path);
			
			result = (Resource) query.uniqueResult();
		} catch (HibernateException e) {
			log.warn("Could not read from data source", e);
			throw e;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
		log.debug("get: returning object {}", result);
		return result;
	}

	public void delete(Resource object) throws HibernateException {
		
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			session.delete(object);
			session.flush();
		} catch (HibernateException e) {
			log.warn("Could not delete from data source", e);
			throw e;
		} finally {
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}

	public void save(Resource object) throws HibernateException {

		Session session = null;
		Transaction t = null;

		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			session.save(object);
			t.commit();
		} catch (HibernateException e) {
			log.error(e.getMessage());
			if (t != null) { 
				log.warn("Rolling back transaction");
				t.rollback();
			}
			throw e;
		} finally {
			// This code is always called. 
			// If a session has been open it should always be closed,
			// regardless if an exception was thrown or not.
			if (session != null && session.isOpen()) {
				session.close();
			}
		}
	}
	
	public boolean update(Resource object) throws HibernateException {
	
		boolean returnedStatus = false;
		Session session = null;
		Transaction t = null;
		
		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			session.update(object);
			t.commit();
			returnedStatus = true;
		} catch (HibernateException he) {
			if(t != null) {
				t.rollback();
			}
			log.error(he.getMessage());
			throw he;
		}
		finally{
			if (session != null && session.isOpen()){
				session.close();
			}
		}
		return returnedStatus;
	}
}
