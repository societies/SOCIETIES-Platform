package org.societies.privacytrust.trust.api.repo;

import java.util.Set;

import org.societies.privacytrust.trust.api.mock.EntityIdentifier;
import org.societies.privacytrust.trust.api.model.TrustedEntity;
import org.societies.privacytrust.trust.api.model.TrustedEntityId;

public interface ITrustRepository {

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
	 * @param cssId
	 */
	public void addUser(EntityIdentifier cssId);

	public void registerEntityEventListener();

	/**
	 * 
	 * @param entity
	 */
	public void removeEntity(TrustedEntity entity);

	/**
	 * 
	 * @param teid
	 */
	public void removeEntity(TrustedEntityId teid);

	/**
	 * 
	 * @param cisId
	 */
	public void retrieveCommunityTrustValue(EntityIdentifier cisId);

	/**
	 * 
	 * @param teid
	 * @param trustType
	 */
	public void retrieveEntity(TrustedEntityId teid, int trustType);

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
	 * @param entity
	 */
	public void updateEntity(TrustedEntity entity);
}