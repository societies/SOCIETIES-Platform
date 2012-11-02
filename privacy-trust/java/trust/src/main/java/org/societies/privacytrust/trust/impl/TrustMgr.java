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
package org.societies.privacytrust.trust.impl;

import java.util.Collection;
import java.util.Dictionary;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.societies.api.comm.xmpp.interfaces.ICommManager;
import org.societies.api.identity.IIdentity;
import org.societies.api.identity.InvalidFormatException;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.api.privacytrust.trust.model.TrustedEntityType;
import org.societies.privacytrust.trust.api.ITrustMgr;
import org.societies.privacytrust.trust.api.TrustMgrException;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.repo.ITrustRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Describe your class here...
 *
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.4.1
 */
@Service
public class TrustMgr implements ITrustMgr {

	private static final Logger LOG = LoggerFactory.getLogger(TrustMgr.class);
	
	@Autowired(required=false)
	private ITrustRepository trustRepo;
	
	private ICommManager commMgr;
	
	private final Collection<TrustedEntityId> myTEIDs = new CopyOnWriteArraySet<TrustedEntityId>();
	
	@Autowired
	public TrustMgr(ICommManager commMgr) throws Exception {
		
		if (LOG.isInfoEnabled())
			LOG.info(this.getClass() + " instantiated");
		
		this.commMgr = commMgr;
		
		final Set<IIdentity> publicIds = this.commMgr.getIdManager().getPublicIdentities();
		for (final IIdentity publicId : publicIds) {
			final String publicIdStr = publicId.getBareJid();
			final TrustedEntityId publicTeid = new TrustedEntityId(
					publicIdStr, TrustedEntityType.CSS, publicIdStr);
			if (LOG.isInfoEnabled())
				LOG.info("Adding my TEID '" + publicTeid + "'");
			this.myTEIDs.add(publicTeid);
		}
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.ITrustMgr#isLocalId(org.societies.api.privacytrust.trust.model.TrustedEntityId)
	 */
	@Override
	public boolean isLocalId(final TrustedEntityId teid)
			throws TrustMgrException {
		
		final String trustorIdStr = teid.getTrustorId();
		try {
			final IIdentity trustorId = this.commMgr.getIdManager().fromJid(trustorIdStr);
			return this.commMgr.getIdManager().isMine(trustorId); // TODO compare with cloud node id???
		} catch (InvalidFormatException ife) {
			
			throw new TrustMgrException("Could not check if trustor id is local: "
					+ ife.getLocalizedMessage(), ife);
		}
	}

	/*
	 * @see org.societies.privacytrust.trust.api.ITrustMgr#getMyIds()
	 */
	@Override
	public Collection<TrustedEntityId> getMyIds()
			throws TrustMgrException {
		
		return new HashSet<TrustedEntityId>(this.myTEIDs);
	}
	
	/**
	 * This method is called when the {@link ITrustRepository} service is bound.
	 * 
	 * @param trustRepo
	 *            the {@link ITrustRepository} service that was bound
	 * @param props
	 *            the set of properties that the {@link ITrustRepository} service
	 *            was registered with
	 */
	public void bindTrustRepository(ITrustRepository trustRepo,	Dictionary<Object,Object> props) {
		
		if (LOG.isInfoEnabled())
			LOG.info("Binding service reference " + trustRepo);
		new Thread() {
			/*
			 * @see java.lang.Thread#run()
			 */
			@Override
		    public void run() {
				
				for (final TrustedEntityId myTeid : myTEIDs) {
					try {
						if (TrustMgr.this.trustRepo.retrieveEntity(myTeid) == null) {
							ITrustedCss myCss = (ITrustedCss) TrustMgr.this.trustRepo.createEntity(myTeid);
							myCss.getDirectTrust().setScore(1d); // TODO check
							myCss.getDirectTrust().setRating(1d);
							myCss = (ITrustedCss) TrustMgr.this.trustRepo.updateEntity(myCss);
							if (LOG.isInfoEnabled())
								LOG.info("Created my CSS entity: " + myCss);
						} 
					} catch (Exception e) {
						LOG.error("Could not create trusted CSS entity for TEID '"
								+ myTeid + "': " + e.getLocalizedMessage(), e);
					}
					
				}
		    }
		}.start();
	}
	
	/**
	 * This method is called when the {@link ITrustRepository} service is unbound.
	 * 
	 * @param trustRepo
	 *            the {@link ITrustRepository} service that was unbound
	 * @param props
	 *            the set of properties that the {@link ITrustRepository} service
	 *            was registered with
	 */
	public void unbindTrustRepository(ITrustRepository trustRepo, Dictionary<Object,Object> props) {
		
		LOG.info("Unbinding service reference " + trustRepo);
	}
}