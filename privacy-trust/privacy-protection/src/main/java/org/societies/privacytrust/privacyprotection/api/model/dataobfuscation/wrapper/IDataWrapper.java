package org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper;


/**
 * This data wrapper is an abstraction between obfuscation manager
 * and data models. This is the way for wrapping data to obfuscate them,
 * and filling a type of data (needed to know how obfuscate them) 
 * This wrapper is linked to a specific data obfuscator
 * and know what kind of data is needed to launch the obfuscation. 
 * @author olivierm
 * @date 18 oct. 2011
 */
public interface IDataWrapper {
	/**
	 * To know if obfuscated data will be stored with this obfuscator
	 * 
	 * @return True if this obfuscator has enabled persistence
	 * @return Otherwise false
	 */
	public boolean isPersistenceEnabled();
	/**
	 * To enable storage of obfuscated data
	 * 
	 * @param dataId A unique ID of the data to obfuscate is needed to enable persistence
	 */
	public void enabledPersistence(String dataId);
	/**
	 * To disabled storage of obfuscated data
	 */
	public void disabledPersistence();
	/**
	 * To know if this wrapper is ready for obfuscation operation
	 * 
	 * @return True if this DataWrapper is ready for obfuscation
	 * @return Otherwise false
	 */
	public boolean isReadyForObfuscation();
	/**
	 * To set this wrapper as ready for obfuscation operation when all information have been filled
	 */
	void setAsReadyForObfuscation();
	/**
	 * To set this wrapper as not ready for obfuscation operation
	 */
	void setAsNotReadyForObfuscation();
	/**
	 * @return the dataId
	 */
	public String getDataId();
	/**
	 * @param dataId the dataId to set
	 */
	public void setDataId(String dataId);
}
