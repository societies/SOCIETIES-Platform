package org.societies.css.directory;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.css.directory.model.CssAdvertisementRecordEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"TestCSSDirectory-context.xml"})
public class TestCCSDirectory extends AbstractTransactionalJUnit4SpringContextTests{
	
	
	@Autowired
	private CssDirectory cssDir;

	
	@Before
	public void setUp() throws Exception {
		assertTrue(null != cssDir);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	@Rollback(true)
	public void searchByIdtest() {
		CssAdvertisementRecord cssAdvert1 = new CssAdvertisementRecord();
		CssAdvertisementRecord cssAdvert2 = new CssAdvertisementRecord();
		List<String> searchIdList = new ArrayList<String>();
		
		cssAdvert1.setId("ignoreme.societies.local");
		cssAdvert1.setName("I'll won't be found");
		cssAdvert1.setUri(" ");

		cssAdvert2.setId("findme.societies.local");
		cssAdvert2.setName("I'll be found");
		cssAdvert2.setUri(" ");
		
		cssDir.addCssAdvertisementRecord(cssAdvert1);
		cssDir.addCssAdvertisementRecord(cssAdvert2);
		
		
		try {
			
			
		
		searchIdList.add("findme.societies.local");
		
		Future<List<CssAdvertisementRecord>> asynchResult = cssDir.searchByID(searchIdList);
		
		
		List<CssAdvertisementRecord> result;

			result = asynchResult.get();
			assert(result != null);
			assert(result.size() == 1);
			assert(result.get(0).getId().contains("findme.societies.local"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		
		
		
	}
	
	
	@Test
	@Rollback(true)
	public void updatetest() {
		CssAdvertisementRecord cssAdvert1 = new CssAdvertisementRecord();
		CssAdvertisementRecord cssAdvert2 = new CssAdvertisementRecord();
		CssAdvertisementRecord cssAdvertResult = new CssAdvertisementRecord();
		List<String> searchIdList = new ArrayList<String>();
		
		cssAdvert1.setId("test.societies.local");
		cssAdvert1.setName("Old Record");
		cssAdvert1.setUri(" ");

		cssAdvert2.setId("test.societies.local");
		cssAdvert2.setName("New Record");
		cssAdvert2.setUri(" ");
		
		cssDir.addCssAdvertisementRecord(cssAdvert1);
		cssDir.updateCssAdvertisementRecord(cssAdvert1, cssAdvert2);
		
		
		try {
			
			
		
		searchIdList.add("test.societies.local");
		
		Future<List<CssAdvertisementRecord>> asynchResult = cssDir.searchByID(searchIdList);
		
		
		List<CssAdvertisementRecord> result;

			result = asynchResult.get();
			assert(result != null);
			assert(result.size() == 1);
			assert(result.get(0).getId().contains("test.societies.local"));
			assert(result.get(0).getName().contains("New Record"));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		
		
		
		
		
		
	}
	

}
