package org.societies.android.api.cis.management;

import org.societies.api.schema.cis.community.Criteria;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;


public class TestCriteria extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() throws Exception {
		Criteria criteria = new Criteria();
		
		criteria.setAttrib("location");
		criteria.setOperator("equals");
		criteria.setRank(1);
		criteria.setValue1("Paris");
		criteria.setValue2("Germany");
		
		assertEquals(0, criteria.describeContents());
		
        Parcel parcel = Parcel.obtain();
        criteria.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        Criteria createFromParcel = criteria.CREATOR.createFromParcel(parcel);
       
        assertEquals(criteria.getAttrib(), createFromParcel.getAttrib());
        assertEquals(criteria.getOperator(), createFromParcel.getOperator());
        assertEquals(criteria.getValue1(), createFromParcel.getValue1());
        assertEquals(criteria.getValue2(), createFromParcel.getValue2());
        assertEquals(criteria.getRank(), createFromParcel.getRank());
	}
}
