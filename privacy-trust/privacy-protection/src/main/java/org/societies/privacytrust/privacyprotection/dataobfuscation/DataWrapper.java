package org.societies.privacytrust.privacyprotection.dataobfuscation;

import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.privacytrust.privacyprotection.api.model.dataobfuscation.wrapper.IDataWrapper;


/**
 * This data wrapper is an abstraction between obfuscation manager
 * and data models. This is the way for wrapping data to obfuscate them,
 * and filling a type of data (needed to know how obfuscate them) 
 * This wrapper is linked to a specific data obfuscator
 * and know what kind of data is needed to launch the obfuscation. 
 * @state skeleton 
 * @author olivierm
 * @date 14 oct. 2011
 */
public class DataWrapper implements IDataWrapper {
	/**
	 * Persistence configuration
	 * Disabled by default 
	 */
	public boolean persistence = false;
	/**
	 * To know if this wrapper is ready to manage obfuscation
	 * Disabled by default 
	 */
	public boolean readyForObfuscation = false;
	/**
	 * Obfuscator to obfuscate this data
	 */
	private IDataObfuscator obfuscator;
	/**
	 * ID of the data, useful for persistence
	 */
	private String dataId;
	
	
	// -- CONSTRUCTOR
	/**
	 * Classical constructor
	 * The persistence is disabled by default, the obfuscated data will not
	 * be stored after obfuscation.
	 */
	protected DataWrapper() {
	}
	/**
	 * Persistence constructor
	 * By using this constructor, the persistence will be enabled automatically,
	 * and the unique ID of the data to obfuscate will be used to retrieve obfuscated version of the data.  
	 * @param dataId A unique ID of the data to obfuscate is needed to enable persistence
	 */
	protected DataWrapper(String dataId) {
		this.dataId = dataId;
		persistence = true;
	}
	
	
	// --- GET/SET
	/**
	 * To know if obfuscated data will be stored with this obfuscator
	 * @return True if this obfuscator has enabled persistence
	 * @return Otherwise false
	 */
	@Override
	public boolean isPersistenceEnabled() { return persistence; }
	/**
	 * To enable storage of obfuscated data
	 * @param dataId A unique ID of the data to obfuscate is needed to enable persistence
	 */
	@Override
	public void enabledPersistence(String dataId) {
		this.dataId = dataId;
		persistence = true;
	}
	/**
	 * To disabled storage of obfuscated data
	 */
	@Override
	public void disabledPersistence() { persistence = false; }
	
	/**
	 * To know if this wrapper is ready for obfuscation operation
	 * @return True if this DataWrapper is ready for obfuscation
	 * @return Otherwise false
	 */
	@Override
	public boolean isReadyForObfuscation() { return readyForObfuscation; }
	@Override
	public void setAsReadyForObfuscation() { readyForObfuscation = true; }
	@Override
	public void setAsNotReadyForObfuscation() { readyForObfuscation = false; }
	public void setReadyForObfuscation(boolean readyForObfuscation) { this.readyForObfuscation = readyForObfuscation; }
	
	/**
	 * @return the dataId
	 */
	@Override
	public String getDataId() {
		return dataId;
	}
	/**
	 * @param dataId the dataId to set
	 */
	@Override
	public void setDataId(String dataId) {
		this.dataId = dataId;
	}
	
	/**
	 * @return the obfuscator
	 */
	protected IDataObfuscator getObfuscator() {
		return obfuscator;
	}
	/**
	 * @param obfuscator the obfuscator to set
	 */
	protected void setObfuscator(IDataObfuscator obfuscator) {
		this.obfuscator = obfuscator;
	}
}
