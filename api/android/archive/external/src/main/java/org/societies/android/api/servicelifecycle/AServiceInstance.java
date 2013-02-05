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
package org.societies.android.api.servicelifecycle;

import org.societies.api.schema.servicelifecycle.model.ServiceInstance;
import android.os.Parcel;
import android.os.Parcelable;

public class AServiceInstance extends ServiceInstance implements Parcelable {

	private static final long serialVersionUID = 8132643014462359734L;

	public AServiceImplementation getServiceImpl() {
		return AServiceImplementation.convertServiceImplementation(super.getServiceImpl());
	}

	public void setServiceImpl(AServiceImplementation aserviceImpl) {
		super.setServiceImpl(aserviceImpl);
	}
	
	public AServiceResourceIdentifier getParentIdentifier() {
		return AServiceResourceIdentifier.convertServiceResourceIdentifier(super.getParentIdentifier());
	}

	public void setParentIdentifier(AServiceResourceIdentifier aParentId) {
		super.setParentIdentifier(aParentId);
	}
	
	public AServiceInstance() {
		super();
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#describeContents() */
	public int describeContents() {
		return 0;
	}

	/* (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int) */
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(this.getCssJid());
		dest.writeString(this.getFullJid());
		dest.writeString(this.getParentJid());
		dest.writeString(this.getXMPPNode());
		dest.writeParcelable(this.getServiceImpl(), flags);
		dest.writeParcelable(this.getParentIdentifier(), flags);
	}
	
	private AServiceInstance(Parcel in) {
		super();
		this.setCssJid(in.readString());
		this.setFullJid(in.readString());
		this.setParentJid(in.readString());
		this.setXMPPNode(in.readString());
		this.setServiceImpl( (AServiceImplementation)in.readParcelable(this.getClass().getClassLoader()) );
		this.setParentIdentifier((AServiceResourceIdentifier) in.readParcelable(this.getClass().getClassLoader()) );
	}

	public static final Parcelable.Creator<AServiceInstance> CREATOR = new Parcelable.Creator<AServiceInstance>() {
		public AServiceInstance createFromParcel(Parcel in) {
			return new AServiceInstance(in);
		}

		public AServiceInstance[] newArray(int size) {
			return new AServiceInstance[size];
		}
	};
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((this.getCssJid() == null) ? 0 : this.getCssJid().hashCode());
		result = prime * result + ((this.getFullJid() == null) ? 0 : this.getFullJid().hashCode());
		result = prime * result + ((this.getParentJid() == null) ? 0 : this.getParentJid().hashCode());
		result = prime * result + ((this.getXMPPNode() == null) ? 0 : this.getXMPPNode().hashCode());
		result = prime * result + ((this.getServiceImpl() == null) ? 0 : this.getServiceImpl().hashCode());
		result = prime * result + ((this.getParentIdentifier() == null) ? 0 : this.getParentIdentifier().hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		
		if (obj == null)
			return false;
		
		if (getClass() != obj.getClass())
			return false;
		
		AServiceInstance other = (AServiceInstance) obj;
		//getCssJid()
		if (this.getCssJid() == null) {
			if (other.getCssJid() != null)
				return false;
		} else if (!this.getCssJid().equals(other.getCssJid()))
			return false;
		//getFullJid()
		if (this.getFullJid() == null) {
			if (other.getFullJid() != null)
				return false;
		} else if (!this.getFullJid().equals(other.getFullJid()))
			return false;
		//getParentJid()
		if (this.getParentJid() == null) {
			if (other.getParentJid() != null)
				return false;
		} else if (!this.getParentJid().equals(other.getParentJid()))
			return false;
		//getXMPPNode()
		if (this.getXMPPNode() == null) {
			if (other.getXMPPNode() != null)
				return false;
		} else if (!this.getXMPPNode().equals(other.getXMPPNode()))
			return false;
		//getServiceImpl()
		if (this.getServiceImpl() == null) {
			if (other.getServiceImpl() != null)
				return false;
		} else if (!this.getServiceImpl().equals(other.getServiceImpl()))
			return false;
		//getParentIdentifier()
		if (this.getParentIdentifier() == null) {
			if (other.getParentIdentifier() != null)
				return false;
		} else if (!this.getParentIdentifier().equals(other.getParentIdentifier()))
			return false;
		
		return true;
	}
	
	public static AServiceInstance convertServiceInstance(ServiceInstance servIns) {
		AServiceInstance aservIns = new AServiceInstance();
		aservIns.setCssJid(servIns.getCssJid());
		aservIns.setFullJid(servIns.getFullJid());
		aservIns.setParentJid(servIns.getParentJid());
		aservIns.setXMPPNode(servIns.getXMPPNode());
		aservIns.setServiceImpl(AServiceImplementation.convertServiceImplementation(servIns.getServiceImpl()));
		aservIns.setParentIdentifier(AServiceResourceIdentifier.convertServiceResourceIdentifier(servIns.getParentIdentifier())); 
		
		return aservIns;
	}
	
	public static ServiceInstance convertAServiceInstance(AServiceInstance aservIns) {
		ServiceInstance servIns = new ServiceInstance();
		servIns.setCssJid(aservIns.getCssJid());
		servIns.setFullJid(aservIns.getFullJid());
		servIns.setParentJid(aservIns.getParentJid());
		servIns.setXMPPNode(aservIns.getXMPPNode());
		servIns.setServiceImpl(AServiceImplementation.convertAServiceImplementation(aservIns.getServiceImpl()));
		servIns.setParentIdentifier(AServiceResourceIdentifier.convertAServiceResourceIdentifier(aservIns.getParentIdentifier())); 
		
		return servIns;
	}
}
