package org.societies.context.broker.api;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import org.societies.context.mock.spm.identity.EntityIdentifier;
import org.societies.context.model.api.CtxAttributeIdentifier;
import org.societies.context.model.api.CtxAttributeValueType;
import org.societies.context.model.api.CtxEntityIdentifier;
import org.societies.context.model.api.CtxIdentifier;
import org.societies.context.model.api.CtxModelObject;
import org.societies.context.model.api.CtxModelType;

public interface IUserCtxBroker {
	
	/**
	 * Creates a CtxAssociation
	 * 
	 * @param requester
	 * @param type
	 * @param callback
	 */
	public void createAssociation(EntityIdentifier requester, String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxAttribute
	 * 
	 * @param requester
	 * @param scope
	 * @param enum
	 * @param type
	 * @param callback
	 */
	public void createAttribute(EntityIdentifier requester, CtxEntityIdentifier scope, CtxAttributeValueType valueType, String type, IUserCtxBrokerCallback callback);

	/**
	 * Creates a CtxEntity
	 * 
	 * @param requester
	 * @param type
	 * @param callback
	 */
	public void createEntity(EntityIdentifier requester, String type, IUserCtxBrokerCallback callback);

	/**
	 * There are several methods missing that would express the similarity of context
	 * values or objects in a quantifiable form (and not via a sorted list of
	 * most/least similar reference objects/values).
	 * 
	 * @param objectUnderComparison
	 * @param referenceObjects
	 * @param callback
	 */
	public void evaluateSimilarity(Serializable objectUnderComparison, List<Serializable> referenceObjects, IUserCtxBrokerCallback callback);

	/**
	 * Looks up for a list of CtxModelObjects defined by the CtxModelType (CtxEntity,
	 * CtxAttribute, CtxAssociation) of  the specified type.
	 * 
	 * @param requester
	 * @param modelType
	 * @param type
	 * @param callback
	 */
	public void lookup(EntityIdentifier requester, CtxModelType modelType, String type, IUserCtxBrokerCallback callback);

	/**
	 * Looks up for a list of CtxEntities of  the specified type, containing the
	 * specified attributes
	 * 
	 * @param requester
	 * @param entityType
	 * @param attribType
	 * @param minAttribValue
	 * @param maxAttribValue
	 * @param callback
	 */
	public void lookupEntities(EntityIdentifier requester, String entityType, String attribType, Serializable minAttribValue, Serializable maxAttribValue, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of context
	 * attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attrType
	 * @param callback
	 */
	public void registerForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attrType, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param callback
	 */
	public void registerForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Removes the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @param callback
	 */
	public void remove(EntityIdentifier requester, CtxIdentifier identifier, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves the specified context model object.
	 * 
	 * @param requester
	 * @param identifier
	 * @param callback
	 */
	public void retrieve(EntityIdentifier requester, CtxIdentifier identifier, IUserCtxBrokerCallback callback);

	/**
	 * Predicts a future context attribute for the specified time.
	 * 
	 * @param requester
	 * @param attrId
	 * @param date
	 * @param callback
	 */
	public void retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date date, IUserCtxBrokerCallback callback);

	/**
	 * Predicts the identified by the modification index  future context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrieveFuture(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified modificationIndex.
	 * 
	 * @param requester
	 * @param attrId
	 * @param modificationIndex
	 * @param callback
	 */
	public void retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, int modificationIndex, IUserCtxBrokerCallback callback);

	/**
	 * Retrieves context attributes stored in the Context History Log based on the
	 * specified date and time information.
	 * 
	 * @param requester
	 * @param attrId
	 * @param startDate
	 * @param endDate
	 * @param callback
	 */
	public void retrievePast(EntityIdentifier requester, CtxAttributeIdentifier attrId, Date startDate, Date endDate, IUserCtxBrokerCallback callback);

	/**
	 * Registers the specified EventListener for value modification events of the
	 * specified context attribute.
	 * 
	 * @param requester
	 * @param attrId
	 * @param callback
	 */
	public void unregisterForUpdates(EntityIdentifier requester, CtxAttributeIdentifier attrId, IUserCtxBrokerCallback callback);

	/**
	 * Unregisters the specified EventListener for value modification events of
	 * context attribute(s) with the supplied scope and type.
	 * 
	 * @param requester
	 * @param scope
	 * @param attributeType
	 * @param callback
	 */
	public void unregisterForUpdates(EntityIdentifier requester, CtxEntityIdentifier scope, String attributeType, IUserCtxBrokerCallback callback);

	/**
	 * Updates a single context model object.
	 * 
	 * @param requester
	 * @param object
	 * @param callback
	 */
	public void update(EntityIdentifier requester, CtxModelObject object, IUserCtxBrokerCallback callback);
}