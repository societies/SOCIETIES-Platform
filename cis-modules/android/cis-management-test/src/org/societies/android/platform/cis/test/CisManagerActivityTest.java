package org.societies.android.platform.cis.test;

import org.societies.android.platform.cis.CisManagerActivity;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

public class CisManagerActivityTest extends ActivityInstrumentationTestCase2<CisManagerActivity> {
	
	private CisManagerActivity mActivity;
    private TextView mView;
    private String resourceString;
    
	public CisManagerActivityTest(){
	super("org.societies.android.platform.cis", CisManagerActivity.class);
	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#setUp()
	 */
	@Override
	protected void setUp() throws Exception {
		// TODO Auto-generated method stub
		super.setUp();
		mActivity = this.getActivity();
        mView = (TextView) mActivity.findViewById(org.societies.android.platform.cis.R.id.textview);
        resourceString = mActivity.getString(org.societies.android.platform.cis.R.string.hello);

	}

	/* (non-Javadoc)
	 * @see android.test.ActivityInstrumentationTestCase2#tearDown()
	 */
	@Override
	protected void tearDown() throws Exception {
		// TODO Auto-generated method stub
		super.tearDown();
	}
	public void testPreconditions() {
	      assertNotNull(mView);
	    }
	public void testText() {
	      assertEquals(resourceString,(String)mView.getText());
	    }
}
