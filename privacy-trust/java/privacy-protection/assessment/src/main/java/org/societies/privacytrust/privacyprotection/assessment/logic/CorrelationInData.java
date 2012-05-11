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
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class CorrelationInData {

	private static Logger LOG = LoggerFactory.getLogger(CorrelationInData.class);

	private final double VALUE_AT_INF_DEFAULT = 0.1;
	private final long SIZE_SHIFT_DEFAULT = 0;
	private final double SIZE_SCALE_DEFAULT = 1;
	
	private double valueAtInf;
	private long xShift;
	private double xScaleLeft;
	private double xScaleRight;

	private double normalizationFactor;
	private double normalizationOffset;
	
	/**
	 * Constructor with default values.
	 */
	public CorrelationInData() {
		valueAtInf = VALUE_AT_INF_DEFAULT;
		xShift = SIZE_SHIFT_DEFAULT;
		xScaleLeft = SIZE_SCALE_DEFAULT;
		xScaleRight = SIZE_SCALE_DEFAULT;
		calculateNormalizationParameters();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param valueAtInf
	 * 
	 * @param sizeShift Shift the correlation function along the x axis.
	 * 
	 * @param sizeScaleLeft x axis scaling factor for cases when difference is negative.
	 * If greater than 1, the correlation function gets wider (less sensitive to size differences).
	 * If smaller than 1, the function gets more narrow (more sensitive to size differences).
	 * 
	 * @param sizeScaleRight x axis scaling factor for cases when difference is positive.
	 * If greater than 1, the correlation function gets wider (less sensitive to size differences).
	 * If smaller than 1, the function gets more narrow (more sensitive to size differences).
	 */
	public CorrelationInData(double valueAtInf, long sizeShift, double sizeScaleLeft, double sizeScaleRight) {
		this.valueAtInf = valueAtInf;
		this.xShift = sizeShift;
		this.xScaleLeft = sizeScaleLeft;
		this.xScaleRight = sizeScaleRight;
		if (xShift != 0) {
			LOG.warn("xShift set to non-zero: {}", xShift);
		}
		calculateNormalizationParameters();
	}
	
	private double correlationUnnormalized(long deltaSize) {
		
		double c;
		double xScale;
		
		if (deltaSize - xShift < 0) {
			xScale = this.xScaleLeft;
		}
		else {
			xScale = this.xScaleRight;
		}
		
		c = (deltaSize - xShift) / xScale;
		c = Math.exp(-Math.pow(c, 2));
		return c;
	}
	
	public double correlation(long deltaSize) {
		
		double c;
		
		c = normalize(correlationUnnormalized(deltaSize));
		return c;
	}
	
	/**
	 * Normalize to interval [valueAtInf, 1]
	 * 
	 * @param x The value to normalize
	 * @return Normalized value
	 */
	private double normalize(double x) {
		return normalizationFactor * x + normalizationOffset;
	}
	
	private void calculateNormalizationParameters() {
		
		this.normalizationFactor = 1 - valueAtInf;
		this.normalizationOffset = valueAtInf;
	}
}
