package org.societies.android.api.cis.management;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.test.AndroidTestCase;

public class TestMembershipCrit extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	public void testParcelable() throws Exception {
		AMembershipCrit amembershipCrit = new AMembershipCrit();
		
		List<ACriteria> l = new ArrayList<ACriteria>();
		
		ACriteria a = new ACriteria();
		
		a.setAttrib("location");
		a.setOperator("equals");
		a.setRank(1);
		a.setValue1("Paris");
		l.add(a);
		amembershipCrit.setACriteria(l);
		
		assertEquals(0, amembershipCrit.describeContents());
		
        Parcel parcel = Parcel.obtain();
        amembershipCrit.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        AMembershipCrit createFromParcel = AMembershipCrit.CREATOR.createFromParcel(parcel);
       
        assertEquals(amembershipCrit.getACriteria().size(), createFromParcel.getACriteria().size());
        assertEquals(amembershipCrit.getACriteria().get(0).getAttrib(), createFromParcel.getACriteria().get(0).getAttrib());
        assertEquals(amembershipCrit.getACriteria().get(0).getOperator(), createFromParcel.getACriteria().get(0).getOperator());
        assertEquals(amembershipCrit.getACriteria().get(0).getValue1(), createFromParcel.getACriteria().get(0).getValue1());
        assertEquals(amembershipCrit.getACriteria().get(0).getValue2(), createFromParcel.getACriteria().get(0).getValue2());
        assertEquals(amembershipCrit.getACriteria().get(0).getRank(), createFromParcel.getACriteria().get(0).getRank());
	}
}
