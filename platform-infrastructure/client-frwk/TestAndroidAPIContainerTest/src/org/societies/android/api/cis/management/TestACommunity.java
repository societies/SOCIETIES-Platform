package org.societies.android.api.cis.management;

import java.util.ArrayList;
import java.util.List;

import org.societies.android.api.internal.cssmanager.AndroidCSSNode;
import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import org.societies.api.schema.cis.manager.CommunityManager;
import org.societies.api.schema.cis.manager.Create;

import android.os.Parcel;
import android.test.AndroidTestCase;

public class TestACommunity extends AndroidTestCase{
	protected void setUp() throws Exception {
		super.setUp();
		//after
	}

	protected void tearDown() throws Exception {
		//before
		super.tearDown();
	}

	public void testParcelable() throws Exception {
		ACommunity aCommunity = new ACommunity();
		aCommunity.setCommunityName("name");
		aCommunity.setDescription("desc");
		aCommunity.setCommunityType("type");
		aCommunity.setCommunityJid("jid");
		aCommunity.setOwnerJid("ownerJid");
		
		assertEquals(0, aCommunity.describeContents());
		
        Parcel parcel = Parcel.obtain();
        aCommunity.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACommunity createFromParcel = ACommunity.CREATOR.createFromParcel(parcel);
       
        assertEquals(aCommunity.getCommunityName(), createFromParcel.getCommunityName());
        assertEquals(aCommunity.getDescription(), createFromParcel.getDescription());
        assertEquals(aCommunity.getCommunityType(), createFromParcel.getCommunityType());
        assertEquals(aCommunity.getCommunityJid(), createFromParcel.getCommunityJid());
        assertEquals(aCommunity.getOwnerJid(), createFromParcel.getOwnerJid());

		
	}
	
	public void testParcelableWithCrit() throws Exception {
		ACommunity aCommunity = new ACommunity();
		aCommunity.setCommunityName("name");
		aCommunity.setDescription("desc");
		aCommunity.setCommunityType("type");
		aCommunity.setCommunityJid("jid");
		aCommunity.setOwnerJid("ownerJid");
		AMembershipCrit amembershipCrit = new AMembershipCrit();
		List<ACriteria> l = new ArrayList<ACriteria>();
		ACriteria a = new ACriteria();
		a.setAttrib("location");
		a.setOperator("equals");
		a.setRank(1);
		a.setValue1("Paris");
		l.add(a);
		amembershipCrit.setACriteria(l);
		aCommunity.setMembershipCrit(amembershipCrit);

		
        Parcel parcel = Parcel.obtain();
        aCommunity.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        ACommunity createFromParcel = ACommunity.CREATOR.createFromParcel(parcel);
       
        assertEquals(aCommunity.getCommunityName(), createFromParcel.getCommunityName());
        assertEquals(aCommunity.getDescription(), createFromParcel.getDescription());
        assertEquals(aCommunity.getCommunityType(), createFromParcel.getCommunityType());
        assertEquals(aCommunity.getCommunityJid(), createFromParcel.getCommunityJid());
        assertEquals(aCommunity.getOwnerJid(), createFromParcel.getOwnerJid());

        assertEquals(aCommunity.getMembershipCrit().getACriteria().size(), createFromParcel.getMembershipCrit().getACriteria().size());
        assertEquals(aCommunity.getMembershipCrit().getACriteria().get(0).getAttrib(), createFromParcel.getMembershipCrit().getACriteria().get(0).getAttrib());
        assertEquals(aCommunity.getMembershipCrit().getACriteria().get(0).getOperator(), createFromParcel.getMembershipCrit().getACriteria().get(0).getOperator());
        assertEquals(aCommunity.getMembershipCrit().getACriteria().get(0).getValue1(), createFromParcel.getMembershipCrit().getACriteria().get(0).getValue1());
        assertEquals(aCommunity.getMembershipCrit().getACriteria().get(0).getRank(), createFromParcel.getMembershipCrit().getACriteria().get(0).getRank());

        
        
		
		
	}

}
