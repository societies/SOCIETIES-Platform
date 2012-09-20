package org.societies.android.api.cis.management;

import android.os.Parcel;
import android.test.AndroidTestCase;

public class TestACriteria extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParcelable() throws Exception {
		ACriteria aCriteria = new ACriteria();
		
		aCriteria.setAttrib("location");
		aCriteria.setOperator("equals");
		aCriteria.setRank(1);
		aCriteria.setValue1("Paris");
		aCriteria.setValue2("Germany");

		
		assertEquals(0, aCriteria.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aCriteria.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACriteria createFromParcel = aCriteria.CREATOR.createFromParcel(parcel);
       
        assertEquals(aCriteria.getAttrib(), createFromParcel.getAttrib());
        assertEquals(aCriteria.getOperator(), createFromParcel.getOperator());
        assertEquals(aCriteria.getValue1(), createFromParcel.getValue1());
        assertEquals(aCriteria.getValue2(), createFromParcel.getValue2());
        assertEquals(aCriteria.getRank(), createFromParcel.getRank());

	}
}
