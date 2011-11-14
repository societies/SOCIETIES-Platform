package org.societies.context.model.api;

import java.util.HashSet;
import java.util.Set;

public abstract class CommunityMemberCtxEntity extends CtxEntity {
	
	private static final long serialVersionUID = 3614587369237968591L;
	
	public Set<CommunityCtxEntity> communities = new HashSet<CommunityCtxEntity>();

	CommunityMemberCtxEntity() {}

	public Set<CommunityCtxEntity> getCommunities(){
		return new HashSet<CommunityCtxEntity>(this.communities);
	}
	
	public void addCommunity(CommunityCtxEntity community) {
		this.communities.add(community);
	}
	
	public void removeCommunity(CommunityCtxEntity community) {
		this.communities.remove(community);
	}
}
