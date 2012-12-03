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
package org.societies.privacytrust.trust.impl.engine.util;

import org.apache.commons.math.stat.StatUtils;
import org.apache.commons.math.stat.descriptive.DescriptiveStatistics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cern.colt.Arrays;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.3
 */
public class MathUtils {
	
	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(MathUtils.class);

	public static double[] normalise(final double[] sample) {
	
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("sample=" + Arrays.toString(sample));
		final DescriptiveStatistics stats = new DescriptiveStatistics();
		
		// Add the data from the series to stats
		for (int i = 0; i < sample.length; i++) {
			stats.addValue(sample[i]);
		}
		
		// Compute mean and standard deviation
		final double mean = stats.getMean();
		final double standardDeviation = stats.getStandardDeviation();
		
		// initialize the standardizedSample, which has the same length as the sample
		double[] normalisedSample = new double[sample.length];
		
		for (int i = 0; i < sample.length; i++) {
			// z = (x- mean)/standardDeviation
			if (standardDeviation != 0.0d)
				normalisedSample[i] = (sample[i] - mean) / standardDeviation;
			else
				normalisedSample[i] = 0.0d;
		}
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("normalisedSample=" + Arrays.toString(normalisedSample));
		return normalisedSample;
	}
	
	public static double[] stanine(final double[] input) {
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("input=" + Arrays.toString(input));
		final double[] zscores = normalise(input);
		final double[] stanines = new double[zscores.length];
		for (int i = 0; i < zscores.length; ++i) {
			if (zscores[i] < -1.75d)
				stanines[i] = 1;
			else if (zscores[i] >= -1.75d && zscores[i] < -1.25d)
				stanines[i] = 2;
			else if (zscores[i] >= -1.25d && zscores[i] < -0.75d)
				stanines[i] = 3;
			else if (zscores[i] >= -0.75d && zscores[i] < -0.25d)
				stanines[i] = 4;
			else if (zscores[i] >= -0.25d && zscores[i] < +0.25d)
				stanines[i] = 5;
			else if (zscores[i] >= +0.25d && zscores[i] < +0.75d)
				stanines[i] = 6;
			else if (zscores[i] >= +0.75d && zscores[i] < +1.25d)
				stanines[i] = 7;
			else if (zscores[i] >= +1.25d && zscores[i] < +1.75d)
				stanines[i] = 8;
			else // if (zscores[i] >= +1.75d)
				stanines[i] = 9;
		}
		
		if (LOG.isInfoEnabled()) // TODO DEBUG
			LOG.info("stanines=" + Arrays.toString(stanines));
		return stanines;
	}
	
	public static double min(double[] input) {
		
		return StatUtils.min(input);
	}
	
	public static double max(double[] input) {
		
		return StatUtils.max(input);
	}
}
