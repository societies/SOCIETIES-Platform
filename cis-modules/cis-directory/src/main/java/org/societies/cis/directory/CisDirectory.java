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

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.societies.api.cis.directory.ICisAdvertisementRecord;
import org.societies.api.cis.directory.ICisDirectory;
import org.societies.cis.directory.model.CisAdvertisementRecordEntry;
import org.societies.cis.directory.model.CriteriaRecordEntry;
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


	/* @see org.societies.api.cis.directory.ICisDirectory#addCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord) */
	@Override
	public void addCisAdvertisementRecord(CisAdvertisementRecord cisAdRec) {
		
		log.info("addCisAdvertisementRecord called.");
		System.out.println("+++++++++++++++++++++++ Name is  = " + cisAdRec.getName());
		System.out.println("+++++++++++++++++++++++ ID is  = " + cisAdRec.getId());
		System.out.println("+++++++++++++++++++++++ Owner Id is  = " + cisAdRec.getCssownerid());
		
		Session session = sessionFactory.openSession();
		CisAdvertisementRecordEntry advertEntry = null;

		Transaction t = session.beginTransaction();
		try {
			//ADVERTISEMENT RECORD
			advertEntry = new CisAdvertisementRecordEntry(cisAdRec.getName(),
					cisAdRec.getId(), cisAdRec.getCssownerid(), cisAdRec.getPassword(), cisAdRec.getType());
			session.save(advertEntry);
			
			//SET OF CRITERIA RECORDS 
			for(Criteria tmpCrit: cisAdRec.getMembershipCrit().getCriteria()) {
				CriteriaRecordEntry critEntry = new CriteriaRecordEntry(tmpCrit.getAttrib(), tmpCrit.getOperator(), tmpCrit.getValue1(), tmpCrit.getValue2(), tmpCrit.getRank());
				critEntry.setCisAdvertRecord(advertEntry);
				advertEntry.getCriteriaRecords().add(critEntry);
				session.save(critEntry);
			}
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

	/*@see org.societies.api.cis.directory.ICisDirectory#deleteCisAdvertisementRecord
	 * (org.societies.api.schema.cis.directory.CisAdvertisementRecord) */
	@Override
	public void deleteCisAdvertisementRecord(CisAdvertisementRecord cisAdRec) {
		Session session = sessionFactory.openSession();

		Transaction t = session.beginTransaction();
		try {
			Query dbQuery = session.createQuery("FROM CisAdvertisementRecordEntry WHERE id = :CisId");
			dbQuery.setParameter("CisId", cisAdRec.getId());
			CisAdvertisementRecordEntry advertEntry = (CisAdvertisementRecordEntry)dbQuery.list().get(0);
			
			session.delete(advertEntry);
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

	/*@see org.societies.api.cis.directory.ICisDirectory#findAllCisAdvertisementRecords() */
	@SuppressWarnings("unchecked")
	@Override
	@Async
	public Future<List<CisAdvertisementRecord>> findAllCisAdvertisementRecords() {
		Session session = sessionFactory.openSession();
		List<CisAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CisAdvertisementRecordEntry>();
		List<CisAdvertisementRecord> returnList = new ArrayList<CisAdvertisementRecord>();
		CisAdvertisementRecord record = null;
	
		try {
			tmpAdvertList = session.createCriteria(CisAdvertisementRecordEntry.class).list();

			for (CisAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CisAdvertisementRecord();
				record.setName(entry.getName());
				record.setId(entry.getId());
				record.setCssownerid(entry.getCssOwnerId());
				record.setPassword(entry.getpassword());
				record.setType(entry.gettype());
				//MEMBERSHIP CRITERIA
				MembershipCrit memberCrit = new MembershipCrit();
				for(CriteriaRecordEntry critRecord: entry.getCriteriaRecords()) {
					Criteria crit = new Criteria();
					crit.setAttrib(critRecord.getAttrib());
					crit.setOperator(critRecord.getOperator());
					crit.setRank(critRecord.getRank());
					crit.setValue1(critRecord.getValue1());
					crit.setValue2(critRecord.getValue2());
					memberCrit.getCriteria().add(crit);
				}
				record.setMembershipCrit(memberCrit); 

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

	/*@see org.societies.api.cis.directory.ICisDirectory#findForAllCis(org.societies
	 * .api.schema.cis.directory.CisAdvertisementRecord) */
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
			tmpAdvertList = session.createCriteria(CisAdvertisementRecordEntry.class).list();

			for (CisAdvertisementRecordEntry entry : tmpAdvertList) {
				record = new CisAdvertisementRecord();
				if (record.getName().equals(filter)) {
					record.setName(entry.getName());
					record.setId(entry.getId());
					record.setCssownerid(entry.getCssOwnerId());
					record.setPassword(entry.getpassword());
					record.setType(entry.gettype()); 
					//MEMBERSHIP CRITERIA
					MembershipCrit memberCrit = new MembershipCrit();
					for(CriteriaRecordEntry critRecord: entry.getCriteriaRecords()) {
						Criteria crit = new Criteria();
						crit.setAttrib(critRecord.getAttrib());
						crit.setOperator(critRecord.getOperator());
						crit.setRank(critRecord.getRank());
						crit.setValue1(critRecord.getValue1());
						crit.setValue2(critRecord.getValue2());
						memberCrit.getCriteria().add(crit);
					}
					record.setMembershipCrit(memberCrit); 

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

	/* @see org.societies.api.cis.directory.ICisDirectory#updateCisAdvertisementRecord(org.societies.api.schema.cis.directory.CisAdvertisementRecord,
	 * org.societies.api.schema.cis.directory.CisAdvertisementRecord) */
	@Override
	public void updateCisAdvertisementRecord(CisAdvertisementRecord oldCisValues, CisAdvertisementRecord updatedCisValues) {
		this.deleteCisAdvertisementRecord(oldCisValues);
		this.addCisAdvertisementRecord(updatedCisValues);
		/*
		 Session session = sessionFactory.openSession();
		 
		Transaction t = session.beginTransaction();
		try {
			//DELETE OLD ADVERTISEMENT RECORD
			Query dbQuery = session.createQuery("FROM CisAdvertisementRecordEntry WHERE id = :CisId");
			dbQuery.setParameter("CisId", oldCisValues.getId());
			CisAdvertisementRecordEntry advertEntry = (CisAdvertisementRecordEntry)dbQuery.list().get(0);
			System.out.println("+++++++++++++++++++++++ Deleting Record" + oldCisValues.getId());
			session.delete(advertEntry);
			
			//NEW ADVERTISEMENT RECORD
			CisAdvertisementRecordEntry newAdvertEntry = new CisAdvertisementRecordEntry(updatedCisValues.getName(), updatedCisValues.getId(), 
					updatedCisValues.getUri(), updatedCisValues.getPassword(), updatedCisValues.getType());
			session.save(newAdvertEntry);
			
			//SET OF CRITERIA RECORDS 
			for(Criteria tmpCrit: updatedCisValues.getMembershipCrit().getCriteria()) {
				CriteriaRecordEntry critEntry = new CriteriaRecordEntry(tmpCrit.getAttrib(), tmpCrit.getOperator(), tmpCrit.getValue1(), tmpCrit.getValue2(), tmpCrit.getRank());
				critEntry.setCisAdvertRecord(advertEntry);
				newAdvertEntry.getCriteriaRecords().add(critEntry);
				session.save(critEntry);
			}
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
		*/
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
	
	@Override
	public Future<List<CisAdvertisementRecord>> searchByID(String cisID) {
		//filter by id, search directory and return CISs that match the relevant cis id 
		// should only be one, but easier to return a list of one as that is
		// what all other searches will return a list
		Session session = sessionFactory.openSession();
		List<CisAdvertisementRecordEntry> tmpAdvertList = new ArrayList<CisAdvertisementRecordEntry>();
		List<CisAdvertisementRecord> returnList = new ArrayList<CisAdvertisementRecord>();
		CisAdvertisementRecord record = null;

		try {
			tmpAdvertList = session.createCriteria(CisAdvertisementRecordEntry.class)
					.add(Restrictions.eq("id", cisID).ignoreCase()).list();

			if ((tmpAdvertList != null) && (tmpAdvertList.size() > 0))
			{
				for (CisAdvertisementRecordEntry entry : tmpAdvertList) {
						
					// Since it's uniquer we should only get one
					record = new CisAdvertisementRecord();
					record.setName(entry.getName());
					record.setId(entry.getId());
					record.setCssownerid(entry.getCssOwnerId());
					record.setPassword(entry.getpassword());
					record.setType(entry.gettype()); 
					//MEMBERSHIP CRITERIA
					MembershipCrit memberCrit = new MembershipCrit();
					for(CriteriaRecordEntry critRecord: entry.getCriteriaRecords()) {
						Criteria crit = new Criteria();
						crit.setAttrib(critRecord.getAttrib());
						crit.setOperator(critRecord.getOperator());
						crit.setRank(critRecord.getRank());
						crit.setValue1(critRecord.getValue1());
						crit.setValue2(critRecord.getValue2());
						memberCrit.getCriteria().add(crit);
					}
					record.setMembershipCrit(memberCrit); 

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
}
