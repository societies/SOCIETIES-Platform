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
package org.societies.android.api.internal.privacytrust.model.dataobfuscation.obfuscator;

import java.lang.reflect.Type;

import org.societies.android.api.internal.privacytrust.model.dataobfuscation.ObfuscationLevelType;
import org.societies.android.api.internal.privacytrust.model.dataobfuscation.wrapper.IDataWrapper;
import org.societies.android.api.privacytrust.privacy.model.PrivacyException;


/**
 * This interface defines an obfuscator.
 * An Obfuscator represents an obfuscation algorithm,
 * and each type of data needs an obfuscation algorithm.
 * @author Olivier Maridat
 * @date 14 oct. 2011
 */
public interface IDataObfuscator {
	/**
	 * Protect data wrapped in the obfuscator to a correct obfuscation level.
	 * 
	 * @param obfuscationLevel Obfuscation level, a real number between 0 and 1.  With 0, there is no obfuscation
	 * @return Obfuscated data wrapped in a DataWrapper (of the same type that the one used to instanciate the obfuscator)
	 * @throws Exception
	 */
	public IDataWrapper obfuscateData(double obfuscationLevel) throws PrivacyException;

	/**
	 * To know if obfuscation of this type of data is available on this node or not
	 * @return true if the obfuscation can be done on this node, false otherwise
	 */
	public boolean isAvailable();
	/**
	 * Type of the obfuscation
	 * @return the type of the obfuscation
	 */
	public ObfuscationLevelType getObfuscationLevelType();
	/**
	 * Number of classes for a discrete obfuscation level
	 * @return the number of steps available
	 */
	int getStepNumber();
	/**
	 * Wrapper of the data to obfuscate
	 * @return the wrapped data to obfuscate
	 */
	public IDataWrapper getDataWrapper();
	/**
	 * Type of the data wrapper to obfuscate
	 * @return the type of the data wrapper to obfuscate
	 */
	public Type getDataType();
}
