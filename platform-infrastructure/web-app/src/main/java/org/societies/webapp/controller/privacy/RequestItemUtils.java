package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.List;

import org.societies.api.privacytrust.privacy.model.privacypolicy.RequestItem;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class RequestItemUtils {
	/**
	 * Instantiate a mandatory request item
	 * @param resource
	 * @param actions
	 * @param conditions
	 * @return
	 */
	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> conditions) {
		return create(resource, actions, conditions, false);
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem create(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Action> actions, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Condition> conditions, boolean optional) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItem = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem();
		requestItem.setResource(resource);
		requestItem.setActions(actions);
		requestItem.setConditions(conditions);
		requestItem.setOptional(optional);
		return requestItem;
	}


	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItem){
		StringBuilder sb = new StringBuilder();
		if (null != requestItem) {
			sb.append("\n<Target>\n");
			sb.append(ResourceUtils.toXmlString(requestItem.getResource()));
			sb.append(ActionUtils.toXmlString(requestItem.getActions()));
			sb.append(ConditionUtils.toXmlString(requestItem.getConditions()));
			sb.append("\t<optional>"+requestItem.isOptional()+"</optional>\n");
			sb.append("</Target>");
		}
		return sb.toString();
	}

	public static String toXmlString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItems){
		StringBuilder sb = new StringBuilder();
		if (null != requestItems) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItem : requestItems) {
				sb.append(toXmlString(requestItem));
			}
		}
		return sb.toString();
	}

	public static String toString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem value){
		StringBuilder builder = new StringBuilder();
		builder.append("RequestItem [");
		if (null != value) {
			builder.append("getActions()=");
			builder.append(ActionUtils.toString(value.getActions()));
			builder.append(", getConditions()=");
			builder.append(ConditionUtils.toString(value.getConditions()));
			builder.append(", isOptional()=");
			builder.append(value.isOptional());
			builder.append(", getResource()=");
			builder.append(ResourceUtils.toString(value.getResource()));
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> values){
		StringBuilder sb = new StringBuilder();
		if (null != values) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem value : values) {
				sb.append(toString(value));
			}
		}
		return sb.toString();
	}



	@Deprecated
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem o1, Object o2) {
		return equal(o1, o2);
	}
	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem o1, Object o2) {
		return equal(o1, o2, false);
	}
	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem o1, Object o2, boolean dontCheckOptional) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem ro2 = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem) o2;
		return (ActionUtils.equal(o1.getActions(), ro2.getActions())
				&& ConditionUtils.equal(o1.getConditions(), ro2.getConditions())
				&& ResourceUtils.equal(o1.getResource(), ro2.getResource())
				&& (dontCheckOptional || o1.isOptional() == ro2.isOptional())
				);
	}

	public static boolean equal(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { 
			return false;
		}
		// -- Verify obj type
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> ro2 = (List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem needle, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}


	public static RequestItem toRequestItem(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean)
	{
		if (null == requestItemBean) {
			return null;
		}
		return new RequestItem(ResourceUtils.toResource(requestItemBean.getResource()), ActionUtils.toActions(requestItemBean.getActions()), ConditionUtils.toConditions(requestItemBean.getConditions()), requestItemBean.isOptional());
	}
	public static List<RequestItem> toRequestItems(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItemBeans)
	{
		if (null == requestItemBeans) {
			return null;
		}
		List<RequestItem> requestItems = new ArrayList<RequestItem>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean : requestItemBeans) {
			requestItems.add(RequestItemUtils.toRequestItem(requestItemBean));
		}
		return requestItems;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem toRequestItemBean(RequestItem requestItem)
	{
		if (null == requestItem) {
			return null;
		}
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem requestItemBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem();
		requestItemBean.setResource(ResourceUtils.toResourceBean(requestItem.getResource()));
		requestItemBean.setOptional(requestItem.isOptional());
		requestItemBean.setActions(ActionUtils.toActionBeans(requestItem.getActions()));
		requestItemBean.setConditions(ConditionUtils.toConditionBeans(requestItem.getConditions()));
		return requestItemBean;
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> toRequestItemBeans(List<RequestItem> requestItems)
	{
		if (null == requestItems) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem> requestItemBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestItem>();
		for(RequestItem requestItem : requestItems) {
			requestItemBeans.add(RequestItemUtils.toRequestItemBean(requestItem));
		}
		return requestItemBeans;
	}
}
