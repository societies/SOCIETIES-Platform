/**
 * Is in charge of managing meta data about CISs.
 * 
 * @author Babak Farshchian
 * @version 0
 */
package cis.management.api;

import cis.management.impl.*;

public interface ICISManager {
	//
	//
	/**
	 * Create a new CIS for the CSS represented by cssId.
	 * TODO: change the type from String to proper type when CSS ID datatype is defined.
	 *  
	 * @param cssId
	 * @return
	 */
	CISRecord createCis(String cssId);
	/**
	 * Delete a specific CIS represented by cisId
	 * TODO: Need to give a more meaningful return.
	 * 
	 * @param cssId the ID of the owner CSS
	 * @param cisId The ID of the CIS to be deleted.
	 * @return true if deleted, false otherwise. 
	 */
	Boolean deleteCis(String cssId, String cisId);
	/**
	 * Updates an existing CIS with the data in the newCis. Update is done canonical. If it fails, the old CIS is
	 * not changed at all.
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param newCis the data to be updated is specified in this CISRecord.
	 * @param oldCisId The ID of the CIS that needs to be updated.
	 * @return true if update was successful, 
	 */
	Boolean updateCis(String cssId, CISRecord newCis, String oldCisId);
	
	/**
	 * Get a CIS Record with the ID cisId.
	 * 
	 * TODO: Check the return value. Should be something more meaningful.
	 * 
	 * @param cisId The ID of the CIS to get.
	 * @return the CISRecord with the ID cisID, or null if no such CIS exists.
	 */
	CISRecord getCis(String cssId, String cisId);
	
	/**
	 * Return an array of all the CISs that match the query. 
	 * 
	 * TODO: need to refine this to something better. I am not sure how the query will be created.
	 * 
	 * @param cssId The ID of the owner CSS
	 * @param query Defines what to search for.
	 * @return Array of CIS Records that match the query.
	 */
	CISRecord[] getCisList(CISRecord query);
	
	/**
	 * Returns the CISActivityFeed for a specific CIS.
	 * 
	 * @param cssId The ID of the owner CSS.
	 * @param cisId The ID of the CIS.
	 * @return The CISActivityFeed of the CIS.
	 */
	CISActivityFeed getActivityFeed(String cssId, String cisId);

}
