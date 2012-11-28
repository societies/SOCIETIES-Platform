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

import java.util.Date;

import org.societies.api.context.model.CtxModelType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Base class for representing context model objects. This class defines methods
 * for accessing information common to all <code>ACtxModelOject</code>
 * implementations. More specifically, every context model object can be referenced
 * by its {@link ACtxIdentifier}. In addition, upon modification of a ACtxModelOject
 * the last modification time is updated.   
 * 
 * @see CtxModelType
 * @author <a href="mailto:nicolas.liampotis@cn.ntua.gr">Nicolas Liampotis</a> (ICCS)
 * @since 0.0.1
 */
public abstract class ACtxModelObject implements Parcelable {

	/** The identifier of this context model object. */
	private ACtxIdentifier id;
	
	/** The last modification time of this context model object. */
	private Date lastModified = new Date();


	/**
	 * Making class Parcelable
	 */

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
//      out.writeInt(mData);
      out.writeParcelable((Parcelable) this.getId(), flags);
      out.writeLong(lastModified.getTime());
      
  }

/*    public static final Parcelable.Creator<ACtxModelObject> CREATOR = new Parcelable.Creator<ACtxModelObject>() {
        public ACtxModelObject createFromParcel(Parcel in) {
            return new ACtxModelObject(in);
        }

        public ACtxModelObject[] newArray(int size) {
            return new ACtxModelObject[size];
        }
    };*/
       
    protected ACtxModelObject(Parcel in) {
//        mData = in.readInt();
    	id = in.readParcelable(ACtxIdentifier.class.getClassLoader());
    	lastModified.setTime(in.readLong());
    }
    
	
	/**
	 * Constructs a ACtxModelObject with the specified identifier
	 * 
	 * @param id
	 *            the identifier of the newly created context model object
	 */
	protected ACtxModelObject(ACtxIdentifier id) {
		
		this.id = id;
	}

	/**
	 * Returns the identifier of this context model object
     * 
     * @return the identifier of this context model object
     * @see ACtxIdentifier
	 */
	public ACtxIdentifier getId(){
		
		return this.id;
	}

	/**
	 * Returns the last modification time of this context model object
	 * 
	 * @return the last modification time of this context model object
	 */
	public Date getLastModified(){
		
		return this.lastModified;
	}
	
	void setLastModified(Date lastModified) {
		
		this.lastModified = lastModified;
	}
	
	/**
	 * Returns the IIdentity String representation of the CSS or CIS where the
	 * identified context model object is stored
     * 
	 * @return the IIdentity String representation of the CSS or CIS where the
	 *             identified context model object is stored
	 * @see ACtxIdentifier#getOwnerId()
	 */
	public String getOwnerId() {
		
	    return this.getId().getOwnerId();
	}
	
	/**
	 * Returns the model type of this context model object, i.e. Entity,
	 * Attribute or Association
	 * 
	 * @return the enum constant for the context model type
	 * @see ACtxIdentifier#getModelType()
	 */
	public CtxModelType getModelType() {
		
	    return this.getId().getModelType();
	}
	
	/**
	 * Returns the semantic tag (e.g. "person") of this context model object
	 * 
	 * @return the semantic tag of this context model object
	 * @see ACtxIdentifier#getType()
	 */
	public String getType() {
		
		return this.getId().getType();
	}
	
	/**
	 * Returns the numeric part of this context model object identifier 
	 * 
	 * @return the numeric part of this context model object identifier
	 * @see ACtxIdentifier#getObjectNumber()
	 */
	public Long getObjectNumber() {
		
		return this.getId().getObjectNumber();
	}
	
	/* TODO
	 * Returns a String representation of this context model object
     * 
     * @return a String representation of this context model object
     * 
	@Override
	public String toString() {
	}*/
	
	/**
	 * @see java.lang.Object#hashCode()
	 * @since 0.0.2
	 */
	@Override
    public int hashCode() {
		
        final int prime = 31;
        int result = 1;
        
        result = prime * result
                + ((this.id == null) ? 0 : this.id.hashCode());
        
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
        if (that == null)
            return false;
        if (this.getClass() != that.getClass())
            return false;
        
        ACtxModelObject other = (ACtxModelObject) that;
        if (this.id == null) {
            if (other.id != null)
                return false;
        } else if (!this.id.equals(other.id))
            return false;
        
        return true;
    }
    
}