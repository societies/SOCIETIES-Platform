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
package org.societies.android.api.context.model;

import java.util.HashSet;
import java.util.Set;

import android.os.Parcel;

/**
 * This abstract class is used in order to represent members of a
 * {@link ACommunityCtxEntity} (CIS). A <code>ACommunityMemberCtxEntity</code>
 * can be an individual or a sub-community, hence, there are two concrete
 * implementations of this class, namely {@link AIndividualCtxEntity} and
 * {@link ACommunityCtxEntity}. A ACommunityMemberCtxEntity may belong to
 * multiple communities, simultaneously. This class provides methods for
 * accessing and modifying these communities.
 * 
 * @see ACtxEntityIdentifier
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public abstract class ACommunityMemberCtxEntity extends ACtxEntity {
		
	/** The communities this entity is member of. */
	private Set<ACommunityCtxEntity> communities = new HashSet<ACommunityCtxEntity>();
	
	ACommunityMemberCtxEntity(ACtxEntityIdentifier id) {
		
		super(id);
	}

	/**
	 * Making class Parcelable
	 */

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
    	super.writeToParcel(out, flags);
    }

/*    public static final Parcelable.Creator<ACommunityMemberCtxEntity> CREATOR = new Parcelable.Creator<ACommunityMemberCtxEntity>() {
        public ACommunityMemberCtxEntity createFromParcel(Parcel in) {
            return new ACommunityMemberCtxEntity(in);
        }

        public ACommunityMemberCtxEntity[] newArray(int size) {
            return new ACommunityMemberCtxEntity[size];
        }
    };*/
       
    protected ACommunityMemberCtxEntity(Parcel in) {
    	super(in);
    }
    
	/**
	 * Returns a set with the community members.
	 * 
	 * @return set CommunityCtxEntity
	 */
	public Set<ACommunityCtxEntity> getCommunities() {
		
		return new HashSet<ACommunityCtxEntity>(this.communities);
	}
	
	/**
	 * Add a CommunityCtxEntity to the community
	 * 
	 * @param community
	 */
	public void addCommunity(ACommunityCtxEntity community) {
		
		this.communities.add(community);
	}
	
	/**
	 * Remove a CommunityCtxEntity from the community.
	 * 
	 * @param community
	 */
	public void removeCommunity(ACommunityCtxEntity community) {
		
		this.communities.remove(community);
	}
}