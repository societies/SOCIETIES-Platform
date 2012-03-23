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
package org.societies.api.context.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

/**
 * This class is used to identify context attributes. It provides methods
 * that return information about the identified attribute including:
 * <ul>
 * <li><tt>OperatorId</tt>: A unique identifier of the CSS or CIS where the 
 * entity containing the identified context attribute was first stored.</li>
 * <li><tt>ModelType</tt>: Describes the type of the identified context model
 * object, i.e. {@link CtxModelTypeBean#ATTRIBUTE ATTRIBUTE}.</li>
 * <li><tt>Type</tt>: A semantic tag that characterises the identified context
 * attribute. e.g. "name".</li>
 * <li><tt>ObjectNumber</tt>: A unique number within the CSS/CIS where the
 * respective context information was initially sensed/collected and stored.</li>
 * </ul>
 * <p>
 * Compared to context entity or association identifiers, attribute identifiers
 * additionally contain their <tt>Scope</tt>, i.e. the Entity identifier they
 * are associated with. The format of the resulting identifier is as follows:
 * <pre>
 * &lt;Scope&gt;/ATTRIBUTE/&lt;Type&gt;/&lt;ObjectNumber&gt;
 * </pre>
 * <p>
 * Use the {@link #getScope()} method to retrieve the {@link CtxEntityIdentifierBean}
 * representing the attribute's <tt>Scope</tt>
 * as a <code>CtxEntityIdentifierBean</code> object.
 * 
 * @see CtxEntityIdentifierBean
 * @see CtxIdentifierBean
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@XmlType(namespace="http://societies.org/api/schema/context/model")
@XmlAccessorType(XmlAccessType.FIELD)
public class CtxAttributeIdentifierBean extends CtxIdentifierBean {
	
	private static final long serialVersionUID = -282171829285239788L;
	
	/** The scope of this context attribute identifier. */
	private transient CtxEntityIdentifierBean scope;

	@SuppressWarnings("unused")
	private CtxAttributeIdentifierBean() {}
	
	/**
	 * Creates a context attribute identifier by specifying the containing
	 * entity, the attribute type and the unique numeric model object identifier
	 * 
	 * @param scope
	 *            the {@link CtxEntityIdentifierBean} of the context entity containing
	 *            the identified attribute
	 * @param type
	 *            the attribute type, e.g. "name"
	 * @param objectNumber
	 *            the unique numeric model object identifier
	 */
	public CtxAttributeIdentifierBean(CtxEntityIdentifierBean scope, String type, Long objectNumber) {
		
		super(scope.getOperatorId(), CtxModelTypeBean.ATTRIBUTE, type, objectNumber);
		this.scope = scope;
	}
	
	CtxAttributeIdentifierBean(String str) throws MalformedCtxIdentifierException {
		
		super(str);
	}
	
	/**
	 * Returns the {@link CtxEntityIdentifierBean} of the context entity containing
	 * the identified attribute.
	 * 
	 * @return the {@link CtxEntityIdentifierBean} of the context entity containing
	 *         the identified attribute.
	 */
	public CtxEntityIdentifierBean getScope() {
		
		return this.scope;
	}
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @since 0.0.2
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = super.hashCode();
		
		result = prime * result + ((this.scope == null) ? 0 : this.scope.hashCode());
		
		return result;
	}

	/**
	 * @see java.lang.Object#equals(java.lang.Object)
	 * @since 0.0.2
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (!super.equals(that))
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		CtxAttributeIdentifierBean other = (CtxAttributeIdentifierBean) that;
		if (this.scope == null) {
			if (other.scope != null)
				return false;
		} else if (!this.scope.equals(other.scope))
			return false;
		
		return true;
	}

	/**
	 * Formats the string representation of a context attribute identifier as follows:
	 * <pre> 
	 * scope/ATTRIBUTE/type/objectNumber
	 * </pre>
	 * 
	 * @see CtxIdentifierBean#defineString()
	 */
	@Override
	protected void defineString() {

		if (super.string != null) 
			return;

		final StringBuilder sb = new StringBuilder();

		sb.append(this.scope);
		sb.append("/");
		sb.append(CtxModelTypeBean.ATTRIBUTE);
		sb.append("/");
		sb.append(super.type);
		sb.append("/");
		sb.append(super.objectNumber);

		super.string = sb.toString();
	}

	/**
	 * Parses the string form of a context attribute identifier as follows:
	 * <pre> 
	 * scope/ATTRIBUTE/type/objectNumber
	 * </pre>
	 * 
	 * @see CtxIdentifierBean#parseString(java.lang.String)
	 */
	@Override
	protected void parseString(String input)
			throws MalformedCtxIdentifierException {
	
		super.string = input;

		final int length = input.length();

		final int objectNumberDelim = input.lastIndexOf("/");
		if (objectNumberDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String objectNumberStr = input.substring(objectNumberDelim+1, length);
		try { 
			super.objectNumber = new Long(objectNumberStr);
		} catch (NumberFormatException nfe) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Invalid context attribute object number", nfe);
		}

		final int typeDelim = input.lastIndexOf("/", objectNumberDelim-1);
		super.type = input.substring(typeDelim+1, objectNumberDelim);
		if (super.type.isEmpty())
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Context attribute type cannot be empty");

		final int modelTypeDelim = input.lastIndexOf("/", typeDelim-1);
		if (modelTypeDelim == -1)
			throw new MalformedCtxIdentifierException("'" + input + "'");
		final String modelTypeStr = input.substring(modelTypeDelim+1, typeDelim);
		try {
			super.modelType = CtxModelTypeBean.valueOf(modelTypeStr);
		} catch (IllegalArgumentException iae) {
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Malformed context model type", iae);
		}
		if (!CtxModelTypeBean.ATTRIBUTE.equals(super.modelType))
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Expected 'ATTRIBUTE' but found '"
					+ super.modelType + "'");

		final String scopeStr = input.substring(0, modelTypeDelim);
		if (scopeStr.isEmpty())
			throw new MalformedCtxIdentifierException("'" + input 
					+ "': Context attribute scope cannot be empty");
		try { 
			this.scope = new CtxEntityIdentifierBean(scopeStr);
		} catch (MalformedCtxIdentifierException mcie) {
			throw new MalformedCtxIdentifierException("'" + input
					+ "': Malformed context attribute scope", mcie);
		}
		super.operatorId = this.scope.getOperatorId();
	}
}