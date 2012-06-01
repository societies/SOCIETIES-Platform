package org.societies.cis.directory;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Collection;
import static org.mockito.Mockito.*;
import org.societies.cis.directory.CisDirectory;
import org.societies.cis.directory.model.CisAdvertisementRecordEntry;
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
	
	
	private static SessionFactory sessionfactory;
	private static Configuration configuration;

	@Before
	public void setUp() throws Exception {
		
		cisDir = CisDirectory.class.newInstance();
		assertTrue(null != cisDir);
		
		cisAdvert1 = CisAdvertisementRecord.class.newInstance();
		assertTrue(null != cisAdvert1);
		cisAdvert2 = CisAdvertisementRecord.class.newInstance();
		assertTrue(null != cisAdvert2);
		cisAdvert2new = CisAdvertisementRecord.class.newInstance();
		assertTrue(null != cisAdvert2new);
		
		//create CIS Advertisement 1
		cisAdvert1.setName("record1");
		cisAdvert1.setId("liam@societies.org");
		cisAdvert1.setUri("/home/advert1");
		
		//create CIS Advertisement 2
		cisAdvert2.setName("record2");
		cisAdvert2.setId("leona@societies.org");
		cisAdvert2.setUri("/home/advert2");
		
		//create CIS Advertisement 2 new
		cisAdvert2new.setName("record2new");
		cisAdvert2new.setId("leona2@societies.org");
		cisAdvert2new.setUri("/home/advert2");
		
		//this.getsessionfactory();
		
		
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(false)
	public void addCISREcordtest() {
		
		try {
			System.out.println("Name is  = " + cisAdvert1.getName());
			System.out.println("ID is  = " + cisAdvert1.getId());
			System.out.println("Uri is  = " + cisAdvert1.getUri());
			
			
			
			
			cisDir.addCisAdvertisementRecord(cisAdvert1);
			//cisdirectory.addCisAdvertisementRecord(cisAdvert2);

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		


	}
	
	@Test
	public void deleteCISREcordtest() {
		
		try {
			cisDir.addCisAdvertisementRecord(cisAdvert1);
			cisDir.addCisAdvertisementRecord(cisAdvert2);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		try {
			cisDir.deleteCisAdvertisementRecord(cisAdvert2);
		}catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
	
	@Test
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
	
	@Ignore
	public void findallCISREcordtest() {
		//fail("Not yet implemented");
	}
	
	public static SessionFactory getsessionfactory(){
		if (sessionfactory == null)
			buildSessionFactory();
		return sessionfactory;
	}
	
	public static Configuration getconfiguration(){
		if (configuration == null)
			configuration = new Configuration();
		return configuration;
	}
	
	public static void buildSessionFactory(){
		if (sessionfactory != null && !sessionfactory.isClosed())
			sessionfactory.close();
		sessionfactory = getconfiguration().buildSessionFactory();
	}

}
