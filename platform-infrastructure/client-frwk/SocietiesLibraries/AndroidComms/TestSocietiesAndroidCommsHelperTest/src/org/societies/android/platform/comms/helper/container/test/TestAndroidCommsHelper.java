package org.societies.android.platform.comms.helper.test;

import org.societies.android.platform.comms.helper.ClientCommunicationMgr;

import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestAndroidCommsHelper extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	@MediumTest
	public void testConstructorLogin() throws Exception {
		ClientCommunicationMgr ccm = new ClientCommunicationMgr(this.getContext(), false);
		assertTrue(null != ccm);
	}
}
