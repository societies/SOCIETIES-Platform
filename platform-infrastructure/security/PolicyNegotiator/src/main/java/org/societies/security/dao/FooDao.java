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
package org.societies.security.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.security.model.Foo;

/**
 * Describe your class here...
 *
 * @author Mitja Vardjan
 *
 */
public class FooDao {

	private static Logger log = LoggerFactory.getLogger(FooDao.class);
	
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

	public List<Foo> getAll() throws Exception {
		
		Session session = null;
		List<Foo> result;
		
		try {
			session = sessionFactory.openSession();
			
			Query query = session.createQuery("SELECT f FROM Foo f");
			
			//Query query = session.createQuery("SELECT foo FROM foo WHERE id = :myId");
			//query.setParameter("myId", "value");
			
			result = (List<Foo>) query.list();
		} catch (Exception e) {
			log.warn("Could not read from data source", e);
			throw new Exception(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return result;
	}

	public void delete(Foo object) throws Exception {
		
		Session session = null;
		
		try {
			session = sessionFactory.openSession();
			
			session.delete(object);
			
//			Query query = session.createQuery("SELECT foo FROM foo WHERE id = :myId");
//			query.setParameter("myId", object.getId());
			
		} catch (Exception e) {
			log.warn("Could not delete from data source", e);
			throw new Exception(e);
		} finally {
			if (session != null) {
				session.close();
			}
		}
	}

	public void write(Foo object) throws Exception {

		Session session = null;
		Transaction t = null;

		try {
			session = sessionFactory.openSession();
			t = session.beginTransaction();
			session.save(object);
			t.commit();
		} catch (Exception e) {
			log.error(e.getMessage());
			if (t != null) { 
				log.warn("Rolling back transaction");
				t.rollback();
			}
			throw new Exception(e);
		} finally {
			// This code is always called. 
			// If a session has been open it should always be closed,
			// regardless if an exception was thrown or not.
			if (session != null) {
				session.close();
			}
		}
	}
	
	public boolean update(Foo object) throws Exception {
	
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
		catch (Exception e) {
			log.error(e.getMessage());
			throw new Exception(e);
		}
		finally{
			if (session != null){
				session.close();
			}
		}
		return returnedStatus;
	}
}
