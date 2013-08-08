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
package org.societies.api.privacytrust.trust.model.util;

import java.text.NumberFormat;

/**
 * This class provides utility methods in order to format trust values as
 * user-friendly Strings.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.2
 */
public class TrustValueFormat {

	/**
	 * Formats the specified trust value as a percentage. With this formatter,
	 * a decimal fraction such as 0.75 is displayed as <code>"75%"</code>. The 
	 * method returns <code>"-"</code> if the specified trust value is 
	 * <code>null</code>.
	 * 
	 * @param trustValue
	 *            the trust value to format as a percentage.
	 * @return a String representing the specified trust value as a percentage.
	 * @throws IllegalArgumentException if the specified value is an out of 
	 *         range Double, i.e. not within [0,1].
	 */
	public static String formatPercent(Double trustValue) {
		
		if (trustValue == null)
			return "-";
		
		if (trustValue < 0d || trustValue > 1d)
			throw new IllegalArgumentException("trustValue is out of range [0,1]");
		
		final NumberFormat percentFormatter = NumberFormat.getPercentInstance();
		return percentFormatter.format(trustValue);
	}
	
	/**
	 * Formats the specified trust value as a fraction of 100. With this 
	 * formatter, a decimal fraction such as 0.75 is displayed as 
	 * <code>"75/100"</code>. The method returns <code>"-"</code> if the 
	 * specified trust value is <code>null</code>.
	 * 
	 * @param trustValue
	 *            the trust value to format as a fraction of 100.
	 * @return a String representing the specified trust value as a fraction of
	 *         100.
	 * @throws IllegalArgumentException if the specified value is an out of 
	 *         range Double, i.e. not within [0,1].
	 */
	public static String formatFraction(Double trustValue) {
		
		if (trustValue == null)
			return "-";
		
		if (trustValue < 0d || trustValue > 1d)
			throw new IllegalArgumentException("trustValue is out of range [0,1]");
		
		return new String(Math.round(100 * trustValue) + "/100");
	}
}