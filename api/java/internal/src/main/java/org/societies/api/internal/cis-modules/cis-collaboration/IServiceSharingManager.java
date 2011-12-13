/**
 * Allow a CSS to create service sharing records that describe how a service is shared in a CIS.
 * These records are then added to a CISRecord which is managed by the CISManager.
 * TODO: Need to pass the pointer to the CISManager. 
 * 
 * @author Babak Farshchian
 * @version 0
 */
package org.societies.cis.collaboration.api;

import org.societies.cis.management.api.ServiceSharingRecord;


public interface IServiceSharingManager {
	boolean addServiceSharingRecord(String CisId, ServiceSharingRecord sharingRecord);
}
