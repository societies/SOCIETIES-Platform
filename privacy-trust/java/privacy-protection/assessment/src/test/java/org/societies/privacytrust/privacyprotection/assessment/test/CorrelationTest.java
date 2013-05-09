package org.societies.privacytrust.privacyprotection.assessment.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;
import org.societies.privacytrust.privacyprotection.assessment.logic.Correlation;

public class CorrelationTest {

	Correlation correlation;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		PrivacyLog privacyLog = new PrivacyLog();
		correlation = new Correlation(privacyLog);
	}

	@Test
	public void testIsAnyMemberEqual() {
		
		List<String> list1 = new ArrayList<String>();
		List<String> list2 = new ArrayList<String>();
		
		list1.add("aaa");
		list1.add("bbb");
		list1.add("ccc");

		// Common entry
		list1.add("eee");

		list2.add("ddd");
		list2.add("eee");
		list2.add("fff");
		list2.add("ggg");
		list2.add("hhh");
		
		assertTrue(correlation.isAnyMemberEqual(list1, list2));

		list1.remove("eee");
		assertFalse(correlation.isAnyMemberEqual(list1, list2));
		
		list1.add("aaa");
		assertFalse(correlation.isAnyMemberEqual(list1, list2));

		list2.add("aaa");
		assertTrue(correlation.isAnyMemberEqual(list1, list2));
	}

}
