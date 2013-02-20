package org.societies.android.api.cis.directory;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.directory.CisAdvertisementRecord;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestCisAdvertisementRecord extends AndroidTestCase{
	protected void setUp() throws Exception {
		super.setUp();
		//after
	}

	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}
	@MediumTest
	public void testParcelable() throws Exception {
		CisAdvertisementRecord rec = new CisAdvertisementRecord();
		rec.setCssownerid("owner");
		rec.setId("id");
		rec.setName("name");
		rec.setType("type");
		
		assertEquals(0, rec.describeContents());
		
        Parcel parcel = Parcel.obtain();
        rec.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        CisAdvertisementRecord createFromParcel = CisAdvertisementRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(rec.getCssownerid(), createFromParcel.getCssownerid());
        assertEquals(rec.getId(), createFromParcel.getId());
        assertEquals(rec.getName(), createFromParcel.getName());
        assertEquals(rec.getType(), createFromParcel.getType());

		
	}

	@MediumTest
	public void testParcelableWithCriteria() throws Exception {
		CisAdvertisementRecord aRec = new CisAdvertisementRecord();
		aRec.setCssownerid("owner");
		aRec.setId("id");
		aRec.setName("name");
		aRec.setType("type");
//		AMembershipCrit amembershipCrit = new AMembershipCrit();
		List<Criteria> l = new ArrayList<Criteria>();
		Criteria a = new Criteria();
		a.setAttrib("location");
		a.setOperator("equals");
		a.setRank(1);
		a.setValue1("Paris");
		l.add(a);
//		amembershipCrit.setACriteria(l);
//		aRec.setMembershipCrit(amembershipCrit);
		
		assertEquals(0, aRec.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aRec.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        CisAdvertisementRecord createFromParcel = CisAdvertisementRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(aRec.getCssownerid(), createFromParcel.getCssownerid());
        assertEquals(aRec.getId(), createFromParcel.getId());
        assertEquals(aRec.getName(), createFromParcel.getName());
        assertEquals(aRec.getType(), createFromParcel.getType());

//        assertEquals(aRec.getMembershipCrit().getACriteria().size(), createFromParcel.getMembershipCrit().getACriteria().size());
//        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getAttrib(), createFromParcel.getMembershipCrit().getACriteria().get(0).getAttrib());
//        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getOperator(), createFromParcel.getMembershipCrit().getACriteria().get(0).getOperator());
//        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getValue1(), createFromParcel.getMembershipCrit().getACriteria().get(0).getValue1());
//        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getRank(), createFromParcel.getMembershipCrit().getACriteria().get(0).getRank());
        
	}
	
}
