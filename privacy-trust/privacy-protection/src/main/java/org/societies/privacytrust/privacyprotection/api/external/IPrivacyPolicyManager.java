package org.societies.privacytrust.privacyprotection.api.external;

import java.util.List;
import java.util.Map;

/**
 * External Interface to do actions relative to a privacy policy or a privacy
 * agreement.
 * @author olivierm
 * @version 1.0
 * @created 09-nov.-2011 16:45:29
 */
public interface IPrivacyPolicyManager {
	/**
	 * Retrieve a (CIS or Service) privacy policy by its ID.
	 * Example of use:
	 * - CIS Management, when it sends the CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * 
	 * @param id
	 */
	public Object getPrivacyPolicy(Object id);

	/**
	 * Retrieve (CIS or Service)  privacy policy using criteria
	 * Example of use:
	 * - CIS Management, when it sends CIS data (URI, description and privacy
	 * policy) to let a user join it.
	 * - CIS Management, to edit a policy (GUI call)
	 * - CIS Management, to list policies (to choose one to edit for example)
	 * 
	 * @param criteria
	 */
	public List<Object> getPrivacyPolicies(Map criteria);

	/**
	 * Store or update a (CIS or Service) privacy policy
	 * Example of use:
	 * - CIS Management, to create a policy for a CIS.
	 * - 3rd Service Creation, to attach a policy to a service
	 * - More generally: GUI, to edit a policy.
	 * 
	 * @param privacyPolicy
	 */
	public Object updatePrivacyPolicy(Object privacyPolicy);

	/**
	 * Delete a (CIS or Service) privacy policy by its ID.
	 * 
	 * @param id
	 */
	public boolean deletePrivacyPolicy(Object id);

	/**
	 * Delete one or more (CIS or Service) privacy policies.
	 * 
	 * @param criteria
	 */
	public boolean deletePrivacyPolicies(Map criteria);

	/**
	 * Help a developer or a user to create a privacy policy by inferring a default
	 * one using information about the CIS or the service. The privacy policy in
	 * result will be slighty completed but still need to be filled. E.g. if a CIS
	 * configuration contains information about geolocation data, the inference engine
	 * will add geolocation data line to the privacy policy.
	 * Example of use:
	 * - CIS Management, or 3rd Service Creation, to create a policy
	 * 
	 * @param configuration
	 * @param privacyPolicyType CIS or Service
	 */
	public Object inferPrivacyPolicy(Map configuration, Object privacyPolicyType);
}