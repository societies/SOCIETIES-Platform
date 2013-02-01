package org.societies.android.api.cis.management;

import java.util.ArrayList;
import java.util.List;
import org.societies.api.schema.cis.community.Criteria;
import org.societies.api.schema.cis.community.MembershipCrit;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Describe your class here...
 *
 * @author aleckey
 *
 */
public class AMembershipCrit extends MembershipCrit implements Parcelable {

	private static final long serialVersionUID = -4953520609328465990L;

	public AMembershipCrit() {
		super();
	}

	public List<ACriteria> getACriteria() {
		List<ACriteria> returnList = new ArrayList<ACriteria>();
		for (Criteria crit: super.getCriteria()) {
			returnList.add( ACriteria.convertCriteria(crit));
		}		
		return returnList;
	}

	public void setACriteria(List<ACriteria> listing) {
		super.getCriteria().clear();
		for (ACriteria acrit: listing) {
			super.getCriteria().add(ACriteria.convertACriteria(acrit));
		}
	}

	/* @see android.os.Parcelable#describeContents()*/
	public int describeContents() {
		return 0;
	}

	/* @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)*/
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeList(this.getACriteria());
	}

	private AMembershipCrit(Parcel in) {
		super();
		this.setACriteria(in.readArrayList(this.getClass().getClassLoader()));
		//this.setACriteria(in.createTypedArrayList(ACriteria.CREATOR));
	}

	public static final Parcelable.Creator<AMembershipCrit> CREATOR = new Parcelable.Creator<AMembershipCrit>() {
		public AMembershipCrit createFromParcel(Parcel in) {
			return new AMembershipCrit(in);
		}

		public AMembershipCrit[] newArray(int size) {
			return new AMembershipCrit[size];
		}
	};

	public static AMembershipCrit convertMembershipCrit(MembershipCrit memberCrit) {
		AMembershipCrit amemberCrit = new AMembershipCrit();
		List<ACriteria> returnList = new ArrayList<ACriteria>();

		if (null != memberCrit && null != memberCrit.getCriteria()) {
			for (Criteria crit: memberCrit.getCriteria()) {
				returnList.add(ACriteria.convertCriteria(crit));
			}
		}
		amemberCrit.setACriteria(returnList);

		return amemberCrit;
	}

	public static MembershipCrit convertAMembershipCrit(AMembershipCrit amemberCrit) {
		MembershipCrit memberCrit = new MembershipCrit();
		List<Criteria> returnList = new ArrayList<Criteria>();

		if (null != amemberCrit && null != amemberCrit.getCriteria()) {
			for (ACriteria acrit: amemberCrit.getACriteria()) {
				returnList.add(ACriteria.convertACriteria(acrit));
			}
		}
		memberCrit.setCriteria(returnList);

		return memberCrit;
	}


}
