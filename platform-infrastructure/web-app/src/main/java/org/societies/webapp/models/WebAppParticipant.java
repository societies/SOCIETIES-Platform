package org.societies.webapp.models;

import org.societies.api.schema.cis.community.ParticipantRole;

public class WebAppParticipant {
	
	String membersJid;
	ParticipantRole membershipType;
	
	public String getMembersJid() {
		return membersJid;
	}
	public void setMembersJid(String membersJid) {
		this.membersJid = membersJid;
	}
	public ParticipantRole getMembershipType() {
		return membershipType;
	}
	public void setMembershipType(ParticipantRole membershipType) {
		this.membershipType = membershipType;
	}


}
