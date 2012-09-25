package org.societies.android.api.cis.directory;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.cis.management.ACommunity;
import org.societies.android.api.cis.management.ACriteria;
import org.societies.android.api.cis.management.AMembershipCrit;

import android.os.Parcel;
import android.test.AndroidTestCase;

public class TestACisAdvertisementRecord extends AndroidTestCase{
	protected void setUp() throws Exception {
		super.setUp();
		//after
	}

	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}

	public void testParcelable() throws Exception {
		ACisAdvertisementRecord aRec = new ACisAdvertisementRecord();
		aRec.setCssownerid("owner");
		aRec.setId("id");
		aRec.setName("name");
		aRec.setPassword("pwd");
		aRec.setType("type");
		
		assertEquals(0, aRec.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aRec.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACisAdvertisementRecord createFromParcel = ACisAdvertisementRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(aRec.getCssownerid(), createFromParcel.getCssownerid());
        assertEquals(aRec.getId(), createFromParcel.getId());
        assertEquals(aRec.getName(), createFromParcel.getName());
        assertEquals(aRec.getPassword(), createFromParcel.getPassword());
        assertEquals(aRec.getType(), createFromParcel.getType());

		
	}

	public void testParcelableWithCriteria() throws Exception {
		ACisAdvertisementRecord aRec = new ACisAdvertisementRecord();
		aRec.setCssownerid("owner");
		aRec.setId("id");
		aRec.setName("name");
		aRec.setPassword("pwd");
		aRec.setType("type");
		AMembershipCrit amembershipCrit = new AMembershipCrit();
		List<ACriteria> l = new ArrayList<ACriteria>();
		ACriteria a = new ACriteria();
		a.setAttrib("location");
		a.setOperator("equals");
		a.setRank(1);
		a.setValue1("Paris");
		l.add(a);
		amembershipCrit.setACriteria(l);
		aRec.setMembershipCrit(amembershipCrit);

		
		
		assertEquals(0, aRec.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aRec.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACisAdvertisementRecord createFromParcel = ACisAdvertisementRecord.CREATOR.createFromParcel(parcel);
       
        assertEquals(aRec.getCssownerid(), createFromParcel.getCssownerid());
        assertEquals(aRec.getId(), createFromParcel.getId());
        assertEquals(aRec.getName(), createFromParcel.getName());
        assertEquals(aRec.getPassword(), createFromParcel.getPassword());
        assertEquals(aRec.getType(), createFromParcel.getType());

        assertEquals(aRec.getMembershipCrit().getACriteria().size(), createFromParcel.getMembershipCrit().getACriteria().size());
        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getAttrib(), createFromParcel.getMembershipCrit().getACriteria().get(0).getAttrib());
        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getOperator(), createFromParcel.getMembershipCrit().getACriteria().get(0).getOperator());
        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getValue1(), createFromParcel.getMembershipCrit().getACriteria().get(0).getValue1());
        assertEquals(aRec.getMembershipCrit().getACriteria().get(0).getRank(), createFromParcel.getMembershipCrit().getACriteria().get(0).getRank());

        
        
	}


	
}
