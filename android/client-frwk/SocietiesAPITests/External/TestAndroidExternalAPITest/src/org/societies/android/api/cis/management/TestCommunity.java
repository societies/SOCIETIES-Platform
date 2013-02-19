package org.societies.android.api.cis.management;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.schema.cis.community.Community;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;

import android.os.Parcel;
import android.test.AndroidTestCase;
import android.test.suitebuilder.annotation.MediumTest;

public class TestCommunity extends AndroidTestCase{
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
		Community community = new Community();
		community.setCommunityName("name");
		community.setDescription("desc");
		community.setCommunityType("type");
		community.setCommunityJid("jid");
		community.setOwnerJid("ownerJid");

		
		assertEquals(0, community.describeContents());
		
        Parcel parcel = Parcel.obtain();
        community.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        Community createFromParcel = Community.CREATOR.createFromParcel(parcel);
       
        assertEquals(community.getCommunityName(), createFromParcel.getCommunityName());
        assertEquals(community.getDescription(), createFromParcel.getDescription());
        assertEquals(community.getCommunityType(), createFromParcel.getCommunityType());
        assertEquals(community.getCommunityJid(), createFromParcel.getCommunityJid());
        assertEquals(community.getOwnerJid(), createFromParcel.getOwnerJid());	
	}
	
	@MediumTest
	public void testParcelableWithEmptyCrit() throws Exception {
		Community community = new Community();
		community.setCommunityName("name");
		community.setDescription("desc");
		community.setCommunityType("type");
		community.setCommunityJid("jid");
		community.setOwnerJid("ownerJid");

		MembershipCrit amembershipCrit = new MembershipCrit();
		List<Criteria> l = new ArrayList<Criteria>();
		amembershipCrit.setCriteria(l);
		community.setMembershipCrit(amembershipCrit);
		
        Parcel parcel = Parcel.obtain();
        community.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        Community createFromParcel = Community.CREATOR.createFromParcel(parcel);
       
        assertEquals(community.getCommunityName(), createFromParcel.getCommunityName());
        assertEquals(community.getDescription(), createFromParcel.getDescription());
        assertEquals(community.getCommunityType(), createFromParcel.getCommunityType());
        assertEquals(community.getCommunityJid(), createFromParcel.getCommunityJid());
        assertEquals(community.getOwnerJid(), createFromParcel.getOwnerJid());

        assertEquals(community.getMembershipCrit().getCriteria().size(), createFromParcel.getMembershipCrit().getCriteria().size());
        assertEquals(0, createFromParcel.getMembershipCrit().getCriteria().size());		
	}
	
	
	@MediumTest
	public void testParcelableWithCrit() throws Exception {
		Community community = new Community();
		community.setCommunityName("name");
		community.setDescription("desc");
		community.setCommunityType("type");
		community.setCommunityJid("jid");
		community.setOwnerJid("ownerJid");

		MembershipCrit amembershipCrit = new MembershipCrit();
		List<Criteria> l = new ArrayList<Criteria>();
		Criteria a = new Criteria();
		a.setAttrib("location");
		a.setOperator("equals");
		a.setRank(1);
		a.setValue1("Paris");
		l.add(a);
		amembershipCrit.setCriteria(l);
		community.setMembershipCrit(amembershipCrit);

        Parcel parcel = Parcel.obtain();
        community.writeToParcel(parcel, 0);
        //done writing, now reset parcel for reading
        parcel.setDataPosition(0);
        //finish round trip
        Community createFromParcel = Community.CREATOR.createFromParcel(parcel);
       
        assertEquals(community.getCommunityName(), createFromParcel.getCommunityName());
        assertEquals(community.getDescription(), createFromParcel.getDescription());
        assertEquals(community.getCommunityType(), createFromParcel.getCommunityType());
        assertEquals(community.getCommunityJid(), createFromParcel.getCommunityJid());
        assertEquals(community.getOwnerJid(), createFromParcel.getOwnerJid());

        assertEquals(community.getMembershipCrit().getCriteria().size(), createFromParcel.getMembershipCrit().getCriteria().size());
        assertEquals(community.getMembershipCrit().getCriteria().get(0).getAttrib(), createFromParcel.getMembershipCrit().getCriteria().get(0).getAttrib());
        assertEquals(community.getMembershipCrit().getCriteria().get(0).getOperator(), createFromParcel.getMembershipCrit().getCriteria().get(0).getOperator());
        assertEquals(community.getMembershipCrit().getCriteria().get(0).getValue1(), createFromParcel.getMembershipCrit().getCriteria().get(0).getValue1());
        assertEquals(community.getMembershipCrit().getCriteria().get(0).getRank(), createFromParcel.getMembershipCrit().getCriteria().get(0).getRank());		
	}

}
