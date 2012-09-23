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
 package org.societies.css.directory;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.css.directory.ICssDirectory;
import org.societies.css.directory.model.CssAdvertisementRecordEntry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class CssDirectory implements ICssDirectory {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(CssDirectory.class);

	
	
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
		this.sessionFactory = sessionFactory;
	}


	public CssDirectory() {
		log.info("CSS Directory bundle instantiated.");
	}

	
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#addCssAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void addCssAdvertisementRecord(CssAdvertisementRecord cssAdRec) {
		Session session = sessionFactory.openSession();
		CssAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CssAdvertisementRecordEntry(cssAdRec.getName(),
					cssAdRec.getId(), cssAdRec.getUri());

			session.save(tmpEntry);

			t.commit();
			log.debug("Css Advertisement Record Saved.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#deleteCssAdvertisementRecord
	 * (org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void deleteCssAdvertisementRecord(CssAdvertisementRecord cssAdRec) {
		Session session = sessionFactory.openSession();
		CssAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CssAdvertisementRecordEntry(cssAdRec.getName(),
					cssAdRec.getId(), cssAdRec.getUri());

			session.delete(tmpEntry);

			t.commit();
			log.debug("Css Advertisement Record deleted.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#findAllCssAdvertisementRecords
	 * ()
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords() {
		Session session = sessionFactory.openSession();
		List<CssAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CssAdvertisementRecordEntry>();
		List<CssAdvertisementRecord> returnList = new ArrayList<CssAdvertisementRecord>();
		CssAdvertisementRecord record = null;

		try {

			tmpAdvertList = session.createCriteria(
					CssAdvertisementRecordEntry.class).list();

			for (CssAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CssAdvertisementRecord();
				record.setName(entry.getName());
				record.setId(entry.getId());
				record.setUri(entry.getUri());

				returnList.add(record);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return new AsyncResult<List<CssAdvertisementRecord>>(returnList);

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#findForAllCss(org.societies
	 * .api.schema.css.directory.CssAdvertisementRecord)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CssAdvertisementRecord>> findForAllCss(
			CssAdvertisementRecord filterCss) {
		
	//TODO : Need to add filter . for now it returns everything	
		Session session = sessionFactory.openSession();
		List<CssAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CssAdvertisementRecordEntry>();
		List<CssAdvertisementRecord> returnList = new ArrayList<CssAdvertisementRecord>();
		CssAdvertisementRecord record = null;

		try {

			tmpAdvertList = session.createCriteria(
					CssAdvertisementRecordEntry.class).list();

			for (CssAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CssAdvertisementRecord();
				record.setName(entry.getName());
				record.setId(entry.getId());
				record.setUri(entry.getUri());

				returnList.add(record);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return new AsyncResult<List<CssAdvertisementRecord>>(returnList);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#updateCssAdvertisementRecord(org.societies
	 * .api.schema.css.directory.CssAdvertisementRecord,
	 * org.societies.api.schema.css.directory.CssAdvertisementRecord)
	 */
	@Override
	public void updateCssAdvertisementRecord(CssAdvertisementRecord oldCssValues,
		CssAdvertisementRecord updatedCssValues) {
		Session session = sessionFactory.openSession();
		CssAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CssAdvertisementRecordEntry(updatedCssValues.getName(),
					updatedCssValues.getId(), updatedCssValues.getUri());
			
			session.saveOrUpdate(tmpEntry);

			t.commit();
			log.debug("Css Advertisement Record updated.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.societies.api.css.directory.ICssDirectory#findAllCssAdvertisementRecords
	 * ()
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CssAdvertisementRecord>> searchByID(List<String> cssIds) {
		Session session = sessionFactory.openSession();
		List<CssAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CssAdvertisementRecordEntry>();
		List<CssAdvertisementRecord> returnList = new ArrayList<CssAdvertisementRecord>();
		CssAdvertisementRecord record = null;

		try {


			tmpAdvertList = session.createCriteria(CssAdvertisementRecordEntry.class).
					add(Restrictions.in("id",cssIds)).list();
			
			for (CssAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CssAdvertisementRecord();
				record.setName(entry.getName());
				record.setId(entry.getId());
				record.setUri(entry.getUri());

				returnList.add(record);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return new AsyncResult<List<CssAdvertisementRecord>>(returnList);

	}
	
}
