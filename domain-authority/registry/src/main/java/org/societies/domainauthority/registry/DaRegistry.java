/**
Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 

(SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp (IBM),
INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
ITALIA S.p.a.(TI), TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
All rights reserved.

Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
conditions are met:

1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.

2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
   disclaimer in the documentation and/or other materials provided with the distribution.

THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 package org.societies.domainauthority.registry;



import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.domainauthority.registry.model.DaRegistryRecordEntry;



// TODO: Auto-generated Javadoc
/**
 * The Class DaRegistry.
 */
public class DaRegistry {
	
	/** The session factory. */
	private SessionFactory sessionFactory;
	
	/** The log. */
	private static Logger log = LoggerFactory.getLogger(DaRegistry.class);

	
	
	/**
	 * Gets the session factory.
	 *
	 * @return the sessionFactory
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}


	/**
	 * Sets the session factory.
	 *
	 * @param sessionFactory the sessionFactory to set
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}


	/**
	 * Instantiates a new da registry.
	 */
	public DaRegistry() {
		log.info("Domain Authority Registry bundle instantiated.");
	}

	
	

	/**
	 * Adds the xmpp identity details.
	 *
	 * @param details the details
	 */
	public void addXmppIdentityDetails(DaUserRecord details) {
		Session session = sessionFactory.openSession();
		DaRegistryRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new DaRegistryRecordEntry();
			tmpEntry.setName(details.getName());
			tmpEntry.setId(details.getId());
			tmpEntry.setHost(details.getHost());
			tmpEntry.setPort(details.getPort());
			tmpEntry.setStatus(details.getStatus());
			tmpEntry.setUserType(details.getUserType());
			tmpEntry.setPassword(details.getPassword());
			session.save(tmpEntry);

			t.commit();
			log.debug("addXmppIdentityDetails Record Saved.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}
	
	/**
	 * Removes the xmpp identity details.
	 *
	 * @param details the details
	 */
	@SuppressWarnings("unchecked")
	public void removeXmppIdentityDetails(DaUserRecord details) {

		Session session = sessionFactory.openSession();
		DaRegistryRecordEntry filterRegistryEntry = new DaRegistryRecordEntry();

	
		Transaction t = session.beginTransaction();
		
		try {

			filterRegistryEntry.setName(details.getName());

			List<DaRegistryRecordEntry> tmpRegistryEntryList = session.createCriteria(DaRegistryRecordEntry.class)
					.add(Restrictions.eq("name",
							filterRegistryEntry.getName()).ignoreCase()).list();
			
			if ((tmpRegistryEntryList != null) && tmpRegistryEntryList.size() > 0) {
				session.delete(tmpRegistryEntryList.get(0));
			}
			t.commit();
			log.debug("removeXmppIdentityDetails Record deleted.");
	
		} catch (Exception e) {
	
			// Do nothing
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}
	
	/**
	 * Update xmpp identity details.
	 *
	 * @param details the details
	 */
	@SuppressWarnings("unchecked")
	public void updateXmppIdentityDetails(DaUserRecord details) {
		Session session = sessionFactory.openSession();
		DaRegistryRecordEntry filterRegistryEntry = new DaRegistryRecordEntry();
		DaRegistryRecordEntry tmpEntry = null;
	
		Transaction t = session.beginTransaction();
		
		try {

			filterRegistryEntry.setName(details.getName());

			List<DaRegistryRecordEntry> tmpRegistryEntryList = session.createCriteria(DaRegistryRecordEntry.class)
					.add(Restrictions.eq("name",
							filterRegistryEntry.getName()).ignoreCase()).list();
			
			if ((tmpRegistryEntryList != null) && tmpRegistryEntryList.size() > 0) {
				session.delete(tmpRegistryEntryList.get(0));
			}
			
			tmpEntry = new DaRegistryRecordEntry();
			tmpEntry.setName(details.getName());
			tmpEntry.setId(details.getId());
			tmpEntry.setHost(details.getHost());
			tmpEntry.setPort(details.getPort());
			tmpEntry.setStatus(details.getStatus());
			tmpEntry.setUserType(details.getUserType());
			tmpEntry.setPassword(details.getPassword());
			
			session.save(tmpEntry);

			t.commit();
			log.debug("updateXmppIdentityDetails Record Saved.");
			
			


	
		} catch (Exception e) {
	
			// Do nothing
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}
	
	/**
	 * Gets the xmpp identity details.
	 *
	 * @param details the details
	 * @return the xmpp identity details
	 */
	@SuppressWarnings("unchecked")
	public DaUserRecord getXmppIdentityDetails(String details) {
		
		DaUserRecord recordDetails = new DaUserRecord();
		Session session = sessionFactory.openSession();
		DaRegistryRecordEntry filterRegistryEntry = new DaRegistryRecordEntry();
		DaRegistryRecordEntry tmpEn = null;
	
		try {

			filterRegistryEntry.setName(details);

			List<DaRegistryRecordEntry> tmpRegistryEntryList = session.createCriteria(DaRegistryRecordEntry.class)
					.add(Restrictions.eq("name",
							filterRegistryEntry.getName()).ignoreCase()).list();
			
			if (tmpRegistryEntryList != null) {
				tmpEn = tmpRegistryEntryList.get(0);
				recordDetails.setName(tmpEn.getName());
				recordDetails.setId(tmpEn.getId());
				recordDetails.setHost(tmpEn.getHost());
				recordDetails.setPort(tmpEn.getPort());
				recordDetails.setStatus(tmpEn.getStatus());
				recordDetails.setUserType(tmpEn.getUserType());
				recordDetails.setPassword(tmpEn.getPassword());
			}

	
		} catch (Exception e) {
	
			// Do nothing
	
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return recordDetails;
	}
	
	/**
	 * Gets the xmpp identity details.
	 *
	 * @return the xmpp identity details
	 */
	@SuppressWarnings("unchecked")
	public List<DaUserRecord> getXmppIdentityDetails() {
		Session session = sessionFactory.openSession();

		List<DaUserRecord> userList = new ArrayList<DaUserRecord>();
		DaUserRecord tmpUserEntry = null;
		try {
			

		List<DaRegistryRecordEntry> tmpRegistryEntryList = session.createCriteria(DaRegistryRecordEntry.class).list();
		
		if (tmpRegistryEntryList != null) {
			for (DaRegistryRecordEntry tmpEn : tmpRegistryEntryList) {
				tmpUserEntry = new DaUserRecord();

				tmpUserEntry.setName(tmpEn.getName());
				tmpUserEntry.setId(tmpEn.getId());
				tmpUserEntry.setHost(tmpEn.getHost());
				tmpUserEntry.setPort(tmpEn.getPort());
				tmpUserEntry.setStatus(tmpEn.getStatus());
				tmpUserEntry.setUserType(tmpEn.getUserType());
				tmpUserEntry.setPassword(tmpEn.getPassword());
				userList.add(tmpUserEntry);
			}
		}

		} catch (Exception e) {

			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		
		return userList;

	}

	

}
