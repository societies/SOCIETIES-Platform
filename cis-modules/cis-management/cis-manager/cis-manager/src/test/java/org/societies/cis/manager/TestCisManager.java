package org.societies.cis.manager;

import static org.junit.Assert.*;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.internal.comm.ICISCommunicationMgrFactory;
import org.societies.cis.persistance.IPersistanceManager;

import static org.mockito.Mockito.*;

public class TestCisManager {
	
	
	private CisManager cisManagerUnderTest;
	private ICISCommunicationMgrFactory mockCcmFactory;
	private ICommManager mockCSSendpoint;
	private IPersistanceManager mockPM;
	
	@Before
	public void setUp() throws Exception {
		// create mocked class
		mockCcmFactory = mock(ICISCommunicationMgrFactory.class);
		mockCSSendpoint = mock (ICommManager.class);
		mockPM = mock(IPersistanceManager.class);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testConstructor() {
		//cisManagerUnderTest = new CisManager(mockCcmFactory,mockCSSendpoint, mockPM);
	}

	

}
