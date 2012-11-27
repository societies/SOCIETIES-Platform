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

import java.io.Serializable;

import org.societies.api.context.model.CtxAttributeValueType;
import org.societies.api.context.model.CtxBondOriginType;
import org.societies.api.context.model.CtxModelType;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * This class is used to represent context bonds among members of a 
 * {@link ACommunityCtxEntity}. Each bond refers to a {@link ACtxAttribute} of a
 * particular type that expresses a commonality shared by community members.
 * For example, a <code>ACtxAttributeBond</code> could be based on the context
 * attribute of type {@link CtxAttributeTypes#LOCATION_SYMBOLIC}. The
 * {@link CtxBondOriginType} is used to specify how the bond was identified.
 * More specifically, a context bond may have been manually set, discovered or
 * inherited.
 * <p>
 * In the following example, a bond is created to specify that all members of a
 * community share the same home city:
 * <pre>
 * ACtxAttributeBond locationBond = new ACtxAttributeBond(
 *     CtxAttributeTypes.ADDRESS_HOME_CITY, CtxBondOriginType.MANUALLY_SET);
 * locationBond.setValueType(CtxAttributeValueType.STRING);
 * locationBond.setMinValue("Athens");
 * locationBond.setMaxValue("Athens");
 * </pre>
 * To specify that community members are 18 years old or more the bond should
 * be created as follows:
 * <pre>
 * CtxAttributeBond ageBond = new CtxAttributeBond(
 *     CtxAttributeTypes.AGE, CtxBondOriginType.MANUALLY_SET);
 * ageBond.setValueType(CtxAttributeValueType.INTEGER);
 * ageBond.setMinValue(new Integer(18));
 * </pre>
 *
 * @since 0.4
 */
public class ACtxAttributeBond extends ACtxBond {

	/** The type of the context attribute value this bond refers to. */
	private CtxAttributeValueType valueType;
	
	/** The minimum value of the context attribute this bond refers to. */
	private Serializable minValue;

	/** The maximum value of the context attribute this bond refers to. */
	private Serializable maxValue;

	/**
	 * Constructs a CtxAttributeBond with the specified parameters.
	 * 
	 * @param type
	 * 			the type of the newly created CtxBond, e.g.
	 *          {@link CtxAttributeTypes#LOCATION_SYMBOLIC}
	 * @param originType
	 *			the origin type of the newly created CtxBond
	 * @return
	 *        a new ACtxAttributeBond
	 */
	public ACtxAttributeBond(final String type, final CtxBondOriginType originType) {

		super(CtxModelType.ATTRIBUTE, type, originType);
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

    public static final Parcelable.Creator<ACtxAttributeBond> CREATOR = new Parcelable.Creator<ACtxAttributeBond>() {
        public ACtxAttributeBond createFromParcel(Parcel in) {
            return new ACtxAttributeBond(in);
        }

        public ACtxAttributeBond[] newArray(int size) {
            return new ACtxAttributeBond[size];
        }
    };
       
    private ACtxAttributeBond(Parcel in) {
    	super(in);
    }
    
	/**
	 * Returns the type of the context attribute value this bond refers to.
	 * 
	 * @return the type of the context attribute value this bond refers to.
	 */
	public CtxAttributeValueType getValueType() {
		
		return this.valueType;
	}
	
	/**
	 * Sets the type of the context attribute value this bond refers to.
	 * 
	 * @param valueType
	 *            the type of the context attribute value to set for this bond.
	 */
	public void setValueType(CtxAttributeValueType valueType) {
		
		this.valueType = valueType;
	}

	/**
	 * Returns the minimum value of the context attribute this bond refers to.
	 * 
	 * @return the minimum value of the context attribute this bond refers to.
	 */
	public Serializable getMinValue() {
		
		return this.minValue;
	}
	
	/**
	 * Returns the minimum value of the context attribute this bond refers to.
	 * 
	 * @param minValue
	 *            the minimum value of the context attribute to set for this bond.
	 */
	public void setMinValue(Serializable minValue) {
		
		this.minValue = minValue;
	}

	/**
	 * Returns the maximum value of the context attribute this bond refers to.
	 * 
	 * @return the maximum value of the context attribute this bond refers to.
	 */
	public Serializable getMaxValue() {
		
		return this.maxValue;
	}
	
	/**
	 * Sets the maximum value of the context attribute this bond refers to.
	 * 
	 * @param maxValue
	 *            the maximum value of the context attribute to set for this
	 *            bond.
	 */
	public void setMaxValue(Serializable maxValue) {
		
		this.maxValue = maxValue;
	}
}