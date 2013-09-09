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
package org.societies.integration.performance.test.upper_tester.trust;

import java.io.Serializable;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.MalformedTrustedEntityIdException;
import org.societies.api.privacytrust.trust.model.TrustEvidence;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;

/**
 * This class is used to parse JSON-formatted Strings representing trust 
 * evidence parameters into {@link TrustEvidence} objects.
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 1.2
 * @see TrustEvidenceParams
 */
public class TrustEvidenceParamParser {

	/**
	 * 
	 * @param trustEvidenceParams
	 * @return
	 * @throws IllegalArgumentException if the specified TrustEvidenceParams is
	 *         invalid.
	 * @throws NullPointerException if the specified TrustEvidenceParams is
	 *         <code>null</code>.
	 */
	public static Map<String,Set<TrustEvidence>> toTrustEvidence(
			TrustEvidenceParams trustEvidenceParams) {
		
		if (trustEvidenceParams == null)
			throw new NullPointerException("trustEvidenceParams can't be null");
			
		final Map<String,Set<TrustEvidence>> result = 
				new LinkedHashMap<String,Set<TrustEvidence>>();
		try {
			final JSONObject jsonObject = new JSONObject(
					trustEvidenceParams.getTrustEvidenceJsonString());
			
			final JSONArray trustEvidenceMap = jsonObject.getJSONArray("trustEvidenceMap");
			for (int i = 0; i < trustEvidenceMap.length(); ++i) { 
				
				final JSONObject trustEvidenceMapEntry = trustEvidenceMap.getJSONObject(i);
				// I. cssId
				final String cssId = trustEvidenceMapEntry.getString("cssId");
				// II. trustEvidence
				final Set<TrustEvidence> trustEvidenceSet = new LinkedHashSet<TrustEvidence>();
				final JSONArray trustEvidenceArray = trustEvidenceMapEntry.getJSONArray("trustEvidence");
				for (int j = 0; j < trustEvidenceArray.length(); ++j) {
				
					final JSONObject trustEvidence = trustEvidenceArray.getJSONObject(j);
					// 1. subjectId
					final TrustedEntityId subjectId = fromJSONObject(trustEvidence.getJSONObject("subjectId"));
					// 2. objectId
					final TrustedEntityId objectId = fromJSONObject(trustEvidence.getJSONObject("objectId"));
					// 3. type
					final TrustEvidenceType type = TrustEvidenceType.valueOf(
							trustEvidence.getString("type"));
					// 4. info
					Serializable info = null;
					if (TrustEvidenceType.RATED == type || TrustEvidenceType.USED_SERVICE == type) {
						final Double rating = trustEvidence.optDouble("info"); 
						if (!Double.isNaN(rating)) {
							info = rating;
						} else if (TrustEvidenceType.RATED == type) { // info is mandatory for RATED evidence 
							throw new IllegalArgumentException("Required JSONObject[\"info\"] is not a Double");
						}
					}
					trustEvidenceSet.add(new TrustEvidence(
							subjectId, objectId, type, new Date(), info, null));
				}
				result.put(cssId, trustEvidenceSet);
			}
		} catch (Exception e) {

			throw new IllegalArgumentException(e.getLocalizedMessage(), e);
		}
		
		return result;
	}
		
	private static TrustedEntityId fromJSONObject(JSONObject jsonTeid) 
			throws JSONException, MalformedTrustedEntityIdException {
		
		final String entityId = 
				jsonTeid.getString("entityId");
		final TrustedEntityType entityType = TrustedEntityType.valueOf(
				jsonTeid.getString("entityType"));
		
		return new TrustedEntityId(entityType, entityId);
	}
	
	public static void main(String[] args) throws Exception {
		
		final String jsonString = "{"
				+ "\"trustEvidenceMap\": ["
				+ "{"
				+ "  \"cssId\": \"university.ict-societies.eu\","
				+ "  \"trustEvidence\": ["
				+ "  {"
				+ "    \"subjectId\": {"
				+ "      \"entityId\": \"university.ict-societies.eu\","
				+ "      \"entityType\": \"CSS\""
				+ "    },"
				+ "    \"objectId\": {"
				+ "      \"entityId\": \"fooService.societies.local\","
				+ "      \"entityType\": \"SVC\""
				+ "    },"
				+ "    \"type\": \"USED_SERVICE\","
				+ "    \"info\": null"
				+ "  }," // 
				+ "  {"
				+ "    \"subjectId\": {"
				+ "      \"entityId\": \"university.ict-societies.eu\","
				+ "      \"entityType\": \"CSS\""
				+ "    },"
				+ "    \"objectId\": {"
				+ "      \"entityId\": \"fooService.societies.local\","
				+ "      \"entityType\": \"SVC\""
				+ "    },"
				+ "    \"type\": \"RATED\","
				+ "    \"info\": 0.5"
				+ "  }," //
				+ "  ]"
				+ "},"
				+ "{"
				+ "  \"cssId\": \"emma.ict-societies.eu\","
				+ "  \"trustEvidence\": ["
				+ "  {"
				+ "    \"subjectId\": {"
				+ "      \"entityId\": \"emma.ict-societies.eu\","
				+ "      \"entityType\": \"CSS\""
				+ "    },"
				+ "    \"objectId\": {"
				+ "      \"entityId\": \"university.ict-societies.eu\","
				+ "      \"entityType\": \"CSS\""
				+ "    },"
				+ "    \"type\": \"FRIENDED_USER\","
				+ "    \"info\": null"
				+ "  }," //
				+ "  ]"
				+ "},"
				+ "]"
				+ "}";
		final TrustEvidenceParams params = new TrustEvidenceParams(jsonString);
		System.out.println(new JSONObject(jsonString).toString(4));
		System.out.println(TrustEvidenceParamParser.toTrustEvidence(params));
	}
}