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
package org.societies.privacytrust.privacyprotection.assessment.logic;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Estimation of correlation between two events (data access and data transmission) based on time
 * difference between the events.
 * 
 * The function itself is sigmoidal, shifted right (x axis), shifted up and scaled (y axis).
 * For negative x values it is always zero.
 * This results in a correlation that: <br/>
 * - is 0 if data transmission occurred before data access <br/>
 * - is 1 if data transmission occurred on same time as data access <br/>
 * - at first decreases only slightly (to account for possible data processing before transmission) <br/>
 * - then decreases faster (for data transmissions that occur much later) <br/>
 * - then asymptotically approaches a value greater than zero (data can be accumulated and sent much later) <br/>
 *
 * @author Mitja Vardjan
 *
 */
public class CorrelationInTime {

	private static Logger LOG = LoggerFactory.getLogger(CorrelationInTime.class);

	/** Asymptote */
	private final double VALUE_AT_INF_DEFAULT = 0.2;
	
	/** Default time shift in ms */
	private final long TIME_SHIFT_DEFAULT = 3000;
	
	private double valueAtInf;

	private long timeShift;

	private double normalizationFactor;
	private double normalizationOffset;

	/**
	 * Constructor with default values.
	 */
	public CorrelationInTime() {
		valueAtInf = VALUE_AT_INF_DEFAULT;
		timeShift = TIME_SHIFT_DEFAULT;
		calculateNormalizationParameters();
	}

	/**
	 * @return the valueAtInf
	 */
	public double getValueAtInf() {
		return valueAtInf;
	}

	/**
	 * @return the timeShift
	 */
	public long getTimeShift() {
		return timeShift;
	}
	
	/**
	 * Constructor.
	 * 
	 * @param valueAtInf Minimal correlation value for events that are most far apart.
	 * 
	 * @param timeShift Shift the correlation function along the x axis. Value in ms.
	 */
	public CorrelationInTime(double valueAtInf, long timeShift) {
		if (valueAtInf >= 1 || valueAtInf < 0) {
			LOG.warn("Unexpected value for valueAtInf: {}. Setting default value: {}",
					valueAtInf, VALUE_AT_INF_DEFAULT);
			this.valueAtInf = VALUE_AT_INF_DEFAULT;
		}
		else {
			this.valueAtInf = valueAtInf;
		}
		this.timeShift = timeShift;
		calculateNormalizationParameters();
	}
	
	private double correlationUnnormalized(long dt) {
		
		double c;
		double exponent = ((double) (dt - timeShift)) / 1000.0;
		
		c = 1 - 1 / (1 + Math.exp(- exponent));
		
		return c;
	}
	
	/**
	 * Estimates correlation between two events (data access and data transmission) based on time
	 * of both events.
	 * 
	 * @param dt Difference in time in miliseconds. Time of data transmission - time of data access.
	 * 
	 * @return correlation based on difference in time.
	 */
	public double correlation(long dt) {
		
		double c;
		
		if (dt < 0) {
			c = 0;
		}
		else {
			c = normalize(correlationUnnormalized(dt));
		}
		return c;
	}
	
	
	/**
	 * Normalize to interval [valueAtInf, 1]
	 * 
	 * @param x The value to normalize
	 * @return Normalized value
	 */
	private double normalize(double x) {
//		LOG.info("normalize({})", x);
		return normalizationFactor * x + normalizationOffset;
	}
	
	private void calculateNormalizationParameters() {
		
		this.normalizationFactor = (1 - valueAtInf) / (1 - 1 / (1 + Math.exp(-(0 - timeShift)/1000.0)));
		this.normalizationOffset = valueAtInf;
//		LOG.info("calculateNormalizationParameters(): normalizationFactor = {}, normalizationOffset = {}",
//				normalizationFactor, normalizationOffset);
	}
}
