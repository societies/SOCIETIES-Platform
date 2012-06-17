package org.societies.android.platform.content;

import android.test.AndroidTestCase;

public class TestDatabase extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
		getContext().deleteDatabase(CssRecordDAO.SOCIETIES_DATABASE_NAME);
	}

	protected void tearDown() throws Exception {
		
		super.tearDown();
	}

	public void testOpenDatabase() throws Exception {
		CssRecordDAO cssRecordDAO = new CssRecordDAO(getContext());
		assertTrue(null != cssRecordDAO);
		cssRecordDAO.openReadable();
		cssRecordDAO.close();
		
		assertTrue(stringArrayContains(getContext().databaseList(), CssRecordDAO.SOCIETIES_DATABASE_NAME));
		
	}
	
	/**
	 * Determine if a String array contains a value
	 * 
	 * @param array String array
	 * @param value contain value
	 * @return boolean true if if found
	 */
	private boolean stringArrayContains(String array [], String value) {
		boolean retValue = false;
		
		for (String element : array) {
			if (element.equals(value)) {
				retValue = true;
			}
		}
		return retValue;
	}
	
}
