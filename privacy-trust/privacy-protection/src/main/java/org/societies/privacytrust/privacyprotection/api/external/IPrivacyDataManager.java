package org.societies.privacytrust.privacyprotection.api.external;

import org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.model.privacyPolicy.ResponseItem;

/**
 * External interface to do actions when using a data.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:26
 */
public interface IPrivacyDataManager extends IDataObfuscationManager{
	/**
	 * Check permission to access/use/disclose a data for a service usage
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data.
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId the ID of the owner of the data. Here this is the CSS_Id of the CSS receiving the request.
	 * @param requestorId The ID of the requestor of the data. Here it is the CSS_Id  of the CSS which creates the Service.
	 * @param serviceId The service_Id the service
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(Object dataId, Object ownerId, Object requestorId, Object serviceId);

	/**
	 * Check permission to access/use/disclose a data for a CIS usage
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId The ID of the owner of the data. Here it is the CSS_Id of the CSS receiving the request.
	 * @param requestorId The ID of the requestor of the data. Here it is the CSS_Id  of the CSS which creates the CIS.
	 * @param cisId the ID of the CIS which wants to access the data
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(Object dataId, Object ownerId, Object requestorId, String cisId);

	/**
	 * Check permission to access/use/disclose a data in a case that no negotiation have been done.
	 * Example of use:
	 * - Context Broker, to get permissions to disclose context data.
	 * - Preference Manager, to get permission to disclose user preference data.
	 * - "Content Accessor", to get permissions to disclose a content data.
	 * 
	 * @param dataId ID of the requested data.
	 * @param ownerId ID of the owner of the data. Here it is the CSS_Id of the CSS receiving the request.
	 * @param requestorId ID of the requestor of the data. Here it is the CSS_Id of the CSS which request the data.
	 * @param usage Information about the use of the data: purpose, retention-time, people who will access this data... Need to be formalised!
	 * @return A ResponseItem with permission information in it
	 */
	public ResponseItem checkPermission(Object dataId, Object ownerId, String requestorId, RequestPolicy usage);
}