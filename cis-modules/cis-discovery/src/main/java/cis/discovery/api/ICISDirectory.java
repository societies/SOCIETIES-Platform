/**
 * Main responsibilities of this interface:
 * - Register and unregister CISs in form of CISAdvertisementRecords. (Note that CIS metadata
 * is not stored here. this is only yellow pages).
 * 
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package cis.discovery.api;


public interface ICISDirectory {
	/*
	 * Various search methods that return an array of CISAdvertisementRecords.
	 */
	CISAdvertisementRecord[] searchByName(String cisName);
	CISAdvertisementRecord[] searchByOwner(String ownerId);
	CISAdvertisementRecord[] searchByUri(String uri);
	
	Boolean RegisterCIS(CISAdvertisementRecord cis);
	Boolean UnregisterCIS(CISAdvertisementRecord cis);
	/*
	 * This method is used to add CIS Directories that reside on other nodes.
	 * 
	 * @param directoryURI URI for the directory to be added.
	 * @param cssId ID for the CSS where the new directory resides.
	 * @param synchMode One of several modes for synchronizing with the new directory. E.g. pull or push.
	 * 
	 */
	Integer AddPeerDirectory(String directoryURI, String cssId, Integer synchMode);
	
	/*
	 * Ping method for checking whether this Directory is alive.
	 */
	Boolean ping();
	/*
	 * A method that will return the current URI for this Directory. This URI might be fetched
	 * from XMPP name-space or be a web service.
	 */
	
	public String getURI();

}
