package org.societies.privacytrust.trust.api;

import java.util.Set;

import org.societies.privacytrust.trust.api.mock.EntityIdentifier;

/**
 * @author nlia
 */
public interface ITrustMgr {

	/**
	 * 
	 * @param userId
	 */
	public void addUser(EntityIdentifier userId);
	
	/**
	 * 
	 * @param cisId
	 * @param members
	 */
	public void addCommunity(EntityIdentifier cisId, Set<EntityIdentifier> members);

	/**
	 * 
	 * @param cisId
	 */
	public void addCommunity(EntityIdentifier cisId);

	/**
	 * 
	 * @param developerId
	 */
	public void addDeveloper(EntityIdentifier developerId);

	/**
	 * 
	 * @param serviceId
	 * @param serviceType
	 * @param provider
	 */
	public void addService(EntityIdentifier serviceId, String serviceType, EntityIdentifier provider);

	/**
	 * 
	 * @param listener
	 * @param entityId
	 */
	public void registerTrustEventListener(TrustEventListener listener, EntityIdentifier entityId);

	/**
	 * 
	 * @param cisId
	 */
	public void retrieveCommunityTrustValue(EntityIdentifier cisId);

	/**
	 * 
	 * @param serviceId
	 */
	public void retrieveServiceTrustValue(EntityIdentifier serviceId);

	/**
	 * 
	 * @param cssId
	 */
	public void retrieveUserTrustValue(EntityIdentifier cssId);
	
	/**
	 * 
	 * @param userId
	 */
	public void removeUser(EntityIdentifier userId);

	/**
	 * 
	 * @param cisId
	 */
	public void removeCommunity(EntityIdentifier cisId);

	/**
	 * 
	 * @param developerId
	 */
	public void removeDeveloper(EntityIdentifier developerId);

	/**
	 * 
	 * @param serviceId
	 * @param serviceType
	 * @param provider
	 */
	public void removeService(EntityIdentifier serviceId);
}