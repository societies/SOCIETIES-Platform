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
package org.societies.privacytrust.trust.impl.repo.model;

import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.Sort;
import org.hibernate.annotations.SortType;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence;
import org.societies.privacytrust.trust.api.model.ITrustedCis;
import org.societies.privacytrust.trust.api.model.ITrustedCss;
import org.societies.privacytrust.trust.api.model.ITrustedService;
import org.societies.privacytrust.trust.impl.evidence.repo.model.TrustEvidence;

/**
 * This class represents trusted CSSs. A <code>TrustedCss</code> object is
 * referenced by its {@link TrustedEntityId}, while the associated 
 * {@link Trust} value objects express the trustworthiness of this CSS, i.e.
 * direct, indirect and user-perceived. Each trusted CSS is assigned a set of
 * {@link TrustedCis} objects representing the communities this CSS is member
 * of. In addition, the services provided by a TrustedCss are modelled as
 * {@link TrustedService} objects.
 * <p>
 * Note: this class has a natural ordering that is inconsistent with equals.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
@Entity
@org.hibernate.annotations.Entity(
		dynamicInsert=true,
		dynamicUpdate=true
)
@Table(
		name = TableName.TRUSTED_CSS, 
		uniqueConstraints = { @UniqueConstraint(columnNames = { "trustor_id", "trustee_id" }) }
)
public class TrustedCss extends TrustedEntity implements ITrustedCss {
	
	private static final long serialVersionUID = 6564159563124215460L;
	
	/** The trust evidence associated with the evaluated trust values of this CSS. */
	@ManyToMany(
			cascade = CascadeType.MERGE,
			targetEntity = TrustEvidence.class,
			fetch = FetchType.EAGER
	)
    @JoinTable(
    		name = TableName.TRUSTED_CSS + "_evidence",
    		joinColumns = { @JoinColumn(name = TableName.TRUSTED_CSS + "_id") },
    		inverseJoinColumns = { @JoinColumn(name = 
    		org.societies.privacytrust.trust.impl.evidence.repo.model.TableName.TRUST_EVIDENCE + "_id") }
    )
	@Sort(type=SortType.NATURAL)
	private final SortedSet<ITrustEvidence> evidence = new TreeSet<ITrustEvidence>();
	
	/** The communities this CSS is member of. */
	@ManyToMany(
			cascade = CascadeType.MERGE,
			targetEntity = TrustedCis.class,
			fetch = FetchType.EAGER
	)
	@JoinTable(
			name = TableName.TRUSTED_CSS_CIS, 
			joinColumns = { @JoinColumn(name = TableName.TRUSTED_CSS + "_id") }, 
			inverseJoinColumns = { @JoinColumn(name = TableName.TRUSTED_CIS + "_id") }
	)
	private final Set<ITrustedCis> communities = new HashSet<ITrustedCis>();
	
	/** The services provided by this CSS. */
	@OneToMany(
			cascade = CascadeType.REMOVE,
			mappedBy = "provider",
			targetEntity = TrustedService.class,
			fetch = FetchType.EAGER
	)
	private final Set<ITrustedService> services = new HashSet<ITrustedService>();
	
	/** The similarity between the trustor and the trustee. */
	@Index(name = "similarity_idx")
	@Column(name = "similarity")
	private Double similarity = 0.0d;

	/* Empty constructor required by Hibernate */
	private TrustedCss() {
		
		super(null, null);
	}
	
	/**
	 * Constructs a <code>TrustedCss</code> with the specified trustor and
	 * trustee identifiers.
	 * 
	 * @param trustorId
	 *            the identifier of the trustor
	 * @param trusteeId
	 *            the identifier of the trustee
	 * @since 0.5
	 */
	public TrustedCss(final TrustedEntityId trustorId, final TrustedEntityId trusteeId) {
		
		super(trustorId, trusteeId);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#getEvidence()
	 */
	@Override
	public SortedSet<ITrustEvidence> getEvidence() {
		
		return this.evidence;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#addEvidence(org.societies.privacytrust.trust.api.evidence.model.ITrustEvidence)
	 */
	@Override
	public void addEvidence(final ITrustEvidence evidence) {
		
		if (!this.evidence.contains(evidence))
			this.evidence.add(evidence);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#removeEvidence(org.societies.api.privacytrust.trust.model.TrustEvidence)
	 */
	@Override
	public void removeEvidence(final ITrustEvidence evidence) {
		
		if (this.evidence.contains(evidence))
			this.evidence.remove(evidence);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#getCommunities()
	 */
	@Override
	public Set<ITrustedCis> getCommunities(){
		
		return this.communities;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#addCommunity(org.societies.privacytrust.trust.api.model.ITrustedCis)
	 */
	@Override
	public void addCommunity(final ITrustedCis community) {
		
		if (!this.communities.contains(community))
			this.communities.add(community);
	
		if (!community.getMembers().contains(this))
			community.getMembers().add(this);
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#removeCommunity(org.societies.privacytrust.trust.api.model.ITrustedCis)
	 */
	@Override
	public void removeCommunity(final ITrustedCis community) {
		
		if (this.communities.contains(community))
			this.communities.remove(community);
		
		if (community.getMembers().contains(this))
			community.getMembers().remove(this);
	}

	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#getServices()
	 */
	@Override
	public Set<ITrustedService> getServices() {
		
		return this.services;
	}
	
	/*
	 * TODO 
	 * @param serviceType
	 *
	public Set<TrustedService> getServices(String serviceType) {
		return null;
	}*/
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#getSimilarity()
	 */
	@Override
	public Double getSimilarity() {
		
		return (this.similarity != null) ? new Double(this.similarity) : null;
	}
	
	/*
	 * @see org.societies.privacytrust.trust.api.model.ITrustedCss#setSimilarity(java.lang.Double)
	 */
	@Override
	public void setSimilarity(Double similarity) {
		
		this.similarity = similarity;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append("(");
		sb.append("trustorId=" + super.getTrustorId());
		sb.append(",");
		sb.append("trusteeId=" + super.getTrusteeId());
		sb.append(",");
		sb.append("directTrust=" + super.getDirectTrust());
		sb.append(",");
		sb.append("indirectTrust=" + super.getIndirectTrust());
		sb.append(",");
		sb.append("userPerceivedTrust=" + super.getUserPerceivedTrust());
		sb.append(",");
		sb.append("similarity=" + this.similarity);
		sb.append(")");
		
		return sb.toString();
	}
}