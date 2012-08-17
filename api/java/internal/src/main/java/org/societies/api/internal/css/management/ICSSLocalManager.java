package org.societies.api.internal.css.management;

import java.util.concurrent.Future;
import java.util.List;


import org.societies.api.schema.cssmanagement.CssAdvertisementRecordDetailed;
import org.societies.api.schema.cssmanagement.CssInterfaceResult;
import org.societies.api.schema.cssmanagement.CssRecord;
import org.societies.api.schema.cssmanagement.CssRequest;

import org.societies.api.schema.css.directory.CssAdvertisementRecord;
import org.societies.api.schema.servicelifecycle.model.Service;

/**
 * 
 * This interface forms the basis of the CSSManager, a component that will exist
 * on all nodes. Its main tasks are:
 * 
 * 1. Allow a user to register or unregister a CSS 2. Allow a user to login into
 * a CSS 3. Allow a user to logout out of a CSS 3. Add and remove nodes that
 * make up a CSS 4. Get the current CSS profile 5. Allow the status of a device
 * to be changed 6. Synchronise profile data 7. Modify the CSS profile
 * 
 * This interface will be implemented for rich and cloud nodes. This interface
 * is used when calling the CSSManager on a different node.
 */
public interface ICSSLocalManager {

	/**
	 * Register with chosen Domain Server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> registerXMPPServer(CssRecord profile);

	/**
	 * Unregister with chosen Domain Server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> unregisterXMPPServer(CssRecord profile);

	/**
	 * Login with chosen Domain server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> loginXMPPServer(CssRecord profile);

	/**
	 * Logout from chosen Domain server
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> logoutXMPPServer(CssRecord profile);

	/**
	 * Login a user to a CSS.
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> loginCSS(CssRecord profile);

	/**
	 * Logout the user from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> logoutCSS(CssRecord profile);

	/**
	 * Register a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> registerCSS(CssRecord profile);

	/**
	 * Unregister the CSS TODO Is a CSS deleted or made inactive
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> unregisterCSS(CssRecord profile);

	/**
	 * Register a device(s) with a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> registerCSSNode(CssRecord profile);

	/**
	 * Unregister a device(s) from a CSS
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> unregisterCSSNode(CssRecord profile);

	/**
	 * Get the CSS Profile. This operation will retrieve the local CSS Profile.
	 * If none exists or the local cache is deemed to have expired this will
	 * result in a synchronisation with the cloud node.
	 * 
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> getCssRecord();

	/**
	 * Modify the CSS Profile
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> modifyCssRecord(CssRecord profile);

	/**
	 * Change the status a CSS device
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> changeCSSNodeStatus(CssRecord profile);

	/**
	 * Synchronise the CSS profile. The CSS cloud node's current profile is
	 * synchronised with the local device's cached version
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> synchProfile(CssRecord profile);

	/**
	 * Set the presence status of the user
	 * 
	 * @param profile
	 * @return Future<CssInterfaceResult>
	 */
	Future<CssInterfaceResult> setPresenceStatus(CssRecord profile);

	/**
	 * Maria - Temporary adding 5 functions below to showcase system
	 * functionality
	 */

	/**
	 * adds advertisement records to CssDirectory
	 * 
	 * @param record
	 */
	void addAdvertisementRecord(CssAdvertisementRecord record);

	/**
	 * deletes advertisement record from CssDirectory
	 * 
	 * @param record
	 */
	void deleteAdvertisementRecord(CssAdvertisementRecord record);

	/**
	 * updates advertisement records to CssDirectory
	 * 
	 * @param currentRecord
	 *            , newRecord
	 */
	void updateAdvertisementRecord(CssAdvertisementRecord currentRecord,
			CssAdvertisementRecord updatedRecord);

	/**
	 * retrusn all advertisement records in CssDirectory
	 * 
	 * @return Future<List<CssAdvertisementRecord>>
	 */
	Future<List<CssAdvertisementRecord>> findAllCssAdvertisementRecords();

	/**
	 * finds all services for advertised Css's via service discovery
	 * 
	 * @param listCssAds
	 * @return Future<List<Service>>
	 */
	Future<List<Service>> findAllCssServiceDetails(
			List<CssAdvertisementRecord> listCssAds);
	
	/* get list of css's and status that you have asked to join/jined */
	Future<List<CssRequest>> findAllCssFriendRequests();
	/* get lost of requests from people that have asked to join/joined your css */
	Future<List<CssRequest>> findAllCssRequests();
	/* Add update a reuest sent to join your css */
	void updateCssRequest(CssRequest request);
	/* send a request to remote css to join  */
	void sendCssFriendRequest(String friendCssId);
	/* Called by remote css is response t your request to join*/
	void updateCssFriendRequest(CssRequest request);
	
 Future<List<CssAdvertisementRecordDetailed>> getCssAdvertisementRecordsFull() ;
 
 
 /**
	 * Returns the current Node Type
	 * i.e. "CSS_CLOUD", "CSS_RICH" or "CSS_LIGHT"
	 * 
	 * @return Future<String>
	 */
 Future<String> getthisNodeType(String nodeId);
 
 public void setNodeType(CssRecord cssrecord, String nodeId, int nodestatus, int nodetype, String cssNodeMAC, String interactable);
 
 public void removeNode(CssRecord cssrecord, String nodeId);

}
