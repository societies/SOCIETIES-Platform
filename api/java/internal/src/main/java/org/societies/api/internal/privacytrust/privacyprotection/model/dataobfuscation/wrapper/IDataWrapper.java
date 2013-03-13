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
package org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper;

import org.societies.api.internal.schema.privacytrust.privacy.model.dataobfuscation.IObfuscable;
import org.societies.api.schema.identity.DataIdentifier;

/**
 * This data wrapper is an abstraction between obfuscation manager
 * and data models. This is the way for wrapping data to obfuscate them,
 * and filling a type of data (needed to know how obfuscate them) 
 * This wrapper is linked to a specific data obfuscator
 * and know what kind of data is needed to launch the obfuscation. 
 * @author Olivier Maridat (Trialog)
 * @date 18 oct. 2011
 */
public interface IDataWrapper<E extends IObfuscable> {
	/**
	 * @return Id of the data to be obfuscated
	 */
	public DataIdentifier getDataId();
	/**
	 * @param dataId Id of the data to be obfuscated
	 */
	public void setDataId(DataIdentifier dataId);
	
	/**
	 * Data
	 * @return The data to be obfuscated
	 */
	public E getData();
	/**
	 * Set the data to be obfuscated
	 * @param data The data to be obfuscated
	 */
	public void setData(E data);
	
	/**
	 * To know if obfuscated data will be stored with this obfuscator
	 * 
	 * @return True if this obfuscator has enabled persistence
	 * @return Otherwise false
	 */
	public boolean isPersistenceEnabled();
	/**
	 * To enable storage of obfuscated data
	 * @param persist True to persist the data, false otherwise
	 */
	public void setPersistenceEnabled(boolean persist);
	
	/**
	 * To know if this wrapper is ready for obfuscation operation
	 * 
	 * @return True if this DataWrapper is ready for obfuscation
	 * @return Otherwise false
	 */
	public boolean isReadyForObfuscation();
}
