/**
 * Copyright (c) 2011, SOCIETIES Consortium (WATERFORD INSTITUTE OF TECHNOLOGY (TSSG), HERIOT-WATT UNIVERSITY (HWU), SOLUTA.NET 
 * (SN), GERMAN AEROSPACE CENTRE (Deutsches Zentrum fuer Luft- und Raumfahrt e.V.) (DLR), Zavod za varnostne tehnologije
 * informacijske dru≈æbe in elektronsko poslovanje (SETCCE), INSTITUTE OF COMMUNICATION AND COMPUTER SYSTEMS (ICCS), LAKE
 * COMMUNICATIONS (LAKE), INTEL PERFORMANCE LEARNING SOLUTIONS LTD (INTEL), PORTUGAL TELECOM INOVA√á√ÉO, SA (PTIN), IBM Corp., 
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
package org.societies.api.context.model;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This class is used to represent complex context attribute values which describe the
 * properties of a {@link CtxEntity}. 
 * 
 * @author <a href="mailto:pkosmidis@cn.ntua.gr">Pavlos Kosmides</a> (ICCS)
 * @since 1.0
 */
public class CtxAttributeComplexValue implements Serializable {

	private static final long serialVersionUID = 1738555858962493332L;

	/** The average value of the current context attribute */
	private Double average;

	/** The median value of the current context attribute */
	private Double median;

	/** The min and max range value of the current context attribute */
	private Number rangeMin, rangeMax;

	/** The location of the current context attribute */
	private String locationgps;

	/** The mode value of the current context attribute */
	private List<Integer> mode;

	/** The pairs depict a pair of a string with an integer value */
	private Map<String, Integer> pairs = new HashMap<String, Integer>();

	/** 
	 * Returns the average of the current context attribute value.
	 * 
	 * @return the average of the current context attribute value.
	 */
	public Double getAverage() {
		
		return this.average;
	}
	
	/**
	 * Sets the average of the current context attribute value.
	 * 
	 * @param average
	 * 			the average of the current context attribute value.
	 */
	public void setAverage(Double average) {
		
		this.average = average;
	}

	/**
	 * Returns the median of the current context attribute value.
	 * 
	 * @return the median of the current context attribute value.
	 */
	public Double getMedian() {
		
		return this.median;
	}
	
	/**
	 * Sets the median of the current context attribute value.
	 * 
	 * @param median
	 * 			the media of the current context attribute value.
	 */
	public void setMedian(Double median) {
		
		this.median = median;
	}

	/** 
	 * Gets the min range of the current context attribute value.
	 * 
	 * @return the min range of the current context attribute value.
	 */
	public Number getRangeMin() {
		
		return this.rangeMin;
	}

	/**
	 * Sets the min range of the current context attribute value.
	 * 
	 * @param range
	 * 			the min range of the current context attribute value.
	 */
	public void setRangeMin (Number rangeMin) {
		
		this.rangeMin = rangeMin;
	}

	/** 
	 * Gets the max range of the current context attribute value.
	 * 
	 * @return the max range of the current context attribute value.
	 */
	public Number getRangeMax() {
		
		return this.rangeMax;
	}	

	/**
	 * Sets the max range of the current context attribute value.
	 * 
	 * @param range
	 * 			the max range of the current context attribute value.
	 */
	public void setRangeMax (Number rangeMax) {
		
		this.rangeMax = rangeMax;
	}
	
	/**
	 * Gets the locationGPS of the current context attribute value.
	 * 
	 * @return the locationGPS of the current context attribute value.
	 */
	public String getLocationGPS() {
		
		return this.locationgps;
	}

	/** 
	 * Sets the locationGPS of the current context attribute value.
	 * 
	 * @param locationgps
	 * 			the locationGPS of the current context attribute value.
	 */
	public void setLocationGPS(String locationgps) {
		
		this.locationgps = locationgps;
	}
	
	/**
	 * Gets the mode of the current context attribute value.
	 * 
	 * @return the mode of the current context attribute value.
	 */
	public List<Integer> getMode() {
		
		return this.mode;
	}
	
	/**
	 * Sets the mode of the current context attribute value.
	 * 
	 * @param mode
	 * 			the mode of the current context attribute value.
	 */
	public void setMode(List<Integer> mode) {
		
		this.mode = mode;
	}

	/**
	 * Gets a pair of <String, Integer> values of the current context attribute value.
	 * 
	 * @return the pair <String, Integer> of the current context attribute value.
	 */
	public Map<String, Integer> getPairs() {
		
		return this.pairs;
	}
	
	/**
	 * Sets a pair of <String, Integer> values of the current context attribute value.
	 * 
	 * @param pairs
	 * 			the pair <String, Integer> of the current context attribute value.
	 */
	public void setPairs(Map<String, Integer> pairs) {
		
		this.pairs = pairs;
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		sb.append("CtxAttributeComplexValue [");
		sb.append("average=").append(this.average);
		sb.append(", median=").append(this.median);
		sb.append(", rangeMin=").append(this.rangeMin);
		sb.append(", rangeMax=").append(this.rangeMax);
		sb.append(", locationgps=").append(this.locationgps);
		sb.append(", mode=").append(this.mode);
		sb.append(", pairs=").append(this.pairs);
		sb.append("]");
		
		return sb.toString();
	}
}