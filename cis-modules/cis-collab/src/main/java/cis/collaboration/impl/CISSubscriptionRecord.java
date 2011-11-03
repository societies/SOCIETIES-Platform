
/**
 * This class represents a CIS subscription record.
 * 
 * @author Babak Farshchian
 * @version 0
 * 
 */

package cis.collaboration.impl;

public class CISSubscriptionRecord {
	private String cssId, cisId, subscriptionMode;

	public CISSubscriptionRecord(String cssId, String cisId, String subscriptionMode) {
		super();
		this.cisId = cisId;
		this.subscriptionMode = subscriptionMode;
		this.cssId = cssId;
	}

	public String getSubscriptionMode() {
		return subscriptionMode;
	}

	public void setSubscriptionMode(String subscriptionMode) {
		this.subscriptionMode = subscriptionMode;
	}

	public String getCssId() {
		return cssId;
	}

	public String getCisId() {
		return cisId;
	}

}
