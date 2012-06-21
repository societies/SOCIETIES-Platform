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
package org.societies.api.internal.privacytrust.privacyprotection;

import java.util.Map;

import org.societies.api.identity.Requestor;
import org.societies.api.internal.privacytrust.privacyprotection.model.PrivacyException;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.RequestPolicy;
import org.societies.api.internal.privacytrust.privacyprotection.model.privacypolicy.constants.PrivacyPolicyTypeConstants;

/**
 * Interface exposed to Societies components in order to do actions relative
 * to a privacy policy
 * 
 * @author Olivier Maridat (Trialog)
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
	 * Retrieve a CIS or 3P service privacy policy by the ID of the CIS or the 3P service
	 * Example of use:
	 * - CIS Management, when it sends the CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * 
	 * @param requestor Id of the CIS or the 3P service
	 * @return the privacy policy
	 * @throws PrivacyException
	 */
	public RequestPolicy getPrivacyPolicy(Requestor requestor) throws PrivacyException;
	
	/**
	 * Retrieve the "privacy-policy.xml" file in the JAR file.
	 * This file need to be in the root folder of the OSGi bundle JAR file.
	 * 
	 * @param jarLocation Location of the 3P service OSGi bundle JAR
	 * @return the content of the "privacy-policy.xml" file
	 * @throws PrivacyException
	 */
	public String getPrivacyPolicyFrom3PServiceJar(String jarLocation) throws PrivacyException;
	
	/**
	 * Retrieve the "privacy-policy.xml" file in the JAR file.
	 * 
	 * @param jarLocation Location of the 3P service JAR
	 * @param options Options giving more information about the JAR file. If null, the JAR has to be an OSGi bundle, and by default, the privacy policy file is on the root folder of the jar file.
	 * @return the content of the "privacy-policy.xml" file
	 * @throws PrivacyException
	 */
	public String getPrivacyPolicyFrom3PServiceJar(String jarLocation, Map<String, String> options) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy
	 * Example of use:
	 * - CIS Management, to create a policy for a CIS.
	 * - 3rd Service Creation, to attach a policy to a service
	 * - More generally: GUI, to edit a policy.
	 * 
	 * @param privacyPolicy The privacy policy
	 * @return The stored privacy policy
	 */
	public RequestPolicy updatePrivacyPolicy(RequestPolicy privacyPolicy) throws PrivacyException;
	
	/**
	 * Store or update a (CIS or 3P Service) privacy policy from an XML version of it.
	 * 
	 * @param privacyPolicy XML formatted string containing the privacy policy
	 * @param requestor Identity of the owner of this privacy policy
	 * @return The privacy policy now stored by the PrivacyPolicyManager
	 * @throws PrivacyException
	 */
	public RequestPolicy updatePrivacyPolicy(String privacyPolicy, Requestor requestor) throws PrivacyException;
	
	/**
	 * Delete a CIS or 3P service privacy policy by the ID of the CIS or the 3P Service
	 * 
	 * @param requestor Id of the CIS or the 3P service
	 * @return True if the privacy policy is successfully deleted
	 */
	public boolean deletePrivacyPolicy(Requestor requestor) throws PrivacyException;

	/**
	 * Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * Example of use:
	 * - CIS Management, or 3rd Service Creation, to create a policy
	 * 
	 * @param privacyPolicyType 1 means CIS privacy policy, 0 means 3P Service privacy policy
	 * @param configuration Configuration of the CIS or the 3P service
	 * @return A not complete privacy policy
	 */
	public RequestPolicy inferPrivacyPolicy(PrivacyPolicyTypeConstants privacyPolicyType, Map configuration) throws PrivacyException;
	
	/**
	 * Create a Privacy Policy in an XML format from a Java format Privacy Policy
	 * @param privacyPolicy Privacy policy
	 * @return A string containing the XML version the privacy policy
	 */
	public String toXMLString(RequestPolicy privacyPolicy);
	
	/**
	 * Create a Privacy Policy in a Java format from a XML format Privacy Policy
	 * @param privacyPolicy Privacy policy
	 * @return A Java object containing the privacy policy
	 * @throws PrivacyException 
	 */
	public RequestPolicy fromXMLString(String privacyPolicy) throws PrivacyException;
}