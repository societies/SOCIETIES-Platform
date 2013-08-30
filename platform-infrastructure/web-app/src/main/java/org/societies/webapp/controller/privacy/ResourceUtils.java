package org.societies.webapp.controller.privacy;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.context.model.MalformedCtxIdentifierException;
import org.societies.api.identity.util.DataIdentifierFactory;
import org.societies.api.identity.util.DataIdentifierSchemeUtils;
import org.societies.api.identity.util.DataTypeFactory;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.privacytrust.privacy.model.privacypolicy.Resource;
import org.societies.api.schema.identity.DataIdentifier;
import org.societies.api.schema.identity.DataIdentifierScheme;

/**
 * Tool class to manage conversion between Java type and Bean XMLschema generated type
 * @author Olivier Maridat (Trialog)
 */
public class ResourceUtils {
	private static Logger LOG = LoggerFactory.getLogger(ResourceUtils.class.getName());

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource create(String dataIdUri) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource();
		DataIdentifier dataId;
		try {
			dataId = DataIdentifierFactory.fromUri(dataIdUri);
			resource.setDataIdUri(dataId.getUri());
			resource.setDataType(dataId.getType());
			resource.setScheme(dataId.getScheme());
		} catch (MalformedCtxIdentifierException e) {
			LOG.error("Can't retrieve all data identifier fields using the data id uri", e);
			resource.setDataIdUri(dataIdUri);
		}

		return resource;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource create(DataIdentifierScheme dataScheme, String dataType) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource();
		resource.setScheme(dataScheme);
		resource.setDataType(dataType);
		return resource;
	}


	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource create(DataIdentifier dataId) {
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource();
		resource.setDataIdUri(dataId.getUri());
		resource.setDataType(dataId.getType());
		resource.setScheme(dataId.getScheme());
		return resource;
	}


	public static String getDataIdUri(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource) {
		return ((null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) ? resource.getScheme()+":///"+resource.getDataType() : resource.getDataIdUri());
	}

	public static String getDataType(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource) {
		// No URI: scheme+type available
		if (null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) {
			return resource.getDataType();
		}
		// URI available
		return DataTypeFactory.fromUri(resource.getDataIdUri()).getType();
	}

	public static DataIdentifier getDataIdentifier(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource) throws MalformedCtxIdentifierException {
		// No URI: scheme+type available
		if (null == resource.getDataIdUri() || "".equals(resource.getDataIdUri())) {
			return DataIdentifierFactory.fromType(resource.getScheme(), resource.getDataType());
		}
		// URI available
		return DataIdentifierFactory.fromUri(resource.getDataIdUri());
	}


	public static String toXmlString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource){
		StringBuilder sb = new StringBuilder();
		if (null != resource) {
			sb.append("\n<Resource>\n");
			// URI
			if (null != resource.getDataIdUri()){
				sb.append("\t<Attribute AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:resource-id\" DataType=\"org.societies.api.context.model.CtxIdentifier\">\n");
				sb.append("\t\t<AttributeValue>"+resource.getDataIdUri()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			// Scheme + Type
			if (null != resource.getDataType()){
				sb.append("\t<Attribute AttributeId=\""+resource.getScheme()+"\" DataType=\"http://www.w3.org/2001/XMLSchema#string\">\n");
				sb.append("\t\t<AttributeValue>"+resource.getDataType()+"</AttributeValue>\n");
				sb.append("\t</Attribute>\n");
			}
			sb.append("</Resource>\n");
		}
		return sb.toString();
	}

	public static String toString(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resource){
		StringBuilder builder = new StringBuilder();
		builder.append("Resource [");
		if (null != resource) {
			builder.append("getDataIdUri()=");
			if (null==resource.getDataIdUri()){
				builder.append("null");
			}else{
				builder.append(resource.getDataIdUri());
			}
			builder.append(", getDataType()=");
			builder.append(resource.getDataType());
			builder.append(", getScheme()=");
			builder.append(resource.getScheme());
		}
		builder.append("]");
		return builder.toString();
	}

	public static String toString(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> values){
		StringBuilder sb = new StringBuilder();
		if (null != values) {
			for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource entry : values) {
				sb.append(toString(entry));
			}
		}
		return sb.toString();
	}


	public static boolean equal(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (o1.getClass() != o2.getClass()) { return false; }
		// -- Verify obj type
		org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource ro2 = (org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource) o2;
		return (StringUtils.equals(o1.getDataIdUri(), ro2.getDataIdUri())
				&& StringUtils.equals(o1.getDataType(), ro2.getDataType())
				&& DataIdentifierSchemeUtils.equal(o1.getScheme(), ro2.getScheme())
				);
	}
	@Deprecated
	public static boolean equals(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource o1, Object o2) {
		return equal(o1, o2);
	}

	public static boolean equal(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> o1, Object o2) {
		// -- Verify reference equality
		if (o1 == o2) { return true; }
		if (o2 == null) { return false; }
		if (o1 == null) { return false; }
		if (!(o2 instanceof List)) { return false; }
		// -- Verify obj type
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> ro2 = (List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource>) o2;
		if (o1.size() != ro2.size()) {
			return false;
		}
		boolean result = true;
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource o1Entry : o1) {
			result &= contain(o1Entry, ro2);
		}
		return result;
	}

	public static boolean contain(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource needle, List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> haystack) {
		if (null == haystack || haystack.size() <= 0 || null == needle) {
			return false;
		}
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource entry : haystack) {
			if (equal(needle, entry)) {
				return true;
			}
		}
		return false;
	}

	public static Resource toResource(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean)
	{
		if (null == resourceBean) {
			return null;
		}
		Resource resource = null;
		try {
			// Data id
			if (null != resourceBean.getDataIdUri() && !"".equals(resourceBean.getDataIdUri()) && null!=resourceBean.getScheme())  {
				resource = new Resource(DataIdentifierFactory.fromUri(resourceBean.getDataIdUri()));
			}
			// Data type
			else if (null != resourceBean.getDataType() && null!=resourceBean.getScheme()) {
				resource = new Resource(resourceBean.getScheme(), resourceBean.getDataType());
			}
			else {
				throw new PrivacyException("The resource id or type and DataIdentifierScheme can't be null!");
			}
		} catch (MalformedCtxIdentifierException e) {
			return null;
		} catch (PrivacyException e) {
			e.printStackTrace();
			return null;
		}
		return resource;
	}
	public static List<Resource> toResources(List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> resourceBeans)
	{
		if (null == resourceBeans) {
			return null;
		}
		List<Resource> resources = new ArrayList<Resource>();
		for(org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean : resourceBeans) {
			resources.add(ResourceUtils.toResource(resourceBean));
		}
		return resources;
	}

	public static org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource toResourceBean(Resource resource)
	{
		try
		{
			if (null == resource) {
				return null;
			}
			org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource resourceBean = new org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource();
			resourceBean.setDataIdUri((null != resource.getDataId() ? resource.getDataId().getUri() : null));
			resourceBean.setDataType(resource.getDataType());
			if (resource.getScheme()==null){
				throw new PrivacyException("The DataIdentifierScheme cannot be null!");
			}
			resourceBean.setScheme(resource.getScheme());
			return resourceBean;
		}catch(PrivacyException e){
			e.printStackTrace();
			return null;
		}
	}
	public static List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> toResourceBeans(List<Resource> resources)
	{
		if (null == resources) {
			return null;
		}
		List<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource> resourceBeans = new ArrayList<org.societies.api.schema.privacytrust.privacy.model.privacypolicy.Resource>();
		for(Resource resource : resources) {
			resourceBeans.add(ResourceUtils.toResourceBean(resource));
		}
		return resourceBeans;
	}
}
