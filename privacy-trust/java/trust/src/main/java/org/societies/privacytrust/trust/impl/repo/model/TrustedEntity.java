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

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.Columns;
import org.hibernate.annotations.Type;
import org.societies.api.privacytrust.trust.model.TrustedEntityId;
import org.societies.privacytrust.trust.api.model.IDirectTrust;
import org.societies.privacytrust.trust.api.model.IIndirectTrust;
import org.societies.privacytrust.trust.api.model.ITrustedEntity;
import org.societies.privacytrust.trust.api.model.IUserPerceivedTrust;

/**
 * This abstract class is used to represent an entity trusted by the trustor,
 * i.e. the owner of a CSS. Each trusted entity is referenced by its
 * {@link TrustedEntityId}, while the associated {@link Trust} objects express
 * the trustworthiness of that entity, i.e. direct, indirect and user-perceived
 * trust.
 * 
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1 
 */
@MappedSuperclass
public abstract class TrustedEntity implements ITrustedEntity {
	
	private static final long serialVersionUID = -6065344074845695865L;

	/* The surrogate key used by Hibernate. */
	@Id
	@GeneratedValue
	@Column(name = "id")
	@SuppressWarnings("unused")
	private long id;
	
	/** The identifier of this trusted entity. */
	@Columns(columns = {
			@Column(name = "trustor_id", nullable = false, updatable = false, length = 256),
			@Column(name = "trustee_id", nullable = false, updatable = false, length = 256)
	})
	@Type(type = "org.societies.privacytrust.trust.impl.repo.model.hibernate.TrustedEntityIdUserType")
	private final TrustedEntityId teid;
	
	/** The direct trust in this entity. */
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "direct_trust_value")),
        @AttributeOverride(name = "lastModified", column = @Column(name = "direct_trust_last_modified")),
        @AttributeOverride(name = "lastUpdated", column = @Column(name = "direct_trust_last_updated"))
	})
	private DirectTrust directTrust = new DirectTrust();
	
	/** The indirect trust in this entity. */
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "indirect_trust_value")),
        @AttributeOverride(name = "lastModified", column = @Column(name = "indirect_trust_last_modified")),
        @AttributeOverride(name = "lastUpdated", column = @Column(name = "indirect_trust_last_updated"))
	})
	private IndirectTrust indirectTrust = new IndirectTrust();
	
	/** The user-perceived trust in this entity. */
	@Embedded
	@AttributeOverrides({
        @AttributeOverride(name = "value", column = @Column(name = "user_perceived_trust_value")),
        @AttributeOverride(name = "lastModified", column = @Column(name = "user_perceived_trust_last_modified")),
        @AttributeOverride(name = "lastUpdated", column = @Column(name = "user_perceived_trust_last_updated"))
	})
	private UserPerceivedTrust userPerceivedTrust = new UserPerceivedTrust();

	TrustedEntity(final TrustedEntityId teid) {
		
		this.teid = teid;
	}
	
	/* (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#getTeid()
	 */
	@Override
	public TrustedEntityId getTeid() {
		
		return this.teid;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#getDirectTrust()
	 */
	@Override
	public IDirectTrust getDirectTrust() {
		
		// ugly hack - see https://issues.jboss.org/browse/HIBERNATE-50
		if (this.directTrust == null) this.directTrust = new DirectTrust();
		return this.directTrust;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#getIndirectTrust()
	 */
	@Override
	public IIndirectTrust getIndirectTrust() {
		
		// ugly hack - see https://issues.jboss.org/browse/HIBERNATE-50
		if (this.indirectTrust == null) this.indirectTrust = new IndirectTrust();
		return this.indirectTrust;
	}
	
	/*
	 * (non-Javadoc)
	 * @see org.societies.privacytrust.trust.api.model.ITrustedEntity#getUserPerceivedTrust()
	 */
	@Override
	public IUserPerceivedTrust getUserPerceivedTrust() {
		
		// ugly hack - see https://issues.jboss.org/browse/HIBERNATE-50
		if (this.userPerceivedTrust == null) this.userPerceivedTrust = new UserPerceivedTrust();
		return this.userPerceivedTrust;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.teid == null) ? 0 : this.teid.hashCode());
		
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object that) {
		
		if (this == that)
			return true;
		if (that == null)
			return false;
		if (this.getClass() != that.getClass())
			return false;
		
		final TrustedEntity other = (TrustedEntity) that;
		
		if (this.teid == null) {
			if (other.teid != null)
				return false;
		} else if (!this.teid.equals(other.teid))
			return false;
		
		return true;
	}
	
	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		
		final StringBuilder sb = new StringBuilder();
		
		sb.append("{");
		sb.append("teid=" + this.teid);
		sb.append(",");
		sb.append("directTrust=" + this.directTrust);
		sb.append(",");
		sb.append("indirectTrust=" + this.indirectTrust);
		sb.append(",");
		sb.append("userPerceivedTrust=" + this.userPerceivedTrust);
		sb.append("}");
		
		return sb.toString();
	}
}