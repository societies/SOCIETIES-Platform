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
package org.societies.context.similarity.attributes;

import java.util.Arrays;
import java.util.HashMap;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeTypes;
import org.societies.api.identity.IIdentity;
import org.societies.context.similarity.utilities.GetContextData;

public class locationCoordinates {
	//
	private GetContextData gcd;
	private Double[][] userLocation;
	private String[] userList;
	private Double shortest = 1000000000.0;
	private CtxAttributeTypes cat;
	
	public locationCoordinates(){
		gcd = new GetContextData();
	}
		
	public HashMap<String, Double> evaluate(IIdentity[] allOwners){
	
		HashMap<String, Double> results = new HashMap<String, Double>();
		
		userLocation = new Double[allOwners.length][allOwners.length];
		Arrays.fill(userLocation, 0d);
		userList = new String[allOwners.length];
		int i = 0; 
				
		for (IIdentity css : allOwners){
			// get context value
			CtxAttribute contextResult = gcd.getContext(css, cat.LOCATION_COORDINATES);
			String contextValue = contextResult.getStringValue();			
			String[] splitString = contextValue.split(",");
			calculateDistance(splitString[0], splitString[1]);
		}
		 
		results = calculateResults();
		//
		return results;
	}

	private HashMap<String, Double> calculateResults(){
		HashMap<String, Double> results = new HashMap<String, Double>();
		results.put("5 Percent", 0.0);
		results.put("10 Percent", 0.0);
		results.put("25 Percent", 0.0);
		results.put("50 Percent", 0.0);
		for (int i = 0; i < userList.length - 1; i++){
			for (int j = (i + 1); j < userList.length; j++){
				double temp = Math.sqrt((shortest- userLocation[i][j])*(shortest- userLocation[i][j]));
				double percent = (shortest/temp*100);
				if (percent < 105.0){
					results.put("5 Percent", results.get("5 Percent") + 1);
				} else if (percent < 110.0){
						results.put("10 Percent", results.get("10 Percent") + 1);
				} else if (percent < 125.0){
					results.put("25 Percent", results.get("25 Percent") + 1);
				} else if (percent < 150.0){
					results.put("50 Percent", results.get("50 Percent") + 1);
				}
			}
		}
		results.put("5 Percent",  (results.get("5 Percent")/userList.length * 100));
		results.put("10 Percent", (results.get("10 Percent")/userList.length * 100));
		results.put("25 Percent", (results.get("25 Percent")/userList.length * 100));
		results.put("50 Percent", (results.get("50 Percent")/userList.length * 100));
		
		return results;
	}
	
	private void calculateDistance(String lat, String lng){
		for (int i = 0; i < userList.length - 1; i++){

			double lat1 = Double.parseDouble(lat); 
			double lng1 = Double.parseDouble(lng);
			
			for (int x = i + 1; x < userList.length;x++){
				// TODO convert from string to lat long
				double lat2 = 0.0; 
				double lng2 = 0.0; 
				double dist = compute (lat1, lng1, lat2, lng2);
				userLocation[i][x] = dist;
				if (dist < shortest){
					shortest = dist;
				}
			}
		}
	}
	
	private double compute (double lat1, double lng1, double lat2, double lng2){
		double earthRadius = 6371.0;
	    double dLat = Math.toRadians(lat2-lat1);
	    double dLng = Math.toRadians(lng2-lng1);
	    double sindLat = Math.sin(dLat / 2);
	    double sindLng = Math.sin(dLng / 2);
	    double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2) * Math.cos(lat1) * Math.cos(lat2);
	    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
	    double dist = earthRadius * c;
	    return dist;
	}
	
}
