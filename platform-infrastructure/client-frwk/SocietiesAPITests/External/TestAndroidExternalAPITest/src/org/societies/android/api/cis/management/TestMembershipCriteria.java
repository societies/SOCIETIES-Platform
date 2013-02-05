package org.societies.android.api.cis.management;

import java.util.ArrayList;
import java.util.List;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;

public class TestMembershipCriteria extends AndroidTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}

	@MediumTest
	public void testParcelable() throws Exception {
		MembershipCrit amembershipCrit = new MembershipCrit();
		List<Criteria> criteriaList = new ArrayList<Criteria>();
		Criteria criteria = new Criteria();
		criteria.setAttrib("location");
		criteria.setOperator("equals");
		criteria.setRank(1);
		criteria.setValue1("Paris");
		criteriaList.add(criteria);
		amembershipCrit.setCriteria(criteriaList);
		


		assertEquals(0, amembershipCrit.describeContents());
        Parcel parcel = Parcel.obtain();
        amembershipCrit.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
		MembershipCrit createFromParcel = MembershipCrit.CREATOR.createFromParcel(parcel);
        assertEquals(amembershipCrit.getCriteria().size(), createFromParcel.getCriteria().size());
        assertEquals(amembershipCrit.getCriteria().get(0).getAttrib(), createFromParcel.getCriteria().get(0).getAttrib());
        assertEquals(amembershipCrit.getCriteria().get(0).getOperator(), createFromParcel.getCriteria().get(0).getOperator());
        assertEquals(amembershipCrit.getCriteria().get(0).getValue1(), createFromParcel.getCriteria().get(0).getValue1());
        assertEquals(amembershipCrit.getCriteria().get(0).getValue2(), createFromParcel.getCriteria().get(0).getValue2());
        assertEquals(amembershipCrit.getCriteria().get(0).getRank(), createFromParcel.getCriteria().get(0).getRank());
	}
	
	@MediumTest
	public void testEmptyMembershipParcelable() throws Exception {
//		AMembershipCrit amembershipCrit = new AMembershipCrit();
		
		List<Criteria> l = new ArrayList<Criteria>();
		
//		amembershipCrit.setACriteria(l);
//		
//		assertEquals(0, amembershipCrit.describeContents());
		
        Parcel parcel = Parcel.obtain();
//        amembershipCrit.writeToParcel(parcel, 0);
//        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
//        AMembershipCrit createFromParcel = AMembershipCrit.CREATOR.createFromParcel(parcel);
//       
//        assertEquals(amembershipCrit.getACriteria().size(), createFromParcel.getACriteria().size());
//        assertEquals(0, createFromParcel.getACriteria().size());
	}
	
}
