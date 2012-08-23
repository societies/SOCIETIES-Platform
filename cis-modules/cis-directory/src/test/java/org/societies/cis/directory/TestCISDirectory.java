package org.societies.cis.directory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.mockito.Mockito.*;
import org.societies.cis.directory.CisDirectory;
import org.societies.cis.directory.model.CisAdvertisementRecordEntry;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.annotation.Rollback;

import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;




@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:META-INF/TestCISDirectory-context.xml"})
public class TestCISDirectory extends AbstractTransactionalJUnit4SpringContextTests{
	
	
	@Autowired
	private CisDirectory cisDir;
	private CisAdvertisementRecord cisAdvert1;
	private CisAdvertisementRecord cisAdvert2;
	private CisAdvertisementRecord cisAdvert2new;
	
//	private static SessionFactory sessionfactory;
//	private static Configuration configuration;

	@Before
	public void setUp() throws Exception {
		//CIS DIR INSTANCE
	//	cisDir = CisDirectory.class.newInstance();
		assertTrue(null != cisDir);
		
		//CIS ADVERT 1
		cisAdvert1 = CisAdvertisementRecord.class.newInstance();
		
		MembershipCrit memberCrit1 = new MembershipCrit();
		List<Criteria> criteria1 = new ArrayList<Criteria>();
		
		Criteria crit1a = new Criteria();
			crit1a.setAttrib("location");
			crit1a.setOperator("=");
			crit1a.setRank(1);
			crit1a.setValue1("Dublin");
			criteria1.add(crit1a);
		
		Criteria crit1b = new Criteria();
			crit1b.setAttrib("age");
			crit1b.setOperator(">");
			crit1b.setRank(2);
			crit1b.setValue1("18");
			criteria1.add(crit1b);
		
		cisAdvert1.setName("record1");
		cisAdvert1.setId("pubs.societies.org");
		cisAdvert1.setUri("/home/advert1");
		memberCrit1.setCriteria(criteria1);
		cisAdvert1.setMembershipCrit(memberCrit1);
		
		assertTrue(null != cisAdvert1);
		System.out.println("CIS ADVERT not null");
		assertTrue(null != cisAdvert1.getMembershipCrit());
		System.out.println("getMembershipCrit not null");
		assertTrue(null != cisAdvert1.getMembershipCrit().getCriteria());
		System.out.println("getCriteria not null");
		
		//CIS ADVERT 2
		MembershipCrit memberCrit2 = new MembershipCrit();
		List<Criteria> criteria2 = new ArrayList<Criteria>();
		
		Criteria crit2a = new Criteria();
		crit2a.setAttrib("location");
		crit2a.setOperator("=");
		crit2a.setRank(1);
		crit2a.setValue1("London");
		criteria2.add(crit1a);
		
		Criteria crit2b = new Criteria();
		crit2b.setAttrib("age");
		crit2b.setOperator("<=");
		crit2b.setRank(2);
		crit2b.setValue1("23");
		criteria2.add(crit2b);
		
		cisAdvert2 = CisAdvertisementRecord.class.newInstance();
		cisAdvert2.setName("record2");
		cisAdvert2.setId("olympics.societies.org");
		cisAdvert2.setUri("/home/advert2");
		memberCrit2.setCriteria(criteria2);
		cisAdvert2.setMembershipCrit(memberCrit2);
		
		assertTrue(null != cisAdvert2);
		
		//create CIS Advertisement 2 new
		cisAdvert2new = CisAdvertisementRecord.class.newInstance();
		cisAdvert2new.setName("record2new");
		cisAdvert2new.setId("leona2@societies.org");
		cisAdvert2new.setUri("/home/advert2");
		assertTrue(null != cisAdvert2new);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(false)
	public void addCISREcordtest() {
		
		try {
			System.out.println("ADDING CIS_ADVERT1...");
			System.out.println("Name is  = " + cisAdvert1.getName());
			System.out.println("ID is  = " + cisAdvert1.getId());
			System.out.println("Uri is  = " + cisAdvert1.getUri());
			System.out.println("Criteria count = " + cisAdvert1.getMembershipCrit().getCriteria().size());
			
			cisDir.addCisAdvertisementRecord(cisAdvert1);
		} catch (Exception e) {
			e.printStackTrace();
			assert(false);
		}
		System.out.println("CIS_ADVERT1 added successfully.");
		
		List<CisAdvertisementRecord> listResults = null;
		Future<List<CisAdvertisementRecord>> asyncResult = cisDir.findAllCisAdvertisementRecords();
		try {
			listResults = asyncResult.get();
		} catch (InterruptedException e) {
			e.printStackTrace();
			assert(false);
		} catch (ExecutionException e) {
			e.printStackTrace();
			assert(false);
		}
		System.out.println("Result Count=" + listResults.size());
		assert(true);
	}

	@Ignore
	public void findallCISREcordtest() {
		
	}
	
	@Ignore
	public void deleteCISREcordtest() {
		
		try {
			cisDir.addCisAdvertisementRecord(cisAdvert1);
			cisDir.addCisAdvertisementRecord(cisAdvert2);
		}catch (Exception e) {
			assert(false);
			e.printStackTrace();
		}
		
		try {
			cisDir.deleteCisAdvertisementRecord(cisAdvert2);
		}catch (Exception e) {
			assert(false);
			e.printStackTrace();
		}
		assert(true);		
	}
	
	@Ignore
	public void modifyCISREcordtest() {
		
		CisAdvertisementRecord oldCisValues = cisAdvert1;
		CisAdvertisementRecord updatedCisValues = cisAdvert2new;
		
		try {
			cisDir.addCisAdvertisementRecord(cisAdvert1);
			cisDir.addCisAdvertisementRecord(cisAdvert2);
			cisDir.updateCisAdvertisementRecord(oldCisValues, updatedCisValues);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
//	public static SessionFactory getSessionFactory(){
//		return sessionfactory;
//	}

//	public static SessionFactory setSessionFactory(){
//		return sessionfactory;
//	}
	
}
