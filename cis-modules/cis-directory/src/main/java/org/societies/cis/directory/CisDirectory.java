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
 package org.societies.cis.directory;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.cis.directory.model.CisAdvertisementRecordEntry;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;

public class CisDirectory implements ICisDirectory {
	private SessionFactory sessionFactory;
	private static Logger log = LoggerFactory.getLogger(CisDirectory.class);



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


	public CisDirectory() {
		log.info("CIS Directory bundle instantiated.");
	}


	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.cis.directory.ICisDirectory#addCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void addCisAdvertisementRecord(CisAdvertisementRecord cisAdRec) {
		
		log.info("addCisAdvertisementRecord called.");
		System.out.println("+++++++++++++++++++++++ Name is  = " + cisAdRec.getName());
		System.out.println("+++++++++++++++++++++++ ID is  = " + cisAdRec.getId());
		System.out.println("+++++++++++++++++++++++ Uri is  = " + cisAdRec.getUri());
		
		Session session = sessionFactory.openSession();
		CisAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CisAdvertisementRecordEntry(cisAdRec.getName(),
					cisAdRec.getId(), cisAdRec.getUri(), cisAdRec.getPassword(), cisAdRec.getType(), 1);

			session.save(tmpEntry);

			t.commit();
			log.debug("Cis Advertisement Record Saved.");

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
	 * org.societies.api.cis.directory.ICisDirectory#deleteCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdRec) {
		Session session = sessionFactory.openSession();
		CisAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CisAdvertisementRecordEntry(cisAdRec.getName(),
					cisAdRec.getId(), cisAdRec.getUri(), cisAdRec.getPassword(), cisAdRec.getType(), 1);

			session.delete(tmpEntry);

			t.commit();
			log.debug("Cis Advertisement Record deleted.");

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
	 * org.societies.api.cis.directory.ICisDirectory#findAllCisAdvertisementRecords
	 * ()
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CisAdvertisementRecord>> findAllCisAdvertisementRecords() {
		Session session = sessionFactory.openSession();
		List<CisAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CisAdvertisementRecordEntry>();
		List<CisAdvertisementRecord> returnList = new ArrayList<CisAdvertisementRecord>();
		CisAdvertisementRecord record = null;

	
		try {

			tmpAdvertList = session.createCriteria(
					CisAdvertisementRecordEntry.class).list();

			for (CisAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CisAdvertisementRecord();
				record.setName(entry.getName());
				record.setId(entry.getId());
				record.setUri(entry.getUri());
				record.setPassword(entry.getpassword());
				record.setType(entry.gettype());
				//record.setMode(entry.getmode()); TODO: replace with membership criteria

				returnList.add(record);

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return new AsyncResult<List<CisAdvertisementRecord>>(returnList);

	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.cis.directory.ICisDirectory#findForAllCis(org.societies
	 * .api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CisAdvertisementRecord>> findForAllCis( CisAdvertisementRecord filteredcis, String filter) {

	//filter by name, search directory and return CISs that match the relevant name
		Session session = sessionFactory.openSession();
		List<CisAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CisAdvertisementRecordEntry>();
		List<CisAdvertisementRecord> returnList = new ArrayList<CisAdvertisementRecord>();
		CisAdvertisementRecord record = null;

		try {

			tmpAdvertList = session.createCriteria(
					CisAdvertisementRecordEntry.class).list();

			for (CisAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CisAdvertisementRecord();
				if (record.getName().equals(filter)) {
					record.setName(entry.getName());
					record.setId(entry.getId());
					record.setUri(entry.getUri());
					record.setPassword(entry.getpassword());
					record.setType(entry.gettype());
					//record.setMode(entry.getmode()); TODO: replace with membership criteria

					returnList.add(record);
				}
					

			}

		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return new AsyncResult<List<CisAdvertisementRecord>>(returnList);
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see
	 * org.societies.api.cis.directory.ICisDirectory#updateCisAdvertisementRecord(org.societies
	 * .api.schema.cis.directory.CisAdvertisementRecord,
	 * org.societies.api.schema.cis.directory.CisAdvertisementRecord)
	 */
	@Override
	public void updateCisAdvertisementRecord(CisAdvertisementRecord oldCisValues,
		CisAdvertisementRecord updatedCisValues) {
		Session session = sessionFactory.openSession();
		CisAdvertisementRecordEntry tmpEntry = null;

		Transaction t = session.beginTransaction();
		try {

			tmpEntry = new CisAdvertisementRecordEntry(oldCisValues.getName(),
					oldCisValues.getId(), oldCisValues.getUri(), oldCisValues.getPassword(), oldCisValues.getType(), 1);
			session.delete(tmpEntry);

			tmpEntry.setName(updatedCisValues.getName());
			tmpEntry.setId(updatedCisValues.getId());
			tmpEntry.setUri(updatedCisValues.getUri());
			tmpEntry.setPassword(updatedCisValues.getPassword());
			tmpEntry.setType(updatedCisValues.getType());
			tmpEntry.setMode(1);
			session.save(tmpEntry);

			t.commit();
			log.debug("Cis Advertisement Record updated.");

		} catch (Exception e) {
			t.rollback();
			e.printStackTrace();
		} finally {
			if (session != null) {
				session.close();
			}
		}

	}


	@Override
	public Integer AddPeerDirectory(String arg0, String arg1, Integer arg2) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean RegisterCis(ICisAdvertisementRecord arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean UnregisterCis(ICisAdvertisementRecord arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public String getURI() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public Boolean ping() {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ICisAdvertisementRecord[] searchByName(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ICisAdvertisementRecord[] searchByOwner(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}


	@Override
	public ICisAdvertisementRecord[] searchByUri(String arg0) {
		// TODO Auto-generated method stub
		return null;
	}

}
