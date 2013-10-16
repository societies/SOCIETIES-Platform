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
package org.societies.privacytrust.privacyprotection.privacypolicy;

import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAssociation;
import org.societies.api.context.model.CtxAssociationIdentifier;
import org.societies.api.context.model.CtxAssociationTypes;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxEntity;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxEntityTypes;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.util.SerialisationHelper;
import org.societies.api.identity.util.RequestorUtils;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.privacytrust.privacy.model.PrivacyException;
import org.societies.api.schema.identity.RequestorBean;
import org.societies.api.schema.privacytrust.privacy.model.privacypolicy.RequestPolicy;
import org.societies.privacytrust.privacyprotection.api.IPrivacyPolicyRegistryManager;
import org.societies.privacytrust.privacyprotection.privacypolicy.registry.PrivacyPolicyRegistry;

/**
 * @author Elizabeth
 * @author Olivier Maridat (Trialog)
 */
public class PrivacyPolicyRegistryManager implements IPrivacyPolicyRegistryManager {
	private static final Logger LOG = LoggerFactory.getLogger(PrivacyPolicyRegistryManager.class.getName());

	private ICtxBroker ctxBroker;
	private ICommManager commManager;
	private PrivacyPolicyRegistry policyRegistry;


	public PrivacyPolicyRegistryManager(ICtxBroker ctxBroker, ICommManager commManager) {
		this.ctxBroker = ctxBroker;
		this.commManager = commManager;
		loadPrivacyPolicyRegistry();
	}


	@Override
	public RequestPolicy getPrivacyPolicy(RequestorBean owner) throws PrivacyException {
		RequestPolicy policy = null;
		// -- Loading
		if (null == policyRegistry) {
			LOG.debug("Registry empty: loading privacy policies");
			this.loadPrivacyPolicyRegistry();
		}

		// -- Retrieve privacy policy
		// Retrieve Context Id
		CtxIdentifier id = this.policyRegistry.getPolicyStorageID(owner);
		if (null == id){
			LOG.error("Requestor: "+RequestorUtils.toString(owner)+" has not provided a privacy policy document");
			return policy;
		}
		// Retrieve in context
		try {
			CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(id).get();
			if (null == ctxAttr) {
				LOG.error("CtxAttr obj is null");
				return policy;
			}
			policy = (RequestPolicy) SerialisationHelper.deserialise(ctxAttr.getBinaryValue(), this.getClass().getClassLoader());
			if (null == policy) {
				LOG.error("Can't deserialize the retrieved privacy policy: {}", ctxAttr.getBinaryValue().toString());
				return policy;
			}
		} catch (Exception e) {
			throw new PrivacyException("Can't retrieve the privacy policy", e);
		}
		return policy;
	}

	@Override
	public boolean updatePrivacyPolicy(RequestorBean owner, RequestPolicy privacyPolicy) throws PrivacyException {
		if (null == policyRegistry){
			LOG.debug("Registry empty: loading privacy policies");
			loadPrivacyPolicyRegistry();
		}

		// Global store
		CtxIdentifier id = this.storePrivacyPolicyToCtx(owner, privacyPolicy);
		if (null == id) {
			throw new PrivacyException("Can't retrieve the Context ID of the stored privacy policy");
		}
		if (null == owner) {
			owner = privacyPolicy.getRequestor();
		}
		this.policyRegistry.addPolicy(owner, id);
		this.storePrivacyPolicyRegistry();
		return true;
	}

	@Override
	public boolean deletePrivacyPolicy(RequestorBean owner) throws PrivacyException {
		// -- No privacy policy in the registry
		if (null == policyRegistry){
			return true;
		}
		// -- Retrieve privacy policy id
		CtxIdentifier privacyPolicyId = policyRegistry.getPolicyStorageID(owner);
		// No privacy policy
		if (null == privacyPolicyId) {
			return true;
		}
		// Remove it
		try {
			ctxBroker.remove(privacyPolicyId).get();
		} catch (Exception e) {
			throw new PrivacyException("Error during privacy policy deletion: "+e.getMessage(), e);
		}
		policyRegistry.removePolicy(owner);
		return true;
	}

	private CtxIdentifier storePrivacyPolicyToCtx(RequestorBean requestor, RequestPolicy policy){
		try {
			String name = "policyOf"+RequestorUtils.toUriString(requestor);
			//TODO: The name might cause an error. We might need to provide different names for storing the policies as attributes in DB
			List<CtxIdentifier> ctxIDs = ctxBroker.lookup(CtxModelType.ATTRIBUTE, name).get();
			if (ctxIDs.size()==0){
				List<CtxIdentifier> entityIDs = ctxBroker.lookup(CtxModelType.ENTITY, CtxEntityTypes.PRIVACY_POLICY).get();
				if (entityIDs.size()==0){
					CtxEntity person = ctxBroker.retrieveIndividualEntity(commManager.getIdManager().getThisNetworkNode()).get();
					if (person==null){
						return null;
					}
					Set<CtxAssociationIdentifier> assocIDs = person.getAssociations(CtxAssociationTypes.HAS_PRIVACY_POLICIES);
					CtxAssociation assoc;
					if (assocIDs.size()==0){
						assoc = ctxBroker.createAssociation(CtxAssociationTypes.HAS_PRIVACY_POLICIES).get();
						assoc.setParentEntity(person.getId());
					}else{
						assoc = (CtxAssociation) ctxBroker.retrieve(assocIDs.iterator().next()).get();
					}
					CtxEntity policyEntity = ctxBroker.createEntity(CtxEntityTypes.PRIVACY_POLICY).get();
					assoc.addChildEntity(policyEntity.getId());
					ctxBroker.update(assoc);
					entityIDs.add(policyEntity.getId());

				}
				CtxAttribute ctxAttr = ctxBroker.createAttribute((CtxEntityIdentifier) entityIDs.get(0), name).get();
				ctxAttr.setBinaryValue(SerialisationHelper.serialise(policy));
				ctxBroker.update(ctxAttr);
				return ctxAttr.getId();


			}else{
				CtxAttribute ctxAttr = (CtxAttribute) ctxBroker.retrieve(ctxIDs.get(0)).get();
				ctxAttr.setBinaryValue(SerialisationHelper.serialise(policy));
				ctxBroker.update(ctxAttr);
				return ctxAttr.getId();
			}
		} catch (Exception e) {
			LOG.error("Error during storage of the privacy policy: "+e.getMessage(), e);
		}	
		return null;
	}
	/**
	 * method to load the policies from context
	 */
	private void loadPrivacyPolicyRegistry(){
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
			if (null != attrList && !attrList.isEmpty()) {
				CtxIdentifier identifier = attrList.get(0);
				CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();
				this.policyRegistry = (PrivacyPolicyRegistry) SerialisationHelper.deserialise(attr.getBinaryValue(), this.getClass().getClassLoader());
			}
		} catch (Exception e) {
			LOG.error("Error when retrieving privacy policies from context DB. Use empty registry.", e);
		}
		finally {
			if (null == policyRegistry) {
				this.policyRegistry = new PrivacyPolicyRegistry();
			}
		}

	}

	/**
	 * method to store the policies currently in the registry in context
	 */
	private void storePrivacyPolicyRegistry(){
		try {
			List<CtxIdentifier> attrList = ctxBroker.lookup(CtxModelType.ATTRIBUTE, org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
			if (attrList.size()>0){
				CtxIdentifier identifier = attrList.get(0);
				CtxAttribute attr = (CtxAttribute) ctxBroker.retrieve(identifier).get();
				attr.setBinaryValue(SerialisationHelper.serialise(this.policyRegistry));
				ctxBroker.update(attr);
			}else{
				CtxEntity operator = ctxBroker.retrieveIndividualEntity(commManager.getIdManager().getThisNetworkNode()).get();
				CtxAttribute attr = ctxBroker.createAttribute(operator.getId(), org.societies.api.internal.context.model.CtxAttributeTypes.PRIVACY_POLICY_REGISTRY).get();
				attr.setBinaryValue(SerialisationHelper.serialise(this.policyRegistry));
				ctxBroker.update(attr);
			}
		} catch (Exception e) {
			LOG.error("Error during storage of the privacy policy identities registry: "+e.getMessage(), e);
		}	
	}
}

