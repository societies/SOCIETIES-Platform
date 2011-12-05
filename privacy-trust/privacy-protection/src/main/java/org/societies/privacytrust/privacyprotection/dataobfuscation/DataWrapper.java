/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske družbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVAÇÃO, SA (PTIN), IBM Corp., 
 * INSTITUT TELECOM (ITSUD), AMITEC DIACHYTI EFYIA PLIROFORIKI KAI EPIKINONIES ETERIA PERIORISMENIS EFTHINIS (AMITEC), TELECOM 
 * ITALIA S.p.a.(TI),  TRIALOG (TRIALOG), Stiftelsen SINTEF (SINTEF), NEC EUROPE LTD (NEC))
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following
 * conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following
 *    disclaimer in the documentation and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING,
 * BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT 
 * SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
 * DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package org.societies.privacytrust.privacyprotection.dataobfuscation;

import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.privacytrust.privacyprotection.mock.DataIdentifier;


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
	private DataIdentifier dataId;
	
	
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
	protected DataWrapper(DataIdentifier dataId) {
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
	public void enabledPersistence(DataIdentifier dataId) {
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
	public DataIdentifier getDataId() {
		return dataId;
	}
	/**
	 * @param dataId the dataId to set
	 */
	@Override
	public void setDataId(DataIdentifier dataId) {
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
