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
package org.societies.privacytrust.privacyprotection.dataobfuscation.obfuscator;

import java.lang.reflect.Type;

import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.ObfuscationLevelType;
import org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.wrapper.IDataWrapper;

/**
 * Abstract class helping the creation of an obfuscator
 *
 * @author Olivier Maridat (Trialog)
 *
 */
public abstract class DataObfuscator<E extends IDataWrapper> implements IDataObfuscator {
	/**
	 * Data to obfuscate
	 */
	protected E dataWrapper;
	/**
	 * Type of the obfuscation level
	 */
	protected ObfuscationLevelType obfuscationLevelType;
	/**
	 * For a DISCRETE obfuscation level type, there is a number
	 * of classes available. This step number is this number of
	 * classes
	 */
	protected int stepNumber = 1;
	/**
	 * Type of the data to obfuscate
	 */
	protected Type dataType;
	
	
	public DataObfuscator(E data) {
		super();
		this.dataWrapper = data;
	}

	
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator#getObfuscationType()
	 */
	@Override
	public ObfuscationLevelType getObfuscationLevelType() {
		return obfuscationLevelType;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator#getDataWrapper()
	 */
	public IDataWrapper getDataWrapper() {
		return dataWrapper;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator#getDataType()
	 */
	@Override
	public Type getDataType() {
		return dataType;
	}
	/* (non-Javadoc)
	 * @see org.societies.api.internal.privacytrust.privacyprotection.model.dataobfuscation.obfuscator.IDataObfuscator#getStepNumber()
	 */
	@Override
	public int getStepNumber() {
		return stepNumber;
	}
}
