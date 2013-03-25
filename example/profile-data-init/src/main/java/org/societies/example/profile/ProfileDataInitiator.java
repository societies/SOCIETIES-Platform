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
package org.societies.example.profile;

import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.context.model.CtxAttribute;
import org.societies.api.context.model.CtxAttributeIdentifier;
import org.societies.api.context.model.CtxEntityIdentifier;
import org.societies.api.context.model.CtxIdentifier;
import org.societies.api.context.model.CtxModelType;
import org.societies.api.context.model.IndividualCtxEntity;
import org.societies.api.identity.IIdentity;
import org.societies.api.internal.context.broker.ICtxBroker;
import org.societies.api.internal.context.model.CtxAttributeTypes;
import org.societies.api.internal.privacytrust.trust.evidence.ITrustEvidenceCollector;
import org.societies.api.privacytrust.trust.evidence.TrustEvidenceType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * This class is used to initialise the user's profile. It assumes that for the
 * configured JID username there is a class extending the BaseUser which 
 * contains the initial data.
 * <p> 
 * For example, if the JID is configured as "jane.societies.local" there should
 * be a class named "jane" (case sensitive!) under the current package.
 * 
 * @author <a href="mailto:nikosk@cn.ntua.gr">Nikos Kalatzis</a> (ICCS)
 * @since 0.4
 */
@Service
public class ProfileDataInitiator {

	/** The logging facility. */
	private static final Logger LOG = LoggerFactory.getLogger(ProfileDataInitiator.class);

	/** The internal Context Broker service. */
	private ICtxBroker ctxBroker;
	
	/** The internal Trust Evidence Collector service. */
	private ITrustEvidenceCollector trustEvidenceCollector;

	/** The Comm Mgr service. */
	private ICommManager commMgr;

	private IIdentity cssOwnerId;

	@Autowired(required=true)
	ProfileDataInitiator(ICtxBroker ctxBroker, ITrustEvidenceCollector trustEvidenceCollector, ICommManager commMgr) 
		throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");

		this.ctxBroker = ctxBroker;
		this.trustEvidenceCollector = trustEvidenceCollector;
		this.commMgr = commMgr;

		try {
			final String cssOwnerIdStr = this.commMgr.getIdManager().getThisNetworkNode().getBareJid();
			this.cssOwnerId = this.commMgr.getIdManager().fromJid(cssOwnerIdStr);	

			// extract the username from the JID, e.g. "jane.societies.local" gives "jane"
			final String username = cssOwnerIdStr.split("\\.")[0];
			final BaseUser user = (BaseUser) Class.forName(
					ProfileDataInitiator.class.getPackage().getName() + "." + username).newInstance();
			// Initialise Context
			this.initContext(user);
			// Initialise Trust
			this.initTrust(user);
		} catch (Exception e) {
			LOG.error("Failed to initialise profile: " + e.getLocalizedMessage(), e);
			throw e;
		}
	}

	private void initContext(BaseUser user) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info("Updating initial context values");
		
		final CtxEntityIdentifier ownerCtxId = 
				this.ctxBroker.retrieveIndividualEntity(this.cssOwnerId).get().getId();
		
		String value;
		
		// AGE
		value = user.getAge();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.AGE, value);

		// BIRTHDAY
		value = user.getBirthday();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.BIRTHDAY, value);

		// EMAIL
		value = user.getEmail();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.EMAIL, value);

		// FRIENDS
		value = user.getFriends();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.FRIENDS, value);

		// INTERESTS
		value = user.getInterests();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.INTERESTS, value);

		// LANGUAGES
		value = user.getLanguages();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LANGUAGES, value);

		// LOCATION_COORDINATES
		value = user.getLocationCoordinates();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LOCATION_COORDINATES, value);

		// LOCATION_SYMBOLIC
		value = user.getLocationSymbolic();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.LOCATION_SYMBOLIC, value);

		// MOVIES
		value = user.getMovies();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.MOVIES, value);

		// NAME
		value = user.getName();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.NAME, value);

		// OCCUPATION
		value = user.getOccupation();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.OCCUPATION, value);

		// POLITICAL_VIEWS
		value = user.getPoliticalViews();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.POLITICAL_VIEWS, value);

		// SEX
		value = user.getSex();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SEX, value);

		// STATUS
		value = user.getStatus();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.STATUS, value);

		// SKILLS	
		value = user.getSkills();
		if (value != null && !value.isEmpty())
			this.updateCtxAttribute(ownerCtxId, CtxAttributeTypes.SKILLS, value);

		this.printCtxAttributes(ownerCtxId);
	}

	private void updateCtxAttribute(CtxEntityIdentifier ownerCtxId, 
			String type, String value) throws Exception {

		if (LOG.isInfoEnabled())
			LOG.info("Updating '" + type + "' of entity " + ownerCtxId + " to '" + value + "'");

		final List<CtxIdentifier> ctxIds = 
				this.ctxBroker.lookup(ownerCtxId, CtxModelType.ATTRIBUTE, type).get();
		if (!ctxIds.isEmpty()) {
			this.ctxBroker.updateAttribute((CtxAttributeIdentifier) ctxIds.get(0), value);
		} else {
			final CtxAttribute attr = this.ctxBroker.createAttribute(ownerCtxId, type).get();
			this.ctxBroker.updateAttribute(attr.getId(), value);
		}
	}

	private void printCtxAttributes(CtxEntityIdentifier ownerCtxId) throws Exception {
		
		final IndividualCtxEntity entity = (IndividualCtxEntity) this.ctxBroker.retrieve(ownerCtxId).get();
		Set<CtxAttribute> attributes = entity.getAttributes();
		
		LOG.info("CtxEntity :"+entity.getId() );
		for(CtxAttribute attr : attributes){
			LOG.info("CtxAttribute :"+ attr.getId() + " value " + attr.getStringValue());	
		}	
	}
	
	private void initTrust(BaseUser user) throws Exception {
		
		final String trustorId = this.cssOwnerId.getBareJid();
		
		String[] trustees;
		
		trustees = user.getFullyTrustedUsers();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CSS, trustee, 1.0d);
		}
	
		trustees = user.getMarginallyTrustedUsers();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CSS, trustee, 0.5d);
		}
		
		trustees = user.getNonTrustedUsers();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CSS, trustee, 0.0d);
		}
		
		trustees = user.getFullyTrustedCommunities();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CIS, trustee, 1.0d);
		}
	
		trustees = user.getMarginallyTrustedCommunities();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CIS, trustee, 0.5d);
		}
		
		trustees = user.getNonTrustedCommunities();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.CIS, trustee, 0.0d);
		}
		
		trustees = user.getFullyTrustedServices();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.SVC, trustee, 1.0d);
		}
	
		trustees = user.getMarginallyTrustedServices();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.SVC, trustee, 0.5d);
		}
		
		trustees = user.getNonTrustedServices();
		if (trustees != null) for (final String trustee : trustees) {
			this.updateTrustRating(trustorId, TrustedEntityType.SVC, trustee, 0.0d);
		}
	}
	
	private void updateTrustRating(String trustorId, TrustedEntityType trusteeType,
			String trusteeId, double rating) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info("CSS '" + trustorId + "' assigned " + trusteeType + " '"
					+ trusteeId + "' a trust rating of " + rating);
		final TrustedEntityId trustorTeid = new TrustedEntityId(
				TrustedEntityType.CSS, trustorId);
		final TrustedEntityId trusteeTeid = new TrustedEntityId(
				trusteeType, trusteeId);
		this.trustEvidenceCollector.addDirectEvidence(trustorTeid, trusteeTeid,
				TrustEvidenceType.RATED, new Date(), rating);
	}
}