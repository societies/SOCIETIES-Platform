/**
 * 
 */
package org.societies.webapp.models.privacy;

import javax.validation.constraints.NotNull;

/**
 * @author Olivier Maridat (Trialog)
 *
 */
public class PrivacyPolicyCriteriaForm {
	@NotNull(message="Please, select the CIS location, and specify the CIS owner id if this is a remote CIS.")
	private String cisLocation;
	private String ownerId;
	@NotNull(message="The CIS id can't be empty.")
	private String cisId;


	public String getCisLocation() {
		return cisLocation;
	}
	public void setCisLocation(String cisLocation) {
		this.cisLocation = cisLocation;
	}
	public String getOwnerId() {
		return ownerId;
	}
	public void setOwnerId(String ownerId) {
		this.ownerId = ownerId;
	}
	public String getCisId() {
		return cisId;
	}
	public void setCisId(String cisId) {
		this.cisId = cisId;
	}


	@Override
	public String toString() {
		return "PrivacyPolicyCriteriaForm ["
				+ (cisLocation != null ? "cisLocation=" + cisLocation + ", "
						: "")
						+ (ownerId != null ? "ownerId=" + ownerId + ", " : "")
						+ (cisId != null ? "cisId=" + cisId : "") + "]";
	}
}
