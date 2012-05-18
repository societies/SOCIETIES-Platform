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
 * Estimation of correlation between two events (data access and data transmission) based on size
 * difference between the data in events.
 * 
 * The function itself is basically e^(-x^2), usually without x axis shift, with max value
 * of 1.
 * This results in a correlation that: <br/>
 * - is 1 if size of accessed data matches size of transmitted data. <br/>
 * - continuously decreases with size difference <br/>
 * - then asymptotically approaches a value greater than zero (multiple data can be accumulated and
 * sent at once in a bigger chunk; on the other hand data can be compressed and smaller transmitted
 * data does not necessarily mean the events are not correlated) <br/>
 *
 * @author Mitja Vardjan
 *
 */
public class CorrelationInData {

	private static Logger LOG = LoggerFactory.getLogger(CorrelationInData.class);

	private final double VALUE_AT_INF_DEFAULT = 0.1;
	private final double SIZE_SCALE_DEFAULT = 1;
	
	private double valueAtInf;
	private double xScaleLeft;
	private double xScaleRight;

	private double normalizationFactor;
	private double normalizationOffset;
	
	/**
	 * Constructor with default values.
	 */
	public CorrelationInData() {
		valueAtInf = VALUE_AT_INF_DEFAULT;
		xScaleLeft = SIZE_SCALE_DEFAULT;
		xScaleRight = SIZE_SCALE_DEFAULT;
		calculateNormalizationParameters();
	}
	
	/**
	 * Constructor.
	 * 
	 * @param valueAtInf Minimal correlation value for events that are most far apart.
	 * 
	 * @param sizeScaleLeft x axis scaling factor for cases when data size difference is negative.
	 * Negative difference can occur for example when data has been compressed before sending.
	 * If greater than 1, the correlation function gets wider (less sensitive to size differences).
	 * If smaller than 1, the function gets more narrow (more sensitive to size differences).
	 * 
	 * @param sizeScaleRight x axis scaling factor for cases when data size difference is positive.
	 * Positive difference can occur for example when multiple pieces of data have been accumulated
	 * before sending everything in a single packet.
	 * If greater than 1, the correlation function gets wider (less sensitive to size differences).
	 * If smaller than 1, the function gets more narrow (more sensitive to size differences).
	 */
	public CorrelationInData(double valueAtInf, double sizeScaleLeft, double sizeScaleRight) {
		
		if (valueAtInf >= 1 || valueAtInf < 0) {
			LOG.warn("Unexpected value for valueAtInf: {}. Setting default value: {}",
					valueAtInf, VALUE_AT_INF_DEFAULT);
			this.valueAtInf = VALUE_AT_INF_DEFAULT;
		}
		else {
			this.valueAtInf = valueAtInf;
		}
		this.xScaleLeft = sizeScaleLeft;
		this.xScaleRight = sizeScaleRight;
		calculateNormalizationParameters();
	}
	
	private double correlationUnnormalized(long deltaSize) {
		
		double c;
		double xScale;
		
		if (deltaSize < 0) {
			xScale = this.xScaleLeft;
		}
		else {
			xScale = this.xScaleRight;
		}
		
		c = Math.exp(-Math.pow(deltaSize / xScale, 2));
		return c;
	}
	
	/**
	 * Estimates correlation between two events (data access and data transmission) based on sizes
	 * of data in both events.
	 * 
	 * @param deltaSize Difference in size of data in bytes.
	 * Size of transmitted data - size of accessed data.
	 * 
	 * @return correlation based on difference in data sizes.
	 */
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
		
		// Value of Math.exp(-Math.pow(0 / xScale, 2)) is always 1
		// => no need to divide normalizationFactor with it
		this.normalizationFactor = (1 - valueAtInf);
		this.normalizationOffset = valueAtInf;
	}
	
	public double getMeanCorrelation() {
		return (1 - valueAtInf) / 2;
	}
}
