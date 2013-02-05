package org.societies.android.api.context.model;

import org.societies.api.context.model.CtxBondOriginType;
import org.societies.api.context.model.CtxModelType;

import android.os.Parcel;
import android.os.Parcelable;

public class ACtxAssociationBond extends ACtxBond {

	public ACtxAssociationBond(CtxModelType modelType, String type,
			CtxBondOriginType originType) {
		
		super(modelType, type, originType);
		
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

    public static final Parcelable.Creator<ACtxAssociationBond> CREATOR = new Parcelable.Creator<ACtxAssociationBond>() {
        public ACtxAssociationBond createFromParcel(Parcel in) {
            return new ACtxAssociationBond(in);
        }

        public ACtxAssociationBond[] newArray(int size) {
            return new ACtxAssociationBond[size];
        }
    };
       
    private ACtxAssociationBond(Parcel in) {
    	super(in);
    }

}
