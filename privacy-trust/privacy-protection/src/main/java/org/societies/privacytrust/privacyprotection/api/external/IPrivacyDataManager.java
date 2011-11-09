package org.societies.privacytrust.privacyprotection.api.external;

import org.societies.privacytrust.privacyprotection.api.internal.IDataObfuscationManager;

/**
 * External interface to do actions when using a data.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:26
 */
public interface IPrivacyDataManager extends IDataObfuscationManager{

	/**
	 * <b>Check permission </b>to access/use/disclose a data for a<b> service
	 * usage</b>
	 * Example of use:
	 * <ul>
	 * 	<li>Context Broker, to get permissions to disclose context data.</li>
	 * </ul>
	 * <ul>
	 * 	<li>Preference Manager, to get permission to disclose user preference data.
	 * </li>
	 * 	<li>"Content Accessor", to get permissions to disclose a content data</li>
	 * </ul>
	 * 
	 * @param dataId    ID of the requested data.
	 * @param ownerId    the ID of the owner of the data. Here it is the CSS_Id of the
	 * CSS receiving the request.
	 * @param equestorId
	 * @param serviceId    The service_Id the service
	 */
	public Object checkPermission(Object dataId, Object ownerId, Object equestorId, Object serviceId);

	/**
	 * <b>Check permission </b>to access/use/disclose a data for a<b> CIS usage</b>
	 * Example of use:
	 * <ul>
	 * 	<li>Context Broker, to get permissions to disclose context data.</li>
	 * </ul>
	 * <ul>
	 * 	<li>Preference Manager, to get permission to disclose user preference data.
	 * </li>
	 * 	<li>"Content Accessor", to get permissions to disclose a content data</li>
	 * </ul>
	 * 
	 * @param dataId    ID of the requested data.
	 * @param ownerId    The ID of the owner of the data. Here it is the CSS_Id of the
	 * CSS receiving the request.
	 * @param requestorId    The ID of the requestor of the data. Here it is the
	 * CSS_Id  of the CSS which creates the CIS.
	 * @param cisId    the ID of the CIS which wants to access the data
	 */
	public Object checkPermission(Object dataId, Object ownerId, Object requestorId, String cisId);

	/**
	 * <b>Check permission </b>to access/use/disclose a data in a case that <b>no
	 * negotiation have been done</b>.
	 * Example of use:
	 * <ul>
	 * 	<li>Context Broker, to get permissions to disclose context data.</li>
	 * </ul>
	 * <ul>
	 * 	<li>Preference Manager, to get permission to disclose user preference data.
	 * </li>
	 * 	<li>"Content Accessor", to get permissions to disclose a content data</li>
	 * </ul>
	 * 
	 * @param dataId    ID of the requested data.
	 * @param ownerId    ID of the owner of the data. Here it is the CSS_Id of the CSS
	 * receiving the request.
	 * @param requestorId    ID of the requestor of the data. Here it is the CSS_Id
	 * of the CSS which request the data.
	 * @param usage    Information about the use of the data: purpose, retention-time,
	 * people who will access this data... Need to be formalised!
	 */
	public Object checkPermission(Object dataId, Object ownerId, String requestorId, String usage);
}