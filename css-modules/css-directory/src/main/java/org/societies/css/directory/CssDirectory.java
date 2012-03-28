package org.societies.css.directory;


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
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
	@Override
	@Async
	public Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords() {
		Session session = sessionFactory.openSession();
		List<CssAdvertisementRecordEntry> tmpAdvertList = null;
		List<CssAdvertisementRecord> returnList = new ArrayList<CssAdvertisementRecord>();
		CssAdvertisementRecord record = null;

		Transaction t = session.beginTransaction();
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
			t.rollback();
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
	@Override
	@Async
	public Future<List<CssAdvertisementRecord>> findForAllCss(
			CssAdvertisementRecord filterCss) {
		
	//TODO : Need to add filter . for now it returns everything	
		Session session = sessionFactory.openSession();
		List<CssAdvertisementRecordEntry> tmpAdvertList = null;
		List<CssAdvertisementRecord> returnList = new ArrayList<CssAdvertisementRecord>();
		CssAdvertisementRecord record = null;

		Transaction t = session.beginTransaction();
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
			t.rollback();
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

			tmpEntry = new CssAdvertisementRecordEntry(oldCssValues.getName(),
					oldCssValues.getId(), oldCssValues.getUri());
			session.delete(tmpEntry);

			tmpEntry.setName(updatedCssValues.getName());
			tmpEntry.setId(updatedCssValues.getId());
			tmpEntry.setUri(updatedCssValues.getUri());
			session.save(tmpEntry);

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

}
