package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.societies.api.identity.IIdentityManager;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.api.schema.identity.DataIdentifierScheme;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.PrivacyPolicyTypeConstants;

/**
 * Utility class to manage RequestPolicy bean
 * * conversion between bean and old Java class RequestPolicy
 * * display method
 * * comparaison method
 * @author Olivier Maridat (Trialog)
 */
public class RequestPolicyUtils {

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy create(PrivacyPolicyTypeConstants privacyPolicyType, RequestorBean requestor, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItems) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicy = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy();
		requestPolicy.setPrivacyPolicyType(privacyPolicyType);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(requestItems);
		return requestPolicy;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy create(RequestorBean requestor, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItems) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicy = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy();
		requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.OTHER);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(requestItems);
		return requestPolicy;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy createList(RequestorBean requestor, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem... requestItems) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicy = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy();
		requestPolicy.setPrivacyPolicyType(PrivacyPolicyTypeConstants.OTHER);
		requestPolicy.setRequestor(requestor);
		requestPolicy.setRequestItems(Arrays.asList(requestItems));
		return requestPolicy;
	}


	/**
	 * Create a XACML string representing the Privacy Policy from a Java RequestPolicy
	 * @param privacyPolicy Privacy policy as a Java object
	 * @return A string containing the XACML version the privacy policy
	 */
	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicy){
		StringBuilder sb = new StringBuilder();
		if (null != requestPolicy) {
			sb.append("<RequestPolicy>");
			sb.append(RequestorUtils.toXmlString(requestPolicy.getRequestor()));
			sb.append(RequestItemUtils.toXmlString(requestPolicy.getRequestItems()));
			sb.append("</RequestPolicy>");
		}
		return sb.toString();
	}


	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy ro2 = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy) o2;
		return (/*PrivacyPolicyTypeConstantsUtils.equal(o1.getPrivacyPolicyType(), ro2.getPrivacyPolicyType())
				&& */RequestItemUtils.equal(o1.getRequestItems(), ro2.getRequestItems())
				&& RequestorUtils.equal(o1.getRequestor(), ro2.getRequestor())
				);
	}
	/**
	 * Use equal instead
	 */
	@Deprecated
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> ro2 = (List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy needle, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}
	//
	//	public static RequestItem getRequestItem(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy, Resource resourceTarger) {
	//		
	//	}

	/**
	 * Use PrivacyPolicyUtils.getDataTypes instead
	 */
	@Deprecated
	public static List<String> getDataTypes(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
		return PrivacyPolicyUtils.getDataTypes(privacyPolicy);
	}
	/**
	 * Use PrivacyPolicyUtils.getDataTypes instead
	 */
	@Deprecated
	public static List<String> getDataTypes(DataIdentifierScheme schemeFilter, org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy privacyPolicy) {
		return PrivacyPolicyUtils.getDataTypes(schemeFilter, privacyPolicy);
	}


	public static RequestPolicy toRequestPolicy(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicyBean, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == requestPolicyBean) {
			return null;
		}
		return new RequestPolicy(RequestorUtils.toRequestor(requestPolicyBean.getRequestor(), identityManager), RequestItemUtils.toRequestItems(requestPolicyBean.getRequestItems()));
	}
	public static List<RequestPolicy> toRequestPolicys(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> requestPolicyBeans, IIdentityManager identityManager) throws InvalidFormatException
	{
		if (null == requestPolicyBeans) {
			return null;
		}
		List<RequestPolicy> requestPolicys = new ArrayList<RequestPolicy>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicyBean : requestPolicyBeans) {
			requestPolicys.add(RequestPolicyUtils.toRequestPolicy(requestPolicyBean, identityManager));
		}
		return requestPolicys;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy toRequestPolicyBean(RequestPolicy requestPolicy)
	{
		if (null == requestPolicy) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy requestPolicyBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy();
		requestPolicyBean.setRequestor(RequestorUtils.toRequestorBean(requestPolicy.getRequestor()));
		requestPolicyBean.setRequestItems(RequestItemUtils.toRequestItemBeans(requestPolicy.getRequests()));
		return requestPolicyBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> toRequestPolicyBeans(List<RequestPolicy> requestPolicys)
	{
		if (null == requestPolicys) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy> requestPolicyBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy>();
		for(RequestPolicy requestPolicy : requestPolicys) {
			requestPolicyBeans.add(RequestPolicyUtils.toRequestPolicyBean(requestPolicy));
		}
		return requestPolicyBeans;
	}
}
