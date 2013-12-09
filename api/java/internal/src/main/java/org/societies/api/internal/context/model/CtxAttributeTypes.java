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
package org.societies.api.internal.context.model;

import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.internal.css.CSSManagerEnums;
import org.societies.api.privacytrust.privacy.util.privacypolicy.PrivacyPolicyUtils;

/**
 * This class defines common {@link CtxAttribute context attribute} types in
 * addition to the ones defined in {@link org.societies.api.context.model.CtxAttributeTypes}.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.7
 */
public class CtxAttributeTypes extends
org.societies.api.context.model.CtxAttributeTypes {


	/**
	 * @since 0.0.8
	 */
	public static final String CAUI_MODEL = "caui_model";

	/**
	 * String used in {@link PrivacyPolicyUtils} (external API). If you change this value, please, change it also in {@link PrivacyPolicyUtils}.
	 * @since 0.0.8
	 */
	public static final String CACI_MODEL = "caci_model";

	
	/**
	 * 
	 */
	public static final String CRIST_MODEL = "crist_model";
	
	/**
	 * The value of this attribute type contains the domain server of a CSS.
	 * The attribute is associated with the {@link IndividualCtxEntity}
	 * representing that particular CSS.
	 * <p>
     * Possible value types: {@link CtxAttributeValueType#STRING}.
	 * 
	 * @since 1.0
	 */
	public static final String CSS_DOMAIN_SERVER = "cssDomainServer";
	
	/**
	 * The value of this attribute type determines the status of a
	 * {@link CtxEntityTypes#CSS_NODE CSS_NODE} context entity.
	 * <p>
     * Possible value types: {@link CtxAttributeValueType#INTEGER}.
	 * <p>
     * Possible values: The ordinals of the enums defined in
     * {@link CSSManagerEnums.nodeStatus}.
	 * 
	 * @since 1.0
	 */
	public static final String CSS_NODE_STATUS = "cssNodeStatus";
	
	/**
	 * The value of this attribute type determines the type of a
	 * {@link CtxEntityTypes#CSS_NODE CSS_NODE} context entity.
	 * <p>
     * Possible value types: {@link CtxAttributeValueType.INTEGER}.
	 * <p>
     * Possible values: The ordinals of the enums defined in
     * {@link CSSManagerEnums.nodeType}.
	 * 
	 * @since 1.0
	 */
	public static final String CSS_NODE_TYPE = "cssNodeType";

	/**
	 * @since 0.0.8
	 */
	public static final String D_NET = "dNet";

	
	/**
	 * 
	 */
	public static final String PARAMETER_NAME = "parameterName";   

	/**
	 *
	 */
	public static final String PRIVACY_POLICY_REGISTRY = "privacyPolicyRegistry";
	
	/**
	 *
	 */
	public static final String PRIVACY_POLICY = "privacyPolicy";
	
	/**
	 * @since 0.0.8
	 */
	public static final String SERVICE_PRIVACY_POLICY_REGISTRY = "servicePrivacyPolicyRegistry";

	/**
	 * @since 0.0.8
	 */
	public static final String SNAPSHOT_REG = "snapshotReg";
	
	/**
	 * @since 0.0.8
	 */
	public static final String UID = "uid";
	
	
	
	 
	/**
	 * @since 0.0.8
	 */
	public static final String SOCIAL_NETWORK_CONNECTOR = "socialNetworkConnector";

}