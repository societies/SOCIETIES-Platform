package org.societies.context.model.api;

import java.util.HashSet;
import java.util.Set;

public class CommunityCtxEntity extends CommunityMemberCtxEntity {
	
	private static final long serialVersionUID = -8564823052068362334L;
	
	public Set<CommunityMemberCtxEntity> members = new HashSet<CommunityMemberCtxEntity>();

	private CommunityCtxEntity() {}

	public Set<CommunityMemberCtxEntity> getMembers() {
		return new HashSet<CommunityMemberCtxEntity>(this.members);
	}
	
	public void addMember(CommunityMemberCtxEntity member) {
		this.members.add(member);
	}

	public void removeMember(CommunityMemberCtxEntity member) {
		this.members.remove(member);
	}
}