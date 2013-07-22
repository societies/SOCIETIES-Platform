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
package org.societies.security.policynegotiator.requester;

import java.util.Random;

import org.societies.api.personalisation.mgmt.IPersonalisationManager;


/**
 * 
 *
 * @author Mitja Vardjan
 *
 */
public class SopSuitability {

	private IPersonalisationManager personalizationMgr;
	
	public SopSuitability(IPersonalisationManager personalizationMgr) {
		this.personalizationMgr = personalizationMgr;
	}
	
	/**
	 * Compares the values in preferences with values in given SOP option.
	 * Suitability of the SOP option is calculated based on weighted average.
	 * 
	 * @param preferenceNames Names of the preferences to be fetched from
	 * {@link IPersonalisationManager}
	 * @param valuesInSop Numerical values in given SOP option
	 * @param weights Importance estimations of parameters.
	 * 
	 * @return Suitability for given SOP option
	 */
	public double calculateSuitability(String[] preferenceNames, double[] valuesInSop, double[] weights) {
		
		double suitability = 0;
		int n = preferenceNames.length;
		double[] preferences = getPreferences(preferenceNames);
		double[] ranges = new double[n];
		
		for (int k = 0; k < n; k++) {
			ranges[k] = 1;  //TODO: When and if possible, get typical ranges of preference values
		}
		for (int k = 0; k < n; k++) {
			// Calculate deviations from preferences and store them in preferences[] array
			preferences[k] = Math.abs((preferences[k] - valuesInSop[k]) / ranges[k]);
			// Some preferences may be more important than others
			preferences[k] *= weights[k];
			// Temporary sum of deviations
			suitability += preferences[k];
		}
		
		// Sum of deviations in range [0, oo] --> suitability in range of [1, 0]
		suitability = 1 / (suitability + 1);
		
		// The lower number of preferences (and calculation accuracy), the more to decrease suitability.
		suitability *= (n - 0.5) / n;
		
		return suitability;
	}

	/**
	 * Get preferences from {@link IInternalPersonalisationManager}
	 * 
	 * @param preferenceNames The preferences to get
	 * 
	 * @return Preferences in same order as given preferenceNames
	 */
	private double[] getPreferences(String[] preferenceNames) {
		
		double[] preferences = new double[preferenceNames.length];
		Random rnd = new Random();
		
		// Get preference values one by one. Values are usually numerical.
		for (int k = 0; k < preferences.length; k++) {
//			try {
//				preferences[k] = personalizationMgr.getPreference(requestor, ownerID, serviceType, serviceId, preferenceName);
//				preferences[k] = personalizationMgr.getPreference(policyNegotiator, poliyNegotiator, "game", serviceId, preferenceName);
//			} catch (ServiceUnavailableException e) {
//				LOG.info("Personalization Manager not available");
//			}
			preferences[k] = rnd.nextDouble();  // FIXME: call personalization manager instead
		}
		return preferences;
	}
}
