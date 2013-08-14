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
package org.societies.privacytrust.privacyprotection.assessment.test;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.IdentityType;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacyassessment.DataAccessLogEntry;
import org.societies.identity.IdentityImpl;
import org.societies.privacytrust.privacyprotection.assessment.log.PrivacyLog;
import org.societies.privacytrust.privacyprotection.assessment.logic.DataAccessAnalyzer;

/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class DataAccessAnalyzerTest {

	private DataAccessAnalyzer dataAccessAnalyzer;
	
	private Date time1 = new Date(1000000);
	private Date time2 = new Date(2000000);
	private Date time3 = new Date(3000000);
	private Date time4 = new Date(4000000);
	private Date time5 = new Date(5000000);
	private Date time6 = new Date(6000000);
	private Date time7 = new Date(7000000);
	
	private IIdentity id1 = new IdentityImpl(IdentityType.CSS_RICH, "identifier1", "domain1");
	private IIdentity id2 = new IdentityImpl(IdentityType.CSS_LIGHT, "identifier2", "domain1");
	private IIdentity id3 = new IdentityImpl(IdentityType.CSS, "identifier3", "domain1");
	private IIdentity id4 = new IdentityImpl(IdentityType.CIS, "identifier4", "domain2");
	
	private String class1 = "class1";
	private String class2 = "class2";
	private String class3 = "class3";

	private List<String> stack1 = new ArrayList<String>();
	private List<String> stack2 = new ArrayList<String>();
	private List<String> stack3 = new ArrayList<String>();

	private String bundle1 = "bundle1";
	private String bundle2 = "bundle2";
	private String bundle3 = "bundle3";
	private String bundle4 = "bundle4";

	private List<String> bundles1 = new ArrayList<String>();
	private List<String> bundles2 = new ArrayList<String>();
	private List<String> bundles3 = new ArrayList<String>();
	private List<String> bundles4 = new ArrayList<String>();

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		
		stack1.add(class1);
		stack2.add(class2);
		stack3.add(class3);

		bundles1.add(bundle1);
		bundles2.add(bundle2);
		bundles3.add(bundle3);
		bundles4.add(bundle2);
		bundles4.add(bundle4);

		PrivacyLog privacyLog;
		
		privacyLog = new PrivacyLog();
		
		privacyLog.append(new DataAccessLogEntry(time1, id1, class1, stack1, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time2, id1, class2, stack2, bundles1, id2, -1));
		privacyLog.append(new DataAccessLogEntry(time3, id1, class3, stack3, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time4, id1, class3, stack3, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time5, id1, class1, stack1, bundles1, id3, -1));
		privacyLog.append(new DataAccessLogEntry(time6, id2, class1, stack1, bundles1, id4, -1));
		privacyLog.append(new DataAccessLogEntry(time7, id2, class1, stack1, bundles1, id1, -1));
		privacyLog.append(new DataAccessLogEntry(time7, null, null, null, null, null, -1));

		dataAccessAnalyzer = new DataAccessAnalyzer(privacyLog.getDataAccess());
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.assessment.logic.DataAccessAnalyzer#getDataAccess(org.societies.api.identity.IIdentity, java.util.Date, java.util.Date)}.
	 */
	@Test
	public void testGetDataAccessIIdentityDateDate() {
		
		List<DataAccessLogEntry> result = dataAccessAnalyzer.getDataAccess(id1, time2, time7);
		
		assertEquals(3, result.size(), 0.0);
		
		assertTrue(result.get(0).getRequestor().equals(id1));
		assertTrue(result.get(0).getRequestorClass().equals(class3));
		assertTrue(result.get(0).getTime().equals(time3));
		assertTrue(result.get(0).getOwner().equals(id1));
		
		assertTrue(result.get(1).getRequestor().equals(id1));
		assertTrue(result.get(1).getRequestorClass().equals(class3));
		assertTrue(result.get(1).getTime().equals(time4));
		assertTrue(result.get(1).getOwner().equals(id1));
		
		assertTrue(result.get(2).getRequestor().equals(id1));
		assertTrue(result.get(2).getRequestorClass().equals(class1));
		assertTrue(result.get(2).getTime().equals(time5));
		assertTrue(result.get(2).getOwner().equals(id3));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.assessment.logic.DataAccessAnalyzer#getDataAccess(java.lang.String, java.util.Date, java.util.Date)}.
	 */
	@Test
	public void testGetDataAccessStringDateDate() {
		
		List<DataAccessLogEntry> result = dataAccessAnalyzer.getDataAccess(class3, time3, time6);

		assertEquals(1, result.size(), 0.0);
		
		assertTrue(result.get(0).getRequestor().equals(id1));
		assertTrue(result.get(0).getRequestorClass().equals(class3));
		assertTrue(result.get(0).getTime().equals(time4));
		assertTrue(result.get(0).getOwner().equals(id1));
	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.assessment.logic.DataAccessAnalyzer#getDataAccessSize(org.societies.api.identity.IIdentity, java.util.Date, java.util.Date)}.
	 */
	@Test
	public void testGetDataAccessSizeIIdentityDateDate() {
		
		// id1
		assertEquals(4, dataAccessAnalyzer.getNumDataAccessEvents(id1, time1, time7), 0.0);
		assertEquals(3, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time7), 0.0);
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(id1, time3, time7), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(id1, time4, time7), 0.0);
		assertEquals(3, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time6), 0.0);
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time5), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time4), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time3), 0.0);

		// id2
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id2, time1, time5), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id2, time2, time6), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(id2, time3, time7), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(id2, time5, time7), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id2, time6, time7), 0.0);

		// Start time after end time => should be 0 entires
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id1, time2, time1), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(id2, time4, time2), 0.0);

	}

	/**
	 * Test method for {@link org.societies.privacytrust.privacyprotection.assessment.logic.DataAccessAnalyzer#getDataAccessSize(java.lang.String, java.util.Date, java.util.Date)}.
	 */
	@Test
	public void testGetDataAccessSizeStringDateDate() {

		// class1: times 1, 5, 6, 7
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(class1, time1, time7), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(class1, time1, time3), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(class1, time2, time4), 0.0);
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(class1, time2, time7), 0.0);

		// class2: time 2
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(class2, time1, time7), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(class2, time1, time3), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(class2, time2, time4), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(class2, time2, time7), 0.0);
		
		// class3: times 3, 4
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(class3, time1, time7), 0.0);
		assertEquals(0, dataAccessAnalyzer.getNumDataAccessEvents(class3, time1, time3), 0.0);
		assertEquals(1, dataAccessAnalyzer.getNumDataAccessEvents(class3, time2, time4), 0.0);
		assertEquals(2, dataAccessAnalyzer.getNumDataAccessEvents(class3, time2, time7), 0.0);
	}

}
